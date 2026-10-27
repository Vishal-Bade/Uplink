package com.example.Uplink_Backend.modules.admin.controller;

import com.example.Uplink_Backend.common.dto.ApiResponse;
import com.example.Uplink_Backend.common.security.UserPrincipal;
import com.example.Uplink_Backend.modules.admin.dto.AdminDashboardStatsDto;
import com.example.Uplink_Backend.modules.admin.dto.UserStatusUpdateRequest;
import com.example.Uplink_Backend.modules.admin.entity.AuditLog;
import com.example.Uplink_Backend.modules.admin.service.AdminService;
import com.example.Uplink_Backend.modules.auth.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAnyRole('ADMIN', 'ROLE_ADMIN')")
@Tag(name = "14. Admin Module", description = "Endpoints for Platform Dashboard & Analytics, User Management, Content Moderation, and System Audit Logs")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/dashboard")
    @Operation(summary = "Get high-level platform analytics & KPI statistics")
    public ResponseEntity<ApiResponse<AdminDashboardStatsDto>> getDashboardStats() {
        AdminDashboardStatsDto stats = adminService.getDashboardStats();
        return ResponseEntity.ok(ApiResponse.ok("Platform dashboard stats retrieved", stats));
    }

    @GetMapping("/users")
    @Operation(summary = "Get all registered users across the platform (paginated)")
    public ResponseEntity<ApiResponse<Page<User>>> getAllUsers(Pageable pageable) {
        Page<User> users = adminService.getAllUsers(pageable);
        return ResponseEntity.ok(ApiResponse.ok("Users retrieved", users));
    }

    @PatchMapping("/users/{id}/status")
    @Operation(summary = "Update user status (activation, deactivation, roles, or soft-delete)")
    public ResponseEntity<ApiResponse<User>> updateUserStatus(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable UUID id,
            @RequestBody UserStatusUpdateRequest request) {
        User updated = adminService.updateUserStatus(userPrincipal.getId(), id, request);
        return ResponseEntity.ok(ApiResponse.ok("User status updated", updated));
    }

    @DeleteMapping("/moderate/post/{postId}")
    @Operation(summary = "Content moderation: delete inappropriate post and record audit log")
    public ResponseEntity<ApiResponse<Void>> moderatePost(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable UUID postId,
            @RequestParam(defaultValue = "Content policy violation") String reason) {
        adminService.moderatePost(userPrincipal.getId(), postId, reason);
        return ResponseEntity.ok(ApiResponse.ok("Post removed via moderation", null));
    }

    @GetMapping("/audit-logs")
    @Operation(summary = "View platform security and audit logs (paginated)")
    public ResponseEntity<ApiResponse<Page<AuditLog>>> getAuditLogs(Pageable pageable) {
        Page<AuditLog> logs = adminService.getAuditLogs(pageable);
        return ResponseEntity.ok(ApiResponse.ok("Audit logs retrieved", logs));
    }
}
