package com.nocountry.equitrust.config;

import com.nocountry.equitrust.exception.UnauthorizedException;
import com.nocountry.equitrust.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
@Slf4j
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtService jwtService;

    @Value("${websocket.allowed.origins:http://localhost:3000}")
    private String allowedOrigins;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-horse")
                .setAllowedOriginPatterns(allowedOrigins.split(","))
                .withSockJS();

        log.info("WebSocket STOMP endpoint registered at /ws-horse");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue");
        config.setApplicationDestinationPrefixes("/app");
        config.setUserDestinationPrefix("/user");

        log.info("Message broker configured with topic and queue prefixes");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    validateJwtToken(accessor);
                }

                if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
                    String username = (String) accessor.getSessionAttributes().get("username");
                    if (username == null || username.isBlank()) {
                        throw new UnauthorizedException("Must be authenticated to subscribe");
                    }
                }

                return message;
            }

            private void validateJwtToken(StompHeaderAccessor accessor) {
                String authHeader = accessor.getFirstNativeHeader("Authorization");

                log.debug("Auth header received: {}", authHeader);

                if (authHeader == null || authHeader.isBlank()) {
                    throw new UnauthorizedException("Authorization header is required");
                }

                String token = authHeader.startsWith("Bearer ")
                    ? authHeader.substring(7)
                    : authHeader;

                try {
                    // Extract username (email) from token and store in session
                    String username = jwtService.extractUsername(token);
                    accessor.getSessionAttributes().put("username", username);

                    log.debug("WebSocket connection authenticated for user: {}", username);
                } catch (Exception e) {
                    log.error("Invalid JWT token in WebSocket connection: {}", e.getMessage());
                    throw new UnauthorizedException("Invalid or expired JWT token");
                }
            }
        });

        log.info("WebSocket client inbound channel configured with JWT validation");
    }
}