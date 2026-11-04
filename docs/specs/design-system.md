---
id: specs-design-system
title: "1:1 Design system layer and component catalog"
type: Concept
status: active
valid_from: 2026-11-04
scope:
  system: readerparser
relations:
  - type: relates_to
    target: ./novel-reader-and-design-system.md
  - type: relates_to
    target: ./reader.md
  - type: relates_to
    target: ./downloads.md
  - type: relates_to
    target: ./catalog-presentation.md
  - type: relates_to
    target: ./browse-discovery.md
  - type: relates_to
    target: ./library-search.md
  - type: relates_to
    target: ../CONTEXT.md
---

# 1:1 Design system layer and component catalog

## Problem Statement

1. **Fragmented screen-private UI components**: Across the presentation layer, most visual components (such as reading progress cards, chapter rows, download queue cards, status badges, section headers, and settings rows) live as private helpers inside individual screen content files rather than in a unified `Design system` layer.
2. **Visual inconsistency across themes and screens**: The `Light` and `Dark` theme palettes diverge from the editorial aesthetic of the `Warm dark theme` and `Sepia` palettes. Spacing, corner radii, elevation, and state feedback views (`Loading`, `Empty`, `Error`) lack shared tokens and reusable primitives.
3. **Inconsistent component patterns between screens**:
   - `Library` and `Browse` share one minimal `Series` card even though `Library` has local `Chapter user state` (unread count, downloaded count, and reading progress) while `Browse` displays remote catalog listings.
   - `Downloads` displays active per-chapter queue items in code without a storage capacity breakdown or per-series offline cache summary.
   - `Series` detail uses floating action buttons for bookmarking and range downloads instead of an integrated hero header card with an inline `Library` toggle, status badge, expandable synopsis, and chapter action sheet.
   - `Reader` controls differ between `Novel` and `Manhwa` modes and lack a unified bottom-sheet settings pattern and a minimal floating progress pill when immersive chrome is hidden.
4. **Implementation leakage in the design reference**: The interactive design prototype previously mixed backend architecture diagrams, code snippets, debug trackers, and unbacked catalog ratings with real UI components, preventing a strict 1:1 mapping between the visual reference and the `Design system` layer.

## Solution

1. **Two-level `Design system` architecture**: Build a dedicated `Design system` layer organized into **Design Tokens** (Color, Typography, Shape, Spacing), **Foundational primitives** (screen-agnostic controls and state feedback views), and **Domain components** (reusable catalog, series, download, and reader patterns).
2. **Four cohesive theme palettes**: Standardize `Warm dark theme`, `Sepia`, warm editorial `Light`, and slate-blue `Dark` (plus `System` automatic selection) across all semantic surface, text, accent, outline, and status tokens.
3. **Thin declarative screen assemblies**: Refactor each screen content target (`Library`, `Browse`, `Downloads`, `Series` detail, unified `Reader`, and `Settings`) to compose `Foundational primitives` and `Domain components` fed by existing domain and persistence state.
4. **Code-agnostic 1:1 visual reference**: Maintain the interactive design prototype as the canonical 1:1 visual contract for every token, primitive, domain component, variant, and screen state without implementation-specific code leakage.

## User Stories

