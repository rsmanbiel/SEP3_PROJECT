package com.sep3.warehouse.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for updating an existing user.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {
    
    @Email(message = "Invalid email format")
    private String email;
    
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
    
    private String firstName;
    private String lastName;
    private String phone;
    private String address;
    private String city;
    private String postalCode;
    private String country;
    private Long roleId;
    private Boolean isActive;
}

