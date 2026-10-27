package com.example.Uplink_Backend.modules.recruiter.repository;

import com.example.Uplink_Backend.modules.recruiter.entity.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CompanyRepository extends JpaRepository<Company, UUID> {
    Optional<Company> findByNameIgnoreCase(String name);
    Page<Company> findByIsVerifiedTrue(Pageable pageable);
    long countByIsVerifiedTrue();
}
