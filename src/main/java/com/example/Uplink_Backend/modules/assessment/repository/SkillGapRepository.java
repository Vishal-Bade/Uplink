package com.example.Uplink_Backend.modules.assessment.repository;

import com.example.Uplink_Backend.modules.assessment.entity.SkillGap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SkillGapRepository extends JpaRepository<SkillGap, UUID> {
    List<SkillGap> findByUserIdOrderByCreatedAtDesc(UUID userId);
}
