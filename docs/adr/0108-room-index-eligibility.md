---
id: decision-room-index-eligibility
title: "Derive search eligibility from Room downloaded state"
type: Decision
status: proposed
valid_from: 2026-06-10
scope:
  system: readerparser
relations:
  - type: supported_by
    target: ../evidence/infrastructure-history.md
---

# Derive search eligibility from Room downloaded state

## Historical choice and alternatives

The index design chose a composite-key Room JOIN with DISTINCT and `downloaded = 1` as the indexable set. It rejected a separately maintained `search_index` table to avoid duplicated truth. It also deferred file-existence checks during every rebuild.

## Status and provenance

This remains a proposed candidate. Historical eligibility assertions and current file consistency were not revalidated.
The source creation date is 2026-06-10. This candidate was incorporated on 2026-10-08.
Source provenance and independent coverage status are in the [coverage ledger](../migrations/coverage-infrastructure.md).
Historical completion claims are in [Samsung verification claims](../evidence/infrastructure-history.md#samsung-verification-claims).

## Consequences and reversal cost

Changing the authority affects DAO projections, deletion semantics, external results, and later Library search. A row may stay searchable after its files disappear, which is surprising without the accepted historical stale-flag trade-off. A maintained table or file-validation path were real alternatives.

## Limits and open questions

Normal deletion was said to reset the flag. External file removal can leave a stale result. Downloads make a series indexable even outside the library. Library search independently requires in-library membership.
