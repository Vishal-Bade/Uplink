package com.example.Uplink_Backend.modules.assessment.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class AssessmentResultDto {
    private UUID attemptId;
    private UUID assessmentId;
    private String assessmentTitle;
    private BigDecimal score;
    private BigDecimal totalMarks;
    private BigDecimal percentage;
    private boolean passed;
    private Instant submittedAt;

    public AssessmentResultDto() {}

    public AssessmentResultDto(UUID attemptId, UUID assessmentId, String assessmentTitle, BigDecimal score,
                               BigDecimal totalMarks, BigDecimal percentage, boolean passed, Instant submittedAt) {
        this.attemptId = attemptId;
        this.assessmentId = assessmentId;
        this.assessmentTitle = assessmentTitle;
        this.score = score;
        this.totalMarks = totalMarks;
        this.percentage = percentage;
        this.passed = passed;
        this.submittedAt = submittedAt;
    }

    public UUID getAttemptId() {
        return attemptId;
    }

    public void setAttemptId(UUID attemptId) {
        this.attemptId = attemptId;
    }

    public UUID getAssessmentId() {
        return assessmentId;
    }

    public void setAssessmentId(UUID assessmentId) {
        this.assessmentId = assessmentId;
    }

    public String getAssessmentTitle() {
        return assessmentTitle;
    }

    public void setAssessmentTitle(String assessmentTitle) {
        this.assessmentTitle = assessmentTitle;
    }

    public BigDecimal getScore() {
        return score;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }

    public BigDecimal getTotalMarks() {
        return totalMarks;
    }

    public void setTotalMarks(BigDecimal totalMarks) {
        this.totalMarks = totalMarks;
    }

    public BigDecimal getPercentage() {
        return percentage;
    }

    public void setPercentage(BigDecimal percentage) {
        this.percentage = percentage;
    }

    public boolean isPassed() {
        return passed;
    }

    public void setPassed(boolean passed) {
        this.passed = passed;
    }

    public Instant getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(Instant submittedAt) {
        this.submittedAt = submittedAt;
    }
}
