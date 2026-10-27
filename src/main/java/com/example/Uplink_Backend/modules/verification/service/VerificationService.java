package com.example.Uplink_Backend.modules.verification.service;

import com.example.Uplink_Backend.common.exception.ResourceNotFoundException;
import com.example.Uplink_Backend.modules.auth.entity.User;
import com.example.Uplink_Backend.modules.auth.entity.UserVerification;
import com.example.Uplink_Backend.modules.auth.repository.UserRepository;
import com.example.Uplink_Backend.modules.auth.repository.UserVerificationRepository;
import com.example.Uplink_Backend.modules.recruiter.entity.Recruiter;
import com.example.Uplink_Backend.modules.recruiter.repository.RecruiterRepository;
import com.example.Uplink_Backend.modules.verification.dto.VerificationReviewRequest;
import com.example.Uplink_Backend.modules.verification.dto.VerificationSubmitRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class VerificationService {

    private final UserVerificationRepository verificationRepository;
    private final UserRepository userRepository;
    private final RecruiterRepository recruiterRepository;

    public VerificationService(UserVerificationRepository verificationRepository,
                               UserRepository userRepository,
                               RecruiterRepository recruiterRepository) {
        this.verificationRepository = verificationRepository;
        this.userRepository = userRepository;
        this.recruiterRepository = recruiterRepository;
    }

    @Transactional
    public UserVerification submitVerification(UUID userId, VerificationSubmitRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        UserVerification uv = new UserVerification();
        uv.setUser(user);
        uv.setType(request.getType().toUpperCase());
        uv.setStatus("PENDING");
        uv.setDocumentUrl(request.getDocumentUrl());
        uv.setRemarks(request.getRemarks());

        return verificationRepository.save(uv);
    }

    public List<UserVerification> getMyVerifications(UUID userId) {
        return verificationRepository.findByUserId(userId);
    }

    public Page<UserVerification> getPendingVerifications(Pageable pageable) {
        return verificationRepository.findByStatus("PENDING", pageable);
    }

    @Transactional
    public UserVerification reviewVerification(UUID adminId, UUID verificationId, VerificationReviewRequest request) {
        UserVerification uv = verificationRepository.findById(verificationId)
                .orElseThrow(() -> new ResourceNotFoundException("UserVerification", "id", verificationId));

        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin User", "id", adminId));

        String newStatus = request.getStatus().toUpperCase();
        uv.setStatus(newStatus);
        uv.setVerifiedBy(admin);
        uv.setVerifiedAt(Instant.now());
        uv.setRemarks(request.getRemarks());

        UserVerification updated = verificationRepository.save(uv);

        if ("APPROVED".equalsIgnoreCase(newStatus)) {
            User candidate = uv.getUser();
            candidate.setIsVerified(true);
            userRepository.save(candidate);

            // If user is recruiter, mark recruiter profile verified
            Optional<Recruiter> recruiter = recruiterRepository.findByUserId(candidate.getId());
            if (recruiter.isPresent()) {
                Recruiter r = recruiter.get();
                r.setIsVerified(true);
                recruiterRepository.save(r);
            }
        }

        return updated;
    }
}
