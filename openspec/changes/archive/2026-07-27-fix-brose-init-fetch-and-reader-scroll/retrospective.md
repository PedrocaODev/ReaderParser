## Retrospective

### What shipped

- **Slice 1:** BrowseViewModel now calls `autoFetchCurrentMode()` at the end of `init`, so the default "Popular" tab immediately loads series instead of staying empty until the user interacts.
- **Slice 2:** ManhwaPageList items now have `Modifier.heightIn(min = 150.dp)`, preventing zero-height AsyncImage placeholders from collapsing LazyColumn items and truncating scroll.
- **Slice 3:** AsuraScans chapter content now fetches page image URLs from the internal API (`/api/series/{slug}/chapters/{slug}`) instead of HTML parsing. Falls back to Jsoup HTML parsing on any API failure. The API also provides width/height metadata per page for future enhancements.

### What went well

- API discovery was quick once the correct endpoint pattern was known (`series_slug/chapter_slug` instead of series_slug/chapters with IDs).
- Each slice had a clean boundary making delegation to @fixer straightforward.
- The API-first approach with HTML fallback means existing tests continued to pass without fixture changes.

### What to watch next time

- `chapterSlug` constructed as `chapter-{N}` works for current chapters but some older chapters use UUID slugs. The HTML fallback path covers those.
- Vector images in manhwa chapters may have extreme aspect ratios (e.g., 900×16000). The 150dp minimum height ensures they don't collapse to zero before loading.

### Follow-ups

- None.