1. As a reader using the app across different lighting conditions, I want to switch seamlessly between `System`, `Warm Dark`, `Sepia`, `Light`, and `Dark` themes, so that every screen and component adapts immediately with consistent contrast.
2. As a daytime reader, I want the `Light` theme to use warm ivory surfaces and terracotta/sage accents instead of harsh blue-white defaults, so that browsing and reading feel like printed paper.
3. As a night reader who prefers cool tones over warm amber, I want the `Dark` theme to use a cohesive slate-blue dark palette with balanced contrast, so that all four manual themes meet WCAG AA/AAA readability standards.
4. As a user navigating top-level destinations, I want a 3-tab bottom navigation bar (`Library`, `Browse`, `Downloads`) with an active pill indicator that hides automatically on `Series` detail, `Reader`, and `Settings`, so that primary navigation stays uncluttered.
5. As a user in `Library`, I want a persistent search input pill with a leading search icon and a trailing clear button, so that I can filter my bookmarked series by keyword at any time.
6. As a user in `Library`, I want a top-bar sort action that opens a sort sheet (`Default (Recently Added)` vs `Title (A–Z)`), so that I can order my bookmarked collection predictably.
7. As a user in `Library`, I want horizontal category filter chips (`All`, `Reading`, `Downloaded`, `Completed`) with live item counts derived from local `Chapter user state`, so that I can narrow my collection by reading or offline status with one tap.
8. As a user viewing `Library` cards, I want each `Series` card to display its 2:3 cover, an unread chapter count badge, the series title, current chapter/progress metadata (or downloaded chapter count), and a mini progress bar, so that I can see my reading status at a glance.
9. As a user in `Browse`, I want a compact source selector pill in the header that opens a `Source` picker bottom sheet listing installed plugins with their language and content type, so that I can switch sources without leaving `Browse`.
10. As a user in `Browse`, I want segmented mode tabs (`Popular`, `Latest`, `Search`) paired with a search input pill, so that I can switch between discovery feeds and keyword search cleanly.
11. As a user viewing `Browse` catalog cards, I want each `Series` card to display its 2:3 cover, content-type or `In Library` badge, title, and author/type subtitle without fake ratings or unscraped chapter counts, so that catalog cards reflect accurate source data.
12. As a user scrolling to the end of a `Browse` catalog grid, I want a pagination footer component that displays a `Load More` button, an inline loading spinner while fetching the next `Series page`, or an inline retry action on failure, so that multi-page browsing is clear and recoverable.
13. As a user on the `Downloads` screen, I want a storage breakdown card showing total offline usage split proportionally between `Novel` text and `Manhwa` images, so that I can monitor how much device storage my `Downloaded chapter` files consume.
14. As a user on the `Downloads` screen, I want an `Active Queue` section displaying each queued, running, or failed chapter with its series title, chapter title, running progress bar, state badge (`Queued`, `45%`, `Failed`), and `Cancel` or `Retry` button, so that I can control in-flight downloads.
15. As a user on the `Downloads` screen, I want a `Saved Offline (By Series)` section grouping completed `Downloaded chapter` files by `Series` with a `Clear` action, so that I can free offline storage for an entire series at once.
16. As a user opening a `Series` detail screen, I want a hero header card that displays the 2:3 cover, title, author, source name, `Series` status badge (`Ongoing`, `Completed`, `Hiatus`), genre tags, and an inline `In Library` / `Add to Library` toggle pill, so that all series metadata and bookmark controls live in one cohesive header.
17. As a user reading a long `Series` description on the detail screen, I want the synopsis inside the hero card to clamp to 3 lines by default with a `Show more` / `Show less` toggle, so that the chapter list remains visible above the fold.
18. As a user on the `Series` detail screen, I want a top-bar refresh action and an overflow menu (`Download Unread Chapters`, `Download Chapter Range`, `Mark All as Read`, `Mark All as Unread`), so that bulk series operations are always reachable without floating action buttons covering chapter rows.
19. As a user on the `Series` detail screen, I want a prominent reading progress card showing my current or next chapter, completion percentage pill, and primary `Start` / `Continue` / `Resume` / `Re-read` action button, so that I can jump back into reading with a single tap.
20. As a user browsing chapters on the `Series` detail screen, I want a chapter section header with total chapter count, `Asc`/`Desc` sort chip, `Unread` filter chip, and a quick `Download Unread` pill, so that I can navigate large chapter lists efficiently.
21. As a user scanning the chapter list on `Series` detail, I want each chapter row to visually distinguish `Unread` (full-contrast title + warm amber dot), `In-Progress` (accented border + completion percentage pill + inline progress bar), and `Completed` (muted title + check badge) alongside a `Downloaded` badge and upload date, so that I can immediately tell where I left off.
22. As a user interacting with a chapter row on `Series` detail, I want a trailing row action button (and long-press gesture) that opens a chapter action bottom sheet (`Mark as Read / Unread`, `Mark Previous Chapters as Read`, `Download Chapter`), so that I can manage chapter state without opening the `Reader`.
23. As a reader inside the unified `Reader`, I want tapping the reading canvas to toggle the shared immersive top bar (`Back`, chapter title, series/source subtitle, reader settings button) and bottom bar (`Prev`, interactive scrub slider with progress/page readout, `Next`, `Chapter List` drawer button, `Download` button), so that I have full navigation control when needed and an unobstructed canvas while reading.
24. As a reader with the immersive `Reader` bars hidden, I want a minimal floating progress pill in the bottom-right corner (`68%` for novels or `1 / 8` for manhwa), so that I can track my position without revealing the full overlay chrome.
25. As a novel reader in the `Novel text engine`, I want chapter HTML rendered as styled prose blocks (`Heading` H1 with bottom border, `Heading` H2 uppercase, justified `Paragraph` with bold/italic/strikethrough emphasis, `SceneDivider` `* * *`, and an `End of Chapter` card with a `Next Chapter` button), so that webnovels read like a typeset book.
26. As a novel reader, I want the top-bar `Aa` action to open a `Novel Typography` bottom sheet with font family selection (`Serif`, `Sans-Serif`, `Monospace`) and live sliders for font size (`12–32`), line height (`1.20x–2.00x`), paragraph spacing (`4–28`), and horizontal margins (`8–40`), so that I can adjust typography while viewing the chapter text underneath.
27. As a manhwa reader, I want the top-bar settings action to open a `Manhwa Settings` bottom sheet with segmented controls for `Reading Layout` (`Webtoon`, `Paged LTR`, `Paged RTL`) and `Image Scale` (`Fit Width`, `Fit Height`, `Original Scale`), so that the image canvas stays edge-to-edge without a persistent inline toolbar.
28. As a manhwa reader, I want loading pages to render a measurable placeholder card (`>= 150dp` minimum height), failed pages to render an error card with a `Retry` button preserving minimum height, and route/payload disagreements to render a `Content Format Mismatch` recovery banner with a `Retry` action, so that layout height never collapses and errors are recoverable in-place.
29. As a reader in either `Novel` or `Manhwa` mode, I want the bottom-bar `Chapter List` button to open an in-reader chapter list bottom sheet showing nearby chapters with active-chapter highlighting, so that I can jump to another chapter without returning to `Series` detail.
30. As a user opening `Settings` from any top-level screen's gear icon, I want a dedicated `Settings` screen organized by `SectionHeader` and `SectionDivider` into `Appearance`, `Novel Reader Defaults`, and `Manhwa Reader Defaults`, so that all global preferences are configurable in one place.
31. As a user encountering a loading, empty, or failed screen state on `Library`, `Browse`, `Downloads`, `Series` detail, or `Reader`, I want a consistent `LoadingStateView`, `EmptyStateView`, or `ErrorRetryStateView` primitive, so that feedback states look and behave uniformly across the entire app.

