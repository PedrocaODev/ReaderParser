---
id: evidence-navigation-and-tooling-history
title: "Navigation and tooling reconciliation"
type: Evidence
status: active
valid_from: 2026-10-08
scope:
  system: readerparser
relations:
  - type: relates_to
    target: ../architecture.md
---

# Navigation and tooling reconciliation

## Provenance and structural context

Migration date is 2026-10-08. Source snapshot HEAD is 49ed7cbf277adba16b2ccb3d8589252f8516fe5f.
The original 38 codemaps mix current navigation with stale/generated claims. They are not preserved as a copied atlas.
Responsibilities, contracts, algorithms, persistence shape, entrypoints, and consequential differences are incorporated by concept.
Pure namespace maps have no independent algorithm. Their useful source-root/package facts are consolidated into Runtime wiring.
Root architecture's separation between normative rules and descriptive locations remains useful, with canonical owners now in the vault.
The old README's beyond-single-file architecture-first and codemap navigation guidance becomes scoped vault routing.

## Current corrections and unresolved discrepancies

The real main Kotlin source root is java, not blueprint/README kotlin. JVM tests remain under test/kotlin, instrumentation under androidTest/java.
The current build minimum is SDK26, while old README says SDK36. Target/compile SDK36 and Java17 remain current.
The root-package/Series blueprint describes separate NovelReader/MangaReader screens, while current Reader/nav use one destination and contentType.
The old UI maps describe seven screens and a fixed two-column grid, but enumerate six current screens and shared adaptive cards.
Unread filtering was a TODO in maps and removed in the consolidated capability requirements.
The old chapter repository map claims replacement keeps existing state and stale chapters. Current code merges state then deletes/upserts remote rows.
The source map miscounts six suspend retrieval methods as seven.
Asura's old static-Astro-HTML-only/no-API claim predates the current chapter JSON API with HTML fallback.
The old filesystem map calls readers/workers future consumers. Both currently use DownloadStore.
Its metadata-last atomicity applies to manhwa, while current novel metadata precedes HTML.
The old preferences map claims enum corruption defaults. Current valueOf can throw on invalid names.
The old theme map describes activity observing saved theme. Current MainActivity uses ReaderParserTheme's default SYSTEM value.
The old worker map claims per-series refresh isolation, automatic App scheduling, and six hours as the platform minimum.
Current LibraryUpdateWorker aborts on first failure, its factory uses the chosen six-hour interval, and App does not schedule it.
The worker map and current ChapterDownloadWorker perform direct Ktor page-byte calls, conflicting with normative Source/repository-only HTTP placement.
Historical data/network and core/result placeholders and gradle-daemon-jvm.properties JDK21 references do not exist in the current tree.
The old Gradle map claimed `updateDaemonJvm` generated OS/architecture-specific JDK21 download URLs from the foojay.io disco API.
Its stated intent was consistent daemon JVM selection across hosts to prevent default-JVM toolchain mismatches.
That file's generation, plugin-classpath presence, implicit configuration-time loading, and JDK21 execution claims remain unverified historical descriptions.
They do not establish a current JDK21 requirement or current generation behavior.
Model composable/import-enforcement claims and sub-millisecond preference latency are unverified descriptions, not new contracts or measured guarantees.
Namespace maps also claimed compiler enforcement of package-directory matching, without independent proof of that constraint.
No app defect is fixed by this migration. These discrepancies remain reviewable implementation limits.

## Original blueprint and bootstrap intent

The old .opencode/project-structure.md was explicitly reference-only and could lag behind code.
Its illustrative tree combined Android app/schemas/source/test/resources, Gradle wrapper/catalog, GitHub CI, and local OpenCode agents/commands/skills.
It used example site plugins, separate reader packages, interfaces under data/repository, optional usecases,
HttpClientFactory/PersistentCookieJar/Json, Paths/Keys, and shared Failure/Dates/Extensions as proposed placeholders.
These example file names are not current implementation claims.
Blueprint bootstrap used Android Studio Empty Activity Compose, chose a package, created directories, installed root guidance/config,
then local agents/commands, tested a TestSite scaffold, and added nested rules as code areas appeared.
Nested rules were intended to remain short (about 50 lines), with narrower scopes overriding overlapping root guidance.
Local instructions still retain distinct boundary rules, while bulk autoload is retired.
The blueprint proposed auto-loading root architecture plus every kotlin/**/AGENTS through OpenCode instructions.
Current scoped vault routing supersedes that loading approach by explicit user approval on 2026-10-08.
Its model field was intentionally omitted to inherit a global model, with project override only when needed.
This is historical setup intent, not an instruction to change global model/settings.
Its permission example allowed edits/webfetch/Gradle/read-only git/add/restore, asked commit/push/adb/default commands,
denied hard reset/rm-rf, and used notify autoupdate. These values do not override current tool permissions.
The blueprint intended .opencode, AGENTS, and config to be committed, alongside normal Android ignores:
*.iml, .gradle, local.properties, .idea, build, captures, .externalNativeBuild, .cxx, and .kotlin.

