package com.nocountry.equitrust.model.chat;

import lombok.*;
import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatEvent {

    private Long userId;
    private Long horseId;
    private EventType type;
    private Instant timestamp;

    public enum EventType {
        JOIN, LEAVE
    }

    public static ChatEvent join(Long userId, Long horseId) {
        return ChatEvent.builder()
                .userId(userId)
                .horseId(horseId)
                .type(EventType.JOIN)
                .timestamp(Instant.now())
                .build();
    }

    public static ChatEvent leave(Long userId, Long horseId) {
        return ChatEvent.builder()
                .userId(userId)
                .horseId(horseId)
                .type(EventType.LEAVE)
                .timestamp(Instant.now())
                .build();
    }
}