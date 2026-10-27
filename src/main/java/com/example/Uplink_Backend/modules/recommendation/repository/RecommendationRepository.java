package com.example.Uplink_Backend.modules.recommendation.repository;

import com.example.Uplink_Backend.modules.recommendation.entity.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RecommendationRepository extends JpaRepository<Recommendation, UUID> {
    List<Recommendation> findByUserIdAndTypeOrderByScoreDesc(UUID userId, String type);
    List<Recommendation> findByUserIdOrderByScoreDesc(UUID userId);
    void deleteByUserId(UUID userId);
}
