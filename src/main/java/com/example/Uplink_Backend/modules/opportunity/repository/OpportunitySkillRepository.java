package com.example.Uplink_Backend.modules.opportunity.repository;

import com.example.Uplink_Backend.modules.opportunity.entity.OpportunitySkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OpportunitySkillRepository extends JpaRepository<OpportunitySkill, UUID> {
    List<OpportunitySkill> findByOpportunityId(UUID opportunityId);
}
