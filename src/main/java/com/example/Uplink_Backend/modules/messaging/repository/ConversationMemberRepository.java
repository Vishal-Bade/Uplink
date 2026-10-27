package com.example.Uplink_Backend.modules.messaging.repository;

import com.example.Uplink_Backend.modules.messaging.entity.ConversationMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConversationMemberRepository extends JpaRepository<ConversationMember, UUID> {
    List<ConversationMember> findByConversationId(UUID conversationId);
    Optional<ConversationMember> findByConversationIdAndUserId(UUID conversationId, UUID userId);
    boolean existsByConversationIdAndUserId(UUID conversationId, UUID userId);
}
