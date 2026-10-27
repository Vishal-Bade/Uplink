# UPLINK — Requirements Specification

> **Version:** 1.0  
> **Status:** Baseline for team review  
> **Last updated:** 26 October 2026

## 1. Purpose

This document defines the functional and non-functional requirements for UPLINK. Requirements are prioritized so the team can deliver a working student experience before implementing every long-term project module.

### Priority definitions

- **MVP / Must:** Required for the first usable release.
- **Next / Should:** Important, but can follow the first release.
- **Later / Could:** Long-term capability; not a blocker for MVP.

## 2. User roles

- `STUDENT`
- `RECRUITER`
- `ADMIN`

All protected endpoints and screens must enforce role and ownership rules on the server, not only by hiding UI controls. A mentor is initially a directory profile/capability rather than a separate account role.

## 3. Functional requirements

### 3.1 Application foundation and identity

| ID | Requirement | Priority |
|---|---|---|
| FR-001 | The system shall provide a consistent application startup flow and route users to onboarding, authentication, or the appropriate authenticated area. | MVP |
| FR-002 | The system shall support account registration, sign-in, sign-out, and session restoration. | MVP |
| FR-003 | The system shall support the agreed account-verification process before granting protected capabilities. | MVP |
| FR-004 | The system shall enforce role-based access for STUDENT, RECRUITER, and ADMIN. | MVP |
| FR-005 | The system shall provide a way for users to recover account access. | Next |
| FR-006 | The system shall store onboarding completion and non-sensitive preferences locally. Authentication secrets shall use an appropriate secure-storage mechanism, not plain AsyncStorage. | MVP |

### 3.2 Student profile

| ID | Requirement | Priority |
|---|---|---|
| FR-010 | A student shall be able to create and edit a professional profile. | MVP |
| FR-011 | The profile shall support basic identity/contact information and academic information such as institution, department/branch, program, and year/semester where applicable. | MVP |
| FR-012 | The profile shall support skills, projects, certifications, achievements, and experience records. | MVP basic fields; richer verification Next |
| FR-013 | A student shall be able to control which profile fields are visible to recruiters, subject to required platform fields. | Next |
| FR-014 | The system shall show profile completeness based on defined required fields. | Next |
| FR-015 | The system shall record verification status for fields or documents where verification is part of the workflow. | Next |

### 3.3 Feed and discovery

| ID | Requirement | Priority |
|---|---|---|
| FR-020 | The Feed shall present a useful summary of career-related content such as recommended/featured opportunities and upcoming events. | MVP |
| FR-021 | Feed sections shall link to the relevant opportunity, event, or detail screen. | MVP |
| FR-022 | The Feed shall display loading, empty, and error states instead of fabricated content. | MVP |
| FR-023 | Personalized ranking shall be introduced only when the data and ranking logic are available; otherwise the feed shall use clearly defined fallback ordering. | MVP |

### 3.4 Opportunities and applications

| ID | Requirement | Priority |
|---|---|---|
| FR-030 | The system shall represent internships, jobs, hackathons, competitions, scholarships, workshops, certifications, training, research, and apprenticeships. | MVP data model |
| FR-031 | Students shall be able to browse and search published opportunities. | MVP |
| FR-032 | Students shall be able to filter opportunities by type and relevant attributes such as location/work mode, skills, eligibility, and deadline when data exists. | MVP |
| FR-033 | An opportunity detail screen shall show organization, description, type, eligibility, requirements, location/work mode, compensation/benefit where applicable, deadline, source, and application method. | MVP |
| FR-034 | Students shall be able to save and unsave opportunities. | MVP |
| FR-035 | Students shall be able to apply through the supported application method and see a recorded application where the platform manages the application. | MVP |
| FR-036 | Students shall be able to view their applications and current status/history. | MVP |
| FR-037 | Application status changes shall be controlled and recorded with timestamps and actor/source. | MVP |
| FR-038 | The system shall distinguish external application links from applications submitted inside UPLINK. | MVP |
| FR-039 | Opportunity records shall include publication and verification/moderation states. | MVP |
| FR-040 | External opportunity ingestion shall use official APIs, public feeds, or explicitly authorized integrations; unauthorized scraping shall not be assumed. | Next |

