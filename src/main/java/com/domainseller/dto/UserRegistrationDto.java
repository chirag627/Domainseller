package com.domainseller.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationDto {
    @NotBlank(message = "Full name is required")
    private String fullName;
    @NotBlank(message = "Username is required")
    private String username;
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
    @NotBlank(message = "Please confirm your password")
    private String confirmPassword;
}
