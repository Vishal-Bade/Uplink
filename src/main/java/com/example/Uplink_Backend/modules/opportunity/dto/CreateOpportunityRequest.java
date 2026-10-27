package com.example.Uplink_Backend.modules.opportunity.dto;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class CreateOpportunityRequest {

    private UUID companyId; // If recruiter is posting on behalf of specific company

    @NotBlank(message = "Type is required (JOB, INTERNSHIP, HACKATHON, WORKSHOP, etc.)")
    private String type;

    @NotBlank(message = "Title is required")
    private String title;

    private String description;
    private String location;
    private String workMode = "REMOTE"; // REMOTE, HYBRID, ONSITE
    private String experienceLevel; // ENTRY, MID, SENIOR
    private BigDecimal stipendMin;
    private BigDecimal stipendMax;
    private String applicationUrl;
    private LocalDate startDate;
    private LocalDate deadline;
    private List<SkillRequirementDto> requiredSkills;

    public static class SkillRequirementDto {
        private String skillName;
        private boolean isMandatory;
        private String proficiencyRequired = "INTERMEDIATE";

        public String getSkillName() {
            return skillName;
        }

        public void setSkillName(String skillName) {
            this.skillName = skillName;
        }

        public boolean isMandatory() {
            return isMandatory;
        }

        public void setMandatory(boolean mandatory) {
            isMandatory = mandatory;
        }

        public String getProficiencyRequired() {
            return proficiencyRequired;
        }

        public void setProficiencyRequired(String proficiencyRequired) {
            this.proficiencyRequired = proficiencyRequired;
        }
    }

    public UUID getCompanyId() {
        return companyId;
    }

    public void setCompanyId(UUID companyId) {
        this.companyId = companyId;
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

    public List<SkillRequirementDto> getRequiredSkills() {
        return requiredSkills;
    }

    public void setRequiredSkills(List<SkillRequirementDto> requiredSkills) {
        this.requiredSkills = requiredSkills;
    }
}
