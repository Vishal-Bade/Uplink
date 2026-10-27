# UPLINK — Backend Architecture

> **Document:** Backend architecture and implementation boundaries  
> **Version:** 1.0  
> **Status:** Proposed implementation baseline for team review  
> **Prepared:** 27 October 2026  
> **Product baseline:** UPLINK project documents v1.0, dated 26 October 2026

## 1. Purpose

This document translates the approved UPLINK product documents into an implementation-level architecture for the core Spring Boot backend. It is intended for the backend developer and the mobile developer who will integrate with the API.

The backend is the authoritative owner of application data, validation, workflow rules, and access control. The React Native application must communicate with it through the REST API. The mobile application must not read or write UPLINK application tables directly through a Supabase client.

This document covers the **core backend only**. AI/ML, OCR, resume generation, ATS analysis, document extraction, payments, real-time messaging, and other deferred services are outside this implementation.

## 2. Scope and delivery phases

### 2.1 Milestone 1 — Student MVP backend

Implement the following vertical workflows:

1. Account registration, sign-in, sign-out, session restoration, and server-side role/ownership checks.
2. Student profile read/update, including academic and basic professional information.
3. Feed data assembled from real or clearly identified seeded opportunity records.
4. Opportunity listing, search, filtering, details, saving, and platform-managed applications.
5. Q&A questions, topics, answers, same-doubt signals, helpful/accepted answers, basic ranking, and content reporting.
6. Mentor directory, mentor profiles, booking requests, and request status retrieval.
7. In-app notifications for core workflow events.
8. Minimal admin support needed to seed, verify, or moderate MVP data, especially reported Q&A content.

### 2.2 Milestone 2 — Recruiter and administrator backend

Add after the student MVP foundation is stable:

- Recruiter and company profiles and verification requests.
- Admin review and manual verification workflows.
- Recruiter opportunity create/edit/publish/close/archive operations.
- Applicant review, application-stage updates, and shortlisting.
- Candidate discovery using only authorized student profile fields.
- Opportunity moderation, broader user/content moderation, and audit logging.

### 2.3 Explicitly out of scope

Do not implement in this backend phase:

- AI chatbots, LLM orchestration, AI/ML recommendation services, or career-path prediction.
- OCR, document extraction, resume generation, ATS scoring, or document-processing APIs.
- Payments, subscriptions, or paid mentorship.
- Real-time one-to-one messaging or a general social network.
- Automated job-portal scraping. External ingestion requires an approved and authorized source/API.
- A separate microservice for each business domain.

Keep boundaries clean so later services can be added without making the core API depend on them.

## 3. Proposed technology baseline

The following is a practical implementation proposal, not a requirement to use a specific patch version. Choose mutually compatible stable versions after checking the team's installed Java toolchain and the repository's existing build setup.

| Concern | Proposed technology | Responsibility |
|---|---|---|
| Runtime/framework | Java + Spring Boot | Core REST application |
| Build | Maven Wrapper | Repeatable local and CI builds |
| HTTP | Spring Web | REST controllers and HTTP handling |
| Validation | Jakarta Bean Validation | Request and domain input validation |
| Security | Spring Security | Authentication and endpoint authorization |
| Persistence | Spring Data JPA / Hibernate | Relational persistence |
| Database | PostgreSQL | Authoritative application data |
| Migrations | Flyway | Versioned schema changes |
| API documentation | OpenAPI / Swagger UI | Inspectable API contract |
| Tests | JUnit 5, Spring Boot Test, Testcontainers where practical | Unit and integration testing |
| Optional cache | Redis | Only measured caching or rate-limit use cases |

Supabase is the planned PostgreSQL hosting option. The backend should connect to PostgreSQL using a server-side database connection string and a least-privilege database user. Do not put Supabase service-role credentials, database credentials, or signing keys in the mobile app.

Redis is optional. The service should remain able to start and serve core requests when Redis is not configured, until a feature genuinely depends on it.

## 4. Architecture style

