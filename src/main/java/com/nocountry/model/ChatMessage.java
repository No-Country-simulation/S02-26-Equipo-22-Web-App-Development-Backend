package com.nocountry.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

// Estas anotaciones de Lombok ahorran escribir Getters y Setters
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessage {

    @NotBlank(message = "El remitente no puede estar vacío")
    @Size(max = 100, message = "El nombre del remitente no puede exceder 100 caracteres")
    private String sender; // Nombre o ID del usuario que envía

    @NotBlank(message = "El contenido no puede estar vacío")
    @Size(max = 1000, message = "El mensaje no puede exceder 1000 caracteres")
    private String content; // El texto del mensaje

    @NotBlank(message = "El ID del caballo no puede estar vacío")
    private String horseId; // ID del caballo (para agrupar el chat por anuncio)

    @NotNull(message = "El tipo de mensaje no puede ser nulo")
    private MessageType type; // Tipo de mensaje (CHAT, JOIN, LEAVE)

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now(); // Timestamp del mensaje

    public enum MessageType {
        CHAT, JOIN, LEAVE
    }
}