---
id: concept-repository-workflow
title: "Repository workflow and governance evolution"
type: Concept
status: active
valid_from: 2026-10-08
scope:
  system: readerparser
relations:
  - type: supported_by
    target: ../evidence/specification-evolution.md
---

# Repository workflow and governance evolution

## Current approved workflow

On 2026-10-08 the user explicitly retired OpenSpec and approved the skill sequence
`grill-with-docs` → `to-spec` → `to-tickets` → implement.
Use grilling to resolve material shared understanding, specifications in the Akashic `docs/` vault, and tickets in GitHub.
Apply the phases that the task needs. Trivial and read-only work does not require every phase.
This approval does not establish global skill settings or implementation details for the named skills.
Verification remains a separate gate. Record evidence before claiming completion.

## Historical change entry and exceptions

The 2026-06-05 archived governance change required an OpenSpec change before any non-trivial implementation or canonical-document edits.
Its minimum artifact was a `proposal.md` explaining what and why.
Implementation waited for schema `applyRequires` artifacts to reach `done`, checked with `openspec status --change "<name>" --json`.
The historical exhaustive exemptions were read-only exploration, direct conversation, single-file cosmetic fixes without behavior change,
dependency version-only bumps without API changes, and non-functional CI/tooling configuration.
Anything outside that list required a change, even if small. A comment/string typo without behavior change could proceed and commit directly.
These entry mechanics are retired. Their distinction between material work and harmless direct work remains useful.

## Historical documentation ownership

The old ownership table assigned README purpose/setup/quick start, root architecture layer contracts/invariants,
root codemap structure/entry points/navigation, and AGENTS routing/specialists/delegation.
Main OpenSpec specs owned capability requirements/scenarios. Per-change directories owned proposal/design/tasks/deltas.
Architecture rules belonged only in architecture.md, structure only in codemap.md, routing only in AGENTS.md.
Other canonical documents could link those owners but could not redefine or duplicate their content.
Current vault ownership preserves one source of truth while relocating substantive project knowledge into `docs/`.

## Historical transient-store retirement

Old governance prohibited creating, maintaining, writing, or reading `memory-bank/` and `plans/` as project truth.
Planning artifacts belonged in `openspec/changes/<name>/`, including proposal, design, tasks, and delta specs.
Legacy transient stores were to be removed after durable content migrated into canonical files or change artifacts.
Current planning/specification knowledge belongs in the vault. This migration retains useful substance before deleting its originals.

## Historical completion and archive

Completed tasks triggered synchronization of durable architecture, structure, and routing outcomes into their canonical owners before archive.
Delta specs synchronized to matching main capability specs before or during archive.
Archive moved the entire change to `openspec/changes/archive/YYYY-MM-DD-<name>/`.
The old policy allowed no leftover artifacts or unsynchronized durable outcomes.
The lane correction and delivery mechanics are preserved in [Agent lanes](../specs/agent-lanes.md).
These mechanics are historical context, not a present requirement to archive new OpenSpec changes.

## Historical house-style configuration

The old `openspec/config.yaml` selected schema `house-style`.
Its `context` and per-artifact `rules` examples were commented examples, with no active project context or custom rules.
The comments' TypeScript/React/Node.js, e-commerce, 500-word proposal, Non-goals, and two-hour task examples described no ReaderParser fact.
They carry no durable project knowledge. The schema selection is retained as configuration history.
