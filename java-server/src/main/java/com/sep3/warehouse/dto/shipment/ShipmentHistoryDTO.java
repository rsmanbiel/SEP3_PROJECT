package com.sep3.warehouse.dto.shipment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for shipment history entries.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShipmentHistoryDTO {
    private Long id;
    private String status;
    private String location;
    private String timestamp;
    private String notes;
}
