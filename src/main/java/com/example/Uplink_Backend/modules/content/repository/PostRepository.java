package com.example.Uplink_Backend.modules.content.repository;

import com.example.Uplink_Backend.modules.content.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PostRepository extends JpaRepository<Post, UUID> {
    Page<Post> findAllByOrderByCreatedAtDesc(Pageable pageable);
    Page<Post> findByTypeOrderByCreatedAtDesc(String type, Pageable pageable);
    Page<Post> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);
}
