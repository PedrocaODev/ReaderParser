---
id: runbook-project-knowledge-vault-migration
title: "Migrate project knowledge into the Akashic vault"
type: Runbook
status: active
valid_from: 2026-10-08
scope:
  system: readerparser
relations:
  - type: depends_on
    target: ../adr/0001-project-knowledge-vault.md
  - type: relates_to
    target: ../evidence/openspec-adr-candidates.md
---

# Migrate project knowledge into the Akashic vault

The user authorized this migration on 2026-10-08. Execute the incorporation, independent verification, and coordinated removal gates below.

## Target layout

| Path | Purpose |
| --- | --- |
| `docs/index.md` | Human entrypoint and graph hub linking canonical knowledge seeds. |
| `docs/architecture.md` | Current architecture, contracts, invariants, and decisions incorporated from root `architecture.md`. |
| `docs/concepts/` | Synthesized domain and project concepts where a distinct node improves retrieval. |
| `docs/CONTEXT.md` | Project glossary. The user's vault requirement takes precedence over a root glossary convention. |
| `docs/specs/<capability>.md` | Consolidated intended requirements, with historical evidence and unresolved gaps linked. |
| `docs/adr/` | Accepted decisions and proposed candidates, explicitly distinguished. |
| `docs/evidence/` | Synthesized supporting evidence, coverage matrix, unresolved gaps, and the ADR candidate ledger for all 12 changes. |
| `docs/migrations/` | Migration design and its verification records. |

Keep architecture together unless a distinct scope needs its own node. Split by concept only when the split improves retrieval.
Keep root `README.md` as a brief human entrypoint linking the vault.
Move its setup, source-authoring, and storage knowledge into relevant vault nodes.
Check source paths against the actual tree when correcting stale `kotlin` versus `java` references.
Preserve useful unique knowledge from `.opencode/project-structure.md` and retire or update its obsolete guidance on loading documentation.

## Incorporate source knowledge

1. Inventory all 104 OpenSpec files, root architecture, all 38 project `codemap.md` files, and substantive knowledge in README and `.opencode/project-structure.md`.
2. Extract requirements and scenario conditions/outcomes, invariants, rationale, alternatives/trade-offs, consequential task/verification claims and limits, meaningful lifecycle dates, and uncertainties.
3. Incorporate that substance into concept, architecture, specification, decision, or supporting evidence nodes organized by knowledge subject.
4. Record each original path/hash, extracted claims, destination nodes and exact sections, reconciliation or disposition, verification evidence, and coverage status.
5. Assess all 13 YAML files for material configuration and lifecycle facts. Incorporate those facts without retaining raw YAML artifacts.
6. Record an explicit reason for each item with no durable substance. Account for duplicate substance through its shared destination.
7. Incorporate useful unique codemap knowledge and durable Headroom lessons into relevant vault nodes and concise agent indexes.
8. Have the runner independently compare every original's substance with its destinations before removing that source.

The original inventory contains 159 sources. Four tracked `.understand-anything` exploration files form a separately reviewed late addendum.
Their unique provenance, discrepancies, and generated-data dispositions belong in synthesized evidence, with exact coverage in the addendum ledger.
The expanded coordinated-removal inventory therefore contains 163 sources.

Coverage matrices under `docs/migrations/` use `incorporated` or `unresolved` for each source artifact.
Mark coverage `incorporated` only when every substantive item has a verified destination or an explicit justified disposition.
Uncertain or unmapped substance blocks deletion of its affected source. No removed source may retain unresolved coverage.
A source with no durable substance still needs a recorded reason and independent verification.
Source paths and hashes are provenance identifiers. They will not be resolvable source links after deletion.
Mechanical whole-file copies do not satisfy incorporation. Preserve historical reasoning and conflicts as knowledge without duplicating proposal, design, task, or verification artifacts.
Organize destination nodes by concept or feature, rather than creating one replacement note for each source artifact.
Deduplicate equivalent claims while retaining every distinct condition and outcome. Broad summaries must not weaken detailed requirements.
Keep source artifacts available until coverage passes verification. The final vault will not contain an archive of original artifacts.
Retain known effective dates and distinguish them from migration dates. Record unknown dates without implying decision acceptance.
Extract Headroom knowledge without manually rewriting its generated source block while that source remains in use.

## Current guidance and historical evidence

Use `supported_by` from consolidated guidance to synthesized supporting Evidence or Decision nodes.
Use `relates_to` for affected concepts and specifications.
Use `supersedes` only when the successor actually covers the predecessor's scope and the effective date is known.
An archived directory or completed checklist does not establish acceptance, implementation completeness, or supersession.

