---
id: decision-project-knowledge-vault
title: "Consolidate project knowledge in an Akashic vault"
type: Decision
status: active
valid_from: 2026-10-08
scope:
  system: readerparser
relations:
  - type: relates_to
    target: ../migrations/project-knowledge-vault.md
---

# Consolidate project knowledge in an Akashic vault

## Context

ReaderParser uses the obsolete OpenSpec workflow. Project knowledge also resides in `architecture.md`, `codemap.md` files, and agent instructions.
The user wants one project knowledge graph and less context loaded into each agent session.

## Settled direction

The user confirmed these choices on 2026-10-08:

1. Store all project knowledge in `docs/`, organized as an Akashic vault.
2. Move the knowledge in root `architecture.md` into the vault.
3. Remove every `codemap.md` in the project. Retain useful knowledge through the vault and concise agent indexes.
4. Use root `AGENTS.md` as the project index. Add scoped `AGENTS.md` files for meaningful layers.
5. Replace the OpenSpec workflow with the available skills: `grill-with-docs`, `to-spec`, `to-tickets`, and `implement`.
6. Incorporate every artifact's substantive knowledge into synthesized vault nodes before removing its source artifact.
7. Review each of the 12 OpenSpec changes for possible architectural decision records (ADRs).
8. Store specifications in the vault. Store tickets in GitHub issues that link to the applicable vault specifications.

Layer indexes will direct agents to relevant knowledge without loading all project documentation into every session.
Deeper `AGENTS.md` files will cover distinct rules, such as Room migrations or source contracts.
The user's correction replaces the earlier recommendation to preserve original artifacts as an archive.
Retain historical rationale, alternatives, conflicts, and provenance within knowledge nodes rather than copies of workflow artifacts.

## Implementation boundaries

The [authorized migration runbook](../migrations/project-knowledge-vault.md) defines the layout, per-artifact coverage matrix, graph relations, and independent verification gate.
The migration will record code/specification discrepancies as evidence and open gaps. It will preserve intended requirements without implementing code fixes.
Uncertain or unmapped substance blocks deletion of its source. A checklist or graph lint alone does not establish complete incorporation.

The unarchived `optimize-source-search-cache` task checklist marks all tasks complete.
Source inspection suggests missing Library debounce, title-first fallback ranking, and FreeWebNovel concurrency behavior.
These are source-inspection findings, not test results. Verification must resolve them before any record asserts implementation completeness.

## Status

The user approved this decision and migration execution on 2026-10-08.
Only this migration ADR is approved. All sixteen historical ADR candidates remain proposed until individually assessed.
Source removal follows independently passed substance-coverage gates. Application code and GitHub issue publishing are outside this migration.
