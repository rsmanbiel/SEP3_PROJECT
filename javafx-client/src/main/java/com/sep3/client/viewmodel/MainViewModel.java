package com.sep3.client.viewmodel;

import com.sep3.client.model.User;
import com.sep3.client.service.AuthService;
import javafx.beans.property.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ViewModel for the main application view.
 * Manages navigation and user session.
 */
public class MainViewModel {
    
    private static final Logger logger = LoggerFactory.getLogger(MainViewModel.class);
    private final AuthService authService;
    
    // Properties
    private final StringProperty currentUserName = new SimpleStringProperty("");
    private final StringProperty currentUserRole = new SimpleStringProperty("");
    private final BooleanProperty canManageProducts = new SimpleBooleanProperty(false);
    private final BooleanProperty canManageOrders = new SimpleBooleanProperty(false);
    private final BooleanProperty canViewReports = new SimpleBooleanProperty(false);
    private final BooleanProperty isAdmin = new SimpleBooleanProperty(false);
    
    // Navigation state
    private final StringProperty currentView = new SimpleStringProperty("dashboard");
    
    public MainViewModel() {
        this.authService = AuthService.getInstance();
        updateUserInfo();
    }
    
    /**
     * Update user information from auth service.
     */
    public void updateUserInfo() {
        User user = authService.getCurrentUser();
        
        if (user != null) {
            currentUserName.set(user.getFullName());
            currentUserRole.set(user.getRole());
            
            // Set permissions based on role
            String role = user.getRole();
            isAdmin.set("ADMIN".equals(role));
            canManageProducts.set(authService.canManageInventory());
            canManageOrders.set(authService.canManageInventory());
            canViewReports.set(authService.isAdminOrSupervisor());
            
            logger.info("User info updated: {} ({})", user.getUsername(), role);
        }
    }
    
    /**
     * Logout current user.
     */
    public void logout() {
        logger.info("User logging out");
        authService.logout();
        
        currentUserName.set("");
        currentUserRole.set("");
        canManageProducts.set(false);
        canManageOrders.set(false);
        canViewReports.set(false);
        isAdmin.set(false);
    }
    
    /**
     * Navigate to a specific view.
     */
    public void navigateTo(String viewName) {
        logger.debug("Navigating to: {}", viewName);
        currentView.set(viewName);
    }
    
    // Property getters
    public StringProperty currentUserNameProperty() { return currentUserName; }
    public StringProperty currentUserRoleProperty() { return currentUserRole; }
    public BooleanProperty canManageProductsProperty() { return canManageProducts; }
    public BooleanProperty canManageOrdersProperty() { return canManageOrders; }
    public BooleanProperty canViewReportsProperty() { return canViewReports; }
    public BooleanProperty isAdminProperty() { return isAdmin; }
    public StringProperty currentViewProperty() { return currentView; }
}
