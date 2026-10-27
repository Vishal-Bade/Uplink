package com.example.Uplink_Backend.modules.admin.service;

import com.example.Uplink_Backend.common.exception.ResourceNotFoundException;
import com.example.Uplink_Backend.modules.admin.dto.AdminDashboardStatsDto;
import com.example.Uplink_Backend.modules.admin.dto.UserStatusUpdateRequest;
import com.example.Uplink_Backend.modules.admin.entity.AuditLog;
import com.example.Uplink_Backend.modules.admin.repository.AuditLogRepository;
import com.example.Uplink_Backend.modules.application.repository.ApplicationRepository;
import com.example.Uplink_Backend.modules.auth.entity.Role;
import com.example.Uplink_Backend.modules.auth.entity.User;
import com.example.Uplink_Backend.modules.auth.repository.RoleRepository;
import com.example.Uplink_Backend.modules.auth.repository.UserRepository;
import com.example.Uplink_Backend.modules.auth.repository.UserVerificationRepository;
import com.example.Uplink_Backend.modules.content.repository.PostRepository;
import com.example.Uplink_Backend.modules.opportunity.repository.OpportunityRepository;
import com.example.Uplink_Backend.modules.recruiter.repository.CompanyRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final OpportunityRepository opportunityRepository;
    private final CompanyRepository companyRepository;
    private final ApplicationRepository applicationRepository;
    private final UserVerificationRepository verificationRepository;
    private final PostRepository postRepository;
    private final AuditLogRepository auditLogRepository;

    public AdminService(UserRepository userRepository,
                        RoleRepository roleRepository,
                        OpportunityRepository opportunityRepository,
                        CompanyRepository companyRepository,
                        ApplicationRepository applicationRepository,
                        UserVerificationRepository verificationRepository,
                        PostRepository postRepository,
                        AuditLogRepository auditLogRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.opportunityRepository = opportunityRepository;
        this.companyRepository = companyRepository;
        this.applicationRepository = applicationRepository;
        this.verificationRepository = verificationRepository;
        this.postRepository = postRepository;
        this.auditLogRepository = auditLogRepository;
    }

    public AdminDashboardStatsDto getDashboardStats() {
        long totalUsers = userRepository.count();
        long activeUsers = userRepository.countByIsActiveTrue();
        long totalCompanies = companyRepository.count();
        long activeOpportunities = opportunityRepository.countByIsActiveTrue();
        long totalApplications = applicationRepository.count();
        long pendingVerifications = verificationRepository.countByStatus("PENDING");
        long selectedPlacements = applicationRepository.countByStatus("SELECTED");

        Map<String, Long> appStatusMap = new HashMap<>();
        for (String status : List.of("APPLIED", "UNDER_REVIEW", "SHORTLISTED", "INTERVIEW", "SELECTED", "REJECTED")) {
            appStatusMap.put(status, applicationRepository.countByStatus(status));
        }

        return new AdminDashboardStatsDto(
                totalUsers,
                activeUsers,
                totalCompanies,
                activeOpportunities,
                totalApplications,
                pendingVerifications,
                selectedPlacements,
                appStatusMap
        );
    }

    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findByIsDeletedFalse(pageable);
    }

    @Transactional
    public User updateUserStatus(UUID adminId, UUID targetUserId, UserStatusUpdateRequest request) {
        User target = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", targetUserId));
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin", "id", adminId));

        if (request.getIsActive() != null) target.setIsActive(request.getIsActive());
        if (request.getIsDeleted() != null) target.setIsDeleted(request.getIsDeleted());
        if (request.getIsVerified() != null) target.setIsVerified(request.getIsVerified());

        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            Set<Role> newRoles = new HashSet<>();
            for (String rName : request.getRoles()) {
                String normalized = rName.toUpperCase().trim();
                if (!normalized.startsWith("ROLE_")) normalized = "ROLE_" + normalized;
                final String finalRoleName = normalized;
                Role role = roleRepository.findByName(finalRoleName)
                        .orElseGet(() -> roleRepository.save(new Role(finalRoleName, "Role")));
                newRoles.add(role);
            }
            target.setRoles(newRoles);
        }

        User saved = userRepository.save(target);

        // Record audit log
        AuditLog log = new AuditLog(admin, "UPDATE_USER_STATUS", "User", target.getId(),
                String.format("{\"isActive\": %s, \"isDeleted\": %s, \"isVerified\": %s}",
                        request.getIsActive(), request.getIsDeleted(), request.getIsVerified()));
        auditLogRepository.save(log);

        return saved;
    }

    @Transactional
    public void moderatePost(UUID adminId, UUID postId, String reason) {
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin", "id", adminId));

        postRepository.deleteById(postId);

        AuditLog log = new AuditLog(admin, "MODERATE_DELETE_POST", "Post", postId,
                "{\"reason\": \"" + reason + "\"}");
        auditLogRepository.save(log);
    }

    public Page<AuditLog> getAuditLogs(Pageable pageable) {
        return auditLogRepository.findAllByOrderByCreatedAtDesc(pageable);
    }
}
