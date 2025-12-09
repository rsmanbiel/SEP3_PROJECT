package com.sep3.client.viewmodel;

import com.sep3.client.model.Order;
import com.sep3.client.model.OrderItem;
import com.sep3.client.service.OrderService;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.math.BigDecimal;
import java.util.function.Consumer;

/**
 * ViewModel for the order detail view.
 * Handles order viewing and status updates.
 */
public class OrderDetailViewModel {
    
    private static final Logger logger = LoggerFactory.getLogger(OrderDetailViewModel.class);
    private final OrderService orderService;
    
    // Order properties
    private final LongProperty id = new SimpleLongProperty();
    private final StringProperty orderNumber = new SimpleStringProperty("");
    private final StringProperty customerName = new SimpleStringProperty("");
    private final StringProperty customerEmail = new SimpleStringProperty("");
    private final StringProperty status = new SimpleStringProperty("");
    private final ObjectProperty<BigDecimal> totalAmount = new SimpleObjectProperty<>(BigDecimal.ZERO);
    private final StringProperty shippingAddress = new SimpleStringProperty("");
    private final StringProperty notes = new SimpleStringProperty("");
    private final StringProperty createdAt = new SimpleStringProperty("");
    
    // Order items
    private final ObservableList<OrderItem> items = FXCollections.observableArrayList();
    
    // State properties
    private final BooleanProperty isLoading = new SimpleBooleanProperty(false);
    private final StringProperty errorMessage = new SimpleStringProperty("");
    
    // Callback for status update
    private Consumer<Order> onStatusUpdated;
    
    public OrderDetailViewModel() {
        this.orderService = OrderService.getInstance();
    }
    
    /**
     * Load order data.
     */
    public void loadOrder(Order order) {
        if (order == null) {
            clearForm();
            return;
        }
        
        id.set(order.getId());
        orderNumber.set(order.getOrderNumber());
        customerName.set(order.getCustomerName());
        customerEmail.set(order.getCustomerEmail());
        status.set(order.getStatus());
        totalAmount.set(order.getTotalAmount());
        shippingAddress.set(order.getFullShippingAddress());
        notes.set(order.getNotes() != null ? order.getNotes() : "");
        createdAt.set(order.getCreatedAt() != null ? order.getCreatedAt().toString() : "");
        
        items.clear();
        if (order.getItems() != null) {
            items.addAll(order.getItems());
        }
        
        errorMessage.set("");
        
        logger.debug("Loaded order: {}", order.getOrderNumber());
    }
    
    /**
     * Clear form.
     */
    public void clearForm() {
        id.set(0);
        orderNumber.set("");
        customerName.set("");
        customerEmail.set("");
        status.set("");
        totalAmount.set(BigDecimal.ZERO);
        shippingAddress.set("");
        notes.set("");
        createdAt.set("");
        items.clear();
        errorMessage.set("");
    }
    
    /**
     * Update order status.
     */
    public void updateStatus(String newStatus) {
        if (id.get() == 0) {
            errorMessage.set("No order loaded");
            return;
        }
        
        isLoading.set(true);
        errorMessage.set("");
        
        logger.info("Updating order {} status to: {}", id.get(), newStatus);
        
        orderService.updateOrderStatus(id.get(), newStatus, notes.get())
                .thenAccept(order -> Platform.runLater(() -> {
                    isLoading.set(false);
                    status.set(order.getStatus());
                    if (onStatusUpdated != null) {
                        onStatusUpdated.accept(order);
                    }
                    logger.info("Order {} status updated to: {}", id.get(), newStatus);
                }))
                .exceptionally(throwable -> {
                    Platform.runLater(() -> {
                        isLoading.set(false);
                        errorMessage.set("Failed to update order status");
                        logger.error("Failed to update order status", throwable);
                    });
                    return null;
                });
    }
    
    /**
     * Get available status transitions.
     */
    public ObservableList<String> getAvailableStatuses() {
        String currentStatus = status.get();
        ObservableList<String> statuses = FXCollections.observableArrayList();
        
        switch (currentStatus) {
            case "PENDING" -> statuses.addAll("CONFIRMED", "CANCELLED");
            case "CONFIRMED" -> statuses.addAll("PROCESSING", "CANCELLED");
            case "PROCESSING" -> statuses.addAll("READY_FOR_SHIPMENT", "CANCELLED");
            case "READY_FOR_SHIPMENT" -> statuses.addAll("SHIPPED", "CANCELLED");
            case "SHIPPED" -> statuses.addAll("DELIVERED", "RETURNED");
            case "DELIVERED" -> statuses.add("RETURNED");
        }
        
        return statuses;
    }
    
    // Property getters
    public LongProperty idProperty() { return id; }
    public StringProperty orderNumberProperty() { return orderNumber; }
    public StringProperty customerNameProperty() { return customerName; }
    public StringProperty customerEmailProperty() { return customerEmail; }
    public StringProperty statusProperty() { return status; }
    public ObjectProperty<BigDecimal> totalAmountProperty() { return totalAmount; }
    public StringProperty shippingAddressProperty() { return shippingAddress; }
    public StringProperty notesProperty() { return notes; }
    public StringProperty createdAtProperty() { return createdAt; }
    public ObservableList<OrderItem> getItems() { return items; }
    public BooleanProperty isLoadingProperty() { return isLoading; }
    public StringProperty errorMessageProperty() { return errorMessage; }
    
    // Callback setter
    public void setOnStatusUpdated(Consumer<Order> callback) {
        this.onStatusUpdated = callback;
    }
}
