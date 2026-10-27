package com.example.Uplink_Backend.modules.recommendation.service;

import com.example.Uplink_Backend.common.exception.ResourceNotFoundException;
import com.example.Uplink_Backend.modules.auth.entity.User;
import com.example.Uplink_Backend.modules.auth.repository.UserRepository;
import com.example.Uplink_Backend.modules.opportunity.entity.Opportunity;
import com.example.Uplink_Backend.modules.opportunity.entity.OpportunitySkill;
import com.example.Uplink_Backend.modules.opportunity.repository.OpportunityRepository;
import com.example.Uplink_Backend.modules.recommendation.dto.CareerPathDto;
import com.example.Uplink_Backend.modules.recommendation.dto.OpportunityRecommendationDto;
import com.example.Uplink_Backend.modules.recommendation.dto.SkillRecommendationDto;
import com.example.Uplink_Backend.modules.recommendation.entity.Recommendation;
import com.example.Uplink_Backend.modules.recommendation.repository.RecommendationRepository;
import com.example.Uplink_Backend.modules.user.entity.UserSkill;
import com.example.Uplink_Backend.modules.user.repository.UserSkillRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    private final RecommendationRepository recommendationRepository;
    private final OpportunityRepository opportunityRepository;
    private final UserSkillRepository userSkillRepository;
    private final UserRepository userRepository;

    public RecommendationService(RecommendationRepository recommendationRepository,
                                 OpportunityRepository opportunityRepository,
                                 UserSkillRepository userSkillRepository,
                                 UserRepository userRepository) {
        this.recommendationRepository = recommendationRepository;
        this.opportunityRepository = opportunityRepository;
        this.userSkillRepository = userSkillRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public List<OpportunityRecommendationDto> getOpportunityRecommendations(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        List<UserSkill> userSkills = userSkillRepository.findByUserId(userId);
        Set<String> candidateSkillNames = userSkills.stream()
                .map(us -> us.getSkill().getName().toLowerCase().trim())
                .collect(Collectors.toSet());

        List<Opportunity> allOpportunities = opportunityRepository.findByIsActiveTrue(PageRequest.of(0, 100)).getContent();
        List<OpportunityRecommendationDto> results = new ArrayList<>();

        for (Opportunity opp : allOpportunities) {
            List<OpportunitySkill> requiredSkills = opp.getRequiredSkills();
            if (requiredSkills == null || requiredSkills.isEmpty()) {
                results.add(new OpportunityRecommendationDto(opp, BigDecimal.valueOf(50.0), "General match for verified candidates"));
                continue;
            }

            int matched = 0;
            List<String> matchedSkillNames = new ArrayList<>();
            for (OpportunitySkill os : requiredSkills) {
                String reqSkill = os.getSkill().getName().toLowerCase().trim();
                if (candidateSkillNames.contains(reqSkill)) {
                    matched++;
                    matchedSkillNames.add(os.getSkill().getName());
                }
            }

            double scoreDouble = ((double) matched / requiredSkills.size()) * 100.0;
            BigDecimal matchScore = BigDecimal.valueOf(scoreDouble).setScale(1, RoundingMode.HALF_UP);

            String reason = matchedSkillNames.isEmpty()
                    ? "Great entry-level opportunity for rapid skill growth"
                    : "Matched " + matched + " of " + requiredSkills.size() + " required skills (" + String.join(", ", matchedSkillNames) + ")";

            results.add(new OpportunityRecommendationDto(opp, matchScore, reason));

            // Persist or update recommendation in database
            Recommendation rec = new Recommendation(user, "OPPORTUNITY", opp.getId(), matchScore, reason);
            recommendationRepository.save(rec);
        }

        results.sort((a, b) -> b.getMatchScore().compareTo(a.getMatchScore()));
        return results;
    }

    public List<CareerPathDto> getCareerPathSuggestions(UUID userId) {
        List<UserSkill> userSkills = userSkillRepository.findByUserId(userId);
        Set<String> skillNames = userSkills.stream()
                .map(us -> us.getSkill().getName().toLowerCase())
                .collect(Collectors.toSet());

        List<CareerPathDto> careerPaths = new ArrayList<>();

        // 1. Full Stack Engineer
        double fsScore = 70.0;
        if (skillNames.contains("java") || skillNames.contains("spring boot") || skillNames.contains("react")) {
            fsScore = 92.5;
        }
        careerPaths.add(new CareerPathDto(
                "Full Stack Engineer",
                "Software Engineering",
                fsScore,
                "Architect, implement, and maintain scalable end-to-end cloud and web enterprise systems.",
                List.of("Master Spring Boot & REST APIs", "Develop React/TypeScript frontend", "Containerize using Docker & deploy on Kubernetes"),
                List.of("AWS Certified Developer Associate", "Oracle Certified Professional Java"),
                "$90,000 - $140,000 / year"
        ));

        // 2. AI / ML Engineer
        double aiScore = 60.0;
        if (skillNames.contains("python") || skillNames.contains("machine learning")) {
            aiScore = 88.0;
        }
        careerPaths.add(new CareerPathDto(
                "AI / Machine Learning Engineer",
                "Artificial Intelligence",
                aiScore,
                "Design machine learning pipelines, LLM fine-tuning, and production AI inference microservices.",
                List.of("Deepen Python data science foundations", "Build PyTorch / HuggingFace models", "Deploy FastAPI endpoints with GPU acceleration"),
                List.of("TensorFlow Developer Certificate", "Google Cloud Professional ML Engineer"),
                "$110,000 - $165,000 / year"
        ));

        // 3. Cloud & DevOps Architect
        double devopsScore = 65.0;
        if (skillNames.contains("docker") || skillNames.contains("kubernetes")) {
            devopsScore = 85.0;
        }
        careerPaths.add(new CareerPathDto(
                "Cloud & DevOps Engineer",
                "Infrastructure & Systems",
                devopsScore,
                "Build automated CI/CD pipelines, Terraform infrastructure-as-code, and resilient multi-region architectures.",
                List.of("Master Linux administration", "Automate with GitHub Actions & Terraform", "Manage Kubernetes clusters"),
                List.of("Certified Kubernetes Administrator (CKA)", "AWS Solutions Architect"),
                "$95,000 - $150,000 / year"
        ));

        careerPaths.sort((a, b) -> Double.compare(b.getMatchProbability(), a.getMatchProbability()));
        return careerPaths;
    }

    public List<SkillRecommendationDto> getSkillRecommendations(UUID userId) {
        List<UserSkill> userSkills = userSkillRepository.findByUserId(userId);
        Set<String> current = userSkills.stream()
                .map(us -> us.getSkill().getName().toLowerCase())
                .collect(Collectors.toSet());

        List<SkillRecommendationDto> recommendations = new ArrayList<>();
        if (!current.contains("docker")) {
            recommendations.add(new SkillRecommendationDto("Docker", "DevOps & Cloud", 94.0, "High industry demand across 85% of backend listings"));
        }
        if (!current.contains("kubernetes")) {
            recommendations.add(new SkillRecommendationDto("Kubernetes", "DevOps & Cloud", 89.0, "Critical for modern microservice orchestrations"));
        }
        if (!current.contains("react")) {
            recommendations.add(new SkillRecommendationDto("React", "Frontend", 92.0, "Most sought-after UI library across tech employers"));
        }
        if (!current.contains("postgresql")) {
            recommendations.add(new SkillRecommendationDto("PostgreSQL", "Database", 91.0, "Leading open-source relational database in modern stacks"));
        }
        if (!current.contains("redis")) {
            recommendations.add(new SkillRecommendationDto("Redis", "Database & Caching", 86.0, "Essential for high-throughput caching and distributed sessions"));
        }

        return recommendations;
    }
}
