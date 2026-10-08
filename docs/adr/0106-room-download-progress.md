---
id: decision-room-download-progress
title: "Keep visible download progress in Room queue state"
type: Decision
status: proposed
valid_from: 2026-06-09
scope:
  system: readerparser
relations:
  - type: supported_by
    target: ../evidence/infrastructure-history.md
---

# Keep visible download progress in Room queue state

## Historical choice and alternatives

The progress design chose worker updates through `downloadRepository.updateQueueState()` so progress follows DAO → Repository → ViewModel → UI. It rejected a separate WorkManager progress observation path and worker-local shared flows.

## Status and provenance

This proposed candidate identifies the authoritative progress owner. The historical records do not establish current runtime or decision acceptance.
The source creation date is 2026-06-09. This candidate was incorporated on 2026-10-08.
Source provenance and independent coverage status are in the [coverage ledger](../migrations/coverage-infrastructure.md).
Historical completion claims are in [Download verification claims](../evidence/infrastructure-history.md#download-verification-claims).

## Consequences and reversal cost

Moving progress ownership changes worker, persistence, ViewModel, and UI observation contracts. Writing intermediate progress to Room can look excessive without reactive queue consistency. WorkManager and worker-local flows were real alternatives.

## Limits and open questions

Milestones limit database writes: page fractions for manhwa, 0.5 after novel fetch, and 1.0 after write completion. Percentage avoids new page-count schema fields. The old design's polling characterization of WorkManager is unverified.
