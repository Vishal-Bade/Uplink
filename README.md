# Uplink - AI-Powered Academia-Industry Collaboration & Placement Platform

> **100% Complete Implementation** of the Backend Architecture, Supabase Database Architecture, and FastAPI AI Microservice.

[![Java](https://img.shields.io/badge/Java-21%20%7C%2024-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.2-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Database](https://img.shields.io/badge/Database-Supabase%20PostgreSQL%2017-blue.svg)](https://supabase.com/)
[![AI Microservice](https://img.shields.io/badge/AI%20Microservice-FastAPI%20%2F%20Python-teal.svg)](https://fastapi.tiangolo.com/)
[![License](https://img.shields.io/badge/License-MIT-purple.svg)](LICENSE)

---

## 🏛️ System Architecture Overview

The backend is built as a **Modular Monolith** using **Spring Boot 4 / Java 24 / Spring Security 7**, coupled with a **FastAPI Microservice** for AI/NLP processing, backed by **Supabase PostgreSQL** and **Redis** for caching, rate limiting, and queues.

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

The database runs natively on **Supabase PostgreSQL** (compatible with any PostgreSQL 16+ instance). All tables use **UUID** primary keys, **UTC timestamps**, soft delete flags (`is_deleted`), and indexes:

| Domain | Tables | Key Columns & Foreign Keys |
| :--- | :--- | :--- |
| **Core & Identity** | `roles`, `users`, `user_roles`, `user_verifications`, `admins` | `id (UUID)`, `email (UNIQUE)`, `password`, `is_verified`, `is_active`, `roles (M:N)` |
| **Profile & Academic** | `profiles`, `education`, `skills`, `user_skills`, `projects`, `experiences`, `certifications`, `achievements` | `user_id (FK)`, `cgpa`, `proficiency`, `years_of_exp`, `tech_stack`, `start_year`, `end_year` |
| **Opportunities & Apps**| `companies`, `recruiters`, `opportunities`, `opportunity_skills`, `opportunity_sources`, `applications`, `application_status_history` | `company_id (FK)`, `stipend_min/max`, `work_mode`, `status (APPLIED, UNDER_REVIEW, SHORTLISTED, INTERVIEW, SELECTED, REJECTED)`, `history audit` |
| **Communication** | `conversations`, `conversation_members`, `messages`, `posts`, `comments`, `likes` | `conversation_id (FK)`, `sender_id (FK)`, `message_type`, `is_read`, `read_at`, `post_id (FK)` |
| **AI & Assessments** | `assessments`, `assessment_questions`, `assessment_attempts`, `skill_gaps`, `recommendations` | `marks`, `options (JSON)`, `correct_answer`, `score`, `match_percentage`, `gap_data (JSON)` |
| **Admin & System** | `notifications`, `documents`, `ocr_jobs`, `audit_logs` | `uploaded_by (FK)`, `status (PENDING, PROCESSING, COMPLETED, FAILED)`, `metadata (JSON)` |

The initial DDL script is located in [`src/main/resources/schema.sql`](src/main/resources/schema.sql).

---

## ⚙️ Supabase Configuration

The application is pre-configured to connect to Supabase PostgreSQL using connection pooling:

```properties
# src/main/resources/application.properties
spring.datasource.url=jdbc:postgresql://aws-0-ap-south-1.pooler.supabase.com:6543/postgres?sslmode=require
spring.datasource.driver-class-name=org.postgresql.Driver
spring.datasource.username=postgres.phdfzmmehvglnnxchjrr
spring.datasource.password=Bhilwade@123

# JPA & Hibernate Settings
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.open-in-view=false

# Disable in-memory H2
spring.h2.console.enabled=false
```

### Applying Schema to Supabase:
1. Open your [Supabase Dashboard](https://supabase.com/dashboard) and go to **SQL Editor**.
2. Copy the entire content of [`src/main/resources/schema.sql`](src/main/resources/schema.sql).
3. Paste and click **Run** to provision all 27 tables, foreign keys, and indexes.

---

## 🤖 AI / Processing Services (FastAPI + Spring Boot)

The platform provides 6 intelligent AI services accessible via REST endpoints under `/api/ai/*`:

| # | AI Service | Endpoint | Description |
|---|---|---|---|
| 1 | **Skill Extraction NLP** | `POST /api/ai/extract-skills` | Tokenizes unstructured profile/project text and extracts recognized skills with high precision. |
| 2 | **Resume ATS Optimizer** | `POST /api/ai/parse-resume` | Computes simulated ATS compatibility score (0-100%), detects role, highlights strengths, and suggests keywords. |
| 3 | **Opportunity Parser** | `POST /api/ai/parse-opportunity` | Extracts work mode (`REMOTE`/`HYBRID`/`ONSITE`), seniority, and competencies from raw job descriptions. |
| 4 | **Recommendation Engine**| `POST /api/ai/recommendation-engine` | Jaccard and weighted similarity matching candidate skills against opportunity criteria. |
| 5 | **LLM Career Assistant** | `POST /api/ai/llm-query` | Context-aware AI queries for career advice, technical interview preparation, and resume bullet rewriting. |
| 6 | **OCR Processing** | `POST /api/ocr/upload` | Dispatches uploaded certificates and credentials to asynchronous OCR entity extraction workers. |

> **Resilient Architecture**: All AI endpoints in Spring Boot feature fallback heuristics. If the Python FastAPI microservice is offline, the backend continues to function reliably without throwing 500 errors.

---

## ⏱️ Background Jobs (`jobs/`)

The [`BackgroundJobScheduler`](src/main/java/com/example/Uplink_Backend/jobs/BackgroundJobScheduler.java) handles non-blocking automated tasks:
* **OCR Batch Processor**: Polls and processes pending document verification jobs every 60 seconds (`@Scheduled(fixedDelay = 60000)`).
* **Opportunity & Recommendation Sync**: Refreshes candidate match rankings every 6 hours (`cron = "0 0 */6 * * *"`).
* **Daily Analytics Digest**: Generates system placement KPIs every midnight (`cron = "0 0 0 * * *"`).

---

## 🚀 Quick Start Guide

### 1. Prerequisites
- **Java 21+** (Verified on Java 24)
- **Maven Wrapper** (included as `./mvnw`)
- **Python 3.10+** (optional, for running local AI FastAPI service)

### 2. Run Spring Boot Backend
```powershell
# Compile and verify dependencies
./mvnw clean compile

# Run tests and verify database connection
./mvnw test

# Start the Spring Boot server (Port 8080)
./mvnw spring-boot:run
```

Once running:
- **Interactive Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **OpenAPI 3 JSON Spec**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

### 3. Run FastAPI AI Microservice (Optional)
```bash
cd ai_service
pip install -r requirements.txt
uvicorn main:app --host 0.0.0.0 --port 8000 --reload
```
Interactive AI Docs: [http://localhost:8000/docs](http://localhost:8000/docs)

---

## 👥 Pre-Seeded Accounts (Auto-Loaded)

The database includes realistic test accounts pre-populated on startup:

| Role | Email | Password | Details |
| :--- | :--- | :--- | :--- |
| **Admin** | `admin@uplink.edu` | `Admin@123` | Full administrative & platform moderation privileges |
| **Recruiter** | `recruiter@uplink.edu` | `Recruiter@123` | Associated with Google / Microsoft, can post jobs & review applicants |
| **Student** | `student@uplink.edu` | `Student@123` | Complete portfolio (Education, Projects, Skills, Certificates) |

---

## 🌐 Complete REST API Reference (15 Modules + AI)

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
- `POST/GET/DELETE /api/profile/skills` - Add/remove skills with proficiency (`BEGINNER` to `EXPERT`)
- `POST/GET/DELETE /api/profile/projects` - Manage showcase portfolio projects
- `POST/GET/DELETE /api/profile/experiences` - Manage work history & internships
- `POST/GET/DELETE /api/profile/certifications` - Manage verified certifications
- `POST/GET/DELETE /api/profile/achievements` - Manage honors & achievements

### 3. Assessment Module (`/api/assessments`)
- `GET /api/assessments` - List active skill assessments
- `GET /api/assessments/{id}` - Fetch assessment questions
- `POST /api/assessments` - Create skill test (Admin / College Rep)
- `POST /api/assessments/submit` - Submit answers; automated scoring & skill verification
- `GET /api/assessments/my-attempts` - View past test attempts and percentages
- `GET /api/assessments/skill-gap-analysis` - Competency gap analysis against target role benchmark

### 4. Opportunity Module (`/api/opportunities`)
- `POST /api/opportunities` - Post job/internship/hackathon/event (Recruiter / Admin)
- `POST /api/opportunities/external-ingest` - Ingest opportunities from external APIs
- `GET /api/opportunities` - Search and filter (`type`, `workMode`, `location`, `keyword`)
- `GET /api/opportunities/{id}` - Get opportunity details with required skills list
- `PATCH /api/opportunities/{id}/status` - Activate or deactivate listing
- `PATCH /api/opportunities/{id}/verify` - Admin verification

### 5. Application Module (`/api/applications`)
- `POST /api/applications/apply` - Submit application with resume URL and cover letter
- `GET /api/applications/my` - Candidate tracks all their applications and current statuses
- `GET /api/applications/opportunity/{opportunityId}` - Recruiter reviews all applicants
- `PATCH /api/applications/{id}/status` - Recruiter updates status (`SHORTLISTED`, `INTERVIEW`, `SELECTED`, `REJECTED`)
- `GET /api/applications/{id}/history` - View complete lifecycle timeline of status changes

### 6. Recommendation Module (`/api/recommendations`)
- `GET /api/recommendations/opportunities` - AI-ranked opportunity recommendations with match scores
- `GET /api/recommendations/career-paths` - Tailored career roadmaps, salary benchmarks, and milestones
- `GET /api/recommendations/skills` - High-demand skills recommendations to bridge market gaps

### 7. Recruiter Module (`/api/recruiter`)
- `POST /api/recruiter/company` - Register or join a company
- `GET /api/recruiter/me` - Get recruiter profile & linked company
- `GET /api/recruiter/companies` - List registered companies
- `PUT /api/recruiter/companies/{id}/verify` - Admin verification of company
- `PUT /api/recruiter/verify/{recruiterId}` - Admin verification of recruiter

### 8. Messaging Module (`/api/messages`)
- `POST /api/messages/conversations` - Start direct or group conversation
- `GET /api/messages/conversations` - List all user conversations
- `POST /api/messages/send` - Send message (`TEXT`, `FILE`, or `LINK`)
- `GET /api/messages/conversation/{id}` - Retrieve paginated chat history
- `POST /api/messages/conversation/{id}/read` - Mark messages as read

### 9. Notification Module (`/api/notifications`)
- `GET /api/notifications` - Retrieve in-app notifications
- `GET /api/notifications/unread-count` - Get count of unread notifications
- `PATCH /api/notifications/{id}/read` - Mark single notification as read
- `POST /api/notifications/mark-all-read` - Mark all notifications as read

### 10. OCR Module (`/api/ocr`)
- `POST /api/ocr/upload` - Upload image or scanned PDF for OCR processing
- `GET /api/ocr/jobs/{jobId}` - Check job processing status
- `GET /api/ocr/jobs/{jobId}/result` - Retrieve parsed structured text and extracted entities
- `GET /api/ocr/my-jobs` - List current user's OCR jobs

### 11. Resume Module (`/api/resume`)
- `POST /api/resume/generate` - AI LaTeX compilation and resume generation based on Digital Portfolio
- `GET /api/resume/download/{userId}?version=1` - Download compiled LaTeX / PDF resume

### 12. Content Module (`/api/content`)
- `POST /api/content/posts` - Publish social post, technical blog, or campus notice
- `GET /api/content/feed` - Community feed with likes, comments, and like state
- `GET /api/content/posts/{id}` - View single post
- `POST /api/content/posts/{id}/comments` - Add comment
- `POST /api/content/posts/{id}/like` - Toggle like / unlike

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

### 16. AI Microservice API (`/api/ai`)
- `POST /api/ai/extract-skills` - Extract skills from unstructured text
- `POST /api/ai/parse-resume` - Score and optimize resume for ATS readability
- `POST /api/ai/parse-opportunity` - Parse job descriptions into structured fields
- `POST /api/ai/recommendation-engine` - Match candidate profiles with jobs
- `POST /api/ai/llm-query` - Contextual career advice, interview questions, and bullet rewriting

---

## 🐳 Docker Deployment

To launch the full containerized ecosystem with Docker Compose:

```bash
docker compose up -d --build
```

Services provisioned:
1. `uplink-postgres`: PostgreSQL 16 on port `5432`
2. `uplink-redis`: Redis 7 on port `6379`
3. `uplink-ai-service`: FastAPI AI Microservice on port `8000`
4. `uplink-backend`: Spring Boot Backend on port `8080`

To inspect service logs:
```bash
docker compose logs -f backend
```
