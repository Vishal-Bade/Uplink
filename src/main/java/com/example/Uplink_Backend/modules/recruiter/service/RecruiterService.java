package com.example.Uplink_Backend.modules.recruiter.service;

import com.example.Uplink_Backend.common.exception.ResourceNotFoundException;
import com.example.Uplink_Backend.modules.auth.entity.Role;
import com.example.Uplink_Backend.modules.auth.entity.User;
import com.example.Uplink_Backend.modules.auth.repository.RoleRepository;
import com.example.Uplink_Backend.modules.auth.repository.UserRepository;
import com.example.Uplink_Backend.modules.recruiter.dto.RegisterCompanyRequest;
import com.example.Uplink_Backend.modules.recruiter.entity.Company;
import com.example.Uplink_Backend.modules.recruiter.entity.Recruiter;
import com.example.Uplink_Backend.modules.recruiter.repository.CompanyRepository;
import com.example.Uplink_Backend.modules.recruiter.repository.RecruiterRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class RecruiterService {

    private final CompanyRepository companyRepository;
    private final RecruiterRepository recruiterRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public RecruiterService(CompanyRepository companyRepository,
                            RecruiterRepository recruiterRepository,
                            UserRepository userRepository,
                            RoleRepository roleRepository) {
        this.companyRepository = companyRepository;
        this.recruiterRepository = recruiterRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Transactional
    public Recruiter registerOrJoinCompany(UUID userId, RegisterCompanyRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        // Add ROLE_RECRUITER to user if not present
        Role recruiterRole = roleRepository.findByName("ROLE_RECRUITER")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_RECRUITER", "Recruiter Role")));
        user.getRoles().add(recruiterRole);
        userRepository.save(user);

        // Find or create company
        Company company = companyRepository.findByNameIgnoreCase(request.getName().trim())
                .orElseGet(() -> {
                    Company c = new Company(request.getName().trim(), request.getType(), request.getWebsite(), request.getDescription());
                    c.setLogoUrl(request.getLogoUrl());
                    return companyRepository.save(c);
                });

        Optional<Recruiter> existingRecruiter = recruiterRepository.findByUserId(userId);
        if (existingRecruiter.isPresent()) {
            Recruiter r = existingRecruiter.get();
            r.setCompany(company);
            if (request.getDesignation() != null) r.setDesignation(request.getDesignation());
            return recruiterRepository.save(r);
        }

        Recruiter recruiter = new Recruiter(user, company, request.getDesignation());
        return recruiterRepository.save(recruiter);
    }

    public Recruiter getRecruiterByUserId(UUID userId) {
        return recruiterRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Recruiter profile not found for user: " + userId));
    }

    public Page<Company> getAllCompanies(Pageable pageable) {
        return companyRepository.findAll(pageable);
    }

    public Company getCompanyById(UUID id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company", "id", id));
    }

    @Transactional
    public Company verifyCompany(UUID companyId, boolean isVerified) {
        Company company = getCompanyById(companyId);
        company.setIsVerified(isVerified);
        return companyRepository.save(company);
    }

    @Transactional
    public Recruiter verifyRecruiter(UUID recruiterId, boolean isVerified) {
        Recruiter recruiter = recruiterRepository.findById(recruiterId)
                .orElseThrow(() -> new ResourceNotFoundException("Recruiter", "id", recruiterId));
        recruiter.setIsVerified(isVerified);
        return recruiterRepository.save(recruiter);
    }
}
