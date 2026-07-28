# Source Metadata Cache Specification

## ADDED Requirements

### Requirement: Parsed source metadata uses bounded TTL caching

ReaderParser SHALL keep successful parsed catalog pages, search pages, series details, and chapter lists in bounded in-memory LRU caches. Catalog/search entries SHALL expire after five minutes with a maximum of 100 entries, detail entries SHALL expire after fifteen minutes with a maximum of 50 entries, and chapter-list entries SHALL expire after two minutes with a maximum of 20 entries. Expiry SHALL use monotonic elapsed time.

#### Scenario: Fresh metadata is requested again

- **GIVEN** a successful metadata result is cached
- **WHEN** the same exact request occurs before its TTL expires
- **THEN** ReaderParser SHALL return the cached parsed result without calling the source

#### Scenario: Metadata entry expires

- **GIVEN** a cached metadata result has exceeded its TTL
- **WHEN** the same request occurs again
- **THEN** ReaderParser SHALL call the source and replace the expired entry after success

#### Scenario: Cache reaches capacity

- **GIVEN** the metadata cache is at its configured capacity
- **WHEN** another successful result is inserted
- **THEN** the least-recently-used entry SHALL be evicted

#### Scenario: Operation TTL differs

- **GIVEN** a catalog/search, detail, and chapter-list entry were inserted at the same monotonic time
- **WHEN** two minutes have elapsed
- **THEN** the chapter-list entry SHALL be expired
- **AND** the catalog/search and detail entries SHALL remain fresh

### Requirement: Metadata cache keys isolate request identity

Catalog and search cache keys SHALL include source ID, operation, normalized query, page, and an immutable ordered snapshot of `FilterList`. Filter values SHALL use structural value equality and list order SHALL remain significant. Detail and chapter-list keys SHALL include source ID and series URL. Results from one source, operation, query, page, or filter set SHALL NOT satisfy a different request.

#### Scenario: Same query targets different source types

- **GIVEN** a NOVEL source result is cached for a query
- **WHEN** the same query is issued for a MANHWA source
- **THEN** the NOVEL result SHALL NOT be returned

#### Scenario: Search page differs

- **GIVEN** page one of a search is cached
- **WHEN** page two is requested
- **THEN** page one SHALL NOT satisfy the page-two request

#### Scenario: Equivalent immutable filter snapshot repeats

- **GIVEN** two filter lists contain structurally equal filter values in the same order
- **WHEN** both produce otherwise identical requests
- **THEN** they SHALL use the same cache-key value

#### Scenario: Filter order differs

- **GIVEN** two filter lists contain the same filters in a different order
- **WHEN** cache keys are created
- **THEN** they SHALL produce different keys

#### Scenario: Caller mutates its filter collection

- **GIVEN** a cache key was created from a filter list
- **WHEN** the caller later changes its own collection reference
- **THEN** the stored immutable key SHALL remain unchanged

### Requirement: Only successful remote metadata becomes reusable remote truth

Failed requests and local fallback results SHALL NOT be stored as successful remote cache entries.

#### Scenario: Source request fails

- **WHEN** a source metadata request throws
- **THEN** no successful cache entry SHALL be created for that request

#### Scenario: Search uses local fallback

- **WHEN** a failed or empty source search displays locally matched rows
- **THEN** those fallback rows SHALL NOT prevent a later remote retry after connectivity recovers

### Requirement: Reader payload ownership remains unchanged

The metadata cache SHALL NOT store opened chapter HTML, manhwa image payloads, or image files. Explicit downloaded chapter content SHALL remain owned by `DownloadStore`, images SHALL remain owned by Coil, and raw HTTP transport caching SHALL remain owned by OkHttp.

#### Scenario: Undownloaded chapter is opened

- **WHEN** a reader fetches chapter content that was not explicitly downloaded
- **THEN** the metadata cache SHALL NOT persist that chapter payload as a downloaded chapter
