package com.sep3.warehouse.dto.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * DTO for creating a new order.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {
    
    @NotNull(message = "Customer ID is required")
    private Long customerId;
    
    @NotEmpty(message = "Order must have at least one item")
    @Valid
    private List<CreateOrderItemRequest> items;
    
    @Size(max = 255, message = "Shipping address must not exceed 255 characters")
    private String shippingAddress;
    
    @Size(max = 100, message = "Shipping city must not exceed 100 characters")
    private String shippingCity;
    
    @Size(max = 20, message = "Shipping postal code must not exceed 20 characters")
    private String shippingPostalCode;
    
    @Size(max = 100, message = "Shipping country must not exceed 100 characters")
    private String shippingCountry;
    
    @Size(max = 20, message = "Shipping phone must not exceed 20 characters")
    private String shippingPhone;
    
    private String notes;
}
