package com.nocountry.equitrust.service;

import com.nocountry.equitrust.dto.auth.ChangePasswordRequest;
import com.nocountry.equitrust.dto.auth.UpdateProfileRequest;
import com.nocountry.equitrust.dto.auth.UserResponse;
import com.nocountry.equitrust.exception.InvalidPasswordException;
import com.nocountry.equitrust.exception.ResourceNotFoundException;
import com.nocountry.equitrust.model.User;
import com.nocountry.equitrust.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    @Lazy
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    // OBTENER USUARIOS

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserResponse::fromUser)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return UserResponse.fromUser(user);
    }

    @Transactional(readOnly = true)
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return UserResponse.fromUser(user);
    }

    @Transactional(readOnly = true)
    public UserResponse getUserByDni(String dni) {
        User user = userRepository.findByDni(dni)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with DNI: " + dni));
        return UserResponse.fromUser(user);
    }

    // ACTUALIZAR PERFIL (Mejorado con Optional)

    @Transactional
    public UserResponse updateProfile(Long userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Usar Optional para actualizar solo campos no nulos y no vacíos
        Optional.ofNullable(request.name())
                .filter(name -> !name.isEmpty())
                .ifPresent(user::setName);

        Optional.ofNullable(request.lastName())
                .filter(lastName -> !lastName.isEmpty())
                .ifPresent(user::setLastName);

        Optional.ofNullable(request.number())
                .ifPresent(user::setNumber);

        Optional.ofNullable(request.address())
                .ifPresent(user::setAddress);

        User updatedUser = userRepository.save(user);
        return UserResponse.fromUser(updatedUser);
    }

    // CAMBIAR CONTRASEÑA

    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {
        // Validar que las nuevas contraseñas coincidan
        if (!request.newPassword().equals(request.confirmPassword())) {
            throw new InvalidPasswordException("New password and confirmation do not match");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Validar contraseña actual
        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new InvalidPasswordException("Current password is incorrect");
        }

        // Validar que la nueva contraseña sea diferente
        if (passwordEncoder.matches(request.newPassword(), user.getPassword())) {
            throw new InvalidPasswordException("New password must be different from current password");
        }

        // Actualizar contraseña
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

    // ELIMINAR USUARIO (SOFT DELETE)

    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        // Soft delete - Hibernate maneja esto automaticamente por @SQLDelete
        userRepository.delete(user);
    }

    // ACTIVAR/DESACTIVAR USUARIO

    @Transactional
    public UserResponse toggleUserStatus(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        user.setEnabled(!user.isEnabled());
        User updatedUser = userRepository.save(user);
        return UserResponse.fromUser(updatedUser);
    }
}