## Implementation Decisions

### Design Token System
- Organize theme tokens into four explicit categories inside the theme layer:
  1. **Color Tokens**:
     - `WARM_DARK`: Background `#181614`, Surface `#211E1B`, SurfaceContainer `#2A2521`, SurfaceContainerHigh `#352F2A`, OnBackground/OnSurface `#E6DFD5`, OnSurfaceVariant `#A89F93`, Outline `#4D453E`, OutlineVariant `#3A342E`, Primary `#D98A4E`, OnPrimary `#2B1708`, PrimaryContainer `#4A2E16`, OnPrimaryContainer `#FFDCC4`, Secondary `#7A9A8B`, OnSecondary `#12261D`, SecondaryContainer `#263D33`, OnSecondaryContainer `#BCE0CF`.
     - `SEPIA`: Background `#F4ECD8`, Surface `#EFE4C8`, SurfaceContainer `#E4D5B7`, SurfaceContainerHigh `#DAC7A4`, OnBackground/OnSurface `#2C221E`, OnSurfaceVariant `#5C4B3B`, Outline `#BCAB94`, OutlineVariant `#D1C2AC`, Primary `#8C5327`, OnPrimary `#FFFFFF`, PrimaryContainer `#F5D9C1`, OnPrimaryContainer `#361B06`, Secondary `#4F6D5A`, OnSecondary `#FFFFFF`, SecondaryContainer `#CFE4D6`, OnSecondaryContainer `#14291C`.
     - `LIGHT`: Background `#FAF8F5`, Surface `#FFFFFF`, SurfaceContainer `#F0ECE1`, SurfaceContainerHigh `#E6E1D4`, OnBackground/OnSurface `#1C1B1A`, OnSurfaceVariant `#605C56`, Outline `#C8C2B8`, OutlineVariant `#DDD8CE`, Primary `#B86528`, OnPrimary `#FFFFFF`, PrimaryContainer `#FDE8D7`, OnPrimaryContainer `#3D1C05`, Secondary `#536358`, OnSecondary `#FFFFFF`, SecondaryContainer `#D6E0D9`, OnSecondaryContainer `#101E15`.
     - `DARK`: Background `#111318`, Surface `#191C22`, SurfaceContainer `#22262F`, SurfaceContainerHigh `#2C313C`, OnBackground/OnSurface `#E2E2E9`, OnSurfaceVariant `#939AA6`, Outline `#444A57`, OutlineVariant `#323741`, Primary `#A2C8FF`, OnPrimary `#00325B`, PrimaryContainer `#004880`, OnPrimaryContainer `#D3E4FF`, Secondary `#BBC7DB`, OnSecondary `#253140`, SecondaryContainer `#3B4858`, OnSecondaryContainer `#D7E3F7`.
     - Semantic status colors: Error (`#E06C75` dark / `#BA1A1A` light) and Success (`#6CB687`).
  2. **Typography Tokens**:
     - UI scale for Hero/Display (`18–20sp` bold), Section/Card Title (`14–16sp` bold), Body (`13–14sp`), and Metadata/Badge Label (`11–12sp` semi-bold/bold).
     - Novel prose scale controlled dynamically by user preferences: Font family (`Serif`, `Sans-Serif`, `Monospace`), Font size (`12–32sp`, default `18sp`), Line height multiplier (`1.2x–2.0x`, default `1.6x`), Paragraph spacing (`4–28dp`, default `14dp`), and Horizontal padding (`8–40dp`, default `20dp`).
  3. **Shape Tokens**:
     - Small (`6dp` for inline tags and pin badges), Medium (`10dp` for series cards, chapter rows, and download cards), Large (`16dp` for hero cards and reading progress cards), Extra-Large (`24dp` top corners for modal bottom sheets), and Full (`9999dp` pill shape for search bars, chips, segmented tabs, and primary CTAs).
  4. **Spacing & Dimension Tokens**:
     - Spacing scale (`4dp`, `8dp`, `12dp`, `16dp`, `20dp`, `24dp`), `2:3` aspect ratio for all series covers, `56dp` top app bar height, `64dp` bottom navigation bar height, and `150dp` minimum height for manhwa page placeholders and error states.

