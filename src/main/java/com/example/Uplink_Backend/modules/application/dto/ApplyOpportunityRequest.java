package com.example.Uplink_Backend.modules.application.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

public class ApplyOpportunityRequest {

    @NotNull(message = "Opportunity ID is required")
    private UUID opportunityId;

    private String coverLetter;
    private String resumeUrl;
    private BigDecimal expectedCtc;
    private String remarks;

    public UUID getOpportunityId() {
        return opportunityId;
    }

    public void setOpportunityId(UUID opportunityId) {
        this.opportunityId = opportunityId;
    }

    public String getCoverLetter() {
        return coverLetter;
    }

    public void setCoverLetter(String coverLetter) {
        this.coverLetter = coverLetter;
    }

    public String getResumeUrl() {
        return resumeUrl;
    }

    public void setResumeUrl(String resumeUrl) {
        this.resumeUrl = resumeUrl;
    }

    public BigDecimal getExpectedCtc() {
        return expectedCtc;
    }

    public void setExpectedCtc(BigDecimal expectedCtc) {
        this.expectedCtc = expectedCtc;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
