---
id: decision-candidate-source-title-authority
title: "Candidate: source-detail title authority and bounded repair"
type: Decision
status: proposed
valid_from: 2026-10-08
scope:
  system: readerparser
relations:
  - type: supported_by
    target: ../evidence/ui-source-history.md
  - type: relates_to
    target: ../concepts/catalog-and-source-parsing.md
  - type: relates_to
    target: ../specs/catalog-presentation.md
---

# Candidate: source-detail title authority and bounded repair

## Choice and trade-off

Prefer extracted nonblank detail titles, with incoming nonblank titles only as fallback.
Repair existing blank bookmarks through existing refresh/persistence, once per Library lifecycle, preserving identity/data on failure.
This retains source-specific authority without a schema change, bulk migration, or background repair.
Unavailable sources can leave blanks until a later lifecycle instead of causing a continuous repair loop.

## Candidate assessment

Persisted data authority and repair timing are consequential and can surprise maintainers without the rejected alternatives.
Changing authority after titles are persisted can require data reconciliation, establishing potential reversal cost.
The original record was created 2026-07-10. That date does not prove this candidate's acceptance date.
2026-10-08 records candidate synthesis. Acceptance remains unestablished until independently assessed.

## Source knowledge

See [title rules and alternatives](../concepts/catalog-and-source-parsing.md#title-authority-and-bookmark-repair) and [historical review limits](../evidence/ui-source-history.md#catalog-refactor-verification-and-limits).
