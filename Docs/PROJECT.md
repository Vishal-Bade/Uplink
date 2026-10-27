# UPLINK — Project Definition

> **Product:** UPLINK  
> **Project title:** A Unified Campus Career and Professional Ecosystem for Skill Matching, Opportunity Management and Industry Interaction  
> **Document status:** Project baseline for team review  
> **Version:** 1.0  
> **Last updated:** 26 October 2026

## 1. Product vision

UPLINK is a campus-focused career and professional ecosystem that brings student profiles, skills, career opportunities, applications, and industry interaction into a connected platform. It is intended to reduce the fragmentation students face when maintaining professional information and discovering opportunities, while giving verified recruiters a more structured way to publish opportunities and identify suitable candidates.

UPLINK is not intended to be a generic social network or a copy of a job portal. Its central purpose is to connect a student's academic and professional information with relevant opportunities and career-development activities.

## 2. Problem being addressed

Students commonly keep resumes, skills, projects, certifications, internships, job applications, and professional profiles across separate platforms. This makes it difficult to maintain consistent information and follow the progress of applications. Recruiters also need to search across multiple sources to identify students who match their requirements.

UPLINK addresses this problem by providing a structured student profile, an opportunity discovery and application workflow, and controlled recruiter interaction. Skill mapping, recommendations, document processing, and other supporting capabilities will be introduced in stages.

## 3. Product goals

1. Provide students with one place to maintain academic and professional information, including skills, projects, certifications, achievements, and experience.
2. Provide a centralized way to discover and manage internships, jobs, hackathons, competitions, and other career-related opportunities.
3. Support skill assessment and skill mapping so that strengths and development gaps can be identified and related to opportunity requirements.
4. Give verified recruiters tools to publish opportunities, find eligible candidates, review profiles, and manage shortlists.
5. Add supporting services such as resume generation, document processing, messaging, notifications, and security controls when the core workflows are stable.
6. Provide a useful student knowledge-sharing space through the newly approved **Q&A** module.

## 4. Users and roles

The initial system uses three primary account roles:

| Role | Main responsibilities |
|---|---|
| **STUDENT** | Maintain a professional profile; discover and save opportunities; apply and track applications; use the mentor directory; ask and answer questions in Q&A. |
| **RECRUITER** | Maintain a recruiter/company profile; publish opportunities after verification; review permitted student information; manage applicants and shortlists. |
| **ADMIN** | Manually verify users/recruiters/companies and opportunities as configured; moderate content; manage platform settings and audit records. |

**Role clarification:** An academician or placement-cell user is not a separate primary role in the current baseline. Institutional oversight is handled through the ADMIN role unless the team formally approves another role later. The T&P cell is not assumed to be responsible for manual account verification. Mentors are represented as directory profiles or approved mentor capabilities for the first version; a separate MENTOR login role is not required unless a later workflow demands it.

## 5. Platform scope

- **Student application:** Android and iOS mobile application built with React Native CLI.
- **Recruiter and administrator interfaces:** Web application built with React. The recruiter and admin experiences are planned separately from the student mobile navigation.
- **Core backend:** Spring Boot REST API.
- **Specialized processing:** FastAPI services for selected OCR, document-processing, resume, or AI/ML workloads when those features are introduced.

The mobile bottom navigation is defined as **Feed**, **Jobs**, **Q&A**, and **Mentors**. The student's **Profile** is opened from the avatar in the top-right header; it is not a fifth bottom-navigation tab. The Jobs tab may use the screen heading **Explore Opportunities**.

## 6. Initial release boundary

The first implementation milestone is the **Student Mobile MVP and the minimum backend needed to make its workflows real**.

### Included in the first release

- App startup, onboarding, authentication entry points, and session handling.
- Student profile basics and editable professional information.
- Feed with opportunity/event discovery sections and clear loading, empty, and error states.
- Opportunity discovery under Jobs, including search, filters, detail view, bookmarking, applying, and application status tracking.
- Q&A question feed, question details, answers, same-doubt signal, and ask-question form.
- Mentor directory, mentor profile, and a basic booking/request workflow.
- In-app notifications for core events where they are needed by these workflows.
- Basic admin support needed to seed, verify, or moderate data during the MVP; a full admin dashboard is a later milestone.

### Explicitly deferred from the first release

- Full recruiter portal and advanced candidate search.
- Comprehensive skill assessments and automated skill-gap analysis.
- Production-grade AI recommendation and career-path prediction.
- Resume builder, ATS scoring, OCR, and automated document extraction.
- Real-time one-to-one messaging and a general social-media feed.
- Automated external job-portal ingestion unless an authorized API or feed is available and approved.
- Payment gateway, premium subscriptions, and paid mentorship.
- Advanced analytics and institution-wide dashboards.

