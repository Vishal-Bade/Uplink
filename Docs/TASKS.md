# UPLINK — Implementation Task Checklist

> **Version:** 1.0  
> **Status:** Initial backlog; tasks are not marked complete until verified  
> **Last updated:** 26 October 2026

Use this file as the working checklist. Assign an owner and due date in the team's tracker if needed. Do not mark an item complete based only on a generated UI or a successful JavaScript bundle.

## Milestone 0 — Foundation

- [ ] **T-0001** Review and approve `PROJECT.md`, `REQUIREMENTS.md`, `FEATURES.md`, `DESIGN_SYSTEM.md`, `SCREENS.md`, `NAVIGATION.md`, `COMPONENTS.md`, `ROADMAP.md`, and this backlog.
- [ ] **T-0002** Confirm repository layout for mobile, web, backend, processing services, and infrastructure.
- [ ] **T-0003** Confirm the package/app identifiers and environment naming for development, testing, and production.
- [ ] **T-0004** Add environment-variable templates; ensure secrets are excluded from version control.
- [ ] **T-0005** Add formatting, linting, and test commands for each codebase.
- [ ] **T-0006** Define API response/error conventions and versioning.
- [ ] **T-0007** Define PostgreSQL migration and seed-data strategy.
- [ ] **T-0008** Establish the shared design tokens from `DESIGN_SYSTEM.md`.

## Milestone 1 — Student mobile MVP

### App shell and identity

- [ ] **T-0101** Create the React Native CLI application shell using the agreed project structure.
- [ ] **T-0102** Configure one root `SafeAreaProvider` and one `NavigationContainer`.
- [ ] **T-0103** Implement bootstrap/session-loading without flashing the wrong screen.
- [ ] **T-0104** Implement three-slide onboarding with Skip/Continue and persisted completion.
- [ ] **T-0105** Implement sign-up, sign-in, sign-out, and session restoration.
- [ ] **T-0106** Select and configure secure storage for authentication secrets; do not store secrets in plain AsyncStorage.
- [ ] **T-0107** Add role-aware routing for STUDENT, RECRUITER, and ADMIN, even if recruiter/admin areas are placeholders until Milestone 2.

### Core backend

- [ ] **T-0110** Create Spring Boot core API skeleton and health endpoint.
- [ ] **T-0111** Connect PostgreSQL and configure migrations.
- [ ] **T-0112** Implement user/account and role persistence.
- [ ] **T-0113** Implement authentication and server-side authorization.
- [ ] **T-0114** Define common validation, error responses, pagination, and logging.
- [ ] **T-0115** Configure Redis only for agreed caching/short-lived/rate-limiting needs; keep the app functional without unnecessary cache dependencies.

### Student profile

- [ ] **T-0120** Create student profile model and API.
- [ ] **T-0121** Implement profile view and edit screens.
- [ ] **T-0122** Add academic information fields.
- [ ] **T-0123** Add basic skills, projects, certifications, achievements, and experience entries.
- [ ] **T-0124** Add profile validation and ownership tests.

### Feed and opportunity workflow

- [ ] **T-0130** Implement Feed screen using real API/seed data and clear loading/empty/error states.
- [ ] **T-0131** Define opportunity types and opportunity model.
- [ ] **T-0132** Implement opportunity listing, search, filters, sorting, and pagination.
- [ ] **T-0133** Implement opportunity detail screen.
- [ ] **T-0134** Implement save/unsave opportunity workflow.
- [ ] **T-0135** Implement internal application creation and explicit external-link handoff.
- [ ] **T-0136** Implement My Applications and status history.
- [ ] **T-0137** Test application ownership, valid status transitions, and duplicate submissions.

### Q&A

- [ ] **T-0140** Define Q&A topic taxonomy and question/answer data model.
- [ ] **T-0141** Implement question creation and validation.
- [ ] **T-0142** Implement Q&A feed tabs: For You, Unanswered, Trending, My Questions.
- [ ] **T-0143** Implement topic filtering and question search.
- [ ] **T-0144** Implement question detail and answer listing.
- [ ] **T-0145** Implement answer creation and helpful/accepted-answer actions.
- [ ] **T-0146** Implement same-doubt toggle with one signal per user/question.
- [ ] **T-0147** Implement transparent rule-based relevance/need/doubt ranking with configurable weights.
- [ ] **T-0148** Add recency/unanswered handling and anti-abuse limits.
- [ ] **T-0149** Implement content reporting and a basic admin moderation path.
- [ ] **T-0150** Test duplicate reactions, authorization, ranking inputs, and empty/new-question cases.

