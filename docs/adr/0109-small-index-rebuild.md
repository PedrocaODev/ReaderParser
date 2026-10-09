---
id: decision-small-index-rebuild
title: "Rebuild the small Samsung index instead of diffing"
type: Decision
status: proposed
valid_from: 2026-06-10
scope:
  system: readerparser
relations:
  - type: supported_by
    target: ../evidence/infrastructure-history.md
---

# Rebuild the small Samsung index instead of diffing

## Historical choice and alternatives

The sync design chose deleting all index documents and bulk-inserting the current Room set on each debounced rebuild. Room observation and `ACTION_UPDATE_INDEX` use the same path. It rejected per-document insert/update/delete tracking because the expected set was only dozens of series.

## Status and provenance

This proposed candidate records an integration consistency choice. Its acceptance and current runtime cost are not established.
The source creation date is 2026-06-10. This candidate was incorporated on 2026-10-08.
Source provenance and independent coverage status are in the [coverage ledger](../migrations/coverage-infrastructure.md).
Historical completion claims are in [Samsung verification claims](../evidence/infrastructure-history.md#samsung-verification-claims).

## Consequences and reversal cost

Changing to diffs adds document state, deletion tracking, and recovery behavior. Full replacement looks wasteful without expected index size. Incremental diffs and polling were considered alternatives.

## Limits and open questions

Insertion is chunked at 100 documents. The historical reconsideration point was more than 500 series, without measurement. The proposal's word “incrementally” conflicts with explicit full-rebuild design/tasks, and that conflict remains visible.
