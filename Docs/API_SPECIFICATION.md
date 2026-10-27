# UPLINK — Backend API Specification

> **Document:** Core REST API contract  
> **Version:** 1.0  
> **Status:** Proposed contract for mobile/backend alignment  
> **Prepared:** 27 October 2026  
> **Base path:** `/api/v1`

## 1. Purpose and contract status

This document defines proposed REST endpoints for the UPLINK core Spring Boot backend. It is derived from the approved product requirements and feature catalogue. Exact endpoint names and JSON shapes were not defined in those product documents; therefore, the routes and examples below are a **proposed implementation contract** that the mobile and backend developers should agree on before implementation.

The scope is the Student MVP plus a separate section for Milestone 2 recruiter/admin endpoints. AI, OCR, resume generation, ATS analysis, payments, messaging, and advanced recommendations are excluded.

## 2. API-wide conventions

### 2.1 Base URL and versioning

All routes use `/api/v1`, for example:

`GET /api/v1/opportunities`

Do not expose internal Java package names or database table names in URLs. Add a new API version only for breaking contract changes; additive optional fields should remain backward-compatible.

### 2.2 Data conventions

- Identifiers: UUID strings.
- JSON property naming: `camelCase`.
- Database naming: `snake_case` (see `DATABASE_SCHEMA.md`).
- Timestamps: ISO-8601 UTC, for example `2026-10-27T08:30:00Z`.
- Dates without a time: ISO date, `YYYY-MM-DD`.
- Enum values: uppercase strings.
- Request bodies: JSON unless an endpoint explicitly documents another content type.
- Do not return passwords, password hashes, token hashes, signing data, or private database fields.

### 2.3 Authentication

Protected endpoints use:

`Authorization: Bearer <access-token>`

The proposed session design uses a short-lived access token and a revocable refresh mechanism. Exact token lifetimes and the account-verification channel remain decisions to confirm. Do not treat a user-supplied `role` value as authorization.

### 2.4 Roles

- `STUDENT`
- `RECRUITER` — Milestone 2 workflows.
- `ADMIN`

A mentor is a directory profile/capability in the MVP, not a separate primary login role.

### 2.5 Pagination

List endpoints should use bounded pagination. Proposed query parameters:

| Parameter | Meaning | Default / limit |
|---|---|---|
| `page` | Zero-based page index | `0` |
| `size` | Number of records | `20`, maximum `100` |
| `sort` | Allowlisted sort key | Endpoint-specific |
| `direction` | `ASC` or `DESC` | Endpoint-specific |

Proposed response shape:

```json
{
  "items": [],
  "page": 0,
  "size": 20,
  "totalElements": 0,
  "totalPages": 0,
  "hasNext": false
}
```

Do not accept arbitrary database column names in `sort`. Validate and map supported sort values.

### 2.6 Standard error response

Use one predictable error shape for all API errors. This is a proposed JSON representation; Spring may implement it using `ProblemDetail` plus extensions.

```json
{
  "type": "about:blank",
  "title": "Validation failed",
  "status": 400,
  "code": "VALIDATION_ERROR",
  "detail": "One or more fields are invalid.",
  "instance": "/api/v1/students/me/profile",
  "traceId": "request-correlation-id",
  "fieldErrors": [
    {
      "field": "institution",
      "code": "NotBlank",
      "message": "Institution is required."
    }
  ]
}
```

Expected status codes:

| Status | Meaning |
|---|---|
| `200` | Successful read/update/action with response body |
| `201` | Resource created |
| `204` | Successful action with no response body |
| `400` | Invalid request or validation failure |
| `401` | Missing, invalid, or expired authentication |
| `403` | Authenticated user lacks permission |
| `404` | Resource not found or not visible to caller |
| `409` | State conflict or duplicate action |
| `422` | Well-formed request cannot be processed under current business rules, if the team chooses to use this distinction |
| `429` | Rate limit exceeded |
| `500` | Unexpected server error; never expose stack traces |

## 3. Endpoint map — Student MVP

`Auth` is public except where noted. All student profile, saved item, application, Q&A write/reaction, mentor request, and notification routes require an authenticated `STUDENT` unless specified otherwise.

