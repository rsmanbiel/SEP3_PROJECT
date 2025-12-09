package com.sep3.client.view;

import com.sep3.client.model.Product;
import com.sep3.client.service.ProductService;
import com.sep3.client.viewmodel.ProductListViewModel;
import com.sep3.client.viewmodel.ViewModelFactory;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.math.BigDecimal;

/**
 * Controller for the product list view.
 */
public class ProductListViewController {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductListViewController.class);
    
    @FXML private TextField searchField;
    @FXML private Button searchButton;
    @FXML private Button refreshButton;
    @FXML private Button addButton;
    @FXML private TableView<Product> productTable;
    @FXML private TableColumn<Product, String> skuColumn;
    @FXML private TableColumn<Product, String> nameColumn;
    @FXML private TableColumn<Product, String> categoryColumn;
    @FXML private TableColumn<Product, BigDecimal> priceColumn;
    @FXML private TableColumn<Product, Integer> stockColumn;
    @FXML private TableColumn<Product, String> locationColumn;
    @FXML private Label pageInfoLabel;
    @FXML private Button prevButton;
    @FXML private Button nextButton;
    @FXML private ProgressIndicator loadingIndicator;
    @FXML private Label errorLabel;
    
    private ProductListViewModel viewModel;
    private ViewModelFactory viewModelFactory;
    private ViewHandler viewHandler;
    private ProductService productService;
    
    /**
     * Initialize the controller.
     */
    public void init(ProductListViewModel viewModel, ViewModelFactory viewModelFactory, ViewHandler viewHandler) {
        this.viewModel = viewModel;
        this.viewModelFactory = viewModelFactory;
        this.viewHandler = viewHandler;
        this.productService = ProductService.getInstance();
        
        // Setup table columns
        skuColumn.setCellValueFactory(new PropertyValueFactory<>("sku"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        stockColumn.setCellValueFactory(new PropertyValueFactory<>("quantityInStock"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        
        // Bind table to observable list
        productTable.setItems(viewModel.getProducts());
        
        // Bind properties
        searchField.textProperty().bindBidirectional(viewModel.searchQueryProperty());
        loadingIndicator.visibleProperty().bind(viewModel.isLoadingProperty());
        errorLabel.textProperty().bind(viewModel.errorMessageProperty());
        
        // Update page info
        viewModel.currentPageProperty().addListener((obs, oldVal, newVal) -> updatePageInfo());
        viewModel.totalPagesProperty().addListener((obs, oldVal, newVal) -> updatePageInfo());
        viewModel.totalElementsProperty().addListener((obs, oldVal, newVal) -> updatePageInfo());
        
        // Handle table selection
        productTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> viewModel.selectProduct(newVal));
        
        // Handle double-click for editing
        productTable.setRowFactory(tv -> {
            TableRow<Product> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    editProduct(row.getItem());
                } else if (event.getClickCount() == 1 && !row.isEmpty() && event.isControlDown()) {
                    // Ctrl+Click for delete
                    deleteProduct(row.getItem());
                }
            });
            return row;
        });
        
        // Add context menu for delete
        ContextMenu contextMenu = new ContextMenu();
        MenuItem editItem = new MenuItem("Edit");
        editItem.setOnAction(e -> {
            Product selected = productTable.getSelectionModel().getSelectedItem();
            if (selected != null) editProduct(selected);
        });
        MenuItem deleteItem = new MenuItem("Delete");
        deleteItem.setOnAction(e -> {
            Product selected = productTable.getSelectionModel().getSelectedItem();
            if (selected != null) deleteProduct(selected);
        });
        contextMenu.getItems().addAll(editItem, deleteItem);
        productTable.setContextMenu(contextMenu);
        
        // Style low stock items
        stockColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item.toString());
                    Product product = getTableView().getItems().get(getIndex());
                    if (product.getIsLowStock() != null && product.getIsLowStock()) {
                        setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                    } else {
                        setStyle("");
                    }
                }
            }
        });
        
        // Load products
        viewModel.loadProducts();
        
        logger.debug("Product list view controller initialized");
    }
    
    @FXML
    private void handleSearch() {
        viewModel.searchProducts();
    }
    
    @FXML
    private void handleRefresh() {
        viewModel.refresh();
    }
    
    @FXML
    private void handleAdd() {
        logger.debug("Adding new product");
        showProductDialog(null);
    }
    
    @FXML
    private void handlePrevPage() {
        viewModel.previousPage();
    }
    
    @FXML
    private void handleNextPage() {
        viewModel.nextPage();
    }
    
    private void editProduct(Product product) {
        logger.debug("Editing product: {}", product.getSku());
        showProductDialog(product);
    }
    
    private void showProductDialog(Product product) {
        Dialog<Product> dialog = new Dialog<>();
        dialog.setTitle(product == null ? "Add New Product" : "Edit Product");
        dialog.setHeaderText(product == null ? "Enter product details" : "Update product details");
        
        // Create form fields
        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));
        
        javafx.scene.control.TextField skuField = new javafx.scene.control.TextField();
        skuField.setPromptText("SKU");
        javafx.scene.control.TextField nameField = new javafx.scene.control.TextField();
        nameField.setPromptText("Product Name");
        javafx.scene.control.TextArea descriptionField = new javafx.scene.control.TextArea();
        descriptionField.setPromptText("Description");
        descriptionField.setPrefRowCount(3);
        javafx.scene.control.TextField priceField = new javafx.scene.control.TextField();
        priceField.setPromptText("Price");
        javafx.scene.control.TextField stockField = new javafx.scene.control.TextField();
        stockField.setPromptText("Stock Quantity");
        javafx.scene.control.TextField locationField = new javafx.scene.control.TextField();
        locationField.setPromptText("Location (e.g., A-01-01)");
        
        if (product != null) {
            skuField.setText(product.getSku() != null ? product.getSku() : "");
            nameField.setText(product.getName() != null ? product.getName() : "");
            descriptionField.setText(product.getDescription() != null ? product.getDescription() : "");
            priceField.setText(product.getPrice() != null ? product.getPrice().toString() : "");
            stockField.setText(product.getQuantityInStock() != null ? product.getQuantityInStock().toString() : "");
            locationField.setText(product.getLocation() != null ? product.getLocation() : "");
            skuField.setEditable(false);
        }
        
        grid.add(new javafx.scene.control.Label("SKU:"), 0, 0);
        grid.add(skuField, 1, 0);
        grid.add(new javafx.scene.control.Label("Name:"), 0, 1);
        grid.add(nameField, 1, 1);
        grid.add(new javafx.scene.control.Label("Description:"), 0, 2);
        grid.add(descriptionField, 1, 2);
        grid.add(new javafx.scene.control.Label("Price:"), 0, 3);
        grid.add(priceField, 1, 3);
        grid.add(new javafx.scene.control.Label("Stock:"), 0, 4);
        grid.add(stockField, 1, 4);
        grid.add(new javafx.scene.control.Label("Location:"), 0, 5);
        grid.add(locationField, 1, 5);
        
        dialog.getDialogPane().setContent(grid);
        
        javafx.scene.control.ButtonType saveButtonType = new javafx.scene.control.ButtonType("Save", javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, javafx.scene.control.ButtonType.CANCEL);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    // Validate required fields
                    if (product == null) {
                        if (skuField.getText().trim().isEmpty()) {
                            showError("SKU is required");
                            return null;
                        }
                        if (nameField.getText().trim().isEmpty()) {
                            showError("Product name is required");
                            return null;
                        }
                        if (priceField.getText().trim().isEmpty()) {
                            showError("Price is required");
                            return null;
                        }
                        if (stockField.getText().trim().isEmpty()) {
                            showError("Stock quantity is required");
                            return null;
                        }
                        
                        BigDecimal price;
                        int stock;
                        try {
                            price = new java.math.BigDecimal(priceField.getText().trim());
                            if (price.compareTo(java.math.BigDecimal.ZERO) < 0) {
                                showError("Price must be positive");
                                return null;
                            }
                            stock = Integer.parseInt(stockField.getText().trim());
                            if (stock < 0) {
                                showError("Stock quantity must be non-negative");
                                return null;
                            }
                        } catch (NumberFormatException e) {
                            showError("Invalid number format for price or stock");
                            return null;
                        }
                        
                        ProductService.CreateProductRequest createRequest = new ProductService.CreateProductRequest(
                                skuField.getText().trim(),
                                nameField.getText().trim(),
                                descriptionField.getText().trim().isEmpty() ? null : descriptionField.getText().trim(),
                                1L, // Default category
                                price,
                                price.multiply(new java.math.BigDecimal("0.5")), // Cost = 50% of price
                                stock,
                                10, // Default min stock
                                null, null, null,
                                locationField.getText().trim().isEmpty() ? null : locationField.getText().trim(),
                                null
                        );
                        
                        productService.createProduct(createRequest)
                                .thenAccept(p -> Platform.runLater(() -> {
                                    viewModel.refresh();
                                    logger.info("Product created: {}", p.getSku());
                                    showSuccess("Product created successfully");
                                }))
                                .exceptionally(throwable -> {
                                    Platform.runLater(() -> {
                                        String errorMsg = throwable.getMessage();
                                        if (errorMsg != null && errorMsg.contains("duplicate")) {
                                            showError("SKU already exists");
                                        } else {
                                            showError("Failed to create product: " + (errorMsg != null ? errorMsg : "Unknown error"));
                                        }
                                    });
                                    return null;
                                });
                    } else {
                        if (nameField.getText().trim().isEmpty()) {
                            showError("Product name is required");
                            return null;
                        }
                        if (priceField.getText().trim().isEmpty()) {
                            showError("Price is required");
                            return null;
                        }
                        if (stockField.getText().trim().isEmpty()) {
                            showError("Stock quantity is required");
                            return null;
                        }
                        
                        BigDecimal price;
                        int stock;
                        try {
                            price = new java.math.BigDecimal(priceField.getText().trim());
                            if (price.compareTo(java.math.BigDecimal.ZERO) < 0) {
                                showError("Price must be positive");
                                return null;
                            }
                            stock = Integer.parseInt(stockField.getText().trim());
                            if (stock < 0) {
                                showError("Stock quantity must be non-negative");
                                return null;
                            }
                        } catch (NumberFormatException e) {
                            showError("Invalid number format for price or stock");
                            return null;
                        }
                        
                        ProductService.UpdateProductRequest updateRequest = new ProductService.UpdateProductRequest(
                                nameField.getText().trim(),
                                descriptionField.getText().trim().isEmpty() ? null : descriptionField.getText().trim(),
                                product.getCategoryId(),
                                price,
                                product.getCostPrice(),
                                stock,
                                product.getMinimumStockLevel(),
                                null, null, null,
                                locationField.getText().trim().isEmpty() ? null : locationField.getText().trim(),
                                null, true
                        );
                        
                        productService.updateProduct(product.getId(), updateRequest)
                                .thenAccept(p -> Platform.runLater(() -> {
                                    viewModel.refresh();
                                    logger.info("Product updated: {}", p.getSku());
                                    showSuccess("Product updated successfully");
                                }))
                                .exceptionally(throwable -> {
                                    Platform.runLater(() -> {
                                        showError("Failed to update product: " + (throwable.getMessage() != null ? throwable.getMessage() : "Unknown error"));
                                    });
                                    return null;
                                });
                    }
                } catch (Exception e) {
                    showError("Invalid input: " + e.getMessage());
                }
            }
            return null;
        });
        
        dialog.showAndWait();
    }
    
    private void deleteProduct(Product product) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Delete Product");
        confirmAlert.setHeaderText("Are you sure you want to delete this product?");
        confirmAlert.setContentText("Product: " + product.getSku() + " - " + product.getName());
        
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                productService.deleteProduct(product.getId())
                        .thenAccept(v -> Platform.runLater(() -> {
                            viewModel.refresh();
                            logger.info("Product deleted: {}", product.getSku());
                            showSuccess("Product deleted successfully");
                        }))
                        .exceptionally(throwable -> {
                            Platform.runLater(() -> {
                                showError("Failed to delete product: " + (throwable.getMessage() != null ? throwable.getMessage() : "Unknown error"));
                            });
                            return null;
                        });
            }
        });
    }
    
    private void updatePageInfo() {
        int current = viewModel.currentPageProperty().get() + 1;
        int total = viewModel.totalPagesProperty().get();
        int elements = viewModel.totalElementsProperty().get();
        pageInfoLabel.setText(String.format("Page %d of %d (%d items)", current, total, elements));
        
        prevButton.setDisable(viewModel.currentPageProperty().get() == 0);
        nextButton.setDisable(viewModel.currentPageProperty().get() >= viewModel.totalPagesProperty().get() - 1);
    }
    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
