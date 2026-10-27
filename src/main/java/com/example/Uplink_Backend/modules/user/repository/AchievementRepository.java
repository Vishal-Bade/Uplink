package com.example.Uplink_Backend.modules.user.repository;

import com.example.Uplink_Backend.modules.user.entity.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AchievementRepository extends JpaRepository<Achievement, UUID> {
    List<Achievement> findByUserIdOrderByDateDesc(UUID userId);
}
