# UPLINK — Database Schema

> **Document:** PostgreSQL logical data model and migration guidance  
> **Version:** 1.0  
> **Status:** Proposed schema for team review  
> **Prepared:** 27 October 2026  
> **Database:** PostgreSQL (planned hosting: Supabase PostgreSQL)

## 1. Purpose and scope

This document proposes the relational data model for the UPLINK core backend. It is based on the approved product requirements and feature catalogue. The product documents define the information and workflows but do not prescribe physical table names, column types, or all enum values; those details are proposed here and must be reviewed before the first production migration.

The schema supports:

- Student identity and professional profiles.
- Opportunities and saved opportunities.
- Platform-managed applications and status history.
- Q&A topics, questions, answers, reactions, reports, and moderation.
- Mentor directory profiles and student booking requests.
- In-app notifications.
- Future recruiter/company/admin workflows at the appropriate milestone.

AI, OCR, resume generation, ATS scoring, documents, payments, messaging, and advanced recommendation storage are intentionally excluded.

## 2. General database conventions

### 2.1 Naming and identifiers

- Use `snake_case` for schema objects and columns.
- Use UUID primary keys for externally addressable entities.
- Use `timestamptz` for timestamps and store/interpret them in UTC.
- Use `date` for deadline or academic dates that do not need a time.
- Use `text` for user-entered prose unless a bounded length is part of validation.
- Add `created_at` and `updated_at` to mutable business records where useful.
- Use foreign keys and unique constraints to enforce relationships and prevent duplicate interactions.

### 2.2 Enum representation

For a student project, PostgreSQL `varchar` columns with application-level enums plus `CHECK` constraints can be easier to evolve than native PostgreSQL enum types. The backend team may use native enums if migrations are handled carefully. Keep the API enum spelling and database mapping consistent.

### 2.3 Ownership

The authenticated user ID comes from the server-side principal. Never trust a `user_id`, `student_id`, or ownership field supplied by a client to decide which record the caller may modify.

### 2.4 Soft deletion and moderation

Do not add `deleted_at` to every table by default. Use explicit state fields for questions/opportunities that need to be hidden, removed, closed, or archived. Preserve moderation records for auditability. Decide whether user-account deletion requires anonymization or hard deletion under the team's privacy policy.

## 3. High-level entity relationships

```mermaid
 erDiagram
   users ||--o| student_profiles : has
   users ||--o{ profile_skills : owns
   users ||--o{ profile_projects : owns
   users ||--o{ profile_experiences : owns
   users ||--o{ profile_certifications : owns
   users ||--o{ profile_achievements : owns
   users ||--o{ refresh_sessions : has
   users ||--o{ saved_opportunities : saves
   opportunities ||--o{ saved_opportunities : saved_as
   users ||--o{ applications : submits
   opportunities ||--o{ applications : receives
   applications ||--o{ application_status_history : has
   users ||--o{ qa_questions : asks
   qa_questions ||--o{ qa_question_topics : tagged
   qa_topics ||--o{ qa_question_topics : assigned
   qa_questions ||--o{ qa_answers : has
   users ||--o{ qa_answers : writes
   qa_questions ||--o{ qa_same_doubt : receives
   users ||--o{ qa_same_doubt : signals
   qa_answers ||--o{ qa_answer_helpful : receives
   users ||--o{ qa_answer_helpful : marks
   users ||--o{ content_reports : submits
   mentors ||--o{ mentor_expertise : has
   mentors ||--o{ mentor_requests : receives
   users ||--o{ mentor_requests : submits
   users ||--o{ notifications : receives
 ```

The diagram is logical. Specific optional relationships and foreign-key behavior are described below.

## 4. Identity and session tables

### 4.1 `users`

One row per account. Roles are `STUDENT`, `RECRUITER`, and `ADMIN`.

| Column | Type | Rules / meaning |
|---|---|---|
| `id` | UUID | PK |
| `email` | varchar(320) | Not null; normalized lowercase; unique |
| `password_hash` | text | Not null; adaptive password hash only |
| `role` | varchar(20) | Not null; `STUDENT`, `RECRUITER`, `ADMIN` |
| `account_status` | varchar(30) | Not null; proposed `PENDING_VERIFICATION`, `ACTIVE`, `SUSPENDED` |
| `email_verified_at` | timestamptz | Nullable; set only if email verification is selected |
| `last_login_at` | timestamptz | Nullable |
| `created_at` | timestamptz | Not null |
| `updated_at` | timestamptz | Not null |

