package com.sep3.client.viewmodel;

import com.sep3.client.model.User;
import com.sep3.client.service.AuthService;
import javafx.application.Platform;
import javafx.beans.property.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.function.Consumer;

/**
 * ViewModel for the login view.
 * Handles user authentication logic.
 */
public class LoginViewModel {
    
    private static final Logger logger = LoggerFactory.getLogger(LoginViewModel.class);
    private final AuthService authService;
    
    // Properties bound to the view
    private final StringProperty username = new SimpleStringProperty("");
    private final StringProperty password = new SimpleStringProperty("");
    private final StringProperty errorMessage = new SimpleStringProperty("");
    private final BooleanProperty isLoading = new SimpleBooleanProperty(false);
    private final BooleanProperty loginSuccessful = new SimpleBooleanProperty(false);
    
    // Callback for successful login
    private Consumer<User> onLoginSuccess;
    
    public LoginViewModel() {
        this.authService = AuthService.getInstance();
    }
    
    /**
     * Attempt to login with provided credentials.
     */
    public void login() {
        String user = username.get().trim();
        String pass = password.get();
        
        // Validation
        if (user.isEmpty()) {
            errorMessage.set("Username is required");
            return;
        }
        if (pass.isEmpty()) {
            errorMessage.set("Password is required");
            return;
        }
        
        isLoading.set(true);
        errorMessage.set("");
        
        logger.info("Attempting login for user: {}", user);
        
        authService.login(user, pass)
                .thenAccept(loggedInUser -> Platform.runLater(() -> {
                    isLoading.set(false);
                    loginSuccessful.set(true);
                    
                    if (onLoginSuccess != null) {
                        onLoginSuccess.accept(loggedInUser);
                    }
                }))
                .exceptionally(throwable -> {
                    Platform.runLater(() -> {
                        isLoading.set(false);
                        String message = throwable.getMessage();
                        if (message.contains("401") || message.contains("Invalid")) {
                            errorMessage.set("Invalid username or password");
                        } else {
                            errorMessage.set("Login failed. Please try again.");
                        }
                        logger.error("Login failed", throwable);
                    });
                    return null;
                });
    }
    
    /**
     * Clear all fields.
     */
    public void clear() {
        username.set("");
        password.set("");
        errorMessage.set("");
        isLoading.set(false);
        loginSuccessful.set(false);
    }
    
    // Property getters
    public StringProperty usernameProperty() { return username; }
    public StringProperty passwordProperty() { return password; }
    public StringProperty errorMessageProperty() { return errorMessage; }
    public BooleanProperty isLoadingProperty() { return isLoading; }
    public BooleanProperty loginSuccessfulProperty() { return loginSuccessful; }
    
    // Callback setter
    public void setOnLoginSuccess(Consumer<User> callback) {
        this.onLoginSuccess = callback;
    }
}
