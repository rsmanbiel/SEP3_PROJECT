package com.sep3.client.service;

import com.sep3.client.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Service for user management operations.
 */
public class UserService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final HttpClientService httpClient;
    
    private static UserService instance;
    
    private UserService() {
        this.httpClient = HttpClientService.getInstance();
    }
    
    public static synchronized UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }
    
    /**
     * Get all users.
     */
    public CompletableFuture<ProductService.PageResponse<User>> getAllUsers(int page, int size) {
        logger.debug("Fetching users - page: {}, size: {}", page, size);
        
        String endpoint = String.format("/users?page=%d&size=%d", page, size);
        
        Type type = new com.google.gson.reflect.TypeToken<ProductService.PageResponse<User>>(){}.getType();
        return httpClient.get(endpoint, type);
    }
    
    /**
     * Get user by ID.
     */
    public CompletableFuture<User> getUserById(Long id) {
        logger.debug("Fetching user: {}", id);
        return httpClient.get("/users/" + id, User.class);
    }
    
    /**
     * Search users.
     */
    public CompletableFuture<ProductService.PageResponse<User>> searchUsers(String query, int page, int size) {
        logger.debug("Searching users: {}", query);
        
        String endpoint = String.format("/users/search?query=%s&page=%d&size=%d", 
                query, page, size);
        
        Type type = new com.google.gson.reflect.TypeToken<ProductService.PageResponse<User>>(){}.getType();
        return httpClient.get(endpoint, type);
    }
    
    /**
     * Create new user.
     */
    public CompletableFuture<User> createUser(CreateUserRequest request) {
        logger.info("Creating user: {}", request.username);
        return httpClient.post("/users", request, User.class);
    }
    
    /**
     * Update user.
     */
    public CompletableFuture<User> updateUser(Long id, UpdateUserRequest request) {
        logger.info("Updating user: {}", id);
        return httpClient.put("/users/" + id, request, User.class);
    }
    
    /**
     * Delete user.
     */
    public CompletableFuture<Void> deleteUser(Long id) {
        logger.info("Deleting user: {}", id);
        return httpClient.delete("/users/" + id);
    }
    
    // Inner classes for requests
    public record CreateUserRequest(
            String username,
            String email,
            String password,
            String firstName,
            String lastName,
            String phone,
            String address,
            String city,
            String postalCode,
            String country,
            Long roleId
    ) {}
    
    public record UpdateUserRequest(
            String email,
            String password,
            String firstName,
            String lastName,
            String phone,
            String address,
            String city,
            String postalCode,
            String country,
            Long roleId,
            Boolean isActive
    ) {}
}