Use a **modular monolith**: one Spring Boot deployable with clear domain boundaries. Do not split the modules into independent services for the MVP.

Use a layered structure within each domain:

- **Controller:** HTTP route, request/response DTOs, authentication principal, HTTP status.
- **Application/service:** business use cases, workflow transitions, transaction boundary.
- **Domain/model:** domain rules and entities/value objects.
- **Repository:** database access.
- **Mapper:** entity-to-response and request-to-command transformations where useful.

Controllers must not contain business logic or call repositories directly for multi-step workflows. Do not expose JPA entities as API responses.

### 4.1 Suggested package structure

```text
com.uplink
├── UplinkApplication.java
├── config/
│   ├── OpenApiConfig.java
│   ├── JpaConfig.java
│   └── AppProperties.java
├── common/
│   ├── api/                 # pagination and common response helpers
│   ├── error/               # exception mapping and error DTOs
│   ├── security/            # shared authentication principal/utilities
│   └── validation/
├── identity/
│   ├── api/
│   ├── application/
│   ├── domain/
│   └── persistence/
├── profile/
│   ├── api/
│   ├── application/
│   ├── domain/
│   └── persistence/
├── opportunity/
│   ├── api/
│   ├── application/
│   ├── domain/
│   └── persistence/
├── application/              # opportunity applications
├── qa/                       # questions, answers, topics, reactions
├── mentor/                   # mentor directory and requests
├── notification/
└── moderation/               # reports and moderation actions
```

`identity`, `profile`, `opportunity`, `application`, `qa`, `mentor`, `notification`, and `moderation` are logical modules, not separate deployables. If the codebase uses a different package layout already, preserve the same ownership boundaries rather than forcing a cosmetic restructure.

## 5. Identity, roles, and authentication

### 5.1 Roles

The product baseline defines three account roles:

- `STUDENT`
- `RECRUITER`
- `ADMIN`

A mentor is initially a directory profile/capability, not a separate login role. Do not introduce `MENTOR` as a fourth primary role for the MVP.

### 5.2 Registration rules

- Public student registration creates a `STUDENT` account by default.
- A public request must not be able to assign `ADMIN` or `RECRUITER` by sending a role field.
- Recruiter onboarding and recruiter/company verification belong to Milestone 2.
- Admin accounts must be provisioned through a controlled administrative process, not public registration.

### 5.3 Authentication proposal

Use Spring Security with a token-based session mechanism. A proposed baseline is a short-lived access token plus a refresh-token mechanism that supports revocation. If refresh tokens are persisted, store a hash of each refresh token rather than its plaintext value.

The exact access-token lifetime, refresh-token lifetime, email/phone verification channel, password-reset provider, and token-rotation policy need team approval. They are not specified by the product documents and must not be silently treated as settled requirements.

Passwords must be stored using a modern adaptive password hash supported by Spring Security. Never store plaintext passwords. Never log passwords, access tokens, refresh tokens, or authorization headers.

### 5.4 Account verification

The requirements state that an agreed account-verification process must exist before protected capabilities are granted, but they do not specify whether verification is by email OTP, institutional email, administrator approval, or another method. Keep verification as an explicit account state and document the selected process before releasing registration.

Recommended state field: `account_status` with at least `PENDING_VERIFICATION`, `ACTIVE`, and `SUSPENDED`. The allowed operations for a pending account must be decided with the verification workflow.

## 6. Authorization and ownership rules

Authorization is enforced by the backend on every protected operation. Hiding a button in the mobile UI is not security.

| Actor | Allowed operations |
|---|---|
| STUDENT | Read published opportunities; manage own profile; save/unsave opportunities; create and view own platform-managed applications; create questions and answers; manage own same-doubt/helpful signals; submit mentor requests; read own notifications; report content. |
| RECRUITER | Milestone 2: manage only their own recruiter/company profile and opportunities; view applicants for their own opportunities; update allowed application stages; access only recruiter-visible student profile fields. |
| ADMIN | Manage approved verification/moderation workflows and platform reference data; review reports; perform sensitive operations with audit records. |
| Anonymous | Only explicitly public endpoints, such as health and published opportunity discovery if the team chooses to allow it. The MVP mobile flow may require authentication for student-specific content. |

