## Why

Two bugs affect the core reading experience:

1. **Browse — empty grid on launch.** The Browse screen defaults to "Popular" tab but shows an empty grid with no loading indicator. Users see nothing until they tap a different tab or reopen the source dropdown. Root cause: `BrowseViewModel.init` selects the first source but never fires the initial fetch.

2. **Manhwa reader — scroll lock mid-chapter.** When reading manhwa, the page list sometimes truncates, preventing scroll to remaining pages. Most likely root cause: the AsuraScans chapter page uses JavaScript-driven lazy loading for images. Jsoup (no JS engine) only captures images rendered in the initial HTML — typically the first N pages — truncating the page list. Secondary factor: `AsyncImage` with `ColorPainter` (zero intrinsic size) as placeholder/error painter collapses failed/loading items to 0px height, making the LazyColumn think it's reached the end.

## What Changes

- Fix `BrowseViewModel.init` to trigger the initial fetch after selecting the first source.
- Fix `AsuraScans.chapterPagesParse` to capture all chapter page images, not just JS-rendered ones.
- Fix `ManhwaPageList` to prevent zero-height items from truncating scroll.

## Capabilities

### New Capabilities

None — both fixes restore expected behavior.

### Modified Capabilities

- **Browse screen:** Auto-fetches popular series on first launch instead of showing an empty grid.
- **AsuraScans source plugin:** Chapter image parsing handles lazy-loaded images, returning the full page list.
- **Manhwa page list:** Image items with failed/loading states maintain minimum height so LazyColumn measures correctly.

## Impact

- Low risk. The Browse fix is one line in the `init` block. The reader fix is scoped to the AsuraScans source (image parser) and the `ManhwaPageList` composable (minimum height). Neither changes domain contracts or the `Source` interface.
- The source-side fix may need to switch from Jsoup HTML parsing to the AsuraScans API for chapter pages, if the site provides one.
- Existing test for `BrowseViewModel` explicitly asserts no fetch on init (`init loads sources and selects first source without fetching`) — this test must be updated.
