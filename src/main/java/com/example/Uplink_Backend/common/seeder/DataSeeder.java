package com.example.Uplink_Backend.common.seeder;

import com.example.Uplink_Backend.modules.admin.entity.AuditLog;
import com.example.Uplink_Backend.modules.admin.repository.AuditLogRepository;
import com.example.Uplink_Backend.modules.assessment.entity.Assessment;
import com.example.Uplink_Backend.modules.assessment.entity.AssessmentQuestion;
import com.example.Uplink_Backend.modules.assessment.repository.AssessmentRepository;
import com.example.Uplink_Backend.modules.auth.entity.Admin;
import com.example.Uplink_Backend.modules.auth.entity.Role;
import com.example.Uplink_Backend.modules.auth.entity.User;
import com.example.Uplink_Backend.modules.auth.repository.AdminRepository;
import com.example.Uplink_Backend.modules.auth.repository.RoleRepository;
import com.example.Uplink_Backend.modules.auth.repository.UserRepository;
import com.example.Uplink_Backend.modules.content.entity.Comment;
import com.example.Uplink_Backend.modules.content.entity.Post;
import com.example.Uplink_Backend.modules.content.repository.CommentRepository;
import com.example.Uplink_Backend.modules.content.repository.PostRepository;
import com.example.Uplink_Backend.modules.notification.entity.Notification;
import com.example.Uplink_Backend.modules.notification.repository.NotificationRepository;
import com.example.Uplink_Backend.modules.opportunity.entity.Opportunity;
import com.example.Uplink_Backend.modules.opportunity.entity.OpportunitySkill;
import com.example.Uplink_Backend.modules.opportunity.repository.OpportunityRepository;
import com.example.Uplink_Backend.modules.recruiter.entity.Company;
import com.example.Uplink_Backend.modules.recruiter.entity.Recruiter;
import com.example.Uplink_Backend.modules.recruiter.repository.CompanyRepository;
import com.example.Uplink_Backend.modules.recruiter.repository.RecruiterRepository;
import com.example.Uplink_Backend.modules.user.entity.*;
import com.example.Uplink_Backend.modules.user.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataSeeder.class);

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final CompanyRepository companyRepository;
    private final RecruiterRepository recruiterRepository;
    private final OpportunityRepository opportunityRepository;
    private final SkillRepository skillRepository;
    private final UserSkillRepository userSkillRepository;
    private final ProfileRepository profileRepository;
    private final EducationRepository educationRepository;
    private final ProjectRepository projectRepository;
    private final ExperienceRepository experienceRepository;
    private final AssessmentRepository assessmentRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final NotificationRepository notificationRepository;
    private final AuditLogRepository auditLogRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(RoleRepository roleRepository,
                      UserRepository userRepository,
                      AdminRepository adminRepository,
                      CompanyRepository companyRepository,
                      RecruiterRepository recruiterRepository,
                      OpportunityRepository opportunityRepository,
                      SkillRepository skillRepository,
                      UserSkillRepository userSkillRepository,
                      ProfileRepository profileRepository,
                      EducationRepository educationRepository,
                      ProjectRepository projectRepository,
                      ExperienceRepository experienceRepository,
                      AssessmentRepository assessmentRepository,
                      PostRepository postRepository,
                      CommentRepository commentRepository,
                      NotificationRepository notificationRepository,
                      AuditLogRepository auditLogRepository,
                      PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.adminRepository = adminRepository;
        this.companyRepository = companyRepository;
        this.recruiterRepository = recruiterRepository;
        this.opportunityRepository = opportunityRepository;
        this.skillRepository = skillRepository;
        this.userSkillRepository = userSkillRepository;
        this.profileRepository = profileRepository;
        this.educationRepository = educationRepository;
        this.projectRepository = projectRepository;
        this.experienceRepository = experienceRepository;
        this.assessmentRepository = assessmentRepository;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.notificationRepository = notificationRepository;
        this.auditLogRepository = auditLogRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (roleRepository.count() > 0) {
            logger.info("DataSeeder: Database already populated. Skipping seeder.");
            return;
        }

        logger.info("DataSeeder: Starting initial comprehensive data seed...");

        // 1. Roles
        Role adminRole = roleRepository.save(new Role("ROLE_ADMIN", "System Administrator"));
        Role recruiterRole = roleRepository.save(new Role("ROLE_RECRUITER", "Corporate Industry Recruiter"));
        Role studentRole = roleRepository.save(new Role("ROLE_STUDENT", "Academic Student Candidate"));
        Role collegeRole = roleRepository.save(new Role("ROLE_COLLEGE", "Institution / Placement Cell Officer"));

        // 2. Admin User
        User adminUser = new User();
        adminUser.setEmail("admin@uplink.edu");
        adminUser.setPassword(passwordEncoder.encode("Admin@123"));
        adminUser.setFullName("Platform Administrator");
        adminUser.setPhone("+1-800-555-0199");
        adminUser.setIsVerified(true);
        adminUser.setIsActive(true);
        adminUser.setRoles(Set.of(adminRole));
        User savedAdmin = userRepository.save(adminUser);
        adminRepository.save(new Admin(savedAdmin, "Engineering & Placements"));

        // 3. Companies
        Company google = companyRepository.save(new Company("Google", "MNC", "https://careers.google.com", "Building products for everyone."));
        google.setIsVerified(true);
        google.setLogoUrl("https://upload.wikimedia.org/wikipedia/commons/2/2f/Google_2015_logo.svg");
        companyRepository.save(google);

        Company microsoft = companyRepository.save(new Company("Microsoft", "MNC", "https://careers.microsoft.com", "Empowering every person and organization on the planet."));
        microsoft.setIsVerified(true);
        microsoft.setLogoUrl("https://upload.wikimedia.org/wikipedia/commons/9/96/Microsoft_logo_%282012%29.svg");
        companyRepository.save(microsoft);

        // 4. Recruiter User
        User recruiterUser = new User();
        recruiterUser.setEmail("recruiter@google.com");
        recruiterUser.setPassword(passwordEncoder.encode("Recruiter@123"));
        recruiterUser.setFullName("Sarah Jenkins");
        recruiterUser.setPhone("+1-650-253-0000");
        recruiterUser.setIsVerified(true);
        recruiterUser.setIsActive(true);
        recruiterUser.setRoles(Set.of(recruiterRole));
        User savedRecruiter = userRepository.save(recruiterUser);

        Recruiter recruiter = new Recruiter(savedRecruiter, google, "Senior University Talent Lead");
        recruiter.setIsVerified(true);
        recruiterRepository.save(recruiter);

        // 5. Student User
        User studentUser = new User();
        studentUser.setEmail("student@mit.edu");
        studentUser.setPassword(passwordEncoder.encode("Student@123"));
        studentUser.setFullName("Vishal Bade");
        studentUser.setPhone("+1-617-253-1000");
        studentUser.setIsVerified(true);
        studentUser.setIsActive(true);
        studentUser.setRoles(Set.of(studentRole));
        User savedStudent = userRepository.save(studentUser);

        // Student Profile & Portfolio
        Profile profile = new Profile(savedStudent);
        profile.setHeadline("Software Engineering Fellow | Cloud Native & AI Systems Enthusiast");
        profile.setBio("Passionate developer with strong foundation in distributed systems, Spring Boot, React, and Machine Learning.");
        profile.setLocation("Boston, MA / Remote");
        profileRepository.save(profile);

        Education edu = new Education();
        edu.setUser(savedStudent);
        edu.setDegree("Bachelor of Technology");
        edu.setCollege("Massachusetts Institute of Technology");
        edu.setBranch("Computer Science and Engineering");
        edu.setStartYear(2022);
        edu.setEndYear(2026);
        edu.setCgpa(new BigDecimal("3.92"));
        edu.setIsCurrent(true);
        educationRepository.save(edu);

        // 6. Skills
        Skill javaSkill = skillRepository.save(new Skill("Java", "Backend"));
        Skill springSkill = skillRepository.save(new Skill("Spring Boot", "Backend"));
        Skill reactSkill = skillRepository.save(new Skill("React", "Frontend"));
        Skill postgresSkill = skillRepository.save(new Skill("PostgreSQL", "Database"));
        Skill dockerSkill = skillRepository.save(new Skill("Docker", "DevOps"));
        Skill mlSkill = skillRepository.save(new Skill("Machine Learning", "AI"));

        // Attach skills to student
        UserSkill us1 = new UserSkill();
        us1.setUser(savedStudent);
        us1.setSkill(javaSkill);
        us1.setProficiency("ADVANCED");
        us1.setYearsOfExp(new BigDecimal("3.0"));
        us1.setIsVerified(true);
        userSkillRepository.save(us1);

        UserSkill us2 = new UserSkill();
        us2.setUser(savedStudent);
        us2.setSkill(springSkill);
        us2.setProficiency("ADVANCED");
        us2.setYearsOfExp(new BigDecimal("2.5"));
        us2.setIsVerified(true);
        userSkillRepository.save(us2);

        UserSkill us3 = new UserSkill();
        us3.setUser(savedStudent);
        us3.setSkill(reactSkill);
        us3.setProficiency("INTERMEDIATE");
        us3.setYearsOfExp(new BigDecimal("2.0"));
        us3.setIsVerified(true);
        userSkillRepository.save(us3);

        // Projects
        Project p1 = new Project();
        p1.setUser(savedStudent);
        p1.setTitle("Autonomous Placement & Collaboration Engine");
        p1.setDescription("Enterprise-grade recruitment platform unifying academic institutions with Fortune 500 recruiters.");
        p1.setTechStack("Java, Spring Boot, PostgreSQL, React, Docker");
        p1.setGithubUrl("https://github.com/Vishal-Bade/Uplink-Backend");
        projectRepository.save(p1);

        // Experience
        Experience exp1 = new Experience();
        exp1.setUser(savedStudent);
        exp1.setTitle("Software Engineer Intern");
        exp1.setCompany("Amazon Web Services");
        exp1.setLocation("Seattle, WA");
        exp1.setStartDate(LocalDate.of(2024, 6, 1));
        exp1.setEndDate(LocalDate.of(2024, 8, 31));
        exp1.setDescription("Contributed to high-throughput cloud streaming APIs, optimizing P99 latency by 28%.");
        experienceRepository.save(exp1);

        // 7. Opportunities
        Opportunity opp1 = new Opportunity();
        opp1.setCompany(google);
        opp1.setCreatedBy(savedRecruiter);
        opp1.setType("INTERNSHIP");
        opp1.setTitle("Software Development Engineer Intern - Summer 2026");
        opp1.setDescription("Join Google engineering teams in Mountain View or New York to build innovative search and cloud features.");
        opp1.setLocation("Mountain View, CA / Remote");
        opp1.setWorkMode("HYBRID");
        opp1.setExperienceLevel("ENTRY");
        opp1.setStipendMin(new BigDecimal("8500.00"));
        opp1.setStipendMax(new BigDecimal("10500.00"));
        opp1.setStartDate(LocalDate.of(2026, 6, 1));
        opp1.setDeadline(LocalDate.of(2026, 12, 31));
        opp1.setIsActive(true);
        opp1.setIsVerified(true);
        opp1.getRequiredSkills().add(new OpportunitySkill(opp1, javaSkill, true, "ADVANCED"));
        opp1.getRequiredSkills().add(new OpportunitySkill(opp1, springSkill, false, "INTERMEDIATE"));
        opportunityRepository.save(opp1);

        Opportunity opp2 = new Opportunity();
        opp2.setCompany(microsoft);
        opp2.setCreatedBy(savedAdmin);
        opp2.setType("JOB");
        opp2.setTitle("Full Stack Cloud Engineer (New Grad)");
        opp2.setDescription("Drive digital transformation with Azure Cloud and modern web microservices.");
        opp2.setLocation("Redmond, WA");
        opp2.setWorkMode("ONSITE");
        opp2.setExperienceLevel("ENTRY");
        opp2.setStipendMin(new BigDecimal("115000.00"));
        opp2.setStipendMax(new BigDecimal("145000.00"));
        opp2.setStartDate(LocalDate.of(2026, 7, 15));
        opp2.setDeadline(LocalDate.of(2026, 11, 30));
        opp2.setIsActive(true);
        opp2.setIsVerified(true);
        opp2.getRequiredSkills().add(new OpportunitySkill(opp2, reactSkill, true, "INTERMEDIATE"));
        opp2.getRequiredSkills().add(new OpportunitySkill(opp2, postgresSkill, false, "INTERMEDIATE"));
        opportunityRepository.save(opp2);

        Opportunity opp3 = new Opportunity();
        opp3.setCompany(google);
        opp3.setCreatedBy(savedRecruiter);
        opp3.setType("HACKATHON");
        opp3.setTitle("Google Cloud Global AI Buildathon 2026");
        opp3.setDescription("Build next-generation multi-agent systems using Google Gemini and Vertex AI. $50,000 in grand prizes!");
        opp3.setLocation("Global / Online");
        opp3.setWorkMode("REMOTE");
        opp3.setStartDate(LocalDate.of(2026, 11, 10));
        opp3.setDeadline(LocalDate.of(2026, 11, 20));
        opp3.setIsActive(true);
        opp3.setIsVerified(true);
        opportunityRepository.save(opp3);

        // 8. Assessments
        Assessment assessment = new Assessment("Java & Backend Systems Proficiency",
                "Official evaluation covering core Java concurrency, Spring Boot DI/IoC, JPA transactions, and REST APIs.",
                "Java", 25);
        assessment.setIsActive(true);

        AssessmentQuestion q1 = new AssessmentQuestion(assessment,
                "What is the default bean scope in the Spring Framework?",
                "[\"singleton\", \"prototype\", \"request\", \"session\"]",
                "singleton", new BigDecimal("5.0"), 1);

        AssessmentQuestion q2 = new AssessmentQuestion(assessment,
                "Which HTTP method is idempotent according to RFC 7231?",
                "[\"POST\", \"PUT\", \"PATCH\", \"CONNECT\"]",
                "PUT", new BigDecimal("5.0"), 2);

        AssessmentQuestion q3 = new AssessmentQuestion(assessment,
                "In PostgreSQL / ACID databases, what does the 'I' in ACID stand for?",
                "[\"Inheritance\", \"Integrity\", \"Isolation\", \"Index\"]",
                "Isolation", new BigDecimal("5.0"), 3);

        assessment.getQuestions().addAll(List.of(q1, q2, q3));
        assessmentRepository.save(assessment);

        // 9. Community Posts
        Post post = new Post(savedRecruiter,
                "Thrilled to announce that Google Summer 2026 Internship applications are now officially OPEN on Uplink! We are searching for passionate builders who love distributed systems and AI.",
                "NOTICE");
        Post savedPost = postRepository.save(post);

        Comment comment = new Comment(savedPost, savedStudent, "Thank you Sarah! Excited to apply with my portfolio.");
        commentRepository.save(comment);

        // 10. Notification & Audit Log
        notificationRepository.save(new Notification(savedStudent, "Welcome to Uplink!",
                "Your profile has been initialized. Explore matched opportunities, take skill assessments, and connect with recruiters.",
                "INFO"));

        auditLogRepository.save(new AuditLog(savedAdmin, "SYSTEM_SEED_INITIALIZED", "System", null,
                "{\"status\": \"All 15 modules seeded successfully\"}"));

        logger.info("DataSeeder: Initial seed completed successfully. System ready!");
    }
}
