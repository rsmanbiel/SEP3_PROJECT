package com.sep3.client;

import com.sep3.client.view.ViewHandler;
import com.sep3.client.viewmodel.ViewModelFactory;
import javafx.application.Application;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main application class for the JavaFX Warehouse Client.
 * This client communicates with the Java server via REST API.
 */
public class WarehouseClientApp extends Application {
    
    private static final Logger logger = LoggerFactory.getLogger(WarehouseClientApp.class);
    
    @Override
    public void start(Stage primaryStage) {
        logger.info("Starting Warehouse Client Application");
        
        try {
            ViewModelFactory viewModelFactory = new ViewModelFactory();
            ViewHandler viewHandler = new ViewHandler(primaryStage, viewModelFactory);
            viewHandler.start();
            
            logger.info("Application started successfully");
        } catch (Exception e) {
            logger.error("Failed to start application", e);
            throw e;
        }
    }
    
    @Override
    public void stop() {
        logger.info("Shutting down Warehouse Client Application");
    }
    
    public static void main(String[] args) {
        logger.info("Launching Warehouse Client");
        launch(args);
    }
}
