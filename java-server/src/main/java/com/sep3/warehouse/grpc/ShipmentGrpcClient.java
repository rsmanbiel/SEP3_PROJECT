package com.sep3.warehouse.grpc;

import com.sep3.warehouse.dto.shipment.CreateShipmentRequest;
import com.sep3.warehouse.dto.shipment.ShipmentDTO;
import com.sep3.warehouse.dto.shipment.ShipmentHistoryDTO;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * gRPC client for communicating with the C# Shipment microservice.
 * 
 * This client handles all shipment-related operations by calling
 * the gRPC server running in the C# microservice.
 */
@Component
@Slf4j
public class ShipmentGrpcClient {
    
    @Value("${grpc.client.shipment-service.host:localhost}")
    private String host;
    
    @Value("${grpc.client.shipment-service.port:5001}")
    private int port;
    
    private ManagedChannel channel;
    // Note: The actual stub will be generated from the proto file
    // private ShipmentServiceGrpc.ShipmentServiceBlockingStub blockingStub;
    
    @PostConstruct
    public void init() {
        log.info("Initializing gRPC client for Shipment service at {}:{}", host, port);
        channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext() // For development; use TLS in production
                .build();
        // blockingStub = ShipmentServiceGrpc.newBlockingStub(channel);
        log.info("gRPC client initialized successfully");
    }
    
    @PreDestroy
    public void shutdown() {
        log.info("Shutting down gRPC client");
        if (channel != null) {
            try {
                channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                log.error("Error shutting down gRPC channel", e);
                Thread.currentThread().interrupt();
            }
        }
    }
    
    /**
     * Create a new shipment via gRPC.
     */
    public ShipmentDTO createShipment(CreateShipmentRequest request) {
        log.info("Creating shipment for order: {}", request.getOrderId());
        
        try {
            // This is a placeholder - actual implementation will use generated stubs
            // CreateShipmentRequest grpcRequest = CreateShipmentRequest.newBuilder()
            //         .setOrderId(request.getOrderId())
            //         .setRecipientName(request.getRecipientName())
            //         .setRecipientAddress(request.getRecipientAddress())
            //         .setRecipientCity(request.getRecipientCity())
            //         .setRecipientPostalCode(request.getRecipientPostalCode())
            //         .setRecipientCountry(request.getRecipientCountry())
            //         .setRecipientPhone(request.getRecipientPhone())
            //         .setWeightKg(request.getWeightKg())
            //         .setNotes(request.getNotes())
            //         .build();
            
            // ShipmentResponse response = blockingStub.createShipment(grpcRequest);
            // return mapToDTO(response.getShipment());
            
            // Placeholder return for compilation
            return createPlaceholderShipment(request);
            
        } catch (StatusRuntimeException e) {
            log.error("gRPC call failed: {}", e.getStatus(), e);
            throw new RuntimeException("Failed to create shipment: " + e.getStatus().getDescription());
        }
    }
    
    /**
     * Get shipment by ID.
     */
    public ShipmentDTO getShipment(Long shipmentId) {
        log.debug("Getting shipment: {}", shipmentId);
        
        try {
            // GetShipmentRequest request = GetShipmentRequest.newBuilder()
            //         .setShipmentId(shipmentId)
            //         .build();
            
            // ShipmentResponse response = blockingStub.getShipment(request);
            // return mapToDTO(response.getShipment());
            
            // Placeholder return
            return ShipmentDTO.builder()
                    .id(shipmentId)
                    .status("PENDING")
                    .build();
            
        } catch (StatusRuntimeException e) {
            log.error("gRPC call failed: {}", e.getStatus(), e);
            throw new RuntimeException("Failed to get shipment: " + e.getStatus().getDescription());
        }
    }
    
    /**
     * Get shipment by order ID.
     */
    public ShipmentDTO getShipmentByOrderId(Long orderId) {
        log.debug("Getting shipment for order: {}", orderId);
        
        try {
            // GetShipmentByOrderIdRequest request = GetShipmentByOrderIdRequest.newBuilder()
            //         .setOrderId(orderId)
            //         .build();
            
            // ShipmentResponse response = blockingStub.getShipmentByOrderId(request);
            // return mapToDTO(response.getShipment());
            
            // Placeholder return
            return ShipmentDTO.builder()
                    .orderId(orderId)
                    .status("PENDING")
                    .build();
            
        } catch (StatusRuntimeException e) {
            log.error("gRPC call failed: {}", e.getStatus(), e);
            throw new RuntimeException("Failed to get shipment: " + e.getStatus().getDescription());
        }
    }
    
