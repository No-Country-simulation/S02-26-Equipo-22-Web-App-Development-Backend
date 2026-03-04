package com.nocountry.equitrust.model.chat;

import com.nocountry.equitrust.exception.UnauthorizedException;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "chat_messages")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessage {

    @Id
    private String id;

    @NotNull
    private Long senderId;

    @NotNull
    private Long receiverId;

    @NotNull
    private Long horseId;

    @Size(max = 1000)
    private String content;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    public void validateForSending() {
        if (senderId == null || senderId <= 0)
            throw new IllegalArgumentException("Invalid sender ID");
        if (receiverId == null || receiverId <= 0)
            throw new IllegalArgumentException("Invalid receiver ID");
        if (horseId == null || horseId <= 0)
            throw new IllegalArgumentException("Invalid horse ID");
        if (content == null || content.isBlank())
            throw new IllegalArgumentException("Content cannot be empty");
        if (content.length() > 1000)
            throw new IllegalArgumentException("Message cannot exceed 1000 characters");
    }

    public void validateDeletionPermission(Long requestingUserId) {
        if (!this.senderId.equals(requestingUserId))
            throw new UnauthorizedException("You can only delete your own messages");
    }
}