Ownership checks must cover at least:

- A student can edit only their own profile and profile child records.
- A student can read only their own applications and application history.
- A student can remove only their own saved-opportunity record and reactions.
- A student cannot accept an answer on another user's question. The question author or an authorized moderator may accept it.
- A recruiter can view or modify only opportunities and applicants associated with their own company/account.
- Only authorized moderators/admins can hide/remove reported content or perform administrative verification.

Return `403 Forbidden` for an authenticated user lacking permission. Return `404 Not Found` where exposing the existence of another user's private resource would itself reveal information; use a consistent policy.

## 7. Domain modules and responsibilities

### 7.1 Identity

Owns user accounts, roles, credentials, account status, token/session operations, and current-user details.

### 7.2 Student profile

Owns academic and professional profile data: basic identity/contact fields, institution, department/branch, program, academic year/semester, skills, projects, experience, certifications, and achievements. The MVP supports basic records. A richer verified skill taxonomy and proficiency/evidence model is a later milestone.

Keep public/recruiter-visible fields separate from private account/authentication data. Do not expose password hashes, token data, private contact details, or fields the user has not authorized for display.

### 7.3 Opportunities

Owns opportunity records and their publication/verification visibility. Supported types:

`INTERNSHIP`, `JOB`, `HACKATHON`, `COMPETITION`, `SCHOLARSHIP`, `WORKSHOP`, `CERTIFICATION`, `TRAINING`, `RESEARCH`, `APPRENTICESHIP`.

An opportunity includes title, organization, description, type, eligibility, skills, location/work mode, compensation/benefit where applicable, deadline, application method, source, and publication/verification state.

MVP discovery must only return records that meet the agreed public visibility rule. Seeded/demo records must be identified honestly and must not be labelled verified unless they have actually been reviewed.

The Feed should compose its cards from appropriate opportunity records. Do not create a separate events subsystem unless a separate event lifecycle is approved. Workshops, competitions, hackathons, and similar records can appear in the feed as opportunity types.

### 7.4 Applications

Owns platform-managed applications, status transitions, and status history. Enforce duplicate-application prevention and valid status transitions.

External application links are not platform-managed submissions. Opening an external URL must not create an application record or show a submitted status. The opportunity detail response should clearly identify whether the application method is internal or external.

The exact application status enum is not specified in the product baseline. Agree a small status list before implementing the workflow; a proposed list is documented in `API_SPECIFICATION.md` and marked provisional.

### 7.5 Q&A

Owns topics, questions, answers, same-doubt signals, helpful-answer signals, accepted-answer state, ranking inputs, and reports.

Enforce one same-doubt signal per user/question and one helpful signal per user/answer. Allow the question author or an authorized moderator to accept an answer. Add basic rate limits and reporting/moderation support.

The initial transparent ranking configuration is:

`Priority = 0.40 × Relevance + 0.30 × Need + 0.30 × Doubt`

Each component is normalized to 0–100. Treat the weights as configurable starting values, not a validated recommendation model. Include recency and unanswered handling. Do not use sensitive personal attributes.

### 7.6 Mentor directory and requests

Owns mentor directory records, expertise, approved profile information, availability supplied by the project, and student booking requests. A dedicated MENTOR role is not required in the MVP.

The product baseline lists request states `REQUESTED`, `CONFIRMED`, `DECLINED`, `CANCELLED`, and `COMPLETED`. However, it does not specify who confirms or declines a request when mentor profiles do not have login accounts. The MVP must either define an admin-managed response process or defer mentor-side state changes until an authorized actor is agreed.

### 7.7 Notifications

Owns in-app notification records for core workflow events, including relevant application updates, answers/accepted answers, and mentor-request responses. Email and push providers are deferred until the core workflows are stable.

Notification creation should be triggered by successful domain actions and committed consistently with the originating transaction where possible. Do not fabricate notifications.

