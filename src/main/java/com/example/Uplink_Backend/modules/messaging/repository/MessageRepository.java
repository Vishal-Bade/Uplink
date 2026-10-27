package com.example.Uplink_Backend.modules.messaging.repository;

import com.example.Uplink_Backend.modules.messaging.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {
    Page<Message> findByConversationIdOrderByCreatedAtAsc(UUID conversationId, Pageable pageable);

    @Modifying
    @Query("UPDATE Message m SET m.isRead = true, m.readAt = :readAt WHERE m.conversation.id = :conversationId AND m.sender.id <> :userId AND m.isRead = false")
    void markAllAsRead(@Param("conversationId") UUID conversationId, @Param("userId") UUID userId, @Param("readAt") Instant readAt);
}
