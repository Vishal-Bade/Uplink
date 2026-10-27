package com.example.Uplink_Backend.modules.auth.repository;

import com.example.Uplink_Backend.modules.auth.entity.UserVerification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserVerificationRepository extends JpaRepository<UserVerification, UUID> {
    List<UserVerification> findByUserId(UUID userId);
    Page<UserVerification> findByStatus(String status, Pageable pageable);
    long countByStatus(String status);
}
