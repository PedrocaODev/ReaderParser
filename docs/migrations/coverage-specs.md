---
id: evidence-coverage-specs
title: "Specification source coverage"
type: Evidence
status: active
valid_from: 2026-10-08
scope:
  system: readerparser
relations:
  - type: relates_to
    target: ../concepts/repository-workflow.md
---

# Specification source coverage

Migration date: 2026-10-08. Historical archive dates remain source context, not acceptance dates.

All 43 artifacts below passed independent semantic verification. Source paths and SHA256 values are provenance identifiers, not future links.

Independent runner returned VERDICT: PASSED on 2026-10-08 against source snapshot HEAD `49ed7cbf277adba16b2ccb3d8589252f8516fe5f`.
The runner compared all 43 original hashes, 141 requirement occurrences, and 378 scenario occurrences with the exact mapped destinations.
It certified every owned artifact's substance incorporated, including historical conflicts and proposed implementation gaps.
The authorized retirement phase removed all 43 original sources after independent coverage passed and pre-mutation hashes matched.
Each row maps the requirement plus all listed scenario conditions/outcomes. Final delivered-state validation belongs to the independent runner.

Equivalent clauses share canonical destinations. Historical conflicts map to explicit historical sections. Proposed gaps retain intended semantics.

Whole-file formatting, empty ADDED/MODIFIED/REMOVED headings, `None.`, and archive-generated TBD purpose placeholders have no durable substance.

Their disposition is no-durable-substance because they add no project requirement beyond the mapped clauses.

## openspec/specs/adaptive-series-catalog-ui/spec.md

SHA256: `f29f8a733ddf6e5522921a0bd80d3fb2389ec62bed1485b4a7ad595312e0cc88`

Coverage status: `incorporated`. Verification: `independently-verified` (2026-10-08, runner).

