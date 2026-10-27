package com.example.Uplink_Backend.modules.application.dto;

import jakarta.validation.constraints.NotBlank;

public class UpdateApplicationStatusRequest {

    @NotBlank(message = "Status is required (APPLIED, UNDER_REVIEW, SHORTLISTED, INTERVIEW, SELECTED, REJECTED, WITHDRAWN)")
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
