package com.example.Uplink_Backend.modules.verification.dto;

import jakarta.validation.constraints.NotBlank;

public class VerificationSubmitRequest {

    @NotBlank(message = "Document type is required (IDENTITY_PROOF, COLLEGE_ID, COMPANY_DOC, OTHER)")
    private String type;

    @NotBlank(message = "Document URL is required")
    private String documentUrl;

    private String remarks;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDocumentUrl() {
        return documentUrl;
    }

    public void setDocumentUrl(String documentUrl) {
        this.documentUrl = documentUrl;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
