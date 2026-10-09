---
id: specs-repository-governance
title: "Repository knowledge and work governance"
type: Concept
status: active
valid_from: 2026-10-08
scope:
  system: readerparser
relations:
  - type: supported_by
    target: ../evidence/specification-evolution.md
---

# Repository knowledge and work governance

The user retired the OpenSpec workflow on 2026-10-08.
Current work follows the [repository workflow](../concepts/repository-workflow.md#current-approved-workflow).
Older governance requirements remain explicitly historical evidence.

## Work classification

For material feature, behavior, data-flow, public-interface, or architecture changes, establish shared understanding and a reviewable specification before implementation.
Use the approved skill sequence `grill-with-docs` → `to-spec` → `to-tickets` → implement when the work needs those phases.
Specifications belong in the `docs/` Akashic vault. Tickets belong in GitHub.
Do not make all phases mandatory for trivial or read-only work.
Read-only exploration and direct conversation proceed directly.
Single-file cosmetic changes proceed directly when behavior does not change.
Dependency version bumps proceed directly when the API is unchanged and only the version string needs editing.
CI/tooling configuration changes proceed directly when they have no application behavior change.
Use the task's material scope to select the needed preparation and verification.

## Canonical ownership

The `docs/` vault owns substantive project knowledge, organized by concept.
Architecture rules and invariants have one canonical home. Capability specs retain requirements and scenario conditions/outcomes.
Root README is a brief human entrypoint. Agent instruction files provide routing and local boundaries.
When architecture or structure changes, update its canonical vault node.
When an agent lane or routing rule changes, update the applicable agent instructions.
Link the owner instead of redefining the same knowledge in multiple documents.

## Durable outcomes and source retirement

Incorporate durable architectural, structural, routing, and capability outcomes into their canonical nodes.
Independent per-artifact coverage verification must precede source removal during this migration.
Maintain historical context and unresolved intent separately from current guidance.
`memory-bank/` and `plans/` do not own project truth. Incorporate useful legacy knowledge into the vault before retiring its source.
Archived artifacts and complete task checklists do not prove implementation or acceptance.
