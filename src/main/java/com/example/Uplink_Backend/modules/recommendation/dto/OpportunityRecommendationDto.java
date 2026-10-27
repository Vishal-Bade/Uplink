package com.example.Uplink_Backend.modules.recommendation.dto;

import com.example.Uplink_Backend.modules.opportunity.entity.Opportunity;
import java.math.BigDecimal;

public class OpportunityRecommendationDto {
    private Opportunity opportunity;
    private BigDecimal matchScore;
    private String reason;

    public OpportunityRecommendationDto() {}

    public OpportunityRecommendationDto(Opportunity opportunity, BigDecimal matchScore, String reason) {
        this.opportunity = opportunity;
        this.matchScore = matchScore;
        this.reason = reason;
    }

    public Opportunity getOpportunity() {
        return opportunity;
    }

    public void setOpportunity(Opportunity opportunity) {
        this.opportunity = opportunity;
    }

    public BigDecimal getMatchScore() {
        return matchScore;
    }

    public void setMatchScore(BigDecimal matchScore) {
        this.matchScore = matchScore;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
