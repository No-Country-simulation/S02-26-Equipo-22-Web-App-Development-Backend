package com.nocountry.equitrust.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nocountry.equitrust.model.Role;
import com.nocountry.equitrust.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Long id;
    private String dni;
    private String name;

    @JsonProperty("last_name")
    private String lastName;

    private String email;
    private String number;
    private String address;
    private Role rol;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

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