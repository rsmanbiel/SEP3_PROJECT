package com.sep3.warehouse.dto.shipment;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating a new shipment.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateShipmentRequest {
    
    @NotNull(message = "Order ID is required")
    private Long orderId;
    
    private String recipientName;
    private String recipientAddress;
    private String recipientCity;
    private String recipientPostalCode;
    private String recipientCountry;
    private String recipientPhone;
    private Double weightKg;
    private String notes;
}
