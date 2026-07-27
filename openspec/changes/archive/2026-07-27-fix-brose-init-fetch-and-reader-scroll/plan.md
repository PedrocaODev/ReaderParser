## Implementation slices

### Slice 1: Browse init fetch

**Tasks:** 1.1, 1.2, 1.3
**Tests first:** Update existing test in `BrowseViewModelTest` — rename to `init loads sources, selects first source, and fetches popular`, change assertion to verify `fetchPopular` is called. Test fails first before production code change.
**TDD exception:** none

### Slice 2: Manhwa page list minimum height

**Tasks:** 3.1, 3.2
**Tests first:** Add a test in `ReaderContentTest` (or similar) that renders `ManhwaPageList` with a list of URLs and verifies the LazyColumn scroll range includes all items regardless of image load state. If a reliable viewport-height test is not feasible, verify with a snapshot test that each item has measurable height.
**TDD exception:** If image load state cannot be reliably controlled in unit tests, the scroll test may be verified via manual testing on device after the production change.

### Slice 3: AsuraScans full chapter page list

**Tasks:** 2.1, 2.2/2.3, 2.4, 2.5
**Tests first:** Fetch a sample chapter URL and determine if the API or an HTML enhancement works. Write fixture-based tests that verify the returned page list matches expected content.
**TDD exception:** none

## Review checkpoints

- After Slice 1: Review the BrowseViewModel init change and updated test for correctness.
- After Slice 3: Review the AsuraScans chapter content change for robustness (API fallback path, if applicable).

## Final verification intent

```bash
ANDROID_HOME=/home/pedro/Android/Sdk ANDROID_SDK_ROOT=/home/pedro/Android/Sdk ./gradlew :app:testDebugUnitTest --console=plain
```

## Intended commit grouping

- Commit 1 `fix: Browse screen now fetches popular series on init`
- Commit 2 `fix: Manhwa page list maintains minimum height to prevent scroll truncation`
- Commit 3 `fix: AsuraScans returns full chapter page list`
