package com.nocountry.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Manejador global de excepciones para WebSocket
 */
@ControllerAdvice
@Slf4j
public class WebSocketExceptionHandler {

    @MessageExceptionHandler
    @SendToUser("/queue/errors")
    public Map<String, String> handleException(Exception exception) {
        log.error("Error en WebSocket: {}", exception.getMessage(), exception);

        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", exception.getMessage());
        errorResponse.put("type", exception.getClass().getSimpleName());

        return errorResponse;
    }
}
