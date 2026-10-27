# Uplink - AI-Powered Academia-Industry Collaboration & Placement Platform

> **100% Complete Implementation** of the Backend Architecture and Database Architecture.

---

## 🏛️ System Architecture Overview

The backend is built as a **Modular Monolith** using **Spring Boot 4 / Java 24 / Spring Security 7**, coupled with a **FastAPI Microservice** for AI/NLP processing, backed by **PostgreSQL (Supabase-compatible)** and **Redis** for caching, rate limiting, and queues.

```
+-----------------------------------------------------------------------------------------------------------------------+
|                                                  CLIENTS & EDGE                                                       |
|                                [ Web App (React) ]        [ Mobile App (React Native) ]                               |
|                                                     |                                                                 |
|                             [ Cloudflare (CDN/WAF/DDoS) ] -> [ Load Balancer (NGINX) ]                                |
+-----------------------------------------------------------------------------------------------------------------------+
                                                      |
                                                      v
+-----------------------------------------------------------------------------------------------------------------------+
|                                    SPRING BOOT BACKEND (MODULAR MONOLITH)                                             |
|                                                                                                                       |
|   1. AUTH MODULE               2. USER & PROFILE MODULE       3. ASSESSMENT MODULE       4. OPPORTUNITY MODULE        |
|      - Register / Login           - Profile & Headline           - Skill Assessments        - Jobs / Internships      |
|      - JWT & Refresh Tokens       - Academic Info                - Competency Scoring       - Hackathons & Events     |
|      - Email Verification         - Skills (with Proficiency)    - Skill Gap Analysis       - Advanced Filters        |
|      - RBAC (Student/Recruiter)   - Digital Portfolio (Full)     - Learning Roadmaps        - External Job Ingestion  |
|                                                                                                                       |
|   5. APPLICATION MODULE        6. RECOMMENDATION MODULE       7. RECRUITER MODULE        8. MESSAGING MODULE          |
|      - Apply to Opportunities     - Opportunity Matching         - Company Registration     - Direct / Group Chat     |
|      - Status Management          - Career Path Roadmaps         - Recruiter Assignment     - Read Receipts           |
|      - Interview Scheduling       - Skill Demand Ranking         - Candidate Shortlisting   - Attachment Links        |
|      - Lifecycle Audit History    - Compatibility Scoring        - Corporate Verification                             |
|                                                                                                                       |
|   9. NOTIFICATION MODULE       10. OCR MODULE (Integration)   11. RESUME MODULE (AI)     12. CONTENT & COMMUNITY      |
|      - Real-time Alerts           - Document Upload              - AI Resume Generation     - Posts / Feed            |
|      - In-App Notification Feed   - Job State Machine            - Dynamic LaTeX Builder    - Discussion Blogs        |
|      - Unread Badging             - Entity Extraction            - PDF Compilation URL      - Comments & Likes        |
|                                                                  - Version History                                    |
|                                                                                                                       |
|   13. VERIFICATION MODULE      14. ADMIN MODULE               15. ANALYTICS MODULE       CROSS-CUTTING CONCERNS       |
|      - Identity / College IDs     - Platform KPI Dashboard       - Placement Rates & Stats  - Global Exceptions       |
|      - Recruiter Accreditation    - User Moderation & Roles      - Funnel Activity Insights - JWT Security Filter     |
|      - Admin Review Workflow      - Content Moderation           - CSV / Report Export      - Audit Logging & Storage |
|                                   - System Audit Logs                                       - Background Scheduler    |
+-----------------------------------------------------------------------------------------------------------------------+
                       |                                           |                              |
                       v                                           v                              v
            [ Supabase PostgreSQL ]                            [ Redis ]                 [ FastAPI AI Service ]
            - 27 Relational Tables                     - Caching Hot Data            - Resume Parsing
            - UUID Primary Keys                        - Distributed Rate Limits     - Skill Extraction
            - UTC Timestamps                           - Background Job Queues       - Recommendation Engine
            - Soft-deletes & Indices                                                 - OCR Text Processing
```

---

## 🗄️ Database Architecture (27 Tables)

All tables use **UUID** primary keys, **UTC timestamps**, soft delete fields (`is_deleted`), and indexes:

