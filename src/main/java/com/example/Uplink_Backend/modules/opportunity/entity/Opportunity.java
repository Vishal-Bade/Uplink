package com.example.Uplink_Backend.modules.opportunity.entity;

import com.example.Uplink_Backend.modules.auth.entity.User;
import com.example.Uplink_Backend.modules.recruiter.entity.Company;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "opportunities")
public class Opportunity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Column(nullable = false, length = 50)
    private String type; // JOB, INTERNSHIP, HACKATHON, COMPETITION, WORKSHOP, SCHOLARSHIP, TRAINING, RESEARCH, APPRENTICESHIP

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String location;

    @Column(name = "work_mode", length = 50)
    private String workMode = "ONSITE"; // REMOTE, HYBRID, ONSITE

    @Column(name = "experience_level", length = 50)
    private String experienceLevel; // ENTRY, MID, SENIOR

    @Column(name = "stipend_min", precision = 12, scale = 2)
    private BigDecimal stipendMin;

    @Column(name = "stipend_max", precision = 12, scale = 2)
    private BigDecimal stipendMax;

    @Column(name = "application_url", columnDefinition = "TEXT")
    private String applicationUrl;

    @Column(name = "start_date")
    private LocalDate startDate;

    private LocalDate deadline;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "is_verified")
    private Boolean isVerified = false;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at")
    private Instant updatedAt = Instant.now();

    @OneToMany(mappedBy = "opportunity", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<OpportunitySkill> requiredSkills = new ArrayList<>();

    public Opportunity() {}

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = Instant.now();
        if (updatedAt == null) updatedAt = Instant.now();
        if (isActive == null) isActive = true;
        if (isVerified == null) isVerified = false;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getWorkMode() {
        return workMode;
    }

    public void setWorkMode(String workMode) {
        this.workMode = workMode;
    }

    public String getExperienceLevel() {
        return experienceLevel;
    }

    public void setExperienceLevel(String experienceLevel) {
        this.experienceLevel = experienceLevel;
    }

    public BigDecimal getStipendMin() {
        return stipendMin;
    }

    public void setStipendMin(BigDecimal stipendMin) {
        this.stipendMin = stipendMin;
    }

    public BigDecimal getStipendMax() {
        return stipendMax;
    }

    public void setStipendMax(BigDecimal stipendMax) {
        this.stipendMax = stipendMax;
    }

    public String getApplicationUrl() {
        return applicationUrl;
    }

    public void setApplicationUrl(String applicationUrl) {
        this.applicationUrl = applicationUrl;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
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

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<OpportunitySkill> getRequiredSkills() {
        return requiredSkills;
    }

    public void setRequiredSkills(List<OpportunitySkill> requiredSkills) {
        this.requiredSkills = requiredSkills;
    }
}
