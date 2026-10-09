---
id: concept-reader-runtime
title: "Unified reader runtime"
type: Concept
status: draft
valid_from: 2026-10-08
scope:
  system: readerparser
relations:
  - type: supported_by
    target: ../evidence/ui-source-history.md
  - type: relates_to
    target: ../evidence/implementation-gaps.md
  - type: relates_to
    target: ../specs/reader.md
  - type: relates_to
    target: ../specs/downloads.md
---

# Unified reader runtime

## Shared workflow and domain boundary

One Reader destination, four-file screen set, state machine, and control layout serve NOVEL and MANHWA.
The design places loaded `ChapterContent` in Reader state and branches once in `ReaderContent`.
The former separate readers duplicated loading, effects, navigation, downloads, chapter selection, errors, and controls.
MANHWA had tap-controlled overlays while NOVEL had a fixed app bar. Both exposed historically inert Back or Retry actions.
Consolidation addresses this drift while retaining distinct content rendering.
Keeping two screens and extracting only controls was rejected because duplicate navigation, state, and effect logic would remain.

`ChapterContent` stays a sealed interface with exactly `Text(html)` and `Pages(imageUrls)`.
Text remains HTML rendered through a JavaScript-disabled WebView. Pages remain an ordered vertical image list.
Do not flatten content into one page model, synthesize text pages, or convert image pages into HTML.
Layout-dependent text pagination would change progress, selection, and accessibility semantics.
Use the exhaustive sealed-content branch rather than a new renderer class hierarchy.
Private renderer composables are sufficient until another reuse case appears.

Source, repositories, downloaded-content formats, Room schema, series/chapter identity, and dependencies were unchanged by consolidation.
Preferences, typography controls, orientation modes, new gestures, prefetching, chapter order, and chapter-sheet redesign were excluded.

## Explicit route context

The Reader route carries `sourceId`, `seriesUrl`, `chapterUrl`, and `contentType`.
The historical route form is `reader/{sourceId}/{seriesUrl}/{chapterUrl}/{contentType}`.
Series already knows content type and supplies one `onNavigateToReader` callback for either series type.
Preserve source ID, series URL, and content type through previous/next and chapter-sheet navigation to the target chapter.
Correct route context lets the ViewModel construct the typed series stub before content loading.
ViewModels must remain independent of `SourceRegistry` and concrete sources.
Inferring type only after loading cannot supply initial chapter-list context or perform mismatch validation.

Legacy internal reader routes were removed instead of compatibility aliases because no external reader deep links were documented.
Series deep links continue opening Series detail.
Route replacement can invalidate restored internal back-stack entries. Destination construction and SavedStateHandle arguments need tests.
One `ReaderScreen` wires state/effects and the existing `ReaderChapterListSheet`.
Old screens, contents, ViewModels, states, routes, and tests were to be removed only after unified coverage passed.

## Content loading and recovery

Keep offline-first repository loading of explicitly downloaded Text and Pages before network access.
For a route-type mismatch, perform one forced network fetch that bypasses `DownloadStore`.
This can recover a corrupt or stale download with the wrong content shape.
If the forced response still mismatches the explicit route type, show a visible retryable unexpected-content error.
Cancellation propagates instead of becoming an error.
Loading a new chapter clears prior content-specific state and shows loading feedback.

Retry reloads the current chapter through the ViewModel without another destination.
Historical tasks also required dead-chapter re-resolution. Store chapter URL so Retry can recover when the chapter row is initially missing.
Back calls the screen's navigation callback.
Download actions enqueue single chapters through the unified Reader while retaining existing queue semantics.

## Shared controls and gestures

Tapping content toggles top and bottom immersive overlays for both renderers.
The top bar shows chapter name and Back. The bottom controls offer previous/next, chapter list, download, and progress.
Text labels use a percentage. Page labels use `current / total`.
A small progress indicator remains available with controls hidden.
Tap detection must coexist with WebView scrolling/links and page scrolling.
Controls need accessibility descriptions and functional callbacks, not only visible affordances.

