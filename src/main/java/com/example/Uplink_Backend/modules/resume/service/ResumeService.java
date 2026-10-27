package com.example.Uplink_Backend.modules.resume.service;

import com.example.Uplink_Backend.common.exception.ResourceNotFoundException;
import com.example.Uplink_Backend.modules.auth.entity.User;
import com.example.Uplink_Backend.modules.auth.repository.UserRepository;
import com.example.Uplink_Backend.modules.ocr.entity.Document;
import com.example.Uplink_Backend.modules.ocr.repository.DocumentRepository;
import com.example.Uplink_Backend.modules.resume.dto.ResumeDto;
import com.example.Uplink_Backend.modules.resume.dto.ResumeGenerateRequest;
import com.example.Uplink_Backend.modules.user.dto.DigitalPortfolioDto;
import com.example.Uplink_Backend.modules.user.entity.*;
import com.example.Uplink_Backend.modules.user.service.UserProfileService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ResumeService {

    private final UserProfileService profileService;
    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;

    public ResumeService(UserProfileService profileService,
                         DocumentRepository documentRepository,
                         UserRepository userRepository) {
        this.profileService = profileService;
        this.documentRepository = documentRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ResumeDto generateResume(UUID userId, ResumeGenerateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        DigitalPortfolioDto portfolio = profileService.getDigitalPortfolio(userId);

        // Build dynamic LaTeX document
        String latex = buildLatexSource(portfolio, request);

        // Count previous versions to increment version
        List<Document> previousResumes = documentRepository.findByUserIdOrderByUploadedAtDesc(userId).stream()
                .filter(d -> "RESUME".equalsIgnoreCase(d.getType()))
                .collect(Collectors.toList());
        int newVersion = previousResumes.size() + 1;

        String pdfUrl = "/api/resume/download/" + user.getId() + "?version=" + newVersion;

        Document doc = new Document(user, "Resume_v" + newVersion + ".pdf", pdfUrl, "RESUME");
        Document savedDoc = documentRepository.save(doc);

        return new ResumeDto(
                savedDoc.getId(),
                user.getId(),
                newVersion,
                request.getTemplate(),
                latex,
                pdfUrl,
                Instant.now()
        );
    }

    private String buildLatexSource(DigitalPortfolioDto p, ResumeGenerateRequest req) {
        StringBuilder sb = new StringBuilder();
        sb.append("\\documentclass[letterpaper,10pt]{article}\n");
        sb.append("\\usepackage{latexsym,titlesec,marvosym,graphicx,amsmath}\n");
        sb.append("\\usepackage[empty]{fullpage}\n");
        sb.append("\\usepackage[hidelinks]{hyperref}\n\n");
        sb.append("\\begin{document}\n\n");

        // Header
        sb.append("\\begin{center}\n");
        sb.append("  {\\Huge \\scshape ").append(escapeLatex(p.getFullName())).append("} \\\\\n");
        if (p.getProfile() != null && p.getProfile().getHeadline() != null) {
            sb.append("  \\large ").append(escapeLatex(p.getProfile().getHeadline())).append(" \\\\\n");
        }
        sb.append("  \\small ").append(p.getEmail());
        if (p.getPhone() != null) sb.append(" $|$ ").append(p.getPhone());
        if (p.getProfile() != null && p.getProfile().getLocation() != null) sb.append(" $|$ ").append(p.getProfile().getLocation());
        sb.append("\n\\end{center}\n\n");

        // Summary
        if (p.getProfile() != null && p.getProfile().getBio() != null) {
            sb.append("\\section*{Professional Summary}\n");
            sb.append(escapeLatex(p.getProfile().getBio())).append("\n\n");
        }

        // Education
        if (p.getEducation() != null && !p.getEducation().isEmpty()) {
            sb.append("\\section*{Education}\n\\begin{itemize}\n");
            for (Education edu : p.getEducation()) {
                sb.append("  \\item \\textbf{").append(escapeLatex(edu.getCollege())).append("} -- ")
                        .append(escapeLatex(edu.getDegree())).append(" in ").append(escapeLatex(edu.getBranch()))
                        .append(" (").append(edu.getStartYear()).append(" -- ")
                        .append(Boolean.TRUE.equals(edu.getIsCurrent()) ? "Present" : edu.getEndYear()).append(")")
                        .append(edu.getCgpa() != null ? " [CGPA: " + edu.getCgpa() + "]" : "")
                        .append("\n");
            }
            sb.append("\\end{itemize}\n\n");
        }

        // Skills
        if (p.getSkills() != null && !p.getSkills().isEmpty()) {
            sb.append("\\section*{Technical Skills}\n");
            String skillList = p.getSkills().stream()
                    .map(us -> us.getSkill().getName() + " (" + us.getProficiency() + ")")
                    .collect(Collectors.joining(", "));
            sb.append("\\textbf{Core Competencies:} ").append(escapeLatex(skillList)).append("\n\n");
        }

        // Experience
        if (p.getExperiences() != null && !p.getExperiences().isEmpty()) {
            sb.append("\\section*{Experience}\n\\begin{itemize}\n");
            for (Experience exp : p.getExperiences()) {
                sb.append("  \\item \\textbf{").append(escapeLatex(exp.getTitle())).append("} at ")
                        .append(escapeLatex(exp.getCompany())).append(" (").append(exp.getStartDate()).append(" -- ")
                        .append(exp.getEndDate() != null ? exp.getEndDate().toString() : "Present").append(")\\\\\n")
                        .append("  ").append(escapeLatex(exp.getDescription() != null ? exp.getDescription() : "")).append("\n");
            }
            sb.append("\\end{itemize}\n\n");
        }

        // Projects
        if (req.isIncludeProjects() && p.getProjects() != null && !p.getProjects().isEmpty()) {
            sb.append("\\section*{Projects}\n\\begin{itemize}\n");
            for (Project proj : p.getProjects()) {
                sb.append("  \\item \\textbf{").append(escapeLatex(proj.getTitle())).append("} \\\\\n")
                        .append("  ").append(escapeLatex(proj.getDescription() != null ? proj.getDescription() : "")).append("\n");
                if (proj.getTechStack() != null) {
                    sb.append("  \\textit{Tech Stack: ").append(escapeLatex(proj.getTechStack())).append("}\n");
                }
            }
            sb.append("\\end{itemize}\n\n");
        }

        // Certifications
        if (req.isIncludeCertifications() && p.getCertifications() != null && !p.getCertifications().isEmpty()) {
            sb.append("\\section*{Certifications}\n\\begin{itemize}\n");
            for (Certification cert : p.getCertifications()) {
                sb.append("  \\item \\textbf{").append(escapeLatex(cert.getTitle())).append("} -- ")
                        .append(escapeLatex(cert.getIssuer())).append("\n");
            }
            sb.append("\\end{itemize}\n\n");
        }

        sb.append("\\end{document}\n");
        return sb.toString();
    }

    private String escapeLatex(String text) {
        if (text == null) return "";
        return text.replace("&", "\\&")
                .replace("%", "\\%")
                .replace("$", "\\$")
                .replace("#", "\\#")
                .replace("_", "\\_");
    }
}
