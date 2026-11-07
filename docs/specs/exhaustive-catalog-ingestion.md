---
id: specs-exhaustive-catalog-ingestion
title: "Exhaustive Catalog Ingestion for FreeWebNovel"
type: Concept
status: active
valid_from: 2026-11-06
scope:
  system: readerparser
relations:
  - type: supported_by
    target: ../adr/0001-project-knowledge-vault.md
  - type: relates_to
    target: ./source-pagination.md
  - type: relates_to
    target: ./browse-discovery.md
---

# Exhaustive Catalog Ingestion for FreeWebNovel

## Problem Statement

When browsing or searching webnovels from FreeWebNovel in the app, users are unable to discover or search for thousands of titles hosted on the site (such as *A Regressor's Tale of Cultivation*). FreeWebNovel's popular listing only provides a single terminal page of 50 titles, latest updates only reflect recently published chapters, and search does not support pagination. Because unlisted novels are never returned by these listings, they never enter local persistence and cannot be found via search, leaving over 90% of the site's catalog inaccessible to readers.

## Solution

Introduce an asynchronous catalog harvesting architecture that systematically enumerates the complete directory of novels from FreeWebNovel and persists lightweight series stubs directly into the local database with `inLibrary = false`. 

Harvesting runs in the background via WorkManager under unmetered Wi-Fi and idle device constraints, politely pacing requests to prevent site rate-limiting. A native Room SQL search engine allows users to instantly search the entire ingested catalog offline with zero latency, while fetching full descriptions and chapter lists strictly on-demand when a series is opened or bookmarked.

## User Stories

1. As a reader searching for webnovels, I want every novel hosted on FreeWebNovel to be discoverable in the app, so that I can read niche or older titles that are not featured on the top-50 popular list.
2. As a reader searching in the catalog, I want search queries to return results instantaneously from my local device, so that I experience zero loading spinners or network lag while typing.
3. As a mobile reader on a metered data plan, I want catalog syncing to occur only when connected to unmetered Wi-Fi and charging, so that my mobile data and battery are never consumed by background scraping.
4. As a reader with intermittent network connectivity, I want catalog search to function completely offline once synced, so that I can discover and queue titles even in airplane mode.
5. As a reader who selects a novel from catalog search, I want the full description and chapter list to be fetched on-demand, so that my device storage is not burdened by thousands of chapters for unread novels.
6. As a reader managing my collection, I want to add any discovered novel to my Library with one tap, so that it is tracked alongside my other reading material without duplicating database rows.
7. As a reader with existing bookmarks, I want catalog updates to refresh novel metadata without modifying my bookmark status, reading progress, or download state, so that my reading history is preserved.
8. As a reader whose background sync is interrupted by network loss or battery constraints, I want each page's progress saved immediately to disk, so that the sync resumes without losing previously fetched titles.
9. As a frequent reader, I want routine sync runs to terminate early once they encounter already-indexed titles, so that the app does not waste bandwidth re-fetching historical directory pages.
10. As a reader searching within a specific source, I want search results to strictly contain titles belonging to that source, so that titles from different websites are not mixed together.
11. As a reader searching for titles with punctuation or partial phrases, I want the search engine to match titles accurately, so that minor punctuation discrepancies do not prevent me from finding a series.
12. As a reader with limited device storage, I want thousands of catalog titles to occupy less than two megabytes of disk space, so that my phone storage remains free for downloaded chapters.
13. As an active reader, I want background catalog syncing to run silently without modal popups or blocking dialogs, so that my active reading sessions are never interrupted.
14. As a reader opening an extensive novel, I want the chapter list to load progressively, so that I can begin reading the first chapter immediately while remaining chapters load in the background.
15. As a reader who removes a series from my Library, I want the series stub to remain in the local catalog cache, so that I can easily find and re-add it in the future without re-scraping the web.

## Implementation Decisions

### 1. Catalog Harvester Plugin Seam
- Create a dedicated plugin seam:
  ```kotlin
  interface CatalogHarvester {
      suspend fun harvestCatalog(): Flow<List<Series>>
  }
  ```
- FreeWebNovel implements `CatalogHarvester` independently of the core `Source` contract, keeping standard browsing and searching contracts untouched.
- Traversal sequentially crawls the `/sort/latest-novel/{page}` directory, extracting series cards (title, URL, cover URL, and status).
- Enforces polite pacing (300–500ms delay between pages) and terminates when no next-page link (`>>`) is present.

### 2. Domain Sync Orchestration (`SyncSourceCatalog` Use Case)
- Encapsulates crawling coordination in a deep domain usecase:
  ```kotlin
  suspend operator fun invoke(sourceId: Long, fullSync: Boolean = false): Flow<SyncProgress>
  ```
- Incremental early-exit: on incremental syncs (`fullSync = false`), checks if all series in a page batch already exist in persistence. If a complete page has 100% overlap, it halts further requests.
- Emits progress updates (`pagesCrawled`, `itemsIngested`, `isFinished`) to callers.

### 3. Repository and Storage Seam (`SeriesRepository` & `SeriesDao`)
- Ingestion writes series stubs to the existing `series` table with `inLibrary = false`.
- **Zero Chapter Invariant**: Catalog ingestion strictly inserts series stubs and never fetches or writes chapters. Chapters remain on-demand only.
- Ingestion executes content-only updates (`SeriesDao.updateDetails`) on existing rows, protecting user bookmark flags (`inLibrary = true`), `addedAt` timestamps, and cascading chapter foreign keys.
- Each page batch is persisted immediately, ensuring crash-safe and network-resilient ingestion.

### 4. Native Room SQL Search Engine
- Replaces external search providers with an indexed native Room query:
  ```sql
  SELECT * FROM series 
  WHERE sourceId = :sourceId 
    AND title LIKE '%' || :query || '%' 
  ORDER BY title ASC
  ```
- Provides instant, sub-5ms local search directly from SQLite C++ without loading thousands of items into Kotlin memory.

### 5. Background Execution (`CatalogSyncWorker`)
- Implements a WorkManager worker executing `SyncSourceCatalog`.
- Configured with `NetworkType.UNMETERED` and `requiresCharging(true)` / `requiresDeviceIdle(true)` constraints.
- Operates silently in the background, updating the local database without active UI involvement.

## Testing Decisions

### Test Quality & Principles
- Tests must verify external observable behavior across module seams rather than internal implementation details.
- Unit tests run on the JVM without Android framework or emulator requirements.
- Network calls are completely isolated using canned HTML fixtures.

### Tested Modules
1. **`FreeWebNovelCatalogHarvesterTest`**:
   - Uses multi-page HTML fixtures (`latest-novel-page1.html`, `latest-novel-page2.html`, `latest-novel-terminal.html`).
   - Asserts extraction of series stubs, cover URLs, completion statuses, and proper termination at the final page.
2. **`SyncSourceCatalogTest`**:
   - Uses fakes for `CatalogHarvester` and `SeriesRepository`.
   - Tests incremental early-exit when an overlapping batch is returned.
   - Tests error handling and cancellation cleanups.
3. **`SeriesRepositoryCatalogTest`**:
   - Uses an in-memory Room database.
   - Verifies batch ingestion with `inLibrary = false`.
   - Verifies that re-ingesting an existing bookmarked series preserves `inLibrary = true` and `addedAt`.
   - Tests native Room search query with partial, case-insensitive, and punctuated title queries.
4. **`CatalogSyncWorkerTest`**:
   - Uses AndroidX WorkManager test harness (`TestListenableWorkerBuilder`).
   - Validates worker completion and constraint compliance.

### Prior Art
- `FreeWebNovelTest.kt` for Jsoup parsing and selector verification.
- `SeriesRepositoryImplTest.kt` for Room repository testing and metadata cache assertions.
- `LibraryUpdateWorkerTest.kt` for WorkManager testing patterns.

## Out of Scope
- Fetching full descriptions, genres, or chapters during catalog ingestion (deferred to on-demand series opening).
- Automated scraping of non-directory sources (sources without full directory structures).
- Cloud synchronization or cross-device catalog syncing.
- UI changes beyond integrating native Room search into the Browse screen.

## Further Notes
- Reopens the intentional terminal pagination constraint in `docs/specs/source-pagination.md` by establishing a separate, background harvesting seam that leaves interactive UI pagination safe and bounded.
- The entire ~4,000+ title catalog is estimated to occupy ~1.2 MB to 1.5 MB in SQLite, maintaining a lightweight device footprint.
