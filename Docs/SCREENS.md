# UPLINK — Screen Inventory

> **Version:** 1.0  
> **Status:** Baseline for team review  
> **Last updated:** 26 October 2026

This document defines screen responsibilities. The first four mobile tabs are Feed, Jobs, Q&A, and Mentors. Profile is accessed from the avatar in the header.

## 1. Mobile app: shared and entry screens

| Screen | Purpose | Release |
|---|---|---|
| Splash / Bootstrap | Restore session, load required app state, and route to the correct entry point without flashing the wrong screen. | MVP |
| Onboarding | Three concise introduction slides; Skip and Continue; persist completion. | MVP |
| Sign In | Authenticate an existing account. | MVP |
| Sign Up | Create a student account and collect only required initial fields. | MVP |
| Verification / Recovery | Handle account verification or access recovery according to the selected auth flow. | MVP/Next |
| Notifications | List in-app notifications and open the relevant destination. | MVP basic |

## 2. Main student navigation screens

### 2.1 Feed

**Purpose:** Provide a concise overview of relevant career activity.

**Primary content:**
- Compact top toolbar: saved/bookmark action, notifications, and profile avatar.
- Search entry point for opportunities and relevant content.
- Simple topic/category tabs such as For You, Internships, Jobs, Hackathons, and Events.
- Resume/profile readiness only when based on real, documented data. Do not show a hardcoded ATS score.
- Recommended or featured opportunities.
- Upcoming events or verified campus notices when available.

**Important layout rule:** No greeting is required. Do not place the UPLINK app name in the top header. The first content should not sit under the status bar.

### 2.2 Jobs / Explore Opportunities

**Purpose:** Allow students to discover and compare opportunities.

**Primary content:**
- Screen title such as Explore Opportunities in the top row.
- Header actions: saved items, notifications, and profile avatar.
- Search by role, organization, skill, or keyword.
- Opportunity type filters.
- Structured filters such as remote/on-site/hybrid, location, stipend/salary, and deadline when data is available.
- Sort options such as relevance, newest, and deadline.
- Compact opportunity list items.

**Related screens:** Opportunity Detail, Saved Opportunities, Apply/External Application handoff, My Applications, Application Detail/History, and Filters.

### 2.3 Q&A

**Purpose:** Provide a structured student question-and-answer space.

**Feed views:** For You, Unanswered, Trending, My Questions.

**Topic filters:** DSA, Web Development, AI/ML, Placements, Academics, Project Help, Resume, System Design, DevOps, and an expandable topic list.

**Question card content:** Author, concise academic context if visible, question title, preview, topic tags, answer count, same-doubt count, and timestamp. Do not add a prominent Unanswered badge to every card; the Unanswered feed filter provides that path.

**Related screens:** Question Detail, Ask Question, Edit Question (author only, within allowed rules), Answer Composer/Reply, and Report Content.

### 2.4 Mentors

**Purpose:** Help students find and request guidance from mentors.

**Primary content:**
- Screen title such as Find Mentors in the top row.
- Search by mentor name, organization, or expertise.
- Domain filters such as Engineering, Product, Data Science, and UI/UX.
- Compact mentor list rows with approved profile image/initials, name, role, organization, expertise, and booking action.

**Related screens:** Mentor Detail, Booking Request, Booking Confirmation/Status, and My Mentorship Requests.

## 3. Profile and personal workspace screens

Profile is accessed from the avatar in the main header, not from a bottom tab.

| Screen | Content |
|---|---|
| My Profile | Basic details, academic details, skills summary, projects, experience, certifications, achievements. |
| Edit Profile | Editable student information with validation. |
| Skills | Skill list and proficiency/source fields when supported. |
| Projects | Project portfolio entries and links. |
| Certifications | Certification details and proof links/files where supported. |
| Experience | Internship/work experience records. |
| Saved Opportunities | Bookmarked opportunity list. |
| My Applications | Application list and status/history. |
| Account Settings | Privacy, preferences, help, sign-out. |

## 4. Detail and action screens

- **Opportunity Detail:** Full opportunity description, eligibility, requirements, location, compensation, deadline, source, verification/publication state, and action.
- **Apply:** Internal application flow where enabled; external links must be labeled as external.
- **Application Status:** Current stage and timestamped history.
- **Question Detail:** Full question, tags, same-doubt interaction, answers, helpful actions, accepted answer, and report action.
- **Ask Question:** Title, description, up to a defined number of topic tags (initial target: five), optional permitted attachment, and post action.
- **Mentor Detail:** Bio, organization/role, expertise, availability, and request action.
- **Booking Request:** Preferred slot or time window, optional message, and confirmation.

## 5. Web application screens (later milestone)

### Recruiter area

- Recruiter sign-in and account state.
- Company profile and verification status.
- Recruiter overview.
- Opportunity list and create/edit opportunity form.
- Applicant list and applicant detail.
- Candidate search and shortlist.
- Opportunity/application status management.

### Admin area

- Admin sign-in and overview.
- User/recruiter/company verification queue.
- Opportunity review/moderation.
- Q&A reports and moderation queue.
- Reference-data management (topics, opportunity types, skills) where required.
- Audit log and basic operational reporting.

## 6. Screen-level quality requirements

Every screen must define its safe-area behaviour, header ownership, scroll behaviour, keyboard handling, loading state, empty state, error state, and primary action. Long forms should preserve entered content on recoverable errors. Screen titles and header actions must follow [DESIGN_SYSTEM.md](./DESIGN_SYSTEM.md).
