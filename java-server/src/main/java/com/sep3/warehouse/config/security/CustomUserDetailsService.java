package com.sep3.warehouse.config.security;

import com.sep3.warehouse.entity.User;
import com.sep3.warehouse.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Collections;

/**
 * Custom UserDetailsService implementation for Spring Security.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {
    
    private final UserRepository userRepository;
    
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        
        String passwordHash = user.getPasswordHash();
        
        // Debug logging to verify hash from database
        log.debug("Loading user: {}", username);
        log.debug("Password hash length: {}", passwordHash != null ? passwordHash.length() : "null");
        log.debug("Password hash (first 30 chars): {}", passwordHash != null && passwordHash.length() >= 30 ? passwordHash.substring(0, 30) : passwordHash);
        log.debug("Password hash (full): [{}]", passwordHash);
        log.debug("Is active: {}", user.getIsActive());
        
        // Trim the hash in case there are leading/trailing spaces
        if (passwordHash != null) {
            passwordHash = passwordHash.trim();
        }
        
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                passwordHash,
                user.getIsActive(),
                true,
                true,
                true,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().getName()))
        );
    }
    
    @Transactional(readOnly = true)
    public UserDetails loadUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));
        
        String passwordHash = user.getPasswordHash();
        // Trim the hash in case there are leading/trailing spaces
        if (passwordHash != null) {
            passwordHash = passwordHash.trim();
        }
        
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                passwordHash,
                user.getIsActive(),
                true,
                true,
                true,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().getName()))
        );
    }
}
