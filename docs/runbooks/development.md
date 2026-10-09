---
id: runbook-development
title: "Development and verification"
type: Runbook
status: active
valid_from: 2026-10-08
scope:
  system: readerparser
relations:
  - type: supported_by
    target: ../evidence/navigation-and-tooling-history.md
---

# Development and verification

## Setup and build

Use the checked-in Gradle wrapper, root settings/build scripts, gradle.properties, and gradle/libs.versions.toml.
The checked-in `gradle/wrapper/gradle-wrapper.jar` bootstraps the distribution pinned by `gradle-wrapper.properties` without preinstalled Gradle.
The current properties pin Gradle 9.3.1 and its distribution SHA256.
The current build uses compileSdk/targetSdk 36, minSdk 26, and Java source/target 17.
README's former minSdk 36 statement was stale. Do not infer a required JDK 21 from the missing old daemon-properties file.
Set ANDROID_HOME and ANDROID_SDK_ROOT to an installed SDK before verification.
The recorded environment command uses `/home/pedro/Android/Sdk`; verify that path on the current host before using it.
Build with `./gradlew :app:assembleDebug`; install with `./gradlew :app:installDebug` when device work is authorized.
Release signing reads KEYSTORE_PATH, KEYSTORE_PASSWORD, KEY_ALIAS, and KEY_PASSWORD without embedding keys in source.
APKs are under app/build/outputs/apk. KSP writes Room exports under app/schemas, exposed to androidTest assets through Gradle sourceSets.
The version catalog centralizes versions/libraries/plugins. Compose entries inherit the BOM, Ktor modules share one version, and Room compiler uses KSP.
Test coordinates share the catalog to avoid drift. Hilt-work and Hilt-navigation-compose connect workers and screen injection.
The historical future module split kept one shared catalog. Each module would consume `libs.*` aliases without duplicating catalog entries.
That split is illustrative intent, not an approved module change. Catalog changes affect what CI scripts compile and test.
After an approved new dependency, add its version/library/plugin entries as needed, consume catalog aliases in the app build, and verify assembly.

## Verification gate and tests

New code ships with meaningful tests. Sources use saved HTML and MockEngine. Repositories/ViewModels use hand-rolled JVM fakes.
Room changes require instrumented migration tests. Stateless Content uses Compose UI tests.
app/src/test/kotlin/com/opus/readerparser holds JVM tests. app/src/androidTest/java/com/opus/readerparser holds instrumented tests.
Test source sets mirror production package responsibilities and use the main classpath plus their test libraries.
JVM libraries include JUnit, Turbine, MockEngine, Truth, and coroutine tests. Instrumented coverage adds Room/WorkManager/Compose UI tools.
Use MainDispatcherRule.kt, KtorMockHelpers.kt, and TestFixtures.kt from the JVM testutil folder.
Use FakeCoilRule.kt from the instrumented testutil folder for image tests.
Use production-class Test/AndroidTest naming conventions where applicable, checking actual names before routing.
Run the required scoped suite through runner and report evidence:

```bash
./gradlew :app:assembleDebug --console=plain
./gradlew :app:lintDebug --console=plain
./gradlew :app:testDebugUnitTest --console=plain
```

