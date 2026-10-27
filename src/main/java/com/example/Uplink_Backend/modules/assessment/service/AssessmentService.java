package com.example.Uplink_Backend.modules.assessment.service;

import com.example.Uplink_Backend.common.exception.ResourceNotFoundException;
import com.example.Uplink_Backend.modules.assessment.dto.AssessmentResultDto;
import com.example.Uplink_Backend.modules.assessment.dto.SkillGapReportDto;
import com.example.Uplink_Backend.modules.assessment.dto.SubmitAssessmentRequest;
import com.example.Uplink_Backend.modules.assessment.entity.*;
import com.example.Uplink_Backend.modules.assessment.repository.*;
import com.example.Uplink_Backend.modules.auth.entity.User;
import com.example.Uplink_Backend.modules.auth.repository.UserRepository;
import com.example.Uplink_Backend.modules.user.entity.UserSkill;
import com.example.Uplink_Backend.modules.user.repository.UserSkillRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AssessmentService {

    private final AssessmentRepository assessmentRepository;
    private final AssessmentQuestionRepository questionRepository;
    private final AssessmentAttemptRepository attemptRepository;
    private final SkillGapRepository skillGapRepository;
    private final UserRepository userRepository;
    private final UserSkillRepository userSkillRepository;

    public AssessmentService(AssessmentRepository assessmentRepository,
                             AssessmentQuestionRepository questionRepository,
                             AssessmentAttemptRepository attemptRepository,
                             SkillGapRepository skillGapRepository,
                             UserRepository userRepository,
                             UserSkillRepository userSkillRepository) {
        this.assessmentRepository = assessmentRepository;
        this.questionRepository = questionRepository;
        this.attemptRepository = attemptRepository;
        this.skillGapRepository = skillGapRepository;
        this.userRepository = userRepository;
        this.userSkillRepository = userSkillRepository;
    }

    public Page<Assessment> getActiveAssessments(Pageable pageable) {
        return assessmentRepository.findByIsActiveTrue(pageable);
    }

    public Assessment getAssessmentById(UUID id) {
        return assessmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assessment", "id", id));
    }

    @Transactional
    public Assessment createAssessment(Assessment assessment) {
        assessment.setIsActive(true);
        if (assessment.getQuestions() != null) {
            for (AssessmentQuestion q : assessment.getQuestions()) {
                q.setAssessment(assessment);
            }
        }
        return assessmentRepository.save(assessment);
    }

    @Transactional
    public AssessmentResultDto submitAssessment(UUID userId, SubmitAssessmentRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        Assessment assessment = getAssessmentById(request.getAssessmentId());

        List<AssessmentQuestion> questions = questionRepository.findByAssessmentIdOrderByOrderIndexAsc(assessment.getId());

        BigDecimal totalMarks = BigDecimal.ZERO;
        BigDecimal earnedMarks = BigDecimal.ZERO;

        Map<UUID, String> candidateAnswers = request.getAnswers() != null ? request.getAnswers() : Collections.emptyMap();

        for (AssessmentQuestion q : questions) {
            BigDecimal questionMark = q.getMarks() != null ? q.getMarks() : BigDecimal.ONE;
            totalMarks = totalMarks.add(questionMark);

            String candidateAns = candidateAnswers.get(q.getId());
            if (candidateAns != null && q.getCorrectAnswer() != null &&
                    candidateAns.trim().equalsIgnoreCase(q.getCorrectAnswer().trim())) {
                earnedMarks = earnedMarks.add(questionMark);
            }
        }

        BigDecimal percentage = totalMarks.compareTo(BigDecimal.ZERO) > 0
                ? earnedMarks.multiply(BigDecimal.valueOf(100)).divide(totalMarks, 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        boolean passed = percentage.compareTo(BigDecimal.valueOf(60.0)) >= 0;

        AssessmentAttempt attempt = new AssessmentAttempt();
        attempt.setUser(user);
        attempt.setAssessment(assessment);
        attempt.setScore(earnedMarks);
        attempt.setSubmittedAt(Instant.now());
        AssessmentAttempt savedAttempt = attemptRepository.save(attempt);

        // If passed, verify user skill matching assessment category if present
        if (passed && assessment.getCategory() != null) {
            List<UserSkill> userSkills = userSkillRepository.findByUserId(userId);
            for (UserSkill us : userSkills) {
                if (us.getSkill().getName().equalsIgnoreCase(assessment.getCategory()) ||
                        us.getSkill().getCategory().equalsIgnoreCase(assessment.getCategory())) {
                    us.setIsVerified(true);
                    userSkillRepository.save(us);
                }
            }
        }

        return new AssessmentResultDto(
                savedAttempt.getId(),
                assessment.getId(),
                assessment.getTitle(),
                earnedMarks,
                totalMarks,
                percentage,
                passed,
                savedAttempt.getSubmittedAt()
        );
    }

    public List<AssessmentAttempt> getUserAttempts(UUID userId) {
        return attemptRepository.findByUserIdOrderByStartedAtDesc(userId);
    }

    @Transactional
    public SkillGapReportDto performSkillGapAnalysis(UUID userId, String targetRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        List<UserSkill> userSkillsList = userSkillRepository.findByUserId(userId);
        List<String> userSkillNames = userSkillsList.stream()
                .map(us -> us.getSkill().getName().toLowerCase().trim())
                .collect(Collectors.toList());

        // Target role competency standard mapping
        List<String> benchmarkSkills = getBenchmarkSkillsForRole(targetRole);

        List<String> missingSkills = new ArrayList<>();
        int matchedCount = 0;

        for (String benchmark : benchmarkSkills) {
            if (userSkillNames.contains(benchmark.toLowerCase().trim())) {
                matchedCount++;
            } else {
                missingSkills.add(benchmark);
            }
        }

        double matchPercentage = benchmarkSkills.isEmpty() ? 0.0 :
                Math.round(((double) matchedCount / benchmarkSkills.size()) * 100.0);

        List<String> recommendations = missingSkills.stream()
                .map(s -> "Complete certified assessment and tutorial modules for " + s)
                .collect(Collectors.toList());

        // Store skill gap record
        String gapDataJson = String.format("{\"matched\": %d, \"total\": %d, \"missing\": \"%s\"}",
                matchedCount, benchmarkSkills.size(), String.join(",", missingSkills));
        SkillGap gap = new SkillGap(user, targetRole, gapDataJson);
        skillGapRepository.save(gap);

        return new SkillGapReportDto(
                targetRole,
                matchPercentage,
                userSkillNames,
                benchmarkSkills,
                missingSkills,
                recommendations
        );
    }

    private List<String> getBenchmarkSkillsForRole(String role) {
        String lowerRole = role.toLowerCase();
        if (lowerRole.contains("full stack") || lowerRole.contains("fullstack")) {
            return List.of("Java", "Spring Boot", "React", "PostgreSQL", "REST API", "Docker", "Git");
        } else if (lowerRole.contains("frontend") || lowerRole.contains("front end")) {
            return List.of("JavaScript", "TypeScript", "React", "HTML5", "CSS3", "Tailwind CSS", "Redux");
        } else if (lowerRole.contains("backend") || lowerRole.contains("back end")) {
            return List.of("Java", "Spring Boot", "PostgreSQL", "Redis", "Microservices", "Docker", "REST API");
        } else if (lowerRole.contains("data") || lowerRole.contains("machine learning") || lowerRole.contains("ai")) {
            return List.of("Python", "Pandas", "NumPy", "Scikit-Learn", "Machine Learning", "SQL", "Deep Learning");
        } else if (lowerRole.contains("devops") || lowerRole.contains("cloud")) {
            return List.of("Docker", "Kubernetes", "Linux", "CI/CD", "AWS", "Terraform", "Monitoring");
        }
        return List.of("Problem Solving", "Data Structures", "Algorithms", "Git", "SQL");
    }
}
