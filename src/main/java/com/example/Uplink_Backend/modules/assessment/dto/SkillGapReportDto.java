package com.example.Uplink_Backend.modules.assessment.dto;

import java.util.List;

public class SkillGapReportDto {
    private String targetRole;
    private double matchPercentage;
    private List<String> userSkills;
    private List<String> requiredRoleSkills;
    private List<String> missingSkills;
    private List<String> recommendedCoursesOrAssessments;

    public SkillGapReportDto() {}

    public SkillGapReportDto(String targetRole, double matchPercentage, List<String> userSkills,
                             List<String> requiredRoleSkills, List<String> missingSkills,
                             List<String> recommendedCoursesOrAssessments) {
        this.targetRole = targetRole;
        this.matchPercentage = matchPercentage;
        this.userSkills = userSkills;
        this.requiredRoleSkills = requiredRoleSkills;
        this.missingSkills = missingSkills;
        this.recommendedCoursesOrAssessments = recommendedCoursesOrAssessments;
    }

    public String getTargetRole() {
        return targetRole;
    }

    public void setTargetRole(String targetRole) {
        this.targetRole = targetRole;
    }

    public double getMatchPercentage() {
        return matchPercentage;
    }

    public void setMatchPercentage(double matchPercentage) {
        this.matchPercentage = matchPercentage;
    }

    public List<String> getUserSkills() {
        return userSkills;
    }

    public void setUserSkills(List<String> userSkills) {
        this.userSkills = userSkills;
    }

    public List<String> getRequiredRoleSkills() {
        return requiredRoleSkills;
    }

    public void setRequiredRoleSkills(List<String> requiredRoleSkills) {
        this.requiredRoleSkills = requiredRoleSkills;
    }

    public List<String> getMissingSkills() {
        return missingSkills;
    }

    public void setMissingSkills(List<String> missingSkills) {
        this.missingSkills = missingSkills;
    }

    public List<String> getRecommendedCoursesOrAssessments() {
        return recommendedCoursesOrAssessments;
    }

    public void setRecommendedCoursesOrAssessments(List<String> recommendedCoursesOrAssessments) {
        this.recommendedCoursesOrAssessments = recommendedCoursesOrAssessments;
    }
}
