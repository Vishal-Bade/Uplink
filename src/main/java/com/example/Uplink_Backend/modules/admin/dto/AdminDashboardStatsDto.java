package com.example.Uplink_Backend.modules.admin.dto;

import java.util.Map;

public class AdminDashboardStatsDto {
    private long totalUsers;
    private long activeUsers;
    private long totalCompanies;
    private long activeOpportunities;
    private long totalApplications;
    private long pendingVerifications;
    private long selectedPlacements;
    private Map<String, Long> applicationsByStatus;

    public AdminDashboardStatsDto() {}

    public AdminDashboardStatsDto(long totalUsers, long activeUsers, long totalCompanies, long activeOpportunities,
                                  long totalApplications, long pendingVerifications, long selectedPlacements,
                                  Map<String, Long> applicationsByStatus) {
        this.totalUsers = totalUsers;
        this.activeUsers = activeUsers;
        this.totalCompanies = totalCompanies;
        this.activeOpportunities = activeOpportunities;
        this.totalApplications = totalApplications;
        this.pendingVerifications = pendingVerifications;
        this.selectedPlacements = selectedPlacements;
        this.applicationsByStatus = applicationsByStatus;
    }

    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public long getActiveUsers() {
        return activeUsers;
    }

    public void setActiveUsers(long activeUsers) {
        this.activeUsers = activeUsers;
    }

    public long getTotalCompanies() {
        return totalCompanies;
    }

    public void setTotalCompanies(long totalCompanies) {
        this.totalCompanies = totalCompanies;
    }

    public long getActiveOpportunities() {
        return activeOpportunities;
    }

    public void setActiveOpportunities(long activeOpportunities) {
        this.activeOpportunities = activeOpportunities;
    }

    public long getTotalApplications() {
        return totalApplications;
    }

    public void setTotalApplications(long totalApplications) {
        this.totalApplications = totalApplications;
    }

    public long getPendingVerifications() {
        return pendingVerifications;
    }

    public void setPendingVerifications(long pendingVerifications) {
        this.pendingVerifications = pendingVerifications;
    }

    public long getSelectedPlacements() {
        return selectedPlacements;
    }

    public void setSelectedPlacements(long selectedPlacements) {
        this.selectedPlacements = selectedPlacements;
    }

    public Map<String, Long> getApplicationsByStatus() {
        return applicationsByStatus;
    }

    public void setApplicationsByStatus(Map<String, Long> applicationsByStatus) {
        this.applicationsByStatus = applicationsByStatus;
    }
}
