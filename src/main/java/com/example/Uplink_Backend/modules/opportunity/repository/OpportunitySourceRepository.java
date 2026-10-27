package com.example.Uplink_Backend.modules.opportunity.repository;

import com.example.Uplink_Backend.modules.opportunity.entity.OpportunitySource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OpportunitySourceRepository extends JpaRepository<OpportunitySource, UUID> {
    List<OpportunitySource> findByOpportunityId(UUID opportunityId);
}
