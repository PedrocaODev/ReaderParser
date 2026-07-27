# AsuraScans Chapter Content Specification

## MODIFIED Requirements

### Requirement: Chapter pages include all images

The AsuraScans source returns all page images for a chapter, not only those rendered in server-side HTML.

#### Scenario: API returns complete page list

Given a chapter URL on AsuraScans
When `getChapterContent(chapter)` is called
Then the returned `ChapterContent.Pages.imageUrls` contains all pages for that chapter
And the count matches the actual number of chapter pages
And no pages are missing due to JavaScript lazy loading

#### Scenario: API fallback preserves existing behavior

Given the AsuraScans API is unavailable or returns an unexpected format
When `getChapterContent(chapter)` is called
Then the implementation falls back to the existing HTML parser
And returns whatever pages the HTML parser can extract

### Requirement: Source identity unchanged

The AsuraScans source plugin retains its `id`, `name`, `lang`, `baseUrl`, and `type`.
