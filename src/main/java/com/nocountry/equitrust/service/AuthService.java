package com.nocountry.equitrust.service;

import com.nocountry.equitrust.dto.auth.AuthResponse;
import com.nocountry.equitrust.dto.auth.LoginRequest;
import com.nocountry.equitrust.dto.auth.RegisterRequest;
import com.nocountry.equitrust.dto.auth.UserResponse;
import com.nocountry.equitrust.exception.DuplicateResourceException;
import com.nocountry.equitrust.model.Role;
import com.nocountry.equitrust.model.User;
import com.nocountry.equitrust.repository.UserRepository;
import com.nocountry.equitrust.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already registered");
        }

        // Check if DNI already exists
        if (userRepository.existsByDni(request.getDni())) {
            throw new DuplicateResourceException("DNI already registered");
        }

        // Create new user
        User user = User.builder()
                .dni(request.getDni())
                .name(request.getName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .number(request.getNumber())
                .address(request.getAddress())
                .rol(request.getRol() != null ? request.getRol() : Role.BUYER)
                .enabled(true)
                .build();

        userRepository.save(user);

        // Generate JWT token
        String jwtToken = jwtService.generateToken(user);
        long expiresIn = jwtService.getExpirationTime();

        return AuthResponse.of(
                jwtToken,
                expiresIn,
                UserResponse.fromUser(user)
        );
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        // Authenticate user
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // Find user
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Generate JWT token
        String jwtToken = jwtService.generateToken(user);
        long expiresIn = jwtService.getExpirationTime();

        return AuthResponse.of(
                jwtToken,
                expiresIn,
                UserResponse.fromUser(user)
        );
    }
}