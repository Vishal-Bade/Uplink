package com.example.Uplink_Backend.modules.user.service;

import com.example.Uplink_Backend.common.exception.ResourceNotFoundException;
import com.example.Uplink_Backend.modules.auth.entity.User;
import com.example.Uplink_Backend.modules.auth.repository.UserRepository;
import com.example.Uplink_Backend.modules.user.dto.*;
import com.example.Uplink_Backend.modules.user.entity.*;
import com.example.Uplink_Backend.modules.user.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class UserProfileService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final EducationRepository educationRepository;
    private final SkillRepository skillRepository;
    private final UserSkillRepository userSkillRepository;
    private final ProjectRepository projectRepository;
    private final ExperienceRepository experienceRepository;
    private final CertificationRepository certificationRepository;
    private final AchievementRepository achievementRepository;

    public UserProfileService(UserRepository userRepository,
                              ProfileRepository profileRepository,
                              EducationRepository educationRepository,
                              SkillRepository skillRepository,
                              UserSkillRepository userSkillRepository,
                              ProjectRepository projectRepository,
                              ExperienceRepository experienceRepository,
                              CertificationRepository certificationRepository,
                              AchievementRepository achievementRepository) {
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
        this.educationRepository = educationRepository;
        this.skillRepository = skillRepository;
        this.userSkillRepository = userSkillRepository;
        this.projectRepository = projectRepository;
        this.experienceRepository = experienceRepository;
        this.certificationRepository = certificationRepository;
        this.achievementRepository = achievementRepository;
    }

    public Profile getOrCreateProfile(UUID userId) {
        return profileRepository.findByUserId(userId).orElseGet(() -> {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
            Profile profile = new Profile(user);
            return profileRepository.save(profile);
        });
    }

    @Transactional
    public Profile updateProfile(UUID userId, ProfileUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        if (request.getFullName() != null && !request.getFullName().isBlank()) {
            user.setFullName(request.getFullName().trim());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone().trim());
        }
        userRepository.save(user);

        Profile profile = getOrCreateProfile(userId);
        if (request.getHeadline() != null) profile.setHeadline(request.getHeadline());
        if (request.getBio() != null) profile.setBio(request.getBio());
        if (request.getLocation() != null) profile.setLocation(request.getLocation());
        if (request.getProfilePicture() != null) profile.setProfilePicture(request.getProfilePicture());

        return profileRepository.save(profile);
    }

    public DigitalPortfolioDto getDigitalPortfolio(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        Profile profile = getOrCreateProfile(userId);
        List<Education> educations = educationRepository.findByUserIdOrderByStartYearDesc(userId);
        List<UserSkill> skills = userSkillRepository.findByUserId(userId);
        List<Project> projects = projectRepository.findByUserIdOrderByCreatedAtDesc(userId);
        List<Experience> experiences = experienceRepository.findByUserIdOrderByStartDateDesc(userId);
        List<Certification> certifications = certificationRepository.findByUserIdOrderByIssueDateDesc(userId);
        List<Achievement> achievements = achievementRepository.findByUserIdOrderByDateDesc(userId);

        return new DigitalPortfolioDto(user.getId(), user.getFullName(), user.getEmail(), user.getPhone(),
                profile, educations, skills, projects, experiences, certifications, achievements);
    }

    // Education
    @Transactional
    public Education addEducation(UUID userId, Education education) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        education.setUser(user);
        return educationRepository.save(education);
    }

    public List<Education> getEducations(UUID userId) {
        return educationRepository.findByUserIdOrderByStartYearDesc(userId);
    }

    @Transactional
    public void deleteEducation(UUID userId, UUID educationId) {
        Education edu = educationRepository.findById(educationId)
                .orElseThrow(() -> new ResourceNotFoundException("Education", "id", educationId));
        if (edu.getUser().getId().equals(userId)) {
            educationRepository.delete(edu);
        }
    }

    // Skills
    @Transactional
    public UserSkill addSkill(UUID userId, AddSkillRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Skill skill = skillRepository.findByNameIgnoreCase(request.getSkillName().trim())
                .orElseGet(() -> skillRepository.save(new Skill(request.getSkillName().trim(), request.getCategory() != null ? request.getCategory() : "General")));

        UserSkill userSkill = userSkillRepository.findByUserIdAndSkillId(userId, skill.getId())
                .orElseGet(() -> {
                    UserSkill us = new UserSkill();
                    us.setUser(user);
                    us.setSkill(skill);
                    return us;
                });

        userSkill.setProficiency(request.getProficiency());
        userSkill.setYearsOfExp(request.getYearsOfExp());
        return userSkillRepository.save(userSkill);
    }

    public List<UserSkill> getUserSkills(UUID userId) {
        return userSkillRepository.findByUserId(userId);
    }

    @Transactional
    public void deleteSkill(UUID userId, UUID userSkillId) {
        UserSkill us = userSkillRepository.findById(userSkillId)
                .orElseThrow(() -> new ResourceNotFoundException("UserSkill", "id", userSkillId));
        if (us.getUser().getId().equals(userId)) {
            userSkillRepository.delete(us);
        }
    }

    // Projects
    @Transactional
    public Project addProject(UUID userId, Project project) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        project.setUser(user);
        return projectRepository.save(project);
    }

    public List<Project> getProjects(UUID userId) {
        return projectRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Transactional
    public void deleteProject(UUID userId, UUID projectId) {
        Project proj = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));
        if (proj.getUser().getId().equals(userId)) {
            projectRepository.delete(proj);
        }
    }

    // Experiences
    @Transactional
    public Experience addExperience(UUID userId, Experience experience) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        experience.setUser(user);
        return experienceRepository.save(experience);
    }

    public List<Experience> getExperiences(UUID userId) {
        return experienceRepository.findByUserIdOrderByStartDateDesc(userId);
    }

    @Transactional
    public void deleteExperience(UUID userId, UUID experienceId) {
        Experience exp = experienceRepository.findById(experienceId)
                .orElseThrow(() -> new ResourceNotFoundException("Experience", "id", experienceId));
        if (exp.getUser().getId().equals(userId)) {
            experienceRepository.delete(exp);
        }
    }

    // Certifications
    @Transactional
    public Certification addCertification(UUID userId, Certification cert) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        cert.setUser(user);
        return certificationRepository.save(cert);
    }

    public List<Certification> getCertifications(UUID userId) {
        return certificationRepository.findByUserIdOrderByIssueDateDesc(userId);
    }

    @Transactional
    public void deleteCertification(UUID userId, UUID certId) {
        Certification cert = certificationRepository.findById(certId)
                .orElseThrow(() -> new ResourceNotFoundException("Certification", "id", certId));
        if (cert.getUser().getId().equals(userId)) {
            certificationRepository.delete(cert);
        }
    }

    // Achievements
    @Transactional
    public Achievement addAchievement(UUID userId, Achievement achievement) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        achievement.setUser(user);
        return achievementRepository.save(achievement);
    }

    public List<Achievement> getAchievements(UUID userId) {
        return achievementRepository.findByUserIdOrderByDateDesc(userId);
    }

    @Transactional
    public void deleteAchievement(UUID userId, UUID achievementId) {
        Achievement ach = achievementRepository.findById(achievementId)
                .orElseThrow(() -> new ResourceNotFoundException("Achievement", "id", achievementId));
        if (ach.getUser().getId().equals(userId)) {
            achievementRepository.delete(ach);
        }
    }
}
