package com.example.Uplink_Backend.modules.recruiter.controller;

import com.example.Uplink_Backend.common.dto.ApiResponse;
import com.example.Uplink_Backend.common.security.UserPrincipal;
import com.example.Uplink_Backend.modules.recruiter.dto.RegisterCompanyRequest;
import com.example.Uplink_Backend.modules.recruiter.entity.Company;
import com.example.Uplink_Backend.modules.recruiter.entity.Recruiter;
import com.example.Uplink_Backend.modules.recruiter.service.RecruiterService;
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
@RequestMapping("/api/recruiter")
@Tag(name = "7. Recruiter Module", description = "Endpoints for Company management, Recruiter registration, and Verification")
public class RecruiterController {

    private final RecruiterService recruiterService;

    public RecruiterController(RecruiterService recruiterService) {
        this.recruiterService = recruiterService;
    }

    @PostMapping("/company")
    @Operation(summary = "Register or join a company as recruiter")
    public ResponseEntity<ApiResponse<Recruiter>> registerCompany(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                                  @Valid @RequestBody RegisterCompanyRequest request) {
        Recruiter recruiter = recruiterService.registerOrJoinCompany(userPrincipal.getId(), request);
        return ResponseEntity.ok(ApiResponse.ok("Company & Recruiter profile setup successfully", recruiter));
    }

    @GetMapping("/me")
    @Operation(summary = "Get current recruiter profile and assigned company")
    public ResponseEntity<ApiResponse<Recruiter>> getMyRecruiterProfile(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        Recruiter recruiter = recruiterService.getRecruiterByUserId(userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.ok("Recruiter profile retrieved", recruiter));
    }

    @GetMapping("/companies")
    @Operation(summary = "List all companies (paginated)")
    public ResponseEntity<ApiResponse<Page<Company>>> getAllCompanies(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(recruiterService.getAllCompanies(pageable)));
    }

    @GetMapping("/companies/{id}")
    @Operation(summary = "Get company details by ID")
    public ResponseEntity<ApiResponse<Company>> getCompanyById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(recruiterService.getCompanyById(id)));
    }

    @PutMapping("/companies/{id}/verify")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Admin verifies or revokes company verification")
    public ResponseEntity<ApiResponse<Company>> verifyCompany(@PathVariable UUID id, @RequestParam boolean isVerified) {
        Company company = recruiterService.verifyCompany(id, isVerified);
        return ResponseEntity.ok(ApiResponse.ok("Company verification status updated", company));
    }

    @PutMapping("/verify/{recruiterId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Admin verifies or revokes recruiter verification")
    public ResponseEntity<ApiResponse<Recruiter>> verifyRecruiter(@PathVariable UUID recruiterId, @RequestParam boolean isVerified) {
        Recruiter recruiter = recruiterService.verifyRecruiter(recruiterId, isVerified);
        return ResponseEntity.ok(ApiResponse.ok("Recruiter verification status updated", recruiter));
    }
}
