## Purpose
Specifies that chapter downloads report intermediate progress at meaningful milestones (per-page for manhwa, on content fetch and disk write for novels), that the Downloads screen reflects RUNNING progress, and that updates propagate reactively through the Worker → repository → DAO → ViewModel → UI chain.

## Requirements

### Requirement: Intermediate progress updates during download

The system SHALL report intermediate progress updates during chapter download
execution. Progress SHALL be updated at meaningful milestones, not continuously.

#### Scenario: Manhwa page progress
- **WHEN** a manhwa chapter is being downloaded
- **THEN** progress SHALL be updated after each page is downloaded
- **AND** progress value SHALL be `pagesDownloaded / totalPages`

#### Scenario: Novel content progress
- **WHEN** a novel chapter is being downloaded
- **THEN** progress SHALL be 0.5 after content fetch completes
- **AND** progress SHALL be 1.0 after content is written to disk

### Requirement: Progress reflected in UI

The Downloads screen SHALL display the current progress value when a download
is in RUNNING state.

#### Scenario: Progress percentage display
- **WHEN** a download item has state RUNNING and progress > 0
- **THEN** the status badge SHALL show the progress percentage (e.g., "45%")

### Requirement: Progress updates flow reactively

Progress updates SHALL propagate through the existing reactive chain:
Worker → DownloadRepository → DownloadQueueDao → ViewModel → UI.

#### Scenario: Real-time progress update
- **WHEN** the worker updates progress via `downloadRepository.updateQueueState()`
- **THEN** the Downloads screen SHALL reflect the updated progress within 500ms
