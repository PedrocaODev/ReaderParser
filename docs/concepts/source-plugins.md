---
id: concept-source-plugins
title: "Source plugins and parsing"
type: Concept
status: active
valid_from: 2026-10-08
scope:
  system: readerparser
relations:
  - type: supported_by
    target: ../evidence/navigation-and-tooling-history.md
---

# Source plugins and parsing

## Contract and template methods

`data/source/Source.kt` defines the stable contract shown in [Architecture](../architecture.md#source-interface).
Human approval is required to change it. The old map's seven-suspend-method count is wrong: there are six retrieval methods and synchronous supports.
HtmlSource is an HTML convenience base. Keep site-specific logic in plugins rather than add it to this base.
fetchDocBody performs the shared Ktor GET. Override it for site headers while fetchDoc retains Jsoup parsing with the request URL as base.
Popular combines popularUrl, popularSelector, seriesFromElement, and popularNextPageSelector.
Search combines searchUrl and optional searchSelector/searchSeriesFromElement/searchNextPageSelector overrides.
The optional search methods default to the corresponding popular methods.
Detail retrieval uses seriesDetailsParse on an incoming copy with the plugin content type.
Chapter listing uses chapterListSelector and chapterFromElement. Latest has no useful base default and must be implemented per site.
Content dispatches by type into chapterTextParse or chapterPagesParse. Override only the relevant one.
The other base parser raises IllegalStateException if erroneously called.
supports defaults true and can restrict supported filters.
Next-page selectors can be null. A missing indicator yields hasNextPage=false.

## Identity and registration

SourceRegistry is a Hilt-populated compile-time Map<Long,Source> with no dynamic loading or PackageManager scanning.
SourceModule instantiates plugins with the shared HttpClient and associates them by id.
Plugins derive id from computeSourceId(name,lang,type), never manually chosen constants.
One lowercase site directory contains `<SiteName>.kt`, normally extending HtmlSource.
Site-specific selectors, URL patterns, dates, and HTTP headers remain isolated from other plugins and UI.
Current plugins include `sources/asurascans/AsuraScans.kt` (MANHWA, en, https://asurascans.com) and
`sources/freewebnovel/FreeWebNovel.kt` (NOVEL). Actual code is the authority for live endpoints.

## Errors and extraction rules

Sources throw on errors instead of logging or returning null sentinels.
Keep broad catch-and-mask logic out of normal source methods. Optional data maps to nullable domain fields.
Use selectFirst with null-safety instead of select(...).first(). Use absUrl for href and image sources and trim extracted text.
The current Asura API-to-HTML fallback is a specific existing recovery path, documented separately rather than a general error-policy rewrite.
Detail title selection and pagination contracts are in [Catalog integrity](../specs/catalog-presentation.md#detail-title-selection)
and [Source pagination](../specs/source-pagination.md).

## Asura listings and details

Popular page one uses /browse. Later pages use /browse?page=N. Search URL-encodes query into /browse?search=... and adds page for later pages.
Cards use `#series-grid div.series-card`, `h3` titles, `a[href^="/comics/"]` links, `img` covers, and `span.capitalize` status.
Enabled next page uses `a[aria-label="Next page"]:not(.opacity-25)`.
Latest is a separate homepage scraper for its `div.grid.grid-cols-12.gap-2.py-4.px-2.border-b` rows, terminal beyond page one.
The historical detail selectors are `a[href^="/browse?author="]` for author and `a[href^="/browse?artist="]` for artist.
Genres use all `a[href^="/browse?genres="]` links. Description uses HTML-decoded `<meta name="description">` content.
Status uses the first `span.capitalize` matching ongoing/completed/hiatus/cancelled.
Cover uses `#desktop-cover-container img` with listing-cover fallback.
fetchDocBody applies browser User-Agent, Accept, and Accept-Language headers using the shared client.
The historical User-Agent emulates Chrome 125 on Windows. Headers were intended to improve site compatibility, not establish bypass guarantees.

## Asura chapters and dates

Chapter anchors use `a[data-astro-prefetch="hover"][href*="/chapter/"]`.
Names parse case-insensitive `Chapter\s+(\d+(?:\.\d+)?)` with -1f for unparseable numbers.
Relative dates handle hours/days/weeks/months/years ago, last week as seven days, and absolute MMM d, yyyy, returning epoch milliseconds when parseable.
Current chapter retrieval first calls `https://api.asurascans.com/api/series/{series_slug}/chapters/{chapter_slug}`.
It derives series slug from chapter.seriesUrl and chapter slug from integer chapter number, then maps data.chapter.pages URLs.
Empty/unavailable/unexpected API results use the HTML fallback `div[data-page] img` absolute src URLs.
The old static-HTML-only/no-JSON description is historical and cannot guide current complete-page extraction.

## New source workflow and tests

The existing /new-source convention takes PascalCase site name, base URL, NOVEL/MANHWA, and optional ISO 639-1 language defaulting to en.
Create `app/src/main/java/com/opus/readerparser/sources/<sitename>/<SiteName>.kt` and register it in core/di/SourceModule.kt.
Obtain actual listing/detail/chapter selectors and URL patterns before parser implementation. Do not invent them from a site name.
Scaffolding may create placeholder fixtures, but real saved HTML is required to verify parsing.
Keep fixtures in app/src/test/resources/fixtures/<sitename> and JVM tests in app/src/test/kotlin/com/opus/readerparser/sources/<sitename>.
Use MockEngine fixtures for popular/latest/search/details/chapters/content and edge cases.
Check missing optional elements, relative URLs, disabled next-page links, unknown statuses, chapter-number failures, and relative-date forms.
