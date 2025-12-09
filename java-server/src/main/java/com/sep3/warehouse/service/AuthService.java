package com.sep3.warehouse.service;

import com.sep3.warehouse.config.security.JwtTokenProvider;
import com.sep3.warehouse.dto.auth.LoginRequest;
import com.sep3.warehouse.dto.auth.LoginResponse;
import com.sep3.warehouse.dto.auth.RegisterRequest;
import com.sep3.warehouse.entity.Role;
import com.sep3.warehouse.entity.User;
import com.sep3.warehouse.exception.BadRequestException;
import com.sep3.warehouse.exception.DuplicateResourceException;
import com.sep3.warehouse.exception.ResourceNotFoundException;
import com.sep3.warehouse.repository.RoleRepository;
import com.sep3.warehouse.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for authentication operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthService {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    
    /**
     * Authenticate user and generate JWT tokens.
     */
    public LoginResponse login(LoginRequest request) {
        log.info("Login attempt for user: {}", request.getUsername());
        
        String receivedPassword = request.getPassword();
        if (receivedPassword == null) {
            throw new BadRequestException("Password cannot be null");
        }
        
        // Trim password to handle leading/trailing spaces
        String passwordForAuth = receivedPassword.trim();
        
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        passwordForAuth
                )
        );
        
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", request.getUsername()));
        
        if (!user.getIsActive()) {
            throw new BadRequestException("User account is deactivated");
        }
        
        String accessToken = jwtTokenProvider.generateAccessToken(user);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user);
        
        log.info("User {} logged in successfully", request.getUsername());
        
        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getAccessTokenExpiration())
                .user(LoginResponse.UserInfo.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .role(user.getRole().getName())
                        .build())
                .build();
    }
    
    /**
     * Register a new user (customer role by default).
     */
    public LoginResponse register(RegisterRequest request) {
        log.info("Registration attempt for user: {}", request.getUsername());
        
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("User", "username", request.getUsername());
        }
        
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("User", "email", request.getEmail());
        }
        
        Role customerRole = roleRepository.findByName("CUSTOMER")
                .orElseThrow(() -> new ResourceNotFoundException("Role", "name", "CUSTOMER"));
        
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
                .role(customerRole)
                .isActive(true)
                .build();
        
        User savedUser = userRepository.save(user);
        
        log.info("User {} registered successfully", savedUser.getUsername());
        
        String accessToken = jwtTokenProvider.generateAccessToken(savedUser);
        String refreshToken = jwtTokenProvider.generateRefreshToken(savedUser);
        
        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getAccessTokenExpiration())
                .user(LoginResponse.UserInfo.builder()
                        .id(savedUser.getId())
                        .username(savedUser.getUsername())
                        .email(savedUser.getEmail())
                        .firstName(savedUser.getFirstName())
                        .lastName(savedUser.getLastName())
                        .role(savedUser.getRole().getName())
                        .build())
                .build();
    }
    
    /**
     * Refresh access token using refresh token.
     */
    public LoginResponse refreshToken(String refreshToken) {
        log.debug("Refreshing token");
        
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new BadRequestException("Invalid refresh token");
        }
        
        String username = jwtTokenProvider.getUsernameFromToken(refreshToken);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        String newAccessToken = jwtTokenProvider.generateAccessToken(user);
        
        return LoginResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getAccessTokenExpiration())
                .user(LoginResponse.UserInfo.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .role(user.getRole().getName())
                        .build())
                .build();
    }
}
