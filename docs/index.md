---
id: concept-project-index
title: "ReaderParser project knowledge"
type: Concept
status: active
valid_from: 2026-10-08
scope:
  system: readerparser
relations:
  - type: relates_to
    target: ./architecture.md
  - type: relates_to
    target: ./CONTEXT.md
  - type: relates_to
    target: ./concepts/domain-contracts.md
  - type: relates_to
    target: ./concepts/persistence.md
  - type: relates_to
    target: ./concepts/presentation.md
  - type: relates_to
    target: ./concepts/source-plugins.md
  - type: relates_to
    target: ./concepts/runtime-wiring.md
  - type: relates_to
    target: ./runbooks/development.md
  - type: relates_to
    target: ./concepts/repository-workflow.md
---

# ReaderParser project knowledge

## Canonical entrypoints

The docs directory is the Akashic project knowledge vault. Read the subject needed for the task.
The glossary uses project terms. Architecture owns normative contracts. Concept nodes own current implementation knowledge.
Specs own intended capability requirements. Evidence identifies history, discrepancies, and unresolved implementation.
Akashic status alone does not select current guidance in installed 0.2.0. Use these canonical seeds and explicit historical labels.

| Task | Canonical node |
| --- | --- |
| Project terms | [Glossary](CONTEXT.md) |
| Layers, contracts, invariants, decisions | [Architecture](architecture.md) |
| Models, repository APIs, identity | [Domain](concepts/domain-contracts.md) |
| Room, conversions, downloads, settings, search infrastructure | [Persistence](concepts/persistence.md) |
| Compose screens, state/effects, navigation, theme | [Presentation](concepts/presentation.md) |
| Source contract/base/parsing/site plugins | [Sources](concepts/source-plugins.md) |
| App/DI/utility algorithms/workers | [Runtime wiring](concepts/runtime-wiring.md) |
| Setup, tests, scripts, git conventions, vault queries | [Development](runbooks/development.md) |
| Approved skills and retired governance context | [Repository workflow](concepts/repository-workflow.md) |
| Historical map/tooling conflicts | [Navigation history](evidence/navigation-and-tooling-history.md) |
| Reusable tooling lessons | [Maintenance lessons](evidence/agent-maintenance-lessons.md) |

## Capability specifications

- [Catalog presentation and title integrity](specs/catalog-presentation.md)
- [Browse discovery and search](specs/browse-discovery.md)
- [Library search](specs/library-search.md)
- [Source metadata cache](specs/source-metadata.md)
- [Source pagination](specs/source-pagination.md)
- [Unified Reader](specs/reader.md)
- [Downloads and refresh state](specs/downloads.md)
- [Samsung Search index](specs/samsung-search-index.md)
- [Repository governance](specs/repository-governance.md)
- [Agent lanes](specs/agent-lanes.md)

Read [Specification evolution](evidence/specification-evolution.md) for superseded intent and partial optimization gaps.
Read [ADR candidates](evidence/openspec-adr-candidates.md) for accepted/proposed decision distinctions.
Coverage records track source incorporation and independent verification under migrations.
