---
id: concept-domain-contracts
title: "Domain contracts and identity"
type: Concept
status: active
valid_from: 2026-10-08
scope:
  system: readerparser
relations:
  - type: supported_by
    target: ../evidence/navigation-and-tooling-history.md
---

# Domain contracts and identity

## Boundary and locations

Production Kotlin is under `app/src/main/java/com/opus/readerparser/`.
`domain/` owns repository interfaces and `domain/model/` owns the model lexicon.
The current app calls repositories directly from ViewModels. A use-case layer is unnecessary until non-trivial policy justifies it.
Use only Kotlin types and pure coroutine Flow in contracts. Models contain data, without platform operations, validation, or business logic.
Model state changes use `.copy(...)`, immutable properties, and read-only collections.
Old maps claimed build enforcement and prohibition of domain models in composable signatures. Actual code must establish enforcement and signature usage.
Those claims do not establish a new compiler rule.

## Identity and nullable data

Series and Chapter identity is `(sourceId: Long, url: String)`, with no auto-increment surrogate identities.
Chapter.seriesUrl references the parent Series URL within the same source. It supports relationship queries, not a third chapter identity component.
`computeSourceId(name, lang, type)` computes `"$name/$lang/${type.name}".hashCode().toLong() and 0xFFFFFFFFL`.
Identical inputs produce identical unsigned 32-bit values across reinstalls. Never hand-pick source IDs.
Keep genuinely optional author, artist, description, coverUrl, and uploadDate nullable.
DownloadItem.errorMessage is null until failure. Prefer empty lists and UNKNOWN when a valid zero value exists. Avoid nullable booleans.

## Closed models and aggregates

ChapterContent is Text(html) or Pages(imageUrls). Adding a third shape requires explicit architectural sign-off and contradicts the present two-shape invariant.
Filter is Text(key,value), Select(key,value), or Toggle(key,boolean). FilterList wraps the ordered filter list.
SeriesPage contains `series: List<Series>` and `hasNextPage: Boolean`. Sources determine next-page availability and callers iterate.
ChapterWithState combines Chapter with read, downloaded, and Float progress.
SourceInfo contains id, name, lang, and type for UI pickers. It omits baseUrl and plugin retrieval capability.
DownloadItem contains sourceId, chapterUrl, seriesTitle, chapterName, state, progress, and optional errorMessage.
AppSettings aggregates theme, novelFontSize, novelFontFamily, manhwaLayout, and manhwaZoom.
LibrarySearchResult represents search results crossing the repository boundary.

| Enum | Values |
| --- | --- |
| ContentType | NOVEL, MANHWA |
| SeriesStatus | UNKNOWN, ONGOING, COMPLETED, HIATUS, CANCELLED |
| DownloadState | QUEUED, RUNNING, COMPLETED, FAILED |
| AppTheme | SYSTEM, LIGHT, DARK |
| ManhwaLayout | PAGED_LTR, PAGED_RTL, WEBTOON |
| ManhwaZoom | FIT_WIDTH, FIT_HEIGHT, ORIGINAL |

## Repository APIs

SeriesRepository owns observeLibrary, observeLibrarySearchInvalidations, popular/latest/search, searchLibrary, refreshDetails,
add/remove Library, and isInLibrary. Remote catalog calls accept source ID and page. Search also accepts query and FilterList.
ChapterRepository owns observeChapters(Series), refreshChapters, findByUrl(sourceId,url), content retrieval, and read/progress/downloaded writes.
`getContent(chapter, forceNetwork=false)` normally prefers downloads. Forced reads bypass downloaded content.
SourceRepository.getSources is a synchronous, non-suspend static descriptor lookup from installed plugins.
DownloadRepository owns observeQueue, cancel/retry, updateQueueState, cancelBatch, and deleteDownload.
Batch cancellation accepts source ID and a set of chapter URLs. State updates include progress and optional errorMessage.
SettingsRepository exposes observeSettings and one setter per AppSettings field.
Each interface owns one concern. A Series ViewModel needing metadata and chapters injects both repositories instead of a composite facade.

## Reactive reads and failure boundary

`observe*` methods return Flow and power reactive Room/DataStore state.
Network retrieval and persistence writes are suspend calls launched in ViewModel scope.
The documented subscription pattern is `stateIn(viewModelScope, WhileSubscribed(5000), initialValue)` when appropriate.
Contracts have no @Throws declarations, but implementations can throw network, parse, or storage failures.
ViewModels catch operational failures and represent errors in UiState or effects.
Sources return domain models directly. Repositories map storage entities to domain values.
ViewModels map data to screen state. Composables consume that state, with ChapterContent selecting the Reader renderer.
