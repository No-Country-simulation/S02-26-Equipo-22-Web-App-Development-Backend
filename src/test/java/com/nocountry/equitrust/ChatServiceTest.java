package com.nocountry.equitrust;

import com.nocountry.equitrust.exception.ResourceNotFoundException;
import com.nocountry.equitrust.exception.UnauthorizedException;
import com.nocountry.equitrust.model.chat.ChatMessage;
import com.nocountry.equitrust.repository.ChatMessageRepository;
import com.nocountry.equitrust.service.interfaces.ChatService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {"JWT_SECRET=test-secret"})
@DisplayName("ChatService Integration Tests")
class ChatServiceTest {

    @Autowired
    private ChatService chatService;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    private static final Long SENDER_ID = 1L;
    private static final Long RECEIVER_ID = 2L;
    private static final Long OTHER_USER_ID = 3L;
    private static final Long HORSE_ID = 1L;
    private static final String MESSAGE_CONTENT = "This is a test message";

    private ChatMessage testMessage;

    @BeforeEach
    void setUp() {
        chatMessageRepository.deleteAll();
        testMessage = ChatMessage.builder()
                .senderId(SENDER_ID)
                .receiverId(RECEIVER_ID)
                .horseId(HORSE_ID)
                .content(MESSAGE_CONTENT)
                .build();
    }

    @Nested
    @DisplayName("Send Message Tests")
    class SendMessageTests {

        @Test
        @DisplayName("Should successfully send a chat message")
        void testSendMessageSuccess() {
            ChatMessage saved = chatService.sendMessage(testMessage);

            assertNotNull(saved.getId());
            assertEquals(SENDER_ID, saved.getSenderId());
            assertEquals(RECEIVER_ID, saved.getReceiverId());
            assertEquals(HORSE_ID, saved.getHorseId());
            assertEquals(MESSAGE_CONTENT, saved.getContent());
            assertNotNull(saved.getCreatedAt());
            assertNotNull(saved.getUpdatedAt());
        }

        @Test
        @DisplayName("Should fail when message is null")
        void testSendMessageNull() {
            assertThrows(IllegalArgumentException.class, () -> chatService.sendMessage(null));
        }

        @Test
        @DisplayName("Should fail when senderId is null")
        void testSendMessageNullSenderId() {
            testMessage.setSenderId(null);
            assertThrows(IllegalArgumentException.class, () -> chatService.sendMessage(testMessage));
        }

        @Test
        @DisplayName("Should fail when senderId is invalid")
        void testSendMessageInvalidSenderId() {
            testMessage.setSenderId(0L);
            assertThrows(IllegalArgumentException.class, () -> chatService.sendMessage(testMessage));

            testMessage.setSenderId(-1L);
            assertThrows(IllegalArgumentException.class, () -> chatService.sendMessage(testMessage));
        }

        @Test
        @DisplayName("Should fail when receiverId is null")
        void testSendMessageNullReceiverId() {
            testMessage.setReceiverId(null);
            assertThrows(IllegalArgumentException.class, () -> chatService.sendMessage(testMessage));
        }

        @Test
        @DisplayName("Should fail when receiverId is invalid")
        void testSendMessageInvalidReceiverId() {
            testMessage.setReceiverId(0L);
            assertThrows(IllegalArgumentException.class, () -> chatService.sendMessage(testMessage));

            testMessage.setReceiverId(-1L);
            assertThrows(IllegalArgumentException.class, () -> chatService.sendMessage(testMessage));
        }

        @Test
        @DisplayName("Should fail when horseId is null")
        void testSendMessageNullHorseId() {
            testMessage.setHorseId(null);
            assertThrows(IllegalArgumentException.class, () -> chatService.sendMessage(testMessage));
        }

        @Test
        @DisplayName("Should fail when horseId is invalid")
        void testSendMessageInvalidHorseId() {
            testMessage.setHorseId(0L);
            assertThrows(IllegalArgumentException.class, () -> chatService.sendMessage(testMessage));

            testMessage.setHorseId(-1L);
            assertThrows(IllegalArgumentException.class, () -> chatService.sendMessage(testMessage));
        }

        @Test
        @DisplayName("Should accept message with exactly 1000 characters")
        void testMessageAtExactLimit() {
            testMessage.setContent("a".repeat(1000));
            assertDoesNotThrow(() -> chatService.sendMessage(testMessage));
        }

