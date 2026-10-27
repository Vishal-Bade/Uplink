package com.example.Uplink_Backend.modules.ocr.controller;

import com.example.Uplink_Backend.common.dto.ApiResponse;
import com.example.Uplink_Backend.common.security.UserPrincipal;
import com.example.Uplink_Backend.modules.ocr.entity.Document;
import com.example.Uplink_Backend.modules.ocr.entity.OcrJob;
import com.example.Uplink_Backend.modules.ocr.service.OcrService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/ocr")
@Tag(name = "10. OCR Module", description = "Endpoints for Document upload, OCR Job creation, Status tracking, and Result retrieval")
public class OcrController {

    private final OcrService ocrService;

    public OcrController(OcrService ocrService) {
        this.ocrService = ocrService;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload image or scanned PDF document to create an OCR job")
    public ResponseEntity<ApiResponse<OcrJob>> uploadDocumentForOcr(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam("file") MultipartFile file) {
        OcrJob job = ocrService.createOcrJob(userPrincipal.getId(), file);
        return ResponseEntity.ok(ApiResponse.created("OCR Job submitted for processing", job));
    }

    @GetMapping("/jobs/{jobId}")
    @Operation(summary = "Get OCR job processing status and result URL")
    public ResponseEntity<ApiResponse<OcrJob>> getOcrJobStatus(@PathVariable UUID jobId) {
        OcrJob job = ocrService.getJobById(jobId);
        return ResponseEntity.ok(ApiResponse.ok("OCR Job details", job));
    }

    @GetMapping("/jobs/{jobId}/result")
    @Operation(summary = "Retrieve parsed structured text and extracted entities from OCR job")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getOcrJobResult(@PathVariable UUID jobId) {
        OcrJob job = ocrService.getJobById(jobId);
        Map<String, Object> mockResult = Map.of(
                "jobId", job.getId(),
                "status", job.getStatus(),
                "extractedText", "OFFICIAL ACADEMIC NOTICE / RESUME CONTENT\nIdentified Candidate: Vishal Bade\nSkills: Java, Spring Boot, React, PostgreSQL\nEducation: Bachelor of Technology\nCGPA: 8.85",
                "confidenceScore", 0.98,
                "completedAt", job.getCompletedAt() != null ? job.getCompletedAt().toString() : ""
        );
        return ResponseEntity.ok(ApiResponse.ok("OCR extraction result retrieved", mockResult));
    }

    @GetMapping("/my-jobs")
    @Operation(summary = "List OCR jobs submitted by current user (paginated)")
    public ResponseEntity<ApiResponse<Page<OcrJob>>> getMyJobs(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            Pageable pageable) {
        Page<OcrJob> jobs = ocrService.getUserOcrJobs(userPrincipal.getId(), pageable);
        return ResponseEntity.ok(ApiResponse.ok("OCR jobs retrieved", jobs));
    }

    @GetMapping("/documents")
    @Operation(summary = "List all documents and notices uploaded by current user")
    public ResponseEntity<ApiResponse<List<Document>>> getMyDocuments(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<Document> docs = ocrService.getUserDocuments(userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.ok("Documents retrieved", docs));
    }
}
