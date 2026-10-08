---
id: concept-download-lifecycle
title: "Download lifecycle and offline reading"
type: Concept
status: active
valid_from: 2026-10-08
scope:
  system: readerparser
relations:
  - type: supported_by
    target: ../evidence/infrastructure-history.md
  - type: relates_to
    target: ../specs/downloads.md
  - type: relates_to
    target: ../adr/0102-retryable-chapter-jobs.md
  - type: relates_to
    target: ../adr/0103-offline-first-content.md
  - type: relates_to
    target: ../adr/0104-preserve-chapter-state.md
  - type: relates_to
    target: ../adr/0106-room-download-progress.md
---

# Download lifecycle and offline reading

This node explains contracts and historical reasoning. The [download specification](../specs/downloads.md) owns intended requirements.
Migration date is 2026-10-08. June 2026 source claims do not establish current runtime behavior or accepted decisions.

## Pipeline and ownership

The June 8 design described an existing `download_queue`, `DownloadQueueEntity`, and `DownloadQueueDao` with chapter states `QUEUED → RUNNING → COMPLETED/FAILED`.
It described `ChapterDownloadWorker` as a one-chapter worker that writes through `DownloadStore`, marks the chapter downloaded, and retries three times with exponential backoff.
`DownloadsScreen` and `DownloadsViewModel` already observed the queue and exposed per-item cancel and retry.
The reported missing flow was UI enqueueing, batch orchestration, and local content loading. Network-only readers caused unreliable-connection and travel problems.

`DownloadEnqueuer` separates orchestration from ViewModels. Its interface belongs in `domain/`, and its implementation belongs in `data/repository/`.
The historical proposal used `enqueueChapter(sourceId, chapterUrl)` and `enqueueBatch(sourceId, seriesUrl, chapterUrls)`.
The checked June 8 task 1.1 required both methods to be suspend functions. This is a historical contract and completion claim.
The intended implementation inserts queue rows, schedules a worker per chapter, and receives a Hilt binding.
WorkManager and Android remain outside the domain contract. This migration does not change any contract.
The June 8 proposal intended to keep `ChapterDownloadWorker` and the Downloads screen unchanged, while extending batch tags.
Its task list nevertheless included Downloads deletion enhancements. Both scope claims remain historical, without silently reconciling them.

The original filesystem layout was app-private `filesDir/downloads/{sourceId}/{seriesHash}/{chapterHash}`.
Chapter download content stays inside the app's private storage. Public search publishes metadata, not payloads.

## Queue ordering and the serialization discrepancy

The intended user behavior is sequential batch execution: the next chapter starts after the previous chapter completes.
The proposal called this a sequential worker chain. The design instead claimed shared tags and `ExistingWorkPolicy.KEEP` serialize separately enqueued requests.
It said WorkManager serializes requests by default and rejected `then()` chaining as cancellation and progress complexity.
Those framework claims are historical assertions, not verified framework guarantees.

Source inspection on 2026-10-08 found explicit `workManager.enqueueChain()` in `DownloadEnqueuerImpl.enqueueBatch()`.
It constructs a request for each filtered chapter, in list order. Its unique batch name is `batch-$sourceId-${hashUrl(toEnqueue.joinToString(","))}`.
The current method accepts `(sourceId, chapterUrls)`, without the historical `seriesUrl` parameter.
Single enqueue uses `enqueueUniqueWork()` with `ExistingWorkPolicy.KEEP` and `download-$sourceId-${hashUrl(chapterUrl)}`.
Both paths skip chapters already `QUEUED` or `RUNNING`. An empty filtered batch returns without scheduling.
These are source observations, not test or device results. They contradict the old serialization mechanism and series-based batch identity.

The rejected long-running worker would loop through all chapters and require custom retry behavior.
The design argued that individual persisted requests better tolerate process death. That argument is retained as rationale, without validating every framework claim.

## Cancellation, deletion, and batch identity

The June 8 proposal identified a batch by `(sourceId, seriesUrl)` and described `cancelBatch(seriesKey)`.
The task contract instead used `cancelBatch(sourceId: Long, chapterUrls: Set<String>)`.
The design required removal of matching queued items and cancellation of an active worker when it belonged to the batch.
Its implementation task described DAO row removal and WorkManager cancellation by tag `download-{sourceId}-{batchId}`.
The list-hash identity observed in current source is a distinct mechanism. Do not equate those identities or assert that cancellation was revalidated.

The Downloads enhancement added deletion through `DownloadStore.delete` and removal of the queue entry, wired through `DownloadsAction` and `DownloadsViewModel`.
The later Samsung index design described `DownloadRepository.deleteDownload()` as the normal path that also resets `chapters.downloaded` to false.
Cancel, retry, and delete are separate operations. The historical first version omitted pause/resume and treated cancel plus retry as sufficient.

## Offline-first content contract