| Method | Endpoint | Access | Purpose |
|---|---|---|---|
| `GET` | `/health` | Public | Health/liveness endpoint |
| `POST` | `/auth/register` | Public | Register a student account |
| `POST` | `/auth/login` | Public | Sign in |
| `POST` | `/auth/refresh` | Refresh credential | Obtain a new access token |
| `POST` | `/auth/logout` | Authenticated | Revoke current refresh session |
| `GET` | `/users/me` | Authenticated | Current account and role summary |
| `GET` | `/students/me/profile` | STUDENT | Read own profile |
| `PUT` | `/students/me/profile` | STUDENT | Create/update own profile basics |
| `POST` | `/students/me/profile/skills` | STUDENT | Add a basic skill entry |
| `PUT` | `/students/me/profile/skills/{skillId}` | STUDENT | Update own skill entry |
| `DELETE` | `/students/me/profile/skills/{skillId}` | STUDENT | Delete own skill entry |
| `POST` | `/students/me/profile/projects` | STUDENT | Add project |
| `PUT` | `/students/me/profile/projects/{projectId}` | STUDENT | Update project |
| `DELETE` | `/students/me/profile/projects/{projectId}` | STUDENT | Delete project |
| `POST` | `/students/me/profile/experience` | STUDENT | Add experience |
| `PUT` | `/students/me/profile/experience/{experienceId}` | STUDENT | Update experience |
| `DELETE` | `/students/me/profile/experience/{experienceId}` | STUDENT | Delete experience |
| `POST` | `/students/me/profile/certifications` | STUDENT | Add certification |
| `PUT` | `/students/me/profile/certifications/{certificationId}` | STUDENT | Update certification |
| `DELETE` | `/students/me/profile/certifications/{certificationId}` | STUDENT | Delete certification |
| `POST` | `/students/me/profile/achievements` | STUDENT | Add achievement |
| `PUT` | `/students/me/profile/achievements/{achievementId}` | STUDENT | Update achievement |
| `DELETE` | `/students/me/profile/achievements/{achievementId}` | STUDENT | Delete achievement |
| `GET` | `/feed` | Authenticated STUDENT | Feed sections using available opportunity data |
| `GET` | `/opportunities` | Public or authenticated; confirm policy | Search/filter published opportunities |
| `GET` | `/opportunities/{opportunityId}` | Public or authenticated; confirm policy | Opportunity details |
| `GET` | `/students/me/saved-opportunities` | STUDENT | List saved opportunities |
| `PUT` | `/students/me/saved-opportunities/{opportunityId}` | STUDENT | Save; idempotent |
| `DELETE` | `/students/me/saved-opportunities/{opportunityId}` | STUDENT | Unsave; idempotent |
| `POST` | `/opportunities/{opportunityId}/applications` | STUDENT | Submit an internal/platform-managed application |
| `GET` | `/students/me/applications` | STUDENT | List own applications |
| `GET` | `/applications/{applicationId}` | Owner; recruiter in later milestone | Read application and current status |
| `GET` | `/applications/{applicationId}/status-history` | Owner; recruiter in later milestone | Read status history |
| `GET` | `/qa/topics` | Public or authenticated | List active Q&A topics |
| `GET` | `/qa/questions` | Public or authenticated; policy to confirm | Browse/search/filter questions |
| `POST` | `/qa/questions` | STUDENT | Create question |
| `GET` | `/qa/questions/{questionId}` | Public or authenticated; policy to confirm | Read question detail |
| `PUT` | `/qa/questions/{questionId}` | Question author | Edit own question where allowed |
| `GET` | `/qa/questions/{questionId}/answers` | Public or authenticated | List answers |
| `POST` | `/qa/questions/{questionId}/answers` | STUDENT | Add answer |
| `PUT` | `/qa/questions/{questionId}/same-doubt` | STUDENT | Add own same-doubt signal; idempotent |
| `DELETE` | `/qa/questions/{questionId}/same-doubt` | STUDENT | Remove own same-doubt signal |
| `PUT` | `/qa/answers/{answerId}/helpful` | STUDENT | Mark answer helpful; idempotent |
| `DELETE` | `/qa/answers/{answerId}/helpful` | STUDENT | Remove helpful signal |
| `PUT` | `/qa/questions/{questionId}/accepted-answer/{answerId}` | Question author or ADMIN | Accept an answer |
| `DELETE` | `/qa/questions/{questionId}/accepted-answer` | Question author or ADMIN | Remove accepted state if permitted |
| `POST` | `/reports` | STUDENT | Report a question or answer |
| `GET` | `/mentors` | Public or authenticated; policy to confirm | Search/filter mentor directory |
| `GET` | `/mentors/{mentorId}` | Public or authenticated; policy to confirm | Read mentor profile |
| `POST` | `/mentors/{mentorId}/requests` | STUDENT | Submit a mentor request |
| `GET` | `/students/me/mentor-requests` | STUDENT | List own mentor requests |
| `GET` | `/students/me/notifications` | STUDENT | List own in-app notifications |
| `PATCH` | `/students/me/notifications/{notificationId}/read` | STUDENT | Mark one notification read |
| `PATCH` | `/students/me/notifications/read-all` | STUDENT | Mark all own notifications read |

