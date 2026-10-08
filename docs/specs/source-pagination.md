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

FreeWebNovel must aggregate all chapters across internally paginated chapter-list pages.
The `Source` contract stays unchanged.

## Proposed bounded concurrency

Current FreeWebNovel chapter-page fetching is sequential. The following optimization remains proposed and absent from current code.
Keep page one as the discovery request. When it identifies remaining AJAX pages, fetch ascending windows of at most three concurrent requests.
Multiple remaining pages may run concurrently, but no more than three remainder-page requests may be active.
Parent cancellation cancels outstanding requests and must not become partial success.

## Proposed deterministic contiguous merge

Merge concurrent responses in ascending source-page order, even when later pages finish first.
Deduplicate chapters by URL, retaining each URL once across adjacent pages.
A failed, undecodable, blank, or non-progressing page terminates the successful contiguous prefix at that page.
Non-progressing means the page adds zero previously unseen chapter URLs, including a page containing only duplicate URLs.
Ignore later responses in that three-request window and schedule no later window.
If a middle page fails while later pages finish, append only successful pages before it.
If a terminal page occurs inside a window, ignore already-completed later results.
Avoidable requests after that terminal page are limited to the other requests already in that window.
