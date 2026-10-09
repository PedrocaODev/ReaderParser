---
id: concept-runtime-wiring
title: "Application runtime and infrastructure wiring"
type: Concept
status: active
valid_from: 2026-10-08
scope:
  system: readerparser
relations:
  - type: supported_by
    target: ../evidence/navigation-and-tooling-history.md
---

# Application runtime and infrastructure wiring

## Entrypoints and production structure

The single :app module builds the com.opus.readerparser APK.
app/src/main contains manifest, java/com/opus/readerparser Kotlin, assets, and Android res directories.
The intervening java/com/opus folders are namespace structure, not additional layers or modules.
App is @HiltAndroidApp plus WorkManager Configuration.Provider. MainActivity is the @AndroidEntryPoint Compose host.
Manifest declares INTERNET, App, launcher MainActivity, resources, backup/data-extraction rules, and removes the default WorkManagerInitializer.
Resources include launcher icons, strings, themes, drawables, and XML settings. Build variations use buildTypes rather than flavor source overlays.
App supplies HiltWorkerFactory and starts Samsung registration/observing in an IO coroutine under SupervisorJob plus Main.immediate scope.
Unavailable provider or failed registration disables sync while preserving normal startup.
The old description of App scheduling periodic Library refresh is not present in current App.onCreate.

## Hilt graph

core/di owns wiring, not business policy. Modules install in SingletonComponent and have one subsystem per concern.
Use @Provides for third-party/Android root types and @Binds for first-party interface implementations.
RepositoryModule co-locates five repository interface bindings.
DatabaseModule supplies AppDatabase and Series/Chapter/DownloadQueue DAOs.
NetworkModule supplies shared HttpClient(OkHttp) and Json, with configured cookies/retries/timeouts/content negotiation/cache.
FilesystemModule binds DownloadStore and provides a @DownloadRoot File to avoid File-binding ambiguity.
PrefsModule provides settingsDataStore Preferences. SearchModule provides SearchProviderDelegate through ContentResolverDelegate.
SourceModule assembles compile-time plugins. SourceMetadataCacheModule supplies typed caches of SeriesPage, Series, and List<Chapter>.
Dependencies come from the graph, not another module's private implementation. Core wiring does not import UI.
Fake bindings and TemporaryFolder download roots preserve testability.
Historical core/result and data/network reserved directories do not currently exist. Network wiring remains in NetworkModule.
The original root placement rule assigns network, JSON, and cookie implementations to `data/network/`.
This placement rule is distinct from current DI wiring in `core/di/NetworkModule.kt` and the observed absence of `data/network/`.

## Pure utility algorithms

core/util contains ComputeSourceId, Hashing, SourceMetadataCache, and TitleMatcher.
Identity and path hashing are described in [Domain identity](domain-contracts.md#identity-and-nullable-data) and [Download storage](persistence.md#download-storage-and-integrity).
SourceMetadataCache<V> takes maxEntries, ttlMs, and injectable nowNanos (default System.nanoTime), supporting deterministic bounded TTL tests.
Its intended cache scopes and limits are in [Metadata specs](../specs/source-metadata.md).
TitleMatcher normalizes lowercase text, strips outer whitespace, and collapses internal whitespace.
An empty query matches all. It first checks a substring, then title windows of query length minus one, equal length, or plus one.
Fuzzy windows match at edit distance at most one. editDistance computes Levenshtein distance capped at two, with early cutoff.
These helpers use JVM types only. Current source-local search uses TitleMatcher.
The old claim of direct LibraryViewModel fuzzy filtering predates repository-backed Library search.

## Chapter download execution

workers/ChapterDownloadWorker is a @HiltWorker CoroutineWorker with AssistedInject context/parameters and injected repositories/store/client.
buildRequest carries keys sourceId and chapterUrl, network-connected constraint, exponential 30-second backoff,
and deterministic tag `download-$sourceId-${hashUrl(chapterUrl)}` for cancellation/deduplication.
Missing chapter URL fails. Missing cached Chapter writes FAILED with `Chapter not found in local database` and fails.
Otherwise write RUNNING progress zero, force fresh ChapterRepository content, store HTML or pages, mark downloaded, and write COMPLETED progress one.
Novel progress reaches 0.5 after fetch. Page writes invoke per-page progress callback.
On failure, Log.e includes source/URL, queue state becomes FAILED with error text, and first two attempts retry before third failure.
The worker's manhwa fetchBytes currently performs HttpClient GET directly. This conflicts with the repository/Source-only Ktor invariant.
The migration records the conflict without changing production behavior.
Workers have no lifecycle/Compose/UI dependencies. Repository methods own entity mapping rather than direct worker DAOs.

## Library and Samsung workers

LibraryUpdateWorker reads observeLibrary().first and refreshes chapters for each series.
Its factory builds a six-hour connected-network periodic request tagged library-update with exponential 30-second backoff.
It retries twice, then fails on the third attempt. The outer try/catch currently stops iteration after the first failing series.
Six hours is this app's chosen interval, not WorkManager's minimum. Existing App does not schedule this factory.
SamsungSearchRebuildWorker calls SearchIndexSyncer.rebuildIndex with default backoff and retry through runAttemptCount<2.
SamsungSearchUpdateReceiver validates ACTION_UPDATE_INDEX and enqueues one-time rebuild work.
Manifest protects its broadcast using SEND_ACTION_UPDATE_INDEX. The full recovery spec is in [Samsung index](../specs/samsung-search-index.md).