    /**
     * Update shipment status.
     */
    public ShipmentDTO updateShipmentStatus(Long shipmentId, String status, String location, String notes) {
        log.info("Updating shipment {} status to: {}", shipmentId, status);
        
        try {
            // UpdateShipmentStatusRequest request = UpdateShipmentStatusRequest.newBuilder()
            //         .setShipmentId(shipmentId)
            //         .setStatus(ShipmentStatus.valueOf(status))
            //         .setLocation(location)
            //         .setNotes(notes)
            //         .build();
            
            // ShipmentResponse response = blockingStub.updateShipmentStatus(request);
            // return mapToDTO(response.getShipment());
            
            // Placeholder return
            return ShipmentDTO.builder()
                    .id(shipmentId)
                    .status(status)
                    .currentLocation(location)
                    .build();
            
        } catch (StatusRuntimeException e) {
            log.error("gRPC call failed: {}", e.getStatus(), e);
            throw new RuntimeException("Failed to update shipment: " + e.getStatus().getDescription());
        }
    }
    
    /**
     * Get all shipments.
     */
    public List<ShipmentDTO> getAllShipments(int page, int size, String statusFilter) {
        log.debug("Getting all shipments - page: {}, size: {}", page, size);
        
        try {
            // GetAllShipmentsRequest.Builder requestBuilder = GetAllShipmentsRequest.newBuilder()
            //         .setPage(page)
            //         .setSize(size);
            
            // if (statusFilter != null) {
            //     requestBuilder.setStatusFilter(ShipmentStatus.valueOf(statusFilter));
            // }
            
            // ShipmentListResponse response = blockingStub.getAllShipments(requestBuilder.build());
            // return response.getShipmentsList().stream()
            //         .map(this::mapToDTO)
            //         .toList();
            
            // Placeholder return
            return List.of();
            
        } catch (StatusRuntimeException e) {
            log.error("gRPC call failed: {}", e.getStatus(), e);
            throw new RuntimeException("Failed to get shipments: " + e.getStatus().getDescription());
        }
    }
    
    /**
     * Cancel a shipment.
     */
    public ShipmentDTO cancelShipment(Long shipmentId, String reason) {
        log.info("Cancelling shipment: {}", shipmentId);
        
        try {
            // CancelShipmentRequest request = CancelShipmentRequest.newBuilder()
            //         .setShipmentId(shipmentId)
            //         .setReason(reason)
            //         .build();
            
            // ShipmentResponse response = blockingStub.cancelShipment(request);
            // return mapToDTO(response.getShipment());
            
            // Placeholder return
            return ShipmentDTO.builder()
                    .id(shipmentId)
                    .status("CANCELLED")
                    .build();
            
        } catch (StatusRuntimeException e) {
            log.error("gRPC call failed: {}", e.getStatus(), e);
            throw new RuntimeException("Failed to cancel shipment: " + e.getStatus().getDescription());
        }
    }
    
    /**
     * Create placeholder shipment for development.
     */
    private ShipmentDTO createPlaceholderShipment(CreateShipmentRequest request) {
        return ShipmentDTO.builder()
                .id(System.currentTimeMillis())
                .orderId(request.getOrderId())
                .trackingNumber("TRK" + System.currentTimeMillis())
                .status("PENDING")
                .recipientName(request.getRecipientName())
                .recipientAddress(request.getRecipientAddress())
                .recipientCity(request.getRecipientCity())
                .recipientPostalCode(request.getRecipientPostalCode())
                .recipientCountry(request.getRecipientCountry())
                .recipientPhone(request.getRecipientPhone())
                .weightKg(request.getWeightKg())
                .notes(request.getNotes())
                .createdAt(LocalDateTime.now())
                .history(List.of(
                        ShipmentHistoryDTO.builder()
                                .status("PENDING")
                                .location("Warehouse")
                                .timestamp(LocalDateTime.now().toString())
                                .notes("Shipment created")
                                .build()
                ))
                .build();
    }
}
