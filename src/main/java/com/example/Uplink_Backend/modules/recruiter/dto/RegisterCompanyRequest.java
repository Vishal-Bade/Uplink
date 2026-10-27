package com.example.Uplink_Backend.modules.recruiter.dto;

import jakarta.validation.constraints.NotBlank;

public class RegisterCompanyRequest {

    @NotBlank(message = "Company name is required")
    private String name;

    private String type; // PRODUCT, SERVICE, STARTUP, MNC
    private String website;
    private String logoUrl;
    private String description;
    private String designation; // Recruiter designation in this company

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }
}
