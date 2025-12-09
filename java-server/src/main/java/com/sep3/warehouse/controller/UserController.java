package com.sep3.warehouse.controller;

import com.sep3.warehouse.dto.user.CreateUserRequest;
import com.sep3.warehouse.dto.user.UpdateUserRequest;
import com.sep3.warehouse.dto.user.UserDTO;
import com.sep3.warehouse.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for user management.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Users", description = "User management endpoints (Admin only)")
public class UserController {
    
    private final UserService userService;
    
    @GetMapping
    @Operation(summary = "Get all users", description = "Retrieve all users with pagination (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserDTO>> getAllUsers(
            @PageableDefault(size = 20) Pageable pageable) {
        log.debug("GET /api/users");
        return ResponseEntity.ok(userService.getAllUsers(pageable));
    }
    
    @GetMapping("/active")
    @Operation(summary = "Get all active users", description = "Retrieve all active users with pagination (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserDTO>> getAllActiveUsers(
            @PageableDefault(size = 20) Pageable pageable) {
        log.debug("GET /api/users/active");
        return ResponseEntity.ok(userService.getAllActiveUsers(pageable));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Retrieve a specific user by its ID (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        log.debug("GET /api/users/{}", id);
        return ResponseEntity.ok(userService.getUserById(id));
    }
    
    @GetMapping("/username/{username}")
    @Operation(summary = "Get user by username", description = "Retrieve a user by username (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> getUserByUsername(@PathVariable String username) {
        log.debug("GET /api/users/username/{}", username);
        return ResponseEntity.ok(userService.getUserByUsername(username));
    }
    
    @GetMapping("/search")
    @Operation(summary = "Search users", description = "Search users by username, email, or name (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserDTO>> searchUsers(
            @RequestParam String query,
            @PageableDefault(size = 20) Pageable pageable) {
        log.debug("GET /api/users/search?query={}", query);
        return ResponseEntity.ok(userService.searchUsers(query, pageable));
    }
    
    @GetMapping("/role/{roleName}")
    @Operation(summary = "Get users by role", description = "Retrieve all users with a specific role (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> getUsersByRole(@PathVariable String roleName) {
        log.debug("GET /api/users/role/{}", roleName);
        return ResponseEntity.ok(userService.getUsersByRole(roleName));
    }
    
    @PostMapping
    @Operation(summary = "Create user", description = "Create a new user (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody CreateUserRequest request) {
        log.info("POST /api/users - Creating user: {}", request.getUsername());
        UserDTO created = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update user", description = "Update an existing user (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request) {
        log.info("PUT /api/users/{}", id);
        return ResponseEntity.ok(userService.updateUser(id, request));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user", description = "Deactivate a user (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.info("DELETE /api/users/{}", id);
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}