## Progress and read semantics requiring reconciliation

The design required text progress clamped to `[0f, 1f]`, persisted for each distinct renderer-reported value by `(sourceId, chapterUrl)`.
Load stored text progress and restore it after WebView layout. A successfully displayed text chapter is immediately marked read.
For pages, initialize each load at page zero, update in-screen centered-page position, and mark read only at the last page.
The design explicitly did not persist page position.
These precise timing requirements are retained as intended/historical claims, not asserted as fully verified current behavior.

The retrospective describes debounced text persistence, unlike the design's per-distinct-value requirement.
It reports separating `restoreProgress` from `pendingProgress` to resolve a scroll/restoration race.
The verification record says persistence is deferred until successful writes and restoration follows `WebViewClient.onPageFinished`.
The retrospective uses `webView.post` after that callback and warns that very long chapters need additional testing.
Consult [implementation gaps](../evidence/implementation-gaps.md#reader-progress-and-coverage) before accepting detailed timing or coverage claims.
Different renderer-only fields were intentionally retained rather than introducing polymorphic UI-state infrastructure.
The retrospective suggests replacing parallel `html`/`pages` fields with sealed loaded content to prevent impossible combinations.
That observation differs from the design's already-sealed loaded-state description.
That is a future improvement, not an implemented guarantee.

## Measurable image pages

`AsyncImage` with zero-intrinsic-size `ColorPainter` placeholders/errors and `ContentScale.FillWidth` could collapse while loading or failing.
A measurable minimum item height prevents LazyColumn from treating remaining pages as nonexistent.
The scroll repair shipped `Modifier.heightIn(min = 150.dp)` according to tasks and retrospective.
The design suggested a viewport fraction or a reasonable default such as `100dp`, acknowledging varied devices.
Retain the shipped value as historical implementation evidence rather than treating the design example as current.
Minimum height also avoids collapse for failed/expired/403 URLs and extreme aspect ratios such as `900×16000`.
It does not establish that every image loads or that incomplete source page discovery is solved.

## Verification and implementation lessons

State tests must cover Text/Pages, explicit route type, identity, mismatch recovery, dead Retry, cancellation, chapter actions, downloads, and progress/read timing.
Compose tests must cover both renderers, shared controls, tap visibility, progress labels, loading, retryable errors, Back, Retry, and chapter-sheet interaction.
Navigation tests must cover both series types, destination construction, process-restored arguments, and chapter-to-chapter replacement.
Repository tests must show downloaded Text/Pages load without a network call.
Image-layout tests should verify scroll range for all URLs in controlled loading/error states, or measurable height through snapshots.
The scroll plan allowed manual device verification when controllable image-state testing was infeasible.

The historical four test-first slices were unified state, shared content, route migration, and canonical documentation.
Reviews covered state/mismatch/cancellation first, gestures/accessibility next, routes/process restoration/offline behavior next, and documentation ownership last.
Every review finding needed fixing or explicit disposition before the next dependent slice.
Documentation synchronization was the explicit non-executable test exception after route/implementation tests.
The durable lesson is keeping architecture and navigation knowledge consistent after replacing entry points.
Its former codemap/OpenSpec synchronization mechanism is retired by the vault workflow.

Historical review fixes included remembered HTML/theme generation to avoid per-scroll rebuilds and `stopLoading()` plus `destroy()` on WebView disposal.
`processLoadedContent()` removed duplicated normal/forced-fetch branches, at the cost of another layer of indirection.
`ManhwaPageList` extraction improved cohesion, but the retrospective still reported about 300 lines of overlay chrome.
The retrospective suggested checking explicit WebView destruction and adding tap/Back/route tests, despite verification claiming related issues resolved.
Those conflicts remain visible in [history](../evidence/ui-source-history.md#reader-verification-and-retrospective).
