package com.example.Uplink_Backend.modules.application.repository;

import com.example.Uplink_Backend.modules.application.entity.Application;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, UUID> {
    Page<Application> findByUserIdOrderByAppliedAtDesc(UUID userId, Pageable pageable);
    Page<Application> findByOpportunityIdOrderByAppliedAtDesc(UUID opportunityId, Pageable pageable);
    Optional<Application> findByOpportunityIdAndUserId(UUID opportunityId, UUID userId);
    boolean existsByOpportunityIdAndUserId(UUID opportunityId, UUID userId);
    long countByStatus(String status);
}
