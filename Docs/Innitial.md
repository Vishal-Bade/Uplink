UPLINK — Repository Initialization Prompt
Use this prompt with the coding agent. The approved documents may be in the current directory or inside a folder named UPLINK_Project_Docs; locate them first, then create the new repository in a separate, clean UPLINK/ directory. Do not place the new repository inside the documentation source folder.
Task
Initialize a clean, maintainable UPLINK repository from scratch using the approved project documents. Do not attempt to implement the complete product in this initialization task.
Mandatory first step: read the source of truth
Read all of these files before creating or changing code. If the source files are in UPLINK_Project_Docs/, read them from there and then copy them into the new repository under docs/:
- PROJECT.md
- REQUIREMENTS.md
- FEATURES.md
- DESIGN_SYSTEM.md
- SCREENS.md
- NAVIGATION.md
- COMPONENTS.md
- ROADMAP.md
- TASKS.md
Treat them as the project baseline. Where they conflict, report the conflict and do not silently invent a decision. Do not rewrite these documents unless the team explicitly approves a change.
1. Repository structure
Create this structure, adapting only where a real toolchain constraint requires it:
UPLINK/
├── apps/
│   ├── mobile/              # React Native CLI student application
│   └── web/                 # Later: React recruiter/admin portal; placeholder only now
├── services/
│   ├── core-api/            # Spring Boot modular monolith
│   └── processing/          # Later: FastAPI OCR/AI processing; placeholder only now
├── infra/
│   └── docker/              # Local development infrastructure
├── docs/                    # The nine approved project documents
├── scripts/
├── .editorconfig
├── .gitignore
├── README.md
└── .env.example
Use independent package/build manifests for mobile, web, and backend. Do not introduce a JavaScript monorepo workspace tool merely for convenience; React Native, web, and Java have different build lifecycles. Keep the root repository lightweight.

Copy the nine project documents into docs/ and preserve their content.
2. Mobile application initialization
Initialize apps/mobile as a React Native CLI application for Android and iOS. Use the current stable React Native CLI release compatible with the installed Node.js, Java, Android SDK, and Xcode toolchains. Check the local environment and official React Native compatibility guidance before choosing versions. Avoid prerelease/nightly packages.

Use TypeScript for the new mobile codebase unless the existing team decision or repository evidence explicitly requires JavaScript. Record the language/version decision in the README. Keep the setup lean and use the design and navigation decisions from the approved documents.

Set up only the application foundation:
- One root SafeAreaProvider.
- One NavigationContainer.
- A bootstrap/loading route.
- Onboarding route and persistence boundary (the complete onboarding UI can follow its task).
- Authentication route placeholders and session boundary.
- Student tab navigator with exactly four tabs: Feed, Jobs, Q&A, and Mentors.
- Profile opened from the top-right avatar; do not create a Profile bottom tab.
- Consistent theme tokens based on DESIGN_SYSTEM.md.
- API client/service boundary and typed configuration.
- Environment configuration that does not commit secrets.
- Basic error boundary and a small set of shared primitives only when needed by the shell.
Do not build all screen features during initialization. Screens may be minimal, clearly labeled scaffolds until their implementation tasks are taken up. Do not use fake ATS percentages, fake recommendation scores, fabricated live counts, or production-looking claims. Clearly label seed/demo content.

Authentication secrets must not be stored in plain AsyncStorage. Before implementing token persistence, select and document a suitable native secure-storage package or platform-backed solution. AsyncStorage may be used for non-sensitive onboarding completion and preferences.
3. Core API initialization
Initialize services/core-api as a Spring Boot REST API using a current stable release compatible with the installed Java LTS toolchain. Use Maven Wrapper unless the repository already has an approved build-tool decision.

Create a modular monolith, not a collection of microservices. Establish package/module boundaries for the domains in COMPONENTS.md and REQUIREMENTS.md, but do not implement every domain yet.