        @Test
        @DisplayName("Should reject message exceeding 1000 characters")
        void testMessageExceedsLimit() {
            testMessage.setContent("a".repeat(1001));
            assertThrows(IllegalArgumentException.class, () -> chatService.sendMessage(testMessage));
        }

        @Test
        @DisplayName("Should fail when content is blank")
        void testSendMessageBlankContent() {
            testMessage.setContent("   ");
            assertThrows(IllegalArgumentException.class, () -> chatService.sendMessage(testMessage));
        }
    }

    @Nested
    @DisplayName("Conversation Tests")
    class ConversationTests {

        @Test
        @DisplayName("Should retrieve conversation between two users")
        void testGetConversation() {
            // Mensaje de sender a receiver
            chatService.sendMessage(testMessage);

            // Respuesta de receiver a sender
            chatService.sendMessage(ChatMessage.builder()
                    .senderId(RECEIVER_ID)
                    .receiverId(SENDER_ID)
                    .horseId(HORSE_ID)
                    .content("Reply message")
                    .build());

            var page = chatMessageRepository.findConversation(
                    HORSE_ID, SENDER_ID, RECEIVER_ID,
                    PageRequest.of(0, 10));

            assertNotNull(page);
            assertEquals(2, page.getTotalElements());
            assertTrue(page.getContent().stream().allMatch(m ->
                    m.getHorseId().equals(HORSE_ID) &&
                            (m.getSenderId().equals(SENDER_ID) || m.getSenderId().equals(RECEIVER_ID))
            ));
        }

        @Test
        @DisplayName("Should not include messages from other conversations")
        void testConversationIsolation() {
            // Mensaje entre SENDER y RECEIVER
            chatService.sendMessage(testMessage);

            // Mensaje de OTHER_USER — no debería aparecer
            chatService.sendMessage(ChatMessage.builder()
                    .senderId(OTHER_USER_ID)
                    .receiverId(RECEIVER_ID)
                    .horseId(HORSE_ID)
                    .content("Other conversation")
                    .build());

            var page = chatMessageRepository.findConversation(
                    HORSE_ID, SENDER_ID, RECEIVER_ID,
                    PageRequest.of(0, 10));

            assertEquals(1, page.getTotalElements());
            assertEquals(MESSAGE_CONTENT, page.getContent().get(0).getContent());
        }

        @Test
        @DisplayName("Should paginate conversation correctly")
        void testConversationPagination() {
            for (int i = 0; i < 5; i++) {
                chatService.sendMessage(ChatMessage.builder()
                        .senderId(SENDER_ID)
                        .receiverId(RECEIVER_ID)
                        .horseId(HORSE_ID)
                        .content("Message " + i)
                        .build());
            }

            var page = chatMessageRepository.findConversation(
                    HORSE_ID, SENDER_ID, RECEIVER_ID,
                    PageRequest.of(0, 3));

            assertEquals(3, page.getContent().size());
            assertEquals(5, page.getTotalElements());
            assertEquals(2, page.getTotalPages());
        }

        @Test
        @DisplayName("Should return conversation ordered by date descending")
        void testConversationOrder() throws InterruptedException {
            chatService.sendMessage(ChatMessage.builder()
                    .senderId(SENDER_ID).receiverId(RECEIVER_ID)
                    .horseId(HORSE_ID).content("First").build());

            Thread.sleep(100);

            chatService.sendMessage(ChatMessage.builder()
                    .senderId(RECEIVER_ID).receiverId(SENDER_ID)
                    .horseId(HORSE_ID).content("Second").build());

            var page = chatMessageRepository.findConversation(
                    HORSE_ID, SENDER_ID, RECEIVER_ID,
                    PageRequest.of(0, 10));

            assertEquals("Second", page.getContent().get(0).getContent());
            assertEquals("First", page.getContent().get(1).getContent());
        }
    }

    @Nested
    @DisplayName("Delete Message Tests")
    class DeleteMessageTests {

        @Test
        @DisplayName("Should successfully delete a message if user is the sender")
        void testDeleteMessageSuccess() {
            ChatMessage saved = chatService.sendMessage(testMessage);
            assertDoesNotThrow(() -> chatService.deleteMessage(saved.getId(), SENDER_ID));
            assertFalse(chatMessageRepository.existsById(saved.getId()));
        }

