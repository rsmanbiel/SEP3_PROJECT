package com.sep3.warehouse.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Product entity representing inventory items in the warehouse.
 */
@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 50)
    private String sku;
    
    @Column(nullable = false, length = 200)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    
    @Column(name = "cost_price", precision = 10, scale = 2)
    private BigDecimal costPrice;
    
    @Column(name = "quantity_in_stock", nullable = false)
    private Integer quantityInStock;
    
    @Column(name = "minimum_stock_level")
    private Integer minimumStockLevel;
    
    @Column(name = "maximum_stock_level")
    private Integer maximumStockLevel;
    
    @Column(name = "weight_kg", precision = 10, scale = 3)
    private BigDecimal weightKg;
    
    @Column(length = 50)
    private String dimensions;
    
    @Column(length = 50)
    private String location;
    
    @Column(length = 100)
    private String barcode;
    
    @Column(name = "is_active")
    private Boolean isActive;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (isActive == null) {
            isActive = true;
        }
        if (minimumStockLevel == null) {
            minimumStockLevel = 10;
        }
        if (maximumStockLevel == null) {
            maximumStockLevel = 1000;
        }
        if (quantityInStock == null) {
            quantityInStock = 0;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public boolean isLowStock() {
        return quantityInStock != null && minimumStockLevel != null 
               && quantityInStock <= minimumStockLevel;
    }
}
