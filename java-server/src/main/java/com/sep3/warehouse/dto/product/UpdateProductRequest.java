package com.sep3.warehouse.dto.product;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

/**
 * DTO for updating an existing product.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductRequest {
    
    @Size(max = 200, message = "Name must not exceed 200 characters")
    private String name;
    
    private String description;
    
    private Long categoryId;
    
    @DecimalMin(value = "0.0", message = "Price must be non-negative")
    private BigDecimal price;
    
    @DecimalMin(value = "0.0", message = "Cost price must be non-negative")
    private BigDecimal costPrice;
    
    @Min(value = 0, message = "Quantity must be non-negative")
    private Integer quantityInStock;
    
    @Min(value = 0, message = "Minimum stock level must be non-negative")
    private Integer minimumStockLevel;
    
    @Min(value = 0, message = "Maximum stock level must be non-negative")
    private Integer maximumStockLevel;
    
    @DecimalMin(value = "0.0", message = "Weight must be non-negative")
    private BigDecimal weightKg;
    
    @Size(max = 50, message = "Dimensions must not exceed 50 characters")
    private String dimensions;
    
    @Size(max = 50, message = "Location must not exceed 50 characters")
    private String location;
    
    @Size(max = 100, message = "Barcode must not exceed 100 characters")
    private String barcode;
    
    private Boolean isActive;
}
