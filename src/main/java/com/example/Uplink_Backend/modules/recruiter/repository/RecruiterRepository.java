package com.example.Uplink_Backend.modules.recruiter.repository;

import com.example.Uplink_Backend.modules.recruiter.entity.Recruiter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RecruiterRepository extends JpaRepository<Recruiter, UUID> {
    Optional<Recruiter> findByUserId(UUID userId);
    List<Recruiter> findByCompanyId(UUID companyId);
    Page<Recruiter> findByIsVerifiedTrue(Pageable pageable);
    boolean existsByUserId(UUID userId);
}
