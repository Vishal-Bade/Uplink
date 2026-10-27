# UPLINK — Development Roadmap

> **Version:** 1.0  
> **Status:** Baseline for team review  
> **Last updated:** 26 October 2026

## 1. Roadmap approach

UPLINK will be delivered in vertical slices. Each milestone should produce a usable, testable workflow. Do not build every module from the project requirement diagram in parallel. The original Phase-I report describes the long-term objectives; this roadmap orders them by dependency and keeps the student MVP focused.

## 2. Milestones

### Milestone 0 — Product and repository foundation

**Outcome:** A reviewed project baseline and a buildable repository structure.

- Finalize the nine project documents.
- Confirm mobile, web, backend, and processing-service repository boundaries.
- Define environment configuration and secret handling.
- Establish linting, formatting, basic testing, and CI checks.
- Confirm API conventions and initial database migration strategy.

**Exit criteria:** Developers can run the agreed app/service skeletons; requirements, routes, and tokens have one agreed source of truth.

### Milestone 1 — Student mobile MVP and core backend

**Outcome:** A student can use the key UPLINK workflows with persistent backend data.

- Application bootstrap, onboarding, sign-in/sign-up, session restoration.
- Student profile basics.
- Feed with real/seeded opportunity and event data, with honest data labels.
- Jobs/Explore: search, filters, opportunity details, save, apply, and application tracking.
- Q&A: question feed, ask question, question detail, answers, same-doubt signal, helpful/accepted answer, basic ranking, and reporting.
- Mentors: directory, profile, booking request, and request status.
- In-app notifications for the MVP workflows.
- Core Spring Boot APIs and PostgreSQL data model for these flows.
- Safe-area, responsive, accessibility, loading, empty, and error-state verification.

**Exit criteria:** The full student path works from account entry through profile, opportunity/application, Q&A, and mentor request; data survives relaunch; permissions and validation are enforced.

### Milestone 2 — Recruiter and administrator workflows

**Outcome:** Verified recruiters can publish opportunities and manage applicants; admins can oversee trust and moderation.

- Recruiter/company profiles and verification requests.
- ADMIN manual verification queue.
- Opportunity create/edit/publish/close workflows.
- Applicant review and shortlisting.
- Candidate discovery using permitted profile fields.
- Admin opportunity moderation, Q&A moderation, and audit logging.
- React web dashboard for recruiter and admin experiences.

**Exit criteria:** Recruiter and admin workflows are role-protected, auditable, and tested end to end.

### Milestone 3 — Structured skills and recommendation

**Outcome:** Student skills are represented consistently and can be compared with opportunity requirements.

- Skill taxonomy and proficiency model.
- Skill assessment/self-assessment workflow.
- Evidence/source and verification metadata for skills.
- Skill-gap analysis against target roles/opportunities.
- Rule-based opportunity matching baseline.
- Recommendation explanation and feedback capture.
- Optional FastAPI/ML service only when a measured need exists.

**Exit criteria:** The system can explain why a skill gap or opportunity match was produced and allows users to correct underlying data.

### Milestone 4 — Documents and supporting services

**Outcome:** Students can manage professional documents and use supporting services.

- Secure file upload and storage policy.
- Resume generation from profile data.
- OCR/document extraction through a separate processing service.
- Human review/correction of extracted information.
- Email/push notification providers.
- Messaging where justified by approved use cases.
- Authorized external opportunity APIs/feeds if access and terms permit.

**Exit criteria:** Document workflows are secure, reviewable, observable, and resilient to processing-service failures.

### Milestone 5 — Hardening, evaluation, and deployment

**Outcome:** A stable integrated system ready for demonstration and controlled use.

- Security review and role/ownership tests.
- Performance, pagination, and load checks.
- Backup and recovery verification.
- Accessibility and device/browser testing.
- User acceptance testing with students and recruiters.
- Evaluate profile completeness, opportunity relevance, Q&A usefulness, application tracking correctness, and system response time.
- Deployment, monitoring, documentation, and final demonstration preparation.

**Exit criteria:** Release checklist passed, known limitations documented, and operational ownership assigned.

## 3. Dependencies

- Profile data is required before reliable skill mapping or personalization.
- Opportunity taxonomy and structured requirements are required before reliable matching.
- Authentication and roles are required before recruiter/admin workflows.
- Question/answer persistence and basic abuse prevention are required before ranking optimization.
- Secure storage and document permissions are required before resume/OCR features.
- Real usage data is required before claiming the ranking algorithm is effective.

## 4. Explicitly deferred items

Payment gateway, premium subscriptions, advanced chatbot, fully automated external portal scraping, broad social networking, and institution-wide advanced analytics are not on the critical path. They require separate approval, architecture review, and a clear user need.

## 5. Release rule

A milestone is not complete when screens merely render. It is complete when the user can perform the workflow, data is saved correctly, authorization is enforced, failure states are handled, and the workflow is tested.
