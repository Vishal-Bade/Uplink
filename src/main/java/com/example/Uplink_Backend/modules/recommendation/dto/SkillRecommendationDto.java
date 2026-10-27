package com.example.Uplink_Backend.modules.recommendation.dto;

public class SkillRecommendationDto {
    private String skillName;
    private String category;
    private double demandScore; // 0 - 100
    private String reason;

    public SkillRecommendationDto() {}

    public SkillRecommendationDto(String skillName, String category, double demandScore, String reason) {
        this.skillName = skillName;
        this.category = category;
        this.demandScore = demandScore;
        this.reason = reason;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getDemandScore() {
        return demandScore;
    }

    public void setDemandScore(double demandScore) {
        this.demandScore = demandScore;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
