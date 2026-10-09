---
id: evidence-ui-source-history
title: "Catalog, reader, and source implementation history"
type: Evidence
status: draft
valid_from: 2026-10-08
scope:
  system: readerparser
relations:
  - type: relates_to
    target: ../concepts/catalog-and-source-parsing.md
  - type: relates_to
    target: ../concepts/reader-runtime.md
  - type: relates_to
    target: ../concepts/source-metadata-performance.md
  - type: relates_to
    target: ./implementation-gaps.md
---

# Catalog, reader, and source implementation history

This evidence incorporates historical reasoning, test claims, review findings, lifecycle facts, and their limits.
Historical reports are assertions from the former workflow. The migration does not rerun application verification or infer acceptance from checked tasks.
Original paths/hashes in the [coverage matrix](../migrations/coverage-ui-source.md) identify provenance after source removal.

## Lifecycle facts

| Change identifier | Original creation date | Original schema | Original directory state at migration |
| --- | --- | --- | --- |
| `2026-07-09-fix-source-listing-pagination` | 2026-07-08 | `house-style` | Under `openspec/changes/archive/`. |
| `2026-07-11-refactor-library-browse-ui` | 2026-07-10 | `house-style` | Under `openspec/changes/archive/`. |
| `2026-07-14-unify-reader-screen` | 2026-07-12 | `house-style` | Under `openspec/changes/archive/`. |
| `2026-07-27-fix-brose-init-fetch-and-reader-scroll` | 2026-07-27 | `house-style` | Under `openspec/changes/archive/`. |
| `optimize-source-search-cache` | 2026-07-12 | `house-style` | Unarchived; all task checkboxes marked complete. |

These facts incorporate the five `.openspec.yaml` files without retaining YAML payload copies.
Dates embedded in archived directory names identify the old record location, not proven acceptance/effective dates.
2026-10-08 is this synthesis's migration date. Unknown decision-acceptance dates remain unknown.
The legacy schema organized proposal/design/plan/tasks/verification records. It no longer mandates future change bundles.

## Pagination implementation and verification

