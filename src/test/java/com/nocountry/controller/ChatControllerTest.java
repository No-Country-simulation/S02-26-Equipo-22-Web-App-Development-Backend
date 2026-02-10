package com.nocountry.controller;

import com.nocountry.model.ChatMessage;
import com.nocountry.model.ChatMessage.MessageType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests básicos para el ChatController
 * Estos tests verifican que los mensajes se procesen correctamente
 */
@SpringBootTest
class ChatControllerTest {

    @Autowired
    private ChatController chatController;

    @Test
    void testSendMessage_ValidMessage_ReturnsMessageWithTimestamp() {
        // Arrange
        ChatMessage message = ChatMessage.builder()
                .sender("TestUser")
                .content("Hello World")
                .horseId("horse123")
                .type(MessageType.CHAT)
                .build();

        // Act
        ChatMessage result = chatController.sendMessage(message);

        // Assert
        assertNotNull(result);
        assertEquals("TestUser", result.getSender());
        assertEquals("Hello World", result.getContent());
        assertEquals("horse123", result.getHorseId());
        assertEquals(MessageType.CHAT, result.getType());
        assertNotNull(result.getTimestamp(), "Timestamp should be set by server");
    }

    @Test
    void testAddUser_ValidUser_ReturnsJoinMessage() {
        // Arrange
        ChatMessage message = ChatMessage.builder()
                .sender("NewUser")
                .content("joined")
                .horseId("horse123")
                .type(MessageType.JOIN)
                .build();

        // Act
        ChatMessage result = chatController.addUser(message);

        // Assert
        assertNotNull(result);
        assertEquals("NewUser", result.getSender());
        assertEquals(MessageType.JOIN, result.getType());
        assertNotNull(result.getTimestamp());
    }
}
