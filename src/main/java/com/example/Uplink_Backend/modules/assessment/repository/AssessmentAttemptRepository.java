package com.example.Uplink_Backend.modules.assessment.repository;

import com.example.Uplink_Backend.modules.assessment.entity.AssessmentAttempt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AssessmentAttemptRepository extends JpaRepository<AssessmentAttempt, UUID> {
    List<AssessmentAttempt> findByUserIdOrderByStartedAtDesc(UUID userId);
    Page<AssessmentAttempt> findByAssessmentIdOrderByScoreDesc(UUID assessmentId, Pageable pageable);
}
