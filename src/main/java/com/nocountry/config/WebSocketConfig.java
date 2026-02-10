package com.nocountry.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Value("${websocket.allowed.origins:PORT}")
    private String allowedOrigins;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Este es el punto de entrada para el Front
        registry.addEndpoint("/ws-horse")
                .setAllowedOriginPatterns("*") // Permitir todos los orígenes (desarrollo)
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic"); // Para mensajes que el front recibe
        config.setApplicationDestinationPrefixes("/app"); // Para mensajes que el front envía
    }
}