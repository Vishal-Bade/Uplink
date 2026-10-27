package com.example.Uplink_Backend.modules.assessment.entity;

import com.example.Uplink_Backend.modules.auth.entity.User;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "skill_gaps")
public class SkillGap {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "target_role", nullable = false, length = 150)
    private String targetRole;

    @Column(name = "gap_data", columnDefinition = "TEXT")
    private String gapData; // JSON string with missing skills, match percentage, and recommended learning

    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();

    public SkillGap() {}

    public SkillGap(User user, String targetRole, String gapData) {
        this.user = user;
        this.targetRole = targetRole;
        this.gapData = gapData;
        this.createdAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getTargetRole() {
        return targetRole;
    }

    public void setTargetRole(String targetRole) {
        this.targetRole = targetRole;
    }

    public String getGapData() {
        return gapData;
    }

    public void setGapData(String gapData) {
        this.gapData = gapData;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
