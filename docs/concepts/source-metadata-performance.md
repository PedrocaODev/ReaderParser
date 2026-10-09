---
id: concept-source-metadata-performance
title: "Source metadata ownership and request performance"
type: Concept
status: draft
valid_from: 2026-10-08
scope:
  system: readerparser
relations:
  - type: supported_by
    target: ../evidence/ui-source-history.md
  - type: relates_to
    target: ../evidence/implementation-gaps.md
  - type: relates_to
    target: ../specs/source-metadata.md
  - type: relates_to
    target: ../specs/browse-discovery.md
  - type: relates_to
    target: ../specs/library-search.md
  - type: relates_to
    target: ../specs/source-pagination.md
---

# Source metadata ownership and request performance

## Ownership and scope

The performance proposal distinguished parsed metadata freshness from server-controlled HTTP reuse.
Its design recorded a shared OkHttp 50 MB disk cache, Room-discovered series rows, DownloadStore reader payloads, and Coil image caching.
Room rows alone do not preserve query/page ordering or deterministic metadata freshness.
Successful parsed metadata caching belongs at repository boundaries across NOVEL and MANHWA.
Reader payloads enter DownloadStore only through explicit downloads. Image caching remains Coil's responsibility.
Keep transport caching enabled without adding duplicate Ktor `HttpCache` ownership.

The proposal excluded persistent query snapshots/offline catalog history, schema migration, new cache dependencies, and caching every opened chapter.
It also excluded cross-source Browse fan-out, speculative prefetch, filters, source ranking changes, Source/ID/identity changes, and global timeout/retry changes.
Healthy Samsung Search and its external registration/index lifecycle remain intact.
Entry-based limits are deliberate. Byte-weighted caches require measured chapter-list sizes before adding complexity.

## Exact fresh metadata cache

Use synchronized access-order maps, a monotonic clock, bounded entry counts, and expiry before requesting remote data.
Only successful remote metadata results enter the cache. Failed results and local fallbacks must not become cached remote truth.
An exact fresh hit returns without a Source call. In-memory entries disappear with process death and do not promise offline catalog browsing.

| Operation | Maximum entries | TTL | Key requirements |
| --- | --- | --- | --- |
| Catalog/search | 100 | Five minutes | Source ID, operation, normalized query, page, immutable ordered `FilterList` snapshot. |
| Series details | 50 | Fifteen minutes | `(sourceId, seriesUrl)`. |
| Chapter list | 20 | Two minutes | `(sourceId, seriesUrl)`. |

Filter order remains significant because it matches source request input.
Prefer a safe cache miss to treating reordered filters as equivalent.
Source identity isolates content types, so no separate content-type cache policy was required.
Short TTLs allow brief stale metadata while bounding it without explicit invalidation machinery.
Review must cover monotonic wraparound, unambiguous key encoding, synchronization, exact isolation, LRU eviction, and immutable key snapshots.

## Browse live search and local fallback

Each nonblank Browse query schedules a 300 ms debounce.
New input, source selection, or mode changes cancel pending debounce and active request work.
Request identity remains a final stale-response guard because cancellation is cooperative.
Blank input clears search results without a Source request.
Changing source while a nonblank Search query remains visible clears old-source results and schedules the same query for the new source.
Search pagination retains selected source and normalized query. Cache hits follow normal successful state transitions.
Preserve loading, retry, keyboard/search affordances, navigation, and pagination feedback.

For page one only, an empty successful remote search or remote failure checks persisted rows for the selected source.
Apply normalized title matching through existing `TitleMatcher` behavior.
Nonempty local matches return with no next page. Never use another source's rows.
If a remote failure has no local matches, propagate the error so the UI can retry.
An empty successful remote result with no local matches remains a successful empty result.
Later pages never fall back because local rows do not represent remote pagination.
Fallback quality depends on previously discovered rows and cannot find unseen catalog titles.

Historical explicit-only search and cancellation-by-ignoring-late-results consumed unnecessary network/parser work.
Migration source inspection reports Browse debounce/cancellation and repository fallback now present, without constituting a new runtime test.
Persistent query caches and immediate Room FTS were rejected because they require schema, synchronization, and invalidation overhead before a measured need.
Always returning an empty fallback after errors was rejected because it would hide failure and remove retry.
Searching all sources while typing was rejected because it multiplies requests outside source-specific Browse scope.