Constraints/indexes:

- Unique index on normalized `email`.
- Check constraint for supported roles and account statuses.
- Never expose `password_hash` through API DTOs.
- Public registration must always create `STUDENT`; role assignment for `ADMIN`/`RECRUITER` is controlled by backend provisioning/verification rules.

**Open decision:** The source requirements require account verification but do not define its channel. Keep `account_status` independent of the chosen email/institutional/admin verification implementation.

### 4.2 `refresh_sessions` (if refresh-token sessions are used)

| Column | Type | Rules / meaning |
|---|---|---|
| `id` | UUID | PK |
| `user_id` | UUID | FK → `users.id`, not null |
| `token_hash` | text | Not null; hash of refresh token, never plaintext |
| `expires_at` | timestamptz | Not null |
| `revoked_at` | timestamptz | Nullable |
| `created_at` | timestamptz | Not null |
| `last_used_at` | timestamptz | Nullable |
| `device_label` | varchar(120) | Nullable; non-sensitive optional label |

Add an index on `(user_id, expires_at)` and a uniqueness strategy for active token hashes. This table is only needed if the selected session design uses refresh tokens.

## 5. Student profile tables

### 5.1 `student_profiles`

One-to-one extension of a student account. The user account remains the source of authentication identity.

| Column | Type | Rules / meaning |
|---|---|---|
| `id` | UUID | PK |
| `user_id` | UUID | FK → `users.id`; unique; not null |
| `first_name` | varchar(100) | Not null |
| `last_name` | varchar(100) | Nullable or not null per registration UX |
| `headline` | varchar(180) | Nullable |
| `bio` | text | Nullable |
| `institution` | varchar(200) | Nullable until completed |
| `department` | varchar(160) | Nullable |
| `program` | varchar(120) | Nullable |
| `academic_year` | smallint | Nullable; validate permitted range |
| `semester` | smallint | Nullable; validate permitted range |
| `location` | varchar(180) | Nullable |
| `created_at` | timestamptz | Not null |
| `updated_at` | timestamptz | Not null |

Add `UNIQUE(user_id)`. Do not copy password/email into this profile table. Contact information visibility must be handled separately from authentication data.

### 5.2 Basic profile child records

These tables support MVP basic profile fields. The more advanced skill taxonomy, proficiency evidence, verification metadata, and assessment model are a later milestone.

#### `profile_skills`

| Column | Type | Rules / meaning |
|---|---|---|
| `id` | UUID | PK |
| `user_id` | UUID | FK → `users.id`, not null |
| `skill_name` | varchar(120) | Not null |
| `proficiency_label` | varchar(40) | Nullable; self-described only unless verified |
| `created_at` | timestamptz | Not null |
| `updated_at` | timestamptz | Not null |

Index by `user_id`; consider a normalized-name uniqueness constraint per user if duplicates should be blocked. Do not claim that self-entered proficiency is independently verified.

#### `profile_projects`

| Column | Type | Rules / meaning |
|---|---|---|
| `id` | UUID | PK |
| `user_id` | UUID | FK → `users.id`, not null |
| `title` | varchar(180) | Not null |
| `description` | text | Nullable |
| `project_url` | text | Nullable; validate allowed URL schemes |
| `technologies` | JSONB | Nullable array of technology-name strings; validate as an array of strings |
| `started_on` | date | Nullable |
| `ended_on` | date | Nullable |
| `created_at` | timestamptz | Not null |
| `updated_at` | timestamptz | Not null |

#### `profile_experiences`

| Column | Type | Rules / meaning |
|---|---|---|
| `id` | UUID | PK |
| `user_id` | UUID | FK → `users.id`, not null |
| `organization` | varchar(180) | Not null |
| `role_title` | varchar(160) | Not null |
| `description` | text | Nullable |
| `started_on` | date | Nullable |
| `ended_on` | date | Nullable; null may represent current role |
| `created_at` | timestamptz | Not null |
| `updated_at` | timestamptz | Not null |

#### `profile_certifications`

