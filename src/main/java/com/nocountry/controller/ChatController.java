package com.nocountry.controller;

import com.nocountry.model.ChatMessage;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;

import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Controller
@Slf4j
public class ChatController {

    // Cuando el Front envíe algo a /app/chat.sendMessage, llegará aquí
    @MessageMapping("/chat.sendMessage")
    // Y esto lo retransmitirá a todos los suscritos a /topic/public
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Valid @Payload ChatMessage chatMessage) {
        log.info("Mensaje recibido de {}: {}", chatMessage.getSender(), chatMessage.getContent());

        // Asegurar que el timestamp sea el del servidor
        chatMessage.setTimestamp(LocalDateTime.now());

        return chatMessage;
    }

    // Manejo de usuarios uniéndose al chat
    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Valid @Payload ChatMessage chatMessage) {
        log.info("Usuario {} se unió al chat del caballo {}", chatMessage.getSender(), chatMessage.getHorseId());

        chatMessage.setType(ChatMessage.MessageType.JOIN);
        chatMessage.setTimestamp(LocalDateTime.now());

        return chatMessage;
    }
}