# Source Request Performance Specification

## ADDED Requirements

### Requirement: FreeWebNovel fetches known chapter-list pages with bounded concurrency

After FreeWebNovel chapter-list page one reports more pages, ReaderParser SHALL fetch known remaining pages in ascending windows containing at most three concurrent requests. Page one SHALL remain the discovery request and source request cancellation SHALL propagate.

#### Scenario: Chapter list spans multiple pages

- **GIVEN** page one reports multiple remaining AJAX pages
- **WHEN** the chapter list is retrieved
- **THEN** more than one remaining page MAY be in flight concurrently
- **AND** no more than three remainder-page requests SHALL be active concurrently

#### Scenario: Chapter-list retrieval is cancelled

- **WHEN** the parent chapter-list request is cancelled
- **THEN** outstanding page requests SHALL be cancelled
- **AND** cancellation SHALL NOT be converted into a partial success

### Requirement: Concurrent chapter-list retrieval preserves deterministic results

Concurrent page responses SHALL be merged in ascending page order and chapters SHALL be deduplicated by URL. A failed, undecodable, or blank page, or a page that adds zero previously unseen chapter URLs, SHALL terminate the contiguous result at that page. Later responses from the same three-request window SHALL be ignored and no later window SHALL be scheduled.

#### Scenario: Later page completes first

- **GIVEN** a later AJAX page completes before an earlier page
- **WHEN** results are merged
- **THEN** chapters SHALL still appear in ascending source page order

#### Scenario: Duplicate chapter appears on adjacent pages

- **WHEN** the same chapter URL appears on multiple fetched pages
- **THEN** the final list SHALL contain that chapter once

#### Scenario: Middle page fails

- **GIVEN** a middle page fails while later pages complete
- **WHEN** the result is assembled
- **THEN** the final list SHALL contain the successful contiguous prefix before the failed page
- **AND** later pages after the failure SHALL not be appended

#### Scenario: Page contains only duplicate URLs

- **GIVEN** a fetched page contains no chapter URL absent from earlier merged pages
- **WHEN** that page is merged
- **THEN** it SHALL be treated as non-progressing
- **AND** no later request window SHALL be scheduled

#### Scenario: Terminal page occurs inside a request window

- **GIVEN** one page in a three-request window is terminal
- **WHEN** later pages in that same window have already completed
- **THEN** those later results SHALL be ignored
- **AND** avoidable requests after the terminal page SHALL be limited to the other requests already in that window
