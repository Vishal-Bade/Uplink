package com.example.Uplink_Backend.modules.messaging.service;

import com.example.Uplink_Backend.common.exception.BadRequestException;
import com.example.Uplink_Backend.common.exception.ResourceNotFoundException;
import com.example.Uplink_Backend.modules.auth.entity.User;
import com.example.Uplink_Backend.modules.auth.repository.UserRepository;
import com.example.Uplink_Backend.modules.messaging.dto.CreateConversationRequest;
import com.example.Uplink_Backend.modules.messaging.dto.SendMessageRequest;
import com.example.Uplink_Backend.modules.messaging.entity.Conversation;
import com.example.Uplink_Backend.modules.messaging.entity.ConversationMember;
import com.example.Uplink_Backend.modules.messaging.entity.Message;
import com.example.Uplink_Backend.modules.messaging.repository.ConversationMemberRepository;
import com.example.Uplink_Backend.modules.messaging.repository.ConversationRepository;
import com.example.Uplink_Backend.modules.messaging.repository.MessageRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class MessagingService {

    private final ConversationRepository conversationRepository;
    private final ConversationMemberRepository memberRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public MessagingService(ConversationRepository conversationRepository,
                            ConversationMemberRepository memberRepository,
                            MessageRepository messageRepository,
                            UserRepository userRepository) {
        this.conversationRepository = conversationRepository;
        this.memberRepository = memberRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Conversation createConversation(UUID creatorId, CreateConversationRequest request) {
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", creatorId));

        Conversation conversation = new Conversation(request.getType() != null ? request.getType() : "DIRECT");
        Conversation savedConversation = conversationRepository.save(conversation);

        // Add creator
        ConversationMember creatorMember = new ConversationMember(savedConversation, creator, "ADMIN");
        memberRepository.save(creatorMember);
        savedConversation.getMembers().add(creatorMember);

        Set<UUID> addedIds = new HashSet<>();
        addedIds.add(creatorId);

        for (UUID participantId : request.getParticipantIds()) {
            if (!addedIds.contains(participantId)) {
                User participant = userRepository.findById(participantId).orElse(null);
                if (participant != null) {
                    ConversationMember member = new ConversationMember(savedConversation, participant, "MEMBER");
                    memberRepository.save(member);
                    savedConversation.getMembers().add(member);
                    addedIds.add(participantId);
                }
            }
        }

        return savedConversation;
    }

    public List<Conversation> getUserConversations(UUID userId) {
        return conversationRepository.findConversationsByUserId(userId);
    }

    @Transactional
    public Message sendMessage(UUID senderId, SendMessageRequest request) {
        Conversation conversation = conversationRepository.findById(request.getConversationId())
                .orElseThrow(() -> new ResourceNotFoundException("Conversation", "id", request.getConversationId()));

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", senderId));

        // Ensure sender is a conversation member
        if (!memberRepository.existsByConversationIdAndUserId(conversation.getId(), senderId)) {
            throw new BadRequestException("You are not a member of this conversation");
        }

        Message message = new Message(conversation, sender, request.getContent(), request.getMessageType());
        Message savedMessage = messageRepository.save(message);

        conversation.setUpdatedAt(Instant.now());
        conversationRepository.save(conversation);

        return savedMessage;
    }

    public Page<Message> getConversationMessages(UUID conversationId, UUID userId, Pageable pageable) {
        if (!memberRepository.existsByConversationIdAndUserId(conversationId, userId)) {
            throw new BadRequestException("You are not a member of this conversation");
        }
        return messageRepository.findByConversationIdOrderByCreatedAtAsc(conversationId, pageable);
    }

    @Transactional
    public void markConversationAsRead(UUID conversationId, UUID userId) {
        messageRepository.markAllAsRead(conversationId, userId, Instant.now());
    }
}
