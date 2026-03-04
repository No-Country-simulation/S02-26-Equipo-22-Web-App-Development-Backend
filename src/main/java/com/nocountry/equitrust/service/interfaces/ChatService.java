package com.nocountry.equitrust.service.interfaces;

import com.nocountry.equitrust.model.chat.ChatEvent;
import com.nocountry.equitrust.model.chat.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface ChatService {

    ChatMessage sendMessage(ChatMessage chatMessage);

    ChatMessage send(ChatMessage chatMessage, Long horseId, Long buyerId, String username);

    ChatEvent joinChat(Long horseId, Long buyerId, String username);

    ChatEvent leaveChat(Long horseId, Long buyerId, String username);

    Page<ChatMessage> getConversationHistory(Long horseId, Long buyerId, String username, Pageable pageable);

    void deleteMessage(String messageId, Long userId);
}

