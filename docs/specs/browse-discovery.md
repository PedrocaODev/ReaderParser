---
id: specs-browse-discovery
title: "Browse discovery and search"
type: Concept
status: active
valid_from: 2026-10-08
scope:
  system: readerparser
relations:
  - type: supported_by
    target: ../evidence/specification-evolution.md
---

# Browse discovery and search

The current 300 ms Browse behavior is verified separately from the older explicit-submit specification.
This node preserves requirements and current context. It does not declare the complete unarchived optimization implemented.

## Initial popular discovery

When sources load on Browse initialization, select the first available source and `BrowseMode.POPULAR`.
Immediately fetch popular series using that source ID. Keep `isLoading=true` until completion and populate `series` with results.
When the source list is empty, select no source, trigger no fetch, and retain an empty grid.
The regression test must assert `seriesRepository.fetchPopular` receives the first source ID.
The historical test name transition is in [Specification evolution](../evidence/specification-evolution.md#initial-discovery-and-reader-stability).

## Live source-specific Search

In Search mode with a selected source, nonblank input unchanged for 300 ms starts a page-one request from that source.
Blank or whitespace-only input clears Search results without a source search request.
The keyboard Search action may start the same current query immediately. It must not create different search semantics.
When source B replaces source A with a nonblank query, clear A's results and schedule that query only for B.
Displayed results belong only to the selected source, including cached and locally persisted rows.
Popular initialization is a separate mode from blank Search input.

## Request progress and recovery

Show loading feedback while the current debounced or immediate request runs.
If the remote request fails without local fallback matches, show the remote error and Retry.
Retry must reissue the same source, normalized query, page, and immutable ordered filter snapshot.
When nonempty local fallback exists, show its rows instead of replacing them with an error-only state.

## Cancellation and stale responses

New query input, source selection, or mode selection cancels pending debounce and active superseded requests.
Only the latest stable query starts after input changes during debounce.
Cancel source A's active request when B becomes selected.
An earlier query, source, mode, or page response must not replace current state, even when cancellation completes late.
This includes an earlier query finishing after the later query and a stale page response completing after context changes.

## Exact search reuse

A fresh successful search page may satisfy only an exactly equal key: source, normalized query, page, and immutable ordered filters.
Before TTL expiry, show that cached page without another source call.
A different source, query, page, or ordered filter snapshot cannot reuse that entry as an exact result.
The cache contract and limits are in [Source metadata](source-metadata.md).

## Page-one local fallback

When a nonblank page-one search fails or succeeds with no rows, search persisted rows from the selected source.
Nonempty local matches form a terminal page with no next page. Remote failure with matches displays those rows.
Remote failure without matches remains an error with Retry.
An empty or failed page after page one must not substitute the source's complete local row set.

## Catalog and pagination continuity

Retain source selection, adaptive cards, keyboard Search, and existing series-detail navigation.
When the current search advertises another page, use the same source, normalized query, and ordered filter snapshot.
Append only that request's results. Preserve Library sort, search, removal, and Samsung-first ordering with local-row display resolution.
Older explicit-submit and provider-error semantics remain historical evidence, not current Browse instructions.
