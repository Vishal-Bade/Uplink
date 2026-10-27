# UPLINK — Feature Catalogue

> **Version:** 1.0  
> **Status:** Baseline for team review  
> **Last updated:** 26 October 2026

This catalogue describes product capabilities and when they should be delivered. It is intentionally staged; inclusion in this file does not mean a feature belongs in the first release.

## 1. Release labels

- **MVP:** First student-facing release and the backend required to support it.
- **Next:** Recruiter/admin workflows and extensions after the MVP foundation.
- **Later:** AI-heavy, document-heavy, external, or advanced platform capabilities.

## 2. Student mobile features

| Feature | Description | Release |
|---|---|---|
| Onboarding | Short introduction to UPLINK, skip/continue, completion persistence. | MVP |
| Authentication | Student sign-up/sign-in, verification, session restoration, and sign-out. | MVP |
| Student profile | Academic and professional information, skills, projects, experience, certifications, and achievements. | MVP basic; richer profile Next |
| Feed | Summary of featured opportunities and upcoming events, with links to details. | MVP |
| Jobs / Explore | Browse, search, filter, sort, and open career opportunities. The tab label is Jobs; screen heading may be Explore Opportunities. | MVP |
| Saved opportunities | Bookmark and revisit saved opportunities. | MVP |
| Applications | Apply through supported methods and track platform-managed applications and statuses. | MVP |
| Q&A | Ask questions, browse topics, answer, mark same doubt, and find useful answers. | MVP |
| Mentor directory | Discover mentors by area and review mentor profiles. | MVP |
| Mentor requests | Request a session and view request status. | MVP basic |
| Notifications | In-app notices for core application activity. | MVP basic |
| Profile settings | Edit profile, privacy preferences, account settings, and sign-out. | MVP basic |

## 3. Q&A feature details

Q&A replaces the idea of a generic community/conversation tab for the initial product. It is a structured knowledge-sharing feature.

### Question feed

- Main views: **For You**, **Unanswered**, **Trending**, **My Questions**.
- Topic filters: examples include DSA, Web Development, AI/ML, Placements, Academics, Project Help, Resume, System Design, and DevOps. Topic taxonomy must be configurable.
- Each question card shows author, relevant academic/profile context when public, title, short preview, tags, answer count, same-doubt count, and age.
- Do not add a prominent status badge to every card. The Unanswered filter is sufficient to browse unanswered items; status can be shown subtly where useful.
- Provide a clear Ask Question action.

### Question detail and answers

- Show the full question, author, tags, and activity counts.
- Allow a student to indicate “I have the same doubt.”
- Show answers with author/context, time, helpful signal, and accepted-answer state where applicable.
- Allow answer sorting such as Most Helpful and Newest.
- Provide an answer composer.

### Ranking

- Relevance: matching topics, skills, and student-selected interests/context.
- Need: unanswered state, age while unanswered, and optional urgency/blocking signal.
- Doubt: count of distinct users indicating the same doubt, with anti-abuse controls.
- Starting weights: 40% Relevance, 30% Need, 30% Doubt. Make weights configurable and evaluate them; this is an initial heuristic, not a validated algorithm.
- Add recency/anti-staleness handling so a popular old question does not dominate indefinitely.
- Never use sensitive attributes or expose private student data in ranking explanations.

### Moderation

- Report question/answer.
- Hide or remove content after moderation according to policy.
- Rate-limit repeated posts, answers, and reactions.
- Preserve audit information for moderator actions.

## 4. Opportunity features

Opportunity categories:

`Internship`, `Job`, `Hackathon`, `Competition`, `Scholarship`, `Workshop`, `Certification`, `Training`, `Research`, `Apprenticeship`.

Core fields include title, organization, description, type, required skills, eligibility, location/work mode, compensation/benefit, deadline, external/internal application method, source, and verification status.

Student actions:

- Search by title, organization, skill, or keyword.
- Filter by type, location/work mode, eligibility, and other available structured fields.
- Sort by relevance, recency, deadline, or other supported criteria.
- Save/unsave.
- View details.
- Apply or open an external application link.
- Track status for applications managed by UPLINK.

Do not claim that an external application has been submitted merely because a student opened an external link.

## 5. Mentor features

- Search and filter by expertise/domain.
- Mentor profile with approved bio, role/organization, expertise, and availability.
- Booking request with preferred slot/message.
- Booking state such as Requested, Confirmed, Declined, Cancelled, Completed.
- Calendar synchronization and payments are deferred.

## 6. Recruiter and admin features

| Feature | Description | Release |
|---|---|---|
| Recruiter account and company profile | Recruiter registration, organization information, verification state. | Next |
| Manual verification | ADMIN reviews recruiter/company and selected opportunity records. | Next |
| Opportunity management | Recruiter creates, edits, publishes, closes, and archives opportunities. | Next |
| Applicant management | Recruiter reviews applicants and updates permitted application stages. | Next |
| Candidate discovery | Search/filter authorized student profile fields and shortlist candidates. | Next |
| Admin moderation | Verification queues, Q&A reports, opportunity moderation, user management. | Next |
| Audit trail | Record significant admin and recruiter actions. | Next |
| Admin analytics | Platform activity and institutional reports. | Later |

## 7. Supporting and intelligent features

| Feature | Description | Release |
|---|---|---|
| Skill profile | Structured skills with proficiency/source metadata. | MVP basic profile fields; Next structured model |
| Skill assessment | Quizzes, self-assessments, and/or verified evidence. | Next |
| Skill-gap analysis | Compare a student's skills to target role/opportunity requirements. | Next |
| Opportunity recommendations | Rule-based baseline first; later enhanced by ML/AI. | Next |
| Career-path suggestions | Suggest development paths based on goals and skill gaps. | Later |
| Resume builder | Generate a resume from approved profile data. | Later |
| ATS/resume analysis | Provide a score only when a documented scoring method exists; never use placeholder percentages as real results. | Later |
| OCR/document extraction | Extract data from uploaded resumes/notices with user/admin review. | Later |
| Messaging | Controlled communication between permitted users. | Later |
| Email/push notifications | Delivery through approved services. | Next/Later |
| External opportunity ingestion | Official APIs, public feeds, or authorized integrations only. | Next |
| Payment gateway | Premium features or paid sessions if approved. | Future/optional |

## 8. What is not a feature commitment yet

The project requirement diagram includes many long-term capabilities. A diagram, mockup, or feature name does not mean the capability is approved for the MVP. Payment, full chatbot functionality, broad social networking, automated scraping, advanced AI, and separate microservices for each domain remain outside the initial release.
