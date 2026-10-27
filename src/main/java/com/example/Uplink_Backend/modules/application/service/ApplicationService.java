package com.example.Uplink_Backend.modules.application.service;

import com.example.Uplink_Backend.common.exception.BadRequestException;
import com.example.Uplink_Backend.common.exception.ResourceNotFoundException;
import com.example.Uplink_Backend.common.service.EmailService;
import com.example.Uplink_Backend.modules.application.dto.ApplyOpportunityRequest;
import com.example.Uplink_Backend.modules.application.dto.UpdateApplicationStatusRequest;
import com.example.Uplink_Backend.modules.application.entity.Application;
import com.example.Uplink_Backend.modules.application.entity.ApplicationStatusHistory;
import com.example.Uplink_Backend.modules.application.repository.ApplicationRepository;
import com.example.Uplink_Backend.modules.application.repository.ApplicationStatusHistoryRepository;
import com.example.Uplink_Backend.modules.auth.entity.User;
import com.example.Uplink_Backend.modules.auth.repository.UserRepository;
import com.example.Uplink_Backend.modules.opportunity.entity.Opportunity;
import com.example.Uplink_Backend.modules.opportunity.repository.OpportunityRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final ApplicationStatusHistoryRepository historyRepository;
    private final OpportunityRepository opportunityRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public ApplicationService(ApplicationRepository applicationRepository,
                              ApplicationStatusHistoryRepository historyRepository,
                              OpportunityRepository opportunityRepository,
                              UserRepository userRepository,
                              EmailService emailService) {
        this.applicationRepository = applicationRepository;
        this.historyRepository = historyRepository;
        this.opportunityRepository = opportunityRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    @Transactional
    public Application apply(UUID userId, ApplyOpportunityRequest request) {
        if (applicationRepository.existsByOpportunityIdAndUserId(request.getOpportunityId(), userId)) {
            throw new BadRequestException("You have already applied for this opportunity");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Opportunity opp = opportunityRepository.findById(request.getOpportunityId())
                .orElseThrow(() -> new ResourceNotFoundException("Opportunity", "id", request.getOpportunityId()));

        if (Boolean.FALSE.equals(opp.getIsActive())) {
            throw new BadRequestException("This opportunity is no longer active");
        }

        Application application = new Application();
        application.setUser(user);
        application.setOpportunity(opp);
        application.setStatus("APPLIED");
        application.setCoverLetter(request.getCoverLetter());
        application.setResumeUrl(request.getResumeUrl());
        application.setExpectedCtc(request.getExpectedCtc());
        application.setRemarks(request.getRemarks());

        Application savedApp = applicationRepository.save(application);

        // Record initial status history
        ApplicationStatusHistory history = new ApplicationStatusHistory(savedApp, "APPLIED", user, "Application submitted");
        historyRepository.save(history);

        return savedApp;
    }

    public Page<Application> getMyApplications(UUID userId, Pageable pageable) {
        return applicationRepository.findByUserIdOrderByAppliedAtDesc(userId, pageable);
    }

    public Page<Application> getApplicationsForOpportunity(UUID opportunityId, Pageable pageable) {
        return applicationRepository.findByOpportunityIdOrderByAppliedAtDesc(opportunityId, pageable);
    }

    public Application getApplicationById(UUID applicationId) {
        return applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application", "id", applicationId));
    }

    @Transactional
    public Application updateStatus(UUID reviewerId, UUID applicationId, UpdateApplicationStatusRequest request) {
        Application application = getApplicationById(applicationId);
        User reviewer = userRepository.findById(reviewerId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", reviewerId));

        String newStatus = request.getStatus().toUpperCase();
        application.setStatus(newStatus);
        if (request.getRemarks() != null) {
            application.setRemarks(request.getRemarks());
        }
        application.setUpdatedAt(Instant.now());

        Application updatedApp = applicationRepository.save(application);

        // Save history audit
        ApplicationStatusHistory history = new ApplicationStatusHistory(updatedApp, newStatus, reviewer, request.getRemarks());
        historyRepository.save(history);

        // Send email update to applicant
        try {
            emailService.sendApplicationStatusEmail(
                    updatedApp.getUser().getEmail(),
                    updatedApp.getOpportunity().getTitle(),
                    newStatus,
                    request.getRemarks()
            );
        } catch (Exception ignored) {}

        return updatedApp;
    }

    public List<ApplicationStatusHistory> getStatusHistory(UUID applicationId) {
        return historyRepository.findByApplicationIdOrderByChangedAtAsc(applicationId);
    }
}
