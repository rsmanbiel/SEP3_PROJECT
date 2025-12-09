package com.sep3.warehouse.dto.shipment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for Shipment from C# microservice.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShipmentDTO {
    private Long id;
    private Long orderId;
    private String trackingNumber;
    private String status;
    private String recipientName;
    private String recipientAddress;
    private String recipientCity;
    private String recipientPostalCode;
    private String recipientCountry;
    private String recipientPhone;
    private Double weightKg;
    private String currentLocation;
    private String estimatedDelivery;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<ShipmentHistoryDTO> history;
}