Mockups may show future concepts, but a visual element must not be represented as a working feature until its underlying workflow exists. In particular, never display a fabricated ATS percentage or recommendation score as a real result. Demo data must be clearly identified as sample data.

## 7. Opportunity types

The opportunity model should support these categories, with the ability to add more through controlled configuration:

- Internship
- Job
- Hackathon
- Competition
- Scholarship
- Workshop
- Certification
- Training
- Research
- Apprenticeship

Each opportunity should have a type, title, organization, description, eligibility, required skills where applicable, location/work mode, compensation or benefit where applicable, application method, deadline, source, and verification/publication status.

## 8. Q&A product addition

Q&A is an approved addition to the original Phase-I project plan. It is a structured question-and-answer module, not a generic community chat. Students can ask questions, add topic tags, answer other questions, indicate that they have the same doubt, and mark or vote on helpful answers. The feed can rank questions using **Relevance**, **Need**, and **Doubt** signals.

The initial ranking should be transparent and rule-based. A suggested starting formula is `Priority = 0.40 × Relevance + 0.30 × Need + 0.30 × Doubt`, with each input normalized to 0–100. Keep weights configurable and validate them with actual usage. Popularity alone must not determine ranking; unanswered questions and recency must be considered so that new or important questions are not buried.

## 9. Product principles

- **Student-first:** The mobile experience should make common student tasks quick and understandable.
- **Trust:** Recruiters, companies, and opportunities must have visible verification states and appropriate review workflows.
- **Evidence over decoration:** Scores, matches, ratings, and status labels must be based on real data or clearly labeled as examples.
- **Minimal interface:** Use a restrained, professional visual system; avoid decorative 3D icons, excessive shadows, gradients, emoji-based controls, and oversized rounded cards.
- **Privacy by default:** Show only information that a user has permission to access.
- **Incremental delivery:** Build one complete workflow at a time rather than implementing every module together.
- **AI as assistance:** AI output must be reviewable and correctable; the platform must remain usable when an AI service is unavailable.

## 10. High-level architecture baseline

| Layer | Planned technology | Responsibility |
|---|---|---|
| Student mobile app | React Native CLI, JavaScript/TypeScript as agreed per repository | Student-facing mobile experience. |
| Web app | React, JavaScript, Tailwind CSS | Recruiter and admin interfaces. |
| Core backend | Java, Spring Boot | Authentication/authorization, profiles, opportunities, applications, Q&A, mentor requests, moderation, and core APIs. |
| Data store | PostgreSQL hosted through Supabase | Persistent application records and relationships. |
| Cache/temporary state | Redis | Caching and selected short-lived or rate-limiting workloads. |
| Processing services | Python, FastAPI | Specialized OCR, document processing, resume, and AI/ML workloads when required. |
| File storage | Supabase Storage or an approved object-storage provider | Resumes, profile documents, and permitted attachments. |
| Delivery/infrastructure | Docker; Cloudflare in front of public endpoints as appropriate | Packaging, deployment, traffic protection, and delivery. |

This is the planned architecture from the Phase-I project report, not a claim that every service is already implemented. Keep the first backend modular and avoid splitting every domain into a separate microservice. The Spring Boot core should own authoritative business rules and data; FastAPI should be introduced only for workloads that benefit from Python libraries or isolated processing.

## 11. Success criteria for the first release

The first release is ready for review when:

- A student can complete onboarding/authentication and reach the main application reliably.
- A student can create and update a profile and see those changes after relaunch.
- A student can search/filter opportunities, open details, save an opportunity, apply, and see application status.
- A student can ask a question, answer a question, indicate the same doubt, and view a ranked Q&A feed.
- A student can find a mentor and submit a booking request.
- The application handles safe areas, status bars, display cutouts, keyboards, loading, empty, and error states correctly on supported Android devices.
- Role checks and API validation prevent users from performing actions they are not authorized to perform.
- Core flows have automated or repeatable manual tests and no known release-blocking defects.

## 12. Related documents

- [REQUIREMENTS.md](./REQUIREMENTS.md) — functional and non-functional requirements.
- [FEATURES.md](./FEATURES.md) — feature catalogue and release boundaries.
- [DESIGN_SYSTEM.md](./DESIGN_SYSTEM.md) — visual and interaction rules.
- [SCREENS.md](./SCREENS.md) — screen inventory and screen responsibilities.
- [NAVIGATION.md](./NAVIGATION.md) — route structure and navigation rules.
- [COMPONENTS.md](./COMPONENTS.md) — reusable component inventory.
- [ROADMAP.md](./ROADMAP.md) — staged delivery plan.
- [TASKS.md](./TASKS.md) — implementation checklist.
