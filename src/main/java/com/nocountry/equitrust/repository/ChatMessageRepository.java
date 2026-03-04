package com.nocountry.equitrust.repository;

import com.nocountry.equitrust.model.chat.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {

    @Query("{ 'horseId': ?0, $or: [ { 'senderId': ?1, 'receiverId': ?2 }, { 'senderId': ?2, 'receiverId': ?1 } ] }")
    Page<ChatMessage> findConversation(Long horseId, Long userId1, Long userId2, Pageable pageable);
}