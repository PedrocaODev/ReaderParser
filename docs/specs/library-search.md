---
id: specs-library-search
title: "Library search and eligible local fallback"
type: Concept
status: active
valid_from: 2026-10-08
scope:
  system: readerparser
relations:
  - type: supported_by
    target: ../evidence/specification-evolution.md
---

# Library search and eligible local fallback

Samsung-first search, eligible local fallback, and batch row resolution exist in current code.
The 300 ms Library debounce and title-first fallback ranking remain proposed gaps in the unarchived optimization.

## Samsung-first query

For a nonblank Library query, prefer `ContentResolver.query()` at
`content://com.samsung.android.smartsuggestions.search/v2/com.opus.readerparser.series`.
The projection includes at least `_id`, `title`, and `source_url`.
On provider success, use provider results rather than local fallback ranking.
Blank input immediately shows the normal observed Library list.

## Eligibility and ordered local resolution

Display only hits resolving to local rows that are in Library and have at least one downloaded chapter.
A matching row outside Library or without downloaded/indexable chapters must remain hidden.
Display the local row's canonical title and cover, even when the provider's stored display title differs.
Preserve provider relevance order among eligible resolved rows.
Load one eligible candidate snapshot and resolve all hits by `(sourceId, url)`.
Do not issue a Room lookup for each provider hit.

## Eligibility invalidation

When Library membership or downloaded-chapter indexability changes, rerun the current nonblank query.
If a series loses its last downloaded chapter, remove its now-ineligible hit.
The proposed typing debounce must not delay data-driven invalidation. Rerun immediately and reject the superseded result set.

## Provider fallback

When Samsung Search is unavailable or its query fails, search one local snapshot of eligible rows.
Match normalized title, author, or genres. Matching ineligible rows remain excluded.
If no eligible row matches, return a successful empty result and the normal no-matches state.
Provider failure must not disable usable local search or create a provider error when local fallback can continue.
The previous distinct-provider-error requirement is recorded as historical context.

## Proposed typing debounce and cancellation

This subsection describes intended work that is absent from current Library debounce behavior.
Wait until user-entered nonblank input remains unchanged for 300 ms before provider search or local fallback.
New input cancels pending debounce and active superseded search work.
Only the latest stable query starts. Reject earlier responses even if cancellation completes late.
Blank input cancels pending or active search work and immediately restores the normal observed list.
Eligibility changes rerun the current nonblank query immediately, without another typing debounce.

## Proposed title-first fallback ranking

Prioritize local title matches over matches only in author or genres.
This ordering is intended but is not implemented by the current fallback ranking.
