package com.nocountry.equitrust.controller.dto.auth;

import com.nocountry.equitrust.model.user.Role;
import jakarta.validation.constraints.*;
import lombok.Builder;

@Builder
public record RegisterRequest(
        @NotBlank(message = "DNI is required")
        @Size(min = 6, max = 20, message = "DNI must be between 6 and 20 characters")
        String dni,

        @NotBlank(message = "Name is required")
        @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
        String name,

        @NotBlank(message = "Last name is required")
        @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
        String lastName,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must be at least 8 characters long")
        @Pattern(
                regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).*$",
                message = "Password must contain at least one digit, one lowercase, and one uppercase letter"
        )
        String password,

        @Size(max = 20, message = "Phone number must not exceed 20 characters")
        String number,

        @Size(max = 500, message = "Address must not exceed 500 characters")
        String address,

        Role rol
) {
}