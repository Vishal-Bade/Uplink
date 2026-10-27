package com.example.Uplink_Backend.modules.opportunity.repository;

import com.example.Uplink_Backend.modules.opportunity.entity.Opportunity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OpportunityRepository extends JpaRepository<Opportunity, UUID> {
    Page<Opportunity> findByIsActiveTrue(Pageable pageable);
    Page<Opportunity> findByTypeAndIsActiveTrue(String type, Pageable pageable);
    List<Opportunity> findByCompanyId(UUID companyId);

    @Query("SELECT o FROM Opportunity o WHERE o.isActive = true " +
            "AND (:type IS NULL OR LOWER(o.type) = LOWER(:type)) " +
            "AND (:workMode IS NULL OR LOWER(o.workMode) = LOWER(:workMode)) " +
            "AND (:location IS NULL OR LOWER(o.location) LIKE LOWER(CONCAT('%', :location, '%'))) " +
            "AND (:keyword IS NULL OR LOWER(o.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(o.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Opportunity> searchOpportunities(@Param("type") String type,
                                          @Param("workMode") String workMode,
                                          @Param("location") String location,
                                          @Param("keyword") String keyword,
                                          Pageable pageable);

    long countByIsActiveTrue();
}
