package com.example.Uplink_Backend.modules.analytics.dto;

import java.util.List;
import java.util.Map;

public class PlacementAnalyticsDto {
    private double placementRatePercentage;
    private long totalPlacements;
    private long totalOpportunities;
    private Map<String, Long> hiresByRoleType;
    private List<String> topHiringCompanies;
    private List<String> topInDemandSkills;
    private Map<String, Long> monthlyTrend;

    public PlacementAnalyticsDto() {}

    public PlacementAnalyticsDto(double placementRatePercentage, long totalPlacements, long totalOpportunities,
                                 Map<String, Long> hiresByRoleType, List<String> topHiringCompanies,
                                 List<String> topInDemandSkills, Map<String, Long> monthlyTrend) {
        this.placementRatePercentage = placementRatePercentage;
        this.totalPlacements = totalPlacements;
        this.totalOpportunities = totalOpportunities;
        this.hiresByRoleType = hiresByRoleType;
        this.topHiringCompanies = topHiringCompanies;
        this.topInDemandSkills = topInDemandSkills;
        this.monthlyTrend = monthlyTrend;
    }

    public double getPlacementRatePercentage() {
        return placementRatePercentage;
    }

    public void setPlacementRatePercentage(double placementRatePercentage) {
        this.placementRatePercentage = placementRatePercentage;
    }

    public long getTotalPlacements() {
        return totalPlacements;
    }

    public void setTotalPlacements(long totalPlacements) {
        this.totalPlacements = totalPlacements;
    }

    public long getTotalOpportunities() {
        return totalOpportunities;
    }

    public void setTotalOpportunities(long totalOpportunities) {
        this.totalOpportunities = totalOpportunities;
    }

    public Map<String, Long> getHiresByRoleType() {
        return hiresByRoleType;
    }

    public void setHiresByRoleType(Map<String, Long> hiresByRoleType) {
        this.hiresByRoleType = hiresByRoleType;
    }

    public List<String> getTopHiringCompanies() {
        return topHiringCompanies;
    }

    public void setTopHiringCompanies(List<String> topHiringCompanies) {
        this.topHiringCompanies = topHiringCompanies;
    }

    public List<String> getTopInDemandSkills() {
        return topInDemandSkills;
    }

    public void setTopInDemandSkills(List<String> topInDemandSkills) {
        this.topInDemandSkills = topInDemandSkills;
    }

    public Map<String, Long> getMonthlyTrend() {
        return monthlyTrend;
    }

    public void setMonthlyTrend(Map<String, Long> monthlyTrend) {
        this.monthlyTrend = monthlyTrend;
    }
}
