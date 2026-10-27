package com.example.Uplink_Backend.modules.recommendation.dto;

import java.util.List;

public class CareerPathDto {
    private String roleTitle;
    private String domain;
    private double matchProbability;
    private String roadmapDescription;
    private List<String> keyMilestones;
    private List<String> recommendedCertifications;
    private String estimatedSalaryRange;

    public CareerPathDto() {}

    public CareerPathDto(String roleTitle, String domain, double matchProbability, String roadmapDescription,
                         List<String> keyMilestones, List<String> recommendedCertifications, String estimatedSalaryRange) {
        this.roleTitle = roleTitle;
        this.domain = domain;
        this.matchProbability = matchProbability;
        this.roadmapDescription = roadmapDescription;
        this.keyMilestones = keyMilestones;
        this.recommendedCertifications = recommendedCertifications;
        this.estimatedSalaryRange = estimatedSalaryRange;
    }

    public String getRoleTitle() {
        return roleTitle;
    }

    public void setRoleTitle(String roleTitle) {
        this.roleTitle = roleTitle;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public double getMatchProbability() {
        return matchProbability;
    }

    public void setMatchProbability(double matchProbability) {
        this.matchProbability = matchProbability;
    }

    public String getRoadmapDescription() {
        return roadmapDescription;
    }

    public void setRoadmapDescription(String roadmapDescription) {
        this.roadmapDescription = roadmapDescription;
    }

    public List<String> getKeyMilestones() {
        return keyMilestones;
    }

    public void setKeyMilestones(List<String> keyMilestones) {
        this.keyMilestones = keyMilestones;
    }

    public List<String> getRecommendedCertifications() {
        return recommendedCertifications;
    }

    public void setRecommendedCertifications(List<String> recommendedCertifications) {
        this.recommendedCertifications = recommendedCertifications;
    }

    public String getEstimatedSalaryRange() {
        return estimatedSalaryRange;
    }

    public void setEstimatedSalaryRange(String estimatedSalaryRange) {
        this.estimatedSalaryRange = estimatedSalaryRange;
    }
}