### Mentors and notifications

- [ ] **T-0160** Define mentor directory and profile data model.
- [ ] **T-0161** Implement mentor list, search, and domain filters.
- [ ] **T-0162** Implement mentor detail view.
- [ ] **T-0163** Implement booking request and status workflow.
- [ ] **T-0164** Implement basic in-app notification records and notification list.
- [ ] **T-0165** Test duplicate/conflicting booking requests and status permissions.

### UI quality and verification

- [ ] **T-0170** Implement the four-tab navigation: Feed, Jobs, Q&A, Mentors.
- [ ] **T-0171** Make Profile accessible through the avatar; do not add a Profile bottom tab.
- [ ] **T-0172** Apply the minimal design system and shared components.
- [ ] **T-0173** Verify safe areas, status bar, display cutouts, Android gesture navigation, and three-button navigation.
- [ ] **T-0174** Verify keyboard handling and modal/sheet layouts.
- [ ] **T-0175** Verify small/large Android screens and supported iOS layouts if iOS is in the test scope.
- [ ] **T-0176** Run lint, unit tests, bundle verification, and a real Android debug build.
- [ ] **T-0177** Perform an end-to-end student test: sign in → profile → discover/save/apply → track application → ask/answer question → request mentor session.

## Milestone 2 — Recruiter and admin

- [ ] **T-0201** Implement recruiter registration and company profile.
- [ ] **T-0202** Implement manual recruiter/company verification for ADMIN.
- [ ] **T-0203** Implement recruiter opportunity create/edit/publish/close workflows.
- [ ] **T-0204** Implement applicant review and application-stage management.
- [ ] **T-0205** Implement candidate search over permitted profile fields.
- [ ] **T-0206** Implement shortlist workflow.
- [ ] **T-0207** Build recruiter web shell and screens.
- [ ] **T-0208** Build admin web shell, verification queue, Q&A moderation, and opportunity moderation.
- [ ] **T-0209** Add audit logging for sensitive actions.
- [ ] **T-0210** Test role separation, data ownership, and unauthorized access attempts.

## Milestone 3 — Skills and recommendations

- [ ] **T-0301** Define skill taxonomy and proficiency scale.
- [ ] **T-0302** Implement skill assessment/self-assessment.
- [ ] **T-0303** Link student skills to opportunity requirements.
- [ ] **T-0304** Implement skill-gap comparison.
- [ ] **T-0305** Implement explainable rule-based opportunity matching.
- [ ] **T-0306** Capture recommendation feedback and evaluate relevance.
- [ ] **T-0307** Decide whether a separate FastAPI/ML service is justified by a measured requirement.

## Milestone 4 — Documents and supporting services

- [ ] **T-0401** Define document categories, file limits, access rules, and retention policy.
- [ ] **T-0402** Configure approved object/file storage.
- [ ] **T-0403** Implement secure document upload/download.
- [ ] **T-0404** Implement resume generation from profile data.
- [ ] **T-0405** Implement OCR/document extraction service with review/correction workflow.
- [ ] **T-0406** Add email/push notification providers.
- [ ] **T-0407** Evaluate and implement controlled messaging if still required.
- [ ] **T-0408** Evaluate authorized external opportunity APIs/feeds; document source terms and refresh policy.

## Milestone 5 — Hardening and release

- [ ] **T-0501** Complete security and privacy review.
- [ ] **T-0502** Add rate limits and abuse controls to authentication, search, Q&A, and upload endpoints.
- [ ] **T-0503** Add database backup and recovery verification.
- [ ] **T-0504** Add performance tests for opportunity and Q&A feeds.
- [ ] **T-0505** Complete accessibility and device/browser testing.
- [ ] **T-0506** Run user acceptance tests with students and recruiters.
- [ ] **T-0507** Evaluate profile completeness, opportunity relevance, application tracking correctness, Q&A usefulness, and response time.
- [ ] **T-0508** Document known limitations, deployment procedure, and rollback plan.
- [ ] **T-0509** Prepare final demo data and demonstration flows.

## Definition of done

A task is complete only when:

1. The implementation matches the requirement and design-system rules.
2. Loading, empty, error, disabled, and success states are handled where relevant.
3. Permissions and data validation are enforced.
4. The feature is tested at the appropriate level.
5. No unrelated functionality or configuration has been broken.
6. Any required documentation is updated.
