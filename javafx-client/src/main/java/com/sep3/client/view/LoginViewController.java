package com.sep3.client.view;

import com.sep3.client.viewmodel.LoginViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller for the login view.
 */
public class LoginViewController {
    
    private static final Logger logger = LoggerFactory.getLogger(LoginViewController.class);
    
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Label errorLabel;
    @FXML private ProgressIndicator loadingIndicator;
    @FXML private VBox loginBox;
    
    private LoginViewModel viewModel;
    private ViewHandler viewHandler;
    
    /**
     * Initialize the controller with ViewModel.
     */
    public void init(LoginViewModel viewModel, ViewHandler viewHandler) {
        this.viewModel = viewModel;
        this.viewHandler = viewHandler;
        
        // Clear previous state
        viewModel.clear();
        
        // Bind properties
        usernameField.textProperty().bindBidirectional(viewModel.usernameProperty());
        passwordField.textProperty().bindBidirectional(viewModel.passwordProperty());
        errorLabel.textProperty().bind(viewModel.errorMessageProperty());
        loadingIndicator.visibleProperty().bind(viewModel.isLoadingProperty());
        loginButton.disableProperty().bind(viewModel.isLoadingProperty());
        
        // Set login success callback
        viewModel.setOnLoginSuccess(user -> {
            logger.info("Login successful, opening main view");
            viewHandler.openMainView();
        });
        
        // Enter key triggers login
        passwordField.setOnAction(event -> handleLogin());
        
        logger.debug("Login view controller initialized");
    }
    
    @FXML
    private void handleLogin() {
        logger.debug("Login button clicked");
        viewModel.login();
    }
}
