package com.sep3.client.viewmodel;

import com.sep3.client.model.Product;
import com.sep3.client.service.ProductService;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.function.Consumer;

/**
 * ViewModel for the product list view.
 * Manages product listing and filtering.
 */
public class ProductListViewModel {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductListViewModel.class);
    private final ProductService productService;
    
    // Observable list of products
    private final ObservableList<Product> products = FXCollections.observableArrayList();
    
    // Properties
    private final StringProperty searchQuery = new SimpleStringProperty("");
    private final BooleanProperty isLoading = new SimpleBooleanProperty(false);
    private final StringProperty errorMessage = new SimpleStringProperty("");
    private final ObjectProperty<Product> selectedProduct = new SimpleObjectProperty<>();
    
    // Pagination
    private final IntegerProperty currentPage = new SimpleIntegerProperty(0);
    private final IntegerProperty totalPages = new SimpleIntegerProperty(0);
    private final IntegerProperty totalElements = new SimpleIntegerProperty(0);
    private final int pageSize = 20;
    
    // Callback for product selection
    private Consumer<Product> onProductSelected;
    
    public ProductListViewModel() {
        this.productService = ProductService.getInstance();
        
        // Listen for search query changes
        searchQuery.addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.equals(oldVal)) {
                currentPage.set(0);
                if (newVal.isEmpty()) {
                    loadProducts();
                } else {
                    searchProducts();
                }
            }
        });
    }
    
    /**
     * Load products from server.
     */
    public void loadProducts() {
        isLoading.set(true);
        errorMessage.set("");
        
        logger.debug("Loading products - page: {}", currentPage.get());
        
        productService.getAllProducts(currentPage.get(), pageSize)
                .thenAccept(response -> Platform.runLater(() -> {
                    products.clear();
                    if (response.content != null) {
                        products.addAll(response.content);
                    }
                    totalPages.set(response.totalPages);
                    totalElements.set(response.totalElements);
                    isLoading.set(false);
                    
                    logger.info("Loaded {} products", products.size());
                }))
                .exceptionally(throwable -> {
                    Platform.runLater(() -> {
                        isLoading.set(false);
                        errorMessage.set("Failed to load products");
                        logger.error("Failed to load products", throwable);
                    });
                    return null;
                });
    }
    
    /**
     * Search products.
     */
    public void searchProducts() {
        String query = searchQuery.get();
        if (query == null || query.isEmpty()) {
            loadProducts();
            return;
        }
        
        isLoading.set(true);
        errorMessage.set("");
        
        logger.debug("Searching products: {}", query);
        
        productService.searchProducts(query, currentPage.get(), pageSize)
                .thenAccept(response -> Platform.runLater(() -> {
                    products.clear();
                    if (response.content != null) {
                        products.addAll(response.content);
                    }
                    totalPages.set(response.totalPages);
                    totalElements.set(response.totalElements);
                    isLoading.set(false);
                    
                    logger.info("Search returned {} products", products.size());
                }))
                .exceptionally(throwable -> {
                    Platform.runLater(() -> {
                        isLoading.set(false);
                        errorMessage.set("Search failed");
                        logger.error("Search failed", throwable);
                    });
                    return null;
                });
    }
    
    /**
     * Go to next page.
     */
    public void nextPage() {
        if (currentPage.get() < totalPages.get() - 1) {
            currentPage.set(currentPage.get() + 1);
            if (searchQuery.get().isEmpty()) {
                loadProducts();
            } else {
                searchProducts();
            }
        }
    }
    
    /**
     * Go to previous page.
     */
    public void previousPage() {
        if (currentPage.get() > 0) {
            currentPage.set(currentPage.get() - 1);
            if (searchQuery.get().isEmpty()) {
                loadProducts();
            } else {
                searchProducts();
            }
        }
    }
    
    /**
     * Delete a product.
     */
    public void deleteProduct(Product product) {
        if (product == null) return;
        
        isLoading.set(true);
        
        productService.deleteProduct(product.getId())
                .thenAccept(v -> Platform.runLater(() -> {
                    products.remove(product);
                    isLoading.set(false);
                    logger.info("Product deleted: {}", product.getSku());
                }))
                .exceptionally(throwable -> {
                    Platform.runLater(() -> {
                        isLoading.set(false);
                        errorMessage.set("Failed to delete product");
                        logger.error("Failed to delete product", throwable);
                    });
                    return null;
                });
    }
    
    /**
     * Select a product.
     */
    public void selectProduct(Product product) {
        selectedProduct.set(product);
        if (onProductSelected != null && product != null) {
            onProductSelected.accept(product);
        }
    }
    
    /**
     * Refresh the product list.
     */
    public void refresh() {
        currentPage.set(0);
        searchQuery.set("");
        loadProducts();
    }
    
    // Property getters
    public ObservableList<Product> getProducts() { return products; }
    public StringProperty searchQueryProperty() { return searchQuery; }
    public BooleanProperty isLoadingProperty() { return isLoading; }
    public StringProperty errorMessageProperty() { return errorMessage; }
    public ObjectProperty<Product> selectedProductProperty() { return selectedProduct; }
    public IntegerProperty currentPageProperty() { return currentPage; }
    public IntegerProperty totalPagesProperty() { return totalPages; }
    public IntegerProperty totalElementsProperty() { return totalElements; }
    
    // Callback setter
    public void setOnProductSelected(Consumer<Product> callback) {
        this.onProductSelected = callback;
    }
}
