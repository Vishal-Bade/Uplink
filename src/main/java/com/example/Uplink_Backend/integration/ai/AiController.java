package com.example.Uplink_Backend.integration.ai;

import com.example.Uplink_Backend.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@Tag(name = "AI / Processing Services", description = "Endpoints for OCR Processing, Resume Parsing, Skill Extraction, NLP & Recommendation Engine")
public class AiController {

    private final FastApiAiServiceClient aiServiceClient;

    public AiController(FastApiAiServiceClient aiServiceClient) {
        this.aiServiceClient = aiServiceClient;
    }

    @PostMapping("/extract-skills")
    @Operation(summary = "AI NLP Skill Extraction: extract technical competencies from unstructured text")
    public ResponseEntity<ApiResponse<List<String>>> extractSkills(@RequestBody Map<String, String> request) {
        String text = request.getOrDefault("text", "");
        List<String> skills = aiServiceClient.extractSkills(text);
        return ResponseEntity.ok(ApiResponse.ok("Skills extracted successfully", skills));
    }

    @PostMapping("/parse-resume")
    @Operation(summary = "AI Resume Parsing & ATS Enhancement: evaluate resume text and get optimization tips")
    public ResponseEntity<ApiResponse<Map<String, Object>>> parseResume(@RequestBody Map<String, String> request) {
        String resumeText = request.getOrDefault("resumeText", "");
        String targetRole = request.getOrDefault("targetRole", "Software Engineer");
        Map<String, Object> result = aiServiceClient.parseAndEnhanceResume(resumeText, targetRole);
        return ResponseEntity.ok(ApiResponse.ok("Resume parsed and enhanced", result));
    }

    @PostMapping("/parse-opportunity")
    @Operation(summary = "NLP Opportunity Parsing: parse unstructured job descriptions into structured metadata")
    public ResponseEntity<ApiResponse<Map<String, Object>>> parseOpportunity(@RequestBody Map<String, String> request) {
        String rawText = request.getOrDefault("rawText", "");
        Map<String, Object> result = aiServiceClient.parseOpportunity(rawText);
        return ResponseEntity.ok(ApiResponse.ok("Opportunity parsed", result));
    }

    @PostMapping("/recommendation-engine")
    @Operation(summary = "AI Recommendation Engine: calculate Jaccard & weighted compatibility score")
    public ResponseEntity<ApiResponse<Map<String, Object>>> rankRecommendation(@RequestBody Map<String, List<String>> request) {
        List<String> candidateSkills = request.getOrDefault("candidateSkills", List.of());
        List<String> oppSkills = request.getOrDefault("opportunitySkills", List.of());
        Map<String, Object> result = aiServiceClient.rankRecommendations(candidateSkills, oppSkills);
        return ResponseEntity.ok(ApiResponse.ok("Recommendation score evaluated", result));
    }

    @PostMapping("/llm-query")
    @Operation(summary = "LLM / NLP Services: generate conversational career advice, interview questions, or bullet improvement")
    public ResponseEntity<ApiResponse<Map<String, Object>>> queryLlm(@RequestBody Map<String, String> request) {
        String prompt = request.getOrDefault("prompt", "");
        String taskType = request.getOrDefault("taskType", "CAREER_ADVICE");
        Map<String, Object> result = aiServiceClient.queryLlm(prompt, taskType);
        return ResponseEntity.ok(ApiResponse.ok("AI response generated", result));
    }
}
