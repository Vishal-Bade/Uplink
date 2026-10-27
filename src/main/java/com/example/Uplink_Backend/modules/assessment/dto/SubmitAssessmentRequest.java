package com.example.Uplink_Backend.modules.assessment.dto;

import jakarta.validation.constraints.NotNull;
import java.util.Map;
import java.util.UUID;

public class SubmitAssessmentRequest {

    @NotNull(message = "Assessment ID is required")
    private UUID assessmentId;

    // Map of Question UUID -> Candidate's Selected Option / Answer
    private Map<UUID, String> answers;

    public UUID getAssessmentId() {
        return assessmentId;
    }

    public void setAssessmentId(UUID assessmentId) {
        this.assessmentId = assessmentId;
    }

    public Map<UUID, String> getAnswers() {
        return answers;
    }

    public void setAnswers(Map<UUID, String> answers) {
        this.answers = answers;
    }
}
