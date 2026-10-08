---
id: decision-provider-ranking-local-display
title: "Keep provider ranking with canonical local display data"
type: Decision
status: proposed
valid_from: 2026-07-07
scope:
  system: readerparser
relations:
  - type: supported_by
    target: ../evidence/infrastructure-history.md
---

# Keep provider ranking with canonical local display data

## Historical choice and alternatives

The Library search design chose Samsung-ranked hits resolved back to local composite-key rows. Results retain provider order, display Room titles and covers, and require both in-library and downloaded eligibility. Local sorting and displaying provider metadata would violate those ownership choices.

## Status and provenance

This proposed candidate isolates ranking, display, and eligibility ownership from the historical failure contract. Acceptance or supersession is not established.
The source creation date is 2026-07-07. This candidate was incorporated on 2026-10-08.
Source provenance and independent coverage status are in the [coverage ledger](../migrations/coverage-infrastructure.md).
Historical completion claims are in [Samsung verification claims](../evidence/infrastructure-history.md#samsung-verification-claims).

## Consequences and reversal cost

Reversal changes search relevance and what stale external metadata can display. Resolving provider hits locally can look redundant without canonical local state. The design considered local title matching and an ordered custom SQL join.

## Limits and open questions

The historical implementation rationale accepted one DAO lookup per hit and surfaced provider failure separately. Current source obtains one eligible snapshot and falls back to local title/author/genre matches on failure. That discrepancy remains qualified source evidence, not a new accepted decision.
