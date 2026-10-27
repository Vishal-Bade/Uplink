package com.example.Uplink_Backend.modules.user.dto;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

public class AddSkillRequest {
    @NotBlank(message = "Skill name is required")
    private String skillName;

    private String category;
    private String proficiency = "INTERMEDIATE"; // BEGINNER, INTERMEDIATE, ADVANCED, EXPERT
    private BigDecimal yearsOfExp = BigDecimal.ZERO;

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getProficiency() {
        return proficiency;
    }

    public void setProficiency(String proficiency) {
        this.proficiency = proficiency;
    }

    public BigDecimal getYearsOfExp() {
        return yearsOfExp;
    }

    public void setYearsOfExp(BigDecimal yearsOfExp) {
        this.yearsOfExp = yearsOfExp;
    }
}
