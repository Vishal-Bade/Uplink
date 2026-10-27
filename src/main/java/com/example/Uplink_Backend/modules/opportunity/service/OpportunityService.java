package com.example.Uplink_Backend.modules.opportunity.service;

import com.example.Uplink_Backend.common.exception.ResourceNotFoundException;
import com.example.Uplink_Backend.modules.auth.entity.User;
import com.example.Uplink_Backend.modules.auth.repository.UserRepository;
import com.example.Uplink_Backend.modules.opportunity.dto.CreateOpportunityRequest;
import com.example.Uplink_Backend.modules.opportunity.dto.ExternalIngestionRequest;
import com.example.Uplink_Backend.modules.opportunity.entity.Opportunity;
import com.example.Uplink_Backend.modules.opportunity.entity.OpportunitySkill;
import com.example.Uplink_Backend.modules.opportunity.entity.OpportunitySource;
import com.example.Uplink_Backend.modules.opportunity.repository.OpportunityRepository;
import com.example.Uplink_Backend.modules.opportunity.repository.OpportunitySourceRepository;
import com.example.Uplink_Backend.modules.recruiter.entity.Company;
import com.example.Uplink_Backend.modules.recruiter.entity.Recruiter;
import com.example.Uplink_Backend.modules.recruiter.repository.CompanyRepository;
import com.example.Uplink_Backend.modules.recruiter.repository.RecruiterRepository;
import com.example.Uplink_Backend.modules.user.entity.Skill;
import com.example.Uplink_Backend.modules.user.repository.SkillRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.UUID;

@Service
public class OpportunityService {

    private final OpportunityRepository opportunityRepository;
    private final OpportunitySourceRepository sourceRepository;
    private final CompanyRepository companyRepository;
    private final RecruiterRepository recruiterRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;

    public OpportunityService(OpportunityRepository opportunityRepository,
                              OpportunitySourceRepository sourceRepository,
                              CompanyRepository companyRepository,
                              RecruiterRepository recruiterRepository,
                              UserRepository userRepository,
                              SkillRepository skillRepository) {
        this.opportunityRepository = opportunityRepository;
        this.sourceRepository = sourceRepository;
        this.companyRepository = companyRepository;
        this.recruiterRepository = recruiterRepository;
        this.userRepository = userRepository;
        this.skillRepository = skillRepository;
    }

    @Transactional
    public Opportunity createOpportunity(UUID userId, CreateOpportunityRequest request) {
        User creator = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Company company;
        if (request.getCompanyId() != null) {
            company = companyRepository.findById(request.getCompanyId())
                    .orElseThrow(() -> new ResourceNotFoundException("Company", "id", request.getCompanyId()));
        } else {
            // Find recruiter's company
            Recruiter recruiter = recruiterRepository.findByUserId(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Recruiter profile not found for user: " + userId));
            company = recruiter.getCompany();
        }

        Opportunity opp = new Opportunity();
        opp.setCompany(company);
        opp.setCreatedBy(creator);
        opp.setType(request.getType().toUpperCase());
        opp.setTitle(request.getTitle());
        opp.setDescription(request.getDescription());
        opp.setLocation(request.getLocation());
        opp.setWorkMode(request.getWorkMode() != null ? request.getWorkMode().toUpperCase() : "ONSITE");
        opp.setExperienceLevel(request.getExperienceLevel());
        opp.setStipendMin(request.getStipendMin());
        opp.setStipendMax(request.getStipendMax());
        opp.setApplicationUrl(request.getApplicationUrl());
        opp.setStartDate(request.getStartDate());
        opp.setDeadline(request.getDeadline());
        opp.setIsActive(true);
        opp.setIsVerified(false);

        if (request.getRequiredSkills() != null) {
            for (CreateOpportunityRequest.SkillRequirementDto reqSkill : request.getRequiredSkills()) {
                Skill skill = skillRepository.findByNameIgnoreCase(reqSkill.getSkillName().trim())
                        .orElseGet(() -> skillRepository.save(new Skill(reqSkill.getSkillName().trim(), "Technical")));

                OpportunitySkill oppSkill = new OpportunitySkill(opp, skill, reqSkill.isMandatory(), reqSkill.getProficiencyRequired());
                opp.getRequiredSkills().add(oppSkill);
            }
        }

        return opportunityRepository.save(opp);
    }

    @Transactional
    public Opportunity ingestExternalOpportunity(UUID adminOrSystemId, ExternalIngestionRequest request) {
        User systemUser = userRepository.findById(adminOrSystemId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", adminOrSystemId));

        Company company = companyRepository.findByNameIgnoreCase(request.getCompanyName().trim())
                .orElseGet(() -> companyRepository.save(new Company(request.getCompanyName().trim(), "COMPANY", null, "Auto-created via external ingestion")));

        Opportunity opp = new Opportunity();
        opp.setCompany(company);
        opp.setCreatedBy(systemUser);
        opp.setType(request.getType() != null ? request.getType().toUpperCase() : "JOB");
        opp.setTitle(request.getTitle());
        opp.setDescription(request.getDescription());
        opp.setLocation(request.getLocation());
        opp.setWorkMode(request.getWorkMode());
        opp.setStipendMin(request.getStipendMin());
        opp.setStipendMax(request.getStipendMax());
        opp.setApplicationUrl(request.getApplicationUrl() != null ? request.getApplicationUrl() : request.getSourceUrl());
        opp.setIsActive(true);
        opp.setIsVerified(true);

        if (request.getSkills() != null) {
            for (String sName : request.getSkills()) {
                Skill skill = skillRepository.findByNameIgnoreCase(sName.trim())
                        .orElseGet(() -> skillRepository.save(new Skill(sName.trim(), "General")));
                opp.getRequiredSkills().add(new OpportunitySkill(opp, skill, false, "INTERMEDIATE"));
            }
        }

        Opportunity savedOpp = opportunityRepository.save(opp);

        OpportunitySource source = new OpportunitySource(savedOpp, request.getSourceName(), request.getSourceUrl());
        sourceRepository.save(source);

        return savedOpp;
    }

    public Page<Opportunity> searchOpportunities(String type, String workMode, String location, String keyword, Pageable pageable) {
        return opportunityRepository.searchOpportunities(type, workMode, location, keyword, pageable);
    }

    public Opportunity getOpportunityById(UUID id) {
        return opportunityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Opportunity", "id", id));
    }

    @Transactional
    public Opportunity updateActiveStatus(UUID id, boolean isActive) {
        Opportunity opp = getOpportunityById(id);
        opp.setIsActive(isActive);
        return opportunityRepository.save(opp);
    }

    @Transactional
    public Opportunity verifyOpportunity(UUID id, boolean isVerified) {
        Opportunity opp = getOpportunityById(id);
        opp.setIsVerified(isVerified);
        return opportunityRepository.save(opp);
    }
}
