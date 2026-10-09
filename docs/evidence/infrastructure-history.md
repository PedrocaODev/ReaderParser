---
id: evidence-infrastructure-history
title: "Infrastructure and workflow history"
type: Evidence
status: stable
valid_from: 2026-10-08
scope:
  system: readerparser
relations:
  - type: relates_to
    target: ../concepts/download-lifecycle.md
  - type: relates_to
    target: ../concepts/samsung-search.md
  - type: relates_to
    target: ../concepts/agent-lanes.md
  - type: relates_to
    target: ../concepts/repository-workflow.md
---

# Infrastructure and workflow history

This is synthesized historical evidence. `stable` describes this evidence record, not acceptance of the original decisions.
Migration occurred on 2026-10-08. Historical checked tasks are completion claims. They do not prove behavior, test execution, or acceptance today.
The [coverage ledger](../migrations/coverage-infrastructure.md) records source provenance hashes and clause-level destinations.

## Lifecycle and artifact metadata

All seven assigned changes used `.openspec.yaml` schema `spec-driven`. The YAML contained no other configuration beyond schema and creation date.
Archive-directory dates are preserved separately from creation dates. An archive directory is not evidence of decision acceptance.

| Historical change | YAML creation date | Archive-directory date | Recorded lifecycle |
| --- | --- | --- | --- |
| `adopt-openspec-repository-governance` | 2026-06-05 | 2026-06-05 | Archived, all tasks checked. |
| `add-sequential-offline-downloads` | 2026-06-08 | 2026-06-08 | Archived, all tasks checked. |
| `harden-opencode-agent-lanes` | 2026-06-09 | 2026-06-09 | Archived, all tasks checked. |
| `show-download-progress` | 2026-06-09 | 2026-06-09 | Archived, including an explicitly removed page-counter task. |
| `samsung-search-public-api` | 2026-06-10 | 2026-06-10 | Archived, all tasks checked, with tests moved or tightened. |
| `fix-samsung-search-registration` | 2026-06-11 | 2026-07-06 | Archived with instrumentation and device verification unchecked. |
| `use-samsung-library-search` | 2026-07-07 | 2026-07-08 | Archived, all tasks checked. |

No raw OpenSpec artifacts or YAML payloads are retained in the vault. Paths and SHA256 values identify provenance rather than a surviving archive.

## Historical capability ownership

The download change introduced `download-enqueue`, `download-offline-reader`, and `chapter-refresh-state-preservation`.
The progress change added `download-progress-reporting` and amended `download-enqueue` progress visibility.
Samsung publication introduced `samsung-search-indexable-series`. Registration repair tightened that same capability's provider contract.
The Library-search proposal named a new `use-samsung-library-search` capability while its delta directory used `library-search-via-samsung-search`.
It kept the existing downloaded-only index scope. These names are historical identifiers, not new active routes.
Governance introduced `repository-governance` as the first permanent main spec, with no existing main specs listed in its proposal.
Lane hardening introduced `agent-lane-hardening` and modified governance routing for integrator ownership.

## Governance ownership and replacement rationale

In June 2026, two change workflows competed: `/start → plans/ → /run-plan`, with `memory-bank/` scratchpads, and OpenSpec proposal/design/specs/tasks.
The stated problem was uncertainty about authoritative truth, transient context, and task status.
The chosen historical policy made OpenSpec the sole structured path for non-trivial changes and kept direct handling for trivial or read-only work.
It aimed for traceability without duplicate plan stores. Specialist execution lanes remained valid inside the workflow.

Historical durable ownership was `README.md`, `architecture.md`, `codemap.md`, `AGENTS.md`, and `openspec/specs/`.
The old design classified multi-file changes, new sources/screens/repositories, Room migrations, architecture changes, and useful proposal/design/spec/task trails as non-trivial.
Its direct exceptions included read-only questions, single-file cosmetic edits, answers without code edits, and one known command.
It barred repo-global session scratchpads, active-context files, and progress trackers outside change artifacts.
The records described canonical `openspec/specs/` as archived specs in one passage, but the durable role was the main requirement store.

The approved 2026-10-08 migration replaces that OpenSpec storage and workflow with the docs vault, GitHub ticket status, and `grill-with-docs`, `to-spec`, `to-tickets`, and `implement`.
The durable historical lesson is explicit ownership and a single active workflow. OpenSpec command paths below explain history, not current instructions.

## Retiring duplicate stores and preserving unique knowledge

The historical policy deleted `plans/` rather than moving it to an archive. An archive was rejected because it could remain a second discoverable plan store.
The claim was that plans were stale, duplicated OpenSpec work, or no longer actionable. The design still required a pre-deletion audit for unique reasoning.
The design called for deleting all 12 `memory-bank/` files after incorporating useful durable content.

The audit distinguished these categories:

