package com.sep3.client.service;

import com.sep3.client.model.Order;
import com.sep3.client.model.OrderItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Service for order operations.
 */
public class OrderService {
    
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);
    private final HttpClientService httpClient;
    
    private static OrderService instance;
    
    private OrderService() {
        this.httpClient = HttpClientService.getInstance();
    }
    
    public static synchronized OrderService getInstance() {
        if (instance == null) {
            instance = new OrderService();
        }
        return instance;
    }
    
    /**
     * Get all orders.
     */
    @SuppressWarnings("unchecked")
    public CompletableFuture<ProductService.PageResponse<Order>> getAllOrders(int page, int size) {
        logger.debug("Fetching orders - page: {}, size: {}", page, size);
        
        String endpoint = String.format("/orders?page=%d&size=%d", page, size);
        return httpClient.get(endpoint, ProductService.PageResponse.class)
                .thenApply(response -> (ProductService.PageResponse<Order>) response);
    }
    
    /**
     * Get order by ID.
     */
    public CompletableFuture<Order> getOrderById(Long id) {
        logger.debug("Fetching order: {}", id);
        return httpClient.get("/orders/" + id, Order.class);
    }
    
    /**
     * Get orders by customer.
     */
    @SuppressWarnings("unchecked")
    public CompletableFuture<ProductService.PageResponse<Order>> getOrdersByCustomer(Long customerId, int page, int size) {
        logger.debug("Fetching orders for customer: {}", customerId);
        
        String endpoint = String.format("/orders/customer/%d?page=%d&size=%d", customerId, page, size);
        return httpClient.get(endpoint, ProductService.PageResponse.class)
                .thenApply(response -> (ProductService.PageResponse<Order>) response);
    }
    
    /**
     * Get orders by status.
     */
    @SuppressWarnings("unchecked")
    public CompletableFuture<ProductService.PageResponse<Order>> getOrdersByStatus(String status, int page, int size) {
        logger.debug("Fetching orders with status: {}", status);
        
        String endpoint = String.format("/orders/status/%s?page=%d&size=%d", status, page, size);
        return httpClient.get(endpoint, ProductService.PageResponse.class)
                .thenApply(response -> (ProductService.PageResponse<Order>) response);
    }
    
    /**
     * Create new order.
     */
    public CompletableFuture<Order> createOrder(CreateOrderRequest request) {
        logger.info("Creating order for customer: {}", request.customerId);
        return httpClient.post("/orders", request, Order.class);
    }
    
    /**
     * Update order status.
     */
    public CompletableFuture<Order> updateOrderStatus(Long orderId, String status, String notes) {
        logger.info("Updating order {} status to: {}", orderId, status);
        
        var request = new UpdateStatusRequest(status, notes);
        return httpClient.put("/orders/" + orderId + "/status", request, Order.class);
    }
    
    /**
     * Cancel order.
     */
    public CompletableFuture<Order> cancelOrder(Long orderId, String reason) {
        logger.info("Cancelling order: {}", orderId);
        return httpClient.delete("/orders/" + orderId + "?reason=" + reason)
                .thenApply(v -> null);
    }
    
    // Inner classes for requests
    public record CreateOrderRequest(
            Long customerId,
            List<OrderItemRequest> items,
            String shippingAddress,
            String shippingCity,
            String shippingPostalCode,
            String shippingCountry,
            String shippingPhone,
            String notes
    ) {}
    
    public record OrderItemRequest(
            Long productId,
            Integer quantity
    ) {}
    
    public record UpdateStatusRequest(
            String status,
            String notes
    ) {}
}
