package com.example.Uplink_Backend.modules.verification.controller;

import com.example.Uplink_Backend.common.dto.ApiResponse;
import com.example.Uplink_Backend.common.security.UserPrincipal;
import com.example.Uplink_Backend.modules.auth.entity.UserVerification;
import com.example.Uplink_Backend.modules.verification.dto.VerificationReviewRequest;
import com.example.Uplink_Backend.modules.verification.dto.VerificationSubmitRequest;
import com.example.Uplink_Backend.modules.verification.service.VerificationService;
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
@RequestMapping("/api/verification")
@Tag(name = "13. Verification Module", description = "Endpoints for User Verification, Institutional Documents, and Admin Verification Review")
public class VerificationController {

    private final VerificationService verificationService;

    public VerificationController(VerificationService verificationService) {
        this.verificationService = verificationService;
    }

    @PostMapping("/submit")
    @Operation(summary = "Submit identity proof, college ID, or company document for verification")
    public ResponseEntity<ApiResponse<UserVerification>> submitVerification(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody VerificationSubmitRequest request) {
        UserVerification uv = verificationService.submitVerification(userPrincipal.getId(), request);
        return ResponseEntity.ok(ApiResponse.created("Verification document submitted successfully", uv));
    }

    @GetMapping("/my")
    @Operation(summary = "Get user's submitted verification documents and review status")
    public ResponseEntity<ApiResponse<List<UserVerification>>> getMyVerifications(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<UserVerification> list = verificationService.getMyVerifications(userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.ok("Verification history retrieved", list));
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Admin lists pending user verifications for review (paginated)")
    public ResponseEntity<ApiResponse<Page<UserVerification>>> getPendingVerifications(Pageable pageable) {
        Page<UserVerification> list = verificationService.getPendingVerifications(pageable);
        return ResponseEntity.ok(ApiResponse.ok("Pending verifications retrieved", list));
    }

    @PatchMapping("/{id}/review")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Admin approves or rejects a verification request")
    public ResponseEntity<ApiResponse<UserVerification>> reviewVerification(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable UUID id,
            @Valid @RequestBody VerificationReviewRequest request) {
        UserVerification updated = verificationService.reviewVerification(userPrincipal.getId(), id, request);
        return ResponseEntity.ok(ApiResponse.ok("Verification reviewed successfully", updated));
    }
}
