## 1. Metadata cache foundation

- [x] 1.1 Add focused tests for the exact five/fifteen/two-minute TTLs, 100/50/20 entry capacities, monotonic expiry, LRU eviction, immutable ordered filter snapshots, request-key isolation, and failed/fallback result exclusion.
- [x] 1.2 Implement the minimum bounded in-memory TTL cache needed by repository metadata requests without adding a dependency or persistent schema.
- [x] 1.3 Apply cache keys and operation TTLs to catalog/search pages, series details, and chapter lists while leaving reader payload ownership unchanged.

## 2. Browse live source search

- [x] 2.1 Add Browse ViewModel tests for 300 ms debounce, blank-query clearing, cancellation of superseded debounce/request jobs, and late-response rejection.
- [x] 2.2 Add tests proving source changes clear old-source results and rerun a nonblank query only for the newly selected source.
- [x] 2.3 Add repository tests for fresh exact search cache hits, source-specific local fallback after empty or failed page one, failure propagation with no local matches, and no fallback after page one.
- [x] 2.4 Replace explicit-only Browse submission with debounced input-driven search while preserving loading, retry, selected-source pagination, and stale-response guards.
- [x] 2.5 Update Browse Compose tests and content interactions for live search without removing keyboard/search affordances or pagination feedback.

## 3. Library search fallback and resolution

- [x] 3.1 Add DAO/repository tests for loading one eligible Library/indexable snapshot and resolving multiple successful provider hits in provider order without per-hit lookups.
- [x] 3.2 Add fallback tests for unavailable/failed Samsung Search, normalized title/author/genre matching, title-first ranking, eligibility restrictions, and no-match success.
- [x] 3.3 Add Library ViewModel tests for input debounce, cancellation, fallback display, active-search invalidation, and preserved blank-query behavior.
- [x] 3.4 Implement batch local resolution and provider-failure fallback without changing Samsung Search registration, sync, or external index lifecycle.

## 4. FreeWebNovel request latency

- [x] 4.1 Add MockEngine tests proving remaining chapter-list pages run in ascending windows of at most three requests while final chapters remain page-ordered and URL-deduplicated.
- [x] 4.2 Add tests for parent cancellation, blank pages, pages adding zero unseen URLs, decode/request failure, contiguous partial results, ignored later same-window responses, and no later-window scheduling after termination.
- [x] 4.3 Parallelize only pages known after page-one discovery in three-request windows and preserve current fallback to the detail-page chapter list.

## 5. Review and verification

- [x] 5.1 Review cache ownership, cache keys, expiry, cancellation, and local-fallback behavior; resolve every finding.
- [x] 5.2 Review Samsung eligibility/order semantics and FreeWebNovel request pressure/partial results; resolve every finding.
- [x] 5.3 Run targeted repository, Browse, Library, cache, TitleMatcher, and FreeWebNovel tests.
- [x] 5.4 Run `./gradlew :app:testDebugUnitTest`.
- [x] 5.5 Run `./gradlew :app:assembleDebug`.
- [x] 5.6 Run `./gradlew :app:lintDebug`.