| Column | Type | Rules / meaning |
|---|---|---|
| `id` | UUID | PK |
| `user_id` | UUID | FK → `users.id`, not null |
| `name` | varchar(180) | Not null |
| `issuing_organization` | varchar(180) | Nullable |
| `issued_on` | date | Nullable |
| `expires_on` | date | Nullable |
| `credential_url` | text | Nullable |
| `created_at` | timestamptz | Not null |
| `updated_at` | timestamptz | Not null |

#### `profile_achievements`

| Column | Type | Rules / meaning |
|---|---|---|
| `id` | UUID | PK |
| `user_id` | UUID | FK → `users.id`, not null |
| `title` | varchar(180) | Not null |
| `description` | text | Nullable |
| `achieved_on` | date | Nullable |
| `created_at` | timestamptz | Not null |
| `updated_at` | timestamptz | Not null |

All child-table write operations must verify the authenticated user owns the parent profile. Do not add file/document columns in this MVP schema.

## 6. Opportunities and applications

### 6.1 `opportunities`

| Column | Type | Rules / meaning |
|---|---|---|
| `id` | UUID | PK |
| `title` | varchar(220) | Not null |
| `organization_name` | varchar(200) | Not null for MVP; later may link to `companies` |
| `description` | text | Not null |
| `opportunity_type` | varchar(30) | Not null; one of the approved types |
| `eligibility` | text | Nullable |
| `location` | varchar(180) | Nullable |
| `work_mode` | varchar(20) | Nullable; proposed `REMOTE`, `ONSITE`, `HYBRID`, `NOT_SPECIFIED` |
| `compensation_text` | varchar(220) | Nullable; display text, not a numeric guarantee |
| `application_method` | varchar(20) | Not null; `INTERNAL` or `EXTERNAL` |
| `application_url` | text | Required for `EXTERNAL`; normally null for `INTERNAL` |
| `source_name` | varchar(180) | Nullable |
| `source_url` | text | Nullable |
| `deadline_date` | date | Nullable; date-only deadline because the product requirements do not specify a deadline time |
| `publication_status` | varchar(20) | Not null; proposed `DRAFT`, `PUBLISHED`, `CLOSED`, `ARCHIVED`, `UNPUBLISHED` |
| `verification_status` | varchar(20) | Not null; proposed `UNREVIEWED`, `PENDING`, `VERIFIED`, `REJECTED` |
| `is_featured` | boolean | Not null default false; only use when explicitly configured |
| `created_by_user_id` | UUID | Nullable FK → `users.id`; nullable for admin-seeded/imported MVP records |
| `published_at` | timestamptz | Nullable |
| `created_at` | timestamptz | Not null |
| `updated_at` | timestamptz | Not null |

Check constraints:

- `application_method = 'EXTERNAL'` requires a valid `application_url`.
- `application_method = 'INTERNAL'` must not pretend to have an external submission URL.
- Use approved `opportunity_type`, publication, verification, and work-mode values.
- Public listing queries must apply the agreed publication/verification visibility policy.

The exact verification state required for MVP visibility is an open product/operations decision. A record must never be labelled `VERIFIED` merely because it was seeded.

### 6.2 `opportunity_skills`

| Column | Type | Rules / meaning |
|---|---|---|
| `id` | UUID | PK |
| `opportunity_id` | UUID | FK → `opportunities.id`, not null |
| `skill_name` | varchar(120) | Not null |

Add an index on `opportunity_id` and, if useful, a normalized text index for search. A canonical skill taxonomy belongs to the later structured-skills milestone.

### 6.3 `saved_opportunities`

| Column | Type | Rules / meaning |
|---|---|---|
| `user_id` | UUID | FK → `users.id`, part of PK |
| `opportunity_id` | UUID | FK → `opportunities.id`, part of PK |
| `created_at` | timestamptz | Not null |

Use composite primary key `(user_id, opportunity_id)`. This enforces one saved relationship per student/opportunity.

### 6.4 `applications`

Create a row only for a platform-managed/internal application. Do not create one when the student simply opens an external application URL.