The API developer may combine or simplify profile-child endpoints if that better matches the mobile implementation, but the ownership and data operations must remain available. Agree the final paths with the mobile developer before freezing the contract.

## 4. Authentication endpoints

### 4.1 Register student

`POST /api/v1/auth/register`

Request example:

```json
{
  "firstName": "Asha",
  "lastName": "Patil",
  "email": "asha@example.edu",
  "password": "user-supplied-password"
}
```

Rules:

- Public registration creates `STUDENT` only.
- Ignore or reject a client-provided `role`; never allow public users to assign themselves `ADMIN` or `RECRUITER`.
- Normalize email consistently and enforce uniqueness.
- Validate password policy on the server.
- Apply the agreed verification workflow. The exact email/institutional verification method is an open decision.

Response proposal: `201 Created` with account ID, role, account status, and verification-required information. Do not return password hashes or secrets. Whether tokens are issued before verification must be decided explicitly.

### 4.2 Login

`POST /api/v1/auth/login`

```json
{
  "email": "asha@example.edu",
  "password": "user-supplied-password"
}
```

Response proposal:

```json
{
  "accessToken": "<access-token>",
  "tokenType": "Bearer",
  "expiresIn": 900,
  "refreshToken": "<refresh-token-if-returned-by-approved-design>",
  "user": {
    "id": "2c879ca4-44a8-4f7a-bfbb-71a5cc94c40f",
    "email": "asha@example.edu",
    "role": "STUDENT",
    "accountStatus": "ACTIVE"
  }
}
```

The values are illustrative. Do not hard-code the example expiry. If refresh credentials are placed in an HTTP-only cookie for a web client, define the mobile-client strategy separately. The React Native client must store authentication secrets in platform-backed secure storage, not plain AsyncStorage.

### 4.3 Refresh and logout

`POST /api/v1/auth/refresh` validates the refresh credential, rotates/reissues tokens according to the chosen policy, and rejects revoked/expired credentials.

`POST /api/v1/auth/logout` revokes the current refresh session. If the system supports multiple devices/sessions, define whether logout revokes only the current session or all sessions.

## 5. Student profile endpoints

### 5.1 Read profile

`GET /api/v1/students/me/profile`

Response proposal:

```json
{
  "id": "profile-uuid",
  "userId": "user-uuid",
  "firstName": "Asha",
  "lastName": "Patil",
  "headline": "Computer Science Student",
  "institution": "Example Institute",
  "department": "Computer Science",
  "program": "B.Tech",
  "academicYear": 3,
  "semester": 5,
  "location": "Pune, India",
  "bio": "Interested in software engineering.",
  "skills": [],
  "projects": [],
  "experience": [],
  "certifications": [],
  "achievements": [],
  "createdAt": "2026-10-27T08:30:00Z",
  "updatedAt": "2026-10-27T08:30:00Z"
}
```

### 5.2 Update profile basics

`PUT /api/v1/students/me/profile`

The request should contain only editable profile fields. The backend derives the owner from the authenticated principal; never accept `userId` from the request as the ownership authority.

Profile privacy controls are a Next priority. The first version must nevertheless avoid exposing private account fields and should return only fields the caller is allowed to see.

## 6. Feed and opportunity endpoints

### 6.1 Feed

`GET /api/v1/feed`

Proposed response:

```json
{
  "featuredOpportunities": [],
  "upcomingOpportunities": [],
  "generatedAt": "2026-10-27T08:30:00Z"
}
```

