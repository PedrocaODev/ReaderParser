---
id: specs-reader
title: "Unified Reader behavior"
type: Concept
status: active
valid_from: 2026-10-08
scope:
  system: readerparser
relations:
  - type: supported_by
    target: ../evidence/specification-evolution.md
---

# Unified Reader behavior

This is the consolidated Reader contract. It includes separately filed offline and page-layout scenarios.

## Shared route and domain variants

NOVEL and MANHWA chapters use one Reader destination, screen, ViewModel, and UI state.
The route carries source ID, series URL, chapter URL, and content type.
Selecting either series type opens that shared destination with `ContentType.NOVEL` or `ContentType.MANHWA` respectively.
Saved navigation state initializes chapter-list context from those four route values.
The ViewModel must not resolve `SourceRegistry` or concrete sources.
`ChapterContent` remains sealed with exactly `Text(html)` and `Pages(imageUrls)`.

## Renderers and payload mismatch

Render `ChapterContent.Text` in the JavaScript-disabled HTML/text renderer.
Render `ChapterContent.Pages` as a vertical image list in reading order.
Check `DownloadStore.read(chapter)` before network retrieval for either route type.
Matching downloaded Text for NOVEL or Pages for MANHWA displays immediately without network access.
A null local payload uses `chapterRepository.getContent(chapter)`.
An initially loaded variant that disagrees with the route, including a non-null downloaded mismatch, must not be displayed.
Perform one forced network fetch that bypasses the downloaded payload.
If that response still disagrees, show a retryable unexpected-content error. Do not render it or loop forced requests automatically.

## Shared immersive controls

Tapping the reading surface toggles the shared top and bottom overlay controls.
Both renderers expose Back, previous chapter, next chapter, chapter list, Download, and appropriate progress.
Text chrome shows normalized scroll progress as a percentage. Page chrome shows the current page and total page count.
Back invokes navigation back.

## Text progress and read state

Clamp renderer-reported text progress to `[0f, 1f]`.
Persist each distinct value by `(sourceId, chapterUrl)`.
After WebView content layout finishes, restore the scroll position corresponding to stored normalized progress.
Mark a text chapter read immediately after successful display.

## Page progress and read state

Each new page load or reload starts at page zero.
Keep page position only in current Reader state. Do not restore or persist page position.
When the final image page becomes current, mark the chapter read.
An earlier page becoming current must not mark the chapter read solely because of that page change.

## Chapter actions and recovery

Available previous/next actions navigate to that chapter through the same Reader destination and content type.
Selecting a different chapter in the sheet preserves source ID, series URL, and content type.
Download enqueues the current chapter through the existing download flow for either content type.
After a load failure, Retry reloads the same chapter while retaining the current destination. It creates no additional navigation destination.
Chapter lists in series detail and Reader overlays show a download indicator for `downloaded=true` chapters.

## Measurable image placeholders

Every loading `AsyncImage` item in `ManhwaPageList` must have measurable minimum height, such as 100dp or a viewport fraction.
The LazyColumn scroll range must include all items before images load.
A failed image retains minimum height under the error painter and must not reduce the scroll range.
After successful loading, resize to natural image dimensions and adjust the scroll range accordingly.

## Complete AsuraScans chapter pages

`getChapterContent(chapter)` must return all actual AsuraScans chapter pages in `ChapterContent.Pages.imageUrls`.
The page count must match the chapter's actual count, with no omissions caused by JavaScript lazy loading.
If the API is unavailable or returns an unexpected format, use the existing HTML parser and return the pages it can extract.
Keep the source plugin's `id`, `name`, `lang`, `baseUrl`, and `type` unchanged.