| Column | Type | Rules / meaning |
|---|---|---|
| `id` | UUID | PK |
| `student_user_id` | UUID | FK → `users.id`, not null |
| `opportunity_id` | UUID | FK → `opportunities.id`, not null |
| `status` | varchar(30) | Not null; provisional status set below |
| `submitted_at` | timestamptz | Not null |
| `updated_at` | timestamptz | Not null |

Proposed statuses: `SUBMITTED`, `UNDER_REVIEW`, `SHORTLISTED`, `REJECTED`, `SELECTED`, `WITHDRAWN`. Confirm this list and transitions before the first migration. Use a unique constraint on `(student_user_id, opportunity_id)` if reapplication is not allowed. If reapplication is needed, define an application-cycle model first rather than dropping duplicate protection casually.

### 6.5 `application_status_history`

| Column | Type | Rules / meaning |
|---|---|---|
| `id` | UUID | PK |
| `application_id` | UUID | FK → `applications.id`, not null |
| `from_status` | varchar(30) | Nullable for first event |
| `to_status` | varchar(30) | Not null |
| `changed_by_user_id` | UUID | Nullable FK → `users.id`; null for system-originated event if justified |
| `change_source` | varchar(30) | Proposed `STUDENT`, `RECRUITER`, `ADMIN`, `SYSTEM` |
| `note` | text | Nullable; do not expose recruiter-private notes to students |
| `created_at` | timestamptz | Not null |

Create a status-history row for the initial submission and every valid transition in the same transaction as the application update. Store actor/source and timestamp as required by the functional requirements.

## 7. Q&A tables

### 7.1 `qa_topics`

| Column | Type | Rules / meaning |
|---|---|---|
| `id` | UUID | PK |
| `name` | varchar(100) | Not null |
| `slug` | varchar(120) | Not null; unique |
| `description` | text | Nullable |
| `is_active` | boolean | Not null default true |
| `sort_order` | integer | Not null default 0 |
| `created_at` | timestamptz | Not null |
| `updated_at` | timestamptz | Not null |

Topic taxonomy must be configurable. Initial topic examples include DSA, Web Development, AI/ML, Placements, Academics, Project Help, Resume, System Design, and DevOps.

### 7.2 `qa_questions`

| Column | Type | Rules / meaning |
|---|---|---|
| `id` | UUID | PK |
| `author_user_id` | UUID | FK → `users.id`, not null |
| `title` | varchar(220) | Not null |
| `body` | text | Not null |
| `status` | varchar(20) | Proposed `PUBLISHED`, `HIDDEN`, `REMOVED` |
| `needs_help` | boolean | Not null default false; optional product signal, confirm before showing |
| `created_at` | timestamptz | Not null |
| `updated_at` | timestamptz | Not null |
| `moderated_at` | timestamptz | Nullable |

Represent acceptance with `qa_answers.is_accepted` and a PostgreSQL partial unique index allowing at most one accepted answer per question. This avoids a circular foreign-key dependency and prevents two accepted answers for the same question.

### 7.3 `qa_question_topics`

| Column | Type | Rules / meaning |
|---|---|---|
| `question_id` | UUID | FK → `qa_questions.id`, part of PK |
| `topic_id` | UUID | FK → `qa_topics.id`, part of PK |

Composite primary key `(question_id, topic_id)` prevents duplicate tags on one question.

### 7.4 `qa_answers`

| Column | Type | Rules / meaning |
|---|---|---|
| `id` | UUID | PK |
| `question_id` | UUID | FK → `qa_questions.id`, not null |
| `author_user_id` | UUID | FK → `users.id`, not null |
| `body` | text | Not null |
| `is_accepted` | boolean | Not null default false |
| `status` | varchar(20) | Proposed `PUBLISHED`, `HIDDEN`, `REMOVED` |
| `created_at` | timestamptz | Not null |
| `updated_at` | timestamptz | Not null |

Add an index on `(question_id, created_at)`. Use a partial unique index on `question_id WHERE is_accepted = true` so no question can have more than one accepted answer. The service must ensure only the question author or an authorized moderator can set `is_accepted`.

### 7.5 `qa_same_doubt`

| Column | Type | Rules / meaning |
|---|---|---|
| `question_id` | UUID | FK → `qa_questions.id`, part of PK |
| `user_id` | UUID | FK → `users.id`, part of PK |
| `created_at` | timestamptz | Not null |

Composite primary key `(question_id, user_id)` enforces one same-doubt signal per user/question.

