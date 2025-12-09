package com.sep3.warehouse.controller;

import com.sep3.warehouse.dto.product.CreateProductRequest;
import com.sep3.warehouse.dto.product.ProductDTO;
import com.sep3.warehouse.dto.product.UpdateProductRequest;
import com.sep3.warehouse.service.ProductService;
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
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * REST Controller for product/inventory management.
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Products", description = "Product/Inventory management endpoints")
public class ProductController {
    
    private final ProductService productService;
    
    @GetMapping
    @Operation(summary = "Get all products", description = "Retrieve all active products with pagination")
    public ResponseEntity<Page<ProductDTO>> getAllProducts(
            @PageableDefault(size = 20) Pageable pageable) {
        log.debug("GET /api/products");
        return ResponseEntity.ok(productService.getAllProducts(pageable));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID", description = "Retrieve a specific product by its ID")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        log.debug("GET /api/products/{}", id);
        return ResponseEntity.ok(productService.getProductById(id));
    }
    
    @GetMapping("/sku/{sku}")
    @Operation(summary = "Get product by SKU", description = "Retrieve a product by its SKU")
    public ResponseEntity<ProductDTO> getProductBySku(@PathVariable String sku) {
        log.debug("GET /api/products/sku/{}", sku);
        return ResponseEntity.ok(productService.getProductBySku(sku));
    }
    
    @GetMapping("/search")
    @Operation(summary = "Search products", description = "Search products by name, SKU, or description")
    public ResponseEntity<Page<ProductDTO>> searchProducts(
            @RequestParam String query,
            @PageableDefault(size = 20) Pageable pageable) {
        log.debug("GET /api/products/search?query={}", query);
        return ResponseEntity.ok(productService.searchProducts(query, pageable));
    }
    
    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Get products by category", description = "Retrieve products in a specific category")
    public ResponseEntity<Page<ProductDTO>> getProductsByCategory(
            @PathVariable Long categoryId,
            @PageableDefault(size = 20) Pageable pageable) {
        log.debug("GET /api/products/category/{}", categoryId);
        return ResponseEntity.ok(productService.getProductsByCategory(categoryId, pageable));
    }
    
    @GetMapping("/low-stock")
    @Operation(summary = "Get low stock products", description = "Retrieve products with stock below minimum level")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'WAREHOUSE_OPERATOR')")
    public ResponseEntity<List<ProductDTO>> getLowStockProducts() {
        log.debug("GET /api/products/low-stock");
        return ResponseEntity.ok(productService.getLowStockProducts());
    }
    
    @PostMapping
    @Operation(summary = "Create product", description = "Create a new product")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'WAREHOUSE_OPERATOR')")
    public ResponseEntity<ProductDTO> createProduct(@Valid @RequestBody CreateProductRequest request) {
        log.info("POST /api/products - Creating product: {}", request.getSku());
        ProductDTO created = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update product", description = "Update an existing product")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'WAREHOUSE_OPERATOR')")
    public ResponseEntity<ProductDTO> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductRequest request) {
        log.info("PUT /api/products/{}", id);
        return ResponseEntity.ok(productService.updateProduct(id, request));
    }
    
    @PatchMapping("/{id}/stock")
    @Operation(summary = "Update product stock", description = "Adjust product stock quantity")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'WAREHOUSE_OPERATOR')")
    public ResponseEntity<ProductDTO> updateStock(
            @PathVariable Long id,
            @RequestParam int quantityChange) {
        log.info("PATCH /api/products/{}/stock - change: {}", id, quantityChange);
        return ResponseEntity.ok(productService.updateStock(id, quantityChange));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete product", description = "Soft delete a product")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        log.info("DELETE /api/products/{}", id);
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
