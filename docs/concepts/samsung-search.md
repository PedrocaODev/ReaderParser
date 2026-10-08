---
id: concept-samsung-search
title: "Samsung Search metadata integration"
type: Concept
status: active
valid_from: 2026-10-08
scope:
  system: readerparser
relations:
  - type: supported_by
    target: ../evidence/infrastructure-history.md
  - type: relates_to
    target: ../specs/samsung-search-index.md
  - type: relates_to
    target: ../specs/library-search.md
  - type: relates_to
    target: ../adr/0107-optional-series-metadata-index.md
  - type: relates_to
    target: ../adr/0108-room-index-eligibility.md
  - type: relates_to
    target: ../adr/0109-small-index-rebuild.md
  - type: relates_to
    target: ../adr/0110-provider-ranking-local-display.md
---

# Samsung Search metadata integration

The linked specifications own requirements. This concept preserves integration reasoning and historical contract repairs.
Dates below refer to original source context. The migration date is 2026-10-08.
The documentation migration performs no Samsung provider calls or device tests.

## Optional metadata scope

The June 10 change aimed to let Samsung Smart Suggestions discover downloaded series and open their Series screen from outside ReaderParser.
It narrowed an older `onboard-readerparser-v2` idea that included series and chapters with library-based eligibility.
The scope is series with at least one chapter whose Room `downloaded` flag is true. Chapter documents, library-only series, and browse-only series are excluded.
Series need not be in the library to enter the system index. Library search later adds its own in-library restriction.

The integration uses the public v2 ContentProvider at `content://com.samsung.android.smartsuggestions.search/v2`.
ReaderParser remains usable when the provider is absent or fails. Availability failure skips operations, and exceptions are caught and logged as warnings.
Schema failure never blocks startup. The original design planned retry on the next launch.
No Samsung engine changes, domain model changes, Room columns, tables, or schema-version migration were part of the index addition.
Query templates and schema migration/version management were deferred. The design treated first-version `register_schema` as idempotent.

## Room eligibility and stale files

The projection joins `series` to `chapters` on both `s.sourceId = c.sourceId` and `s.url = c.seriesUrl`.
It filters `c.downloaded = 1`, selects `DISTINCT s.*`, and orders titles ascending.
`SeriesDao.observeIndexableSeries()` provides Flow updates. `getIndexableSeries()` provides a one-shot query for explicit rebuilds.
Composite series identity remains `(sourceId, url)`.

A separate maintained `search_index` table was rejected because it creates a second source of truth that can drift.
The original account says workers set `downloaded` only after files are written and normal `DownloadRepository.deleteDownload()` resets it on deletion.
Files removed outside that normal path can leave a stale flag and a search result whose chapter payload is gone.
This was accepted for the MVP. Checking every file during every sync was rejected as expensive and outside scope.
Optional later file validation remained a possible follow-up. The original assessment called stale results a UX issue rather than a crash or corruption risk.

## Document fields and navigation

Schema name is `com.opus.readerparser.series`. The design says names must start with the caller package under `validateSchemaName()`.
`_id` is a string key `{sourceId}:{seriesUrl}`. It preserves both parts of composite identity.

| Field | Historical indexing intent |
| --- | --- |
| `title` | Searchable text title. |
| `author` | Searchable text author. |
| `description` | Searchable text with `stored=false`. |
| `genres` | Searchable comma-separated text. |
| `status` | String such as ONGOING or COMPLETED. |
| `type` | String NOVEL or MANHWA. |
| `source_url` | Stored string deep link to Series. |

There are no `chapter_name`, `chapter_number`, or `doc_type` fields.
The XML asset was `app/src/main/assets/search/samsung-search-indexable-series.xml`.
`SamsungSearchSchema.kt` reads its bytes for registration. `toContentValues()` maps the local series metadata to the schema.

The intended URI was `readerparser://series/{sourceId}/{seriesUrl}`, resolving to `Destinations.SERIES`, route `series/{sourceId}/{seriesUrl}`.
The main Activity handles the URI in `onCreate()` and `onNewIntent()`, extracting Long `sourceId` and String `seriesUrl`.
An intent-filter declares the custom scheme. An explicit component intent was rejected because it couples Samsung Search to an internal Activity class.
Search results open series detail, rather than the reader.

## Rebuild lifecycle

`SearchIndexSyncer` observes the Room indexable set and debounces changes, especially during a batch download.
Each rebuild queries the full eligible set, deletes the old search documents, and bulk-inserts the current set.
The proposal's phrase “rebuilds the index incrementally” conflicts with its design and task descriptions of full replacement.
The explicit intended MVP strategy was full rebuilding without document diffing.
The client divides bulk insertion into chunks of 100 documents.

The set was expected to contain dozens of downloaded series, rather than thousands.
Per-document insert/update/delete diffs were rejected as disproportionate complexity for a small set.
The design suggested reconsidering diffs above 500 series. This is a historical planning threshold, not a measured limit.
Periodic polling was rejected because Room Flow is reactive.

