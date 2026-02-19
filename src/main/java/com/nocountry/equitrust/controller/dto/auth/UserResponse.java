package com.nocountry.equitrust.controller.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nocountry.equitrust.model.user.Role;
import com.nocountry.equitrust.model.user.User;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record UserResponse(
        Long id,
        String dni,
        String name,

        @JsonProperty("last_name")
        String lastName,

        String email,
        String number,
        String address,
        Role rol,

        @JsonProperty("created_at")
        LocalDateTime createdAt
) {
    public static UserResponse fromUser(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .dni(user.getDni())
                .name(user.getName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .number(user.getNumber())
                .address(user.getAddress())
                .rol(user.getRol())
                .createdAt(user.getCreatedAt())
                .build();
    }
}