package com.nocountry.equitrust.controller;

import com.nocountry.equitrust.exception.ResourceNotFoundException;
import com.nocountry.equitrust.exception.UnauthorizedException;
import com.nocountry.equitrust.model.chat.ChatEvent;
import com.nocountry.equitrust.model.chat.ChatMessage;
import com.nocountry.equitrust.service.interfaces.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @MessageMapping("/chat.send/{horseId}/{buyerId}")
    @SendTo("/topic/horse/{horseId}/conversation/{buyerId}")
    public ChatMessage sendMessage(
            @DestinationVariable Long horseId,
            @DestinationVariable Long buyerId,
            @Payload ChatMessage chatMessage,
            SimpMessageHeaderAccessor headerAccessor) {
        String username = (String) headerAccessor.getSessionAttributes().get("username");
        return chatService.send(chatMessage, horseId, buyerId, username);
    }

    @MessageMapping("/chat.join/{horseId}/{buyerId}")
    @SendTo("/topic/horse/{horseId}/conversation/{buyerId}")
    public ChatEvent join(
            @DestinationVariable Long horseId,
            @DestinationVariable Long buyerId,
            SimpMessageHeaderAccessor headerAccessor) {
        String username = (String) headerAccessor.getSessionAttributes().get("username");
        return chatService.joinChat(horseId, buyerId, username);
    }

    @MessageMapping("/chat.leave/{horseId}/{buyerId}")
    @SendTo("/topic/horse/{horseId}/conversation/{buyerId}")
    public ChatEvent leave(
            @DestinationVariable Long horseId,
            @DestinationVariable Long buyerId,
            SimpMessageHeaderAccessor headerAccessor) {
        String username = (String) headerAccessor.getSessionAttributes().get("username");
        return chatService.leaveChat(horseId, buyerId, username);
    }

    // Manejo de excepciones para errores comunes en WebSocket

    @MessageExceptionHandler(IllegalArgumentException.class)
    @SendToUser("/queue/errors")
    public Map<String, Object> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("Validation error in WebSocket: {}", ex.getMessage());
        return buildErrorResponse("VALIDATION_ERROR", ex.getMessage(), 400);
    }

    @MessageExceptionHandler(UnauthorizedException.class)
    @SendToUser("/queue/errors")
    public Map<String, Object> handleUnauthorized(UnauthorizedException ex) {
        log.warn("Unauthorized access in WebSocket: {}", ex.getMessage());
        return buildErrorResponse("UNAUTHORIZED", ex.getMessage(), 401);
    }

    @MessageExceptionHandler(ResourceNotFoundException.class)
    @SendToUser("/queue/errors")
    public Map<String, Object> handleResourceNotFound(ResourceNotFoundException ex) {
        log.warn("Resource not found in WebSocket: {}", ex.getMessage());
        return buildErrorResponse("NOT_FOUND", ex.getMessage(), 404);
    }

    @MessageExceptionHandler(Exception.class)
    @SendToUser("/queue/errors")
    public Map<String, Object> handleGenericException(Exception ex) {
        log.error("Unexpected WebSocket error: {}", ex.getMessage(), ex);
        return buildErrorResponse("INTERNAL_ERROR", "An unexpected error occurred", 500);
    }

    private Map<String, Object> buildErrorResponse(String errorCode, String message, int statusCode) {
        Map<String, Object> response = new HashMap<>();
        response.put("errorCode", errorCode);
        response.put("message", message);
        response.put("statusCode", statusCode);
        response.put("timestamp", Instant.now());
        return response;
    }
}