### 3.5 Q&A

| ID | Requirement | Priority |
|---|---|---|
| FR-050 | Students shall be able to create a question with a title, detailed description, and one or more topic tags. | MVP |
| FR-051 | A question may include permitted attachments such as images or documents, subject to file-type and size limits. | Next |
| FR-052 | Students shall be able to browse questions through For You, Unanswered, Trending, and My Questions views. | MVP |
| FR-053 | Students shall be able to search questions and filter by topic. | MVP |
| FR-054 | Students shall be able to open a question and read its answers. | MVP |
| FR-055 | Eligible users shall be able to post an answer and reply where threaded replies are supported. | MVP |
| FR-056 | Users shall be able to indicate “I have the same doubt” once per question and remove that signal. | MVP |
| FR-057 | Users shall be able to mark answers as helpful; the question author or an authorized moderator shall be able to mark an answer as accepted. | MVP |
| FR-058 | The Q&A feed shall rank questions using Relevance, Need, and Doubt signals. The initial implementation shall be explainable and rule-based. | MVP |
| FR-059 | Ranking weights shall be configurable. A suggested initial default is Relevance 40%, Need 30%, Doubt 30%; weights shall be reviewed using observed outcomes. | MVP, tune later |
| FR-060 | Ranking shall account for recency and unanswered questions so that popularity does not permanently bury new or unresolved questions. | MVP |
| FR-061 | Users shall be able to report inappropriate or spam content; admins shall be able to moderate reported content. | MVP basic moderation |
| FR-062 | The system shall prevent duplicate same-doubt signals and basic vote/reaction manipulation. | MVP |

#### Initial Q&A score interpretation

Each dimension is normalized to a 0–100 score:

- **Relevance:** How closely a question matches a student's selected topics, skills, academic context, and followed areas.
- **Need:** How urgent or unresolved the question appears, using signals such as unanswered status, time waiting, and explicit “blocked/need help” indication if included.
- **Doubt:** How many distinct students indicate that they have the same doubt, with safeguards against repeated or abusive signals.

`Priority Score = (0.40 × Relevance) + (0.30 × Need) + (0.30 × Doubt)` is a starting configuration, not a proven model. Persist the component scores or enough input signals to explain why a question was ranked. Do not use sensitive personal attributes in ranking.

### 3.6 Mentors

| ID | Requirement | Priority |
|---|---|---|
| FR-070 | Students shall be able to browse a mentor directory. | MVP |
| FR-071 | Mentor listings shall show name, role/organization where approved, expertise, short bio, and availability information where supplied. | MVP |
| FR-072 | Students shall be able to search and filter mentors by expertise or domain. | MVP |
| FR-073 | Students shall be able to view a mentor profile. | MVP |
| FR-074 | Students shall be able to submit a mentorship booking/request using available time slots or a request form. | MVP basic request |
| FR-075 | The system shall record booking request status and prevent duplicate conflicting requests where applicable. | MVP |
| FR-076 | Calendar synchronization, paid sessions, and a dedicated MENTOR account role are not required for the MVP. | Later |

### 3.7 Recruiter and company workflows

| ID | Requirement | Priority |
|---|---|---|
| FR-080 | Recruiters shall be able to create recruiter and company profiles. | Next |
| FR-081 | Recruiter accounts and companies shall support manual ADMIN verification. | Next |
| FR-082 | Verified recruiters shall be able to create, edit, publish, close, and archive opportunities. | Next |
| FR-083 | Recruiters shall be able to view applications for their own opportunities. | Next |
| FR-084 | Recruiters shall be able to search permitted student profile fields using skills and relevant criteria. | Next |
| FR-085 | Recruiters shall be able to shortlist candidates and update application stages according to permissions. | Next |
| FR-086 | Recruiters shall not access private student profile fields or documents without authorization. | Next |

### 3.8 Administration and moderation

