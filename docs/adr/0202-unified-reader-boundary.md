---
id: decision-candidate-unified-reader-boundary
title: "Candidate: one reader workflow with explicit route content type"
type: Decision
status: proposed
valid_from: 2026-10-08
scope:
  system: readerparser
relations:
  - type: supported_by
    target: ../evidence/ui-source-history.md
  - type: relates_to
    target: ../concepts/reader-runtime.md
  - type: relates_to
    target: ../specs/reader.md
---

# Candidate: one reader workflow with explicit route content type

## Choice and trade-off

One Reader route/state/control workflow keeps separate Text and Pages renderers through the sealed domain content boundary.
Pass content type with source/series/chapter identity instead of consulting SourceRegistry in a ViewModel.
Shared controls reduce duplicated behavior while renderer-specific state and read/progress semantics remain explicit.
Reject synthetic text pages, a new renderer hierarchy, and inference only after content loads.

## Candidate assessment

Changing route/state boundaries affects restored navigation and multiple content types, with substantial reversal and migration cost.
Explicit content type before loading is surprising without mismatch and chapter-list rationale.
The two state shapes versus one workflow represent a real trade-off.
The original record was created 2026-07-12. This synthesis date does not prove precise semantics accepted or runtime-tested.
General one-reader constraints are current project rules, but this candidate's detailed progress/timing claims still need reconciliation.

## Source knowledge

See [route context](../concepts/reader-runtime.md#explicit-route-context), [renderer boundary](../concepts/reader-runtime.md#shared-workflow-and-domain-boundary), and [progress gaps](../evidence/implementation-gaps.md#reader-progress-and-coverage).