### Foundational Primitives Catalog
Build and expose the following 10 reusable `Foundational primitive` components in the `Design system` layer:
1. `AppTopBar`: Configurable title, optional back navigation icon, and slot/presets for screen-specific action icons (`Sort`, `Settings`, `Refresh`, `More`).
2. `BottomNavBar`: 3-destination navigation bar (`Library`, `Browse`, `Downloads`) with active pill indicator.
3. `SearchInputPill`: Pill-shaped search text field with leading search icon, placeholder, trailing clear button when non-empty, and IME search action.
4. `ChipFilter` & `ChipFilterRow`: Horizontally scrollable row of selectable pill chips with active primary-container styling and optional count labels.
5. `SegmentedModeTabs`: Pill-container segmented tab selector with elevated active pill indicator.
6. `SourceSelectorPill` & `SourcePickerSheet`: Compact pill trigger showing active `Source` label and chevron, paired with a modal bottom sheet listing available sources.
7. `Badges`: `UnreadBadge` (primary pill overlay), `DownloadedBadge` (surface-high pin badge with secondary accent text), `StatusBadge` (`Ongoing`, `Completed`, `Hiatus`), and `QueueStateBadge` (`Queued`, running percentage `45%`, `Done`, `Failed`).
8. `SectionHeader` & `SectionDivider`: Uppercase/accented section title with optional trailing count/action and subtle `outlineVariant` horizontal rule.
9. `FormControls`: Labeled range slider (`SliderControl`), segmented option button group (`SegmentedOptionGroup`), and selectable radio row (`RadioRow`).
10. `StateFeedbackViews`: `LoadingStateView` (centered progress indicator + optional label), `EmptyStateView` (icon, title, subtitle, optional action button), and `ErrorRetryStateView` (error icon, message, and `Retry` button).

