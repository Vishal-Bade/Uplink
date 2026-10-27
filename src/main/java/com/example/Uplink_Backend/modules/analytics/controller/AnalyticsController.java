package com.example.Uplink_Backend.modules.analytics.controller;

import com.example.Uplink_Backend.common.dto.ApiResponse;
import com.example.Uplink_Backend.common.security.UserPrincipal;
import com.example.Uplink_Backend.modules.analytics.dto.PlacementAnalyticsDto;
import com.example.Uplink_Backend.modules.analytics.dto.UserActivityDto;
import com.example.Uplink_Backend.modules.analytics.service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/analytics")
@Tag(name = "15. Analytics Module", description = "Endpoints for Platform Metrics, Placement Statistics, User Activity Insights, and Reports Export")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/placements")
    @Operation(summary = "Get overall platform placement analytics, conversion rates, and hiring trends")
    public ResponseEntity<ApiResponse<PlacementAnalyticsDto>> getPlacementAnalytics() {
        PlacementAnalyticsDto analytics = analyticsService.getPlacementAnalytics();
        return ResponseEntity.ok(ApiResponse.ok("Placement analytics retrieved", analytics));
    }

    @GetMapping("/user-activity")
    @Operation(summary = "Get personalized user funnel activity (applications, interviews, offers, assessments)")
    public ResponseEntity<ApiResponse<UserActivityDto>> getUserActivity(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        UserActivityDto activity = analyticsService.getUserActivity(userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.ok("User activity retrieved", activity));
    }

    @GetMapping("/export/csv")
    @Operation(summary = "Export platform placement & recruitment KPI report in CSV format")
    public ResponseEntity<byte[]> exportCsvReport() {
        String csv = analyticsService.exportPlacementReportCsv();
        byte[] bytes = csv.getBytes(StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"uplink_placement_report.csv\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(bytes);
    }
}