## Reusable project tooling from the blueprint

The /new-source template dispatched source-author and accepted name/baseURL/contentType/language (default en).
It scaffolded plugin/registration/fixtures, computed IDs, and required actual URL/selectors instead of inventing extraction.
The /new-screen template dispatched screen-author, defined four files/state/actions/effects, added navigation entries,
and asked screen purpose before state fields while leaving business TODOs.
The /add-migration template dispatched room-migration, checked database/schema version, bumped once, wrote explicit SQL Migration,
registered it, updated entities/DAOs/mappers, added MigrationTestHelper instrumentation, and rebuilt exports.
It avoided auto-migration/destructive fallbacks and required explicit identity-change approval.
The /verify template ran assemble → lint → JVM tests → configured ktlint/detekt, stopped on failure with file/line context,
and prohibited suppression/disabled rules. Its old pause-before-fix instruction is historical command behavior, not a new blocker for authorized fixes.
Reviewer was read-only, inspecting layering/identity/state/errors/tests/style in order with severity and file/line evidence.
Its historical error check asked whether exceptions were caught only at the ViewModel boundary.
Its output rule required describing findings without proposing code, leaving the decision to the user.
These are original reviewer instructions, not a replacement for current authorized correction policy or explicit existing recovery contracts.
Source/screen/migration specialist constraints remain in their canonical concept nodes and scoped agent routing.

## Retired propose procedures

openspec-propose and opsx-propose accepted a kebab-case name or an informal description.
Without clear input they asked what to build, derived a name, and checked existing-name ambiguity.
They created a change scaffold/.openspec.yaml, read status schema/applyRequires/artifact dependency order,
then fetched instructions for ready artifacts and read completed dependencies.
Instruction JSON supplied context/rules/template/instruction/outputPath/dependencies.
Context/rules constrained authors but were not copied into output.
They tracked progress, wrote dependency-ready artifacts, checked existence, and repeated status until applyRequires was done.
Clarification was required for critical missing input. Completion listed change/artifacts and suggested opsx-apply.
Those artifact/status/CLI mechanics are retired. Material understanding, dependency context, and checkable completion remain useful principles.

## Retired apply procedures

openspec-apply-change and opsx-apply selected a provided/conversation change, auto-selected a sole active change, or asked when ambiguous.
They announced selection and override, read status for schema/tasks, and obtained dynamic apply instructions.
Missing artifacts produced blocked output. all_done suggested archive. Other states read every contextFiles path supplied by schema.
They reported total/complete/remaining tasks and instructions, implemented narrowly, and checked task boxes immediately.
Ambiguity, design problems, errors, blockers, or interruption paused work with remaining-issue options and current progress.
Artifacts could evolve and apply could resume/interleave before every artifact finished if tasks existed.
After implementation they dispatched runner then reviewer, corrected failures/BLOCKER/SHOULD-FIX findings, and allowed at most three cycles.
They declared ready-to-archive only after both passed. Progress templates/status banners/examples add no further project requirement.
The reusable verification and review boundaries are in [Agent lanes](../specs/agent-lanes.md).

## Retired archive procedures

openspec-archive-change and opsx-archive required user selection when no clear change was supplied, listing active changes and schema.
They checked artifact graph status and task checkboxes. Incomplete artifacts/tasks prompted confirmation rather than silently blocking forever.
Missing task files needed no task warning. Delta assessment compared main specs for adds/modifications/removals/renames before choosing sync/skip/archive/cancel.
Both the skill and command's numbered sync step dispatched a general-purpose subagent to invoke `openspec-sync-specs` through the Skill tool.
The command's guardrail instead required direct Skill-tool invocation of `openspec-sync-specs`.
This dispatch inconsistency belongs to the retired command. It is preserved without selecting either procedure as current guidance.
Both procedures continued according to the selected sync policy.
They checked date-named archive targets and failed on collisions, offering rename/remove-duplicate/different-date alternatives.
They moved the whole change including .openspec.yaml and reported sync/completion/warnings.
Project-specific archive invoked integrator for grouped-prefix commits, change/<name> branch, push/PR/CI, and confirmed merge commits.
These artifact movement/select/sync/confirmation/output procedures are retired, not a new archive tree to maintain.
Delivery authorization and runner/reviewer/integrator knowledge remain meaningful history.

## Retired exploration procedures

