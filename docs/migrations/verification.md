---
id: evidence-project-knowledge-vault-verification
title: "Project knowledge vault migration verification"
type: Evidence
status: active
valid_from: 2026-10-08
scope:
  system: readerparser
relations:
  - type: supported_by
    target: ./coverage-specs.md
  - type: supported_by
    target: ./coverage-ui-source.md
  - type: relates_to
    target: ./coverage-infrastructure.md
  - type: relates_to
    target: ./coverage-navigation.md
  - type: relates_to
    target: ./coverage-generated-graph.md
  - type: relates_to
    target: ./project-knowledge-vault.md
---

# Project knowledge vault migration verification

Reviews below concern documentary substance coverage. They do not rerun application tests or prove runtime behavior.
The recorded source snapshot is `49ed7cbf277adba16b2ccb3d8589252f8516fe5f`, reviewed on 2026-10-08 by the independent runner.
Original source hashes in coverage matrices identify the exact reviewed content, including uncommitted content when applicable.

## Specification coverage

`VERDICT: PASSED` for the 43 sources assigned to specification coverage.
The independent review counted 141 requirements and 378 scenario occurrences, with conditions and outcomes preserved.
Two operational configuration rows additionally received SHA256 and clause checks within that source set.
Formatting-only/no-durable-substance dispositions remain explicit, rather than silent omissions.
See [specification coverage](./coverage-specs.md) for source identifiers, hashes, scenario mappings, and dispositions.

## UI and source coverage

`VERDICT: PASSED` for 33 sources and 141 claim groups in [UI/source coverage](./coverage-ui-source.md).
The runner checked all 33 SHA256 values and independently compared original substance with destination sections.
An initial failed review identified two bounded omissions: the rejected controls-only reader alternative and disposal-claim attribution.
The fixer incorporated the missing rationale and attributed disposal repair to verification rather than task coverage.
Targeted independent re-review passed both corrections. Each source is now recorded as `incorporated` and `independently-verified`.
Application gaps remain qualified evidence and are not silently treated as implementation success.

## Infrastructure coverage

`VERDICT: PASSED` for the 28 sources in [infrastructure coverage](./coverage-infrastructure.md).
The independent runner checked all 28 original hashes, 146 task statuses with line locators, and 321 links/anchors.
Fifteen owned nodes had valid unique IDs. All ten infrastructure ADRs remain proposed.
The review passed after two contract corrections and fifteen row-locator additions.
Four unchecked device checks remain explicitly unverified; documentary coverage does not turn them into runtime passes.

## Navigation and root-documentation coverage

`VERDICT: PASSED` for the 55 inventoried navigation/root-documentation sources after independent re-review by `runner_navigation` on 2026-10-08.
This set includes root architecture, all 38 project codemaps, and other mapped project documentation/configuration.
The initial review found eight bounded gaps in edit-batching lessons, placement, worker testing, Series state, build/tooling, selectors, reviewer policy, and archive dispatch.
The fixer incorporated these details, preserving historical discrepancies and distinguishing current source observations from runtime proof.
The runner rechecked 55 original hashes, 190 substantive mapping rows, and 231 destination links.
Its broader check covered 1042 links, 56 nodes, and 144 edges with healthy graph integrity.
See [navigation coverage](./coverage-navigation.md) for exact sources and destinations.

## Generated-snapshot addendum coverage

Four tracked `.understand-anything` exploration files form a late addendum to the original 159-source inventory.
Their provenance, unique limitations, and derived-cache dispositions are mapped in [generated snapshot coverage](./coverage-generated-graph.md).
Independent `oracle_generated_graph` revalidation returned `VERDICT: PASSED` on 2026-10-08 for all four sources.
The reviewer compared all structured claims with mapped knowledge and derived-data dispositions, checked four unchanged hashes, and resolved 34 ledger links.
Its inventory and integrity check covered 57 nodes and 148 edges with no findings.
The additional Evidence node changes the vault inventory after the navigation runner's recorded graph check.
Final delivery checks must measure the resulting graph rather than reuse that earlier inventory count.

## Coordinated removal gate

All 163 inventoried sources must receive independently verified incorporation before coordinated cleanup.
All 104 OpenSpec sources passed semantic coverage gates: 43 specification/configuration, 28 infrastructure, and 33 UI/source artifacts.
The 55 architecture/navigation/tooling sources also passed, completing the original 159-source inventory's four coverage gates.
The four generated-snapshot addendum sources also passed their separate fifth incorporation gate.
All 163 sources therefore passed semantic incorporation review before the root authorized coordinated cleanup.
A historical application PASS, completed checklist, or Akashic lint result cannot substitute for a source-level substance review.
After cleanup, final graph/link checks must distinguish source provenance identifiers from resolvable vault links.

## Delivered state and final verification

The root authorized delivery after all 163 sources passed the five independent incorporation gates (43 + 28 + 33 + 55 + 4).
Immediately before source mutation, the delivery script checked all 163 source hashes, four configuration/comment originals, and ten staged-file hashes.
There were zero mismatches. The source snapshot HEAD was unchanged.
The exact delivery removed 157 inventoried files and installed six reviewed routing replacements.
It also installed four bounded configuration/comment changes and removed 72 empty obsolete directories with scoped `rmdir`.
The four new scoped layer indexes were preserved. All 172 unrelated Kotlin files retained their pre-delivery hashes.
The sole Kotlin change updates the SourceContractTest documentation pointer to the vault's Source interface section.
JSON parsing passed for both changed OpenCode configurations. Other configuration fields and permission rules retain their staged semantic checks.
Replacement skill names are registered. OpenCode runtime discovery was not tested.
No application builds/tests, behavior changes, issue publication, or Git delivery occurred.
Delivery-side Akashic lint and inventory checks passed: 57 nodes, 148 edges, zero errors, and zero warnings.
Representative architecture `current` and `lineage` queries and persistence `impact` query all exited zero.
These delivery-side checks do not constitute the independent final verdict.

The independent `runner_navigation` returned final delivered-state `VERDICT: PASSED` on 2026-10-08.
It verified the exact 157 deletions, six routing replacements, four scoped configuration/comment changes, and all 163 incorporated, independently verified provenance records.
All 104 OpenSpec files and 38 codemaps are absent. No active instructions route to the retired workflows.
All 1080 delivered links and anchors resolve. Root AGENTS has 43 lines and routes to eight scoped indexes.
The final inventory contains 57 nodes and 148 edges. Akashic lint reports zero errors and warnings.
Representative `current`, `lineage`, and `impact` queries succeeded.
All 12 historical changes have decision assessments. Sixteen ADR candidates remain proposed, and migration ADR 0001 is active.
Both JSON configurations preserve unrelated settings and permissions. The Kotlin diff changes only the documented Source interface pointer.
HEAD is unchanged and `git diff --check` is clean. Application builds/tests were not run. OpenCode runtime skill discovery remains untested.
The five incorporation gates certify source substance before removal. This separate final gate certifies the delivered graph, links, routing, and retirement state.