| Category | Historical disposition and preservation target |
| --- | --- |
| `activeContext.md`, `progress.md` | Transient task state, to discard after structured change tracking replaced it. |
| `projectbrief.md`, `productContext.md` | Unique durable product facts to README or architecture. |
| `systemPatterns.md`, `techContext.md` | Unique architecture patterns and technology facts to architecture. |
| `commit-conventions.md`, `conventions.md`, `test-strategy.md`, `directory-map.md`, `decision-log.md` | Unique rules to AGENTS or architecture where not already covered. |
| `memory-bank/README.md` | Redundant with root README, to discard. |

The later checked audit called `decision-log.md` transient/historical with no migration needed, whereas the design singled it out as a candidate for unique reasoning.
That distinction remains a historical audit claim. This migration does not establish that every deleted 2026 file was substantively covered.
The audit also called conventions, test strategy, directory map, and inner README stubs or retired with no unique content.
It claimed commit prefixes were merged into AGENTS, test-utility paths into architecture, and screen/source patterns and product facts were reviewed for missing content.

## Historical routing changes and continuity risks

The old policy removed `.opencode/command/start.md`, `light-start.md`, and `run-plan.md` because they referenced retired stores.
`run-plan.md` was later recorded already absent. `add-migration.md`, `new-screen.md`, `new-source.md`, and `verify.md` were retained as bounded specialist commands.
OpenSpec propose/apply skills replaced kickoff and execution. Restructuring working `.opencode/skills/` was outside the governance change.

AGENTS was to remove its Memory Bank section and old context references, add workflow routing, and preserve non-negotiables, specialists, placement, and testing rules.
The concise retrieval rule started with the directly referenced file, then nearest AGENTS, then architecture or codemap only when needed.
README was to list codemap and main specs, update agent workflow, and remove non-canonical `project-structure.md` or `kickoff-prompt.md` references.
`.opencode/opencode.json` was to remove the `instructions` array loading `memory-bank/activeContext.md` and `progress.md` at startup.
Replacing that injection with another file was rejected because AGENTS already loaded and change artifacts were read on demand.

Risks included losing interruption continuity, losing historical decisions, dangling routes during migration, and changed session-start behavior.
The proposed mitigations were explicit task checkboxes/design context, an audit and durable merge, atomic delivery in one commit, and on-demand reading instead of scratchpad injection.
No Kotlin, Gradle, CI, dependency, or CLI/schema changes were intended. Agents relying on old commands could break until routing was updated.

## Governance completion claims

The June governance checklist claimed completion of the file audit, durable merges, canonical governance spec creation, and spec/design consistency checks.
It claimed AGENTS, README, architecture, and OpenCode startup instructions were updated, with unrelated repo rules retained.
It claimed both duplicate directories and obsolete command files were gone.
It claimed searches removed stale `memory-bank/`, `plans/`, kickoff-command, and standalone context-file references, including `.opencode/skills/` pointers.
Follow-up notes specifically claimed stale entries were removed from `.understand-anything/fingerprints.json` and `.understand-anything/knowledge-graph.json`, and `plans/` was removed from `.gitignore`.

Final checked claims included no dangling AGENTS references, clean OpenCode deleted-path references, an accurate README routing table, the governance spec as sole policy source, and absent retired stores and commands.
`./gradlew :app:assembleDebug` was checked for this doc-only change. There is no independent output retained here proving that command result.
These are historical assertions, not permission to remove any migration source without current independent coverage verification.

## Download verification claims

The June 8 checklist marked every item complete. It claimed the domain enqueue interface, set-based `cancelBatch`, DAO enqueueing, tag cancellation, and Hilt wiring existed.
It claimed local `DownloadStore` injection and cache-first repository loading, plus refresh merging for retained state, new default rows, and stale row removal.
It claimed series unread download, range picker, confirmation snackbar, both historical reader actions/state wiring, downloaded chapter indicators, and Downloads deletion wiring.

The claimed unit test subjects were `DownloadEnqueuerImpl` enqueue correctness, deduplication and order, `DownloadRepositoryImpl.cancelBatch`, cache-first loading with no source call, and refresh state preservation.
The refresh test tasks separately covered retained state, new default state, and removal of chapters absent remotely.
It checked `:app:assembleDebug` and `:app:testDebugUnitTest` as clean/all passing.
The duplicate offline-loading and refresh test tasks represent repeated coverage claims, not extra independent evidence.
No current test run occurred in this migration. The serialization, signature, batch identity, and framework discrepancies remain in the download concept.

