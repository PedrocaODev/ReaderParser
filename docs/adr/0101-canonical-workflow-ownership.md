---
id: decision-canonical-workflow-ownership
title: "Canonical workflow and durable ownership"
type: Decision
status: proposed
valid_from: 2026-06-05
scope:
  system: readerparser
relations:
  - type: supported_by
    target: ../evidence/infrastructure-history.md
---

# Canonical workflow and durable ownership

## Historical choice and alternatives

The June governance design chose one structured workflow and one explicit durable knowledge store to eliminate competing plans and scratchpads. It rejected archiving `plans/` because agents could keep following a second plan store. It required an audit before retiring duplicate context and kept direct handling for trivial work.

## Status and provenance

The accepted migration now replaces the historical OpenSpec mechanism with the vault and GitHub ticket ownership. This candidate records the consequential ownership principle and historical rationale, without asserting acceptance of the old workflow.
The source creation date is 2026-06-05. This candidate was incorporated on 2026-10-08.
Source provenance and independent coverage status are in the [coverage ledger](../migrations/coverage-infrastructure.md).
Historical completion claims are in [Governance ownership and replacement rationale](../evidence/infrastructure-history.md#governance-ownership-and-replacement-rationale).

## Consequences and reversal cost

Changing ownership spans agent routes, startup configuration, persisted planning knowledge, and continuity behavior. A future reader could mistake removal of plan and memory stores for accidental loss. Competing stores and archival retention were real alternatives.

## Limits and open questions

The design required atomic routing and storage retirement. Its audit checklist does not prove all earlier deleted knowledge was preserved. See the evidence sections on duplicate stores, routing, and completion claims.