openspec-explore and opsx-explore were read-only thinking modes for vague ideas, problems, current changes, comparisons, or no argument.
They allowed code reading/search/investigation and explicitly requested OpenSpec artifact capture, but no application implementation.
Curiosity, multiple threads, grounded investigation, optional diagrams/comparisons, and patient clarification shaped the mode.
They inspected active changes and read referenced artifacts, offered to capture requirements/design/scope/tasks, and waited for user choice.
No fixed steps, sequence, output, conclusion, brevity, or auto-capture was required.
Implementation requests instructed exiting explore and proposing a change. That restriction belongs to the retired mode, not the new approved work scope.
Illustrative auth/collaboration/database diagrams/examples and generated tool metadata contain no durable ReaderParser fact.

## Codemap generation cache

.slim/codemap.json records generator metadata version1.0.0 and last run 2026-05-27T04:42:01.899Z.
Its historical root was `/home/pedro/.local/share/opencode/worktrees/ReaderParser/redesigning-reader-content`.
Generation included main Kotlin, manifest, app/root/settings Gradle, gradle.properties/catalog, avd-config, and scripts.
It excluded build output, JVM/instrumented tests, Room schemas, .opencode, journeys, Markdown, and LICENSE, with no exceptions.
File/folder hashes are old incremental-generation cache state. They are not source requirements or durable runtime identity.
Their explicit disposition is no-durable-substance. Retain scope/exclusions/date instead of the cache payload.

## Generated exploration snapshot

The retired `.understand-anything` exploration snapshot uses format version `1.0.0` and commit `b7852189a3bff5e1165f195ac4fd5a565e1c9794`.
Its graph `analyzedAt` is `2026-05-29T04:51:27.469507+00:00`.
Fingerprint `generatedAt` is `2026-05-29T04:52:32.834Z`, and metadata `lastAnalyzedAt` is `2026-05-29T04:52:54.922608+00:00`.
These are original analysis dates, distinct from the migration date. They do not establish present implementation behavior.

The graph contains 466 nodes representing 269 distinct file paths, 724 edges, ten groups, and a ten-stop tour.
Edges comprise 353 imports, 197 contains, 143 documents, 22 configures, and nine triggers.
Metadata reports 288 analyzed files, while fingerprints contain 258 file entries. The reason for this count discrepancy is unknown.
All graph edge endpoints resolve within the historical graph. However, 43 nodes reference 18 paths absent from the current tree.
Those paths include separate-reader production/test/map files and the old singular `.opencode/command` directory.
The snapshot contains 39 codemap nodes, compared with 38 inventoried current codemaps. These differences do not justify restoring obsolete paths.

The groups describe documentation/workflow, build/tooling/CI, tests/QA, Android resources, bootstrap/DI, domain/utilities, data/persistence,
source plugins, presentation, and background work. Those responsibilities have canonical architecture, concept, and development destinations.
The tour repeats those responsibilities in reading order. Its separate novel/manhwa reader stop and project description are stale.
Current guidance uses one Reader with text and page renderers. The source-count suggestion to narrow analysis is generic navigation advice.
Language/framework labels are derived inventories, not additional dependency or architecture requirements.

File/class/function summaries, tags, complexity labels, line locations, imports, containment, and hash arrays are derived navigation/cache data.
They add no independent design contract beyond the mapped responsibilities. Their disposition is no-durable-substance after that reconciliation.
Fingerprint function/class/import/export arrays are otherwise empty. The sole nonempty array identifies `scripts/setup-wsl.sh` function `print_status`.
That function location is generated structure, not a separate setup requirement or execution claim.
Document/configuration/trigger edges are heuristic descriptions. For example, codemap links to App/MainActivity/NavGraph do not establish runtime dependencies.
Exported schema configuration edges to the database likewise do not establish runtime execution or successful migration tests.

The snapshot describes six historical automation roles: `alert-to-issue` converts alerts into issues, and `ci` performs builds and checks.
`detekt` performs static analysis, and `journey` performs emulator checks.
`opencode` supports tooling/repository operations, and `release` publishes artifacts.
These summaries preserve automation intent. They do not prove a workflow ran, passed, created an issue, or published a release.

The ignore file's only active project pattern is `.understand-anything/`, which prevents the generated output from analyzing itself.
Other patterns are commented suggestions using gitignore syntax, including globs, comments, `!` negation, and trailing slashes for directories.
The file lists built-in defaults such as `node_modules/`, `.git/`, `dist/`, `build/`, `obj/`, lock files, and minified JavaScript.
Those listed defaults are tool descriptions, not active project-specific exclusion patterns. No custom inclusion exception is active.
The [generated snapshot coverage](../migrations/coverage-generated-graph.md) records exact source hashes and canonical destinations.