### 7.8 Moderation

Owns content reports and moderation actions. Q&A reporting/basic moderation is part of the MVP. Broader recruiter/company and opportunity moderation is expanded in Milestone 2.

## 8. API conventions

The detailed contract is in `API_SPECIFICATION.md`. The agreed baseline is:

- Prefix routes with `/api/v1`.
- Use resource-oriented REST routes and standard HTTP methods/status codes.
- Use UUID identifiers in public API paths.
- Use ISO-8601 UTC timestamps.
- Validate query parameters and request bodies on the server.
- Use bounded pagination for list endpoints; never return unbounded feeds.
- Keep DTOs separate from persistence entities.
- Use a consistent error format with a machine-readable `code` and field-level validation details.
- Do not expose internal exception messages or stack traces.
- Add OpenAPI documentation and keep it aligned with the implementation.

## 9. Database and persistence

PostgreSQL is the authoritative store. Use Flyway migrations as the only normal mechanism for changing the schema in shared environments. Do not rely on Hibernate automatic schema creation/update in production.

Use database constraints for uniqueness, foreign keys, valid relationships, and duplicate reaction/application prevention. Index common filters, ownership queries, feed ordering, and foreign keys after inspecting actual query patterns.

The proposed relational model is in `DATABASE_SCHEMA.md`. Where the project documents specify a capability but not the exact physical representation, the schema document identifies the suggested design and any decision that still needs confirmation.

## 10. Error handling and logging

Use centralized exception handling. Expected validation, authentication, authorization, missing-resource, conflict, and rate-limit errors should map to predictable HTTP responses.

Log operational events with correlation/request IDs. Do not log credentials, tokens, private profile fields, full question content unnecessarily, or sensitive uploaded content. Log moderation and administrative actions with actor, action, target, timestamp, and outcome.

## 11. Testing strategy

At minimum, add:

- Unit tests for domain/service rules and state transitions.
- Repository/integration tests for constraints and queries.
- Controller tests for request validation and response contracts.
- Security tests for role and ownership boundaries.
- Workflow tests for register/login, profile edit, save/apply, Q&A reactions, mentor request, and notification creation.
- Duplicate-submission tests for applications and Q&A reactions.
- Migration tests against PostgreSQL-compatible behavior; prefer PostgreSQL Testcontainers over relying only on H2 for database-specific constraints.

A feature is complete only when the workflow persists correctly, authorization is enforced, error/empty cases are handled, and tests cover its important rules.

## 12. Configuration and secrets

Use environment-based configuration for at least:

- Active Spring profile.
- Database JDBC URL, username, and password.
- JWT/access-token signing secret or private key.
- Refresh-token settings if used.
- CORS allowed origins.
- Server port and public API base URL.
- Optional Redis connection settings.

Commit `.env.example` or equivalent placeholders only. Never commit `.env`, database passwords, service-role keys, signing keys, private certificates, or production credentials. Use a separate secret store in deployed environments.

## 13. Decisions to confirm before implementation is considered contract-stable

1. **Account verification:** Which exact verification channel/process applies to students at MVP launch?
2. **Session policy:** Access/refresh token lifetimes, rotation, revocation, and secure storage expectations.
3. **Application status model:** Confirm the allowed statuses and who may move an application between them.
4. **Internal application form:** What fields, if any, are submitted inside UPLINK for an internal application?
5. **Opportunity publication:** Who may seed/publish MVP opportunity data, and what makes a record verified versus merely available?
6. **Mentor request response:** Who confirms/declines requests while mentors have no separate login role?
7. **MVP moderation:** Which admin account/process handles reports and opportunity corrections before the full admin portal exists?
8. **Profile privacy:** Which fields are visible to other students, public users, and recruiters?
9. **Event representation:** Confirm that Feed event-like cards are represented by opportunity types such as workshops/competitions unless a separate event module is approved.

Until these are agreed, implement the data structures and service boundaries in a way that keeps the unresolved rule configurable rather than hard-coding assumptions.
