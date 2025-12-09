package com.sep3.client.service;

import com.sep3.client.model.AuthResponse;
import com.sep3.client.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.CompletableFuture;

/**
 * Service for authentication operations.
 */
public class AuthService {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private final HttpClientService httpClient;
    
    private User currentUser;
    private String accessToken;
    private String refreshToken;
    
    private static AuthService instance;
    
    private AuthService() {
        this.httpClient = HttpClientService.getInstance();
    }
    
    public static synchronized AuthService getInstance() {
        if (instance == null) {
            instance = new AuthService();
        }
        return instance;
    }
    
    /**
     * Login user with username and password.
     */
    public CompletableFuture<User> login(String username, String password) {
        logger.info("Attempting login for user: {}", username);
        
        var loginRequest = new LoginRequest(username, password);
        
        return httpClient.post("/auth/login", loginRequest, AuthResponse.class)
                .thenApply(response -> {
                    this.accessToken = response.getAccessToken();
                    this.refreshToken = response.getRefreshToken();
                    this.currentUser = response.getUser();
                    
                    if (this.currentUser != null) {
                        logger.info("Login successful for user: {} (role: {})", 
                                this.currentUser.getUsername(), 
                                this.currentUser.getRole());
                        logger.debug("User details - id: {}, email: {}, firstName: {}, lastName: {}, roleName: {}", 
                                this.currentUser.getId(),
                                this.currentUser.getEmail(),
                                this.currentUser.getFirstName(),
                                this.currentUser.getLastName(),
                                this.currentUser.getRoleName());
                    } else {
                        logger.error("Login response received but user is null!");
                    }
                    
                    httpClient.setAuthToken(accessToken);
                    
                    return currentUser;
                });
    }
    
    /**
     * Register new user.
     */
    public CompletableFuture<User> register(RegisterRequest request) {
        logger.info("Attempting registration for user: {}", request.username);
        
        return httpClient.post("/auth/register", request, AuthResponse.class)
                .thenApply(response -> {
                    this.accessToken = response.getAccessToken();
                    this.refreshToken = response.getRefreshToken();
                    this.currentUser = response.getUser();
                    
                    httpClient.setAuthToken(accessToken);
                    
                    logger.info("Registration successful for user: {}", request.username);
                    return currentUser;
                });
    }
    
    /**
     * Logout current user.
     */
    public void logout() {
        logger.info("Logging out user: {}", currentUser != null ? currentUser.getUsername() : "none");
        
        this.currentUser = null;
        this.accessToken = null;
        this.refreshToken = null;
        
        httpClient.clearAuthToken();
    }
    
    /**
     * Check if user is logged in.
     */
    public boolean isLoggedIn() {
        return currentUser != null && accessToken != null;
    }
    
    /**
     * Get current logged in user.
     */
    public User getCurrentUser() {
        return currentUser;
    }
    
    /**
     * Check if current user has a specific role.
     */
    public boolean hasRole(String role) {
        return currentUser != null && role.equals(currentUser.getRole());
    }
    
    /**
     * Check if current user is admin or supervisor.
     */
    public boolean isAdminOrSupervisor() {
        return hasRole("ADMIN") || hasRole("SUPERVISOR");
    }
    
    /**
     * Check if current user can manage inventory.
     */
    public boolean canManageInventory() {
        return hasRole("ADMIN") || hasRole("SUPERVISOR") || hasRole("WAREHOUSE_OPERATOR");
    }
    
    // Inner classes for requests
    private record LoginRequest(String username, String password) {}
    
    public record RegisterRequest(
            String username,
            String email,
            String password,
            String firstName,
            String lastName,
            String phone,
            String address,
            String city,
            String postalCode,
            String country
    ) {}
}
