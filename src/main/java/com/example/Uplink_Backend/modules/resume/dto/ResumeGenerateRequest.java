package com.example.Uplink_Backend.modules.resume.dto;

public class ResumeGenerateRequest {
    private String template = "MODERN"; // MODERN, CLASSIC, ACADEMIC, EXECUTIVE
    private String targetRole; // e.g., "Full Stack Software Engineer"
    private boolean includeProjects = true;
    private boolean includeCertifications = true;
    private boolean includeAchievements = true;

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getTargetRole() {
        return targetRole;
    }

    public void setTargetRole(String targetRole) {
        this.targetRole = targetRole;
    }

    public boolean isIncludeProjects() {
        return includeProjects;
    }

    public void setIncludeProjects(boolean includeProjects) {
        this.includeProjects = includeProjects;
    }

    public boolean isIncludeCertifications() {
        return includeCertifications;
    }

    public void setIncludeCertifications(boolean includeCertifications) {
        this.includeCertifications = includeCertifications;
    }

    public boolean isIncludeAchievements() {
        return includeAchievements;
    }

    public void setIncludeAchievements(boolean includeAchievements) {
        this.includeAchievements = includeAchievements;
    }
}
