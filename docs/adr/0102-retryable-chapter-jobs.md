---
id: decision-retryable-chapter-jobs
title: "Persist retryable jobs per chapter"
type: Decision
status: proposed
valid_from: 2026-06-08
scope:
  system: readerparser
relations:
  - type: supported_by
    target: ../evidence/infrastructure-history.md
---

# Persist retryable jobs per chapter

## Historical choice and alternatives

The June download design chose persisted queue state and one worker per chapter for sequential batch downloads. It rejected one long-running worker over the whole list because that alternative would require custom retry behavior and was considered harder to recover after process death.

## Status and provenance

This remains a proposed candidate. The record establishes historical rationale, not verified process-death behavior or decision acceptance.
The source creation date is 2026-06-08. This candidate was incorporated on 2026-10-08.
Source provenance and independent coverage status are in the [coverage ledger](../migrations/coverage-infrastructure.md).
Historical completion claims are in [Download verification claims](../evidence/infrastructure-history.md#download-verification-claims).

## Consequences and reversal cost

Reversing chapter-granular queue and retry ownership affects persistence, cancellation, recovery, and visible state. Sequential per-chapter jobs can look needlessly numerous without the failure-isolation rationale.

## Limits and open questions

The old design incorrectly or incompletely attributed serialization to tags/default WorkManager behavior and rejected chaining. Current source uses explicit `enqueueChain` with list-hash batch names. Keep the chapter-job choice separate from those unverified mechanism claims.