`App.onCreate()` checks availability, registers the schema, and starts observation only after successful registration.
Observation uses an application-lifecycle coroutine scope.
`SearchModule.kt` supplies singleton client and syncer dependencies through Hilt and injects the syncer into `App`.
`SamsungSearchClient` exposes `registerSchema()`, `bulkInsert()`, and `deleteAll()` with error handling.

`SamsungSearchUpdateReceiver` accepts `com.samsung.android.smartsuggestions.search.ACTION_UPDATE_INDEX` and schedules a one-time WorkManager rebuild through `rebuildIndex()`.
The receiver's manifest permission is `com.samsung.android.smartsuggestions.search.permission.SEND_ACTION_UPDATE_INDEX`.
Historical manifest tasks added `com.samsung.android.smartsuggestions.search.permission.WRITE`, `.READ`, and `.UPDATE_INDEX`.
The broadcast and Room triggers use the same rebuild path. The migration adds no permissions.

## Registration repair and compatibility boundary

The repair source was created 2026-06-11 and archived under a 2026-07-06 directory. These dates describe creation and archive naming separately.
Its proposal calls the problem “two bugs” but enumerates three distinct defects. All three are preserved here.
The old `getType()` availability probe returned null for this provider and disabled startup integration.
The old registration omitted the required schema name in extras. The old XML used `<search-scheme>` without required `<fieldType>` declarations.
The repair claimed these defects prevented activation on all devices. Device verification remained unchecked, so that universality is a historical claim.

The corrected probe calls `request_search_api_version` with null arg and extras, through `SearchProviderDelegate`.
Availability requires a non-null Bundle with `response_search_api_version` present and at least 1.
Null, missing, zero, or implausible values fail the probe. Exceptions return false.
`METHOD_REQUEST_API_VERSION` names the method constant.
Registration sends `extras["name"] = "com.opus.readerparser.series"` and `extras["schema-content"]` containing XML bytes.
It calls `register_schema` with null `arg`. The source says `PublicSchemaManager.registerSchema()` reads `extras.getString("name")`.

The corrected XML uses `<schema>` with `name`, `package`, `version`, and `keyFieldName` attributes.
It declares `string_field` and `text_field` through `<fieldType>`.
The bounded repair keeps `App`, `SearchIndexSyncer`, `SearchProviderDelegate`, schema fields, and manifest permissions unchanged.
The repair design asserted that startup and sync logic were correct. Its unchecked device tasks limit that assertion to source reasoning.
It accepts higher API versions without negotiation. A changed method or removed version field fails gracefully.
Basic registration and bulk insertion were described as stable across versions. That is historical rationale, not a current compatibility guarantee.
This is a provider contract repair, with no qualifying architectural ADR.

## Library search and canonical local data

The Library-search source was created 2026-07-07 and archived under 2026-07-08.
It replaced in-memory title filtering for non-blank queries, which the proposal said could surface stale or non-indexed rows.
The client adds a `query()` wrapper using `ContentResolver.query(SCHEMA_URI, projection, selection, selectionArgs, null)`.
`SearchProviderDelegate` and `ContentResolverDelegate` receive query support.

The historical design returns a pure domain sealed result through a `SeriesRepository` method so provider failure differs from empty success.
Each provider hit resolves to local Room data using composite identity. Only rows both in-library and downloaded/indexable survive.
The displayed title and cover come from Room. Provider order remains unchanged during active search, because local sorting would discard ranking.
One DAO lookup per hit was accepted as simpler than an order-preserving custom join for small result sets.
Provider selection syntax may vary. The source planned simple selection and fake-delegate tests, with failures surfaced separately.

For blank queries, the normal library list remains reactive and locally sorted.
`LibraryViewModel` uses repository-backed search for non-blank queries, exposes failures separately, and revalidates an active search when membership or indexability changes.
Browse search, other sort/filter behavior, Source contracts, schema fields, and manifest permissions were outside scope.

## Library search discrepancy

Source inspection on 2026-10-08 found `SeriesRepositoryImpl.searchLibrary()` trimming input and returning empty success for blank input.
It obtains one eligible snapshot through `seriesDao.getLibraryIndexableSeries()`.
On provider success it builds a composite-key map, resolves hits in their returned order, and drops unresolved or ineligible entries.
On provider failure it returns `LibrarySearchResult.Success` with eligible local title, author, or genre matches.
This differs from the July design's no-local-fallback rationale, separate failure display, and one-lookup-per-hit trade-off.
These are source observations only. No test run or accepted supersession is asserted.
The later intended Library specification retains its own evolution and uncertainty. This migration does not repair application behavior.

## Verification scope

The [historical verification record](../evidence/infrastructure-history.md#samsung-verification-claims) records instrumentation placement, fake seams, completed claims, and remaining device checks.
It separates successful build claims from unchecked on-device activation.
