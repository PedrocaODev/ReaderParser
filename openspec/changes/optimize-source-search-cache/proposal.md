## Why

Browse search waits for explicit submission, obsolete source requests continue running after newer input supersedes them, and repeated metadata requests have no deterministic application-level freshness policy. Library search also becomes unusable when Samsung Search is unavailable, while successful provider results are resolved through one Room lookup per hit. FreeWebNovel chapter lists compound latency by fetching every internal AJAX page sequentially.

## What Changes

- Make Browse search source-specific and automatic after a short input debounce, cancelling superseded work rather than only ignoring late responses.
- Reuse fresh, exact metadata request results from a bounded in-memory TTL cache shared across NOVEL and MANHWA repository flows.
- Fall back to source-specific locally persisted title matches when a page-one Browse search is empty or its remote request fails.
- Fall back to performant local Library matching when Samsung Search is unavailable or its query fails, while preserving provider ordering when Samsung Search succeeds.
- Resolve successful Samsung Search hits against one eligible local snapshot instead of issuing one Room query per hit.
- Fetch FreeWebNovel chapter-list pages with bounded concurrency after the first page reveals the total page count, preserving ordered and deduplicated output.

## Capabilities

### New Capabilities

- `source-metadata-cache`: catalog pages, search pages, series details, and chapter lists use bounded in-memory TTL caching independent of content type.
- `source-request-performance`: multi-page FreeWebNovel chapter-list retrieval avoids serial request latency while preserving result semantics.

### Modified Capabilities

- `library-browse-catalog`: Browse replaces explicit submission with debounced source-specific search while retaining progress, recovery, stale-response protection, pagination, and navigation.
- `library-search-via-samsung-search`: Samsung Search remains preferred, but unavailable or failed provider queries fall back to eligible local Library matching and successful hits are resolved without N+1 Room lookups.

## Impact

- **Presentation:** Browse search state and tests change from explicit submission to debounced input-driven requests with actual cancellation.
- **Data:** `SeriesRepositoryImpl`, `ChapterRepositoryImpl`, and `SeriesDao` gain cache/fallback coordination. Existing Room rows remain the local fallback source; no schema migration is required.
- **Sources:** FreeWebNovel chapter-list orchestration changes, but source parsing, source identity, and the `Source` interface remain unchanged.
- **Compatibility:** Series identity remains `(sourceId, url)`. Downloaded reader content remains owned by `DownloadStore`; image caching remains owned by Coil.
- **Dependencies:** No new third-party dependency is introduced.
