# Library Search via Samsung Search Delta

## ADDED Requirements

### Requirement: Library search debounces input and cancels superseded work

Library SHALL wait until a user-entered nonblank query remains unchanged for 300 ms before starting Samsung Search or local fallback. New input and blank input SHALL cancel pending debounce and active superseded search work. A response for a superseded query SHALL NOT replace current Library state even if cancellation completes late. Existing active-search invalidations caused by Library membership or downloaded/indexable changes SHALL rerun the current nonblank query immediately without another typing debounce.

#### Scenario: Stable Library input starts search

- **WHEN** a user-entered nonblank Library query remains unchanged for 300 ms
- **THEN** Library SHALL start its Samsung-first search flow

#### Scenario: Library input changes during debounce

- **GIVEN** a query is waiting for the debounce interval
- **WHEN** the user changes that query
- **THEN** the earlier debounce SHALL be cancelled
- **AND** only the latest stable nonblank query SHALL start search

#### Scenario: Library input changes during active search

- **GIVEN** a search is active for one query
- **WHEN** the user enters a different query
- **THEN** the earlier search work SHALL be cancelled
- **AND** its response SHALL NOT replace the newer query state if it completes late

#### Scenario: Library query becomes blank

- **WHEN** the current Library query becomes blank
- **THEN** pending or active search work SHALL be cancelled
- **AND** Library SHALL immediately show its normal observed list

#### Scenario: Eligible local data changes during active search

- **GIVEN** Library has a current nonblank query
- **WHEN** Library membership or downloaded/indexable eligibility changes
- **THEN** Library SHALL rerun that query immediately
- **AND** it SHALL still reject a response from the superseded result set

### Requirement: Provider failure falls back to eligible local matching

If Samsung Search is unavailable or its query fails, ReaderParser SHALL search one local snapshot of series rows that are both in Library and indexable by downloaded chapters. Local fallback SHALL match normalized title, author, or genres, prioritize title matches, and return a successful empty result when no eligible row matches.

#### Scenario: Samsung Search is unavailable

- **WHEN** the Samsung Search provider cannot execute a Library query
- **THEN** ReaderParser SHALL search eligible local rows
- **AND** Library search SHALL remain usable without a provider error state

#### Scenario: Local fallback matches metadata

- **GIVEN** an eligible Library row matches the query in title, author, or genres
- **WHEN** Samsung Search fails
- **THEN** the row SHALL appear in local fallback results

#### Scenario: Ineligible row matches

- **GIVEN** a matching row is outside Library or has no downloaded/indexable chapter
- **WHEN** local fallback runs
- **THEN** that row SHALL NOT appear

#### Scenario: No eligible local match

- **WHEN** Samsung Search fails and no eligible local row matches
- **THEN** Library SHALL show its normal no-matches state instead of a provider error

## MODIFIED Requirements

### Requirement: Active Library search uses Samsung Search query

When the user enters a nonblank Library search query, ReaderParser SHALL prefer Samsung Search's public ContentProvider at `content://com.samsung.android.smartsuggestions.search/v2/com.opus.readerparser.series` using `ContentResolver.query()` with a projection that includes at least `_id`, `title`, and `source_url`. Samsung Search failure or unavailability SHALL activate eligible local fallback rather than disable Library search.

#### Scenario: Non-blank query prefers provider results

- **WHEN** the Library screen receives a nonblank search query and Samsung Search succeeds
- **THEN** ReaderParser SHALL use Samsung Search results instead of local fallback ranking

#### Scenario: Non-blank query cannot use provider

- **WHEN** the Library screen receives a nonblank search query and Samsung Search is unavailable or fails
- **THEN** ReaderParser SHALL use eligible local fallback results

#### Scenario: Blank query keeps local library view

- **WHEN** the Library search query is blank
- **THEN** ReaderParser SHALL continue showing the normal observed library list

### Requirement: Library search resolves local rows and preserves provider ordering

ReaderParser SHALL resolve successful Samsung Search hits against one local snapshot of eligible series rows before displaying them. The UI SHALL show the local row's canonical title and cover, displayed results SHALL keep Samsung Search ordering, and result resolution SHALL NOT issue one Room lookup per provider hit.

#### Scenario: Local display data is used

- **WHEN** a Samsung Search hit maps to an eligible local series row with a different stored display title than the provider row
- **THEN** ReaderParser SHALL display the local row's title and cover

#### Scenario: Provider order is preserved

- **WHEN** Samsung Search returns hits in relevance order
- **THEN** ReaderParser SHALL display resolved eligible rows in that same order

#### Scenario: Multiple provider hits are resolved

- **WHEN** Samsung Search returns multiple hits
- **THEN** ReaderParser SHALL load the eligible local candidate set once
- **AND** resolve all hits from that snapshot by `(sourceId, url)`

## REMOVED Requirements

### Requirement: Provider failure is distinct from empty results

**Reason:** Samsung Search failure now activates local fallback, so provider failure is no longer exposed as a Library search error when local search can continue.

**Migration:** Existing failure handling is replaced by a successful local fallback result, including a successful empty result when no eligible local row matches.
