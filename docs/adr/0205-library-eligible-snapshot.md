---
id: decision-candidate-library-eligible-snapshot
title: "Candidate: Samsung-first search from one eligible local snapshot"
type: Decision
status: proposed
valid_from: 2026-10-08
scope:
  system: readerparser
relations:
  - type: supported_by
    target: ../evidence/ui-source-history.md
  - type: relates_to
    target: ../concepts/source-metadata-performance.md
  - type: relates_to
    target: ../specs/library-search.md
---

# Candidate: Samsung-first search from one eligible local snapshot

## Choice and trade-off

Preserve Samsung provider ranking on success while resolving all hits from one Library/download-indexable snapshot.
Use the same eligible snapshot for title/author/genre fallback on provider failure or unavailability.
Avoid N+1 Room resolution and a separate availability probe for every query.
Local fallback improves availability but cannot search unseen or ineligible rows.
Retain external registration/index lifecycle; defer FTS until a measured corpus need justifies schema/synchronization cost.

## Candidate assessment

Eligibility and provider/local ranking boundaries affect persisted-library user expectations and external integration behavior.
The single-snapshot strategy and fallback alternative are consequential, but full ADR acceptance still needs evidence.
The original proposal was created 2026-07-12. Migration inspection reports batch/fallback present.
Intended Library debounce and title-first fallback ranking remain missing according to source inspection.
This candidate does not assert those requirements implemented or infer acceptance from task checks.

## Source knowledge

See [snapshot conditions](../concepts/source-metadata-performance.md#samsung-first-library-snapshot-and-fallback) and [unimplemented conditions](../evidence/implementation-gaps.md#library-debounce-and-ranking).
