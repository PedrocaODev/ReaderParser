---
id: specs-downloads
title: "Downloads and chapter state preservation"
type: Concept
status: active
valid_from: 2026-10-08
scope:
  system: readerparser
relations:
  - type: supported_by
    target: ../evidence/specification-evolution.md
---

# Downloads and chapter state preservation

These requirements combine enqueue, queue progress, offline deletion, and chapter-refresh state preservation.

## Single and batch enqueue

The unified Reader and series detail must allow a single chapter download.
For NOVEL and MANHWA alike, insert `QUEUED` and enqueue a `ChapterDownloadWorker` request.
If the chapter is already `QUEUED` or `RUNNING`, preserve its queue entry unchanged and create no duplicate work.
Series detail supports unread-only batches and user-selected start/end ranges.
Download unread includes every `read=false` chapter in chapter-number order, with one work request per chapter.
Ranges include both endpoints and follow chapter-number order, with one work request per chapter.
If every chapter is read, Download unread adds no queue items and shows a brief nothing-to-download message.

## Sequential work and batch cancellation

Execute only one chapter worker at a time.
The next worker starts only after the current chapter completes, fails permanently, or is cancelled.
Cancelling a batch removes its `QUEUED` entries and cancels the active worker when that worker belongs to the batch.
If no worker in that batch is running, remove its queued entries without requiring an active chapter.

## Queue display and progress

Downloads shows each chapter's state, progress, series title, and chapter name.
States are `QUEUED`, `RUNNING`, `COMPLETED`, and `FAILED`. Progress ranges from 0.0 to 1.0.
Queue additions, state updates, and removals must appear reactively in real time.
A `RUNNING` item with progress greater than zero shows the percentage in its status badge, such as `45%`.
Update progress at meaningful milestones rather than continuously.
After every downloaded manhwa page, report `pagesDownloaded / totalPages`.
For novels, report 0.5 after content fetch and 1.0 after disk write.
Progress flows Worker → DownloadRepository → DownloadQueueDao → ViewModel → UI.
When the worker calls `downloadRepository.updateQueueState()`, Downloads must reflect the progress within 500 ms.

## Offline reading and deletion

The cache-first route and mismatch contract is in [Reader](reader.md#renderers-and-payload-mismatch).
Series detail and Reader chapter lists indicate chapters with `downloaded=true`.
Allow deletion of a single chapter's downloaded files from the chapter list or Downloads screen.
Delete on a `COMPLETED` Downloads item removes its files from `DownloadStore`, sets `downloaded=false`, and removes its queue entry.

## Refresh preserves chapter state

When `ChapterRepository.refreshChapters(series)` fetches and upserts remote chapters, preserve existing local `read`, `progress`, and `downloaded` values.
An existing `read=true` remains true. Existing progress 0.75 remains 0.75. Existing `downloaded=true` remains true.
New remote chapters default to `read=false`, `progress=0f`, and `downloaded=false`.
Remove stale local chapter rows that no longer exist in the remote chapter list.