### Domain Components Catalog
Build and expose the following 12 reusable `Domain component` patterns in the `Design system` layer:
1. `SeriesCard`:
   - **Library Variant**: 2:3 cover with optional `UnreadBadge`, 1-line ellipsis title, metadata row (`Ch. X / Y · Z%` or `DownloadedBadge` count), and 4dp `MiniProgressBar`.
   - **Catalog Variant**: 2:3 cover with content-type or `In Library` badge, 2-line ellipsis title, and author/type subtitle (no unbacked star ratings or chapter counts).
2. `CatalogPaginationFooter`: Full-span grid footer supporting `Load More` button, inline loading spinner, and inline retry button.
3. `SeriesHeroCard` & `ExpandableSynopsis`: Elevated surface card containing 2:3 cover, title, author/source subtitle, `StatusBadge`, genre tags, inline `Library` toggle pill button, and a 3-line clamped synopsis with `Show more` / `Show less` toggle.
4. `ReadingProgressCard`: Highlighted gradient/accented card supporting `Start`, `Resume (%)`, `Continue`, and `Completed` states with completion pill and full-width primary CTA button.
5. `ChapterRow` & `ChapterActionSheet`:
   - 3 visual row states (`Unread` with warm amber dot and full-contrast title, `In-Progress` with primary border, completion pill, and inline progress bar, `Completed` with check icon and muted title), `DownloadedBadge`, relative date, and trailing `More` action button.
   - `ChapterActionSheet` modal bottom sheet providing `Mark as Read / Unread`, `Mark Previous Chapters as Read`, and `Download Chapter`.
6. `StorageBreakdownCard`: Card displaying used vs. available storage (`Novels` primary segment + `Manhwa` secondary segment) with legend.
7. `DownloadQueueItemCard`: Per-chapter queue card showing series title, chapter name, running-only linear progress bar, `QueueStateBadge`, and state-appropriate `Cancel` or `Retry` action.
8. `DownloadedSeriesCard`: Offline cached series summary card showing series title, cached chapter summary, and `Clear` action button.
9. `ReaderImmersiveBars`: Auto-hiding top overlay bar (`Back`, chapter title, series/source subtitle, reader settings icon) and bottom overlay bar (`Prev`, interactive scrub slider with percentage or page label, `Next`, `Chapter List` icon, `Download` icon).
10. `FloatingProgressPill`: Compact bottom-right corner pill showing current scroll percentage or page index (`X / N`) when `ReaderImmersiveBars` are hidden.
11. `NovelProseBlocks` & `NovelChapterEndCard`: Styled prose items (`Heading` H1/H2, justified `Paragraph` with bold/italic/strikethrough spans, `SceneDivider` `* * *`) and end-of-chapter card with `Next Chapter` CTA.
12. `ManhwaPageItem`, `ManhwaMismatchBanner`, & `ReaderBottomSheets`:
    - `ManhwaPageItem` supporting `Webtoon`, `Paged LTR`, and `Paged RTL` layouts with `Fit Width`, `Fit Height`, and `Original` scales, measurable loading placeholder card (`>= 150dp`), and failed image error card with `Retry`.
    - `ManhwaMismatchBanner` for unexpected `Chapter content` variant recovery.
    - `NovelTypographySheet`, `ManhwaSettingsSheet`, and `ReaderChapterListSheet` modal bottom sheets.