### 7.6 `qa_answer_helpful`

| Column | Type | Rules / meaning |
|---|---|---|
| `answer_id` | UUID | FK → `qa_answers.id`, part of PK |
| `user_id` | UUID | FK → `users.id`, part of PK |
| `created_at` | timestamptz | Not null |

Composite primary key `(answer_id, user_id)` enforces one helpful signal per user/answer.

### 7.7 `content_reports`

Use a single report table for questions and answers while retaining foreign-key integrity.

| Column | Type | Rules / meaning |
|---|---|---|
| `id` | UUID | PK |
| `reporter_user_id` | UUID | FK → `users.id`, not null |
| `question_id` | UUID | Nullable FK → `qa_questions.id` |
| `answer_id` | UUID | Nullable FK → `qa_answers.id` |
| `reason` | varchar(40) | Not null; controlled reason list |
| `description` | text | Nullable |
| `status` | varchar(20) | Proposed `OPEN`, `UNDER_REVIEW`, `RESOLVED`, `DISMISSED` |
| `reviewed_by_user_id` | UUID | Nullable FK → `users.id` |
| `resolution_note` | text | Nullable |
| `created_at` | timestamptz | Not null |
| `updated_at` | timestamptz | Not null |
| `resolved_at` | timestamptz | Nullable |

Add a check constraint requiring exactly one target: `question_id` or `answer_id`, not both and not neither. Index open reports by status and creation time. Prevent obvious duplicate report spam according to an agreed policy; do not silently suppress distinct users' reports.

### 7.8 `moderation_actions`

| Column | Type | Rules / meaning |
|---|---|---|
| `id` | UUID | PK |
| `moderator_user_id` | UUID | FK → `users.id`, not null |
| `report_id` | UUID | Nullable FK → `content_reports.id` |
| `target_type` | varchar(20) | `QUESTION`, `ANSWER`, or later approved type |
| `target_id` | UUID | Target identifier; polymorphic reference, validated by service |
| `action` | varchar(30) | For example `HIDE`, `RESTORE`, `REMOVE`, `DISMISS_REPORT` |
| `reason` | text | Nullable but recommended for sensitive actions |
| `created_at` | timestamptz | Not null |

Because `target_id` is polymorphic, it does not have a normal foreign key to both target tables. The service must verify the target exists and matches `target_type`. If strict referential integrity is preferred, split actions into target-specific tables.

## 8. Mentor tables

### 8.1 `mentors`

A mentor is a directory profile. It is not automatically a user account and does not imply a `MENTOR` role.

| Column | Type | Rules / meaning |
|---|---|---|
| `id` | UUID | PK |
| `user_id` | UUID | Nullable FK → `users.id`; optional link if a mentor later receives an approved account capability |
| `display_name` | varchar(160) | Not null |
| `role_title` | varchar(160) | Nullable |
| `organization` | varchar(180) | Nullable |
| `bio` | text | Nullable |
| `availability_text` | text | Nullable; descriptive availability only unless structured slots are approved |
| `status` | varchar(20) | Proposed `ACTIVE`, `INACTIVE` |
| `created_at` | timestamptz | Not null |
| `updated_at` | timestamptz | Not null |

If `user_id` is populated, add a unique constraint so one user cannot be linked to multiple directory profiles unless explicitly needed.

### 8.2 `mentor_expertise`

| Column | Type | Rules / meaning |
|---|---|---|
| `id` | UUID | PK |
| `mentor_id` | UUID | FK → `mentors.id`, not null |
| `expertise_name` | varchar(120) | Not null |

Index by `mentor_id` and normalized expertise text for filtering.

### 8.3 `mentor_requests`

| Column | Type | Rules / meaning |
|---|---|---|
| `id` | UUID | PK |
| `student_user_id` | UUID | FK → `users.id`, not null |
| `mentor_id` | UUID | FK → `mentors.id`, not null |
| `preferred_start_at` | timestamptz | Nullable if using a free-form request instead of a slot |
| `preferred_end_at` | timestamptz | Nullable; must be after start when present |
| `message` | text | Nullable |
| `status` | varchar(20) | `REQUESTED`, `CONFIRMED`, `DECLINED`, `CANCELLED`, `COMPLETED` |
| `status_changed_by_user_id` | UUID | Nullable FK → `users.id`; may be admin-managed in MVP |
| `created_at` | timestamptz | Not null |
| `updated_at` | timestamptz | Not null |

