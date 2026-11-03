---
id: concept-presentation
title: "Presentation, navigation, and theme"
type: Concept
status: active
valid_from: 2026-10-08
scope:
  system: readerparser
relations:
  - type: supported_by
    target: ../evidence/navigation-and-tooling-history.md
---

# Presentation, navigation, and theme

## Screen contract

`ui/<screen>/` contains exactly Screen, Content, ViewModel, and UiState files for each new screen.
Screen obtains hiltViewModel, uses collectAsStateWithLifecycle, collects one-shot effects in LaunchedEffect, and delegates to Content.
Screen is never previewed. Stateless Content accepts state and onAction and always supplies a Preview.
ViewModel exposes one StateFlow<UiState> plus optional Flow<Effect> and a single onAction entrypoint.
UiState is an immutable data class. Action and Effect use sealed interfaces.
Navigation/snackbars/toasts use a buffered Channel exposed as receiveAsFlow, never navigation state in UiState.
Settings has no effects because settings edits produce reactive state only.
Material 3 is exclusive. Hardcoded colors/dp values belong in ui/theme.
Use theme color/typography/shape/spacing tokens, Coil 3 AsyncImage placeholders/error painters, and stable lazy-list/grid keys.
Hoist composables used by at least two screens into ui/components. Business policy belongs in ViewModels/repositories.
Scaffolding first establishes the screen purpose before defining fields, leaves business TODOs, and adds both destination and graph entries.

## Library and Browse state

Library is the home screen. LibraryViewModel observes SeriesRepository Library and search-invalidations flows.
Blank input displays the local list with DEFAULT insertion order or TITLE sort. Nonblank input uses repository Samsung-first search.
OpenSeries emits navigation. RemoveFromLibrary calls the repository. Settings uses the supplied settings callback.
The search field remains visible when empty, with leading search icon, trailing clear, and IME Search.
Loading/error/empty/no-matches/populated states have distinct rendering. Cover/title cards use 2:3 cover ratio and long-press removal.
Shared adaptive cards replace historical fixed two-column/LibrarySeriesCard descriptions.
Unread controls and filterUnreadOnly are historical unsupported intent, not current UI requirements.
Library title repair and search fallback/debounce gaps are governed by [Catalog](../specs/catalog-presentation.md) and [Library search](../specs/library-search.md).
BrowseViewModel injects SourceRepository and SeriesRepository and owns sources, selected source, POPULAR/LATEST/SEARCH mode,
series, isLoading/error, hasNextPage/currentPage, and searchQuery.
SelectSource/SetMode reset page context, LoadMore appends, query/search actions invoke Search, and OpenSeries emits detail navigation.
Popular/latest map to fetchPopular/fetchLatest. Search passes source ID, query, page, and filters.
Initial first-source Popular discovery and current 300 ms Search behavior are in [Browse](../specs/browse-discovery.md).

## Series and Reader state

SeriesViewModel obtains sourceId/seriesUrl from SavedStateHandle and injects SeriesRepository and ChapterRepository.
Its state includes nullable `series: Series?`, `chapters: List<ChapterWithState>`, `isLoading`, nullable `error`, and membership flag `inLibrary`.
It builds a minimal Series stub to begin observing existing chapters before details arrive, avoiding a subscription-after-fetch race.
It refreshes details, then chapters, and reads Library membership.
ChapterWithState rows are sorted newest-first by number at every reactive emission, independent of DAO ascending order.
The detail header shows cover/title/author/status/genres and expandable description, followed by read/download indicators.
Refresh loads details/chapters. ToggleLibrary updates membership. Chapter selection sends NavigateToReader with Chapter and ContentType.
The old Series map claimed optimistic ToggleLibrary updates. Current SeriesViewModel updates `inLibrary` only after add/remove succeeds.
Failure emits `ShowError(message)` without assigning the requested membership value. This is source inspection, not a runtime test result.
The historical `ShowError` snackbar handler was marked TODO. Preserve that limitation separately from the ViewModel effect contract.
Current SeriesScreen uses one Reader callback. Old two-callback novel/manhwa routing is historical.
ReaderViewModel consumes saved source/series/chapter/contentType, validates payloads, observes chapters, persists text progress, and marks read.
ReaderContent branches once into native Compose NovelTextEngine (LazyColumn of NovelBlock headings, paragraphs, and dividers with customizable typography) or vertical pages with shared immersive controls.
ReaderAction includes SetProgress, SetPage, PreviousChapter, NextChapter, OpenChapterList, DownloadChapter, SelectChapter, and Retry.
Effects include NavigateToChapter, ShowChapterList, ShowError, and ShowSnackbar. ReaderChapterListSheet supplies selection.
DownloadEnqueuer handles single-chapter enqueue. The complete content/progress/mismatch contract is in [Reader specs](../specs/reader.md).

