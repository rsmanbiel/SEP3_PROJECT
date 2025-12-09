package com.sep3.warehouse.controller;

import com.sep3.warehouse.dto.order.CreateOrderRequest;
import com.sep3.warehouse.dto.order.OrderDTO;
import com.sep3.warehouse.dto.order.UpdateOrderStatusRequest;
import com.sep3.warehouse.entity.OrderStatus;
import com.sep3.warehouse.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for order management.
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Orders", description = "Order management endpoints")
public class OrderController {
    
    private final OrderService orderService;
    
    @GetMapping
    @Operation(summary = "Get all orders", description = "Retrieve all orders with pagination")
    public ResponseEntity<Page<OrderDTO>> getAllOrders(
            @PageableDefault(size = 20) Pageable pageable) {
        log.debug("GET /api/orders");
        return ResponseEntity.ok(orderService.getAllOrders(pageable));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get order by ID", description = "Retrieve a specific order by its ID")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long id) {
        log.debug("GET /api/orders/{}", id);
        return ResponseEntity.ok(orderService.getOrderById(id));
    }
    
    @GetMapping("/number/{orderNumber}")
    @Operation(summary = "Get order by number", description = "Retrieve an order by its order number")
    public ResponseEntity<OrderDTO> getOrderByNumber(@PathVariable String orderNumber) {
        log.debug("GET /api/orders/number/{}", orderNumber);
        return ResponseEntity.ok(orderService.getOrderByNumber(orderNumber));
    }
    
    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Get orders by customer", description = "Retrieve orders for a specific customer")
    public ResponseEntity<Page<OrderDTO>> getOrdersByCustomer(
            @PathVariable Long customerId,
            @PageableDefault(size = 20) Pageable pageable) {
        log.debug("GET /api/orders/customer/{}", customerId);
        return ResponseEntity.ok(orderService.getOrdersByCustomer(customerId, pageable));
    }
    
    @GetMapping("/status/{status}")
    @Operation(summary = "Get orders by status", description = "Retrieve orders with a specific status")
    public ResponseEntity<Page<OrderDTO>> getOrdersByStatus(
            @PathVariable OrderStatus status,
            @PageableDefault(size = 20) Pageable pageable) {
        log.debug("GET /api/orders/status/{}", status);
        return ResponseEntity.ok(orderService.getOrdersByStatus(status, pageable));
    }
    
    @PostMapping
    @Operation(summary = "Create order", description = "Create a new order")
    public ResponseEntity<OrderDTO> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        log.info("POST /api/orders - Creating order for customer: {}", request.getCustomerId());
        OrderDTO created = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    @PutMapping("/{id}/status")
    @Operation(summary = "Update order status", description = "Update the status of an order")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'WAREHOUSE_OPERATOR')")
    public ResponseEntity<OrderDTO> updateOrderStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateOrderStatusRequest request,
            Authentication authentication) {
        log.info("PUT /api/orders/{}/status - New status: {}", id, request.getStatus());
        // Get user ID from authentication context (simplified - in production use proper user resolution)
        Long userId = 1L; // Placeholder - should be resolved from authentication
        return ResponseEntity.ok(orderService.updateOrderStatus(id, request, userId));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Cancel order", description = "Cancel an order")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<OrderDTO> cancelOrder(
            @PathVariable Long id,
            @RequestParam(required = false) String reason) {
        log.info("DELETE /api/orders/{} - Cancelling order", id);
        return ResponseEntity.ok(orderService.cancelOrder(id, reason));
    }
}