The product specifies these states but does not define who confirms/declines a request without a separate mentor login role. Confirm the operating workflow before enabling status updates. Prevent duplicate conflicting requests when the mentor provides structured availability; the exact conflict rule needs confirmation.

## 9. Notifications

### `notifications`

| Column | Type | Rules / meaning |
|---|---|---|
| `id` | UUID | PK |
| `recipient_user_id` | UUID | FK → `users.id`, not null |
| `type` | varchar(40) | Controlled notification type |
| `title` | varchar(180) | Not null |
| `body` | text | Not null |
| `related_entity_type` | varchar(30) | Nullable, e.g. `APPLICATION`, `QUESTION`, `MENTOR_REQUEST` |
| `related_entity_id` | UUID | Nullable; polymorphic reference, validated by service |
| `read_at` | timestamptz | Nullable |
| `created_at` | timestamptz | Not null |

Index `(recipient_user_id, created_at DESC)` and, if useful, `(recipient_user_id, read_at)`. Notifications are in-app records for MVP; no provider-delivery columns are required until email/push is approved.

## 10. Milestone 2 recruiter/company tables

These tables are planned for the recruiter/admin milestone and should not block the student MVP unless the backend developer chooses to create the base tables early for a clear reason.

### 10.1 `companies`

Suggested fields: `id` UUID PK, `name`, `website`, `description`, `industry`, `location`, `verification_status`, `created_at`, `updated_at`.

### 10.2 `recruiter_profiles`

Suggested fields: `id` UUID PK, `user_id` unique FK → `users.id`, `company_id` FK → `companies.id`, `job_title`, `work_email`, `created_at`, `updated_at`.

### 10.3 `company_members`

Use only if multiple recruiter accounts may belong to one company. Suggested fields: `company_id`, `user_id`, `membership_role`, `created_at`, with composite unique/primary key.

### 10.4 `verification_requests`

Suggested fields: `id`, `subject_type` (`RECRUITER` or `COMPANY`), `subject_id`, `requested_by_user_id`, `status`, `reviewed_by_user_id`, `review_note`, `created_at`, `reviewed_at`. Because subject types may refer to different tables, use separate nullable foreign keys or dedicated request tables if strict referential integrity is required.

Opportunity ownership should eventually link to the recruiter/company. The MVP may use `created_by_user_id` and `organization_name` for seed/admin-managed records. Do not invent recruiter UI or workflows in the student MVP.

## 11. Indexing and query guidance

Start with indexes that support known product queries; measure before adding more.

| Table | Suggested index |
|---|---|
| `users` | Unique normalized email |
| `student_profiles` | Unique `user_id` |
| Profile child tables | `user_id` |
| `opportunities` | `(publication_status, deadline_date)`; `(opportunity_type, publication_status)`; `created_at DESC` |
| `opportunity_skills` | `opportunity_id`; normalized skill lookup if needed |
| `saved_opportunities` | Composite PK `(user_id, opportunity_id)`; optional `(user_id, created_at DESC)` |
| `applications` | Unique `(student_user_id, opportunity_id)` if reapplication is disallowed; `(student_user_id, submitted_at DESC)`; `(opportunity_id, status)` for recruiter milestone |
| `application_status_history` | `(application_id, created_at)` |
| `qa_questions` | `(status, created_at DESC)`; `author_user_id`; search index appropriate to PostgreSQL query strategy |
| `qa_question_topics` | PK plus `(topic_id, question_id)` for topic filters |
| `qa_answers` | `(question_id, created_at)` and partial unique accepted-answer index |
| `qa_same_doubt` | Composite PK; `(question_id)` for counts |
| `qa_answer_helpful` | Composite PK; `(answer_id)` for counts |
| `content_reports` | `(status, created_at)`; reporter index |
| `mentors` | `status`; expertise lookup index |
| `mentor_requests` | `(student_user_id, created_at DESC)`; `(mentor_id, status)` |
| `notifications` | `(recipient_user_id, created_at DESC)`; `(recipient_user_id, read_at)` |

