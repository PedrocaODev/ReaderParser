---
id: evidence-ui-source-implementation-gaps
title: "Catalog, reader, and performance implementation gaps"
type: Evidence
status: draft
valid_from: 2026-10-08
scope:
  system: readerparser
relations:
  - type: supported_by
    target: ./ui-source-history.md
  - type: relates_to
    target: ../specs/reader.md
  - type: relates_to
    target: ../specs/library-search.md
  - type: relates_to
    target: ../specs/source-pagination.md
  - type: relates_to
    target: ../specs/browse-discovery.md
---

# Catalog, reader, and performance implementation gaps

These gaps distinguish original intended behavior, historical verification testimony, and coordinator-supplied migration source inspection.
No new application tests ran for this documentation migration. Source inspection cannot prove runtime behavior or test execution.
All uncertainty is incorporated as knowledge. These gaps require no code changes to complete documentation coverage.

## Library debounce and ranking

The performance design/tasks require 300 ms user-input debounce, cancellation, and immediate nonblank reruns on active-search invalidation.
They also require fallback across title/author/genres with title matches ranked first.
The checked tasks and final report imply completion.
Migration source inspection reports eligible snapshot/batch mapping/local fallback present in `SeriesRepositoryImpl`, but no Library debounce or title-first fallback ranking.
Retain the unimplemented conditions as intended requirements. Do not use historical green tests to declare them satisfied.
To close the behavioral gap, verify ViewModel scheduling/invalidation and repository ordering with targeted tests in a separately authorized implementation task.

## FreeWebNovel concurrency

The performance change requires discovery on page one, then windows of at most three concurrent remainder requests.
Requirements include page-order merge, URL deduplication, cancellation, contiguous partial results, zero-unseen termination, ignored later same-window results, and no later window.
Historical verification substitutes sequential AJAX retrieval with deduplication for that requirement.
Migration source inspection reports the remainder still serial. The concurrency candidate remains proposed, not an accepted implemented decision.
Closing the behavioral gap requires deferred-response request-count/ordering tests and separately authorized source work.

## Reader progress and coverage

The design/tasks specify per-distinct-value text persistence, immediate text read, page-zero loads, non-persisted page position, and final-page read.
The retrospective says persistence is debounced and restoration separates pending updates from restored values.
Verification says progress confirmation waits for successful writes and restoration happens at page-finished time.
These differences do not establish one authoritative detailed timing contract without source/test reconciliation.
The design says state holds loaded `ChapterContent`, while the retrospective describes parallel `html`/`pages` fields and proposes sealed-state replacement.

Tasks mark Compose and navigation coverage complete. The verification report claims disposal repair complete.
The retrospective still requests tap/Back and route-construction tests and suggests explicit WebView destruction, while verification claims disposal already fixed.
The general green unit/assemble/lint report cannot resolve specific test execution or instrumentation coverage.
Retain both historical assertions and the targeted review questions without weakening the intended scenarios.

## Catalog connected-test claim

The catalog report says its emulator was offline and connected Compose tests did not run.
It also claims Library/Browse content tests “compile and pass via assembleDebug.”
Assembly alone does not demonstrate Compose test execution. Record UI coverage as historically claimed but unverified.
The report's JDK/deprecated-icon/component-dimension warnings are explicitly pre-existing or deferred; the migration does not fix unrelated code.

## Asura endpoint and completeness

The design proposes `/api/novels/{slug}/download`, subject to completeness verification.
Tasks/retrospective and migration source inspection identify `/api/series/{series_slug}/chapters/{chapter_slug}` instead.
Preserve the former as an unsuccessful hypothesis and the latter as reported/source-inspected implementation, rather than claiming both contracts accepted.
The live historical sample returned sixteen pages for one chapter. This does not prove every chapter or future API stability.
Old UUID chapter slugs rely on HTML fallback rather than guaranteed `chapter-{N}` synthesis.
Manual item-height verification and preserved existing parser tests do not independently prove complete API fallback or all-device scroll behavior.
The report claims no lint issues without documenting a lint run.

## Browse policy evolution

July catalog work required explicit-only search and no blank-query fetch on source/mode changes.
Later performance work replaces explicit-only search with debounced nonblank input, actual cancellation, and final stale-response guards.
Coordinator-supplied source inspection reports current Browse debounce implemented.
Keep old rationale as historical context, and route guidance through canonical Browse specifications rather than obsolete policy.
The July 27 Popular-on-entry fix concerns discovery initialization, not blank search submission.

## Coverage versus runtime certainty

An unresolved behavioral question can have complete documentary coverage when its claims, evidence limits, and question are incorporated explicitly.
Unmapped or uncertain substance in the source-coverage matrix still blocks deletion of that affected source.
Only the independent runner may verify coverage. A checked task list, historical PASS, or healthy graph is insufficient by itself.