Akashic 0.2.0 does not select current guidance from status alone.
Route root instructions and the vault index to canonical seeds. Identify historical knowledge explicitly and retain its known context.
Document this limitation in the vault index: a direct `current` query on a historical knowledge seed may return historical guidance without a supersession relation.
Use `lineage` for historical context. Do not add artificial supersession relations solely to alter query results.

## ADR candidates and discrepancies

Review each of the 12 OpenSpec changes for ADR candidates.
The [candidate ledger](../evidence/openspec-adr-candidates.md) maps all twelve changes to incorporated knowledge and sixteen proposed ADR candidates.
It retains original change paths as provenance identifiers rather than source links.
The ledger identifies each source change, candidate decision, incorporated supporting knowledge, assessment, and unresolved questions.
Record an explicit outcome when a change contains no decision that needs an ADR.
Create proposed candidate nodes only when they capture a specific consequential choice.
Record acceptance only when evidence or user confirmation establishes it. Candidates use `proposed`, and confirmed current decisions use `active` or `stable`.

Preserve discrepancies as linked evidence and open gaps. Distinguish intended requirements, source observations, and test results.
The `optimize-source-search-cache` checklist marks all tasks complete.
Source inspection suggests missing Library debounce, title-first fallback ranking, and FreeWebNovel concurrency behavior.
These observations remain untested. The migration retains both the requirements and the completion claims with their uncertainty.
Clearly qualified open gaps can completely incorporate a conflict's substance without resolving the application behavior.
Behavioral uncertainty does not block source removal when all conflicting claims and their uncertainty are independently verified as incorporated.
Do not change application code or overwrite intended requirements to make the documentation agree with implementation.

## Agent indexes and workflow

Root `AGENTS.md` contains a concise project index and triggers for consulting the vault.
Layer-level `AGENTS.md` files describe local rules and relevant knowledge seeds.
Add deeper indexes only for distinct rules, such as Room migrations or source contracts.
Existing namespace or feature directory boundaries alone do not justify additional indexes.

Future work uses `grill-with-docs`, `to-spec`, `to-tickets`, and `implement`.
Store durable specifications and planning knowledge in the vault, regardless of a skill's temporary or external storage default.
GitHub issues own authoritative ticket status and link to the applicable vault specification and commit when available.
The migration does not publish GitHub issues. Publishing requires an explicit request.

Retire repository-local OpenSpec commands, skills, and active instruction references.
Retire codemap generation hooks and configuration only when they directly serve the removed codemaps.
Retire `.slim/codemap.json` if inspection confirms that it contains only codemap generation metadata.
Update `.opencode/agent/domain-author.md` and other relevant pointers to root `architecture.md`.
Preserve unrelated OpenCode configuration and historical references that explain source provenance.
Keep global skills and settings unchanged.

## Verification gate

The runner owns verification and returns `VERDICT: PASSED`, `FAILED`, or `BLOCKED` with evidence.
Only `PASSED` completes the migration gate.

1. Independently compare every original's substance against the coverage matrix destinations, including all 104 OpenSpec files and 13 YAML files.
2. Check all 12 changes have an ADR candidate assessment and that discrepancies remain visible.
3. Check node IDs are unique, dates are valid, and original dates are distinct from migration dates.
4. Check all 38 project `codemap.md` files were removed and no active instructions route to removed OpenSpec or codemap workflows.
5. Check root and scoped `AGENTS.md` links resolve to the intended vault nodes.
6. Run `akashic lint docs/` and inspect the vault inventory.
7. Run representative `current`, `lineage`, and `impact` queries. Check canonical seeds, evidence links, and the documented historical-query limitation.
8. Check each source removal occurs only after its incorporated coverage passes independent validation, with no unresolved coverage on removed sources.

A completed checklist and successful Akashic lint do not prove substantive coverage.
The coverage matrix must also account for root architecture, all 38 codemaps, and other inventoried project documentation.
The [verification record](./verification.md) records independently passed coverage for all original 159 sources and the four-source generated-snapshot addendum.
The authorized cleanup followed all five incorporation gates and matched pre-mutation hashes for all 163 sources.
It removed 157 sources, installed six reviewed routing replacements and four bounded configuration/comment changes, and preserved the four new scoped indexes.
The independent runner_navigation returned final delivered-state `VERDICT: PASSED` on 2026-10-08.
It verified retirement and routing, all 1080 links/anchors, healthy 57-node/148-edge integrity, and successful representative context queries.
Incorporation review and delivered-state verification remain separate completed gates. The migration is complete.

No Gradle build or application test run is necessary for this documentation-only migration.
