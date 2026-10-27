package com.example.Uplink_Backend.modules.opportunity.controller;

import com.example.Uplink_Backend.common.dto.ApiResponse;
import com.example.Uplink_Backend.common.security.UserPrincipal;
import com.example.Uplink_Backend.modules.opportunity.dto.CreateOpportunityRequest;
import com.example.Uplink_Backend.modules.opportunity.dto.ExternalIngestionRequest;
import com.example.Uplink_Backend.modules.opportunity.entity.Opportunity;
import com.example.Uplink_Backend.modules.opportunity.service.OpportunityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/opportunities")
@Tag(name = "4. Opportunity Module", description = "Endpoints for Jobs, Internships, Hackathons, Workshops, Advanced Search, and External Ingestion")
public class OpportunityController {

    private final OpportunityService opportunityService;

    public OpportunityController(OpportunityService opportunityService) {
        this.opportunityService = opportunityService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('RECRUITER', 'ADMIN')")
    @Operation(summary = "Post a new opportunity (Job, Internship, Hackathon, Event, Training)")
    public ResponseEntity<ApiResponse<Opportunity>> createOpportunity(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                                      @Valid @RequestBody CreateOpportunityRequest request) {
        Opportunity opp = opportunityService.createOpportunity(userPrincipal.getId(), request);
        return ResponseEntity.ok(ApiResponse.created("Opportunity created successfully", opp));
    }

    @PostMapping("/external-ingest")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Ingest external opportunity from job boards/APIs (LinkedIn, Indeed, Unstop, etc.)")
    public ResponseEntity<ApiResponse<Opportunity>> ingestOpportunity(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                                      @Valid @RequestBody ExternalIngestionRequest request) {
        Opportunity opp = opportunityService.ingestExternalOpportunity(userPrincipal.getId(), request);
        return ResponseEntity.ok(ApiResponse.created("External opportunity ingested successfully", opp));
    }

    @GetMapping
    @Operation(summary = "Search and filter opportunities (by type, workMode, location, and keywords)")
    public ResponseEntity<ApiResponse<Page<Opportunity>>> searchOpportunities(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String workMode,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String keyword,
            Pageable pageable) {
        Page<Opportunity> result = opportunityService.searchOpportunities(type, workMode, location, keyword, pageable);
        return ResponseEntity.ok(ApiResponse.ok("Opportunities retrieved", result));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get detailed opportunity by ID including required skills")
    public ResponseEntity<ApiResponse<Opportunity>> getOpportunityById(@PathVariable UUID id) {
        Opportunity opp = opportunityService.getOpportunityById(id);
        return ResponseEntity.ok(ApiResponse.ok("Opportunity details retrieved", opp));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('RECRUITER', 'ADMIN')")
    @Operation(summary = "Activate or deactivate an opportunity listing")
    public ResponseEntity<ApiResponse<Opportunity>> updateStatus(@PathVariable UUID id, @RequestParam boolean isActive) {
        Opportunity opp = opportunityService.updateActiveStatus(id, isActive);
        return ResponseEntity.ok(ApiResponse.ok("Opportunity status updated", opp));
    }

    @PatchMapping("/{id}/verify")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Admin verifies opportunity listing")
    public ResponseEntity<ApiResponse<Opportunity>> verifyOpportunity(@PathVariable UUID id, @RequestParam boolean isVerified) {
        Opportunity opp = opportunityService.verifyOpportunity(id, isVerified);
        return ResponseEntity.ok(ApiResponse.ok("Opportunity verification updated", opp));
    }
}
