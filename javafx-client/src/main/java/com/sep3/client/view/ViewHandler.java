package com.sep3.client.view;

import com.sep3.client.viewmodel.ViewModelFactory;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;

/**
 * Handler for managing views and navigation.
 */
public class ViewHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(ViewHandler.class);
    
    private final Stage primaryStage;
    private final ViewModelFactory viewModelFactory;
    private Scene currentScene;
    
    public ViewHandler(Stage primaryStage, ViewModelFactory viewModelFactory) {
        this.primaryStage = primaryStage;
        this.viewModelFactory = viewModelFactory;
    }
    
    /**
     * Start the application with the login view.
     */
    public void start() {
        primaryStage.setTitle("Warehouse Management System");
        openLoginView();
        primaryStage.show();
    }
    
    /**
     * Open the login view.
     */
    public void openLoginView() {
        logger.info("Opening login view");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LoginView.fxml"));
            Parent root = loader.load();
            
            LoginViewController controller = loader.getController();
            controller.init(viewModelFactory.getLoginViewModel(), this);
            
            if (currentScene == null) {
                currentScene = new Scene(root, 400, 500);
                currentScene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
            } else {
                currentScene.setRoot(root);
            }
            
            primaryStage.setScene(currentScene);
            primaryStage.setWidth(400);
            primaryStage.setHeight(500);
            primaryStage.centerOnScreen();
            
        } catch (IOException e) {
            logger.error("Failed to open login view", e);
            throw new RuntimeException("Failed to load login view", e);
        }
    }
    
    /**
     * Open the main view after successful login.
     */
    public void openMainView() {
        logger.info("Opening main view");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainView.fxml"));
            Parent root = loader.load();
            
            MainViewController controller = loader.getController();
            controller.init(viewModelFactory, this);
            
            currentScene.setRoot(root);
            primaryStage.setWidth(1200);
            primaryStage.setHeight(800);
            primaryStage.centerOnScreen();
            
        } catch (IOException e) {
            logger.error("Failed to open main view", e);
            throw new RuntimeException("Failed to load main view", e);
        }
    }
    
    /**
     * Get the ViewModelFactory.
     */
    public ViewModelFactory getViewModelFactory() {
        return viewModelFactory;
    }
}