| Domain | Tables | Key Columns & Foreign Keys |
| :--- | :--- | :--- |
| **Core & Identity** | `roles`, `users`, `user_roles`, `user_verifications`, `admins` | `id (UUID)`, `email (UNIQUE)`, `password`, `is_verified`, `is_active`, `roles (M:N)` |
| **Profile & Academic** | `profiles`, `education`, `skills`, `user_skills`, `projects`, `experiences`, `certifications`, `achievements` | `user_id (FK)`, `cgpa`, `proficiency`, `years_of_exp`, `tech_stack`, `start_year`, `end_year` |
| **Opportunities & Apps**| `companies`, `recruiters`, `opportunities`, `opportunity_skills`, `opportunity_sources`, `applications`, `application_status_history` | `company_id (FK)`, `stipend_min/max`, `work_mode`, `status (APPLIED, UNDER_REVIEW, SHORTLISTED, INTERVIEW, SELECTED, REJECTED)`, `history audit` |
| **Communication** | `conversations`, `conversation_members`, `messages`, `posts`, `comments`, `likes` | `conversation_id (FK)`, `sender_id (FK)`, `message_type`, `is_read`, `read_at`, `post_id (FK)` |
| **AI & Assessments** | `assessments`, `assessment_questions`, `assessment_attempts`, `skill_gaps`, `recommendations` | `marks`, `options (JSON)`, `correct_answer`, `score`, `match_percentage`, `gap_data (JSON)` |
| **Admin & System** | `notifications`, `documents`, `ocr_jobs`, `audit_logs` | `uploaded_by (FK)`, `status (PENDING, PROCESSING, COMPLETED, FAILED)`, `metadata (JSON)` |

