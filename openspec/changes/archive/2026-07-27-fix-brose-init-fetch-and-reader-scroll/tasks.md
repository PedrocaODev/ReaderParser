## 1. Browse — init fetch on launch

- [x] 1.1 Add `autoFetchCurrentMode(first.id, BrowseMode.POPULAR)` call at end of `BrowseViewModel.init`
- [x] 1.2 Update `BrowseViewModelTest` — rename test from `init loads sources and selects first source without fetching` to `init loads sources and fetches popular`, update assertion to verify `fetchPopular` is called
- [x] 1.3 Run BrowseViewModel tests to confirm green

## 2. AsuraScans — full chapter page list

- [x] 2.1 Verify the AsuraScans API at `https://api.asurascans.com/api/series/{series_slug}/chapters/{chapter_slug}` returns the complete page list for sample chapters
- [x] 2.2 Implement API-based `getChapterContent` in AsuraScans with HTML fallback
- [x] 2.3 N/A (enhancement not needed — API works correctly)
- [x] 2.4 Update AsuraScans tests — existing `chapterPagesParse` override retained for fallback, all existing tests pass
- [x] 2.5 Run AsuraScans tests to confirm green

## 3. Manhwa page list — minimum height for scroll

- [x] 3.1 Add `Modifier.heightIn(min = 150.dp)` to each `AsyncImage` item in `ManhwaPageList` to prevent zero-height collapse
- [x] 3.2 Verify scroll behavior — manual verification: minimum height ensures LazyColumn items have measurable height regardless of image load state
- [x] 3.3 Run reader tests to confirm no regressions