## Downloads and Settings state

DownloadsViewModel injects DownloadRepository and continuously observes queue changes without polling or a refresh action.
UiState contains downloads, isLoading, and nullable error. Cancel/Retry call repository methods and emit ShowError on failure.
DownloadItemRow shows series/chapter names, state badge, running-only progress bar, and state-appropriate Cancel/Retry controls.
StateBadge is a read-only SuggestionChip. Delete and percentage requirements remain in [Downloads specs](../specs/downloads.md).
Test tags downloads_list, loading, and error_message support loading/error/empty/populated Compose cases.
Some historical map descriptions identify unwired snackbar TODOs. Preserve that limitation without claiming current full error-host integration.
SettingsViewModel observes SettingsRepository into AppSettings plus isLoading and delegates one action per preference setter.
It waits for repository flow confirmation rather than optimistic field assignment. No separate error state/effects are specified for mutations.
Controls select theme (SYSTEM, LIGHT, DARK, WARM_DARK, SEPIA), font family (Serif, Sans-Serif, Monospace, Default), font size 12–32 sp, line height multiplier 1.2–2.0x, paragraph spacing 4–28 dp, horizontal padding 8–40 dp, manhwa layout, and zoom.
Private SectionHeader, SectionDivider/HorizontalDivider, and selectable RadioRow support the settings form.
Test tags settings_list, font_size_slider, and typography sliders support UI verification.
Persisted manhwa layout/zoom choices do not prove all modes are implemented by the current vertical Reader renderer.

## Navigation and deep links

ui/navigation/Destinations defines LIBRARY, BROWSE, DOWNLOADS, SETTINGS, SERIES, and READER routes.
Series requires sourceId and seriesUrl. Reader requires sourceId, seriesUrl, chapterUrl, and contentType.
series()/reader() encode URL arguments with Uri.encode. NavType.LongType and StringType provide route arguments to saved state.
AppNavGraph owns rememberNavController, Scaffold, NavHost, and typed screen callbacks. Start destination is LIBRARY.
Library, Browse, and Downloads show bottom NavigationBar tabs, each providing a top-bar Settings icon button. Browse header provides a source selector dropdown. Series, Reader, and Settings hide the navigation bar.
Tabs use launchSingleTop and restoreState with popUpTo start destination/saveState to preserve tabs without duplicate routes.
Reader chapter navigation replaces the current Reader entry through inclusive popUpTo and preserves content type.
Back callbacks use popBackStack.
MainActivity accepts readerparser://series deep links at initial fresh create and onNewIntent.
It defers navigation until the graph is ready, clears the pending URI, and avoids reprocessing initial links after recreation.
Malformed source IDs or insufficient path segments are ignored. Valid links open Series detail, not Reader.

## Theme ownership and limits

ui/theme/Color.kt defines tokens for Light, Dark, Warm Dark, and Sepia schemes.
Theme.kt maps SYSTEM, LIGHT, DARK, WARM_DARK, and SEPIA to their respective color schemes using isSystemInDarkTheme for SYSTEM and passes AppTypography.
Type.kt owns default Typography and NovelReaderBodyStyle.
Reader preferences override font size, font family, line height, paragraph spacing, and horizontal padding at runtime. Theme stays independent of navigation.
Content previews wrap ReaderParserTheme.
MainActivity observes SettingsRepository.observeSettings() to apply the selected theme dynamically across the app.
