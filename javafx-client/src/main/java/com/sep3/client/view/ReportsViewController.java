package com.sep3.client.view;

import com.sep3.client.model.Product;
import com.sep3.client.model.Order;
import com.sep3.client.service.ProductService;
import com.sep3.client.service.OrderService;
import com.sep3.client.viewmodel.ViewModelFactory;
import com.sep3.client.viewmodel.ReportsViewModel;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Controller for the reports/analytics view.
 */
public class ReportsViewController {
    
    private static final Logger logger = LoggerFactory.getLogger(ReportsViewController.class);
    
    @FXML private Label totalProductsLabel;
    @FXML private Label totalOrdersLabel;
    @FXML private Label lowStockLabel;
    @FXML private Label totalRevenueLabel;
    @FXML private PieChart categoryChart;
    @FXML private BarChart<String, Number> stockChart;
    @FXML private PieChart orderStatusChart;
    @FXML private BarChart<String, Number> topProductsChart;
    @FXML private CategoryAxis stockCategoryAxis;
    @FXML private NumberAxis stockValueAxis;
    @FXML private CategoryAxis productAxis;
    @FXML private NumberAxis valueAxis;
    @FXML private ProgressIndicator loadingIndicator;
    @FXML private Label errorLabel;
    
    private ProductService productService;
    private OrderService orderService;
    private ReportsViewModel viewModel;
    
    public void init(ReportsViewModel viewModel, ViewModelFactory viewModelFactory) {
        this.viewModel = viewModel;
        this.productService = ProductService.getInstance();
        this.orderService = OrderService.getInstance();
        
        // Bind properties
        loadingIndicator.visibleProperty().bind(viewModel.isLoadingProperty());
        errorLabel.textProperty().bind(viewModel.errorMessageProperty());
        
        // Load data
        loadReports();
        
        logger.debug("Reports view controller initialized");
    }
    
    private void loadReports() {
        viewModel.setLoading(true);
        viewModel.setErrorMessage("");
        
        // Load products and orders in parallel
        productService.getAllProducts(0, 1000)
                .thenCompose(productsResponse -> {
                    List<Product> products = productsResponse.content != null ? productsResponse.content : Collections.emptyList();
                    
                    return orderService.getAllOrders(0, 1000)
                            .thenApply(ordersResponse -> {
                                List<Order> orders = ordersResponse.content != null ? ordersResponse.content : Collections.emptyList();
                                return new Object[]{products, orders};
                            });
                })
                .thenAccept(data -> Platform.runLater(() -> {
                    @SuppressWarnings("unchecked")
                    List<Product> products = (List<Product>) data[0];
                    @SuppressWarnings("unchecked")
                    List<Order> orders = (List<Order>) data[1];
                    
                    updateStats(products, orders);
                    updateCharts(products, orders);
                    
                    viewModel.setLoading(false);
                }))
                .exceptionally(throwable -> {
                    Platform.runLater(() -> {
                        viewModel.setLoading(false);
                        viewModel.setErrorMessage("Failed to load reports: " + throwable.getMessage());
                        logger.error("Failed to load reports", throwable);
                    });
                    return null;
                });
    }
    
    private void updateStats(List<Product> products, List<Order> orders) {
        totalProductsLabel.setText(String.valueOf(products.size()));
        totalOrdersLabel.setText(String.valueOf(orders.size()));
        
        long lowStockCount = products.stream()
                .filter(p -> p.getIsLowStock() != null && p.getIsLowStock())
                .count();
        lowStockLabel.setText(String.valueOf(lowStockCount));
        
        BigDecimal totalRevenue = orders.stream()
                .filter(o -> o.getTotalAmount() != null)
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        totalRevenueLabel.setText(String.format("%.2f DKK", totalRevenue.doubleValue()));
    }
    
    private void updateCharts(List<Product> products, List<Order> orders) {
        // Clear all charts first
        categoryChart.getData().clear();
        stockChart.getData().clear();
        orderStatusChart.getData().clear();
        topProductsChart.getData().clear();
        
        // Products by Category Chart
        if (!products.isEmpty()) {
            Map<String, Long> categoryCounts = products.stream()
                    .filter(p -> p.getCategoryName() != null)
                    .collect(Collectors.groupingBy(
                            Product::getCategoryName,
                            Collectors.counting()
                    ));
            
            if (!categoryCounts.isEmpty()) {
                ObservableList<PieChart.Data> categoryData = FXCollections.observableArrayList();
                categoryCounts.forEach((category, count) -> 
                        categoryData.add(new PieChart.Data(category + " (" + count + ")", count)));
                categoryChart.setData(categoryData);
            }
            
            // Stock Levels Chart
            long inStock = products.stream()
                    .filter(p -> p.getQuantityInStock() != null && p.getQuantityInStock() > 0)
                    .count();
            long lowStock = products.stream()
                    .filter(p -> p.getIsLowStock() != null && p.getIsLowStock())
                    .count();
            long outOfStock = products.stream()
                    .filter(p -> p.getQuantityInStock() != null && p.getQuantityInStock() == 0)
                    .count();
            
            XYChart.Series<String, Number> stockSeries = new XYChart.Series<>();
            stockSeries.setName("Products");
            stockSeries.getData().add(new XYChart.Data<>("In Stock", inStock));
            stockSeries.getData().add(new XYChart.Data<>("Low Stock", lowStock));
            stockSeries.getData().add(new XYChart.Data<>("Out of Stock", outOfStock));
            
            stockChart.getData().add(stockSeries);
            
            // Top Products by Stock Value
            List<Product> topProducts = products.stream()
                    .filter(p -> p.getPrice() != null && p.getQuantityInStock() != null && p.getQuantityInStock() > 0)
                    .sorted((p1, p2) -> {
                        BigDecimal value1 = p1.getPrice().multiply(BigDecimal.valueOf(p1.getQuantityInStock()));
                        BigDecimal value2 = p2.getPrice().multiply(BigDecimal.valueOf(p2.getQuantityInStock()));
                        return value2.compareTo(value1);
                    })
                    .limit(10)
                    .collect(Collectors.toList());
            
            if (!topProducts.isEmpty()) {
                XYChart.Series<String, Number> topProductsSeries = new XYChart.Series<>();
                topProductsSeries.setName("Stock Value");
                for (Product product : topProducts) {
                    BigDecimal value = product.getPrice().multiply(BigDecimal.valueOf(product.getQuantityInStock()));
                    String label = product.getSku() != null && product.getSku().length() > 15 
                            ? product.getSku().substring(0, 15) + "..." 
                            : (product.getSku() != null ? product.getSku() : "N/A");
                    topProductsSeries.getData().add(new XYChart.Data<>(label, value.doubleValue()));
                }
                
                topProductsChart.getData().add(topProductsSeries);
            }
        }
        
        // Orders by Status Chart
        if (!orders.isEmpty()) {
            Map<String, Long> statusCounts = orders.stream()
                    .filter(o -> o.getStatus() != null)
                    .collect(Collectors.groupingBy(
                            Order::getStatus,
                            Collectors.counting()
                    ));
            
            if (!statusCounts.isEmpty()) {
                ObservableList<PieChart.Data> statusData = FXCollections.observableArrayList();
                statusCounts.forEach((status, count) -> 
                        statusData.add(new PieChart.Data(status + " (" + count + ")", count)));
                orderStatusChart.setData(statusData);
            }
        }
    }
    
    @FXML
    private void handleRefresh() {
        loadReports();
    }
}

