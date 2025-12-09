package com.sep3.client.view;

import com.sep3.client.model.Order;
import com.sep3.client.viewmodel.OrderListViewModel;
import com.sep3.client.viewmodel.ViewModelFactory;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.math.BigDecimal;

/**
 * Controller for the order list view.
 */
public class OrderListViewController {
    
    private static final Logger logger = LoggerFactory.getLogger(OrderListViewController.class);
    
    @FXML private ComboBox<String> statusFilter;
    @FXML private Button refreshButton;
    @FXML private TableView<Order> orderTable;
    @FXML private TableColumn<Order, String> orderNumberColumn;
    @FXML private TableColumn<Order, String> customerColumn;
    @FXML private TableColumn<Order, String> statusColumn;
    @FXML private TableColumn<Order, BigDecimal> totalColumn;
    @FXML private TableColumn<Order, Integer> itemsColumn;
    @FXML private TableColumn<Order, String> createdColumn;
    @FXML private Label pageInfoLabel;
    @FXML private Button prevButton;
    @FXML private Button nextButton;
    @FXML private ProgressIndicator loadingIndicator;
    @FXML private Label errorLabel;
    
    private OrderListViewModel viewModel;
    private ViewModelFactory viewModelFactory;
    private ViewHandler viewHandler;
    
    /**
     * Initialize the controller.
     */
    public void init(OrderListViewModel viewModel, ViewModelFactory viewModelFactory, ViewHandler viewHandler) {
        this.viewModel = viewModel;
        this.viewModelFactory = viewModelFactory;
        this.viewHandler = viewHandler;
        
        // Setup status filter
        statusFilter.getItems().addAll(
                "",
                "PENDING",
                "CONFIRMED",
                "PROCESSING",
                "READY_FOR_SHIPMENT",
                "SHIPPED",
                "DELIVERED",
                "CANCELLED"
        );
        statusFilter.setValue("");
        statusFilter.valueProperty().bindBidirectional(viewModel.statusFilterProperty());
        
        // Setup table columns
        orderNumberColumn.setCellValueFactory(new PropertyValueFactory<>("orderNumber"));
        customerColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        totalColumn.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        itemsColumn.setCellValueFactory(new PropertyValueFactory<>("itemCount"));
        createdColumn.setCellValueFactory(cellData -> {
            var created = cellData.getValue().getCreatedAt();
            return new javafx.beans.property.SimpleStringProperty(
                    created != null ? created.toString() : ""
            );
        });
        
        // Style status column
        statusColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    String color = switch (item) {
                        case "PENDING" -> "#f39c12";
                        case "CONFIRMED", "PROCESSING" -> "#3498db";
                        case "READY_FOR_SHIPMENT", "SHIPPED" -> "#9b59b6";
                        case "DELIVERED" -> "#27ae60";
                        case "CANCELLED" -> "#e74c3c";
                        default -> "#333";
                    };
                    setStyle("-fx-text-fill: " + color + "; -fx-font-weight: bold;");
                }
            }
        });
        
        // Bind table to observable list
        orderTable.setItems(viewModel.getOrders());
        
        // Bind properties
        loadingIndicator.visibleProperty().bind(viewModel.isLoadingProperty());
        errorLabel.textProperty().bind(viewModel.errorMessageProperty());
        
        // Update page info
        viewModel.currentPageProperty().addListener((obs, oldVal, newVal) -> updatePageInfo());
        viewModel.totalPagesProperty().addListener((obs, oldVal, newVal) -> updatePageInfo());
        viewModel.totalElementsProperty().addListener((obs, oldVal, newVal) -> updatePageInfo());
        
        // Handle table selection
        orderTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> viewModel.selectOrder(newVal));
        
        // Handle double-click for details
        orderTable.setRowFactory(tv -> {
            TableRow<Order> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    showOrderDetail(row.getItem());
                }
            });
            return row;
        });
        
        // Load orders
        viewModel.loadOrders();
        
        logger.debug("Order list view controller initialized");
    }
    
    @FXML
    private void handleRefresh() {
        viewModel.refresh();
    }
    
    @FXML
    private void handlePrevPage() {
        viewModel.previousPage();
    }
    
    @FXML
    private void handleNextPage() {
        viewModel.nextPage();
    }
    
    private void showOrderDetail(Order order) {
        logger.debug("Showing order detail: {}", order.getOrderNumber());
        // Open order detail dialog
    }
    
    private void updatePageInfo() {
        int current = viewModel.currentPageProperty().get() + 1;
        int total = viewModel.totalPagesProperty().get();
        int elements = viewModel.totalElementsProperty().get();
        pageInfoLabel.setText(String.format("Page %d of %d (%d orders)", current, total, elements));
        
        prevButton.setDisable(viewModel.currentPageProperty().get() == 0);
        nextButton.setDisable(viewModel.currentPageProperty().get() >= viewModel.totalPagesProperty().get() - 1);
    }
}