The response should be based on actual opportunity records and a documented fallback order. Do not return fabricated counts, fake personalized scores, or placeholder recommendations. Feed event-like content can be represented by relevant opportunity types such as workshops, competitions, and hackathons unless a separate event model is approved.

### 6.2 Opportunity listing

`GET /api/v1/opportunities`

Supported query parameters (apply only when the field exists on the data):

| Parameter | Meaning |
|---|---|
| `q` | Search title, organization, description, or supported skill keywords |
| `type` | Opportunity type; repeat or comma-separate only if implementation agrees |
| `location` | Location text/filter |
| `workMode` | `REMOTE`, `ONSITE`, `HYBRID`, or agreed equivalent |
| `skill` | Required skill keyword |
| `sort` | `RELEVANCE`, `RECENT`, `DEADLINE` |
| `page`, `size` | Pagination |

Only return records that satisfy the agreed publication/visibility rule. Do not claim that all listed records are verified. Include separate `publicationStatus` and `verificationStatus` fields where relevant.

### 6.3 Opportunity detail

`GET /api/v1/opportunities/{opportunityId}`

Response must include organization, description, type, eligibility, required skills, location/work mode, compensation/benefit if supplied, deadline, source, publication/verification state, and application method.

Example application method:

```json
{
  "type": "EXTERNAL",
  "url": "https://example.org/apply",
  "label": "Apply on organization website"
}
```

For internal applications, use `type: "INTERNAL"` and expose the relevant application workflow. Do not create an application record merely because an external URL was opened.

### 6.4 Save and unsave

- `PUT /api/v1/students/me/saved-opportunities/{opportunityId}` creates the saved relationship. Repeated calls should be safe/idempotent.
- `DELETE /api/v1/students/me/saved-opportunities/{opportunityId}` removes it. Repeated calls should be safe/idempotent.
- `GET /api/v1/students/me/saved-opportunities` returns a paginated list.

## 7. Applications

### 7.1 Create platform-managed application

`POST /api/v1/opportunities/{opportunityId}/applications`

Only accept this request when the opportunity's application method is internal/platform-managed and the opportunity is open for applications.

Request proposal:

```json
{
  "answers": [
    {
      "questionKey": "motivation",
      "answer": "I am interested in this opportunity because..."
    }
  ]
}
```

The actual application form fields are not specified in the product baseline. Use a small agreed model; do not implement arbitrary attachment/document upload as part of this phase.

Rules:

- Derive the student ID from the authenticated user.
- Enforce one active/submitted application per student/opportunity unless a documented rule permits reapplication.
- Validate the deadline and publication state.
- Create an initial status and status-history event in the same transaction.
- Return `409 Conflict` for a duplicate application.

### 7.2 External applications

If the opportunity is external, the detail response provides the external URL. The client opens that URL. The backend must not create an internal application record or display a submitted status simply because the URL was opened.

### 7.3 List and history

- `GET /api/v1/students/me/applications` returns only the current student's records.
- `GET /api/v1/applications/{applicationId}` returns the resource only to its owner or an authorized recruiter/admin in the later milestone.
- `GET /api/v1/applications/{applicationId}/status-history` returns ordered status changes with timestamps and actor/source metadata.

**Provisional application statuses:** `SUBMITTED`, `UNDER_REVIEW`, `SHORTLISTED`, `REJECTED`, `SELECTED`, `WITHDRAWN`. This set is proposed because the source requirements require status tracking but do not enumerate the exact values. Confirm the lifecycle and who may make each transition before implementing it.

## 8. Q&A endpoints

### 8.1 Topics and question feed

`GET /api/v1/qa/topics` returns active topic records.

`GET /api/v1/qa/questions` supports:

| Parameter | Values / meaning |
|---|---|
| `view` | `FOR_YOU`, `UNANSWERED`, `TRENDING`, `MY_QUESTIONS` |
| `q` | Search question title/body |
| `topicId` | Filter by topic |
| `sort` | `RELEVANCE`, `NEWEST`, `MOST_HELPFUL` where applicable |
| `page`, `size` | Pagination |

`FOR_YOU` is based on permitted interests/topics/skills if available. If personalization inputs are missing, use a clearly defined fallback ordering. `UNANSWERED` filters questions without answers. `TRENDING` must use a transparent rule with recency, not popularity alone.

### 8.2 Create and read questions

`POST /api/v1/qa/questions`

