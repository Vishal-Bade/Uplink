package com.example.Uplink_Backend.modules.assessment.repository;

import com.example.Uplink_Backend.modules.assessment.entity.AssessmentQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AssessmentQuestionRepository extends JpaRepository<AssessmentQuestion, UUID> {
    List<AssessmentQuestion> findByAssessmentIdOrderByOrderIndexAsc(UUID assessmentId);
}
