# UPLINK — Design System

> **Version:** 1.0  
> **Status:** Baseline for new UI implementation  
> **Last updated:** 26 October 2026

## 1. Design direction

UPLINK uses a **minimal, professional, information-first interface**. The visual direction should feel like a mature productivity and career platform, not a decorative social app or an AI-generated dashboard.

Reference qualities: the clarity and restraint of Linear and Notion, combined with the information structure of established job platforms. These are visual references only; do not copy their branding or proprietary layouts.

### Core principles

- Put information and actions ahead of decoration.
- Use white and neutral surfaces with one controlled blue accent.
- Prefer typography, alignment, and spacing over shadows and colorful containers.
- Use consistent outline icons; do not use 3D emoji-style icons.
- Do not wrap every item in a card or pill.
- Use status colors only when they communicate a real state.
- Keep the interface calm, readable, and fast to scan.

## 2. Color tokens

Use these as the initial shared tokens. If the repository already contains equivalent tokens, consolidate rather than creating duplicates.

| Token | Hex | Usage |
|---|---|---|
| `color.primary` | `#135BEC` | Primary actions, active navigation, links, selected states. |
| `color.primaryPressed` | `#104BC2` | Pressed/active primary controls. |
| `color.background` | `#FFFFFF` | Main screen background. |
| `color.surface` | `#FFFFFF` | Cards, sheets, and input surfaces. |
| `color.surfaceMuted` | `#F7F9FC` | Subtle section backgrounds and selected neutral surfaces. |
| `color.text` | `#111827` | Main text. |
| `color.textSecondary` | `#64748B` | Descriptions, metadata, hints. |
| `color.textMuted` | `#94A3B8` | Timestamps and lower-priority metadata. |
| `color.border` | `#E5E7EB` | Dividers, input borders, card outlines. |
| `color.success` | `#16A34A` | Confirmed success states. |
| `color.warning` | `#D97706` | Warnings and approaching deadlines. |
| `color.danger` | `#DC2626` | Errors, destructive actions, rejected states. |
| `color.info` | `#2563EB` | Informational state where distinct from primary is useful. |

Do not use all status colors in the same card. Avoid gradients as the default treatment. A gradient requires a specific product reason and design approval.

## 3. Typography

Use the platform system font initially for consistent rendering and low setup overhead. Inter may be adopted later if the team bundles and tests it across platforms.

Suggested mobile scale (sp):

| Token | Size | Weight | Usage |
|---|---:|---|---|
| `type.screenTitle` | 20–22 | 600/700 | Screen heading. |
| `type.sectionTitle` | 16–18 | 600 | Section heading. |
| `type.cardTitle` | 15–16 | 600 | Opportunity/question/mentor title. |
| `type.body` | 14–15 | 400 | Main content. |
| `type.bodySmall` | 12–13 | 400 | Metadata and helper text. |
| `type.label` | 12–13 | 500/600 | Form labels and compact controls. |
| `type.button` | 14–15 | 600 | Button labels. |

Use sentence case. Avoid unnecessary uppercase labels and extra-bold text. Do not shrink long descriptions to fit a card; truncate with a clear route to the full detail view.

## 4. Spacing and layout

Use a 4 dp base scale and prefer multiples of 4 or 8.

| Token | Value |
|---|---:|
| `space.1` | 4 dp |
| `space.2` | 8 dp |
| `space.3` | 12 dp |
| `space.4` | 16 dp |
| `space.5` | 20 dp |
| `space.6` | 24 dp |
| `space.8` | 32 dp |

- Default horizontal screen padding: 16 dp, adjusted for device width.
- Space between major sections: 20–24 dp.
- Space inside compact list rows: 12–16 dp.
- Touch targets should be at least 44–48 dp where practical.
- Keep content aligned to a consistent left edge.
- Use responsive constraints rather than hardcoded device widths.

## 5. Shape and elevation

UPLINK should not look like a collection of oversized bubbles.

| Element | Radius guidance |
|---|---:|
| Small chips | 6–8 dp |
| Inputs | 6–8 dp |
| Buttons | 6–8 dp |
| Cards/list containers | 8–10 dp |
| Modal/sheet | 12–16 dp at top corners |
| Avatar | Circular |

Use thin borders (`color.border`) as the default separation method. Use no shadow for most cards. Reserve a subtle elevation for menus, dialogs, and modal sheets when needed for layering. Avoid large soft shadows on every component.

## 6. Iconography and imagery

- Use one consistent outline icon family across the app.
- Use simple icons for bookmark, notifications, search, filters, navigation, and actions.
- Avoid decorative 3D bells, 3D briefcases, and emoji icons as primary controls.
- Use company logos only when they are legitimate assets and licensing/usage is permitted; otherwise use initials or a neutral organization mark.
- Use real mentor/student avatars only when provided or consented; otherwise use initials or neutral placeholders.
- Decorative illustrations are optional, not required for ordinary screens.

## 7. Buttons and controls

- Primary button: blue fill, white text, compact radius, clear action label.
- Secondary button: white/neutral fill, thin border, dark text.
- Tertiary action: text or icon button with a visible accessible label.
- Destructive action: danger color and confirmation when irreversible.
- Disabled controls must remain legible and communicate why they are unavailable.
- Selected filters should use a subtle blue-tinted background or blue text/border, not large glowing pills.

## 8. Cards and list rows

Prefer list rows when users need to compare many opportunities or questions. Use cards only where grouping is useful.

An opportunity row should prioritize: title, organization, location/work mode, type, compensation/deadline where relevant, and save/apply action.

A question row should prioritize: question title, concise preview, topic tags, author context, answer count, same-doubt count, and recency.

A mentor row should prioritize: name, role/organization, expertise, and booking action.

Do not show a badge on every item. Use badges only for meaningful states such as Verified, Accepted Answer, or Application Status.

## 9. Navigation and headers

- Student bottom navigation contains **Feed**, **Jobs**, **Q&A**, and **Mentors**.
- Profile is opened through the top-right avatar; do not add a Profile bottom tab in this baseline.
- Main headers should be compact and not repeat the UPLINK application name on every screen.
- Feed should not show a greeting as the dominant header content. Use the available header space for simple actions such as saved items, notifications, and avatar.
- Jobs and Mentors may show their screen title on the same top row as header actions.
- Q&A may show the short `Q&A` title on the top row.

## 10. Status, safe areas, and system UI

- Use `react-native-safe-area-context` consistently.
- Initialize one SafeAreaProvider at the app root.
- Do not apply the same inset at both the navigator and screen level.
- Keep content clear of status bars, display cutouts, and Android gesture/three-button navigation areas.
- Bottom navigation must sit above system navigation controls and account for bottom insets.
- Set status-bar icon/text contrast appropriate to the current background.
- Test keyboard and modal layouts on real Android devices.

## 11. States and accessibility

Every data-driven screen must define:

- Loading state (prefer restrained skeletons or a small progress indicator).
- Empty state with a clear next action.
- Error state with retry where appropriate.
- Offline or stale-data state where relevant.
- Disabled and success states for actions.

Use accessible labels for icon-only buttons, adequate contrast, screen-reader order, scalable text, and visible focus states on web. Do not communicate status by color alone.

## 12. Dark mode

Dark mode is not part of the initial release. Keep tokens centralized so a dark theme can be added later without rewriting individual screens.
