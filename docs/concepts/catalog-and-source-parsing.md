---
id: concept-catalog-and-source-parsing
title: "Catalog presentation and source parsing"
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
    target: ../specs/catalog-presentation.md
  - type: relates_to
    target: ../specs/browse-discovery.md
  - type: relates_to
    target: ../specs/source-pagination.md
---

# Catalog presentation and source parsing

This node incorporates rationale and conditions from catalog and pagination work. Intended behavior and historical claims remain distinct from migration-time observations.

## Title authority and bookmark repair

Source detail parsers own title extraction before persistence. Prefer a nonblank detail-page title over the incoming title.
Use the incoming title only when extraction is blank or unavailable and the incoming title is nonblank.
A blank incoming title is not a usable fallback. This avoids a persistence-only workaround for source-specific markup.

For an existing blank-title bookmark, use the existing source-detail refresh and persistence flow.
Persist a successful repair while preserving `(sourceId, url)` and other bookmark data.
If retrieval, response handling, or parsing fails, retain the existing bookmark unchanged.
Record attempted bookmark identities for the current Library screen lifecycle.
Attempt each blank bookmark at most once in that lifecycle. A new lifecycle may retry it.
This bounds repeated requests when a source is unavailable without promising continuous repair while the screen remains open.

Rejected alternatives were persisting an incoming title before parsing, a schema field/migration, and a background or bulk repair job.
The parsed title is more authoritative, and the existing refresh flow meets the bounded repair requirement.
Failed repairs can leave a title blank until a later lifecycle. Markup drift remains a source-specific risk.

## Shared adaptive catalog

Library and Browse share one cover-first series card with cover, readable title, and the caller's existing series interaction.
Use an adaptive grid/container at compact and expanded widths rather than a fixed device-specific column count.
Use Material colors, typography, shape, and spacing so the catalog follows the current theme.
Separate cards were rejected because duplicated presentation can drift.
Responsive cards trade stable list density for usable sizing. Compact-width tests must cover title readability and available actions.

Retain Library sorting, text search, bookmark removal, and Samsung Search behavior.
Remove the inert Unread control and unused presentation/state because unread semantics were never defined.
Implementing an Unread filter was outside scope.
Retain Browse source selection, series-detail navigation, pagination, and empty-query meaning.
Series detail redesign, database identity/schema changes, Source changes, dependencies, and navigation redesign were outside the catalog refactor.

## Browse discovery and request identity

Browse load-more increments pages and guards `isLoading` and `hasNextPage`.
Reaching the end of results should dispatch `LoadMore` automatically while retaining the manual Load more footer as fallback.
An over-eager viewport sentinel can over-trigger. Existing ViewModel guard tests were an explicit test-first exception for this UI slice.

