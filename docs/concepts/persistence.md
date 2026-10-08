---
id: concept-persistence
title: "Persistence and repository orchestration"
type: Concept
status: active
valid_from: 2026-10-08
scope:
  system: readerparser
relations:
  - type: supported_by
    target: ../evidence/navigation-and-tooling-history.md
---

# Persistence and repository orchestration

## Structured state and repositories

`data/repository/` bridges source metadata with local state. Hilt binds each domain repository to one implementation.
SeriesRepositoryImpl handles catalogs, search, Library membership, details, and search invalidations.
It checks in-memory SourceMetadataCache before remote metadata requests and persists returned series through saveSeries.
saveSeries first calls targeted `SeriesDao.updateDetails()` to preserve inLibrary and addedAt.
When rows affected equals zero, insert the missing row. `addToLibrary` updates only existing rows and silently does nothing for missing rows.
Normal flow calls detail refresh before bookmark actions, so it establishes the row first.
Page-one nonblank search can use source-local TitleMatcher fallback after empty or failed remote results, returning a title-sorted terminal page.
Library search invalidation combines Library and indexable-series observations into Flow<Unit>.
SourceRepositoryImpl maps `SourceRegistry.all()` to SourceInfo without storage access or dynamic-source caching.
DownloadRepositoryImpl observes the queue with joined labels and owns cancellation/retry/deletion operations.
SettingsRepositoryImpl resides in `data/local/prefs/`, unlike the other repository implementations.

## Room tables and queries

`data/local/database/AppDatabase.kt` is version 1 with exportSchema enabled and three tables.
SeriesEntity uses table series and `(sourceId,url)` PK. ChapterEntity uses chapters and `(sourceId,url)` PK.
Chapter's `(sourceId,seriesUrl)` foreign key references Series and cascades on deletion. Its relationship index speeds chapter lookup.
DownloadQueueEntity uses download_queue and `(sourceId,chapterUrl)` PK without a foreign key, permitting not-yet-cached chapter references.
Nullable entity metadata follows domain optionality.
Series stores genres as explicit genresJson, not a Room TypeConverter. Status/type persist enum `.name` strings instead of ordinals.
inLibrary is a flag rather than a separate membership table. Unbookmarked metadata can remain cached.
SeriesDao observes Library ordered by addedAt descending, updates details, and reads source-scoped cached rows.
ChapterDao observes a series' chapters by number ascending and writes read/progress/downloaded state.
DAO observable lists are Flow. One-shot reads/writes are suspend operations.
Series/Chapter upsertAll use REPLACE, but repositories must preserve user state before replacing rows.
DownloadQueueDao.observeAllWithDetails uses LEFT JOIN through chapters and series into DownloadQueueWithDetails, a DTO rather than an entity.
Its updateStateWithError writes state, progress, and error text together.

## Conversion and chapter refresh

SeriesMappers supplies SeriesEntity.toDomain and Series.toEntity.
ChapterMappers supplies ChapterEntity.toDomain, Chapter.toEntity, and ChapterEntity.toChapterWithState.
These are top-level functions without DI or Android operations.
toDomain intentionally drops storage-only fields: Series membership/addedAt and Chapter read/downloaded/progress.
toChapterWithState restores the chapter-state wrapper.
Chapter.toEntity creates clean read=false, progress=0f, downloaded=false defaults. Preservation belongs to the repository.
Series enum parsing degrades unknown status/type values to UNKNOWN/NOVEL rather than throwing.
GenreJson explicitly encodes string lists as JSON arrays and decodes them, with empty lists for blank input.
Current refreshChapters checks its chapter-list cache, then fetches remote chapters and joins existing state by chapter URL.
It copies read/progress/downloaded from existing rows, deletes the prior series chapter rows, and upserts merged remote rows.
This removes stale chapters. It caches nonempty remote lists only after success.
The old map's claim that REPLACE preserves unchanged progress and leaves old chapters is incorrect for current implementation.

## Download storage and integrity

`data/local/filesystem/DownloadStore` is fakeable using domain types. DownloadStoreImpl injects a @DownloadRoot File and Json.
Production root is `File(context.filesDir, "downloads")`. JVM tests can supply a TemporaryFolder.
File I/O uses Dispatchers.IO. Network bytes are supplied by the caller's suspend fetchBytes lambda.
The store is pull-based rather than reactive. Readers check it before network. Uninstall removes app-private data.
The directory is `downloads/<sourceId>/<hashUrl(seriesUrl)>/<hashUrl(chapterUrl)>/`.
hashUrl computes SHA-1 of UTF-8 URL bytes, lowercase hex, truncated to 16 characters for stable safe path components.
The old map described this 64-bit truncation as adequate for its namespace. That is a historical collision assumption, not a collision guarantee.
Each chapter has meta.json recording type, originalUrls, pageCount, and downloadedAt.
Novels have content.html. Manhwa has one-based, three-digit page names 001.jpg, 002.jpg, and so forth.
Manhwa writes meta.json last after all page writes, using its absence as an incomplete-download marker.
Partial page files can remain after interruption and are overwritten on retry.
Current novel writes metadata before HTML. A missing HTML file returns null rather than valid Text.
read checks metadata, decodes type, and returns Text or filename-sorted file URI Pages. Unknown types return null.
A null directory listing on an existing manhwa directory throws IOException.
ChapterRepository treats unreadable/corrupt local content as a cache miss while preserving coroutine cancellation.
delete removes the chapter directory. deleteByHash scans source-series directories for a matching chapter hash.

## Preferences and atomic edits

DataStoreExt defines internal Context.settingsDataStore named settings.
SettingsStore wraps its data Flow and edit for injectable/fakeable preference access.
SettingsRepositoryImpl maps Preferences to AppSettings and binds through RepositoryModule.
Keys/defaults are theme=SYSTEM, novel_font_size=16, novel_font_family=Default, manhwa_layout=WEBTOON, manhwa_zoom=FIT_WIDTH.
Each setter performs one atomic DataStore edit. Concurrent edits serialize and UI reacts to resulting flow emissions.
Enums persist names and use valueOf on restoration. Missing values use defaults. Invalid names currently can throw, unlike the old corruption-default claim.

## Samsung index infrastructure

`data/local/search/SearchIndexSyncer` observes SeriesDao.observeIndexableSeries and offers startObserving and one-shot rebuildIndex.
Debounced emissions probe availability, clear the index, and bulk-insert mapped ContentValues.
SamsungSearchClient wraps provider registration, queries, batching, and clearing with graceful failure handling.
SearchProviderDelegate abstracts ContentResolver for tests. ContentResolverDelegate supplies the production implementation.
SamsungSearchSchema reads `app/src/main/assets/search/samsung-search-indexable-series.xml` bytes and supplies fake() for tests.
Document mapping uses sourceId:url as _id, local title/author/description/status/type, decoded comma-separated genres, and
`readerparser://series/{sourceId}/{encodedUrl}` as source_url.
The full availability/schema/eligibility/broadcast contract is in [Samsung index specs](../specs/samsung-search-index.md).

## Migration discipline

Increment AppDatabase.version exactly once per schema change, with no skipped version.
Write an explicit Migration class with SQL and register it in DatabaseModule.
Never use fallbackToDestructiveMigration in any build configuration.
Update affected entities, DAOs, and mappers. Generate schema exports with the build and commit app/schemas changes without hand-editing them.
Migration tests use MigrationTestHelper in androidTest and the exported schemas as assets.
Changing identity, PK, or FK behavior requires prior user approval.