The pagination change combined three bounded goals: end-of-list Browse auto-load with manual fallback, complete FreeWebNovel chapters, and distinct Asura latest/popular parsing.
Tests had to precede each production slice, except existing ViewModel guard coverage could remain unchanged.
The plan required reviewing auto-load minimality, complete chapter aggregation, and whether Asura needed production changes at all.
Tasks marked all three slices and final targeted/full verification complete.
Source mode expectations and bounded chapter behavior are incorporated in [catalog parsing](../concepts/catalog-and-source-parsing.md#source-pagination-contracts).

The retrospective reports AJAX `pageSize=200`, `totalPage`, page-one HTML fallback, malformed-page stop, and preserved cancellation.
It says Source remained unchanged and targeted tests caught source edge cases before broad verification.
It still flags some latency and later-page markup drift. Preserve selector/fixture coverage and investigate fresh slowdowns.

| Recorded command/check | Recorded outcome |
| --- | --- |
| `rtk gradlew :app:compileDebugAndroidTestKotlin` | Passed. |
| `rtk gradlew :app:testDebugUnitTest --tests "*BrowseViewModelTest"` | Passed. |
| Connected adb `BrowseContentTest` | Six tests OK. |
| `rtk gradlew :app:testDebugUnitTest --tests "*FreeWebNovelTest"` | Passed. |
| `rtk gradlew :app:testDebugUnitTest --tests "*AsuraScansTest"` | Passed. |
| Full `:app:testDebugUnitTest --console=plain` with historical SDK environment and `rtk gradlew` | BUILD SUCCESSFUL in 15s. |
| `:app:assembleDebug --console=plain` with the same environment | BUILD SUCCESSFUL in 33s. |
| `:app:testDebugUnitTest --tests "*FreeWebNovelTest" --console=plain` with the same environment | BUILD SUCCESSFUL; claimed coverage of AJAX size, pagination, HTML fallback, malformed stop, cancellation. |
| `rtk adb -s RXCYA05Q1DN install ./app/build/outputs/apk/debug/app-debug.apk` | Success. |

Historical tasks name `BrowseContentTest.scrollToEnd_dispatchesLoadMoreAutomatically` and `BrowseContentTest.manualLoadMoreButton_remainsClickable`.
They cite compilation, green BrowseViewModel tests, and previously green six-test adb runs.
The plan made APK assembly conditional on needing a device APK. The report records assembly/install as actually performed.
Historical commit grouping separated auto-load UI, FreeWebNovel pagination, Asura regression/parser repair, and optional verification cleanup.
No pagination repair clearly establishes the three ADR criteria by itself.

## Catalog refactor verification and limits

The catalog refactor addressed inconsistent list-first surfaces, blank saved titles, and an inert Unread filter.
Its five slices were title extraction, lifecycle-scoped repair, shared adaptive cards/grid, Library cleanup, and explicit Browse submission.
All task checkboxes are checked. Source-title/repair, adaptive UI, and request-identity conditions are in [catalog parsing](../concepts/catalog-and-source-parsing.md).
Three checkpoint reviews required fixing or disposing findings after repair, UI cleanup, and Browse handling.
Historical commit grouping separated title/repair fixes, shared catalog/Unread refactoring, and explicit-search request guarding.
The last policy is historical because later source inspection reports live Browse search.

The final report is dated 2026-07-10 and claims successful `./gradlew :app:testDebugUnitTest`, `:app:assembleDebug`, and `:app:lintDebug`.
Oracle gates reportedly fixed one source/repair finding, four catalog/Browse findings, and three final-diff findings.
The report asserts all five slices implemented, all gates resolved, and no regressions.
An offline emulator prevented connected Compose instrumentation.
Its additional claim that Library/Browse content tests “compile and pass via assembleDebug” does not establish that Compose tests executed.
Do not turn this report into current UI verification without independent execution evidence.

The plan intended targeted FreeWebNovel, Asura, repository, Library/Browse ViewModel, and Library/Browse content cases before full unit/assemble/lint.
Connected instrumentation was conditional on emulator availability.
Remaining pre-existing warnings were JDK target fallback, deprecated `Icons.Filled.Sort`, and `SeriesCard.kt` dimensions outside the theme package.
The dimensions placement was explicitly deferred as non-blocking. The migration does not change it.

## Reader verification and retrospective

The reader change removed duplicated screen/state/navigation paths while retaining HTML and page renderers.
It planned test-first state consolidation, shared controls, route replacement, and documentation synchronization.
Documentation review was the explicit non-executable test exception, after route and implementation tests established final architecture.
Checkpoint reviews covered state/progress/cancellation, gestures/accessibility, restored routing/offline behavior, and canonical documentation consistency.
Historical commits separated Reader state/content, route replacement, and architectural documentation.

Tasks marked ViewModel, Compose, navigation, documentation, and full verification work complete.
They specifically claim distinct text progress persistence, immediate text read, page-zero initialization, non-persisted page position, final-page read, and restoration after layout.
The retrospective instead calls text persistence debounced. These statements require reconciliation rather than silent selection.

The verification report claims PASS for `./gradlew :app:testDebugUnitTest`, `:app:assembleDebug`, and `:app:lintDebug`.
It lists nine resolved oracle findings:

| Finding | Historical claimed resolution |
| --- | --- |
| Progress restoration timing | `WebViewClient.onPageFinished`. |
| Dead Retry with missing chapter | Store `chapterUrl` in state. |
| Initial load errors unhandled | Add a try/catch wrapper. |
| Progress considered persisted before write success | Defer confirmation until successful write. |
| O(H) HTML generation on each scroll | `remember(html, isDarkTheme)`. |
| WebView leak on disposal | `stopLoading()` and `destroy()`. |
| ReaderContent over 400 lines | Extract `ManhwaPageList`. |
| Four duplicated ViewModel branches | Extract `processLoadedContent()`. |
| Task checklist overclaims | Claimed correction to actual coverage. |

Its final oracle quote claims no blockers or warnings. That is historical testimony, not this migration's verdict.
The retrospective praises tests for mismatch recovery/progress clamping, parallel ViewModel/content dispatch, and real review fixes for reload, Retry, and progress races.
It describes separating `restoreProgress` from `pendingProgress` and posting restoration after page-finished layout.
It cautions about very long chapters, shared load-processing indirection, and about 300 lines of remaining overlay chrome.
It recommends tap-visibility/Back Compose tests, route-construction tests, explicit WebView destruction, and sealed state rather than parallel HTML/pages fields.
These follow-ups conflict with some tasks/verification claims of completed coverage and disposal repair.

The retrospective reports six new files, sixteen deleted files, 129 added lines, 2,167 removed lines, and a net reduction of 2,038 lines.
These are reported historical diff counts, not a migration-time recount.
It also records `ui/reader/`, one content-type route, the shared chapter sheet, and no new dependencies.
Former synchronization of architecture/AGENTS/codemaps records an ownership obligation, not a requirement to retain codemap generation.
See [reader runtime](../concepts/reader-runtime.md) for retained behavior and [gaps](./implementation-gaps.md#reader-progress-and-coverage) for qualification.

## Browse launch and image completeness evidence

The July 27 repair addressed an initially empty Popular grid and mid-chapter scroll truncation.
The proposal identified missing initial auto-fetch, hypothesized JavaScript-lazy-loaded page omission, and zero-height loading/error images.
The plan put initial Browse fetch first, measurable image items second, and API discovery with fixture-based completeness tests third.
Initial-fetch tests were to fail before adding the call.
The image plan allowed snapshot measurement or manual testing when reliable controlled image loading was infeasible.
Reviews after initial fetch and API integration checked correctness and fallback robustness.
Historical commit grouping separated initial fetch, image minimum height, and source completeness repair.

Tasks/retrospective report `autoFetchCurrentMode` at initialization, `heightIn(min = 150.dp)`, and API-first page lists with HTML fallback.
The design's `/api/novels/{slug}/download` endpoint is an unaccepted hypothesis.
Tasks identify `https://api.asurascans.com/api/series/{series_slug}/chapters/{chapter_slug}`.
The retrospective notes corrected slug patterns, old UUID chapter slugs covered by HTML fallback, and API page dimensions as future enhancement material.
It says existing parser tests still passed without fixture changes and no additional HTML enhancement was needed.
Minimum height addresses transient image failure/collapse even for extreme image ratios; it does not prove API completeness.

| Historical check | Recorded outcome and limitation |
| --- | --- |
| Full `:app:testDebugUnitTest --console=plain` | 6s, 34 actionable tasks (1 executed, 33 up-to-date), 370 tests, zero failed/skipped. |
| `:app:testDebugUnitTest --tests "*AsuraScansTest*" --console=plain` | Twenty tests, zero failures. |
| `:app:testDebugUnitTest --tests "*BrowseViewModelTest*" --console=plain` | BUILD SUCCESSFUL. |
| `curl "https://api.asurascans.com/api/series/player-who-cant-level-up/chapters/chapter-237"` | Claimed sixteen pages with URLs and dimensions. One sampled chapter does not prove every chapter. |
| Image scroll verification | Task claims manual measurable-height verification; no independent device artifact is retained. |

The report says review had no actionable findings, all planned tests passed, and live exploratory API verification succeeded.
It concludes no lint issues but does not list a lint command. Preserve that distinction.
The retrospective claims clean slice delegation and no follow-ups; API format/authentication drift and UUID fallback remain integration risks.

## Performance verification and overclaims

The unarchived performance change proposed cache, Browse live search, Library snapshot/fallback, and three-request FreeWebNovel windows.
Every task is checked, including exact TTL/capacity tests, Browse cancellation, Library debounce/ranking, concurrency terminal conditions, reviews, and full verification.
The proposal's motivation was explicit-only search, obsolete network/parser work, uncertain metadata freshness, unavailable Samsung results, N+1 lookups, and serial chapter pages.
Its compatibility boundary retained identity, Source contracts, DownloadStore/Coil ownership, external index lifecycle, and dependencies.

The report records successful unit tests, debug assemble, and lint using the historical SDK environment.
It claims all four slices complete, reports monotonic LRU caching with 5/15/2-minute TTLs, Browse 300 ms debounce/cancellation/source isolation, and Samsung-first snapshot fallback.
For FreeWebNovel it calls sequential AJAX pagination with deduplication “already implemented,” which does not establish proposed bounded concurrency.
It claims wraparound, key collision, coverage gaps, uncanceled jobs, and non-Library eligibility were fixed, with every finding resolved.
Migration inspection supplied by the coordinator confirms cache/Browse/snapshot work but identifies missing Library debounce, title-first ranking, and FreeWebNovel concurrency.
These conflicts are fully incorporated as qualified gaps rather than corrected code or rewritten requirements.

The historical plan required controllable-clock cache tests, deferred-response MockEngine concurrency tests, and four dependent review checkpoints.
Each review covered ownership/key/expiry, Browse cancellation/fallback, Library ordering/eligibility/invalidation, or source concurrency/partial results respectively.
The planned targeted set included cache, Series/Chapter repositories, Browse/Library ViewModels, TitleMatcher, and FreeWebNovel.
It additionally intended full unit/assemble/lint and conditional connected Browse/Library tests.
Historical commit grouping separated cache, Browse fallback, Library fallback, and FreeWebNovel parallelization.
The parallelization group was labeled `refactor:` even though it changes request concurrency; this is a historical label, not a future commit convention.

## Historical command environment

The historical environment prefix was `ANDROID_HOME=/home/pedro/Android/Sdk ANDROID_SDK_ROOT=/home/pedro/Android/Sdk`.
Where plans/reports used `./gradlew`, their common checks were `:app:testDebugUnitTest --console=plain`, `:app:assembleDebug --console=plain`, and `:app:lintDebug --console=plain`.
Conditional instrumentation used `:app:connectedDebugAndroidTest --console=plain`.
These exact identifiers preserve old verification claims. They do not establish that the old SDK path, device ID, tool aliases, or emulator are usable now.
