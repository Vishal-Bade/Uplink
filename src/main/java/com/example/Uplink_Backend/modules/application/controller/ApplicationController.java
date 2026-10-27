package com.example.Uplink_Backend.modules.application.controller;

import com.example.Uplink_Backend.common.dto.ApiResponse;
import com.example.Uplink_Backend.common.security.UserPrincipal;
import com.example.Uplink_Backend.modules.application.dto.ApplyOpportunityRequest;
import com.example.Uplink_Backend.modules.application.dto.UpdateApplicationStatusRequest;
import com.example.Uplink_Backend.modules.application.entity.Application;
import com.example.Uplink_Backend.modules.application.entity.ApplicationStatusHistory;
import com.example.Uplink_Backend.modules.application.service.ApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/applications")
@Tag(name = "5. Application Module", description = "Endpoints for applying to opportunities, tracking applications, status updates, and history")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping("/apply")
    @Operation(summary = "Apply for a job, internship, or hackathon opportunity")
    public ResponseEntity<ApiResponse<Application>> apply(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                          @Valid @RequestBody ApplyOpportunityRequest request) {
        Application application = applicationService.apply(userPrincipal.getId(), request);
        return ResponseEntity.ok(ApiResponse.created("Application submitted successfully", application));
    }

    @GetMapping("/my")
    @Operation(summary = "Get all applications submitted by current student/user")
    public ResponseEntity<ApiResponse<Page<Application>>> getMyApplications(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                                            Pageable pageable) {
        Page<Application> apps = applicationService.getMyApplications(userPrincipal.getId(), pageable);
        return ResponseEntity.ok(ApiResponse.ok("User applications retrieved", apps));
    }

    @GetMapping("/opportunity/{opportunityId}")
    @PreAuthorize("hasAnyRole('RECRUITER', 'ADMIN')")
    @Operation(summary = "Recruiter views all applications received for an opportunity")
    public ResponseEntity<ApiResponse<Page<Application>>> getApplicationsForOpportunity(@PathVariable UUID opportunityId,
                                                                                        Pageable pageable) {
        Page<Application> apps = applicationService.getApplicationsForOpportunity(opportunityId, pageable);
        return ResponseEntity.ok(ApiResponse.ok("Opportunity applications retrieved", apps));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get application details by ID")
    public ResponseEntity<ApiResponse<Application>> getApplicationById(@PathVariable UUID id) {
        Application app = applicationService.getApplicationById(id);
        return ResponseEntity.ok(ApiResponse.ok("Application details retrieved", app));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('RECRUITER', 'ADMIN')")
    @Operation(summary = "Recruiter/Admin updates application status (UNDER_REVIEW, SHORTLISTED, INTERVIEW, SELECTED, REJECTED)")
    public ResponseEntity<ApiResponse<Application>> updateStatus(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                                 @PathVariable UUID id,
                                                                 @Valid @RequestBody UpdateApplicationStatusRequest request) {
        Application updated = applicationService.updateStatus(userPrincipal.getId(), id, request);
        return ResponseEntity.ok(ApiResponse.ok("Application status updated and applicant notified", updated));
    }

    @GetMapping("/{id}/history")
    @Operation(summary = "Get lifecycle audit history of an application")
    public ResponseEntity<ApiResponse<List<ApplicationStatusHistory>>> getStatusHistory(@PathVariable UUID id) {
        List<ApplicationStatusHistory> history = applicationService.getStatusHistory(id);
        return ResponseEntity.ok(ApiResponse.ok("Application timeline history retrieved", history));
    }
}
