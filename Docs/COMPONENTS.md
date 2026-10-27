# UPLINK — Component Catalogue

> **Version:** 1.0  
> **Status:** Baseline for team review  
> **Last updated:** 26 October 2026

## 1. Component architecture principles

- Keep screens responsible for composition, not for duplicating all business logic.
- Separate reusable visual components from domain components.
- Keep API calls in service/query layers or hooks, not directly inside presentational components.
- Reuse design tokens from [DESIGN_SYSTEM.md](./DESIGN_SYSTEM.md).
- Prefer small, explicit props over components with many hidden behaviours.
- Keep mobile and web components platform-specific where their interaction models differ; share tokens and domain rules where possible.

## 2. Mobile shared components

| Component | Responsibility |
|---|---|
| `AppHeader` | Compact header with optional title and saved, notification, and profile actions. Must respect top safe area. |
| `BottomTabBar` | Four tabs: Feed, Jobs, Q&A, Mentors. Must account for bottom system inset. |
| `ScreenContainer` | Standard horizontal padding, background, and screen layout. Must not duplicate navigator insets. |
| `PrimaryButton` | Main action button with loading/disabled states. |
| `SecondaryButton` | Less prominent bordered action. |
| `IconButton` | Accessible icon-only action. |
| `SearchField` | Search input with clear action and optional filter trigger. |
| `FilterChips` | Compact selectable filters; avoid oversized pill styling. |
| `SectionHeader` | Section title and optional See All action. |
| `Avatar` | Profile image with initials fallback and optional verification/status mark. |
| `StatusLabel` | Small label for meaningful statuses only. |
| `Divider` | Subtle visual separator. |
| `EmptyState` | Explains missing content and provides a useful action. |
| `ErrorState` | Explains a recoverable failure and provides Retry where possible. |
| `LoadingState` / `Skeleton` | Restrained loading feedback. |
| `ConfirmDialog` | Confirmation for destructive or important actions. |
| `Toast` | Short success/error feedback; should not be the only way to report a critical failure. |

## 3. Mobile domain components

### Feed and opportunity components

- `OpportunityListItem`: title, organization, work mode/location, type, pay/benefit when supplied, deadline, save action.
- `OpportunityCard`: use only where a grouped/featured presentation is useful; keep it compact.
- `ResumeReadinessSummary`: show only a real calculation with a defined method and data timestamp. Hide or label sample values during development.
- `EventListItem`: event title, organization, date, mode/location, and details action.
- `ApplicationStatusTimeline`: status changes and timestamps for an application.

### Q&A components

- `QuestionListItem`: author, title, preview, topic tags, answer count, same-doubt count, timestamp, and open action.
- `QuestionTopicSelector`: select topic tags with a configured maximum.
- `SameDoubtButton`: one toggle per user/question; display current count.
- `QuestionActivityBar`: answer count, same-doubt signal, and allowed reactions.
- `AnswerItem`: author, answer text, optional attachment/code block, helpful action, accepted state, reply action.
- `AnswerComposer`: answer/reply input with validation and submit state.
- `QuestionSortMenu`: Most Helpful / Newest (and later other supported modes).
- `AskQuestionForm`: title, description, topics, optional attachment, and submit state.
- `ReportContentAction`: allows user reporting with a reason.

### Mentor components

- `MentorListItem`: avatar, name, role/organization, expertise, and booking action.
- `MentorExpertiseTags`: compact domain labels.
- `MentorAvailability`: availability only when maintained and reliable.
- `BookingRequestForm`: preferred time/slot and optional message.
- `BookingStatusLabel`: Requested, Confirmed, Declined, Cancelled, Completed.

### Profile components

- `ProfileHeader`
- `ProfileSection`
- `SkillTag` / `SkillList`
- `ProjectItem`
- `CertificationItem`
- `ExperienceItem`
- `ProfileCompleteness` (only when based on configured required fields)

## 4. Web components (later milestone)

- `DashboardShell` with role-specific navigation.
- `DataTable` with pagination, sorting, loading, and empty states.
- `VerificationStatusPanel` and `VerificationReviewActions`.
- `OpportunityEditor` and `OpportunityPreview`.
- `ApplicantTable` and `CandidateProfileSummary`.
- `ShortlistAction` and `ApplicationStageControl`.
- `ModerationQueue` and `AuditLogTable`.

Web components must use the same color, typography, spacing, and status semantics as mobile, adapted to larger screens and keyboard use.

## 5. State and data responsibilities

- Use Zustand for appropriate client-side session/UI state and small cross-screen state that must be shared.
- Keep server data and mutations in a dedicated API layer. Use explicit loading/error/success states.
- AsyncStorage is suitable for non-sensitive local preferences and selected cache data; it is not secure storage for credentials or long-lived secrets.
- Select a React Native-compatible secure-storage solution before implementing persistent auth tokens.
- Avoid storing duplicate copies of server entities in multiple unrelated stores.
- Optimistic updates should be used only where rollback is handled correctly.

## 6. API/service modules

Suggested client-side service boundaries:

- `authService`
- `profileService`
- `opportunityService`
- `applicationService`
- `questionService`
- `answerService`
- `mentorService`
- `notificationService`

Each service should centralize request construction, authentication headers, response parsing, and error mapping. UI components should not know database table names or call the database directly.

## 7. Naming and file organization guidance

A suggested React Native structure:

```text
src/
  assets/
  components/
    common/
    feed/
    opportunities/
    qa/
    mentors/
    profile/
  screens/
    auth/
    feed/
    jobs/
    qa/
    mentors/
    profile/
  navigation/
  services/
  hooks/
  store/
  theme/
  utils/
```

Adapt this to the repository after inspection. Do not duplicate a component just because a screen uses it differently; expose variants when the behaviour is genuinely shared.