`ChapterRepositoryImpl.getContent(chapter)` checks `DownloadStore.read(chapter)` first and returns non-null cached content immediately.
The checked June 8 task 3.1 claimed `DownloadStore` was injected into `ChapterRepositoryImpl`.
That checkbox is a historical completion claim, not new execution evidence from this migration.
It calls the source only when no local copy exists. Readers benefit through their existing repository call.
The old readers were `NovelReaderViewModel` and `MangaReaderViewModel`. Their separate names are historical context for a later unified Reader.

A separate `getOfflineContent()` method was rejected because each caller would need changes and existing reader paths would not benefit automatically.
Cached content can be stale. The design accepted that trade-off for offline reading and suggested download deletion or a series refresh to force retrieval.
The claim that refresh forces retrieval is historical intent. This migration does not verify cache invalidation behavior.

## Refresh preserves local state

The original problem was `refreshChapters()` converting remote chapters to entities and using `OnConflictStrategy.REPLACE` in `ChapterDao.upsertAll`.
Remote default or stale values could erase `read`, `progress`, and `downloaded` values.
The design also identified delete-and-insert foreign-key cascade risk.
Refresh must preserve local state for retained chapters, insert new chapters with defaults, and remove stale chapters absent from the remote list.

Two designs were considered: merge state before `REPLACE`, or use `@Upsert` with state-preserving mapping, described as available in Room 2.5+.
The selected family was `@Upsert` or a transaction that reads existing state before inserting merged values.
The design claimed Room `@Upsert` maps to `INSERT ... ON CONFLICT DO UPDATE`, avoiding deletion and reinsertion.
That is an unverified historical framework claim, not a current API guarantee.
State preservation is the durable requirement. No new queue schema was needed because the relevant state columns already existed.

## Enqueue experience and resource limits

Series detail was to offer a Downloads FAB or menu action, unread-only enqueueing, and a start/end chapter range dialog.
Readers were to offer single-chapter enqueueing through their actions and state, plus downloaded indicators on chapter lists.
The historical action/state identifiers were `NovelReaderAction`, `NovelReaderUiState`, `MangaReaderAction`, and `MangaReaderUiState`.
The UI was to confirm enqueueing with a snackbar, such as a queued chapter count.
For large batches of 100 or more chapters, sequential execution limits pressure. The enqueue UI should show the chapter count before confirmation.
Storage pressure remained the user's responsibility in the historical design.

The first version excluded parallel whole-series downloads, extra Wi-Fi-only or battery-saving constraints beyond `NetworkType.CONNECTED`, cloud state, and a new settings screen.
Persisted queue entries and worker retry were the proposed process-death mitigation. Users could manually retry failed entries after restart.

## Progress ownership and milestones

The June 9 problem was progress remaining at zero until completion despite a RUNNING badge and `LinearProgressIndicator`.
`DownloadItem` and `DownloadQueueEntity` already carried `progress: Float`. `DownloadQueueDao.updateStateWithError` supported updates.
The proposal allowed a possible additional granular repository method. The design instead chose the existing queue-state update method.
The worker calls `downloadRepository.updateQueueState()` with intermediate progress.
Updates flow through DAO → Repository → ViewModel → UI. Room queue state is the proposed single owner of visible progress.

Manhwa progress is downloaded pages divided by total pages, updated after each successful image write in `DownloadStore.writeManhwa()` through `onPageDownloaded`.
Novel progress has milestones: 0.5 after fetch, then 1.0 after writing and transition to `COMPLETED`.
The progress range remains 0.0–1.0. The historical task wording says “fetch/write completes (0.5)” while the design separates fetch from write.
Preserve that ambiguity as a wording gap, rather than claiming a precise test of the first milestone.

`StateBadge` shows a percentage when progress is greater than zero and less than one.
Percentage uses the existing float and works for both content types. A page counter would require `totalPages` and `currentPage` throughout domain, Room, joins, and ViewModel.
The page-counter task was explicitly removed to avoid those changes and a schema migration. The existing preview already used 0.45 progress.
The design described the existing-badge choice as consistent with Material 3 conventions. That is historical design rationale, not a conformance check.

WorkManager progress data was rejected because it introduces a separate UI observation path. The design described this as polling, without current framework verification.
Worker-local SharedFlow or StateFlow was rejected as excess machinery. Bytes and time estimates were rejected because total bytes vary and estimates add complexity.
A separate label would clutter the screen, and hover tooltips do not fit touch interaction.

Progress updates occur only at milestones to limit database writes and flicker. Failure resets progress to zero in the historical error-handling account.
Progress can be imprecise after failure. Speed, ETA, screen redesign, queue redesign, and domain model changes were outside this change.
The proposal identified `androidx.hilt:hilt-compiler` through KSP as required for `@HiltWorker` code generation.
It claimed both `ChapterDownloadWorker` and `LibraryUpdateWorker` could fail instantiation without it. No dependency is added by this migration.

## Verification scope

The [historical verification record](../evidence/infrastructure-history.md#download-verification-claims) preserves checked tasks, test subjects, and their limits.
None of those checkboxes proves current runtime behavior or ADR acceptance.
