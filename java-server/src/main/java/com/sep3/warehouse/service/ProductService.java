package com.sep3.warehouse.service;

import com.sep3.warehouse.dto.product.CreateProductRequest;
import com.sep3.warehouse.dto.product.ProductDTO;
import com.sep3.warehouse.dto.product.UpdateProductRequest;
import com.sep3.warehouse.entity.Category;
import com.sep3.warehouse.entity.Product;
import com.sep3.warehouse.exception.DuplicateResourceException;
import com.sep3.warehouse.exception.ResourceNotFoundException;
import com.sep3.warehouse.repository.CategoryRepository;
import com.sep3.warehouse.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * Service for managing products/inventory.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductService {
    
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    
    /**
     * Get all active products with pagination.
     */
    @Transactional(readOnly = true)
    public Page<ProductDTO> getAllProducts(Pageable pageable) {
        log.debug("Fetching all active products");
        return productRepository.findAllActive(pageable)
                .map(this::mapToDTO);
    }
    
    /**
     * Get product by ID.
     */
    @Transactional(readOnly = true)
    public ProductDTO getProductById(Long id) {
        log.debug("Fetching product with id: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        return mapToDTO(product);
    }
    
    /**
     * Get product by SKU.
     */
    @Transactional(readOnly = true)
    public ProductDTO getProductBySku(String sku) {
        log.debug("Fetching product with SKU: {}", sku);
        Product product = productRepository.findBySku(sku)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "sku", sku));
        return mapToDTO(product);
    }
    
    /**
     * Search products by name, SKU, or description.
     */
    @Transactional(readOnly = true)
    public Page<ProductDTO> searchProducts(String query, Pageable pageable) {
        log.debug("Searching products with query: {}", query);
        return productRepository.search(query, pageable)
                .map(this::mapToDTO);
    }
    
    /**
     * Get products by category.
     */
    @Transactional(readOnly = true)
    public Page<ProductDTO> getProductsByCategory(Long categoryId, Pageable pageable) {
        log.debug("Fetching products for category: {}", categoryId);
        return productRepository.findByCategoryId(categoryId, pageable)
                .map(this::mapToDTO);
    }
    
    /**
     * Get low stock products.
     */
    @Transactional(readOnly = true)
    public List<ProductDTO> getLowStockProducts() {
        log.debug("Fetching low stock products");
        return productRepository.findLowStockProducts().stream()
                .map(this::mapToDTO)
                .toList();
    }
    
    /**
     * Create a new product.
     */
    public ProductDTO createProduct(CreateProductRequest request) {
        log.info("Creating new product with SKU: {}", request.getSku());
        
        if (productRepository.existsBySku(request.getSku())) {
            throw new DuplicateResourceException("Product", "sku", request.getSku());
        }
        
        Product product = Product.builder()
                .sku(request.getSku())
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .costPrice(request.getCostPrice())
                .quantityInStock(request.getQuantityInStock())
                .minimumStockLevel(request.getMinimumStockLevel())
                .maximumStockLevel(request.getMaximumStockLevel())
                .weightKg(request.getWeightKg())
                .dimensions(request.getDimensions())
                .location(request.getLocation())
                .barcode(request.getBarcode())
                .isActive(true)
                .build();
        
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));
            product.setCategory(category);
        }
        
        Product savedProduct = productRepository.save(product);
        log.info("Product created with id: {}", savedProduct.getId());
        
        return mapToDTO(savedProduct);
    }
    
    /**
     * Update an existing product.
     */
    public ProductDTO updateProduct(Long id, UpdateProductRequest request) {
        log.info("Updating product with id: {}", id);
        
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        
        if (request.getName() != null) {
            product.setName(request.getName());
        }
        if (request.getDescription() != null) {
            product.setDescription(request.getDescription());
        }
        if (request.getPrice() != null) {
            product.setPrice(request.getPrice());
        }
        if (request.getCostPrice() != null) {
            product.setCostPrice(request.getCostPrice());
        }
        if (request.getQuantityInStock() != null) {
            product.setQuantityInStock(request.getQuantityInStock());
        }
        if (request.getMinimumStockLevel() != null) {
            product.setMinimumStockLevel(request.getMinimumStockLevel());
        }
        if (request.getMaximumStockLevel() != null) {
            product.setMaximumStockLevel(request.getMaximumStockLevel());
        }
        if (request.getWeightKg() != null) {
            product.setWeightKg(request.getWeightKg());
        }
        if (request.getDimensions() != null) {
            product.setDimensions(request.getDimensions());
        }
        if (request.getLocation() != null) {
            product.setLocation(request.getLocation());
        }
        if (request.getBarcode() != null) {
            product.setBarcode(request.getBarcode());
        }
        if (request.getIsActive() != null) {
            product.setIsActive(request.getIsActive());
        }
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));
            product.setCategory(category);
        }
        
        Product updatedProduct = productRepository.save(product);
        log.info("Product updated: {}", updatedProduct.getId());
        
        return mapToDTO(updatedProduct);
    }
    
    /**
     * Delete a product (soft delete).
     */
    public void deleteProduct(Long id) {
        log.info("Deleting product with id: {}", id);
        
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        
        product.setIsActive(false);
        productRepository.save(product);
        
        log.info("Product soft-deleted: {}", id);
    }
    
    /**
     * Update product stock quantity.
     */
    public ProductDTO updateStock(Long id, int quantityChange) {
        log.info("Updating stock for product {}: change={}", id, quantityChange);
        
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        
        int newQuantity = product.getQuantityInStock() + quantityChange;
        if (newQuantity < 0) {
            throw new IllegalArgumentException("Stock cannot be negative");
        }
        
        product.setQuantityInStock(newQuantity);
        Product updatedProduct = productRepository.save(product);
        
        log.info("Stock updated for product {}: new quantity={}", id, newQuantity);
        
        return mapToDTO(updatedProduct);
    }
    
    /**
     * Map Product entity to DTO.
     */
    private ProductDTO mapToDTO(Product product) {
        return ProductDTO.builder()
                .id(product.getId())
                .sku(product.getSku())
                .name(product.getName())
                .description(product.getDescription())
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
                .price(product.getPrice())
                .costPrice(product.getCostPrice())
                .quantityInStock(product.getQuantityInStock())
                .minimumStockLevel(product.getMinimumStockLevel())
                .maximumStockLevel(product.getMaximumStockLevel())
                .weightKg(product.getWeightKg())
                .dimensions(product.getDimensions())
                .location(product.getLocation())
                .barcode(product.getBarcode())
                .isActive(product.getIsActive())
                .isLowStock(product.isLowStock())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}
