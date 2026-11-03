---
id: specs-novel-reader-and-design-system
title: "Chapter state preservation, progressive novel loading, native novel text engine, and warm dark design system"
type: Concept
status: active
valid_from: 2026-10-27
scope:
  system: readerparser
relations:
  - type: relates_to
    target: ./reader.md
  - type: relates_to
    target: ./downloads.md
  - type: relates_to
    target: ./source-pagination.md
  - type: relates_to
    target: ../CONTEXT.md
---

# Chapter state preservation, progressive novel loading, native novel text engine, and warm dark design system

## Problem Statement

1. **Lost chapter user state on restart**: When closing and reopening the app after the in-memory chapter cache expires, some bookmarked series (such as AsuraScans comics whose chapter URLs include a rotating 8-character hex suffix) lose all chapter reading state (`read`, `progress`, and `downloaded` reset to defaults) because chapter refresh matches existing rows strictly by exact chapter URL and deletes unmatched rows.
2. **Slow initial chapter list loading for long novels**: Opening a long webnovel on FreeWebNovel (such as a 2,000+ chapter series spanning 10+ AJAX pages of 200 chapters each) blocks on sequential page fetching before displaying any chapters.
3. **Unrefined webnovel reading experience**: Rendering novel chapters inside an embedded `WebView` produces jarring layout shifts, imprecise scroll-progress restoration, and limited typographic control. Pure-black and pure-white themes also cause eye strain during long reading sessions.
4. **Fragmented visual hierarchy and navigation chrome**: The 5-destination bottom navigation bar crowds primary navigation, and the app lacks a cohesive editorial design system with a browser-previewable visual reference for agent-first development.

## Solution

1. **Resilient chapter user state matching**: Preserve `Chapter user state` (`read`, `progress`, `downloaded`) during chapter refresh by matching incoming chapters against existing local chapters by exact URL first, then falling back to matching by unique non-negative chapter number within `(sourceId, seriesUrl)`, and refusing to wipe local chapters when a remote refresh returns an empty list.
2. **Progressive chapter list with bounded concurrency**: Introduce a progressive chapter list retrieval contract (`fetchChapterListProgressive`) on `Source` so `FreeWebNovel` emits Page 1 (chapters 1–200) immediately and then fetches remaining AJAX pages concurrently (up to 4 in-flight requests) with deterministic ascending-page merge and URL deduplication.
3. **Native Compose Novel text engine**: Replace `NovelWebView` with a native Compose `LazyColumn` renderer (`NovelTextEngine`) that parses chapter HTML into structured prose blocks (headings, paragraphs, inline emphasis, scene dividers) with continuous vertical scroll, deterministic scroll restoration, and customizable typography (font family, font size, line height, paragraph spacing, horizontal margins).
4. **Warm dark theme, editorial design system, and browser-first HTML prototype**: Introduce a `Warm dark theme` (warm charcoal/espresso surfaces with muted cream text and amber/terracotta accents) and `Sepia` reading palette, consolidate primary navigation into a 3-tab bar (**Library**, **Browse**, **Downloads**) with **Sources** integrated into Browse controls and **Settings** in the top app bar, and provide a standalone interactive HTML/CSS design system prototype (`docs/design/prototype.html`) for visual verification prior to Compose implementation.

## User Stories

