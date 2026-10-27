package com.example.Uplink_Backend.modules.assessment.controller;

import com.example.Uplink_Backend.common.dto.ApiResponse;
import com.example.Uplink_Backend.common.security.UserPrincipal;
import com.example.Uplink_Backend.modules.assessment.dto.AssessmentResultDto;
import com.example.Uplink_Backend.modules.assessment.dto.SkillGapReportDto;
import com.example.Uplink_Backend.modules.assessment.dto.SubmitAssessmentRequest;
import com.example.Uplink_Backend.modules.assessment.entity.Assessment;
import com.example.Uplink_Backend.modules.assessment.entity.AssessmentAttempt;
import com.example.Uplink_Backend.modules.assessment.service.AssessmentService;
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
@RequestMapping("/api/assessments")
@Tag(name = "3. Assessment Module", description = "Endpoints for Skill Assessments, Competency Mapping, and Skill Gap Analysis")
public class AssessmentController {

    private final AssessmentService assessmentService;

    public AssessmentController(AssessmentService assessmentService) {
        this.assessmentService = assessmentService;
    }

    @GetMapping
    @Operation(summary = "List all active skill assessments (paginated)")
    public ResponseEntity<ApiResponse<Page<Assessment>>> getAssessments(Pageable pageable) {
        Page<Assessment> assessments = assessmentService.getActiveAssessments(pageable);
        return ResponseEntity.ok(ApiResponse.ok("Assessments retrieved", assessments));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get assessment questions and details by ID")
    public ResponseEntity<ApiResponse<Assessment>> getAssessmentById(@PathVariable UUID id) {
        Assessment assessment = assessmentService.getAssessmentById(id);
        return ResponseEntity.ok(ApiResponse.ok("Assessment details retrieved", assessment));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'COLLEGE_REP')")
    @Operation(summary = "Create an assessment with multiple choice or coding questions")
    public ResponseEntity<ApiResponse<Assessment>> createAssessment(@Valid @RequestBody Assessment assessment) {
        Assessment created = assessmentService.createAssessment(assessment);
        return ResponseEntity.ok(ApiResponse.created("Assessment created successfully", created));
    }

    @PostMapping("/submit")
    @Operation(summary = "Submit assessment answers and get instant scoring and competency evaluation")
    public ResponseEntity<ApiResponse<AssessmentResultDto>> submitAssessment(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody SubmitAssessmentRequest request) {
        AssessmentResultDto result = assessmentService.submitAssessment(userPrincipal.getId(), request);
        return ResponseEntity.ok(ApiResponse.ok("Assessment evaluated successfully", result));
    }

    @GetMapping("/my-attempts")
    @Operation(summary = "Get historical assessment attempts and scores for current user")
    public ResponseEntity<ApiResponse<List<AssessmentAttempt>>> getMyAttempts(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<AssessmentAttempt> attempts = assessmentService.getUserAttempts(userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.ok("User attempts retrieved", attempts));
    }

    @GetMapping("/skill-gap-analysis")
    @Operation(summary = "Analyze skill gap against a target industry role (e.g. 'Full Stack Developer', 'Data Scientist')")
    public ResponseEntity<ApiResponse<SkillGapReportDto>> analyzeSkillGap(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam String targetRole) {
        SkillGapReportDto report = assessmentService.performSkillGapAnalysis(userPrincipal.getId(), targetRole);
        return ResponseEntity.ok(ApiResponse.ok("Skill gap report generated", report));
    }
}
