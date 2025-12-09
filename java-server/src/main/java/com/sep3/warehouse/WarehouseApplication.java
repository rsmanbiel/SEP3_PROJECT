package com.sep3.warehouse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main application class for the Warehouse Management System.
 * 
 * This is the Java Server (Tier 2) that:
 * - Exposes REST API for the JavaFX client
 * - Connects to PostgreSQL database
 * - Communicates with C# Shipment microservice via gRPC
 */
@SpringBootApplication
@EnableScheduling
public class WarehouseApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(WarehouseApplication.class, args);
    }
}