Run :app:ktlintCheck and :app:detekt when configured. Run connectedDebugAndroidTest for device/migration/UI changes with an available device.
Never suppress warnings, add suppression annotations, disable rules, or silence failed tests merely to obtain green.
Capture a failure's full output once or inspect its report/XML before fixes and targeted reruns.
Tests touching Android Log/Uri/ContentResolver/ContentValues need platform isolation or instrumented execution unless JVM support is configured.
Current worker tests exist in JVM and instrumented sets. The old map's claimed LibraryUpdateWorker test absence is historical observation, not verification.
The historical worker map described JVM `ChapterDownloadWorkerTest` using `TestListenableWorkerBuilder`, fake repositories, and a fake `DownloadStore`.
Its scenarios covered success, source-not-found, retry on network error, and final failure.
It separately described instrumented `ChapterDownloadWorkerTest` using `WorkManagerTestInitHelper` for the full on-device WorkManager lifecycle.
These descriptions preserve the original testing claims. This migration does not verify those APIs, test locations, or scenario execution.
Independent runner must return VERDICT: PASSED, FAILED, or BLOCKED with evidence. Only PASSED meets the gate.
Read-only reviewer checks layering, identity, four-file/state/effect rules, error handling, tests, and style including no wildcard imports or production runBlocking.
Findings identify file/line and severity BLOCKER, SHOULD-FIX, or nit.
Historical three-cycle runner/reviewer correction limits remain in [Agent lanes](../specs/agent-lanes.md#historical-verification-correction-cycle).

## Developer scripts and journeys

scripts are developer/CI tools, excluded from the APK.
The original script map records these prerequisites. Current availability and each script's actual code govern execution.

| Script | Recorded prerequisites |
| --- | --- |
| `ci-check` | `adb`, `android` CLI for journeys, and `./gradlew` |
| `pre-push` | optional `adb`, `ANDROID_HOME`, and `./gradlew` |
| `emulator` | `android` CLI and `python3` for JSON configuration |
| `run-journeys` | `android` CLI, `adb`, `python3`, and `./gradlew` for APK build with `--setup` |
| `setup-wsl.sh` | `curl`, `sudo apt-get`, and `python3`, on Linux/WSL only |

scripts/ci-check runs lint, JVM tests, device tests when attached, assembly, and optional journey provisioning.
Missing device/AVD/CLI can skip optional instrumentation/journey work. Report skips instead of equating them to passed behavior.
scripts/pre-push requires ANDROID_HOME, runs JVM tests, optionally device tests, and aborts pushes on failures.
It can be installed as a Git pre-push hook. Integration owns git writes.
scripts/emulator uses avd-config.json and create/start/stop/list/delete operations.
Its documented defaults are readerparser-api36 and system-images;android-36;google_apis;x86_64.
Create is idempotent. Delete stops and removes an AVD. Inspect authorization before destructive operations.
scripts/run-journeys lists XML names, prints one journey's description/ordered actions, or provisions an emulator and installs the APK with --setup.
It prints instructions rather than executes touch/tap/swipe. journey-runner uses android-cli and reports journey evidence per journeys/README.md.
Use scripts/emulator stop for teardown when appropriate.
scripts/setup-wsl.sh performs a one-time idempotent Linux/WSL bootstrap: JDK17, android CLI, SDK36/build-tools36/platform-tools/emulator/x86_64 image.
It updates the selected shell RC with ANDROID_HOME/JAVA_HOME/PATH. Current tool availability and permissions govern execution.
Script changes need workflow-specific manual/CI verification, not assumed unit coverage.

## Delivery and commit conventions

Use git for local history/diff/staging/commit/branch/push/pull and gh for GitHub PRs/issues/releases/workflow checks.
Prefer CLI over token-heavy integration when equivalent. Git delivery requests route to integrator.
Once a PR exists, babysit-pr owns the watch/fix/merge loop under user authorization.
Commits use exactly one prefix: feat for new capability/files/screens/plugins, fix for defects in previously committed code,
refactor for behavior-preserving restructuring, ci for quality gates/hooks/tests, cd for release/signing/deployment/bootstrap,
and docs for agent guidance, architecture, README, specs, and KDoc.
A compile correction within an uncommitted feature remains part of feat. Behavior-changing restructuring is feat or fix rather than refactor.
One commit expresses one verb. A screen plus its tests is one feat. A new screen plus an unrelated migration fix needs separate feat/fix commits.
Subjects are imperative, present tense, at most 72 characters. Bodies explain why rather than repeat the diff.
Merge commits preserve grouped commits when merge is authorized. CI failures block merge.

## Approval and specialist boundaries

Ask before changing the Source interface or changing entity identity, primary-key, or foreign-key behavior.
Ask before adding a new top-level layer/module, manifest permission, or third-party dependency.
Ask before replacing Hilt or Ktor's engine.
Routine source/screen/repository-method/migration work otherwise proceeds within the authorized task scope.
Use source-author for site plugins/fixtures, screen-author for four-file screens, room-migration for schemas/tests,
domain-author for contracts, journey-runner for emulator/APK journeys, runner for verification, reviewer for read-only review,
and integrator for git/GitHub delivery. The orchestrator coordinates bounded work rather than writing implementation.

## Current vault and skills workflow

For material work, use grill-with-docs → to-spec → to-tickets → implement as the task requires.
The docs vault owns glossary, specs, and durable project knowledge. GitHub owns tickets, overriding generic skill storage defaults.
Trivial/read-only work proceeds directly. Root/local instructions route context by subject instead of bulk-loading every document.
Akashic 0.2.0 reads nodes independently of status selection. Canonical seeds identify current guidance, with history clearly marked.
Use actual project IDs for read-only retrieval:

```bash
akashic query docs --seed architecture-readerparser --mode current
akashic query docs --seed concept-persistence --mode current
akashic query docs --seed specs-reader --mode current
akashic inspect docs --json
akashic lint docs --json
```

Status alone does not suppress proposed/history nodes from query packets.
Historical queries are not a reliable temporal selector in installed 0.2.0. Read explicit known dates and historical headings instead.
Load relevant skills only when their activation condition applies. Prefer local skills when a genuine path collision exists.
android-cli is for Android docs/device/emulator/APK/journey tasks, not every Gradle check.
If graphify-out/graph.json exists, query graphify before broad raw navigation.
Use graphify query for questions, path for relationships, explain for concepts, wiki index for broad navigation,
and GRAPH_REPORT only for broad architecture review or inadequate scoped queries. Update the graph after changes when graph maintenance is in scope.
