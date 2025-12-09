package com.sep3.client.service;

import com.sep3.client.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Service for product/inventory operations.
 */
public class ProductService {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);
    private final HttpClientService httpClient;
    
    private static ProductService instance;
    
    private ProductService() {
        this.httpClient = HttpClientService.getInstance();
    }
    
    public static synchronized ProductService getInstance() {
        if (instance == null) {
            instance = new ProductService();
        }
        return instance;
    }
    
    /**
     * Get all products.
     */
    @SuppressWarnings("unchecked")
    public CompletableFuture<PageResponse<Product>> getAllProducts(int page, int size) {
        logger.debug("Fetching products - page: {}, size: {}", page, size);
        
        String endpoint = String.format("/products?page=%d&size=%d", page, size);
        
        return httpClient.get(endpoint, PageResponse.class)
                .thenApply(response -> (PageResponse<Product>) response);
    }
    
    /**
     * Get product by ID.
     */
    public CompletableFuture<Product> getProductById(Long id) {
        logger.debug("Fetching product: {}", id);
        return httpClient.get("/products/" + id, Product.class);
    }
    
    /**
     * Search products.
     */
    @SuppressWarnings("unchecked")
    public CompletableFuture<PageResponse<Product>> searchProducts(String query, int page, int size) {
        logger.debug("Searching products: {}", query);
        
        String endpoint = String.format("/products/search?query=%s&page=%d&size=%d", 
                query, page, size);
        
        return httpClient.get(endpoint, PageResponse.class)
                .thenApply(response -> (PageResponse<Product>) response);
    }
    
    /**
     * Get low stock products.
     */
    @SuppressWarnings("unchecked")
    public CompletableFuture<List<Product>> getLowStockProducts() {
        logger.debug("Fetching low stock products");
        
        return httpClient.get("/products/low-stock", List.class)
                .thenApply(response -> (List<Product>) response);
    }
    
    /**
     * Create new product.
     */
    public CompletableFuture<Product> createProduct(CreateProductRequest request) {
        logger.info("Creating product: {}", request.sku);
        return httpClient.post("/products", request, Product.class);
    }
    
    /**
     * Update product.
     */
    public CompletableFuture<Product> updateProduct(Long id, UpdateProductRequest request) {
        logger.info("Updating product: {}", id);
        return httpClient.put("/products/" + id, request, Product.class);
    }
    
    /**
     * Update product stock.
     */
    public CompletableFuture<Product> updateStock(Long id, int quantityChange) {
        logger.info("Updating stock for product {}: {}", id, quantityChange);
        return httpClient.get("/products/" + id + "/stock?quantityChange=" + quantityChange, Product.class);
    }
    
    /**
     * Delete product.
     */
    public CompletableFuture<Void> deleteProduct(Long id) {
        logger.info("Deleting product: {}", id);
        return httpClient.delete("/products/" + id);
    }
    
    // Inner classes for requests
    public record CreateProductRequest(
            String sku,
            String name,
            String description,
            Long categoryId,
            BigDecimal price,
            BigDecimal costPrice,
            Integer quantityInStock,
            Integer minimumStockLevel,
            Integer maximumStockLevel,
            BigDecimal weightKg,
            String dimensions,
            String location,
            String barcode
    ) {}
    
    public record UpdateProductRequest(
            String name,
            String description,
            Long categoryId,
            BigDecimal price,
            BigDecimal costPrice,
            Integer quantityInStock,
            Integer minimumStockLevel,
            Integer maximumStockLevel,
            BigDecimal weightKg,
            String dimensions,
            String location,
            String barcode,
            Boolean isActive
    ) {}
    
    // Page response wrapper
    public static class PageResponse<T> {
        public List<T> content;
        public int totalElements;
        public int totalPages;
        public int size;
        public int number;
    }
}
