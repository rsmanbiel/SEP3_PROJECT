package com.sep3.warehouse.service;

import com.sep3.warehouse.dto.user.CreateUserRequest;
import com.sep3.warehouse.dto.user.UpdateUserRequest;
import com.sep3.warehouse.dto.user.UserDTO;
import com.sep3.warehouse.entity.Role;
import com.sep3.warehouse.entity.User;
import com.sep3.warehouse.exception.DuplicateResourceException;
import com.sep3.warehouse.exception.ResourceNotFoundException;
import com.sep3.warehouse.repository.RoleRepository;
import com.sep3.warehouse.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing users.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    
    /**
     * Get all users with pagination.
     */
    @Transactional(readOnly = true)
    public Page<UserDTO> getAllUsers(Pageable pageable) {
        log.debug("Fetching all users");
        return userRepository.findAll(pageable)
                .map(this::mapToDTO);
    }
    
    /**
     * Get all active users with pagination.
     */
    @Transactional(readOnly = true)
    public Page<UserDTO> getAllActiveUsers(Pageable pageable) {
        log.debug("Fetching all active users");
        return userRepository.findAllActive(pageable)
                .map(this::mapToDTO);
    }
    
    /**
     * Get user by ID.
     */
    @Transactional(readOnly = true)
    public UserDTO getUserById(Long id) {
        log.debug("Fetching user with id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return mapToDTO(user);
    }
    
    /**
     * Get user by username.
     */
    @Transactional(readOnly = true)
    public UserDTO getUserByUsername(String username) {
        log.debug("Fetching user with username: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        return mapToDTO(user);
    }
    
    /**
     * Search users.
     */
    @Transactional(readOnly = true)
    public Page<UserDTO> searchUsers(String search, Pageable pageable) {
        log.debug("Searching users with query: {}", search);
        return userRepository.search(search, pageable)
                .map(this::mapToDTO);
    }
    
    /**
     * Get users by role.
     */
    @Transactional(readOnly = true)
    public List<UserDTO> getUsersByRole(String roleName) {
        log.debug("Fetching users with role: {}", roleName);
        return userRepository.findByRoleName(roleName).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Create a new user.
     */
    public UserDTO createUser(CreateUserRequest request) {
        log.info("Creating new user: {}", request.getUsername());
        
        // Check for duplicate username
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("User", "username", request.getUsername());
        }
        
        // Check for duplicate email
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("User", "email", request.getEmail());
        }
        
        // Get role
        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", request.getRoleId()));
        
        // Create user
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .address(request.getAddress())
                .city(request.getCity())
                .postalCode(request.getPostalCode())
                .country(request.getCountry() != null ? request.getCountry() : "Denmark")
                .role(role)
                .isActive(true)
                .build();
        
        User savedUser = userRepository.save(user);
        log.info("User created successfully: {}", savedUser.getUsername());
        
        return mapToDTO(savedUser);
    }
    
    /**
     * Update an existing user.
     */
    public UserDTO updateUser(Long id, UpdateUserRequest request) {
        log.info("Updating user: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        
        // Update email if provided and different
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new DuplicateResourceException("User", "email", request.getEmail());
            }
            user.setEmail(request.getEmail());
        }
        
        // Update password if provided
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }
        
        // Update other fields
        if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
        if (request.getLastName() != null) user.setLastName(request.getLastName());
        if (request.getPhone() != null) user.setPhone(request.getPhone());
        if (request.getAddress() != null) user.setAddress(request.getAddress());
        if (request.getCity() != null) user.setCity(request.getCity());
        if (request.getPostalCode() != null) user.setPostalCode(request.getPostalCode());
        if (request.getCountry() != null) user.setCountry(request.getCountry());
        if (request.getIsActive() != null) user.setIsActive(request.getIsActive());
        
        // Update role if provided
        if (request.getRoleId() != null) {
            Role role = roleRepository.findById(request.getRoleId())
                    .orElseThrow(() -> new ResourceNotFoundException("Role", "id", request.getRoleId()));
            user.setRole(role);
        }
        
        User updatedUser = userRepository.save(user);
        log.info("User updated successfully: {}", updatedUser.getUsername());
        
        return mapToDTO(updatedUser);
    }
    
    /**
     * Delete (deactivate) a user.
     */
    public void deleteUser(Long id) {
        log.info("Deleting user: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        
        // Soft delete - deactivate instead of hard delete
        user.setIsActive(false);
        userRepository.save(user);
        
        log.info("User deactivated successfully: {}", user.getUsername());
    }
    
    /**
     * Map User entity to UserDTO.
     */
    private UserDTO mapToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .address(user.getAddress())
                .city(user.getCity())
                .postalCode(user.getPostalCode())
                .country(user.getCountry())
                .roleId(user.getRole() != null ? user.getRole().getId() : null)
                .roleName(user.getRole() != null ? user.getRole().getName() : null)
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}

