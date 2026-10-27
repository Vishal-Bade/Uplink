package com.example.Uplink_Backend.modules.user.dto;

import com.example.Uplink_Backend.modules.user.entity.*;
import java.util.List;
import java.util.UUID;

public class DigitalPortfolioDto {
    private UUID userId;
    private String fullName;
    private String email;
    private String phone;
    private Profile profile;
    private List<Education> education;
    private List<UserSkill> skills;
    private List<Project> projects;
    private List<Experience> experiences;
    private List<Certification> certifications;
    private List<Achievement> achievements;

    public DigitalPortfolioDto() {}

    public DigitalPortfolioDto(UUID userId, String fullName, String email, String phone, Profile profile,
                               List<Education> education, List<UserSkill> skills, List<Project> projects,
                               List<Experience> experiences, List<Certification> certifications,
                               List<Achievement> achievements) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.profile = profile;
        this.education = education;
        this.skills = skills;
        this.projects = projects;
        this.experiences = experiences;
        this.certifications = certifications;
        this.achievements = achievements;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public List<Education> getEducation() {
        return education;
    }

    public void setEducation(List<Education> education) {
        this.education = education;
    }

    public List<UserSkill> getSkills() {
        return skills;
    }

    public void setSkills(List<UserSkill> skills) {
        this.skills = skills;
    }

    public List<Project> getProjects() {
        return projects;
    }

    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }

    public List<Experience> getExperiences() {
        return experiences;
    }

    public void setExperiences(List<Experience> experiences) {
        this.experiences = experiences;
    }

    public List<Certification> getCertifications() {
        return certifications;
    }

    public void setCertifications(List<Certification> certifications) {
        this.certifications = certifications;
    }

    public List<Achievement> getAchievements() {
        return achievements;
    }

    public void setAchievements(List<Achievement> achievements) {
        this.achievements = achievements;
    }
}
