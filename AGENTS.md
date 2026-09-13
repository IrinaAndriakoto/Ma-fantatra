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
- Gradle toolchain requires JDK 25 (`gradle/gradle-daemon-jvm.properties`); foojay resolver auto-provisions it. `configuration-cache=true` is on.
- `local.properties` (`sdk.dir`) holds the local SDK path and is gitignored; never create/commit it.

## Conventions

- Package/namespace is `com.ma_fantatra` (single segment after the TLD, underlines allowed). All code lives under `app/src/main/java/com/ma_fantatra/`.
- minSdk 24, targetSdk/compileSdk 37. UI is Compose-only, edge-to-edge, theme in `ui/theme/` with dynamic color enabled on Android 12+.
- Current UI is a skeleton: `MainActivity` → `MafantatraApp()` uses `NavigationSuiteScaffold` (material3 adaptive navigation suite) with a manual `rememberSaveable` state — no Navigation library wired up yet.