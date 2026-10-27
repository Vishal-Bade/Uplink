package com.example.Uplink_Backend.modules.recommendation.entity;

import com.example.Uplink_Backend.modules.auth.entity.User;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "recommendations")
public class Recommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 50)
    private String type; // OPPORTUNITY, SKILL, CAREER_PATH

    @Column(name = "ref_id", nullable = false)
    private UUID refId;

    @Column(precision = 5, scale = 2)
    private BigDecimal score = BigDecimal.ZERO;

    @Column(columnDefinition = "TEXT")
    private String reason;

    public Recommendation() {}

    public Recommendation(User user, String type, UUID refId, BigDecimal score, String reason) {
        this.user = user;
        this.type = type;
        this.refId = refId;
        this.score = score;
        this.reason = reason;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public UUID getRefId() {
        return refId;
    }

    public void setRefId(UUID refId) {
        this.refId = refId;
    }

    public BigDecimal getScore() {
        return score;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
