## Context

- BrowseViewModel starts with `mode = BrowseMode.POPULAR`, loads sources, selects the first one, but never triggers a fetch. The `autoFetchCurrentMode` helper exists and works correctly from `SelectSource` and `SetMode` handlers — it's simply not called from `init`.
- AsuraScans chapter pages use `<div data-page="N">` containers with `<img>` tags. The site runs on Astro and may use JavaScript-driven lazy loading for images past the initial viewport. Jsoup has no JS engine, so `doc.select("div[data-page] img")` only captures `<img>` elements present in the server-rendered HTML.
- The ManhwaPageList composable uses `AsyncImage` with `ColorPainter` (zero intrinsic size) as placeholder/error painter and `ContentScale.FillWidth`, causing collapsed items before images load or on error.

## Goals / Non-Goals

**Goals:**
- Browse screen fetches and displays Popular series automatically on first launch.
- AsuraScans chapter content includes all page images, not just server-rendered ones.
- Manhwa reader scrolls through all available pages without unexpected truncation.

**Non-Goals:**
- No changes to the `Source` interface or `HtmlSource` contract.
- No architectural changes to the reader or browse data flow.
- No changes to novel reading (only manhwa).
- No performance optimization of image loading beyond what fixes the scroll issue.

## Decisions

### Decision: Use AsuraScans API instead of HTML parsing for chapter content

The AsuraScans site provides an API at `https://api.asurascans.com/api/novels/{slug}/download` that returns JSON with page image URLs. This avoids the Jsoup lazy-loading limitation entirely.

However, this decision depends on whether the API returns full page lists for all chapters. If the API is unreliable, fall back to enhancing the HTML parser to extract image URLs from additional page attributes or metadata.

**Rationale:** The existing `chapterPagesParse` pattern is fundamentally limited against JS-rendered content. Switching to the API is more robust than trying to work around Jsoup's limitations.

### Decision: Add minimum height to AsyncImage items in ManhwaPageList

Set `Modifier.heightIn(min = /* viewport-dependent value */)` on each AsyncImage item to prevent zero-height collapse during loading/error states. This lets LazyColumn measure correctly regardless of image load state.

**Rationale:** Even with the API fix, individual image URLs may fail (403, expired). A minimum height prevents the list from collapsing during transient errors.

### Decision: Update BrowseViewModelTest to match new init behavior

The existing test `init loads sources and selects first source without fetching` must be renamed and updated to assert that the init block triggers a fetch.

**Rationale:** The test explicitly encodes the buggy behavior. Updating it is required.

## Risks / Trade-offs

- **API reliability:** If the AsuraScans API changes format or requires authentication, the HTML parser fallback path must still work. The fix should check the API response format before switching.
- **Minimum height value:** A fixed minimum height may be too large for devices with varying screen sizes. Use a viewport-height fraction or a reasonable default (e.g., 100dp) that works across devices.
- **Scope creep:** If the API approach doesn't work for all chapters, consider whether to invest in the HTML parser enhancement or scope the fix to the Browse issue only.