        @Test
        @DisplayName("Should fail when trying to delete non-existent message")
        void testDeleteNonExistentMessage() {
            assertThrows(ResourceNotFoundException.class,
                    () -> chatService.deleteMessage("non-existent-id", SENDER_ID));
        }

        @Test
        @DisplayName("Should fail when non-sender tries to delete message")
        void testDeleteMessageUnauthorized() {
            ChatMessage saved = chatService.sendMessage(testMessage);
            assertThrows(UnauthorizedException.class,
                    () -> chatService.deleteMessage(saved.getId(), OTHER_USER_ID));
            assertTrue(chatMessageRepository.existsById(saved.getId()));
        }

        @Test
        @DisplayName("Should fail when message ID is null or blank")
        void testDeleteMessageInvalidId() {
            assertThrows(IllegalArgumentException.class,
                    () -> chatService.deleteMessage(null, SENDER_ID));
            assertThrows(IllegalArgumentException.class,
                    () -> chatService.deleteMessage("", SENDER_ID));
            assertThrows(IllegalArgumentException.class,
                    () -> chatService.deleteMessage("   ", SENDER_ID));
        }

        @Test
        @DisplayName("Should fail when user ID is invalid")
        void testDeleteMessageInvalidUserId() {
            assertThrows(IllegalArgumentException.class,
                    () -> chatService.deleteMessage("id", null));
            assertThrows(IllegalArgumentException.class,
                    () -> chatService.deleteMessage("id", 0L));
            assertThrows(IllegalArgumentException.class,
                    () -> chatService.deleteMessage("id", -1L));
        }
    }

    @Nested
    @DisplayName("Buyer Validation Tests")
    class BuyerValidationTests {

        private static final Long INVALID_BUYER_ID = 0L;

        @Test
        @DisplayName("Should fail with IllegalArgumentException when buyerId is null")
        void testSendMessageNullBuyerId() {
            ChatMessage message = ChatMessage.builder()
                    .senderId(SENDER_ID)
                    .receiverId(RECEIVER_ID)
                    .horseId(HORSE_ID)
                    .content(MESSAGE_CONTENT)
                    .build();

            assertThrows(IllegalArgumentException.class,
                    () -> chatService.send(message, HORSE_ID, null, "sender@test.com"));
        }

        @Test
        @DisplayName("Should fail with IllegalArgumentException when buyerId is invalid (0)")
        void testSendMessageInvalidBuyerIdZero() {
            ChatMessage message = ChatMessage.builder()
                    .senderId(SENDER_ID)
                    .receiverId(RECEIVER_ID)
                    .horseId(HORSE_ID)
                    .content(MESSAGE_CONTENT)
                    .build();

            assertThrows(IllegalArgumentException.class,
                    () -> chatService.send(message, HORSE_ID, INVALID_BUYER_ID, "sender@test.com"));
        }

        @Test
        @DisplayName("Should fail with IllegalArgumentException when buyerId is invalid (negative)")
        void testSendMessageInvalidBuyerIdNegative() {
            ChatMessage message = ChatMessage.builder()
                    .senderId(SENDER_ID)
                    .receiverId(RECEIVER_ID)
                    .horseId(HORSE_ID)
                    .content(MESSAGE_CONTENT)
                    .build();

            assertThrows(IllegalArgumentException.class,
                    () -> chatService.send(message, HORSE_ID, -1L, "sender@test.com"));
        }

        @Test
        @DisplayName("Should fail when authenticated user does not exist")
        void testOperationsWithNonExistentUser() {
            // When user tries to authenticate with non-existent email
            assertThrows(UnauthorizedException.class,
                    () -> chatService.joinChat(HORSE_ID, RECEIVER_ID, "nonexistent@test.com"));

            assertThrows(UnauthorizedException.class,
                    () -> chatService.leaveChat(HORSE_ID, RECEIVER_ID, "nonexistent@test.com"));

            assertThrows(UnauthorizedException.class,
                    () -> chatService.getConversationHistory(HORSE_ID, RECEIVER_ID, "nonexistent@test.com", PageRequest.of(0, 10)));
        }
    }
}

