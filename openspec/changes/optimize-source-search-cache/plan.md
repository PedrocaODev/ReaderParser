## Implementation slices

### Slice 1: Bounded metadata cache

**Tasks:** 1.1–1.3
**Tests first:** Add deterministic unit tests using a controllable monotonic clock for exact hits, five/fifteen/two-minute expiry, 100/50/20 LRU capacities, immutable ordered filter snapshots, key isolation, and unsuccessful-result exclusion before introducing the smallest repository-facing cache implementation and applying it to metadata operations.
**TDD exception:** none

### Slice 2: Browse live search and fallback

**Tasks:** 2.1–2.5
**Tests first:** In `BrowseViewModelTest`, prove debounce, job cancellation, source changes, blank input, late-response rejection, cached results, and pagination context. In `SeriesRepositoryImplTest`, prove selected-source page-one fallback and error/no-fallback boundaries before changing state, repository orchestration, and Compose interactions.
**TDD exception:** none

### Slice 3: Library fallback and batch resolution

**Tasks:** 3.1–3.4
**Tests first:** Extend DAO/repository fakes and tests to prove a single eligible candidate snapshot, provider-order mapping, unavailable/failed provider fallback, metadata matching, eligibility, and no-match success. Add ViewModel debounce/cancellation/invalidation cases before changing production flow.
**TDD exception:** none

### Slice 4: FreeWebNovel chapter-list concurrency

**Tasks:** 4.1–4.3
**Tests first:** Use `MockEngine` with controlled deferred responses to prove three-request windows, page-order merge, URL deduplication, cancellation, zero-unseen-URL termination, ignored later same-window results, request counts, and contiguous partial results before replacing the serial remainder loop.
**TDD exception:** none

## Review checkpoints

- After Slice 1: Review cache synchronization, monotonic time use, bounded eviction, operation keys, and ownership boundaries; fix or explicitly disposition every finding before Slice 2.
- After Slice 2: Review Browse cancellation, source isolation, cache reuse, fallback/error distinction, pagination, and UI state; fix or explicitly disposition every finding before Slice 3.
- After Slice 3: Review Samsung-first behavior, eligible local snapshot semantics, ordering, fallback ranking, and active-search invalidation; fix or explicitly disposition every finding before Slice 4.
- After Slice 4: Review request concurrency pressure, cancellation, ordering, deduplication, and partial-result compatibility; fix or explicitly disposition every finding before final verification.

## Final verification intent

- Run targeted cache, `SeriesRepositoryImplTest`, `ChapterRepositoryImplTest`, `BrowseViewModelTest`, `LibraryViewModelTest`, `TitleMatcherTest`, and `FreeWebNovelTest` cases.
- `ANDROID_HOME=/home/pedro/Android/Sdk ANDROID_SDK_ROOT=/home/pedro/Android/Sdk ./gradlew :app:testDebugUnitTest --console=plain`
- `ANDROID_HOME=/home/pedro/Android/Sdk ANDROID_SDK_ROOT=/home/pedro/Android/Sdk ./gradlew :app:assembleDebug --console=plain`
- `ANDROID_HOME=/home/pedro/Android/Sdk ANDROID_SDK_ROOT=/home/pedro/Android/Sdk ./gradlew :app:lintDebug --console=plain`
- If an emulator is available, run affected Browse and Library Compose instrumentation tests with `:app:connectedDebugAndroidTest`.

## Intended commit grouping

- Commit 1: `feat:` add bounded source metadata caching.
- Commit 2: `feat:` make Browse search live with source-local fallback.
- Commit 3: `feat:` add local fallback for unavailable Samsung Search.
- Commit 4: `refactor:` parallelize FreeWebNovel chapter-list requests.
