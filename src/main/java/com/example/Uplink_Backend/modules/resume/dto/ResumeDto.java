package com.example.Uplink_Backend.modules.resume.dto;

import java.time.Instant;
import java.util.UUID;

public class ResumeDto {
    private UUID id;
    private UUID userId;
    private int version;
    private String template;
    private String latexSource;
    private String pdfDownloadUrl;
    private Instant generatedAt;

    public ResumeDto() {}

    public ResumeDto(UUID id, UUID userId, int version, String template, String latexSource, String pdfDownloadUrl, Instant generatedAt) {
        this.id = id;
        this.userId = userId;
        this.version = version;
        this.template = template;
        this.latexSource = latexSource;
        this.pdfDownloadUrl = pdfDownloadUrl;
        this.generatedAt = generatedAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getLatexSource() {
        return latexSource;
    }

    public void setLatexSource(String latexSource) {
        this.latexSource = latexSource;
    }

    public String getPdfDownloadUrl() {
        return pdfDownloadUrl;
    }

    public void setPdfDownloadUrl(String pdfDownloadUrl) {
        this.pdfDownloadUrl = pdfDownloadUrl;
    }

    public Instant getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(Instant generatedAt) {
        this.generatedAt = generatedAt;
    }
}