For the initialization task, include:
- A runnable application entry point.
- Configuration profiles for local development and tests.
- A health endpoint (for example /api/v1/health).
- Request validation and a consistent error-response structure.
- Centralized configuration and environment-variable binding.
- Structured logging without secrets or sensitive personal data.
- OpenAPI documentation setup if it is compatible with the selected Spring Boot version.
- A database connection boundary and Flyway migration setup.
- A minimal initial migration only if required for the health/startup path; do not invent the full schema prematurely.
- Basic unit/integration test scaffolding.
Do not build full JWT authentication, user management, opportunity CRUD, Q&A ranking, or mentor booking as part of repository initialization. Those are feature tasks in TASKS.md and should be implemented in dependency order.

PostgreSQL is the authoritative data store. The planned hosted option is Supabase PostgreSQL. The app must call the Spring Boot API; do not let the mobile client directly read/write application tables through a Supabase client. Supabase Storage may be used for files later under backend-controlled access rules.

Redis is optional at this stage. Add a local Docker service only if the team needs it for development; do not make the API fail to start simply because Redis is unavailable before a real Redis-dependent feature exists.
4. Deferred services
Create concise README placeholders only:
- apps/web/README.md: React recruiter/admin portal is a later roadmap milestone.
- services/processing/README.md: FastAPI is reserved for specialized OCR, document, resume, or AI/ML processing when justified.
Do not initialize or install full web/FastAPI dependencies in this task. Do not split the core backend into microservices.
5. Local development infrastructure
Create infra/docker/compose.yaml for local PostgreSQL development, with a named volume and health check. Redis may be an optional profile/service if useful. Do not add default production credentials. Read connection values from environment variables and provide safe local placeholders in .env.example.

Do not commit .env, API keys, signing keys, Supabase service-role keys, database passwords, or private certificates. Add appropriate ignore rules.
6. Repository quality baseline
Add:
- Root README with prerequisites and exact start/test commands for each initialized service.
- .gitignore covering Node, React Native, Android, iOS, Java/Maven, Python, IDE, build, and environment artifacts.
- .editorconfig for consistent editor behaviour.
- .env.example containing variable names and non-secret placeholders only.
- Formatting/lint/test scripts for the initialized codebases where appropriate.
Do not add CI workflows unless they can be validated in this task; if added, keep them minimal and document required secrets.
7. Explicitly out of scope for initialization
Do not implement:
- Full authentication/verification workflows.
- Complete student profile editing.
- Feed ranking or recommendation engine.
- Opportunity ingestion, search, filters, applications, or tracking.
- Q&A creation, answers, same-doubt tracking, or ranking.
- Mentor booking workflows.
- Recruiter portal or admin dashboard.
- Skill assessments or automated skill-gap analysis.
- Resume generation, ATS analysis, OCR, or document extraction.
- Messaging, push notifications, payments, or advanced analytics.
Keep these as explicit future tasks in TASKS.md.
8. Verification before completion
Run and report the actual results of the checks that the local environment supports:
1. Mobile dependency installation.
2. Mobile lint/typecheck/test commands that exist.
3. Android debug build (not only a Gradle dry-run), if Android SDK/JDK are available.
4. iOS build only on macOS with Xcode; otherwise state that it was not run.
5. Spring Boot compile and tests using Maven Wrapper.
6. API health endpoint startup test.
7. docker compose config and PostgreSQL health check if Docker is available.
8. Secret scan or manual check confirming no real secrets were committed.
Do not claim a build passed unless the command actually completed successfully. If an environment prerequisite is missing, report the exact blocker and command not run.
9. Final report
At the end, report:
- Repository tree created.
- Toolchain versions detected and versions selected.
- Files and configurations added.
- Commands to start mobile, API, and local database.
- Verification commands and actual pass/fail results.
- Any environment blockers or unresolved decisions.
- The next unchecked task IDs from TASKS.md.
Proceed incrementally. Initialize the foundation and runnable shells only. Do not jump ahead into all product features.