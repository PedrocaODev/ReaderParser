---
id: specs-source-pagination
title: "Source listing and chapter pagination"
type: Concept
status: active
valid_from: 2026-10-08
scope:
  system: readerparser
relations:
  - type: supported_by
    target: ../evidence/specification-evolution.md
---

# Source listing and chapter pagination

Baseline pagination requirements and proposed concurrency are distinguished below.

## Browse end-of-list loading

When Browse reaches the end and needs more content with `hasNextPage=true`, automatically request the next page.
Retain the manual Load more fallback when supported.
A terminal result reports `hasNextPage=false` and has no Load More eligibility.

## Page-specific listings

Paged source listings must construct requests for the requested page and parse that page's content.
FreeWebNovel latest page one and page two use distinct URLs. Page two cannot duplicate parsed page-one content.
AsuraScans popular and search page two use the next-page URL for that mode and return page-two fixture/content results.
AsuraScans latest and popular page one use distinct URLs and yield distinct results.

## Single-page and terminal modes

FreeWebNovel popular remains terminal even when requested beyond page one.
FreeWebNovel search pages one and two use the same search URL, parse the same single-page result set, and both report `hasNextPage=false`.
FreeWebNovel latest reports `hasNextPage=false` at its final page.
AsuraScans latest is homepage-only. A request beyond page one is terminal/non-paged and expects no separate page-two parsing.
It has no Load More eligibility unless product semantics change.

## Complete chapter aggregation

FreeWebNovel aggregates all chapters across internally paginated chapter-list pages.
`Source` provides `fetchChapterListProgressive(series): Flow<List<Chapter>>`, defaulting to emitting `getChapterList(series)`.
`FreeWebNovel` implements progressive chapter list emission for responsive series presentation. `getChapterList(series)` delegates to `fetchChapterListProgressive(series).last()`.

## Progressive chapter loading and bounded concurrency

FreeWebNovel fetches AJAX Page 1 (`pageSize=200`) and emits immediately to display initial chapters without waiting for the full catalog.
It then fetches remaining pages `2..totalPage` concurrently with concurrency bounded by `Semaphore(4)`.
Parent coroutine cancellation terminates all outstanding requests cleanly without partial corrupted emissions.

## Deterministic ascending merge

Merge concurrent responses in strictly ascending page order with deduplication by chapter URL.
Emissions maintain cumulative ascending chapter lists.
A failed, undecodable, or blank page stops further pagination while retaining all successfully fetched prior chapters.
