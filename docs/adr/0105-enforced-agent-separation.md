---
id: decision-enforced-agent-separation
title: "Enforce separate implementation and delivery lanes"
type: Decision
status: proposed
valid_from: 2026-06-09
scope:
  system: readerparser
relations:
  - type: supported_by
    target: ../evidence/infrastructure-history.md
---

# Enforce separate implementation and delivery lanes

## Historical choice and alternatives

The lane design chose a dedicated integrator and per-agent permission overrides. It separated implementation, runner verification, reviewer inspection, and git delivery. It rejected adding delivery to runner or orchestrator and rejected prompt-only self-policing with globally allowed writes.

## Status and provenance

This is a proposed historical architectural candidate. Current authorization and tool permissions remain authoritative. Acceptance of the older merge strategy is not established.
The source creation date is 2026-06-09. This candidate was incorporated on 2026-10-08.
Source provenance and independent coverage status are in the [coverage ledger](../migrations/coverage-infrastructure.md).
Historical completion claims are in [Agent lane completion claims](../evidence/infrastructure-history.md#agent-lane-completion-claims).

## Consequences and reversal cost

Reversal changes workflow gates, permission configuration, and accountability across agents. A specialist unable to stage its own work is surprising without this separation. Combined lanes and soft prompt rules were real alternatives.

## Limits and open questions

The old config included broad `git *` denial and a mutating formatter allowance for runner. Historical OpenSpec correction/archive wiring is replaced by the vault workflow. Merge commits were preferred to preserve prefixed groups, but that preference is not verified current policy.
