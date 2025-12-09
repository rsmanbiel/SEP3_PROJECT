package com.sep3.client.viewmodel;

import com.sep3.client.model.Order;
import com.sep3.client.service.OrderService;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.function.Consumer;

/**
 * ViewModel for the order list view.
 * Manages order listing and filtering.
 */
public class OrderListViewModel {
    
    private static final Logger logger = LoggerFactory.getLogger(OrderListViewModel.class);
    private final OrderService orderService;
    
    // Observable list of orders
    private final ObservableList<Order> orders = FXCollections.observableArrayList();
    
    // Properties
    private final StringProperty statusFilter = new SimpleStringProperty("");
    private final BooleanProperty isLoading = new SimpleBooleanProperty(false);
    private final StringProperty errorMessage = new SimpleStringProperty("");
    private final ObjectProperty<Order> selectedOrder = new SimpleObjectProperty<>();
    
    // Pagination
    private final IntegerProperty currentPage = new SimpleIntegerProperty(0);
    private final IntegerProperty totalPages = new SimpleIntegerProperty(0);
    private final IntegerProperty totalElements = new SimpleIntegerProperty(0);
    private final int pageSize = 20;
    
    // Callback for order selection
    private Consumer<Order> onOrderSelected;
    
    public OrderListViewModel() {
        this.orderService = OrderService.getInstance();
        
        // Listen for status filter changes
        statusFilter.addListener((obs, oldVal, newVal) -> {
            currentPage.set(0);
            loadOrders();
        });
    }
    
    /**
     * Load orders from server.
     */
    public void loadOrders() {
        isLoading.set(true);
        errorMessage.set("");
        
        String status = statusFilter.get();
        
        logger.debug("Loading orders - page: {}, status: {}", currentPage.get(), status);
        
        var future = (status == null || status.isEmpty())
                ? orderService.getAllOrders(currentPage.get(), pageSize)
                : orderService.getOrdersByStatus(status, currentPage.get(), pageSize);
        
        future.thenAccept(response -> Platform.runLater(() -> {
                    orders.clear();
                    if (response.content != null) {
                        orders.addAll(response.content);
                    }
                    totalPages.set(response.totalPages);
                    totalElements.set(response.totalElements);
                    isLoading.set(false);
                    
                    logger.info("Loaded {} orders", orders.size());
                }))
                .exceptionally(throwable -> {
                    Platform.runLater(() -> {
                        isLoading.set(false);
                        errorMessage.set("Failed to load orders");
                        logger.error("Failed to load orders", throwable);
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
            loadOrders();
        }
    }
    
    /**
     * Go to previous page.
     */
    public void previousPage() {
        if (currentPage.get() > 0) {
            currentPage.set(currentPage.get() - 1);
            loadOrders();
        }
    }
    
    /**
     * Select an order.
     */
    public void selectOrder(Order order) {
        selectedOrder.set(order);
        if (onOrderSelected != null && order != null) {
            onOrderSelected.accept(order);
        }
    }
    
    /**
     * Refresh the order list.
     */
    public void refresh() {
        currentPage.set(0);
        loadOrders();
    }
    
    // Property getters
    public ObservableList<Order> getOrders() { return orders; }
    public StringProperty statusFilterProperty() { return statusFilter; }
    public BooleanProperty isLoadingProperty() { return isLoading; }
    public StringProperty errorMessageProperty() { return errorMessage; }
    public ObjectProperty<Order> selectedOrderProperty() { return selectedOrder; }
    public IntegerProperty currentPageProperty() { return currentPage; }
    public IntegerProperty totalPagesProperty() { return totalPages; }
    public IntegerProperty totalElementsProperty() { return totalElements; }
    
    // Callback setter
    public void setOnOrderSelected(Consumer<Order> callback) {
        this.onOrderSelected = callback;
    }
}