| Source requirement and complete scenario set | Destination | Disposition |
| --- | --- | --- |
| Library and Browse use a shared cover-first series card — scenarios: Library renders catalog cards, Browse renders catalog cards | [specs/catalog-presentation.md#shared-cards-and-adaptive-layout](../specs/catalog-presentation.md#shared-cards-and-adaptive-layout) | deduplicated |
| Catalog layout adapts to available width — scenarios: Compact width remains usable, Expanded width uses available space | [specs/catalog-presentation.md#shared-cards-and-adaptive-layout](../specs/catalog-presentation.md#shared-cards-and-adaptive-layout) | deduplicated |

## openspec/specs/agent-lane-hardening/spec.md

SHA256: `de8a5d5390b10c70aa291fdcfec527cbe3069877cdc5a4f050179f36f3efa2fc`

Coverage status: `incorporated`. Verification: `independently-verified` (2026-10-08, runner).

| Source requirement and complete scenario set | Destination | Disposition |
| --- | --- | --- |
| Build agent SHALL NOT run git commands or verification tasks — scenarios: Build agent attempts git commit, Build agent runs verification | [specs/agent-lanes.md#lane-ownership](../specs/agent-lanes.md#lane-ownership) | deduplicated |
| Integrator lane owns git staging, commit, branch, push, PR, CI, and merge — scenarios: Integrator groups commits by prefix, Integrator creates branch and PR, Integrator watches CI and merges on green, CI checks fail | [specs/agent-lanes.md#lane-ownership](../specs/agent-lanes.md#lane-ownership) | deduplicated |
| Apply flow includes runner and reviewer correction cycle — scenarios: Runner reports failures after implementation, Reviewer reports blockers after implementation, Correction cycle exceeds maximum iterations, Both runner and reviewer pass | [specs/agent-lanes.md#historical-verification-correction-cycle](../specs/agent-lanes.md#historical-verification-correction-cycle) | historical |
| Archive flow includes integrator phase — scenarios: Archive triggers integrator, Integrator creates feature branch, Integrator creates PR and watches CI, CI passes and integrator merges, User confirmation before merge | [specs/agent-lanes.md#historical-archive-delivery](../specs/agent-lanes.md#historical-archive-delivery) | historical |
| Permission model reflects lane boundaries — scenarios: Build agent denied verification and git writes, Runner allowed verification and read-only git, Reviewer allowed read-only git, denied gradle, Integrator allowed git/gh workflow commands, Non-integrator agent attempts git commit, Integrator runs git commit | [specs/agent-lanes.md#historical-opencode-permission-contract](../specs/agent-lanes.md#historical-opencode-permission-contract) | historical |
| AGENTS.md documents integrator lane — scenarios: Integrator appears in specialists list | [specs/agent-lanes.md#lane-ownership](../specs/agent-lanes.md#lane-ownership) | deduplicated |

## openspec/specs/chapter-refresh-state-preservation/spec.md

SHA256: `115c910aa5a85636aaa8fb5495d75e35bd6cfe5410e869a90e4400cd128804d3`

Coverage status: `incorporated`. Verification: `independently-verified` (2026-10-08, runner).

| Source requirement and complete scenario set | Destination | Disposition |
| --- | --- | --- |
| refreshChapters preserves chapter state — scenarios: Refresh preserves read state, Refresh preserves progress, Refresh preserves downloaded flag, New chapters get default state, Removed chapters are cleaned up | [specs/downloads.md#refresh-preserves-chapter-state](../specs/downloads.md#refresh-preserves-chapter-state) | deduplicated |

## openspec/specs/download-enqueue/spec.md

SHA256: `d4c4353e02714ab6fb90c41c6e3107d065b65d40273a5038b83407d95f11ca0d`

Coverage status: `incorporated`. Verification: `independently-verified` (2026-10-08, runner).

| Source requirement and complete scenario set | Destination | Disposition |
| --- | --- | --- |
| Enqueue single chapter download — scenarios: Enqueue novel from unified Reader, Enqueue manhwa from unified Reader, Enqueue from series detail, Duplicate enqueue is ignored | [specs/downloads.md#single-and-batch-enqueue](../specs/downloads.md#single-and-batch-enqueue) | deduplicated |
| Enqueue batch download for a series — scenarios: Enqueue unread-only batch, Enqueue range batch, Empty batch | [specs/downloads.md#single-and-batch-enqueue](../specs/downloads.md#single-and-batch-enqueue) | deduplicated |
| Sequential execution — scenarios: Sequential processing | [specs/downloads.md#sequential-work-and-batch-cancellation](../specs/downloads.md#sequential-work-and-batch-cancellation) | deduplicated |
| Cancel batch download — scenarios: Cancel active batch, Cancel batch with only queued items | [specs/downloads.md#sequential-work-and-batch-cancellation](../specs/downloads.md#sequential-work-and-batch-cancellation) | deduplicated |
| Download progress visibility — scenarios: Observe queue updates, Progress percentage display | [specs/downloads.md#queue-display-and-progress](../specs/downloads.md#queue-display-and-progress) | deduplicated |

## openspec/specs/download-offline-reader/spec.md

SHA256: `cdbe7bb5b0c02d224fd861453f0a78671a3f32f4ac585f45a9fd8af9931c76b2`

Coverage status: `incorporated`. Verification: `independently-verified` (2026-10-08, runner).

| Source requirement and complete scenario set | Destination | Disposition |
| --- | --- | --- |
| Readers prefer local content — scenarios: Reader serves cached novel chapter, Reader serves cached manhwa chapter, Reader falls back to network when not downloaded, Downloaded content variant mismatches the route, Forced response remains mismatched | [specs/reader.md#renderers-and-payload-mismatch](../specs/reader.md#renderers-and-payload-mismatch) | deduplicated |
| Downloaded indicator on chapters — scenarios: Downloaded chapter shown in list | [specs/reader.md#chapter-actions-and-recovery](../specs/reader.md#chapter-actions-and-recovery) | deduplicated |
| Delete downloaded chapter — scenarios: Delete from Downloads screen | [specs/downloads.md#offline-reading-and-deletion](../specs/downloads.md#offline-reading-and-deletion) | deduplicated |

## openspec/specs/download-progress-reporting/spec.md

SHA256: `297304e8e979c9b6e32830f1a536d52f7114ae4a69f1327f11d6657cd8ce0dba`

Coverage status: `incorporated`. Verification: `independently-verified` (2026-10-08, runner).

| Source requirement and complete scenario set | Destination | Disposition |
| --- | --- | --- |
| Intermediate progress updates during download — scenarios: Manhwa page progress, Novel content progress | [specs/downloads.md#queue-display-and-progress](../specs/downloads.md#queue-display-and-progress) | deduplicated |
| Progress reflected in UI — scenarios: Progress percentage display | [specs/downloads.md#queue-display-and-progress](../specs/downloads.md#queue-display-and-progress) | deduplicated |
| Progress updates flow reactively — scenarios: Real-time progress update | [specs/downloads.md#queue-display-and-progress](../specs/downloads.md#queue-display-and-progress) | deduplicated |

## openspec/specs/library-blank-title-repair/spec.md

SHA256: `0dcb0e4a29ac27ad889dd1dc2ed39d085162862ee68d5eb1dc4bb188ab5e5165`

Coverage status: `incorporated`. Verification: `independently-verified` (2026-10-08, runner).

| Source requirement and complete scenario set | Destination | Disposition |
| --- | --- | --- |
| Library repairs blank bookmark titles through refresh — scenarios: Successful refresh repairs a blank title, Failed repair preserves the bookmark | [specs/catalog-presentation.md#blank-bookmark-repair](../specs/catalog-presentation.md#blank-bookmark-repair) | deduplicated |
| Library limits blank-title repair attempts per lifecycle — scenarios: Failed repair is not retried in the same lifecycle, A later lifecycle may retry | [specs/catalog-presentation.md#blank-bookmark-repair](../specs/catalog-presentation.md#blank-bookmark-repair) | deduplicated |

## openspec/specs/library-browse-catalog/spec.md

SHA256: `3071e24e63b5c5da05701cde413a418fa5fc43bb7ea1dd2e5ff70bd18ebdb740`

Coverage status: `incorporated`. Verification: `independently-verified` (2026-10-08, runner).

| Source requirement and complete scenario set | Destination | Disposition |
| --- | --- | --- |
| Browse search requires explicit submission — scenarios: Mode change does not fetch a blank query, Submitted query starts search | [evidence/specification-evolution.md#browse-submission-conflict](../evidence/specification-evolution.md#browse-submission-conflict) | historical |
| Browse exposes search progress and recovery — scenarios: Search is loading, Search failure can be retried | [specs/browse-discovery.md#request-progress-and-recovery](../specs/browse-discovery.md#request-progress-and-recovery) <br> [Explicit-submit history](../evidence/specification-evolution.md#browse-submission-conflict) | deduplicated |
| Browse rejects stale search responses — scenarios: Earlier query finishes last, Stale page response is ignored | [specs/browse-discovery.md#cancellation-and-stale-responses](../specs/browse-discovery.md#cancellation-and-stale-responses) | deduplicated |
| Existing catalog behavior remains available — scenarios: Library search retains Samsung Search semantics, Browse preserves pagination | [specs/browse-discovery.md#catalog-and-pagination-continuity](../specs/browse-discovery.md#catalog-and-pagination-continuity) <br> [Historical provider-error continuity](../evidence/specification-evolution.md#browse-submission-conflict) | deduplicated |

## openspec/specs/library-filtering/spec.md

SHA256: `fd3bb281007020df8a03ea532a2d461ce64423fc1cde60ee8418d195a02387ea`

Coverage status: `incorporated`. Verification: `independently-verified` (2026-10-08, runner).

| Source requirement and complete scenario set | Destination | Disposition |
| --- | --- | --- |
| Library omits unsupported Unread filtering — scenarios: Library filtering controls are displayed | [specs/catalog-presentation.md#supported-library-controls](../specs/catalog-presentation.md#supported-library-controls) | deduplicated |

## openspec/specs/library-search-via-samsung-search/spec.md

SHA256: `cefc5ef785c8f6aef51b3279efcff7900da606c20fce205c1be0e70f9861d51e`

Coverage status: `incorporated`. Verification: `independently-verified` (2026-10-08, runner).

| Source requirement and complete scenario set | Destination | Disposition |
| --- | --- | --- |
| Active Library search uses Samsung Search query — scenarios: Non-blank query uses provider results, Blank query keeps local library view | [specs/library-search.md#samsung-first-query](../specs/library-search.md#samsung-first-query) | deduplicated |
| Library search resolves local rows and preserves provider ordering — scenarios: Local display data is used, Provider order is preserved | [specs/library-search.md#eligibility-and-ordered-local-resolution](../specs/library-search.md#eligibility-and-ordered-local-resolution) | deduplicated |
| Search results are limited to downloadable library rows — scenarios: Non-library hit is hidden, Non-indexable hit is hidden | [specs/library-search.md#eligibility-and-ordered-local-resolution](../specs/library-search.md#eligibility-and-ordered-local-resolution) | deduplicated |
| Active search revalidates on library and indexable changes — scenarios: Search hit disappears after indexability changes | [specs/library-search.md#eligibility-invalidation](../specs/library-search.md#eligibility-invalidation) | deduplicated |
| Provider failure is distinct from empty results — scenarios: Provider failure shows an error, No matches shows empty results | [evidence/specification-evolution.md#library-provider-error-conflict](../evidence/specification-evolution.md#library-provider-error-conflict) | historical |

## openspec/specs/repository-governance/spec.md

SHA256: `477affd4a88a37fad6c1b5f96ff2b91a22cd4fcc5dbc74b901c37bb8d96afad4`

Coverage status: `incorporated`. Verification: `independently-verified` (2026-10-08, runner).

| Source requirement and complete scenario set | Destination | Disposition |
| --- | --- | --- |
| Non-trivial work MUST start as an OpenSpec change — scenarios: Feature development requires an OpenSpec change, Implementation begins only after artifacts exist | [concepts/repository-workflow.md#historical-change-entry-and-exceptions](../concepts/repository-workflow.md#historical-change-entry-and-exceptions) | historical |
| Trivial and read-only work MAY proceed without an OpenSpec change — scenarios: Read-only codebase exploration, Typo fix in a comment, Work outside the trivial list requires an OpenSpec change | [concepts/repository-workflow.md#historical-change-entry-and-exceptions](../concepts/repository-workflow.md#historical-change-entry-and-exceptions) | historical |
| Canonical documentation files have defined ownership — scenarios: Architecture decision lives in architecture.md, Repository structure lives in codemap.md, Agent routing lives in AGENTS.md, Integrator owns git/PR/CI lifecycle, Build agent does not run git commands, Agent permission model enforces lane boundaries, Change artifacts live in openspec/changes/ | [concepts/repository-workflow.md#historical-documentation-ownership](../concepts/repository-workflow.md#historical-documentation-ownership) <br> [Lane additions](../specs/agent-lanes.md#lane-ownership) <br> [Permission scenarios](../specs/agent-lanes.md#historical-opencode-permission-contract) <br> [Artifact location](../concepts/repository-workflow.md#historical-transient-store-retirement) | historical |
| Repo-global transient context stores MUST NOT be used — scenarios: memory-bank directory must not exist, plans directory must not exist, Removal of legacy transient stores | [concepts/repository-workflow.md#historical-transient-store-retirement](../concepts/repository-workflow.md#historical-transient-store-retirement) | historical |
| OpenSpec changes have archive and completion expectations — scenarios: Durable outcomes sync to canonical docs before archive, Delta specs sync to main specs, Archive moves the complete change | [concepts/repository-workflow.md#historical-completion-and-archive](../concepts/repository-workflow.md#historical-completion-and-archive) | historical |

## openspec/specs/samsung-search-indexable-series/spec.md

SHA256: `939e16914c332d8992d6296ab38cf3e6600ad87021b31a0c0c8658202bbc8ddd`

Coverage status: `incorporated`. Verification: `independently-verified` (2026-10-08, runner).

| Source requirement and complete scenario set | Destination | Disposition |
| --- | --- | --- |
| Schema registration via public API — scenarios: Successful schema registration, Availability probe via request_search_api_version, Availability probe returns null, Availability probe throws, Samsung Search not installed, Idempotent re-registration, register_schema extras contain name key, register_schema passes null for arg, Schema XML uses schema root with fieldTypes | [specs/samsung-search-index.md#availability-and-schema-registration](../specs/samsung-search-index.md#availability-and-schema-registration) | deduplicated |
| Indexable series projection query — scenarios: Series with downloaded chapters is included, Series with no downloaded chapters is excluded, Series not in library but with downloaded chapters is included | [specs/samsung-search-index.md#indexable-projection](../specs/samsung-search-index.md#indexable-projection) | deduplicated |
| Series document schema fields — scenarios: Document contains all required fields, Chapter fields are absent | [specs/samsung-search-index.md#series-document-fields](../specs/samsung-search-index.md#series-document-fields) | deduplicated |
| Deep link to Series screen — scenarios: Tapping search result opens Series screen, Deep link does not open reader | [specs/samsung-search-index.md#deep-link-destination](../specs/samsung-search-index.md#deep-link-destination) | deduplicated |
| Index rebuild on chapter download state change — scenarios: New chapter downloaded triggers rebuild, Chapter download deleted triggers rebuild, Batch download does not thrash | [specs/samsung-search-index.md#rebuild-and-batching](../specs/samsung-search-index.md#rebuild-and-batching) | deduplicated |
| ACTION_UPDATE_INDEX broadcast handling — scenarios: Index corruption recovery, WorkManager ensures execution | [specs/samsung-search-index.md#broadcast-recovery-and-permissions](../specs/samsung-search-index.md#broadcast-recovery-and-permissions) | deduplicated |
| Manifest permission declarations — scenarios: Permissions declared | [specs/samsung-search-index.md#broadcast-recovery-and-permissions](../specs/samsung-search-index.md#broadcast-recovery-and-permissions) | deduplicated |
| Graceful degradation — scenarios: Search unavailable does not block app | [specs/samsung-search-index.md#graceful-degradation](../specs/samsung-search-index.md#graceful-degradation) | deduplicated |
| Bulk insert with batching — scenarios: Large indexable set is batched, Small indexable set is single batch | [specs/samsung-search-index.md#rebuild-and-batching](../specs/samsung-search-index.md#rebuild-and-batching) | deduplicated |

## openspec/specs/source-detail-parsing/spec.md

SHA256: `3d3f1c76bb1f47c8b7418066d6c0e68298ccf46ca3c9a6c1438362d90879536a`

Coverage status: `incorporated`. Verification: `independently-verified` (2026-10-08, runner).

| Source requirement and complete scenario set | Destination | Disposition |
| --- | --- | --- |
| Source detail parsing produces a usable title — scenarios: Extracted title is preferred, Nonblank incoming title is a fallback, Blank incoming title does not become a usable fallback | [specs/catalog-presentation.md#detail-title-selection](../specs/catalog-presentation.md#detail-title-selection) | deduplicated |

## openspec/specs/source-listing-pagination/spec.md

SHA256: `e412897d4d8842d0ca3ee6818a009e7e4a831b7318af1d1d9f99b84173f9be4e`

Coverage status: `incorporated`. Verification: `independently-verified` (2026-10-08, runner).

| Source requirement and complete scenario set | Destination | Disposition |
| --- | --- | --- |
| Browse results auto-load when scrolled to the end — scenarios: browse auto-loads next page on end-of-list | [specs/source-pagination.md#browse-end-of-list-loading](../specs/source-pagination.md#browse-end-of-list-loading) | deduplicated |
| FreeWebNovel chapters are fully aggregated across paginated chapter lists — scenarios: FreeWebNovel chapter list walks multiple pages | [specs/source-pagination.md#complete-chapter-aggregation](../specs/source-pagination.md#complete-chapter-aggregation) | deduplicated |
| Supported source listings request and parse the requested page — scenarios: FreeWebNovel latest uses page-specific requests, AsuraScans popular and search use page-specific requests, AsuraScans latest and popular remain distinct | [specs/source-pagination.md#page-specific-listings](../specs/source-pagination.md#page-specific-listings) | deduplicated |
| Terminal pages disable next-page navigation — scenarios: End-of-list pages return hasNextPage=false | [specs/source-pagination.md#single-page-and-terminal-modes](../specs/source-pagination.md#single-page-and-terminal-modes) | deduplicated |
| Single-page listings remain single-page — scenarios: FreeWebNovel popular remains single-page, FreeWebNovel search remains single-page, FreeWebNovel latest remains terminal at its final page, AsuraScans latest remains homepage-only for page > 1 | [specs/source-pagination.md#single-page-and-terminal-modes](../specs/source-pagination.md#single-page-and-terminal-modes) | deduplicated |

## openspec/specs/unified-reader-screen/spec.md

SHA256: `40b64efd303f6deed72471895d458cb3168c55276b0c687c463a6e1a736b3cd8`

Coverage status: `incorporated`. Verification: `independently-verified` (2026-10-08, runner).

| Source requirement and complete scenario set | Destination | Disposition |
| --- | --- | --- |
| Both series types use one Reader destination and screen — scenarios: Novel chapter opens Reader, Manhwa chapter opens Reader, ViewModel initializes route context | [specs/reader.md#shared-route-and-domain-variants](../specs/reader.md#shared-route-and-domain-variants) | deduplicated |
| Reader renders the existing chapter content shapes — scenarios: Text content loads, Page content loads, Route type and content disagree, Forced content still disagrees | [specs/reader.md#renderers-and-payload-mismatch](../specs/reader.md#renderers-and-payload-mismatch) | deduplicated |
| Reader provides shared immersive controls — scenarios: Reader content is tapped, Text progress is displayed, Page progress is displayed, Back is selected | [specs/reader.md#shared-immersive-controls](../specs/reader.md#shared-immersive-controls) | deduplicated |
| Reader preserves content-specific progress and read semantics — scenarios: Text scroll position changes, Stored text progress is loaded, Text content loads successfully, Final image page becomes current, Earlier image page becomes current, Page chapter loads again | [specs/reader.md#text-progress-and-read-state](../specs/reader.md#text-progress-and-read-state) <br> [Page semantics](../specs/reader.md#page-progress-and-read-state) | deduplicated |
| Reader supports chapter actions and recovery — scenarios: Adjacent chapter is selected, Chapter is selected from the sheet, Download is selected, Load fails and Retry is selected | [specs/reader.md#chapter-actions-and-recovery](../specs/reader.md#chapter-actions-and-recovery) | deduplicated |

## openspec/changes/archive/2026-06-05-adopt-openspec-repository-governance/specs/repository-governance/spec.md

SHA256: `40fa8f0c67e0257cf396a2c4673e9e2f45118a191df8cfc9369976dc8be4c03f`

Coverage status: `incorporated`. Verification: `independently-verified` (2026-10-08, runner).

| Source requirement and complete scenario set | Destination | Disposition |
| --- | --- | --- |
| Non-trivial work MUST start as an OpenSpec change — scenarios: Feature development requires an OpenSpec change, Implementation begins only after artifacts exist | [concepts/repository-workflow.md#historical-change-entry-and-exceptions](../concepts/repository-workflow.md#historical-change-entry-and-exceptions) | historical |
| Trivial and read-only work MAY proceed without an OpenSpec change — scenarios: Read-only codebase exploration, Typo fix in a comment, Work outside the trivial list requires an OpenSpec change | [concepts/repository-workflow.md#historical-change-entry-and-exceptions](../concepts/repository-workflow.md#historical-change-entry-and-exceptions) | historical |
| Canonical documentation files have defined ownership — scenarios: Architecture decision lives in architecture.md, Repository structure lives in codemap.md, Agent routing lives in AGENTS.md, Change artifacts live in openspec/changes/ | [concepts/repository-workflow.md#historical-documentation-ownership](../concepts/repository-workflow.md#historical-documentation-ownership) <br> [Lane additions](../specs/agent-lanes.md#lane-ownership) <br> [Permission scenarios](../specs/agent-lanes.md#historical-opencode-permission-contract) <br> [Artifact location](../concepts/repository-workflow.md#historical-transient-store-retirement) | historical |
| Repo-global transient context stores MUST NOT be used — scenarios: memory-bank directory must not exist, plans directory must not exist, Removal of legacy transient stores | [concepts/repository-workflow.md#historical-transient-store-retirement](../concepts/repository-workflow.md#historical-transient-store-retirement) | historical |
| OpenSpec changes have archive and completion expectations — scenarios: Durable outcomes sync to canonical docs before archive, Delta specs sync to main specs, Archive moves the complete change | [concepts/repository-workflow.md#historical-completion-and-archive](../concepts/repository-workflow.md#historical-completion-and-archive) | historical |

## openspec/changes/archive/2026-06-08-add-sequential-offline-downloads/specs/chapter-refresh-state-preservation/spec.md

SHA256: `61c62fd9bc1953ec02e109ad4f1037d973e615510d44b45b3c2d3270f37e65b5`

Coverage status: `incorporated`. Verification: `independently-verified` (2026-10-08, runner).

| Source requirement and complete scenario set | Destination | Disposition |
| --- | --- | --- |
| refreshChapters preserves chapter state — scenarios: Refresh preserves read state, Refresh preserves progress, Refresh preserves downloaded flag, New chapters get default state, Removed chapters are cleaned up | [specs/downloads.md#refresh-preserves-chapter-state](../specs/downloads.md#refresh-preserves-chapter-state) | deduplicated |

## openspec/changes/archive/2026-06-08-add-sequential-offline-downloads/specs/download-enqueue/spec.md

SHA256: `7d3aef1dad4f3e9669bfeb4b518a2b6e33f4d9e7cc5f970e899954015d2298aa`

Coverage status: `incorporated`. Verification: `independently-verified` (2026-10-08, runner).

| Source requirement and complete scenario set | Destination | Disposition |
| --- | --- | --- |
| Enqueue single chapter download — scenarios: Enqueue from novel reader, Enqueue from manhwa reader, Duplicate enqueue is ignored | [specs/downloads.md#single-and-batch-enqueue](../specs/downloads.md#single-and-batch-enqueue) <br> [Separate-reader history](../evidence/specification-evolution.md#reader-and-download-evolution) | deduplicated |
| Enqueue batch download for a series — scenarios: Enqueue unread-only batch, Enqueue range batch, Empty batch | [specs/downloads.md#single-and-batch-enqueue](../specs/downloads.md#single-and-batch-enqueue) | deduplicated |
| Sequential execution — scenarios: Sequential processing | [specs/downloads.md#sequential-work-and-batch-cancellation](../specs/downloads.md#sequential-work-and-batch-cancellation) | deduplicated |
| Cancel batch download — scenarios: Cancel active batch, Cancel batch with only queued items | [specs/downloads.md#sequential-work-and-batch-cancellation](../specs/downloads.md#sequential-work-and-batch-cancellation) | deduplicated |
| Download progress visibility — scenarios: Observe queue updates | [specs/downloads.md#queue-display-and-progress](../specs/downloads.md#queue-display-and-progress) | deduplicated |

## openspec/changes/archive/2026-06-08-add-sequential-offline-downloads/specs/download-offline-reader/spec.md

SHA256: `1034b14970d9b8f3961ddfb799c00186f285e36f9deaec21ea0985cc7971c06c`

Coverage status: `incorporated`. Verification: `independently-verified` (2026-10-08, runner).

| Source requirement and complete scenario set | Destination | Disposition |
| --- | --- | --- |
| Readers prefer local content — scenarios: Novel reader serves cached chapter, Manhwa reader serves cached chapter, Fallback to network when not downloaded | [specs/reader.md#renderers-and-payload-mismatch](../specs/reader.md#renderers-and-payload-mismatch) <br> [Separate-reader history](../evidence/specification-evolution.md#reader-and-download-evolution) | deduplicated |
| Downloaded indicator on chapters — scenarios: Downloaded chapter shown in list | [specs/reader.md#chapter-actions-and-recovery](../specs/reader.md#chapter-actions-and-recovery) | deduplicated |
| Delete downloaded chapter — scenarios: Delete from Downloads screen | [specs/downloads.md#offline-reading-and-deletion](../specs/downloads.md#offline-reading-and-deletion) | deduplicated |

## openspec/changes/archive/2026-06-09-harden-opencode-agent-lanes/specs/agent-lane-hardening/spec.md

SHA256: `18862bc28edba19247ca96dc894f077ce1291dceec217dab197fe9da90bce5ac`

Coverage status: `incorporated`. Verification: `independently-verified` (2026-10-08, runner).

| Source requirement and complete scenario set | Destination | Disposition |
| --- | --- | --- |
| Build agent SHALL NOT run git commands or verification tasks — scenarios: Build agent attempts git commit, Build agent runs verification | [specs/agent-lanes.md#lane-ownership](../specs/agent-lanes.md#lane-ownership) | deduplicated |
| Integrator lane owns git staging, commit, branch, push, PR, CI, and merge — scenarios: Integrator groups commits by prefix, Integrator creates branch and PR, Integrator watches CI and merges on green, CI checks fail | [specs/agent-lanes.md#lane-ownership](../specs/agent-lanes.md#lane-ownership) | deduplicated |
| Apply flow includes runner and reviewer correction cycle — scenarios: Runner reports failures after implementation, Reviewer reports blockers after implementation, Correction cycle exceeds maximum iterations, Both runner and reviewer pass | [specs/agent-lanes.md#historical-verification-correction-cycle](../specs/agent-lanes.md#historical-verification-correction-cycle) | historical |
| Archive flow includes integrator phase — scenarios: Archive triggers integrator, Integrator creates feature branch, Integrator creates PR and watches CI, CI passes and integrator merges, User confirmation before merge | [specs/agent-lanes.md#historical-archive-delivery](../specs/agent-lanes.md#historical-archive-delivery) | historical |
| Permission model reflects lane boundaries — scenarios: Build agent denied verification and git writes, Runner allowed verification and read-only git, Reviewer allowed read-only git, denied gradle, Integrator allowed git/gh workflow commands, Non-integrator agent attempts git commit, Integrator runs git commit | [specs/agent-lanes.md#historical-opencode-permission-contract](../specs/agent-lanes.md#historical-opencode-permission-contract) | historical |
| AGENTS.md documents integrator lane — scenarios: Integrator appears in specialists list | [specs/agent-lanes.md#lane-ownership](../specs/agent-lanes.md#lane-ownership) | deduplicated |

## openspec/changes/archive/2026-06-09-harden-opencode-agent-lanes/specs/repository-governance/spec.md

SHA256: `e21f1df3a8af977a2e80da10744d3ad06ec05d1a4e8fae5eb8a58ecef2c4588f`

Coverage status: `incorporated`. Verification: `independently-verified` (2026-10-08, runner).

| Source requirement and complete scenario set | Destination | Disposition |
| --- | --- | --- |
| Agent routing lives in AGENTS.md — scenarios: Integrator owns git/PR/CI lifecycle, Build agent does not run git commands, Agent permission model enforces lane boundaries | [specs/agent-lanes.md#lane-ownership](../specs/agent-lanes.md#lane-ownership) <br> [Permission boundary](../specs/agent-lanes.md#historical-opencode-permission-contract) | deduplicated |

## openspec/changes/archive/2026-06-09-show-download-progress/specs/download-enqueue/spec.md

SHA256: `686cc73bdb704061bc917549ed718fae06379393fef9fa5d57321b872087f84c`

Coverage status: `incorporated`. Verification: `independently-verified` (2026-10-08, runner).

| Source requirement and complete scenario set | Destination | Disposition |
| --- | --- | --- |
| Download progress visibility — scenarios: Observe queue updates, Progress percentage display | [specs/downloads.md#queue-display-and-progress](../specs/downloads.md#queue-display-and-progress) | deduplicated |

## openspec/changes/archive/2026-06-09-show-download-progress/specs/download-progress-reporting/spec.md

SHA256: `2f8570580bd10c0b4f76afa4a1acfa6aec9ae86f487c851fb3de24bae667936e`

Coverage status: `incorporated`. Verification: `independently-verified` (2026-10-08, runner).

| Source requirement and complete scenario set | Destination | Disposition |
| --- | --- | --- |
| Intermediate progress updates during download — scenarios: Manhwa page progress, Novel content progress | [specs/downloads.md#queue-display-and-progress](../specs/downloads.md#queue-display-and-progress) | deduplicated |
| Progress reflected in UI — scenarios: Progress percentage display | [specs/downloads.md#queue-display-and-progress](../specs/downloads.md#queue-display-and-progress) | deduplicated |
| Progress updates flow reactively — scenarios: Real-time progress update | [specs/downloads.md#queue-display-and-progress](../specs/downloads.md#queue-display-and-progress) | deduplicated |

## openspec/changes/archive/2026-06-10-samsung-search-public-api/specs/samsung-search-indexable-series/spec.md

SHA256: `e4d8701795705dff18016a23a7f152a55aecd6366566af3302fdeb8560ed052f`

Coverage status: `incorporated`. Verification: `independently-verified` (2026-10-08, runner).

| Source requirement and complete scenario set | Destination | Disposition |
| --- | --- | --- |
| Schema registration via public API — scenarios: Successful schema registration, Samsung Search not installed, Idempotent re-registration | [specs/samsung-search-index.md#availability-and-schema-registration](../specs/samsung-search-index.md#availability-and-schema-registration) | deduplicated |
| Indexable series projection query — scenarios: Series with downloaded chapters is included, Series with no downloaded chapters is excluded, Series not in library but with downloaded chapters is included | [specs/samsung-search-index.md#indexable-projection](../specs/samsung-search-index.md#indexable-projection) | deduplicated |
| Series document schema fields — scenarios: Document contains all required fields, Chapter fields are absent | [specs/samsung-search-index.md#series-document-fields](../specs/samsung-search-index.md#series-document-fields) | deduplicated |
| Deep link to Series screen — scenarios: Tapping search result opens Series screen, Deep link does not open reader | [specs/samsung-search-index.md#deep-link-destination](../specs/samsung-search-index.md#deep-link-destination) | deduplicated |
| Index rebuild on chapter download state change — scenarios: New chapter downloaded triggers rebuild, Chapter download deleted triggers rebuild, Batch download does not thrash | [specs/samsung-search-index.md#rebuild-and-batching](../specs/samsung-search-index.md#rebuild-and-batching) | deduplicated |
| ACTION_UPDATE_INDEX broadcast handling — scenarios: Index corruption recovery, WorkManager ensures execution | [specs/samsung-search-index.md#broadcast-recovery-and-permissions](../specs/samsung-search-index.md#broadcast-recovery-and-permissions) | deduplicated |
| Manifest permission declarations — scenarios: Permissions declared | [specs/samsung-search-index.md#broadcast-recovery-and-permissions](../specs/samsung-search-index.md#broadcast-recovery-and-permissions) | deduplicated |
| Graceful degradation — scenarios: Search unavailable does not block app | [specs/samsung-search-index.md#graceful-degradation](../specs/samsung-search-index.md#graceful-degradation) | deduplicated |
| Bulk insert with batching — scenarios: Large indexable set is batched, Small indexable set is single batch | [specs/samsung-search-index.md#rebuild-and-batching](../specs/samsung-search-index.md#rebuild-and-batching) | deduplicated |

## openspec/changes/archive/2026-07-06-fix-samsung-search-registration/specs/samsung-search-indexable-series/spec.md

SHA256: `8caa76cc11e4364fc95844b52001abc1863cc5e572d1dc380af8766a41bb4b33`

Coverage status: `incorporated`. Verification: `independently-verified` (2026-10-08, runner).

| Source requirement and complete scenario set | Destination | Disposition |
| --- | --- | --- |
| Schema registration via public API — scenarios: Successful schema registration, Availability probe via request_search_api_version, Availability probe returns null, Availability probe throws, Samsung Search not installed, Idempotent re-registration, register_schema extras contain name key, register_schema passes null for arg, Schema XML uses schema root with fieldTypes | [specs/samsung-search-index.md#availability-and-schema-registration](../specs/samsung-search-index.md#availability-and-schema-registration) | deduplicated |

## openspec/changes/archive/2026-07-08-use-samsung-library-search/specs/library-search-via-samsung-search/spec.md

SHA256: `487a0000bebdf8e27565ba5f09b80adc2fe80e737886e0f68fddd25c66047569`

Coverage status: `incorporated`. Verification: `independently-verified` (2026-10-08, runner).

| Source requirement and complete scenario set | Destination | Disposition |
| --- | --- | --- |
| Active Library search uses Samsung Search query — scenarios: Non-blank query uses provider results, Blank query keeps local library view | [specs/library-search.md#samsung-first-query](../specs/library-search.md#samsung-first-query) | deduplicated |
| Library search resolves local rows and preserves provider ordering — scenarios: Local display data is used, Provider order is preserved | [specs/library-search.md#eligibility-and-ordered-local-resolution](../specs/library-search.md#eligibility-and-ordered-local-resolution) | deduplicated |
| Search results are limited to downloadable library rows — scenarios: Non-library hit is hidden, Non-indexable hit is hidden | [specs/library-search.md#eligibility-and-ordered-local-resolution](../specs/library-search.md#eligibility-and-ordered-local-resolution) | deduplicated |
| Active search revalidates on library and indexable changes — scenarios: Search hit disappears after indexability changes | [specs/library-search.md#eligibility-invalidation](../specs/library-search.md#eligibility-invalidation) | deduplicated |
| Provider failure is distinct from empty results — scenarios: Provider failure shows an error, No matches shows empty results | [evidence/specification-evolution.md#library-provider-error-conflict](../evidence/specification-evolution.md#library-provider-error-conflict) | historical |

## openspec/changes/archive/2026-07-09-fix-source-listing-pagination/specs/source-listing-pagination/spec.md

SHA256: `b21425c6d4963266c00e3596c378cf11bd1c30289e969377570964c8cc629763`

Coverage status: `incorporated`. Verification: `independently-verified` (2026-10-08, runner).

| Source requirement and complete scenario set | Destination | Disposition |
| --- | --- | --- |
| Browse results auto-load when scrolled to the end — scenarios: browse auto-loads next page on end-of-list | [specs/source-pagination.md#browse-end-of-list-loading](../specs/source-pagination.md#browse-end-of-list-loading) | deduplicated |
| FreeWebNovel chapters are fully aggregated across paginated chapter lists — scenarios: FreeWebNovel chapter list walks multiple pages | [specs/source-pagination.md#complete-chapter-aggregation](../specs/source-pagination.md#complete-chapter-aggregation) | deduplicated |
| Supported source listings request and parse the requested page — scenarios: FreeWebNovel latest uses page-specific requests, AsuraScans popular and search use page-specific requests, AsuraScans latest and popular remain distinct | [specs/source-pagination.md#page-specific-listings](../specs/source-pagination.md#page-specific-listings) | deduplicated |
| Terminal pages disable next-page navigation — scenarios: End-of-list pages return hasNextPage=false | [specs/source-pagination.md#single-page-and-terminal-modes](../specs/source-pagination.md#single-page-and-terminal-modes) | deduplicated |
| Single-page listings remain single-page — scenarios: FreeWebNovel popular remains single-page, FreeWebNovel search remains single-page, FreeWebNovel latest remains terminal at its final page, AsuraScans latest remains homepage-only for page > 1 | [specs/source-pagination.md#single-page-and-terminal-modes](../specs/source-pagination.md#single-page-and-terminal-modes) | deduplicated |

## openspec/changes/archive/2026-07-11-refactor-library-browse-ui/specs/adaptive-series-catalog-ui/spec.md

SHA256: `899cef9d53a5ca72231a4ffaa3d0cb0058c32a7f1762b13fbf643ea0ef9e30e9`

Coverage status: `incorporated`. Verification: `independently-verified` (2026-10-08, runner).

| Source requirement and complete scenario set | Destination | Disposition |
| --- | --- | --- |
| Library and Browse use a shared cover-first series card — scenarios: Library renders catalog cards, Browse renders catalog cards | [specs/catalog-presentation.md#shared-cards-and-adaptive-layout](../specs/catalog-presentation.md#shared-cards-and-adaptive-layout) | deduplicated |
| Catalog layout adapts to available width — scenarios: Compact width remains usable, Expanded width uses available space | [specs/catalog-presentation.md#shared-cards-and-adaptive-layout](../specs/catalog-presentation.md#shared-cards-and-adaptive-layout) | deduplicated |

## openspec/changes/archive/2026-07-11-refactor-library-browse-ui/specs/library-blank-title-repair/spec.md

SHA256: `aacaedd4a1fc61db3f65876e40d76c2b1b96adae724902f16ea3a191ee9c6b48`

Coverage status: `incorporated`. Verification: `independently-verified` (2026-10-08, runner).

| Source requirement and complete scenario set | Destination | Disposition |
| --- | --- | --- |
| Library repairs blank bookmark titles through refresh — scenarios: Successful refresh repairs a blank title, Failed repair preserves the bookmark | [specs/catalog-presentation.md#blank-bookmark-repair](../specs/catalog-presentation.md#blank-bookmark-repair) | deduplicated |
| Library limits blank-title repair attempts per lifecycle — scenarios: Failed repair is not retried in the same lifecycle, A later lifecycle may retry | [specs/catalog-presentation.md#blank-bookmark-repair](../specs/catalog-presentation.md#blank-bookmark-repair) | deduplicated |

## openspec/changes/archive/2026-07-11-refactor-library-browse-ui/specs/library-browse-catalog/spec.md

SHA256: `201074c835877e0cd2b2205bd34c38dbfcd0dcbb4c678e05e8e90f58f3512105`

Coverage status: `incorporated`. Verification: `independently-verified` (2026-10-08, runner).

| Source requirement and complete scenario set | Destination | Disposition |
| --- | --- | --- |
| Browse search requires explicit submission — scenarios: Mode change does not fetch a blank query, Submitted query starts search | [evidence/specification-evolution.md#browse-submission-conflict](../evidence/specification-evolution.md#browse-submission-conflict) | historical |
| Browse exposes search progress and recovery — scenarios: Search is loading, Search failure can be retried | [specs/browse-discovery.md#request-progress-and-recovery](../specs/browse-discovery.md#request-progress-and-recovery) <br> [Explicit-submit history](../evidence/specification-evolution.md#browse-submission-conflict) | deduplicated |
| Browse rejects stale search responses — scenarios: Earlier query finishes last, Stale page response is ignored | [specs/browse-discovery.md#cancellation-and-stale-responses](../specs/browse-discovery.md#cancellation-and-stale-responses) | deduplicated |
| Existing catalog behavior remains available — scenarios: Library search retains Samsung Search semantics, Browse preserves pagination | [specs/browse-discovery.md#catalog-and-pagination-continuity](../specs/browse-discovery.md#catalog-and-pagination-continuity) <br> [Historical provider-error continuity](../evidence/specification-evolution.md#browse-submission-conflict) | deduplicated |

## openspec/changes/archive/2026-07-11-refactor-library-browse-ui/specs/library-filtering/spec.md

SHA256: `a64a573732a5a2e1971b4fb37807285d6b0ad5483cffe9d577a41fd5340807a1`

Coverage status: `incorporated`. Verification: `independently-verified` (2026-10-08, runner).

| Source requirement and complete scenario set | Destination | Disposition |
| --- | --- | --- |
| Library Unread filtering control — scenarios: Library filtering controls are displayed | [specs/catalog-presentation.md#supported-library-controls](../specs/catalog-presentation.md#supported-library-controls) | deduplicated |

## openspec/changes/archive/2026-07-11-refactor-library-browse-ui/specs/source-detail-parsing/spec.md

SHA256: `287fb0949f30481660f0eaf8604538b61c3f6fa28ca26d21ba11d389cdddf6a9`

Coverage status: `incorporated`. Verification: `independently-verified` (2026-10-08, runner).

| Source requirement and complete scenario set | Destination | Disposition |
| --- | --- | --- |
| Source detail parsing produces a usable title — scenarios: Extracted title is preferred, Nonblank incoming title is a fallback, Blank incoming title does not become a usable fallback | [specs/catalog-presentation.md#detail-title-selection](../specs/catalog-presentation.md#detail-title-selection) | deduplicated |

## openspec/changes/archive/2026-07-14-unify-reader-screen/specs/download-enqueue/spec.md

SHA256: `dee0d90d11508518c52a949081ae8b5e0b041ef4676b637ce85bc8ab25ad5931`

Coverage status: `incorporated`. Verification: `independently-verified` (2026-10-08, runner).

| Source requirement and complete scenario set | Destination | Disposition |
| --- | --- | --- |
| Enqueue single chapter download — scenarios: Enqueue novel from unified Reader, Enqueue manhwa from unified Reader, Enqueue from series detail, Duplicate enqueue is ignored | [specs/downloads.md#single-and-batch-enqueue](../specs/downloads.md#single-and-batch-enqueue) | deduplicated |

## openspec/changes/archive/2026-07-14-unify-reader-screen/specs/download-offline-reader/spec.md

SHA256: `62e52b93efe199c7b55c5a2a75a8e02d788bea2be3cbc28bdbf98a39981705cc`

Coverage status: `incorporated`. Verification: `independently-verified` (2026-10-08, runner).

| Source requirement and complete scenario set | Destination | Disposition |
| --- | --- | --- |
| Readers prefer local content — scenarios: Reader serves cached novel chapter, Reader serves cached manhwa chapter, Reader falls back to network when not downloaded, Downloaded content variant mismatches the route, Forced response remains mismatched | [specs/reader.md#renderers-and-payload-mismatch](../specs/reader.md#renderers-and-payload-mismatch) | deduplicated |

## openspec/changes/archive/2026-07-14-unify-reader-screen/specs/unified-reader-screen/spec.md

SHA256: `3ee80a1df5d601c6eb3e2838695fedcbe87f481dd01e3dd3e2c4cc34bee2abde`

Coverage status: `incorporated`. Verification: `independently-verified` (2026-10-08, runner).

| Source requirement and complete scenario set | Destination | Disposition |
| --- | --- | --- |
| Both series types use one Reader destination and screen — scenarios: Novel chapter opens Reader, Manhwa chapter opens Reader, ViewModel initializes route context | [specs/reader.md#shared-route-and-domain-variants](../specs/reader.md#shared-route-and-domain-variants) | deduplicated |
| Reader renders the existing chapter content shapes — scenarios: Text content loads, Page content loads, Route type and content disagree, Forced content still disagrees | [specs/reader.md#renderers-and-payload-mismatch](../specs/reader.md#renderers-and-payload-mismatch) | deduplicated |
| Reader provides shared immersive controls — scenarios: Reader content is tapped, Text progress is displayed, Page progress is displayed, Back is selected | [specs/reader.md#shared-immersive-controls](../specs/reader.md#shared-immersive-controls) | deduplicated |
| Reader preserves content-specific progress and read semantics — scenarios: Text scroll position changes, Stored text progress is loaded, Text content loads successfully, Final image page becomes current, Earlier image page becomes current, Page chapter loads again | [specs/reader.md#text-progress-and-read-state](../specs/reader.md#text-progress-and-read-state) <br> [Page semantics](../specs/reader.md#page-progress-and-read-state) | deduplicated |
| Reader supports chapter actions and recovery — scenarios: Adjacent chapter is selected, Chapter is selected from the sheet, Download is selected, Load fails and Retry is selected | [specs/reader.md#chapter-actions-and-recovery](../specs/reader.md#chapter-actions-and-recovery) | deduplicated |

## openspec/changes/archive/2026-07-27-fix-brose-init-fetch-and-reader-scroll/specs/asurascans-chapter-content.md

SHA256: `e14cb46c0c61886c467081cb6259cb40267a556b8a6fa9744c6ebb002c35e238`

Coverage status: `incorporated`. Verification: `independently-verified` (2026-10-08, runner).

| Source requirement and complete scenario set | Destination | Disposition |
| --- | --- | --- |
| Chapter pages include all images — scenarios: API returns complete page list, API fallback preserves existing behavior | [specs/reader.md#complete-asurascans-chapter-pages](../specs/reader.md#complete-asurascans-chapter-pages) | deduplicated |
| Source identity unchanged — invariant in requirement prose | [specs/reader.md#complete-asurascans-chapter-pages](../specs/reader.md#complete-asurascans-chapter-pages) | deduplicated |

## openspec/changes/archive/2026-07-27-fix-brose-init-fetch-and-reader-scroll/specs/browse-screen.md

SHA256: `d7a7cbc1da7763e32b8a2b45abb2f203840dc6b743abc4bdd27dbbd88bf385f3`

Coverage status: `incorporated`. Verification: `independently-verified` (2026-10-08, runner).

| Source requirement and complete scenario set | Destination | Disposition |
| --- | --- | --- |
| Browse screen auto-fetches popular series on launch — scenarios: Init fetches popular series for the first source, Init shows empty state when no sources available | [specs/browse-discovery.md#initial-popular-discovery](../specs/browse-discovery.md#initial-popular-discovery) | deduplicated |
| Existing test updated — scenarios: Test asserts init triggers fetch | [specs/browse-discovery.md#initial-popular-discovery](../specs/browse-discovery.md#initial-popular-discovery) <br> [Exact test-name transition](../evidence/specification-evolution.md#initial-discovery-and-reader-stability) | deduplicated |

## openspec/changes/archive/2026-07-27-fix-brose-init-fetch-and-reader-scroll/specs/manhwa-page-list.md

SHA256: `83e34b9e834bfeb4a959e121870375352cd945051337c69428a8a4ed496fd701`

Coverage status: `incorporated`. Verification: `independently-verified` (2026-10-08, runner).

| Source requirement and complete scenario set | Destination | Disposition |
| --- | --- | --- |
| Image items maintain minimum height during loading/error — scenarios: Image during loading has measurable height, Image on error has measurable height, Loaded image replaces minimum height | [specs/reader.md#measurable-image-placeholders](../specs/reader.md#measurable-image-placeholders) | deduplicated |

## openspec/changes/optimize-source-search-cache/specs/library-browse-catalog/spec.md

SHA256: `b9b73b9cfe86f1dbab12daf7487ba9c05e7a99118a3bd707f7c4bcbc4c60c11c`

Coverage status: `incorporated`. Verification: `independently-verified` (2026-10-08, runner).

| Source requirement and complete scenario set | Destination | Disposition |
| --- | --- | --- |
| Browse reuses fresh exact search results — scenarios: Exact query is repeated within TTL, Query, source, page, or filters differ | [specs/browse-discovery.md#exact-search-reuse](../specs/browse-discovery.md#exact-search-reuse) | incorporated |
| Browse falls back to matching local rows — scenarios: Empty remote result has local matches, Remote failure has local matches, Remote failure has no local matches, Later page fails | [specs/browse-discovery.md#page-one-local-fallback](../specs/browse-discovery.md#page-one-local-fallback) | incorporated |
| Browse search requires explicit submission — scenarios: Stable nonblank input starts search, Blank input performs no search, Selected source changes with a current query, Search remains source-specific | [specs/browse-discovery.md#live-source-specific-search](../specs/browse-discovery.md#live-source-specific-search) | incorporated |
| Browse exposes search progress and recovery — scenarios: Live search is loading, Search failure without fallback can be retried, Search failure has local fallback | [specs/browse-discovery.md#request-progress-and-recovery](../specs/browse-discovery.md#request-progress-and-recovery) | incorporated |
| Browse rejects stale search responses — scenarios: Query changes during debounce, Source changes during an active search, Cancelled response still completes, Stale page response is ignored | [specs/browse-discovery.md#cancellation-and-stale-responses](../specs/browse-discovery.md#cancellation-and-stale-responses) | incorporated |
| Existing catalog behavior remains available — scenarios: Library search retains Samsung-first semantics, Browse preserves pagination context, Browse preserves series navigation | [specs/browse-discovery.md#catalog-and-pagination-continuity](../specs/browse-discovery.md#catalog-and-pagination-continuity) | incorporated |

## openspec/changes/optimize-source-search-cache/specs/library-search-via-samsung-search/spec.md

SHA256: `bba89758554511883abfccef21625886cef571a7a6a960e1a83889ce09b0e535`

Coverage status: `incorporated`. Verification: `independently-verified` (2026-10-08, runner).

| Source requirement and complete scenario set | Destination | Disposition |
| --- | --- | --- |
| Library search debounces input and cancels superseded work — scenarios: Stable Library input starts search, Library input changes during debounce, Library input changes during active search, Library query becomes blank, Eligible local data changes during active search | [specs/library-search.md#proposed-typing-debounce-and-cancellation](../specs/library-search.md#proposed-typing-debounce-and-cancellation) | incorporated |
| Provider failure falls back to eligible local matching — scenarios: Samsung Search is unavailable, Local fallback matches metadata, Ineligible row matches, No eligible local match | [specs/library-search.md#provider-fallback](../specs/library-search.md#provider-fallback) <br> [Proposed title priority](../specs/library-search.md#proposed-title-first-fallback-ranking) | incorporated |
| Active Library search uses Samsung Search query — scenarios: Non-blank query prefers provider results, Non-blank query cannot use provider, Blank query keeps local library view | [specs/library-search.md#samsung-first-query](../specs/library-search.md#samsung-first-query) | incorporated |
| Library search resolves local rows and preserves provider ordering — scenarios: Local display data is used, Provider order is preserved, Multiple provider hits are resolved | [specs/library-search.md#eligibility-and-ordered-local-resolution](../specs/library-search.md#eligibility-and-ordered-local-resolution) | incorporated |
| Provider failure is distinct from empty results — invariant in requirement prose | [evidence/specification-evolution.md#library-provider-error-conflict](../evidence/specification-evolution.md#library-provider-error-conflict) | historical |

## openspec/changes/optimize-source-search-cache/specs/source-metadata-cache/spec.md

SHA256: `aeb5b8c8fb8c5d6c4ce5a47c192175d5e79a0f953966e9aea8fcf40c3a1bef8b`

Coverage status: `incorporated`. Verification: `independently-verified` (2026-10-08, runner).

| Source requirement and complete scenario set | Destination | Disposition |
| --- | --- | --- |
| Parsed source metadata uses bounded TTL caching — scenarios: Fresh metadata is requested again, Metadata entry expires, Cache reaches capacity, Operation TTL differs | [specs/source-metadata.md#bounded-parsed-metadata](../specs/source-metadata.md#bounded-parsed-metadata) | incorporated |
| Metadata cache keys isolate request identity — scenarios: Same query targets different source types, Search page differs, Equivalent immutable filter snapshot repeats, Filter order differs, Caller mutates its filter collection | [specs/source-metadata.md#request-identity](../specs/source-metadata.md#request-identity) | incorporated |
| Only successful remote metadata becomes reusable remote truth — scenarios: Source request fails, Search uses local fallback | [specs/source-metadata.md#successful-remote-truth](../specs/source-metadata.md#successful-remote-truth) | incorporated |
| Reader payload ownership remains unchanged — scenarios: Undownloaded chapter is opened | [specs/source-metadata.md#payload-ownership](../specs/source-metadata.md#payload-ownership) | incorporated |

## openspec/changes/optimize-source-search-cache/specs/source-request-performance/spec.md

SHA256: `e2fc7b17a564fd229f1178815639bdf256cdb9cf3ce1da72e9894f1f6bfef03d`

Coverage status: `incorporated`. Verification: `independently-verified` (2026-10-08, runner).

| Source requirement and complete scenario set | Destination | Disposition |
| --- | --- | --- |
| FreeWebNovel fetches known chapter-list pages with bounded concurrency — scenarios: Chapter list spans multiple pages, Chapter-list retrieval is cancelled | [specs/source-pagination.md#proposed-bounded-concurrency](../specs/source-pagination.md#proposed-bounded-concurrency) | incorporated |
| Concurrent chapter-list retrieval preserves deterministic results — scenarios: Later page completes first, Duplicate chapter appears on adjacent pages, Middle page fails, Page contains only duplicate URLs, Terminal page occurs inside a request window | [specs/source-pagination.md#proposed-deterministic-contiguous-merge](../specs/source-pagination.md#proposed-deterministic-contiguous-merge) | incorporated |

## openspec/config.yaml

SHA256: `5ee0ff07ee73a20a662f55055d1f75dd599fa1fb3c18527893b7b3d5352362a0`

Coverage status: `incorporated`. Verification: `independently-verified` (2026-10-08, runner).

| Source claim | Destination | Disposition |
| --- | --- | --- |
| Active schema selects house-style | [Workflow](../concepts/repository-workflow.md#historical-house-style-configuration) | historical |
| Commented project context and per-artifact examples | [Assessment](../concepts/repository-workflow.md#historical-house-style-configuration) | no-durable-substance: commented generic examples, no active project facts/rules |
