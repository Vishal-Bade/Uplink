package com.example.Uplink_Backend.modules.recommendation.controller;

import com.example.Uplink_Backend.common.dto.ApiResponse;
import com.example.Uplink_Backend.common.security.UserPrincipal;
import com.example.Uplink_Backend.modules.recommendation.dto.CareerPathDto;
import com.example.Uplink_Backend.modules.recommendation.dto.OpportunityRecommendationDto;
import com.example.Uplink_Backend.modules.recommendation.dto.SkillRecommendationDto;
import com.example.Uplink_Backend.modules.recommendation.service.RecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
@Tag(name = "6. Recommendation Module", description = "Endpoints for Opportunity Recommendations, Career Path Suggestions, and In-Demand Skills")
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping("/opportunities")
    @Operation(summary = "Get AI-ranked opportunity recommendations personalized to current user skills")
    public ResponseEntity<ApiResponse<List<OpportunityRecommendationDto>>> getOpportunityRecommendations(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<OpportunityRecommendationDto> recommendations = recommendationService.getOpportunityRecommendations(userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.ok("Personalized opportunities retrieved", recommendations));
    }

    @GetMapping("/career-paths")
    @Operation(summary = "Get personalized career path roadmaps, salary benchmarks, and milestones")
    public ResponseEntity<ApiResponse<List<CareerPathDto>>> getCareerPathSuggestions(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<CareerPathDto> careerPaths = recommendationService.getCareerPathSuggestions(userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.ok("Career path suggestions retrieved", careerPaths));
    }

    @GetMapping("/skills")
    @Operation(summary = "Get recommended skills to learn based on user profile and market demand")
    public ResponseEntity<ApiResponse<List<SkillRecommendationDto>>> getSkillRecommendations(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<SkillRecommendationDto> skills = recommendationService.getSkillRecommendations(userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.ok("Skill recommendations retrieved", skills));
    }
}
