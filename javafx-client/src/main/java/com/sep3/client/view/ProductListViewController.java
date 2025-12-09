package com.sep3.client.view;

import com.sep3.client.model.Product;
import com.sep3.client.viewmodel.ProductListViewModel;
import com.sep3.client.viewmodel.ViewModelFactory;
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
    
    /**
     * Initialize the controller.
     */
    public void init(ProductListViewModel viewModel, ViewModelFactory viewModelFactory, ViewHandler viewHandler) {
        this.viewModel = viewModel;
        this.viewModelFactory = viewModelFactory;
        this.viewHandler = viewHandler;
        
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
                }
            });
            return row;
        });
        
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
        // Open product detail view for new product
        // This would open a dialog or navigate to detail view
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
        // Open product detail view
    }
    
    private void updatePageInfo() {
        int current = viewModel.currentPageProperty().get() + 1;
        int total = viewModel.totalPagesProperty().get();
        int elements = viewModel.totalElementsProperty().get();
        pageInfoLabel.setText(String.format("Page %d of %d (%d items)", current, total, elements));
        
        prevButton.setDisable(viewModel.currentPageProperty().get() == 0);
        nextButton.setDisable(viewModel.currentPageProperty().get() >= viewModel.totalPagesProperty().get() - 1);
    }
}
