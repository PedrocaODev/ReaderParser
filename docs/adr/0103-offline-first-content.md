---
id: decision-offline-first-content
title: "Resolve local chapter content through the repository"
type: Decision
status: proposed
valid_from: 2026-06-08
scope:
  system: readerparser
relations:
  - type: supported_by
    target: ../evidence/infrastructure-history.md
---

# Resolve local chapter content through the repository

## Historical choice and alternatives

The download design chose `ChapterRepositoryImpl.getContent()` as the local-first content boundary. It returns non-null `DownloadStore.read(chapter)` content before reaching the source. It rejected a separate `getOfflineContent()` method because each reader caller would need a new path.

## Status and provenance

This proposed candidate captures a read-precedence contract. No current offline journey or ADR acceptance is established.
The source creation date is 2026-06-08. This candidate was incorporated on 2026-10-08.
Source provenance and independent coverage status are in the [coverage ledger](../migrations/coverage-infrastructure.md).
Historical completion claims are in [Download verification claims](../evidence/infrastructure-history.md#download-verification-claims).

## Consequences and reversal cost

Reversal changes every reader's content precedence and invalidation expectations. Serving local content despite fresher network data is surprising without the offline rationale. A separate caller-selected offline path was a real alternative.

## Limits and open questions

Stale local content was accepted historically. Suggested deletion/refresh escape paths were not revalidated. A checked no-source-call unit test is a historical completion claim.
