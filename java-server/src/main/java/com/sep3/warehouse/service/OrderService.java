package com.sep3.warehouse.service;

import com.sep3.warehouse.dto.order.*;
import com.sep3.warehouse.entity.*;
import com.sep3.warehouse.exception.BadRequestException;
import com.sep3.warehouse.exception.InsufficientStockException;
import com.sep3.warehouse.exception.ResourceNotFoundException;
import com.sep3.warehouse.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Service for managing orders.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    
    /**
     * Get all orders with pagination.
     */
    @Transactional(readOnly = true)
    public Page<OrderDTO> getAllOrders(Pageable pageable) {
        log.debug("Fetching all orders");
        return orderRepository.findAll(pageable)
                .map(this::mapToDTO);
    }
    
    /**
     * Get order by ID.
     */
    @Transactional(readOnly = true)
    public OrderDTO getOrderById(Long id) {
        log.debug("Fetching order with id: {}", id);
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));
        return mapToDTO(order);
    }
    
    /**
     * Get order by order number.
     */
    @Transactional(readOnly = true)
    public OrderDTO getOrderByNumber(String orderNumber) {
        log.debug("Fetching order with number: {}", orderNumber);
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "orderNumber", orderNumber));
        return mapToDTO(order);
    }
    
    /**
     * Get orders by customer ID.
     */
    @Transactional(readOnly = true)
    public Page<OrderDTO> getOrdersByCustomer(Long customerId, Pageable pageable) {
        log.debug("Fetching orders for customer: {}", customerId);
        return orderRepository.findByCustomerId(customerId, pageable)
                .map(this::mapToDTO);
    }
    
    /**
     * Get orders by status.
     */
    @Transactional(readOnly = true)
    public Page<OrderDTO> getOrdersByStatus(OrderStatus status, Pageable pageable) {
        log.debug("Fetching orders with status: {}", status);
        return orderRepository.findByStatus(status, pageable)
                .map(this::mapToDTO);
    }
    
    /**
     * Create a new order.
     */
    public OrderDTO createOrder(CreateOrderRequest request) {
        log.info("Creating new order for customer: {}", request.getCustomerId());
        
        User customer = userRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", request.getCustomerId()));
        
        // Generate order number
        String orderNumber = generateOrderNumber();
        
        Order order = Order.builder()
                .orderNumber(orderNumber)
                .customer(customer)
                .status(OrderStatus.PENDING)
                .shippingAddress(request.getShippingAddress() != null ? request.getShippingAddress() : customer.getAddress())
                .shippingCity(request.getShippingCity() != null ? request.getShippingCity() : customer.getCity())
                .shippingPostalCode(request.getShippingPostalCode() != null ? request.getShippingPostalCode() : customer.getPostalCode())
                .shippingCountry(request.getShippingCountry() != null ? request.getShippingCountry() : customer.getCountry())
                .shippingPhone(request.getShippingPhone() != null ? request.getShippingPhone() : customer.getPhone())
                .notes(request.getNotes())
                .totalAmount(BigDecimal.ZERO)
                .build();
        
        // Validate and add order items
        BigDecimal total = BigDecimal.ZERO;
        for (CreateOrderItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "id", itemRequest.getProductId()));
            
            // Check stock availability
            if (product.getQuantityInStock() < itemRequest.getQuantity()) {
                throw new InsufficientStockException(
                        product.getName(),
                        itemRequest.getQuantity(),
                        product.getQuantityInStock()
                );
            }
            
            OrderItem orderItem = OrderItem.builder()
                    .product(product)
                    .quantity(itemRequest.getQuantity())
                    .unitPrice(product.getPrice())
                    .totalPrice(product.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity())))
                    .build();
            
            order.addOrderItem(orderItem);
            total = total.add(orderItem.getTotalPrice());
            
            // Reserve stock
            product.setQuantityInStock(product.getQuantityInStock() - itemRequest.getQuantity());
            productRepository.save(product);
        }
        
        order.setTotalAmount(total);
        Order savedOrder = orderRepository.save(order);
        
        log.info("Order created with number: {}", savedOrder.getOrderNumber());
        
        return mapToDTO(savedOrder);
    }
    
    /**
     * Update order status.
     */
    public OrderDTO updateOrderStatus(Long orderId, UpdateOrderStatusRequest request, Long userId) {
        log.info("Updating status for order {} to {}", orderId, request.getStatus());
        
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
        
        validateStatusTransition(order.getStatus(), request.getStatus());
        
        order.setStatus(request.getStatus());
        
        if (request.getNotes() != null) {
            order.setNotes(request.getNotes());
        }
        
        // Set timestamps based on status
        switch (request.getStatus()) {
            case PROCESSING -> {
                User operator = userRepository.findById(userId)
                        .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
                order.setProcessedBy(operator);
            }
            case SHIPPED -> order.setShippedAt(LocalDateTime.now());
            case DELIVERED -> order.setDeliveredAt(LocalDateTime.now());
            case CANCELLED -> restoreStock(order);
        }
        
        Order updatedOrder = orderRepository.save(order);
        log.info("Order {} status updated to {}", orderId, request.getStatus());
        
        return mapToDTO(updatedOrder);
    }
    
    /**
     * Cancel an order.
     */
    public OrderDTO cancelOrder(Long orderId, String reason) {
        log.info("Cancelling order: {}", orderId);
        
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
        
        if (order.getStatus() == OrderStatus.SHIPPED || order.getStatus() == OrderStatus.DELIVERED) {
            throw new BadRequestException("Cannot cancel order that has already been shipped or delivered");
        }
        
        order.setStatus(OrderStatus.CANCELLED);
        order.setNotes(reason);
        
        restoreStock(order);
        
        Order cancelledOrder = orderRepository.save(order);
        log.info("Order {} cancelled", orderId);
        
        return mapToDTO(cancelledOrder);
    }
    
    /**
     * Generate unique order number.
     */
    private String generateOrderNumber() {
        String datePrefix = "ORD-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-";
        Integer maxSequence = orderRepository.getMaxOrderSequence(datePrefix);
        int nextSequence = (maxSequence != null ? maxSequence : 0) + 1;
        return datePrefix + String.format("%06d", nextSequence);
    }
    
    /**
     * Validate order status transition.
     */
    private void validateStatusTransition(OrderStatus currentStatus, OrderStatus newStatus) {
        // Define valid transitions
        boolean valid = switch (currentStatus) {
            case PENDING -> newStatus == OrderStatus.CONFIRMED || newStatus == OrderStatus.CANCELLED;
            case CONFIRMED -> newStatus == OrderStatus.PROCESSING || newStatus == OrderStatus.CANCELLED;
            case PROCESSING -> newStatus == OrderStatus.READY_FOR_SHIPMENT || newStatus == OrderStatus.CANCELLED;
            case READY_FOR_SHIPMENT -> newStatus == OrderStatus.SHIPPED || newStatus == OrderStatus.CANCELLED;
            case SHIPPED -> newStatus == OrderStatus.DELIVERED || newStatus == OrderStatus.RETURNED;
            case DELIVERED -> newStatus == OrderStatus.RETURNED;
            case CANCELLED, RETURNED -> false;
        };
        
        if (!valid) {
            throw new BadRequestException(
                    String.format("Invalid status transition from %s to %s", currentStatus, newStatus)
            );
        }
    }
    
    /**
     * Restore stock when order is cancelled.
     */
    private void restoreStock(Order order) {
        for (OrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();
            product.setQuantityInStock(product.getQuantityInStock() + item.getQuantity());
            productRepository.save(product);
        }
    }
    
    /**
     * Map Order entity to DTO.
     */
    private OrderDTO mapToDTO(Order order) {
        List<OrderItemDTO> items = order.getOrderItems().stream()
                .map(this::mapItemToDTO)
                .toList();
        
        return OrderDTO.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .customerId(order.getCustomer().getId())
                .customerName(order.getCustomer().getFullName())
                .customerEmail(order.getCustomer().getEmail())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .shippingAddress(order.getShippingAddress())
                .shippingCity(order.getShippingCity())
                .shippingPostalCode(order.getShippingPostalCode())
                .shippingCountry(order.getShippingCountry())
                .shippingPhone(order.getShippingPhone())
                .notes(order.getNotes())
                .processedById(order.getProcessedBy() != null ? order.getProcessedBy().getId() : null)
                .processedByName(order.getProcessedBy() != null ? order.getProcessedBy().getFullName() : null)
                .approvedById(order.getApprovedBy() != null ? order.getApprovedBy().getId() : null)
                .approvedByName(order.getApprovedBy() != null ? order.getApprovedBy().getFullName() : null)
                .items(items)
                .itemCount(items.size())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .shippedAt(order.getShippedAt())
                .deliveredAt(order.getDeliveredAt())
                .build();
    }
    
    /**
     * Map OrderItem entity to DTO.
     */
    private OrderItemDTO mapItemToDTO(OrderItem item) {
        return OrderItemDTO.builder()
                .id(item.getId())
                .productId(item.getProduct().getId())
                .productSku(item.getProduct().getSku())
                .productName(item.getProduct().getName())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .totalPrice(item.getTotalPrice())
                .build();
    }
}
