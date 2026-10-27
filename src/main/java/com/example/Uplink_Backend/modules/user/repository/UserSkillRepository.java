package com.example.Uplink_Backend.modules.user.repository;

import com.example.Uplink_Backend.modules.user.entity.UserSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserSkillRepository extends JpaRepository<UserSkill, UUID> {
    List<UserSkill> findByUserId(UUID userId);
    Optional<UserSkill> findByUserIdAndSkillId(UUID userId, UUID skillId);
    boolean existsByUserIdAndSkillId(UUID userId, UUID skillId);
}
