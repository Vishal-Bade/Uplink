package com.example.Uplink_Backend.modules.opportunity.entity;

import com.example.Uplink_Backend.modules.user.entity.Skill;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "opportunity_skills", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"opportunity_id", "skill_id"})
})
public class OpportunitySkill {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "opportunity_id", nullable = false)
    private Opportunity opportunity;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    @Column(name = "is_mandatory")
    private Boolean isMandatory = false;

    @Column(name = "proficiency_required", length = 50)
    private String proficiencyRequired; // BEGINNER, INTERMEDIATE, ADVANCED, EXPERT

    public OpportunitySkill() {}

    public OpportunitySkill(Opportunity opportunity, Skill skill, Boolean isMandatory, String proficiencyRequired) {
        this.opportunity = opportunity;
        this.skill = skill;
        this.isMandatory = isMandatory;
        this.proficiencyRequired = proficiencyRequired;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Opportunity getOpportunity() {
        return opportunity;
    }

    public void setOpportunity(Opportunity opportunity) {
        this.opportunity = opportunity;
    }

    public Skill getSkill() {
        return skill;
    }

    public void setSkill(Skill skill) {
        this.skill = skill;
    }

    public Boolean getIsMandatory() {
        return isMandatory;
    }

    public void setIsMandatory(Boolean isMandatory) {
        this.isMandatory = isMandatory;
    }

    public String getProficiencyRequired() {
        return proficiencyRequired;
    }

    public void setProficiencyRequired(String proficiencyRequired) {
        this.proficiencyRequired = proficiencyRequired;
    }
}
