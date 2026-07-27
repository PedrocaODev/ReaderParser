## Context

The shared OkHttp client already has a 50 MB disk cache, but HTTP reuse depends on each server's cache headers and does not define application-level freshness for parsed domain results. Room persists series discovered through catalog requests, but it does not preserve query/page ordering or freshness. `DownloadStore` and Coil separately own downloaded chapter payloads and images.

Browse currently searches only after explicit submission. Request identities prevent late responses from replacing current state, but superseded coroutines continue consuming network and parser work. Repository fallback runs only after an empty successful page-one response. Library delegates every nonblank query to Samsung Search; provider failure becomes an error, and each successful hit performs a separate Room lookup. FreeWebNovel learns its chapter-list page count from page one and then fetches every remaining page serially.

## Goals / Non-Goals

**Goals:**

- Start source-specific Browse search automatically after a short debounce for each nonblank query.
- Cancel superseded debounce and network work and reject any response that still completes late.
- Reuse only exact, fresh metadata request results with bounded memory use and deterministic expiry.
- Use locally persisted rows from only the selected source when remote Browse search is empty or unavailable.
- Keep Samsung Search as the preferred Library backend while making Library search functional without it.
- Remove per-hit Room lookups from successful Samsung result resolution.
- Reduce FreeWebNovel multi-page chapter-list latency without changing final ordering or identity.

**Non-Goals:**

- Persistent catalog query snapshots, offline catalog browsing, a Room schema migration, or a new cache dependency.
- Caching every opened chapter payload; explicit downloads remain the reader-content cache.
- Cross-source Browse search, speculative prefetching, search filters, or changing source-side ranking.
- Changing the `Source` interface, source IDs, series identity, global timeout policy, or HTTP retry policy.
- Replacing Samsung Search when it is healthy or changing its external index lifecycle.

## Decisions

### Decision: Cache parsed metadata at repository boundaries

Repositories use synchronized access-order maps with a monotonic time source, explicit maximum entry counts, and TTLs. Catalog keys include source ID, operation, normalized query, page, and an immutable ordered snapshot of `FilterList`. Filter order remains significant, matching the source request input exactly and preferring a safe cache miss over assuming two reordered requests are equivalent. Detail and chapter-list keys use `(sourceId, seriesUrl)`. Content type is not part of cache policy because source identity already isolates NOVEL and MANHWA sources.

Fresh exact hits return without a source call. Expired entries are removed before a remote request. Only successful remote results are cached; a local fallback is not cached as remote truth. Catalog/search caches retain at most 100 entries for five minutes, detail caches retain at most 50 entries for fifteen minutes, and chapter-list caches retain at most 20 entries for two minutes. The memory ceiling is deliberately entry-based; a byte-weighted cache is deferred until measured chapter-list sizes justify its added complexity.

The existing OkHttp cache stays enabled. Ktor `HttpCache` is not added because it would duplicate transport caching, and reader payloads are not written into `DownloadStore` unless explicitly downloaded.

### Decision: Drive Browse search from debounced input

Each nonblank query schedules a 300 ms debounce. New input, source changes, or mode changes cancel the pending debounce and active request job. Request identity remains as a final stale-response guard because cancellation is cooperative. Blank input clears search results without a source request.

Changing the selected source while a nonblank Search query is visible clears results from the previous source and schedules the same query for the new source. Search pagination retains the submitted source and normalized query. A fresh exact cache hit follows the same state transition as a remote success.

### Decision: Use persisted source rows as a bounded fallback, not a replacement index

For page one only, an empty successful remote search or a remote failure checks Room rows for the selected source and applies normalized title matching. A nonempty local match set is returned with no next page. A remote failure with no local matches continues to the UI as an error so retry remains available. Later pages never fall back because local rows cannot represent remote pagination.

This reuses the existing `TitleMatcher` and persisted series rows. A Room FTS table is rejected until the local corpus demonstrates a measured need for schema and synchronization overhead.

### Decision: Resolve Library search from one eligible local snapshot

For each nonblank Library query, the repository loads the rows that are both in Library and indexable by downloaded chapters once. When Samsung Search succeeds, those rows are indexed by `(sourceId, url)` in memory and provider hits are mapped in provider order. When Samsung Search fails or is unavailable, the same eligible snapshot is filtered locally across title, author, and genres with title matches ranked first.

The repository attempts the provider query directly and falls back on its failure result; it does not perform an extra availability probe for every keystroke. User-entered Library input uses a 300 ms debounce and cancels superseded work so ContentProvider queries are not started for every intermediate character. Existing active-search invalidations rerun the current nonblank query immediately because they represent changed local eligibility rather than new typing.

### Decision: Parallelize only the known remainder of FreeWebNovel chapter lists

Page one remains the authoritative discovery request. Pages `2..totalPage` are processed in ascending windows of at most three concurrent requests. Each completed window is merged in page order and deduplicated by chapter URL before the next window is scheduled. Cancellation is rethrown immediately. A failed or undecodable page, a blank page, or a page that adds zero previously unseen chapter URLs terminates the contiguous result; later pages in the same window may have completed but are ignored, and no later window is scheduled. This bounds avoidable requests after a terminal page to two.

## Alternatives Rejected

- **Add a persistent query-cache table:** rejected because it requires a migration, invalidation rules, and stored page ordering before offline catalog browsing is required.
- **Install Ktor `HttpCache`:** rejected because the OkHttp engine already owns transport caching.
- **Use unbounded `async` for chapter pages:** rejected because large novels could create a burst of requests and trigger source throttling.
- **Return local fallback after every remote error, including an empty fallback:** rejected because it would hide failures and remove the user's retry path.
- **Search every source while typing:** rejected because Browse remains source-specific and cross-source fan-out would multiply traffic.
- **Add Room FTS immediately:** rejected because eligible Library and per-source cached sets are expected to be small and can be scanned without schema complexity.

## Risks / Trade-offs

- In-memory cache entries disappear with the process and intentionally do not provide offline catalog history.
- Short TTLs can briefly show stale metadata; bounded expiry limits this without adding explicit invalidation machinery.
- ContentResolver cancellation may not stop provider work already inside the platform, but debounce prevents most obsolete calls and request generation prevents stale display.
- Parallel FreeWebNovel requests reduce latency but increase short request bursts; the concurrency bound limits that pressure.
- Local fallback quality depends on previously discovered Room rows and cannot find titles the app has never fetched.

## Testing Expectations

- Cache tests cover exact hits, expiry, eviction, source/query/page isolation, and exclusion of failed or fallback results.
- Browse ViewModel tests cover debounce, actual job cancellation, blank queries, source switching, cache reuse, fallback, pagination, and stale responses.
- Repository tests cover source-specific local fallback after empty and failed page-one searches, error propagation when fallback is empty, and no fallback on later pages.
- Library tests cover provider success order, one eligible local snapshot, unavailable/failed provider fallback, field matching, and eligibility restrictions.
- FreeWebNovel MockEngine tests cover bounded multi-page requests, ordered deduplication, cancellation, and partial-result behavior.
