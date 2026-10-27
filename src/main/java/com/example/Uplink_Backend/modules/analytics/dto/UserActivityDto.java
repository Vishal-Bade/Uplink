package com.example.Uplink_Backend.modules.analytics.dto;

import java.util.Map;
import java.util.UUID;

public class UserActivityDto {
    private UUID userId;
    private long totalApplicationsSent;
    private long shortlistedApplications;
    private long interviewsScheduled;
    private long offersReceived;
    private long assessmentsCompleted;
    private Map<String, Long> statusBreakdown;

    public UserActivityDto() {}

    public UserActivityDto(UUID userId, long totalApplicationsSent, long shortlistedApplications,
                           long interviewsScheduled, long offersReceived, long assessmentsCompleted,
                           Map<String, Long> statusBreakdown) {
        this.userId = userId;
        this.totalApplicationsSent = totalApplicationsSent;
        this.shortlistedApplications = shortlistedApplications;
        this.interviewsScheduled = interviewsScheduled;
        this.offersReceived = offersReceived;
        this.assessmentsCompleted = assessmentsCompleted;
        this.statusBreakdown = statusBreakdown;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public long getTotalApplicationsSent() {
        return totalApplicationsSent;
    }

    public void setTotalApplicationsSent(long totalApplicationsSent) {
        this.totalApplicationsSent = totalApplicationsSent;
    }

    public long getShortlistedApplications() {
        return shortlistedApplications;
    }

    public void setShortlistedApplications(long shortlistedApplications) {
        this.shortlistedApplications = shortlistedApplications;
    }

    public long getInterviewsScheduled() {
        return interviewsScheduled;
    }

    public void setInterviewsScheduled(long interviewsScheduled) {
        this.interviewsScheduled = interviewsScheduled;
    }

    public long getOffersReceived() {
        return offersReceived;
    }

    public void setOffersReceived(long offersReceived) {
        this.offersReceived = offersReceived;
    }

    public long getAssessmentsCompleted() {
        return assessmentsCompleted;
    }

    public void setAssessmentsCompleted(long assessmentsCompleted) {
        this.assessmentsCompleted = assessmentsCompleted;
    }

    public Map<String, Long> getStatusBreakdown() {
        return statusBreakdown;
    }

    public void setStatusBreakdown(Map<String, Long> statusBreakdown) {
        this.statusBreakdown = statusBreakdown;
    }
}