### Data & State Derivation Rules
- Derive `Library` category filter chip counts (`All`, `Reading`, `Downloaded`, `Completed`) and `Library` `SeriesCard` metadata (unread chapter count, downloaded chapter count, current chapter number, and reading progress ratio) from existing local `series` and `chapters` tables without adding unbacked remote fields.
- Derive `Downloads` `StorageBreakdownCard` byte totals and `DownloadedSeriesCard` groupings from the app-private `downloads/` directory (`meta.json` content types and file sizes) and local `chapters` rows where `downloaded = true`.
- Keep `Series` detail bookmarking and range/unread download actions in the `SeriesHeroCard`, `ChapterSectionHeader`, and top-bar overflow menu, removing redundant floating action buttons from `Series` detail and `Reader`.

## Testing Decisions

- **Good Test Principles**: Assert strictly on observable external behavior (`UiState` emissions, `Action` callbacks, and visible semantic nodes/text/tags), never on private composable implementation details or internal helper functions.
- **Seam 1 — Stateless Screen `*Content` Compose Semantics (`androidTest`)**:
  - Test `LibraryContent`, `BrowseContent`, `DownloadsContent`, `SeriesContent`, `ReaderContent`, and `SettingsContent` wrapped in `ReaderParserTheme`.
  - Verify that each screen renders the expected `Foundational primitive` and `Domain component` variants across `Populated`, `Loading`, `Empty`, and `Error` states, and that user interactions (search input, chip selection, sort/source/chapter bottom sheets, reader chrome toggle, typography/layout controls) invoke the corresponding `Action` callbacks.
  - Prior art: `LibraryContentTest`, `BrowseContentTest`, `DownloadsContentTest`, `SeriesContentTest`, `ReaderContentTest`, `SettingsContentTest`.
- **Seam 2 — ViewModel `UiState` Derivation (`test` on JVM)**:
  - Verify in `LibraryViewModelTest`, `DownloadsViewModelTest`, `SeriesViewModelTest`, `ReaderViewModelTest`, and `SettingsViewModelTest` that local repository flows and storage data map accurately into the `UiState` properties consumed by the `Design system` components.
  - Prior art: existing `*ViewModelTest` suite using hand-rolled JVM fakes.
- **Seam 3 — Theme Token & Prototype Contract Verification (`test` on JVM + CLI)**:
  - Verify `colorSchemeForTheme` across `WARM_DARK`, `SEPIA`, `LIGHT`, `DARK`, and `SYSTEM` in `ThemeTest`, and verify the HTML design system contract via `./.agents/skills/design-prototype/scripts/serve_prototype.py --check`.

## Out of Scope

- Cloud account sync, multi-user profiles, or external reading-tracker integrations.
- Remote star ratings or remote chapter-count scraping on `Browse` catalog listings.
- Paginated horizontal page-flip mode for `Novel` prose (continuous vertical scroll remains the canonical `Novel text engine` mode).
- Room database schema migrations (all `Library`, `Series`, and `Downloads` summaries derive from existing tables and app-private download files).

## Further Notes

- The interactive prototype at `docs/design/prototype.html` is the 1:1 visual and interaction reference for every token, `Foundational primitive`, and `Domain component` specified in this document.
