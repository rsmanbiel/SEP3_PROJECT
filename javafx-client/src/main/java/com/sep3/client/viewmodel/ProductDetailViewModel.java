package com.sep3.client.viewmodel;

import com.sep3.client.model.Product;
import com.sep3.client.service.ProductService;
import javafx.application.Platform;
import javafx.beans.property.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.math.BigDecimal;
import java.util.function.Consumer;

/**
 * ViewModel for the product detail/edit view.
 * Handles product creation and editing.
 */
public class ProductDetailViewModel {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductDetailViewModel.class);
    private final ProductService productService;
    
    // Properties bound to form fields
    private final LongProperty id = new SimpleLongProperty();
    private final StringProperty sku = new SimpleStringProperty("");
    private final StringProperty name = new SimpleStringProperty("");
    private final StringProperty description = new SimpleStringProperty("");
    private final ObjectProperty<BigDecimal> price = new SimpleObjectProperty<>(BigDecimal.ZERO);
    private final ObjectProperty<BigDecimal> costPrice = new SimpleObjectProperty<>(BigDecimal.ZERO);
    private final IntegerProperty quantityInStock = new SimpleIntegerProperty(0);
    private final IntegerProperty minimumStockLevel = new SimpleIntegerProperty(10);
    private final IntegerProperty maximumStockLevel = new SimpleIntegerProperty(1000);
    private final StringProperty location = new SimpleStringProperty("");
    private final StringProperty barcode = new SimpleStringProperty("");
    private final BooleanProperty isActive = new SimpleBooleanProperty(true);
    
    // State properties
    private final BooleanProperty isNewProduct = new SimpleBooleanProperty(true);
    private final BooleanProperty isLoading = new SimpleBooleanProperty(false);
    private final StringProperty errorMessage = new SimpleStringProperty("");
    private final BooleanProperty saveSuccessful = new SimpleBooleanProperty(false);
    
    // Callbacks
    private Consumer<Product> onSaveSuccess;
    
    public ProductDetailViewModel() {
        this.productService = ProductService.getInstance();
    }
    
    /**
     * Load product data into the form.
     */
    public void loadProduct(Product product) {
        if (product == null) {
            clearForm();
            return;
        }
        
        isNewProduct.set(false);
        id.set(product.getId());
        sku.set(product.getSku());
        name.set(product.getName());
        description.set(product.getDescription() != null ? product.getDescription() : "");
        price.set(product.getPrice());
        costPrice.set(product.getCostPrice());
        quantityInStock.set(product.getQuantityInStock());
        minimumStockLevel.set(product.getMinimumStockLevel() != null ? product.getMinimumStockLevel() : 10);
        maximumStockLevel.set(product.getMaximumStockLevel() != null ? product.getMaximumStockLevel() : 1000);
        location.set(product.getLocation() != null ? product.getLocation() : "");
        barcode.set(product.getBarcode() != null ? product.getBarcode() : "");
        isActive.set(product.getIsActive());
        
        errorMessage.set("");
        saveSuccessful.set(false);
        
        logger.debug("Loaded product: {}", product.getSku());
    }
    
    /**
     * Clear form for new product.
     */
    public void clearForm() {
        isNewProduct.set(true);
        id.set(0);
        sku.set("");
        name.set("");
        description.set("");
        price.set(BigDecimal.ZERO);
        costPrice.set(BigDecimal.ZERO);
        quantityInStock.set(0);
        minimumStockLevel.set(10);
        maximumStockLevel.set(1000);
        location.set("");
        barcode.set("");
        isActive.set(true);
        errorMessage.set("");
        saveSuccessful.set(false);
    }
    
    /**
     * Save the product (create or update).
     */
    public void save() {
        // Validation
        if (sku.get().isEmpty()) {
            errorMessage.set("SKU is required");
            return;
        }
        if (name.get().isEmpty()) {
            errorMessage.set("Name is required");
            return;
        }
        if (price.get() == null || price.get().compareTo(BigDecimal.ZERO) < 0) {
            errorMessage.set("Price must be non-negative");
            return;
        }
        
        isLoading.set(true);
        errorMessage.set("");
        
        if (isNewProduct.get()) {
            createProduct();
        } else {
            updateProduct();
        }
    }
    
    private void createProduct() {
        var request = new ProductService.CreateProductRequest(
                sku.get(),
                name.get(),
                description.get(),
                null, // categoryId
                price.get(),
                costPrice.get(),
                quantityInStock.get(),
                minimumStockLevel.get(),
                maximumStockLevel.get(),
                null, // weightKg
                null, // dimensions
                location.get(),
                barcode.get()
        );
        
        logger.info("Creating product: {}", sku.get());
        
        productService.createProduct(request)
                .thenAccept(product -> Platform.runLater(() -> {
                    isLoading.set(false);
                    saveSuccessful.set(true);
                    if (onSaveSuccess != null) {
                        onSaveSuccess.accept(product);
                    }
                    logger.info("Product created: {}", product.getSku());
                }))
                .exceptionally(throwable -> {
                    Platform.runLater(() -> {
                        isLoading.set(false);
                        errorMessage.set("Failed to create product");
                        logger.error("Failed to create product", throwable);
                    });
                    return null;
                });
    }
    
    private void updateProduct() {
        var request = new ProductService.UpdateProductRequest(
                name.get(),
                description.get(),
                null, // categoryId
                price.get(),
                costPrice.get(),
                quantityInStock.get(),
                minimumStockLevel.get(),
                maximumStockLevel.get(),
                null, // weightKg
                null, // dimensions
                location.get(),
                barcode.get(),
                isActive.get()
        );
        
        logger.info("Updating product: {}", id.get());
        
        productService.updateProduct(id.get(), request)
                .thenAccept(product -> Platform.runLater(() -> {
                    isLoading.set(false);
                    saveSuccessful.set(true);
                    if (onSaveSuccess != null) {
                        onSaveSuccess.accept(product);
                    }
                    logger.info("Product updated: {}", product.getSku());
                }))
                .exceptionally(throwable -> {
                    Platform.runLater(() -> {
                        isLoading.set(false);
                        errorMessage.set("Failed to update product");
                        logger.error("Failed to update product", throwable);
                    });
                    return null;
                });
    }
    
    // Property getters
    public LongProperty idProperty() { return id; }
    public StringProperty skuProperty() { return sku; }
    public StringProperty nameProperty() { return name; }
    public StringProperty descriptionProperty() { return description; }
    public ObjectProperty<BigDecimal> priceProperty() { return price; }
    public ObjectProperty<BigDecimal> costPriceProperty() { return costPrice; }
    public IntegerProperty quantityInStockProperty() { return quantityInStock; }
    public IntegerProperty minimumStockLevelProperty() { return minimumStockLevel; }
    public IntegerProperty maximumStockLevelProperty() { return maximumStockLevel; }
    public StringProperty locationProperty() { return location; }
    public StringProperty barcodeProperty() { return barcode; }
    public BooleanProperty isActiveProperty() { return isActive; }
    public BooleanProperty isNewProductProperty() { return isNewProduct; }
    public BooleanProperty isLoadingProperty() { return isLoading; }
    public StringProperty errorMessageProperty() { return errorMessage; }
    public BooleanProperty saveSuccessfulProperty() { return saveSuccessful; }
    
    // Callback setter
    public void setOnSaveSuccess(Consumer<Product> callback) {
        this.onSaveSuccess = callback;
    }
}