```json
{
  "title": "How does a hash map handle collisions?",
  "body": "I understand hashing but need clarification on collision handling...",
  "topicIds": ["topic-uuid"],
  "needsHelp": false
}
```

`needsHelp` is optional and should only be included if the team decides to expose an explicit blocking/urgency signal. The source requirements allow this as an optional signal; it is not mandatory.

`GET /api/v1/qa/questions/{questionId}` returns question details, author display fields permitted for Q&A, topics, counts, and accepted-answer summary where applicable.

`PUT /api/v1/qa/questions/{questionId}` is limited to the question author and the agreed editable fields. The team should decide whether editing is allowed after answers exist; retain timestamps and moderation history.

### 8.3 Answers

- `GET /api/v1/qa/questions/{questionId}/answers` supports pagination and `sort=NEWEST|MOST_HELPFUL`.
- `POST /api/v1/qa/questions/{questionId}/answers` creates an answer for an authenticated student.
- `PUT /api/v1/qa/answers/{answerId}/helpful` and `DELETE .../helpful` add/remove the current user's helpful signal.
- `PUT /api/v1/qa/questions/{questionId}/accepted-answer/{answerId}` accepts an answer only when the caller is the question author or an authorized moderator.
- `DELETE /api/v1/qa/questions/{questionId}/accepted-answer` removes the accepted state only when permitted.

The backend must ensure the answer belongs to the question in the URL. One user may create at most one helpful signal per answer.

### 8.4 Same-doubt signal

- `PUT /api/v1/qa/questions/{questionId}/same-doubt` adds the current user's signal.
- `DELETE /api/v1/qa/questions/{questionId}/same-doubt` removes it.

Enforce a unique `(question_id, user_id)` relationship in the database. Counts represent distinct users, not raw repeated actions.

### 8.5 Reporting and moderation

`POST /api/v1/reports`

```json
{
  "targetType": "QUESTION",
  "targetId": "question-uuid",
  "reason": "SPAM",
  "description": "Optional additional context"
}
```

For MVP, support `QUESTION` and `ANSWER` targets. Admin report-list and moderation routes are in the Milestone 2 section, but the project baseline requires a basic moderation path for Q&A reports during MVP. Provide a restricted internal/admin operation or minimal admin route for that purpose before the full dashboard exists.

## 9. Mentor endpoints

### 9.1 Directory

`GET /api/v1/mentors` supports `q`, `expertise`, `page`, and `size`.

`GET /api/v1/mentors/{mentorId}` returns approved mentor profile data, expertise, and availability where supplied.

### 9.2 Booking request

`POST /api/v1/mentors/{mentorId}/requests`

```json
{
  "preferredStartAt": "2026-11-02T10:00:00Z",
  "preferredEndAt": "2026-11-02T10:30:00Z",
  "message": "I would like guidance on backend development."
}
```

`GET /api/v1/students/me/mentor-requests` returns the student's requests and current status.

The source documents specify states `REQUESTED`, `CONFIRMED`, `DECLINED`, `CANCELLED`, and `COMPLETED`, but do not define who can confirm/decline when the mentor is a directory profile without a dedicated login role. Do not expose mentor response endpoints until the team chooses an authorized actor (for example, an admin-managed MVP process). A student may cancel their own request only under the agreed cancellation rule.

## 10. Notifications

- `GET /api/v1/students/me/notifications` returns a paginated list ordered newest first.
- `PATCH /api/v1/students/me/notifications/{notificationId}/read` marks one notification read, only if it belongs to the caller.
- `PATCH /api/v1/students/me/notifications/read-all` marks all current user's notifications read.

The MVP is in-app notification storage only. Do not require email or push-provider integration in order for core flows to work.

## 11. Milestone 2 — Recruiter and admin endpoints

These routes are planned, not required to finish the Student MVP. Final request/response DTOs should be added when the recruiter/admin web workflows are designed.

