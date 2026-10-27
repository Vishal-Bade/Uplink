package com.example.Uplink_Backend.modules.verification.dto;

import jakarta.validation.constraints.NotBlank;

public class VerificationReviewRequest {

    @NotBlank(message = "Status is required (APPROVED, REJECTED)")
    private String status;

    private String remarks;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
