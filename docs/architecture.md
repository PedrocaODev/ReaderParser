---
id: architecture-readerparser
title: "ReaderParser architecture"
type: Concept
status: active
valid_from: 2026-10-08
scope:
  system: readerparser
relations:
  - type: supported_by
    target: ./evidence/navigation-and-tooling-history.md
  - type: relates_to
    target: ./concepts/domain-contracts.md
  - type: relates_to
    target: ./concepts/persistence.md
  - type: relates_to
    target: ./concepts/presentation.md
  - type: relates_to
    target: ./concepts/source-plugins.md
  - type: relates_to
    target: ./concepts/runtime-wiring.md
---

# ReaderParser architecture

## Purpose and scope

ReaderParser is a personal Android reader for webnovels and manhwa from site-specific Source plugins.
Isolate scraping behind a stable plugin boundary. Keep domain contracts Android-free and testable on the JVM.
Share one Reader screen and controls while preserving content-specific rendering.
The project has one Android Gradle module. Logical boundaries apply even without separate modules.
Compose/Material 3 provides UI. Coroutines and Flow provide asynchronous work/state.
Ktor with OkHttp and Jsoup handle remote content. kotlinx.serialization handles JSON.
Room stores structured state, DataStore stores preferences, filesystem stores downloads, WorkManager runs jobs, and Hilt wires dependencies.
Personal use grants no license. The app is not affiliated with source sites and is not intended for distribution.
The scope excludes accounts/cloud/sync, content hosting/redistribution, multi-user behavior, and a general browser shell.
Android is the only supported platform unless the user explicitly requests another platform.

## Layer contracts

Calls proceed downward. Models and state return upward. Lower layers must not import higher layers.
UI renders state and forwards actions. Presentation owns ViewModels, UiState, actions, and effects.
Both depend on domain contracts instead of concrete storage/source implementations.
UI must not depend on SourceRegistry, concrete sources, Room, or Ktor.
ViewModels must not depend on concrete sources, Room entities, DAOs, filesystem, network clients, or raw HTTP.
Domain owns immutable models, repository contracts, and any justified use cases.
Domain has zero Android, Room, Compose, Ktor, Jsoup, or serialization dependencies.
Data repositories coordinate local storage and source output, with no UI concerns.
Source plugins own request construction, parsing, and mapping to domain models. They must not depend on ViewModels, navigation, or Room DAOs.
Infrastructure provides Room, DataStore, filesystem, Ktor, WorkManager, and DI without higher-level feature policy.
Only repositories coordinate both source plugins and local storage.
Ktor calls belong only in Source implementations or repositories.
Current worker HTTP use is an unresolved violation of that rule. The observed implementation does not authorize a waiver.
`*Screen` wires its ViewModel. `*Content` is a stateless preview target.

## Stable invariants

Domain models are immutable data classes. Closed content shapes use a sealed interface.
`ChapterContent` has exactly `Text(html: String)` and `Pages(imageUrls: List<String>)`.
Series and chapter identity is `(sourceId, url)` across every layer.
Source IDs are deterministic and stable because persistence identity depends on them.
Keep one Reader screen with content-specific text and image renderers.
Downloads remain in app-private `context.filesDir` storage.
Production code must not use `runBlocking`.

## Source interface

The app depends on Source, with HtmlSource as an optional reusable HTML helper.
The contract has five properties, one synchronous filter-support method, and six suspend retrieval methods:

```kotlin
interface Source {
    val id: Long
    val name: String
    val lang: String
    val baseUrl: String
    val type: ContentType
    fun supports(filter: Filter): Boolean
    suspend fun getPopular(page: Int): SeriesPage
    suspend fun getLatest(page: Int): SeriesPage
    suspend fun search(query: String, page: Int, filters: FilterList): SeriesPage
    suspend fun getSeriesDetails(series: Series): Series
    suspend fun getChapterList(series: Series): List<Chapter>
    suspend fun getChapterContent(chapter: Chapter): ChapterContent
}
```

`supports` defaults to true in the implementation. SourceRegistry selects plugins by source ID.
Site churn stays within plugin implementation, registration, and tests instead of UI contracts.

## Persistence ownership

Room owns library metadata, chapter read/progress/downloaded state, and the download queue.
Filesystem owns downloaded HTML and image payloads. DataStore owns user preferences and reader settings.
Repositories translate domain models and persistence models.
Sources do not write Room, DataStore, or downloaded-file storage. ViewModels use repository interfaces for these concerns.
Room schema changes require explicit migrations. Release behavior must not rely on destructive migration.

## Runtime flow

Android starts App and Hilt creates the object graph.
Screens send actions to ViewModels, which call domain repositories.
Repositories serve local state, refresh through a Source, or combine both.
Remote work resolves `SourceRegistry[sourceId]`, fetches/parses domain models, and persists or merges results.
Suspend results and reactive flows return state to presentation.
Workers reuse the repository/source/storage graph outside UI lifecycle.
Reader navigation carries explicit content type. Reader selects the renderer once from ChapterContent.
Text uses a JavaScript-disabled WebView. Pages uses a vertical image renderer.

## Decisions and trade-offs

The unified Reader shares navigation, immersive controls, effects, and state management.
Private content renderers preserve distinct text/image behavior without a polymorphic renderer abstraction.
Repository orchestration keeps ViewModels simple and sources focused on remote parsing instead of app-state policy.
Pure Kotlin domain contracts preserve JVM testability and contain infrastructure dependencies.
One Gradle module keeps the current build simple without relaxing layer rules.
If build or ownership pressure later justifies module splits, use existing boundaries rather than redefine them.
Current implementation locations are linked from the [vault index](index.md), separate from these normative contracts.