1. As a reader of bookmarked AsuraScans series, I want my read chapters and in-progress scroll positions to remain intact when I close and reopen the app even after the source rotates its chapter URL slug hash, so that I never lose my reading history.
2. As a reader, I want a transient empty chapter scrape or network hiccup during chapter refresh to keep my existing local chapter list and `Chapter user state`, so that a bad response never wipes my progress.
3. As a reader opening a 3,000-chapter novel on FreeWebNovel for the first time, I want the first 200 chapters to appear on the Series detail screen as soon as Page 1 finishes (~300ms), so that I can start reading Chapter 1 without waiting for all 15 pages.
4. As a reader viewing a long novel on FreeWebNovel, I want the remaining chapter pages (`2..totalPage`) to load concurrently in the background and update the Series screen automatically, so that the full chapter list completes in a few seconds instead of 15+ seconds.
5. As a reader, I want concurrent chapter pages to merge in strict ascending chapter/page order with duplicate URLs removed, so that the chapter list order is always deterministic.
6. As a novel reader, I want chapter prose rendered natively in a continuous vertical scroll without `WebView` flicker, so that scrolling feels smooth and responsive.
7. As a novel reader, I want headings, paragraphs, italic/bold inline emphasis, and scene breaks rendered with clear typographic hierarchy, so that chapters read like a well-typeset book.
8. As a novel reader, I want to customize font family (Serif, Sans-Serif, Monospace), font size, line height, paragraph spacing, and horizontal margins from the Reader typography sheet, so that I can tailor the layout to my eyes.
9. As a novel reader, I want my scroll position restored accurately to the paragraph and offset where I left off when reopening an in-progress chapter, so that I never lose my place.
10. As a reader who reads at night or for long sessions, I want a `Warm dark theme` using warm charcoal/espresso surfaces and soft cream text instead of pure black and pure white, so that contrast does not strain my eyes.
11. As a novel reader, I want a `Sepia` reading surface option in addition to Warm Dark, Light, and System themes, so that I can read in warm paper tones during the day.
12. As a user navigating the app, I want a clean 3-tab bottom navigation bar (**Library**, **Browse**, **Downloads**) with **Sources** integrated into the Browse screen header/filter controls and **Settings** accessible from the top app bar, so that primary navigation is uncluttered.
13. As a developer working with coding agents without Android Studio open, I want an interactive standalone HTML/CSS design system and reader prototype in the repository (`docs/design/prototype.html`), so that I can visually inspect and refine themes, typography, and component states in any browser before applying Compose changes.

## Implementation Decisions

### Chapter User State Preservation
- Keep the existing `chapters` table primary key `(sourceId, url)` and avoid a Room schema migration.
- In `ChapterDao.replaceChaptersForSeries`:
  - Return early without deleting existing rows if `remoteChapters` is empty.
  - Match each incoming remote chapter against existing rows by exact `url` first.
  - When no exact `url` match exists and the incoming chapter has `number >= 0f`, fall back to matching an existing row in the same `(sourceId, seriesUrl)` with the same `number` (when that `number` uniquely identifies a single existing row not already matched by URL).
  - Copy `read`, `progress`, and `downloaded` from the matched existing row onto the incoming chapter entity.
  - Delete stale existing rows whose `url` is not in `remoteUrls` before upserting the merged list.

### Progressive Chapter List Retrieval
- Add a default method `fetchChapterListProgressive(series: Series): Flow<List<Chapter>> = flow { emit(getChapterList(series)) }` to `Source`.
- Override `fetchChapterListProgressive` in `FreeWebNovel`:
  - Fetch AJAX Page 1 (`?ajax=chapters&page=1&pageSize=200`). If unavailable or blank, fall back to HTML document parsing and emit once.
  - Parse and emit Page 1 chapters immediately on the first `Flow` emission.
  - When `totalPage > 1`, fetch pages `2..totalPage` concurrently bounded by `Semaphore(4)`, merge results deterministically in ascending page order, deduplicate by chapter URL, and emit the full aggregated `List<Chapter>`.
- Update `ChapterRepositoryImpl.refreshChapters` to collect `source.fetchChapterListProgressive(series)`, calling `chapterDao.replaceChaptersForSeries` on each emission and updating the in-memory `chapterListCache` with the latest list.

### Native Compose Novel Text Engine
- Replace `NovelWebView` with a native Compose `NovelTextEngine` (`LazyColumn`) while keeping `ChapterContent.Text(html: String)` unchanged.
- Parse chapter HTML via Jsoup into a platform-free or UI-model list of `NovelBlock` elements:
  - `Heading(text, level)`
  - `Paragraph(annotatedText / inline spans for bold, italic, strikethrough)`
  - `Divider` (for `<hr>` or asterisk scene breaks)