Historical July 10 catalog work required explicit search submission.
Mode/source changes without a submitted query made no blank-query request. Submissions used the selected source and mode.
Loading and retry feedback retained the submitted request context. Query/source/mode/page identity rejected a slower obsolete response.
Fetching a blank query on a mode change was rejected as unrequested network work.
Migration source inspection reports later 300 ms debounced input-driven Browse search, described in [metadata performance](./source-metadata-performance.md#browse-live-search-and-local-fallback).
The explicit-only policy is historical, rather than current routing guidance.

The July 27 defect was different: initial Popular browsing selected the first source but never fetched it.
`autoFetchCurrentMode` already worked for source and mode actions. Historical shipped behavior calls it after initial source selection.
The initial-fetch test must assert Popular fetching instead of preserving `init loads sources and selects first source without fetching`.
Fetching Popular on entry does not imply searching a blank query.

## Source pagination contracts

| Source and operation | Page-two behavior retained by the pagination change |
| --- | --- |
| FreeWebNovel popular | Intentionally single-page and terminal. |
| FreeWebNovel latest | Page-aware, with distinct page-two results. |
| FreeWebNovel search | Intentionally single-page and terminal. |
| FreeWebNovel chapter list | Aggregate all discovered pages. |
| AsuraScans popular | Page-aware, with distinct page-two results. |
| AsuraScans search | Page-aware, with distinct page-two results. |
| AsuraScans latest | Homepage-only, terminal after page one. |

Prove AsuraScans `latest(1)` and `popular(1)` request different URLs and parse different results.
Change a production parser for that split only when regression evidence shows drift.
Do not broaden intentionally terminal operations or add generic pagination behavior to `HtmlSource`.
Keep domain, Source, repository, and ViewModel contracts stable. Schema, permission, and dependency changes were excluded.
URL-specific stubs and distinct fixtures must distinguish page one, page two, and terminal responses.
Returning the same fixture for every request can hide a pagination defect.

## FreeWebNovel complete chapter lists

Historical shipped retrieval uses AJAX `pageSize=200` and discovers `totalPage` from the first response.
Aggregate chapter pages without changing Source semantics for sources that already expose all chapters on one page.
If the first AJAX page fails, use the detail-page HTML chapter list as fallback.
Stop on malformed later pages rather than making an unbounded fetch loop. Preserve cancellation instead of converting it to a successful fallback.
Bound retrieval to avoid duplicate chapters and infinite requests.
Later HTML shape can drift and truncate aggregation. Preserve selector and fixture coverage when changing this source.
The retrospective still reported some slowness and recommended performance investigation on fresh slowdown reports.
The later proposed three-request windows have distinct requirements and an unresolved implementation gap in [performance](./source-metadata-performance.md#freewebnovel-bounded-remainder-proposal).

## AsuraScans complete image pages

The original scroll defect combined incomplete page discovery and image items that collapsed while loading or failing.
`div[data-page="N"] img` in server-rendered HTML can omit Astro/JavaScript-lazy-loaded images because Jsoup does not execute JavaScript.
That explanation was a hypothesis in the proposal, not a measured guarantee for every chapter.

The durable integration direction is API-first complete page discovery with HTML fallback if API retrieval or interpretation fails.
The design hypothesized `https://api.asurascans.com/api/novels/{slug}/download`.
Historical tasks, retrospective, and migration source inspection instead identify `/api/series/{series_slug}/chapters/{chapter_slug}`.
Do not promote the hypothesized endpoint into an accepted contract.
The old HTML parser remains necessary for fallback, including old chapters with UUID slugs that do not match constructed `chapter-{N}` slugs.
API width/height metadata was noted for possible future enhancements, not required rendering behavior.

Check format and complete page lists on sample chapters before relying on the API.
Format changes, authentication changes, or unreliability can trigger fallback.
If an API does not work for all chapters, HTML metadata/attribute enhancement was the alternative, with scope reconsideration if needed.
This work kept Source/HtmlSource contracts, reader data flow, and novel reading unchanged.
Image performance optimization beyond repairing scrolling was excluded.

## Test and review obligations

Parser tests cover extracted title preference, nonblank fallback, and rejection of blank fallback.
The plan named `getSeriesDetails prefers extracted detail title`, `getSeriesDetails falls back to nonblank incoming title`, and `getSeriesDetails does not use blank incoming title`.
Repository/ViewModel tests cover persisted successful repairs, each failure boundary, identity/data preservation, once-per-lifecycle attempts, and new-lifecycle retries.
Named lifecycle cases were `blank bookmark is attempted once per lifecycle` and `blank bookmark may be retried in a new lifecycle`.
Compose tests cover shared card actions, readable compact/expanded layouts, absent Unread, and retained Library interactions.
Browse tests cover loading/retry, source and pagination context, navigation, stale identity, auto-load, and clickable manual fallback.
Source tests use separate page fixtures, explicit URL/path assertions, complete aggregation, and Asura latest/popular separation.

Catalog work planned five test-first slices: title parsing, repair, adaptive card/grid, Unread removal, and explicit search.
Reviews after repair, UI cleanup, and Browse orchestration required fixing or explicitly disposing each finding before the next dependent slice.
Pagination work used three ordered slices: Browse auto-load, FreeWebNovel aggregation, and Asura latest/popular regression proof.
Review each slice for minimality, completeness, contract stability, and whether parser changes are actually necessary.
Asura scroll work put initial Browse fetch first, measurable image items second, and API/HTML page completeness third.
The image test could use snapshot measurements or manual device verification if controllable loading-state tests were infeasible.
Historical verification outcomes and their limits are incorporated in [history](../evidence/ui-source-history.md).
