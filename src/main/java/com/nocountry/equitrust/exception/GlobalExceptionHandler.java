package com.nocountry.equitrust.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Maneja las excepciones globalmente para toda la API.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(VeterinaryRecordNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleRecordNotFound(VeterinaryRecordNotFoundException ex) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                Map.of(
                        "timestamp", LocalDateTime.now(),
                        "status", HttpStatus.NOT_FOUND.value(),
                        "error", "Veterinary Record Not Found",
                        "message", ex.getMessage()
                )
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                Map.of(
                        "timestamp", LocalDateTime.now(),
                        "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "error", "Internal Server Error",
                        "message", ex.getMessage()
                )
        );
    }
}