| ID | Requirement | Priority |
|---|---|---|
| FR-090 | ADMIN shall be able to review and approve/reject recruiter and company verification requests. | Next; minimal seed/admin support may be MVP |
| FR-091 | ADMIN shall be able to review and moderate reported Q&A content. | MVP basic moderation |
| FR-092 | ADMIN shall be able to moderate or unpublish inappropriate or unverified opportunities. | Next |
| FR-093 | Sensitive administrative actions shall be recorded in an audit log. | Next |
| FR-094 | ADMIN shall be able to manage reference data such as opportunity categories and Q&A topics. | Next |

### 3.9 Supporting capabilities

| ID | Requirement | Priority |
|---|---|---|
| FR-100 | The system shall provide in-app notifications for relevant events such as application updates, answers, accepted answers, and booking responses. | MVP basic |
| FR-101 | Email and push notifications shall be added through approved providers when the core workflows are stable. | Next |
| FR-102 | The system shall support secure document uploads and access-controlled downloads. | Next |
| FR-103 | The system shall generate resumes from verified/approved profile data. | Later |
| FR-104 | OCR/document extraction shall allow users or admins to review and correct extracted data before it becomes authoritative. | Later |
| FR-105 | A messaging system may support controlled student–recruiter or student–mentor communication. | Later |
| FR-106 | Advanced analytics and institution-level reports shall be added after reliable operational data exists. | Later |

## 4. Non-functional requirements

| ID | Requirement |
|---|---|
| NFR-001 Security | Use HTTPS in deployed environments, password hashing, secure session/token handling, server-side authorization, input validation, and protection against common web/API attacks. |
| NFR-002 Privacy | Collect only necessary personal and academic data. Enforce least-privilege access and give users clear visibility into profile sharing. |
| NFR-003 Data integrity | Enforce validation and database constraints for users, profiles, opportunities, applications, Q&A interactions, and booking requests. |
| NFR-004 Reliability | Show recoverable error states; avoid losing user input during recoverable failures; back up persistent data according to deployment policy. |
| NFR-005 Performance | Use pagination for feeds/lists, indexes for common filters, and caching only where useful. Do not load unbounded question or opportunity lists. |
| NFR-006 Scalability | Keep domain modules separated and APIs versionable; support growth without requiring a microservice for every feature. |
| NFR-007 Usability | Keep common student actions discoverable and reduce unnecessary steps. Use consistent layouts and plain-language labels. |
| NFR-008 Accessibility | Support adequate contrast, scalable text, screen-reader labels, touch targets, and keyboard navigation on web. |
| NFR-009 Compatibility | Support target Android/iOS versions and common screen sizes agreed by the team; respect status bars, cutouts, safe areas, keyboards, and navigation modes. |
| NFR-010 Maintainability | Use modular code, shared design tokens, consistent naming, documented APIs, linting, formatting, and tests. |
| NFR-011 Observability | Record application errors and security-relevant events without logging passwords, tokens, or unnecessary personal data. |
| NFR-012 AI transparency | AI-generated scores, extracted fields, or recommendations must be explainable enough for review and must not be presented as guaranteed decisions. |
| NFR-013 Fairness | Opportunity and Q&A ranking must not use irrelevant sensitive personal characteristics. Provide a way to report inaccurate or unfair results. |
| NFR-014 Content safety | Support reporting and moderation for questions, answers, attachments, and opportunity content. |

## 5. Data and security rules

- Server-side authorization is authoritative. Client-side role checks are for UX, not security.
- Students may edit their own profiles and applications only within allowed workflow states.
- Recruiters may manage only their own company, opportunities, and applicant workflows.
- Admin privileges must be explicit and auditable.
- Public profile fields and recruiter-visible fields must be defined separately from private account fields.
- Do not store passwords, refresh tokens, or other secrets in plain AsyncStorage.
- Uploaded files require type/size validation, access controls, and safe download handling.
- Q&A attachments and text must be reportable and moderatable.
- Recommendations and extracted data are advisory; users must be able to correct profile data.

## 6. Acceptance principle

A requirement is not complete merely because a screen or button exists. It is complete when the complete workflow works, validation and permissions are enforced, state persists where expected, error/empty/loading states are handled, and the acceptance condition can be tested.
