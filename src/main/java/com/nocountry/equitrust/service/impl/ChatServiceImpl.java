package com.nocountry.equitrust.service.impl;

import com.nocountry.equitrust.exception.ResourceNotFoundException;
import com.nocountry.equitrust.exception.UnauthorizedException;
import com.nocountry.equitrust.model.chat.ChatMessage;
import com.nocountry.equitrust.model.chat.ChatEvent;
import com.nocountry.equitrust.model.horse.HorsePost;
import com.nocountry.equitrust.model.user.User;
import com.nocountry.equitrust.repository.ChatMessageRepository;
import com.nocountry.equitrust.repository.UserRepository;
import com.nocountry.equitrust.repository.horse.HorseRepository;
import com.nocountry.equitrust.service.interfaces.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class ChatServiceImpl implements ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final HorseRepository horseRepository;

    @Override
    public ChatMessage send(ChatMessage chatMessage, Long horseId, Long buyerId, String username) {
        // Validate buyerId parameter first (null/invalid) -> IllegalArgumentException
        validateBuyerIdParam(buyerId);

        // Resolve authenticated user next so UnauthorizedException is thrown if username not found
        User sender = resolveUser(username);

        // Now ensure buyer exists in DB (throws ResourceNotFoundException if absent)
        ensureBuyerExists(buyerId);

        HorsePost horse = resolveHorse(horseId);

        // Validate that the horse is not deleted or sold
        if (horse.isDeleted()) {
            throw new IllegalArgumentException("Cannot send messages about deleted horses");
        }
        if (horse.isSold()) {
            throw new IllegalArgumentException("Cannot send messages about sold horses");
        }

        if (horse.isOwnedBy(sender)) {
            // Owner responding to buyer
            return buildAndSend(chatMessage, horseId, sender.getId(), buyerId);
        } else {
            // Buyer writing to owner - validate sender is the buyer
            if (!sender.getId().equals(buyerId)) {
                throw new UnauthorizedException("Buyer ID does not match authenticated user");
            }
            return buildAndSend(chatMessage, horseId, sender.getId(), horse.getOwner().getId());
        }
    }

    private ChatMessage buildAndSend(ChatMessage chatMessage, Long horseId, Long senderId, Long receiverId) {
        chatMessage.setSenderId(senderId);
        chatMessage.setReceiverId(receiverId);
        chatMessage.setHorseId(horseId);
        return sendMessage(chatMessage);
    }

    @Override
    public ChatMessage sendMessage(ChatMessage chatMessage) {
        if (chatMessage == null)
            throw new IllegalArgumentException("Chat message cannot be null");

        chatMessage.validateForSending();

        ChatMessage saved = chatMessageRepository.save(chatMessage);
        log.debug("Message saved: {} from user {} to user {}",
                saved.getId(), saved.getSenderId(), saved.getReceiverId());

        return saved;
    }

    @Override
    public ChatEvent joinChat(Long horseId, Long buyerId, String username) {
        // Validate buyerId parameter first
        validateBuyerIdParam(buyerId);

        // Resolve authenticated user first
        User user = resolveUser(username);

        // Ensure buyer exists after authentication
        ensureBuyerExists(buyerId);

        HorsePost horse = resolveHorse(horseId);

        // Validate user is part of this conversation (either owner or buyer)
        boolean isOwner = horse.isOwnedBy(user);
        boolean isBuyer = user.getId().equals(buyerId);

        if (!isOwner && !isBuyer) {
            throw new UnauthorizedException("You don't have access to this conversation");
        }

        log.info("User {} joined chat for horse {}", user.getId(), horseId);
        return ChatEvent.join(user.getId(), horseId);
    }

    @Override
    public ChatEvent leaveChat(Long horseId, Long buyerId, String username) {
        // Validate buyerId parameter first
        validateBuyerIdParam(buyerId);

        // Resolve authenticated user first
        User user = resolveUser(username);

        // Ensure buyer exists after authentication
        ensureBuyerExists(buyerId);

        HorsePost horse = resolveHorse(horseId);

        // Validate user is part of this conversation (either owner or buyer)
        boolean isOwner = horse.isOwnedBy(user);
        boolean isBuyer = user.getId().equals(buyerId);

        if (!isOwner && !isBuyer) {
            throw new UnauthorizedException("You don't have access to this conversation");
        }

        log.info("User {} left chat for horse {}", user.getId(), horseId);
        return ChatEvent.leave(user.getId(), horseId);
    }

    @Override
    public Page<ChatMessage> getConversationHistory(Long horseId, Long buyerId, String username, Pageable pageable) {
        // Validate buyerId parameter first
        validateBuyerIdParam(buyerId);

        // Resolve authenticated user first
        User user = resolveUser(username);

        // Ensure buyer exists after authentication
        ensureBuyerExists(buyerId);

        HorsePost horse = resolveHorse(horseId);

        Long ownerId = horse.getOwner().getId();

        boolean isOwner = horse.isOwnedBy(user);
        boolean isBuyer = user.getId().equals(buyerId);

        if (!isOwner && !isBuyer) {
            throw new UnauthorizedException("You don't have access to this conversation");
        }

        return chatMessageRepository.findConversation(horseId, buyerId, ownerId, pageable);
    }

    @Override
    public void deleteMessage(String messageId, Long userId) {
        if (messageId == null || messageId.isBlank())
            throw new IllegalArgumentException("Invalid message ID");
        if (userId == null || userId <= 0)
            throw new IllegalArgumentException("Invalid user ID");

        ChatMessage message = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with ID: " + messageId));

        message.validateDeletionPermission(userId);
        chatMessageRepository.deleteById(messageId);

        log.info("Message {} deleted by user {}", messageId, userId);
    }

    private User resolveUser(String username) {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new UnauthorizedException("User not found"));
    }

    private HorsePost resolveHorse(Long horseId) {
        return horseRepository.findById(horseId)
                .orElseThrow(() -> new ResourceNotFoundException("Horse not found with id: " + horseId));
    }

    private void validateBuyerIdParam(Long buyerId) {
        if (buyerId == null || buyerId <= 0) {
            throw new IllegalArgumentException("Invalid buyer ID");
        }
    }

    private void ensureBuyerExists(Long buyerId) {
        if (!userRepository.existsById(buyerId)) {
            throw new ResourceNotFoundException("Buyer not found with id: " + buyerId);
        }
    }
}