## Samsung-first Library snapshot and fallback

For each nonblank Library query, load once the local rows that are both in Library and indexable through downloaded chapters.
Attempt Samsung Search directly rather than adding a separate availability probe on each keystroke.
On success, index the eligible snapshot by `(sourceId, url)` and map hits in provider order without per-hit Room lookups.
On failed or unavailable provider queries, filter that same snapshot by normalized title, author, and genres.
The intended fallback ranks title matches first. No local matches should produce a successful empty result.
Eligibility excludes non-Library and non-indexable rows, even if the provider returns them.
Provider ordering remains authoritative for successful provider results.

The design also required 300 ms Library input debounce and cancellation of superseded work.
Active-search invalidations rerun the current nonblank query immediately because local eligibility changed, rather than treating invalidation as new typing.
Retain existing blank-query behavior. ContentResolver work may continue inside the platform even after cancellation.
Debounce limits obsolete starts, while request generation prevents stale display.
Migration inspection reports snapshot resolution and local fallback present, but Library debounce and title-first ranking absent.
These gaps remain intended requirements in [implementation gaps](../evidence/implementation-gaps.md#library-debounce-and-ranking), not reasons to rewrite the specification.

## FreeWebNovel bounded remainder proposal

The first AJAX page remains authoritative for discovering `totalPage` and HTML fallback behavior.
The proposed optimization processes pages `2..totalPage` in ascending windows of at most three concurrent requests.
Merge each completed window in page order and deduplicate chapter URLs before scheduling another window.
Rethrow parent cancellation immediately.
A request/decode failure, blank page, or page adding zero previously unseen URLs terminates the contiguous result.
Ignore results after that terminal page, even if later requests in the same window already completed.
Schedule no later window. At most two avoidable requests can complete after a terminal page.
Retain the existing detail-page chapter-list fallback.
This preserves final ordering/identity while reducing serial latency without an unbounded request burst.

Unbounded `async` was rejected because large novels could create throttling-inducing bursts.
Three-request windows trade greater short-term pressure for lower latency with a fixed ceiling.
Migration inspection reports the source still fetches the remainder serially.
The active verification report's phrase “already implemented” describes sequential AJAX with deduplication and does not establish this concurrency requirement.

## Verification obligations and review findings

Deterministic cache tests need a controllable monotonic clock for exact five/fifteen/two-minute TTLs and 100/50/20 capacities.
Cover expiry, LRU order, key/source/query/page isolation, immutable ordered filters, wraparound, and exclusion of failed/fallback results.
Browse tests need actual job cancellation, debounce, blank clearing, source switching, stale completion, cached results, and pagination context.
Repository tests cover empty/failed page-one fallback, selected-source isolation, failure propagation with no matches, and no later-page fallback.
Library tests cover provider order, one eligible snapshot, no per-hit lookups, failure/unavailability, metadata fields, title-first order, eligibility, and no-match success.
ViewModel tests include input debounce/cancellation, fallback display, immediate active-search invalidation, and existing blank-query behavior.
FreeWebNovel MockEngine tests need controlled deferred responses to prove three-request windows and request counts.
Cover page-order merge, URL deduplication, parent cancellation, zero-unseen-page stop, blank/decode/request failures, ignored same-window results, and contiguous partial results.

Four test-first slices were cache, Browse/fallback, Library/snapshot, and FreeWebNovel concurrency, with no declared TDD exceptions.
Reviews after each slice required fixing or explicitly disposing findings before proceeding.
Reported review fixes were monotonic-clock wraparound, key collisions, missing cache test coverage, uncanceled request jobs, and excluding non-Library candidates.
The historical report claims every finding resolved; independent coverage and implementation gaps limit that claim.
Targeted cache, `SeriesRepositoryImplTest`, `ChapterRepositoryImplTest`, Browse/Library ViewModel, `TitleMatcherTest`, and FreeWebNovel tests were planned.
Full unit tests, debug assemble, lint, and affected connected Browse/Library Compose tests when an emulator was available completed the intended gate.
Historical test results are in [history](../evidence/ui-source-history.md#performance-verification-and-overclaims), rather than new migration test results.
