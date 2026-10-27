package com.example.Uplink_Backend.modules.user.entity;

import com.example.Uplink_Backend.modules.auth.entity.User;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "user_skills", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "skill_id"})
})
public class UserSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    @Column(length = 50)
    private String proficiency = "BEGINNER"; // BEGINNER, INTERMEDIATE, ADVANCED, EXPERT

    @Column(name = "years_of_exp", precision = 4, scale = 1)
    private BigDecimal yearsOfExp = BigDecimal.ZERO;

    @Column(name = "is_verified")
    private Boolean isVerified = false;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();

    public UserSkill() {}

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

    public Skill getSkill() {
        return skill;
    }

    public void setSkill(Skill skill) {
        this.skill = skill;
    }

    public String getProficiency() {
        return proficiency;
    }

    public void setProficiency(String proficiency) {
        this.proficiency = proficiency;
    }

    public BigDecimal getYearsOfExp() {
        return yearsOfExp;
    }

    public void setYearsOfExp(BigDecimal yearsOfExp) {
        this.yearsOfExp = yearsOfExp;
    }

    public Boolean getIsVerified() {
        return isVerified;
    }

    public void setIsVerified(Boolean isVerified) {
        this.isVerified = isVerified;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
