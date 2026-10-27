package com.example.Uplink_Backend.modules.opportunity.dto;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.List;

public class ExternalIngestionRequest {

    @NotBlank(message = "Source name is required (e.g., LinkedIn, Indeed, Devpost)")
    private String sourceName;

    @NotBlank(message = "Source URL is required")
    private String sourceUrl;

    @NotBlank(message = "Company name is required")
    private String companyName;

    @NotBlank(message = "Opportunity title is required")
    private String title;

    private String type = "JOB";
    private String description;
    private String location;
    private String workMode = "REMOTE";
    private BigDecimal stipendMin;
    private BigDecimal stipendMax;
    private String applicationUrl;
    private List<String> skills;

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }
}
