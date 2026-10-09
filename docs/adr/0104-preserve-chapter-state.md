---
id: decision-preserve-chapter-state
title: "Preserve local chapter state during refresh"
type: Decision
status: proposed
valid_from: 2026-06-08
scope:
  system: readerparser
relations:
  - type: supported_by
    target: ../evidence/infrastructure-history.md
---

# Preserve local chapter state during refresh

## Historical choice and alternatives

The refresh design keeps `read`, `progress`, and `downloaded` values for retained chapters when remote metadata replaces or updates rows. It compared merging state before `REPLACE` with state-preserving `@Upsert` or a read/merge transaction, selecting the latter family to avoid state loss and deletion side effects.

## Status and provenance

This candidate concerns ownership of local state during remote refresh. It does not establish acceptance of a particular Room SQL implementation.
The source creation date is 2026-06-08. This candidate was incorporated on 2026-10-08.
Source provenance and independent coverage status are in the [coverage ledger](../migrations/coverage-infrastructure.md).
Historical completion claims are in [Download verification claims](../evidence/infrastructure-history.md#download-verification-claims).

## Consequences and reversal cost

Changing this rule can erase reading and offline state and affect foreign-key behavior. Remote chapter defaults look like convenient replacement values without this context. Replacement and merged update paths were real alternatives.

## Limits and open questions

New chapters receive defaults and chapters absent remotely are removed under the historical test scope. The design's Room SQL mapping assertion is unverified. No identity, PK, FK, or application change occurs in this migration.
