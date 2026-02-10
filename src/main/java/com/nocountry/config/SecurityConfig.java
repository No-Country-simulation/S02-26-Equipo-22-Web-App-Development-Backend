package com.nocountry.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Desactivar CSRF para desarrollo (habilitar en producción)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/ws-horse/**", // WebSocket endpoint
                                "/h2-console/**", // Consola H2
                                "/v3/api-docs/**", // Swagger docs
                                "/swagger-ui/**", // Swagger UI
                                "/swagger-ui.html" // Swagger UI HTML
                        ).permitAll()
                        .anyRequest().authenticated())
                .headers(headers -> headers
                        .frameOptions(frame -> frame.sameOrigin()) // Permitir H2 console en iframe
                );

        return http.build();
    }
}