The initial DDL script is located in [`src/main/resources/schema.sql`](file:///c:/Users/visha_n3qlc1/OneDrive/Desktop/Uplink-Backend/src/main/resources/schema.sql).

---

## 🚀 Quick Start Guide

### 1. Prerequisites
- **Java 21+** (Java 24 installed & verified)
- **Maven** (included via `./mvnw`)
- **Docker & Docker Compose** (optional for containerized deployment)

### 2. Run Locally (Zero external dependencies needed!)
The application has an embedded H2 database with PostgreSQL compatibility enabled by default:

```bash
# Build & verify all modules
./mvnw clean compile

# Run integration tests
./mvnw test

# Start the Spring Boot backend server
./mvnw spring-boot:run
```

Once running:
- **Interactive Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **OpenAPI 3 JSON Spec**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)
- **H2 Web Console**: [http://localhost:8080/h2-console](http://localhost:8080/h2-console) (JDBC URL: `jdbc:h2:mem:uplinkdb`, User: `sa`, Password: *(empty)*)

---

## 👥 Pre-seeded Credentials (Out-of-the-Box)

The built-in `DataSeeder` automatically populates realistic test data:

| Role | Email | Password | Pre-populated Data |
| :--- | :--- | :--- | :--- |
| **Admin** | `admin@uplink.edu` | `Admin@123` | Department: Engineering & Placements, Full system access |
| **Recruiter** | `recruiter@google.com` | `Recruiter@123` | Company: Google, Designation: Senior University Talent Lead |
| **Student** | `student@mit.edu` | `Student@123` | Full Portfolio (B.Tech MIT, Java/React/Spring skills, AWS Internship experience, Projects) |

---

## 🌐 15 Modules REST API Reference

### 1. Auth Module (`/api/auth`)
- `POST /api/auth/register` - Register a new account (`ROLE_STUDENT`, `ROLE_RECRUITER`, etc.)
- `POST /api/auth/login` - Authenticate & obtain JWT access + refresh token
- `POST /api/auth/refresh-token` - Refresh expired JWT token
- `POST /api/auth/forgot-password` - Request password reset email
- `POST /api/auth/reset-password` - Reset password with verification token
- `POST /api/auth/verify-email` - Verify email address
- `GET /api/auth/me` - Get current authenticated user profile

### 2. User & Profile Module (`/api/profile`)
- `GET /api/profile` - Get current user profile
- `PUT /api/profile` - Update bio, headline, location, and avatar
- `GET /api/profile/portfolio` - Get full Digital Portfolio (Education, Skills, Projects, Experiences, Certifications)
- `GET /api/profile/portfolio/{userId}` - View candidate public portfolio
- `POST/GET/DELETE /api/profile/education` - Manage academic degrees & CGPA
- `POST/GET/DELETE /api/profile/skills` - Add/remove skills with proficiency (BEGINNER to EXPERT)
- `POST/GET/DELETE /api/profile/projects` - Manage showcase portfolio projects
- `POST/GET/DELETE /api/profile/experiences` - Manage work history & internships
- `POST/GET/DELETE /api/profile/certifications` - Manage verified certifications
- `POST/GET/DELETE /api/profile/achievements` - Manage honors & achievements

### 3. Assessment Module (`/api/assessments`)
- `GET /api/assessments` - List active skill assessments
- `GET /api/assessments/{id}` - Fetch assessment questions
- `POST /api/assessments` - Create skill test (Admin / College Rep)
- `POST /api/assessments/submit` - Submit answers; receives automated scoring & updates skill verification
- `GET /api/assessments/my-attempts` - View past test attempts and percentages
- `GET /api/assessments/skill-gap-analysis?targetRole=Full+Stack+Developer` - Real-time competency gap analysis against target role benchmark

### 4. Opportunity Module (`/api/opportunities`)
- `POST /api/opportunities` - Post job/internship/hackathon/event (Recruiter / Admin)
- `POST /api/opportunities/external-ingest` - Ingest opportunities from external hiring APIs (LinkedIn, Unstop, etc.)
- `GET /api/opportunities` - Advanced search and filter (`type`, `workMode`, `location`, `keyword`)
- `GET /api/opportunities/{id}` - Get opportunity details with required skills list
- `PATCH /api/opportunities/{id}/status` - Activate or deactivate listing
- `PATCH /api/opportunities/{id}/verify` - Admin verification

### 5. Application Module (`/api/applications`)
- `POST /api/applications/apply` - Submit application with resume URL and cover letter
- `GET /api/applications/my` - Candidate tracks all their applications and current statuses
- `GET /api/applications/opportunity/{opportunityId}` - Recruiter reviews all applicants for an opening
- `PATCH /api/applications/{id}/status` - Recruiter updates status (`UNDER_REVIEW`, `SHORTLISTED`, `INTERVIEW`, `SELECTED`, `REJECTED`)
- `GET /api/applications/{id}/history` - View complete lifecycle timeline of status changes

### 6. Recommendation Module (`/api/recommendations`)
- `GET /api/recommendations/opportunities` - AI-ranked opportunity recommendations with match score & reason
- `GET /api/recommendations/career-paths` - Tailored career trajectory roadmaps, salary benchmarks, and milestones
- `GET /api/recommendations/skills` - High-demand skills recommendations to bridge market gaps

### 7. Recruiter Module (`/api/recruiter`)
- `POST /api/recruiter/company` - Register or join a company
- `GET /api/recruiter/me` - Get recruiter profile & linked company
- `GET /api/recruiter/companies` - List companies
- `PUT /api/recruiter/companies/{id}/verify` - Admin verification of company
- `PUT /api/recruiter/verify/{recruiterId}` - Admin verification of recruiter

### 8. Messaging Module (`/api/messages`)
- `POST /api/messages/conversations` - Start direct or group conversation
- `GET /api/messages/conversations` - List all user conversations
- `POST /api/messages/send` - Send message (TEXT, FILE, or LINK)
- `GET /api/messages/conversation/{id}` - Retrieve paginated chat history
- `POST /api/messages/conversation/{id}/read` - Mark messages as read (Read receipt)

### 9. Notification Module (`/api/notifications`)
- `GET /api/notifications` - Retrieve in-app notifications
- `GET /api/notifications/unread-count` - Get count of unread notifications
- `PATCH /api/notifications/{id}/read` - Mark single notification as read
- `POST /api/notifications/mark-all-read` - Mark all notifications as read

### 10. OCR Module (`/api/ocr`)
- `POST /api/ocr/upload` - Upload image or scanned PDF for OCR processing
- `GET /api/ocr/jobs/{jobId}` - Check job processing status
- `GET /api/ocr/jobs/{jobId}/result` - Retrieve parsed structured text and extracted entities
- `GET /api/ocr/my-jobs` - List all user's OCR jobs
- `GET /api/ocr/documents` - List all uploaded documents

### 11. Resume Module (`/api/resume`)
- `POST /api/resume/generate` - AI LaTeX compilation and resume generation based on Digital Portfolio
- `GET /api/resume/download/{userId}?version=1` - Download compiled LaTeX / PDF resume

### 12. Content Module (`/api/content`)
- `POST /api/content/posts` - Publish social post, technical blog, or campus notice
- `GET /api/content/feed` - Community feed with likes, comment counts, and like state
- `GET /api/content/posts/{id}` - View single post
- `POST /api/content/posts/{id}/comments` - Add comment
- `GET /api/content/posts/{id}/comments` - View comment thread
- `POST /api/content/posts/{id}/like` - Toggle like / unlike
- `DELETE /api/content/posts/{id}` - Delete post (Owner or Admin)

### 13. Verification Module (`/api/verification`)
- `POST /api/verification/submit` - Submit document for identity/institutional proof
- `GET /api/verification/my` - View verification submissions
- `GET /api/verification/pending` - Admin lists pending verifications
- `PATCH /api/verification/{id}/review` - Admin approves or rejects verification

### 14. Admin Module (`/api/admin`)
- `GET /api/admin/dashboard` - Platform-wide KPI metrics and application status breakdown
- `GET /api/admin/users` - View all platform users
- `PATCH /api/admin/users/{id}/status` - Activate/deactivate user, update roles, or soft-delete
- `DELETE /api/admin/moderate/post/{postId}` - Content moderation: remove post with audit logging
- `GET /api/admin/audit-logs` - View system audit trail

### 15. Analytics Module (`/api/analytics`)
- `GET /api/analytics/placements` - Platform conversion rates, top hiring partners, and demand trends
- `GET /api/analytics/user-activity` - Candidate-specific funnel metrics (applications, interviews, offers)
- `GET /api/analytics/export/csv` - Export placement metrics as CSV report

---

## 🐳 Docker Deployment

To launch the full production ecosystem with Docker Compose:

```bash
docker compose up -d --build
```

Services started:
1. `uplink-postgres`: PostgreSQL 16 on port `5432`
2. `uplink-redis`: Redis 7 on port `6379`
3. `uplink-ai-service`: FastAPI AI Microservice on port `8000`
4. `uplink-backend`: Spring Boot Backend on port `8080`

To view logs:
```bash
docker compose logs -f backend
```
