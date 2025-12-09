package com.sep3.client.view;

import com.sep3.client.viewmodel.MainViewModel;
import com.sep3.client.viewmodel.ViewModelFactory;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;

/**
 * Controller for the main application view.
 */
public class MainViewController {
    
    private static final Logger logger = LoggerFactory.getLogger(MainViewController.class);
    
    @FXML private BorderPane mainPane;
    @FXML private Label userNameLabel;
    @FXML private Label userRoleLabel;
    @FXML private Button dashboardButton;
    @FXML private Button productsButton;
    @FXML private Button ordersButton;
    @FXML private Button reportsButton;
    @FXML private Button usersButton;
    @FXML private Button logoutButton;
    @FXML private StackPane contentPane;
    
    private MainViewModel viewModel;
    private ViewModelFactory viewModelFactory;
    private ViewHandler viewHandler;
    
    /**
     * Initialize the controller.
     */
    public void init(ViewModelFactory viewModelFactory, ViewHandler viewHandler) {
        this.viewModelFactory = viewModelFactory;
        this.viewHandler = viewHandler;
        this.viewModel = viewModelFactory.getMainViewModel();
        
        viewModel.updateUserInfo();
        
        // Bind user info
        userNameLabel.textProperty().bind(viewModel.currentUserNameProperty());
        userRoleLabel.textProperty().bind(viewModel.currentUserRoleProperty());
        
        // Bind visibility based on permissions
        reportsButton.visibleProperty().bind(viewModel.canViewReportsProperty());
        reportsButton.managedProperty().bind(viewModel.canViewReportsProperty());
        usersButton.visibleProperty().bind(viewModel.isAdminProperty());
        usersButton.managedProperty().bind(viewModel.isAdminProperty());
        
        // Show dashboard by default
        showDashboard();
        
        logger.debug("Main view controller initialized");
    }
    
    @FXML
    private void showDashboard() {
        logger.debug("Showing dashboard");
        loadView("/fxml/DashboardView.fxml", "dashboard");
    }
    
    @FXML
    private void showProducts() {
        logger.debug("Showing products");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ProductListView.fxml"));
            Parent view = loader.load();
            
            ProductListViewController controller = loader.getController();
            controller.init(viewModelFactory.getProductListViewModel(), viewModelFactory, viewHandler);
            
            contentPane.getChildren().clear();
            contentPane.getChildren().add(view);
        } catch (IOException e) {
            logger.error("Failed to load products view", e);
            showError("Failed to load products view");
        }
    }
    
    @FXML
    private void showOrders() {
        logger.debug("Showing orders");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/OrderListView.fxml"));
            Parent view = loader.load();
            
            OrderListViewController controller = loader.getController();
            controller.init(viewModelFactory.getOrderListViewModel(), viewModelFactory, viewHandler);
            
            contentPane.getChildren().clear();
            contentPane.getChildren().add(view);
        } catch (IOException e) {
            logger.error("Failed to load orders view", e);
            showError("Failed to load orders view");
        }
    }
    
    @FXML
    private void showReports() {
        logger.debug("Showing reports");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ReportsView.fxml"));
            Parent view = loader.load();
            
            ReportsViewController controller = loader.getController();
            controller.init(viewModelFactory.getReportsViewModel(), viewModelFactory);
            
            contentPane.getChildren().clear();
            contentPane.getChildren().add(view);
        } catch (IOException e) {
            logger.error("Failed to load reports view", e);
            showError("Failed to load reports view");
        }
    }
    
    @FXML
    private void showUsers() {
        logger.debug("Showing users");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/UsersManagementView.fxml"));
            Parent view = loader.load();
            
            UsersManagementViewController controller = loader.getController();
            controller.init(viewModelFactory, viewHandler);
            
            contentPane.getChildren().clear();
            contentPane.getChildren().add(view);
        } catch (IOException e) {
            logger.error("Failed to load users view", e);
            showError("Failed to load users view");
        }
    }
    
    @FXML
    private void handleLogout() {
        logger.info("User logging out");
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout");
        alert.setHeaderText("Are you sure you want to logout?");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                viewModel.logout();
                viewHandler.openLoginView();
            }
        });
    }
    
    private void loadView(String fxmlPath, String viewName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
            
            contentPane.getChildren().clear();
            contentPane.getChildren().add(view);
            
            viewModel.navigateTo(viewName);
        } catch (IOException e) {
            logger.error("Failed to load view: {}", fxmlPath, e);
            showError("Failed to load view");
        } catch (Exception e) {
            // If FXML not found, show placeholder
            logger.warn("View not found: {}, showing placeholder", fxmlPath);
            showPlaceholder(viewName);
        }
    }
    
    private void showPlaceholder(String viewName) {
        Label placeholder = new Label("Coming soon: " + viewName);
        placeholder.setStyle("-fx-font-size: 24px; -fx-text-fill: #666;");
        contentPane.getChildren().clear();
        contentPane.getChildren().add(placeholder);
    }
    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
