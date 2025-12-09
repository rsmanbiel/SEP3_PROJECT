package com.sep3.warehouse.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Utility to generate correct BCrypt hash for password123
 * Run this with: mvn exec:java -Dexec.mainClass="com.sep3.warehouse.util.GenerateCorrectHash"
 */
public class GenerateCorrectHash {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "password123";
        
        // Generate a new hash
        String hash = encoder.encode(password);
        
        System.out.println("========================================");
        System.out.println("Password: " + password);
        System.out.println("BCrypt Hash: " + hash);
        System.out.println("========================================");
        
        // Verify it works
        boolean matches = encoder.matches(password, hash);
        System.out.println("Verification: Hash matches password = " + matches);
        
        System.out.println("\nSQL UPDATE command:");
        System.out.println("UPDATE users SET password_hash = '" + hash + "' WHERE username = 'admin';");
        System.out.println("\nOr update all users:");
        System.out.println("UPDATE users SET password_hash = '" + hash + "';");
    }
}

