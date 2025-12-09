package com.sep3.warehouse.service;

import com.sep3.warehouse.dto.shipment.CreateShipmentRequest;
import com.sep3.warehouse.dto.shipment.ShipmentDTO;
import com.sep3.warehouse.entity.Order;
import com.sep3.warehouse.entity.OrderStatus;
import com.sep3.warehouse.exception.BadRequestException;
import com.sep3.warehouse.exception.ResourceNotFoundException;
import com.sep3.warehouse.grpc.ShipmentGrpcClient;
import com.sep3.warehouse.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * Service for shipment operations.
 * This service acts as a bridge between the REST API and the gRPC client
 * that communicates with the C# Shipment microservice.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ShipmentService {
    
    private final ShipmentGrpcClient shipmentGrpcClient;
    private final OrderRepository orderRepository;
    
    /**
     * Create a shipment for an order.
     */
    public ShipmentDTO createShipment(Long orderId) {
        log.info("Creating shipment for order: {}", orderId);
        
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
        
        if (order.getStatus() != OrderStatus.READY_FOR_SHIPMENT) {
            throw new BadRequestException("Order must be in READY_FOR_SHIPMENT status to create shipment");
        }
        
        // Calculate total weight from order items
        double totalWeight = order.getOrderItems().stream()
                .mapToDouble(item -> {
                    if (item.getProduct().getWeightKg() != null) {
                        return item.getProduct().getWeightKg().doubleValue() * item.getQuantity();
                    }
                    return 0.0;
                })
                .sum();
        
        CreateShipmentRequest request = CreateShipmentRequest.builder()
                .orderId(orderId)
                .recipientName(order.getCustomer().getFullName())
                .recipientAddress(order.getShippingAddress())
                .recipientCity(order.getShippingCity())
                .recipientPostalCode(order.getShippingPostalCode())
                .recipientCountry(order.getShippingCountry())
                .recipientPhone(order.getShippingPhone())
                .weightKg(totalWeight)
                .notes(order.getNotes())
                .build();
        
        ShipmentDTO shipment = shipmentGrpcClient.createShipment(request);
        
        // Update order status to SHIPPED
        order.setStatus(OrderStatus.SHIPPED);
        orderRepository.save(order);
        
        log.info("Shipment created with tracking number: {}", shipment.getTrackingNumber());
        
        return shipment;
    }
    
    /**
     * Get shipment by ID.
     */
    @Transactional(readOnly = true)
    public ShipmentDTO getShipment(Long shipmentId) {
        log.debug("Getting shipment: {}", shipmentId);
        return shipmentGrpcClient.getShipment(shipmentId);
    }
    
    /**
     * Get shipment by order ID.
     */
    @Transactional(readOnly = true)
    public ShipmentDTO getShipmentByOrderId(Long orderId) {
        log.debug("Getting shipment for order: {}", orderId);
        return shipmentGrpcClient.getShipmentByOrderId(orderId);
    }
    
    /**
     * Update shipment status.
     */
    public ShipmentDTO updateShipmentStatus(Long shipmentId, String status, String location, String notes) {
        log.info("Updating shipment {} status to: {}", shipmentId, status);
        
        ShipmentDTO updatedShipment = shipmentGrpcClient.updateShipmentStatus(shipmentId, status, location, notes);
        
        // If shipment is delivered, update order status
        if ("DELIVERED".equals(status)) {
            Long orderId = updatedShipment.getOrderId();
            if (orderId != null) {
                Order order = orderRepository.findById(orderId).orElse(null);
                if (order != null) {
                    order.setStatus(OrderStatus.DELIVERED);
                    orderRepository.save(order);
                    log.info("Order {} marked as DELIVERED", orderId);
                }
            }
        }
        
        return updatedShipment;
    }
    
    /**
     * Get all shipments.
     */
    @Transactional(readOnly = true)
    public List<ShipmentDTO> getAllShipments(int page, int size, String statusFilter) {
        log.debug("Getting all shipments");
        return shipmentGrpcClient.getAllShipments(page, size, statusFilter);
    }
    
    /**
     * Cancel a shipment.
     */
    public ShipmentDTO cancelShipment(Long shipmentId, String reason) {
        log.info("Cancelling shipment: {}", shipmentId);
        
        ShipmentDTO cancelledShipment = shipmentGrpcClient.cancelShipment(shipmentId, reason);
        
        // Update order status back to READY_FOR_SHIPMENT if needed
        Long orderId = cancelledShipment.getOrderId();
        if (orderId != null) {
            Order order = orderRepository.findById(orderId).orElse(null);
            if (order != null && order.getStatus() == OrderStatus.SHIPPED) {
                order.setStatus(OrderStatus.READY_FOR_SHIPMENT);
                orderRepository.save(order);
                log.info("Order {} status reverted to READY_FOR_SHIPMENT", orderId);
            }
        }
        
        return cancelledShipment;
    }
}