The June 9 progress checklist claimed total-page tracking through `onPageDownloaded`, write-loop callbacks, novel 0.5 and 1.0 milestones, and percentage `StateBadge` rendering.
It explicitly removed the page-count UI item because the float percentage met the goal without schema/domain changes. Preview data already had 0.45.
Per-page behavior was attributed to new callback tests. Novel milestones were attributed to worker inspection plus a passing full unit suite.
Reactive propagation was supported by unchanged infrastructure and `FakeDownloadRepository.updateQueueState` call recording.
Enqueue, cancel, retry, and delete regression coverage was attributed to the full suite being green.
Those claims do not establish a device journey or an end-to-end reactive UI test.

## Agent lane completion claims

The June 9 lane checklist marked all tasks complete. It claimed the integrator definition, metadata, seven-step delivery body, and hard rules were installed.
It claimed global git ask defaults, allowed integrator dispatch, and build/runner/reviewer/integrator Bash overrides matched the design.
It claimed integrator was added after `journey-runner` in AGENTS.

It claimed apply-command runner/reviewer steps, correction limits, failure/blocker handling, and mirrored apply-skill steps were present.
It claimed archive-command and archive-skill integration steps, prefixed grouping, branch naming, CI watching, confirmed merge, and output fields were present.
It claimed a repository-governance routing delta was created. The design separately proposed an `agent-lane-hardening` delta synced to canonical specs.
This checklist records file/configuration changes. It contains no successful integration journey or permission-enforcement test result.

## Samsung verification claims

The June 10 checklist marked all tasks complete. It claimed the schema asset/reader, client operations and 100-document batching, permissions, DAO Flow/one-shot queries, mapper, syncer, startup, links, receiver, and Hilt wiring.
These implementation claims preserve the scope in the Samsung concept. The later registration repair documents defects in that supposedly completed integration.

Client tests introduced `SearchProviderDelegate` and `FakeSearchProviderDelegate` as a seam.
They moved to `androidTest` because `Bundle`, `Uri`, and `ContentValues` are Android framework classes unavailable to plain local JVM tests.
The claimed cases were register success/failure/null, availability probing, batch splitting, delete-all, and exception resilience.
Syncer tests also moved to instrumentation because the fakes used Android objects.
Their claimed cases were clear/insert rebuilding, empty lists, unavailable-client skip, observation-triggered rebuild, cancellation of previous `startObserving` activity, and mapping.

DAO instrumentation cases included downloaded inclusion, undownloaded exclusion, downloaded inclusion with `inLibrary = false`, DISTINCT deduplication, title order, and Flow/one-shot parity.
Receiver JVM tests checked action gating. Instrumentation with `WorkManagerTestInitHelper` checked actual scheduling by tag, null-safety, wrong-action rejection, and exactly one enqueue.
`:app:testDebugUnitTest` and `:app:assembleDebug` were checked. The source did not state that all added instrumentation tests ran.

The registration repair checked the probe method/constant, extras name and bytes, null registration arg, and corrected XML attributes/types.
Its updated availability cases expected success only with a plausible version and failure for null Bundle, missing key, and exceptions.
Register tests asserted name, schema bytes, and `lastCallArg == null`. The asset test checked `<schema>` and `keyFieldName`.
Only `:app:assembleDebug` was checked under verification.
The `SamsungSearchClientTest.kt` instrumentation run remained unchecked, as did installation/launch/logcat verification on a Samsung-enabled device.
The proposed log check searched `App|SamsungSearchClient|SearchProvider|PublicSchemaManager|register_schema|search integration disabled` and expected the disabled message to disappear.
Schema existence at `/data/user/0/com.samsung.android.smartsuggestions/files/aisearch/client/com.opus.readerparser.series/schema.xml` remained unchecked.
Index existence at `/data/user/0/com.samsung.android.smartsuggestions/files/aisearch/indexes/com.opus.readerparser.series/` also remained unchecked.
No device activation success is established by these records.

The July Library-search checklist marked all tasks complete. It claimed a change spec, pure domain result, repository contract, delegate query additions, and client hit/failure wrapper.
Its client cases covered successful query, empty cursor, null cursor, and exception.
It claimed eligible DAO lookup, provider-order repository mapping, unchanged browse behavior, non-blank ViewModel search, reactive sorted blank lists, failure display, and active-search revalidation.
It claimed fake-repository and repository/ViewModel test updates, targeted unit checks, and a debug build.
Current source observation of batched eligibility and local provider-failure fallback conflicts with the historical design, as recorded in the Samsung concept.

## Decision assessment and uncertainty

Proposed ADRs preserve specific costly trade-offs without declaring acceptance: historical ownership, chapter jobs, offline-first content, retained chapter state, enforced lanes, Room progress, optional series metadata, Room eligibility, small-index rebuild, and provider ranking with local display.
The registration repair has no qualifying architectural ADR because it repairs a bounded provider contract rather than choosing a new costly architectural alternative.
Archive naming and checked tasks do not establish acceptance. Runtime uncertainty remains distinct from fully incorporating the source's knowledge.
