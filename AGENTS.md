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

- Room DB is pre-populated and shipped: `MafantatraDatabase` is built with `createFromAsset("ma_fantatra.db")` → file `app/src/main/assets/databases/ma_fantatra.db`.
- The asset DB is **generated**, not hand-written: `python tools/build_db.py`. It re-creates the tables verbatim from the Room schema JSON exported at `app/schemas/com.ma_fantatra.data.local.MafantatraDatabase/1.json` (created by KSP via `ksp { arg("room.schemaLocation", ...) }`), so schema and asset never drift. Seed rows (procedures, document requirements, fokontany) live in the script.
- Regenerate order when entities/DAO change: `.\gradlew :app:kspDebugKotlin` (exports new schema JSON) → `python tools/build_db.py` → add a migration or bump DB `version`.
- Checklist state is persisted with DataStore Preferences (`checked_doc_<documentId>` keys), not in Room.

## Architecture

- Package `com.ma_fantatra` split: `domain/` (models, repo interfaces — no Android deps), `data/` (Room entities/DAOs/DB, repos, DataStore), `di/` (Hilt modules), `ui/` (Compose screens + ViewModels per feature). Clean Arch + MVVM.
- Dependency injection is Hilt (2.60.1, KSP). `@HiltViewModel` in viewModels; inject repo interfaces from `di/`.
- Navigation is Navigation-Compose with type-safe `@Serializable` routes (`ui/navigation/AppRoutes.kt`). Top-level nav is `NavigationSuiteScaffold`; bottom-bar selection is computed from `destination.route == <Route>::class.qualifiedName` (do not use `hasRoute`/`hierarchy`, which do not resolve on this nav version).

## Conventions

- Package/namespace is `com.ma_fantatra` (single segment after the TLD, underlines allowed). All code lives under `app/src/main/java/com/ma_fantatra/`.
- minSdk 24, targetSdk/compileSdk 37. UI is Compose-only, edge-to-edge, theme in `ui/theme/` with dynamic color enabled on Android 12+. Strings are in `res/values/strings.xml` (UI labels in French); editorial content (titles/costs/steps) is seed data in the asset DB, not string resources.
- GOTCHA: `NavigationSuiteScaffold` (material3 adaptive) content lambda takes **no** `PaddingValues` param and applies its own window insets — do not pass `innerPadding`. The adaptive scaffold also does not pull in `material-icons-core`; add it explicitly if you use `Icons.*`.