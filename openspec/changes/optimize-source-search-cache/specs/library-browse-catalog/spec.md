# Library Browse Catalog Delta

## ADDED Requirements

### Requirement: Browse reuses fresh exact search results

Browse SHALL return a fresh cached search page without issuing another source request when source, normalized query, page, and the immutable ordered filter snapshot exactly match the cached key.

#### Scenario: Exact query is repeated within TTL

- **GIVEN** a successful page is cached for one source and normalized query
- **WHEN** Browse repeats the exact request before expiry
- **THEN** it SHALL display the cached page
- **AND** it SHALL NOT call the source again

#### Scenario: Query, source, page, or filters differ

- **GIVEN** a cached search page exists
- **WHEN** the source, normalized query, page, or ordered filters differ
- **THEN** Browse SHALL NOT use that entry as an exact result

### Requirement: Browse falls back to matching local rows

When a nonblank page-one source search returns no rows or fails, Browse SHALL search locally persisted rows belonging to the selected source. A nonempty fallback SHALL be returned as a terminal page. A failed remote request with no local matches SHALL remain an error with retry available.

#### Scenario: Empty remote result has local matches

- **WHEN** page one succeeds with no rows and matching rows for the selected source exist locally
- **THEN** Browse SHALL display those local matches
- **AND** it SHALL report no next page

#### Scenario: Remote failure has local matches

- **WHEN** page one fails and matching rows for the selected source exist locally
- **THEN** Browse SHALL display those local matches instead of an error-only state

#### Scenario: Remote failure has no local matches

- **WHEN** page one fails and no matching selected-source rows exist locally
- **THEN** Browse SHALL expose the remote failure
- **AND** retry SHALL remain available

#### Scenario: Later page fails

- **WHEN** a source search page after page one fails or is empty
- **THEN** Browse SHALL NOT substitute the source's complete local row set for that page

## MODIFIED Requirements

### Requirement: Browse search requires explicit submission

Browse SHALL replace explicit-only submission with source-specific live search. A nonblank query that remains unchanged for 300 ms SHALL request page one from the selected source. Blank input SHALL clear Search results without a source request. The keyboard Search action MAY trigger the same current query immediately without creating distinct search semantics.

#### Scenario: Stable nonblank input starts search

- **GIVEN** Browse is in Search mode with a selected source
- **WHEN** a nonblank query remains unchanged for 300 ms
- **THEN** Browse SHALL request page one from that selected source

#### Scenario: Blank input performs no search

- **WHEN** the Browse query is blank or whitespace-only
- **THEN** Browse SHALL clear Search results
- **AND** it SHALL NOT issue a source search request

#### Scenario: Selected source changes with a current query

- **GIVEN** Browse has a nonblank Search query and results for source A
- **WHEN** the user selects source B
- **THEN** Browse SHALL clear source A results
- **AND** schedule the current query only for source B

#### Scenario: Search remains source-specific

- **GIVEN** cached or persisted rows exist for multiple sources
- **WHEN** Browse searches with one source selected
- **THEN** displayed results SHALL belong only to the selected source

### Requirement: Browse exposes search progress and recovery

Browse SHALL expose loading feedback while the current debounced or immediate search is in progress. If the current remote request fails and no local fallback matches, Browse SHALL expose retry feedback that reissues that same source, normalized query, page, and filter request.

#### Scenario: Live search is loading

- **GIVEN** a stable nonblank Browse query has started a request
- **WHEN** that request is in progress
- **THEN** Browse SHALL show loading feedback

#### Scenario: Search failure without fallback can be retried

- **GIVEN** the current source request fails and local fallback is empty
- **WHEN** Browse presents the failure state
- **THEN** it SHALL show retry feedback
- **AND** Retry SHALL reissue the same request context

#### Scenario: Search failure has local fallback

- **GIVEN** the current source request fails and local fallback is nonempty
- **WHEN** Browse displays fallback rows
- **THEN** it SHALL not replace those rows with an error-only state

### Requirement: Browse rejects stale search responses

New query input, source selection, or mode selection SHALL cancel pending debounce work and the active superseded request. A response belonging to an earlier query, source, mode, or page SHALL NOT replace current state even if cancellation completes late.

#### Scenario: Query changes during debounce

- **GIVEN** a query is waiting for its debounce interval
- **WHEN** the user changes the query
- **THEN** the earlier debounce SHALL be cancelled
- **AND** only the latest stable query SHALL start a request

#### Scenario: Source changes during an active search

- **GIVEN** a search request is active for source A
- **WHEN** the user selects source B
- **THEN** the source A request SHALL be cancelled
- **AND** source A SHALL not replace source B state if it completes late

#### Scenario: Cancelled response still completes

- **GIVEN** a superseded source call completes despite cancellation
- **WHEN** its response reaches Browse after a newer request became current
- **THEN** Browse SHALL retain the newer request state

#### Scenario: Stale page response is ignored

- **GIVEN** Browse has moved to a later source, mode, query, or page request
- **WHEN** a response for the previous request completes
- **THEN** Browse SHALL retain results associated with the current request

### Requirement: Existing catalog behavior remains available

The catalog presentation SHALL preserve Library sort, text search, removal, and Samsung-first behavior subject to the separately specified local provider fallback. Browse SHALL preserve source selection, current-query pagination, series-detail navigation, adaptive catalog presentation, and keyboard Search affordance.

#### Scenario: Library search retains Samsung-first semantics

- **GIVEN** the user enters a nonblank Library search query
- **WHEN** Samsung Search succeeds
- **THEN** Library SHALL preserve provider ordering and local-row display resolution

#### Scenario: Browse preserves pagination context

- **GIVEN** the current source search reports another page
- **WHEN** Browse requests more results
- **THEN** it SHALL use the same source, normalized query, and ordered filter snapshot
- **AND** append only that request's results

#### Scenario: Browse preserves series navigation

- **WHEN** the user selects a Browse result
- **THEN** Browse SHALL navigate to that series' existing detail destination

## REMOVED Requirements

None.