- Compute normalized scroll progress (`[0f, 1f]`) from `LazyListState` visible item index and scroll offset across total items, and restore initial scroll position on chapter load from persisted `progress`.
- Extend `AppSettings` and `SettingsStore` (with safe defaults) to persist novel reader preferences:
  - `novelFontSize: Int` (default `18`)
  - `novelFontFamily: String` (`"Serif"`, `"Sans"`, `"Mono"`, default `"Serif"`)
  - `novelLineHeightMultiplier: Float` (default `1.6f`)
  - `novelParagraphSpacingDp: Int` (default `14`)
  - `novelHorizontalPaddingDp: Int` (default `20`)

### Design System, Warm Dark Theme & Navigation Chrome
- Create a standalone interactive HTML/CSS prototype (`docs/design/prototype.html`) showcasing:
  - Color tokens across `WARM_DARK`, `SEPIA`, `LIGHT`, and `DARK`.
  - Typography scale and live interactive sliders for `NovelTextEngine` (font family, font size, line height, paragraph spacing, margins).
  - Series detail header card, chapter row states (unread dot, in-progress bar, completed muted check), 3-tab navigation bar, and Browse source selector pill.
- Extend `AppTheme` with `WARM_DARK` and `SEPIA` (alongside `SYSTEM`, `LIGHT`, `DARK`) and wire the warm charcoal/espresso palette (`#181614` background, `#211E1B` surface, `#2A2521` elevated container, `#E6DFD5` primary text, `#A89F93` secondary text, `#D98A4E` warm amber accent) into `Color.kt` and `Theme.kt`.
- Consolidate `AppNavGraph` bottom navigation bar to 3 top-level tabs (**Library**, **Browse**, **Downloads**); expose **Sources** selection inside Browse's top header/filter bar and **Settings** as a top-bar action icon on top-level screens.

## Testing Decisions

- **Good Test Principles**: Assert only on observable external behavior (`UiState` emissions, `Flow` outputs, Room query state, and Compose semantic nodes), never on private internal state.
- **Seam 1 — Repository & Room DAO (`ChapterRepositoryImplTest`, `ChapterDaoTest`)**:
  - Verify `replaceChaptersForSeries` preserves `read`, `progress`, and `downloaded` when chapter URLs rotate their slug hash but keep chapter numbers.
  - Verify `replaceChaptersForSeries` does not delete existing chapters when `remoteChapters` is empty.
  - Verify `refreshChapters` persists both the initial Page 1 emission and the final complete emission from `fetchChapterListProgressive`.
- **Seam 2 — Source Plugin (`FreeWebNovelTest`)**:
  - Verify `fetchChapterListProgressive` emits Page 1 first, fetches remaining pages with at most 4 concurrent requests, and emits the deterministically ordered deduplicated full list.
- **Seam 3 — Reader & Settings (`ReaderViewModelTest`, `ReaderContentTest`, `SettingsStoreTest`)**:
  - Verify `NovelTextEngine` renders structured paragraphs/headings in Compose without a `WebView`, reports scroll progress, and applies persisted typography and theme settings.
- **Seam 4 — UI & Navigation (`BrowseContentTest`, `LibraryContentTest`, `SeriesContentTest`, `SettingsContentTest`)**:
  - Verify Warm Dark / Sepia theme options and the 3-tab navigation layout with top-bar Settings and Browse source controls.

## Out of Scope

- Cloud account sync or third-party tracker integration.
- Paginated horizontal page-flip mode for novels (continuous vertical scroll is the canonical novel reading mode).
- Room database schema migrations (all persistence improvements operate within the current schema v3).
- Android Studio preview bridge (`android studio render-compose-preview`).

## Further Notes

- Update `docs/architecture.md`, `docs/concepts/presentation.md`, `docs/specs/reader.md`, `docs/specs/downloads.md`, and `docs/specs/source-pagination.md` during implementation so the Akashic vault remains synchronized with the native Compose `NovelTextEngine` and progressive `Source` contract.
