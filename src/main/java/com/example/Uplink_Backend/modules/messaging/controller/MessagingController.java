package com.example.Uplink_Backend.modules.messaging.controller;

import com.example.Uplink_Backend.common.dto.ApiResponse;
import com.example.Uplink_Backend.common.security.UserPrincipal;
import com.example.Uplink_Backend.modules.messaging.dto.CreateConversationRequest;
import com.example.Uplink_Backend.modules.messaging.dto.SendMessageRequest;
import com.example.Uplink_Backend.modules.messaging.entity.Conversation;
import com.example.Uplink_Backend.modules.messaging.entity.Message;
import com.example.Uplink_Backend.modules.messaging.service.MessagingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/messages")
@Tag(name = "8. Messaging Module", description = "Endpoints for Direct & Group Conversations, Messaging, Links, and Read Receipts")
public class MessagingController {

    private final MessagingService messagingService;

    public MessagingController(MessagingService messagingService) {
        this.messagingService = messagingService;
    }

    @PostMapping("/conversations")
    @Operation(summary = "Start a new conversation (Direct or Group with candidate, recruiter, or peer)")
    public ResponseEntity<ApiResponse<Conversation>> createConversation(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody CreateConversationRequest request) {
        Conversation conversation = messagingService.createConversation(userPrincipal.getId(), request);
        return ResponseEntity.ok(ApiResponse.created("Conversation created", conversation));
    }

    @GetMapping("/conversations")
    @Operation(summary = "Get all conversations for current user")
    public ResponseEntity<ApiResponse<List<Conversation>>> getMyConversations(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<Conversation> conversations = messagingService.getUserConversations(userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.ok("User conversations retrieved", conversations));
    }

    @PostMapping("/send")
    @Operation(summary = "Send a text message, attachment link, or system notification in a conversation")
    public ResponseEntity<ApiResponse<Message>> sendMessage(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody SendMessageRequest request) {
        Message message = messagingService.sendMessage(userPrincipal.getId(), request);
        return ResponseEntity.ok(ApiResponse.created("Message sent", message));
    }

    @GetMapping("/conversation/{conversationId}")
    @Operation(summary = "Get conversation message thread (paginated)")
    public ResponseEntity<ApiResponse<Page<Message>>> getMessages(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable UUID conversationId,
            Pageable pageable) {
        Page<Message> messages = messagingService.getConversationMessages(conversationId, userPrincipal.getId(), pageable);
        return ResponseEntity.ok(ApiResponse.ok("Messages retrieved", messages));
    }

    @PostMapping("/conversation/{conversationId}/read")
    @Operation(summary = "Mark all unread messages in conversation as read (Read receipt)")
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable UUID conversationId) {
        messagingService.markConversationAsRead(conversationId, userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.ok("Conversation marked as read", null));
    }
}
