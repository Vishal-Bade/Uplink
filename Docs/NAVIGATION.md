# UPLINK — Navigation Specification

> **Version:** 1.0  
> **Status:** Baseline for team review  
> **Last updated:** 26 October 2026

## 1. Navigation principles

- Use one `NavigationContainer` for the mobile application.
- Keep navigation structure separate from screen implementation.
- Main student navigation contains four tabs: **Feed**, **Jobs**, **Q&A**, and **Mentors**.
- Profile is opened from the avatar in the shared header; it is not a bottom tab.
- Detail and form screens are pushed onto the relevant stack, not implemented as separate navigation containers.
- Use server-side authorization for protected operations; navigation guards are not security controls.

## 2. Mobile application flow

```text
App Bootstrap
  ├─ First launch / onboarding not complete → Onboarding
  ├─ No valid session → Authentication
  └─ Valid session → Role Router
       ├─ STUDENT → Student Tab Navigator
       ├─ RECRUITER → Recruiter Area (when available)
       └─ ADMIN → Admin Area (when available)
```

Onboarding completion and authentication state are separate. Completing onboarding must not be treated as authentication.

## 3. Student tab navigator

| Tab | Suggested route name | Root screen |
|---|---|---|
| Feed | `FeedTab` | `FeedScreen` |
| Jobs | `JobsTab` | `JobsScreen` / Explore Opportunities |
| Q&A | `QATab` | `QAFeedScreen` |
| Mentors | `MentorsTab` | `MentorsScreen` |

The visual tab labels are **Feed**, **Jobs**, **Q&A**, and **Mentors**. Do not add a Profile tab.

Each tab may own a nested stack so its detail screens can be pushed while preserving the selected tab and back history.

## 4. Suggested route inventory

Route names are recommendations. The team may adjust naming to match the chosen React Navigation structure, but the destinations and flows should remain equivalent.

### Root/authentication

- `BootstrapScreen`
- `OnboardingScreen`
- `SignInScreen`
- `SignUpScreen`
- `VerificationScreen`
- `PasswordRecoveryScreen`

### Feed stack

- `FeedScreen`
- `EventDetailScreen` (when event content is implemented)

### Jobs stack

- `JobsScreen`
- `OpportunityDetailScreen`
- `OpportunityFiltersScreen` or filter modal
- `SavedOpportunitiesScreen`
- `ApplyScreen` (internal applications only)
- `MyApplicationsScreen`
- `ApplicationDetailScreen`

### Q&A stack

- `QAFeedScreen`
- `QuestionDetailScreen`
- `AskQuestionScreen`
- `EditQuestionScreen`
- `ReportContentScreen` or report modal

### Mentors stack

- `MentorsScreen`
- `MentorDetailScreen`
- `BookingRequestScreen`
- `MyMentorshipRequestsScreen`

### Profile/account stack

- `MyProfileScreen`
- `EditProfileScreen`
- `SkillsScreen`
- `ProjectsScreen`
- `CertificationsScreen`
- `ExperienceScreen`
- `AccountSettingsScreen`
- `NotificationScreen`

## 5. Header actions

The main header may expose:

- Saved/bookmark destination.
- Notifications destination.
- Profile avatar destination.

Header presentation rules:

- Do not repeat the UPLINK brand name in each screen header.
- Feed does not need a greeting.
- Jobs and Mentors may display their screen title in the same row as header actions.
- Q&A may display the short title `Q&A` in the header.
- All action icons require accessible labels and adequate touch targets.

## 6. Back behaviour

- A detail screen returns to the list/feed from which it was opened.
- A form with unsaved changes should warn before discarding them.
- Android hardware/system back should follow the active stack first.
- Switching tabs should not unexpectedly reset every tab's state unless that is the explicit product behaviour.
- Modal dismissal must not accidentally trigger a second navigation action.

## 7. Deep links and notifications

Plan route mapping for opportunity details, Q&A questions, mentor profiles, and application updates. Deep links should validate that the user is authenticated and authorized before opening protected content. If a route is not available or content is deleted, show a recoverable not-found state.

## 8. Safe area and root composition

- Initialize `SafeAreaProvider` once at the application root.
- Keep one `NavigationContainer`.
- Use navigator-managed safe areas or explicit insets consistently; do not double-apply top/bottom padding.
- Ensure modal, keyboard, status bar, display cutout, and Android navigation-area behaviour is tested on devices.

## 9. Web navigation (later milestone)

The web application should use role-specific route groups:

- `/recruiter/*` for company, opportunities, applicants, candidate discovery, and shortlists.
- `/admin/*` for verification, moderation, configuration, and audit views.

Unknown roles or unauthorized routes must redirect to a safe access-denied or sign-in page. Web route guards complement, but do not replace, backend authorization.
