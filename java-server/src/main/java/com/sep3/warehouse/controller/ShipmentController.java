package com.sep3.warehouse.controller;

import com.sep3.warehouse.dto.shipment.ShipmentDTO;
import com.sep3.warehouse.service.ShipmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * REST Controller for shipment management.
 * This controller communicates with the C# Shipment microservice via gRPC.
 */
@RestController
@RequestMapping("/api/shipments")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Shipments", description = "Shipment management endpoints (via C# microservice)")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'WAREHOUSE_OPERATOR')")
public class ShipmentController {
    
    private final ShipmentService shipmentService;
    
    @GetMapping
    @Operation(summary = "Get all shipments", description = "Retrieve all shipments with optional filtering")
    public ResponseEntity<List<ShipmentDTO>> getAllShipments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String status) {
        log.debug("GET /api/shipments - page: {}, size: {}, status: {}", page, size, status);
        return ResponseEntity.ok(shipmentService.getAllShipments(page, size, status));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get shipment by ID", description = "Retrieve a specific shipment")
    public ResponseEntity<ShipmentDTO> getShipment(@PathVariable Long id) {
        log.debug("GET /api/shipments/{}", id);
        return ResponseEntity.ok(shipmentService.getShipment(id));
    }
    
    @GetMapping("/order/{orderId}")
    @Operation(summary = "Get shipment by order ID", description = "Retrieve shipment for a specific order")
    public ResponseEntity<ShipmentDTO> getShipmentByOrderId(@PathVariable Long orderId) {
        log.debug("GET /api/shipments/order/{}", orderId);
        return ResponseEntity.ok(shipmentService.getShipmentByOrderId(orderId));
    }
    
    @PostMapping("/order/{orderId}")
    @Operation(summary = "Create shipment", description = "Create a shipment for an order")
    public ResponseEntity<ShipmentDTO> createShipment(@PathVariable Long orderId) {
        log.info("POST /api/shipments/order/{} - Creating shipment", orderId);
        ShipmentDTO created = shipmentService.createShipment(orderId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    @PutMapping("/{id}/status")
    @Operation(summary = "Update shipment status", description = "Update the status of a shipment")
    public ResponseEntity<ShipmentDTO> updateShipmentStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String notes) {
        log.info("PUT /api/shipments/{}/status - New status: {}", id, status);
        return ResponseEntity.ok(shipmentService.updateShipmentStatus(id, status, location, notes));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Cancel shipment", description = "Cancel a shipment")
    public ResponseEntity<ShipmentDTO> cancelShipment(
            @PathVariable Long id,
            @RequestParam(required = false) String reason) {
        log.info("DELETE /api/shipments/{} - Cancelling shipment", id);
        return ResponseEntity.ok(shipmentService.cancelShipment(id, reason));
    }
}