Use PostgreSQL full-text search or another approved search approach only after defining the desired matching behavior. Do not add Elasticsearch or another search service for the MVP without a demonstrated need.

## 12. Integrity rules that must be enforced

1. A student profile belongs to exactly one user account.
2. Profile child rows can only be changed by the owning student or an authorized admin.
3. One student can save an opportunity only once.
4. A platform-managed application belongs to one student and one opportunity; duplicates are blocked according to the approved reapplication rule.
5. External-link opens do not create application records.
6. Every application status transition creates a history row with timestamp and actor/source.
7. A question has one or more valid topics if topic selection is required by the UI.
8. One user can signal same doubt once per question.
9. One user can mark an answer helpful once per answer.
10. At most one answer can be accepted per question.
11. The question author or an authorized moderator may accept an answer.
12. A report must target exactly one supported content item.
13. A student can view/update only their own notifications and mentor requests.
14. Mentor request state changes are restricted to the actor approved by the product workflow.
15. Published opportunity visibility follows publication and verification policy; seeded records are not automatically verified.

## 13. Migration strategy

Use Flyway versioned migrations. A suggested sequence:

- `V1__create_identity_tables.sql` — `users` and optional refresh sessions.
- `V2__create_student_profile_tables.sql` — profile and child records.
- `V3__create_opportunity_tables.sql` — opportunities, required skills, saved opportunities.
- `V4__create_application_tables.sql` — applications and status history.
- `V5__create_qa_tables.sql` — topics, questions, answers, topics join, reactions, reports, moderation actions.
- `V6__create_mentor_tables.sql` — mentors, expertise, requests.
- `V7__create_notification_tables.sql` — notifications.
- Later migrations — recruiter/company/verification/audit extensions after Milestone 2 is approved.

The exact split can change, but migrations must be ordered, immutable after being applied to shared environments, and tested against PostgreSQL. Use a new migration to modify an already-applied schema. Do not use Hibernate `ddl-auto=update` in production.

Seed only controlled reference data (for example, approved opportunity types and Q&A topics) through repeatable or versioned seed scripts. Sample opportunities must be clearly labeled as demo/seed data and must not display false verification claims.

## 14. Deliberately excluded tables for this phase

Do not create tables for these deferred capabilities merely because they appear in long-term project diagrams:

- AI prompts, model runs, embeddings, chatbot conversations, or ML recommendation scores.
- Resume files, OCR jobs, extracted document fields, ATS results, or document-processing queues.
- Payments, subscriptions, invoices, or payouts.
- Real-time messaging threads/messages.
- Email/push delivery attempts unless an approved provider integration is added.
- Advanced skill assessments or skill-gap results.

## 15. Decisions required before the first full migration

1. Exact account verification and account-status workflow.
2. Refresh-session/token policy and whether `refresh_sessions` is required.
3. Exact allowed application statuses, valid transitions, and reapplication policy.
4. Confirm whether a date-only deadline is sufficient or whether a specific deadline time/time zone is required. The proposed MVP column is `deadline_date DATE`.
5. Opportunity publication/verification policy for MVP and who owns seeded records.
6. Whether basic profile collections use individual relational tables as proposed or another agreed model.
7. Whether topic tags are mandatory for every question and the maximum number allowed.
8. Whether question/answer editing and deletion are allowed after activity exists.
9. Whether mentor profiles link to real users and who can update request statuses.
10. Whether admin moderation actions need a general audit table in MVP or only domain-specific moderation records initially.
11. Profile visibility policy for private contact information and recruiter access.

## 16. Schema acceptance checklist

- [ ] Every entity maps to an approved MVP or clearly planned Next requirement.
- [ ] Every owner-scoped table has a clear ownership rule.
- [ ] Foreign keys and unique constraints protect relationships and duplicate interactions.
- [ ] External applications are not stored as submitted platform applications.
- [ ] Application status history records actor/source and timestamp.
- [ ] Q&A same-doubt/helpful/accepted rules are enforceable in PostgreSQL.
- [ ] Mentor request response authority is agreed.
- [ ] Indexes support bounded list/feed queries.
- [ ] Flyway migrations run on PostgreSQL in a clean environment.
- [ ] No AI/OCR/resume/ATS/payment/messaging schema has been added prematurely.
