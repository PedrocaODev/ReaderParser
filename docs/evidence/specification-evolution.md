---
id: evidence-specification-evolution
title: "Specification evolution and unresolved intent"
type: Evidence
status: active
valid_from: 2026-10-08
scope:
  system: readerparser
relations:
  - type: relates_to
    target: ../concepts/repository-workflow.md
---

# Specification evolution and unresolved intent

## Interpretation and dates

This synthesis dates from 2026-10-08. That date is not the original effective date of migrated capability requirements.
Dates embedded in archive directory names identify historical archive context. They do not prove acceptance or complete implementation.
Main specs with an unspecified purpose or archive-generated TBD carry no extra behavioral claim in that placeholder.
The specs vault consolidates intended requirements. Code and independent verification determine actual implementation.
No supersedes relation is asserted between historical OpenSpec artifacts and these topic nodes.
Their scopes overlap only partially and original effective dates are not established by directory naming alone.

## Governance retirement

The user explicitly retired normative OpenSpec workflow on 2026-10-08 and approved the skill workflow described in
[Repository workflow](../concepts/repository-workflow.md#current-approved-workflow).
The historical requirement to create a change before code/docs, satisfy `applyRequires`, maintain deltas, and archive is no longer current guidance.
The 2026-06-05 governance context, 2026-06-09 lane hardening, canonical ownership, exclusions, and archive rules retain historical value.
The old house-style schema choice was active configuration. Its generic commented examples were not active rules or project facts.

## Browse submission conflict

The 2026-07-11 catalog spec and its main copy required explicit submission before source search.
Without a submitted query, mode/source changes could not request source results for a blank query.
They required loading feedback for submitted requests, retry of that submitted request, and rejection of earlier query/source/mode/page responses.
The unarchived optimization explicitly replaces explicit-only submission with source-specific 300 ms live Search, optional immediate keyboard action,
blank clearing, cancellation, local fallback, and exact cached reuse.
Current Browse code already debounces by 300 ms. Therefore explicit-only submission cannot be advertised as current implemented behavior.
The 2026-07-27 initial Popular fetch is separate from blank Search requests.
Historical catalog continuity required Library sort/search/removal and then-established Samsung query/local resolution/order/error behavior.
The new provider fallback changes only the failure policy described below. Preserve the other continuity conditions.

## Library provider-error conflict

The 2026-07-08 Library main/delta specs required a nonblank Samsung query rather than in-memory title filtering.
They distinguished provider execution failure (search error) from successful no hits (normal no matches).
The unarchived optimization instead requires Samsung-first results with eligible local fallback on unavailable/failed providers.
Fallback can return a successful empty result, so absence of local matches is not a provider-error screen.
Current code contains provider fallback and batch local lookup. The historical error rule must not be presented as current behavior.
Eligibility, canonical local display data, provider ordering, blank local view, and downloaded/library invalidation remain common requirements.

## Partial optimization gaps

The unarchived `optimize-source-search-cache` requirements are intended work, not proof of complete implementation.
Current Browse uses 300 ms debounce. Current Library lacks the proposed 300 ms typing debounce.
Current Library has local provider fallback and batch lookup but lacks title-first fallback ranking.
Current FreeWebNovel does not implement the proposed three-request chapter-page concurrency.
The proposed Library cancellation/data-invalidation distinctions, cache limits and key semantics, and contiguous concurrent-page merge remain
preserved in their capability nodes. These documents alone do not certify all remaining optimization scenarios.

## Initial discovery and reader stability

The 2026-07-27 delta required first-source Popular fetching immediately after Browse initialization, including loading and empty-source cases.
It replaced the old test `init loads sources and selects first source without fetching` with
`init loads sources, selects first source, and fetches popular`, asserting `seriesRepository.fetchPopular` with the first ID.
The same context required measurable manhwa image placeholders during loading/error, natural-size transition after success,
and full AsuraScans page retrieval with HTML fallback while retaining source identity.
Those direct Markdown deltas were not synchronized into separate main capabilities and must remain represented in the vault.

## Reader and download evolution

The 2026-06-08 download delta used separate novel and manhwa reader terminology, each enqueueing QUEUED work and preferring matching local content.
Its general non-null local-copy prose lacked the later explicit mismatch recovery conditions.
The 2026-07-14 Reader deltas consolidate both types into one destination and add one forced network fetch on content mismatch,
retryable unexpected-content errors after a second mismatch, and no automatic forced-request loop.
The new Reader contract covers both old content-specific enqueue/cache scenarios without preserving obsolete separate-screen guidance.
The 2026-06-09 progress delta adds percentages and milestone reporting to the earlier state/progress display.
The chapter refresh invariant retains existing read/progress/downloaded values, defaults new rows, and removes stale rows.

## Registration and pagination corrections

The 2026-06-10 Samsung public-API delta establishes schema, indexable projection, series fields, Series deep links, rebuilds, permissions,
broadcast recovery, graceful degradation, and 100-document batches.
The 2026-07-06 registration correction adds API-version availability probing, name in extras with null arg,
and `<schema>`/fieldType requirements. `getType()` and `<search-scheme>` cannot satisfy that corrected contract.
The 2026-07-09 pagination context distinguishes real paged listings from FreeWebNovel popular/search and homepage-only AsuraScans latest.
Terminal semantics and complete FreeWebNovel chapter aggregation stay separate from the later proposed concurrency optimization.

## Catalog integrity evolution

The 2026-07-11 catalog UI context adds shared cover-first cards, adaptive layout, active-theme tokens, and title repair.
It removes unsupported Unread filtering while retaining other Library behavior.
Detail extraction prefers a nonblank page title, uses only a nonblank incoming fallback, and does not treat blank input as usable.
Blank bookmark repair preserves identity/data, leaves failures unchanged, attempts once per lifecycle, and may retry in a later lifecycle.

## Lane-history limits

The historical lane spec combines automatic merge-on-green phrasing with a scenario requiring explicit pre-merge confirmation.
It also describes OpenCode permission settings that do not establish present tool permissions.
Preserve the responsibilities and both historical conditions, but use actual authorization before present git delivery.
The retired apply/archive triggers and three-cycle correction policy remain historical, separate from current task-specific verification.