| Method | Endpoint | Access | Purpose |
|---|---|---|---|
| `POST` | `/recruiters/me/profile` | RECRUITER | Create recruiter profile |
| `GET` | `/recruiters/me/profile` | RECRUITER | Read own recruiter profile |
| `PUT` | `/recruiters/me/profile` | RECRUITER | Update own recruiter profile |
| `POST` | `/companies` | RECRUITER | Create company profile |
| `GET` | `/companies/{companyId}` | Authorized member/ADMIN | Read company profile |
| `POST` | `/companies/{companyId}/verification-requests` | Authorized company member | Submit verification request |
| `POST` | `/recruiter/opportunities` | Verified RECRUITER | Create opportunity draft |
| `PUT` | `/recruiter/opportunities/{opportunityId}` | Owning RECRUITER | Edit own opportunity |
| `POST` | `/recruiter/opportunities/{opportunityId}/publish` | Verified RECRUITER | Request/publish opportunity under policy |
| `POST` | `/recruiter/opportunities/{opportunityId}/close` | Owning RECRUITER | Close opportunity |
| `GET` | `/recruiter/opportunities/{opportunityId}/applications` | Owning RECRUITER | List applicants |
| `PATCH` | `/recruiter/applications/{applicationId}/status` | Owning RECRUITER | Update allowed stage |
| `GET` | `/admin/verification-requests` | ADMIN | Review queue |
| `PATCH` | `/admin/verification-requests/{requestId}` | ADMIN | Approve/reject with reason |
| `GET` | `/admin/reports` | ADMIN | List content reports |
| `PATCH` | `/admin/reports/{reportId}` | ADMIN | Resolve/dismiss report and moderate target |
| `PATCH` | `/admin/opportunities/{opportunityId}/moderation` | ADMIN | Moderate/unpublish opportunity |
| `GET` | `/admin/audit-logs` | ADMIN | Read permitted audit records |

Every recruiter action must be scoped to the recruiter's own company/opportunities. Every sensitive admin action must create an audit record.

## 12. Core request/response examples

### 12.1 Opportunity summary

```json
{
  "id": "opportunity-uuid",
  "title": "Software Engineering Internship",
  "organization": "Example Organization",
  "type": "INTERNSHIP",
  "location": "Pune, India",
  "workMode": "HYBRID",
  "deadline": "2026-11-20",
  "requiredSkills": ["Java", "SQL"],
  "applicationMethod": {
    "type": "INTERNAL",
    "url": null
  },
  "publicationStatus": "PUBLISHED",
  "verificationStatus": "VERIFIED"
}
```

Values above are examples only. Do not seed fake records that appear to be actual verified opportunities.

### 12.2 Application summary

```json
{
  "id": "application-uuid",
  "opportunityId": "opportunity-uuid",
  "opportunityTitle": "Software Engineering Internship",
  "status": "SUBMITTED",
  "submittedAt": "2026-10-27T08:30:00Z",
  "updatedAt": "2026-10-27T08:30:00Z"
}
```

### 12.3 Question summary

```json
{
  "id": "question-uuid",
  "title": "How does a hash map handle collisions?",
  "preview": "I understand hashing but need clarification...",
  "author": {
    "id": "user-uuid",
    "displayName": "Asha P."
  },
  "topics": [
    { "id": "topic-uuid", "name": "Data Structures" }
  ],
  "answerCount": 2,
  "sameDoubtCount": 5,
  "hasSameDoubt": false,
  "createdAt": "2026-10-27T08:30:00Z"
}
```

Only include profile context that is approved for Q&A display. The `hasSameDoubt` value is caller-specific and should be calculated for the authenticated user.

## 13. Decisions required before freezing the API contract

1. Student verification workflow and whether an unverified account can sign in.
2. JWT/refresh-token mechanism and session lifetime.
3. Public versus authenticated access for opportunity, mentor, and Q&A reads.
4. Exact internal-application form fields.
5. Final application status enum and transition permissions.
6. How opportunities are seeded/verified/published in the MVP.
7. Who handles mentor confirmation/decline without a separate mentor account role.
8. Whether question editing is allowed after answers exist and the final moderation state model.
9. Exact API fields needed by the current mobile implementation; compare with the existing screen code before freezing DTOs.

## 14. Contract acceptance checklist

- [ ] Mobile and backend developers agree endpoint paths and DTOs.
- [ ] Authentication and role behavior is documented.
- [ ] All list endpoints are paginated and bounded.
- [ ] All writes validate input and enforce ownership.
- [ ] External application links are not recorded as internal submissions.
- [ ] Duplicate applications and duplicate reactions are prevented.
- [ ] Q&A accepted-answer permissions are enforced.
- [ ] Mentor request status ownership is defined.
- [ ] Errors follow the common contract.
- [ ] OpenAPI output matches the implementation.
