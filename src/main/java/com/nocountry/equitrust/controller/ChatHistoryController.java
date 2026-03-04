package com.nocountry.equitrust.controller;

import com.nocountry.equitrust.model.chat.ChatMessage;
import com.nocountry.equitrust.model.user.User;
import com.nocountry.equitrust.service.interfaces.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/horses/{horseId}/chat")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Chat History", description = "REST API for chat history")
public class ChatHistoryController {

    private final ChatService chatService;

    @GetMapping("/history/{buyerId}")
    @Operation(summary = "Get conversation history between buyer and horse owner")
    public ResponseEntity<Page<ChatMessage>> getHistory(
            @PathVariable Long horseId,
            @PathVariable Long buyerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal User currentUser) {

        return ResponseEntity.ok(chatService.getConversationHistory(
                horseId,
                buyerId,
                currentUser.getUsername(),
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))
        ));
    }
}
