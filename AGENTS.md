# AGENTS.md

Android app **Ma-fantatra** — single module `:app`, Jetpack Compose + Material 3, built with Gradle Kotlin DSL. There is no CI and no existing instruction files.

## Build & test

- Build: `.\gradlew :app:assembleDebug`
- Unit tests: `.\gradlew :app:testDebugUnitTest`
- Instrumented tests: `.\gradlew :app:connectedDebugAndroidTest` (needs a device/emulator)
- Lint: `.\gradlew :app:lintDebug`
- Verification order used here: `test -> lint` before considering changes done.

## Toolchain quirks (AGP 9 / Gradle 9.5 — do NOT "fix" these to older DSL)

- `app/build.gradle.kts` uses the new AGP 9.3 DSL. `compileSdk` is a block: `compileSdk { version = release(37) }` — not `compileSdk = 37`. Release build is configured via `optimization { enable = false }` — there is **no** `minifyEnabled`/`isMinifyEnabled`.
- R8 keep rules live in `app/src/main/keepRules/rules.keep` — there is no `proguard-rules.pro`.
- Compose compiler is the Kotlin 2.2 plugin (`org.jetbrains.kotlin.plugin.compose`); no `composeOptions.kotlinCompilerExtensionVersion`.
- KSP (Room + Hilt processors) requires `android.disallowKotlinSourceSets=false` in `gradle.properties` — AGP 9's built-in Kotlin otherwise rejects KSP-generated sources.
- Gradle toolchain requires JDK 25 (`gradle/gradle-daemon-jvm.properties`); foojay resolver auto-provisions it. `configuration-cache=true` is on.
- `local.properties` (`sdk.dir`) holds the local SDK path and is gitignored; never create/commit it.

## Data pipeline (offline-first)

- Room DB is pre-populated with seed data on first install via `createFromAsset("ma_fantatra.db")` → file `app/src/main/assets/databases/ma_fantatra.db`.
- The asset DB is **generated** from `tools/build_db.py` (Python) using the Room schema JSON exported at `app/schemas/`. Seed rows (procedures, document requirements, fokontany) live in the script.
- **Backend API** (FastAPI, Python) in `backend/` is the source of truth. The app syncs data from it at startup (in ViewModel `init`) and via periodic WorkManager sync (every 6h when network is available).
- Room serves as the **offline cache**: repositories read from Room (Flow), and refresh from the API when possible. If offline, the cache is used as-is.
- Base URL for Retrofit: `http://10.0.2.2:8000/` (emulator localhost). Change for real device testing.
- DB version is 2 (v1→v2 added `commune` table). Migration in `MafantatraDatabase.MIGRATION_1_2`.
- Checklist state is persisted with DataStore Preferences (`checked_doc_<documentId>` keys), not in Room.

## Architecture

- Package `com.ma_fantatra` split: `domain/` (models, repo interfaces — no Android deps), `data/` (Room entities/DAOs/DB, repos, DataStore, remote API), `di/` (Hilt modules), `ui/` (Compose screens + ViewModels per feature). Clean Arch + MVVM.
- Dependency injection is Hilt (2.60.1, KSP). `@HiltViewModel` in viewModels; inject repo interfaces from `di/`.
- Navigation is Navigation-Compose with type-safe `@Serializable` routes (`ui/navigation/AppRoutes.kt`). Top-level nav is `NavigationSuiteScaffold`; bottom-bar selection is computed from `destination.route == <Route>::class.qualifiedName` (do not use `hasRoute`/`hierarchy`, which do not resolve on this nav version).
- **Offline-first**: Each ViewModel calls `repository.refresh()` in `init`. Repositories try to sync from the API, silently falling back to the Room cache on failure. `SyncWorker` (WorkManager) runs every 6h with network constraint for background sync.

## Conventions

- Package/namespace is `com.ma_fantatra` (single segment after the TLD, underlines allowed). All code lives under `app/src/main/java/com/ma_fantatra/`.
- minSdk 24, targetSdk/compileSdk 37. UI is Compose-only, edge-to-edge, theme in `ui/theme/` with dynamic color enabled on Android 12+. Strings are in `res/values/strings.xml` (UI labels in French); editorial content (titles/costs/steps) is seed data in the asset DB, not string resources.
- GOTCHA: `NavigationSuiteScaffold` (material3 adaptive) content lambda takes **no** `PaddingValues` param and applies its own window insets — do not pass `innerPadding`. The adaptive scaffold also does not pull in `material-icons-core`; add it explicitly if you use `Icons.*`.