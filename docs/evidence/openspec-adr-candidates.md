---
id: evidence-openspec-adr-candidates
title: "ADR candidates from OpenSpec changes"
type: Evidence
status: active
valid_from: 2026-10-08
scope:
  system: readerparser
relations:
  - type: supported_by
    target: ./infrastructure-history.md
  - type: supported_by
    target: ./ui-source-history.md
  - type: relates_to
    target: ../adr/0001-project-knowledge-vault.md
  - type: relates_to
    target: ../adr/0101-canonical-workflow-ownership.md
  - type: relates_to
    target: ../adr/0102-retryable-chapter-jobs.md
  - type: relates_to
    target: ../adr/0103-offline-first-content.md
  - type: relates_to
    target: ../adr/0104-preserve-chapter-state.md
  - type: relates_to
    target: ../adr/0105-enforced-agent-separation.md
  - type: relates_to
    target: ../adr/0106-room-download-progress.md
  - type: relates_to
    target: ../adr/0107-optional-series-metadata-index.md
  - type: relates_to
    target: ../adr/0108-room-index-eligibility.md
  - type: relates_to
    target: ../adr/0109-small-index-rebuild.md
  - type: relates_to
    target: ../adr/0110-provider-ranking-local-display.md
  - type: relates_to
    target: ../adr/0201-source-title-authority.md
  - type: relates_to
    target: ../adr/0202-unified-reader-boundary.md
  - type: relates_to
    target: ../adr/0203-asura-api-completeness.md
  - type: relates_to
    target: ../adr/0204-parsed-metadata-ownership.md
  - type: relates_to
    target: ../adr/0205-library-eligible-snapshot.md
  - type: relates_to
    target: ../adr/0206-bounded-chapter-page-concurrency.md
---

# ADR candidates from OpenSpec changes

All sixteen candidates remain unaccepted with Akashic status `proposed`.
Only [0001, the vault migration decision](../adr/0001-project-knowledge-vault.md#status), received user approval and status `active`.
Independent substance-coverage verification establishes incorporation, not ADR acceptance or application runtime correctness.
A candidate needs a costly reversal, a choice surprising without context, and a real trade-off.

Original change paths below are provenance identifiers. Resolvable links target incorporated vault knowledge and candidate sections.
Individual artifact hashes, substantive claims, dispositions, and independent review evidence remain in the coverage ledgers.

| Original change identifier | Incorporated knowledge | Proposed ADR candidates | Assessment and caveat |
| --- | --- | --- | --- |
| `openspec/changes/archive/2026-06-05-adopt-openspec-repository-governance` | [Workflow ownership](../concepts/repository-workflow.md#historical-documentation-ownership) | [0101: canonical ownership](../adr/0101-canonical-workflow-ownership.md#historical-choice-and-alternatives) | Historical single-workflow rationale is incorporated. User-approved migration replaces the obsolete mechanism without accepting every historical candidate. |
| `openspec/changes/archive/2026-06-08-add-sequential-offline-downloads` | [Queue ordering](../concepts/download-lifecycle.md#queue-ordering-and-the-serialization-discrepancy), [offline ownership](../concepts/download-lifecycle.md#offline-first-content-contract), [refresh state](../concepts/download-lifecycle.md#refresh-preserves-local-state) | [0102: retryable jobs](../adr/0102-retryable-chapter-jobs.md#historical-choice-and-alternatives), [0103: offline-first content](../adr/0103-offline-first-content.md#historical-choice-and-alternatives), [0104: retained chapter state](../adr/0104-preserve-chapter-state.md#historical-choice-and-alternatives) | Design rejects chaining while implementation uses `enqueueChain`; batch identity differs. Keep intended/implemented claims separate before accepting precise consequences. |
| `openspec/changes/archive/2026-06-09-harden-opencode-agent-lanes` | [Independent lanes](../concepts/agent-lanes.md#separate-execution-verification-review-and-integration), [historical merge rationale](../concepts/agent-lanes.md#delivery-lifecycle-and-historical-merge-choice) | [0105: enforced separation](../adr/0105-enforced-agent-separation.md#historical-choice-and-alternatives) | Verify current permission enforcement and merge preference before acceptance. Former OpenSpec lifecycle coupling is historical. |
| `openspec/changes/archive/2026-06-09-show-download-progress` | [Room progress ownership](../concepts/download-lifecycle.md#progress-ownership-and-milestones) | [0106: queue progress](../adr/0106-room-download-progress.md#historical-choice-and-alternatives) | Room single-owner rationale remains incorporated; migration coverage does not revalidate current runtime progress. |
| `openspec/changes/archive/2026-06-10-samsung-search-public-api` | [Optional metadata scope](../concepts/samsung-search.md#optional-metadata-scope), [Room eligibility](../concepts/samsung-search.md#room-eligibility-and-stale-files), [rebuild lifecycle](../concepts/samsung-search.md#rebuild-lifecycle) | [0107: optional series index](../adr/0107-optional-series-metadata-index.md#historical-choice-and-alternatives), [0108: Room eligibility](../adr/0108-room-index-eligibility.md#historical-choice-and-alternatives), [0109: full rebuild](../adr/0109-small-index-rebuild.md#historical-choice-and-alternatives) | External schema/deep links have compatibility cost. Registration was corrected later; detailed synchronization behavior needs current verification. |
| `openspec/changes/archive/2026-07-06-fix-samsung-search-registration` | [Registration compatibility](../concepts/samsung-search.md#registration-repair-and-compatibility-boundary) | None clearly qualifies. | Provider-contract correction is durable integration evidence. It does not clearly establish all three ADR criteria. |
| `openspec/changes/archive/2026-07-08-use-samsung-library-search` | [Canonical local display](../concepts/samsung-search.md#library-search-and-canonical-local-data), [later fallback discrepancy](../concepts/samsung-search.md#library-search-discrepancy) | [0110: provider rank/local display](../adr/0110-provider-ranking-local-display.md#historical-choice-and-alternatives) | Migration inspection reports fallback and batch lookup in `SeriesRepositoryImpl`. The old no-fallback/per-hit policies remain historical, with no invented graph supersession. |
| `openspec/changes/archive/2026-07-09-fix-source-listing-pagination` | [Operation pagination contracts](../concepts/catalog-and-source-parsing.md#source-pagination-contracts), [retrieval evidence](./ui-source-history.md#pagination-implementation-and-verification) | None clearly qualifies. | Bounded source repair, URL-specific fixtures, and serial-fetch history are incorporated without forcing an ADR. |
| `openspec/changes/archive/2026-07-11-refactor-library-browse-ui` | [Title authority/repair](../concepts/catalog-and-source-parsing.md#title-authority-and-bookmark-repair), [request policy evolution](./implementation-gaps.md#browse-policy-evolution) | [0201: authoritative titles](../adr/0201-source-title-authority.md#choice-and-trade-off) | Existing refresh and lifecycle-scoped repair avoid migration/background jobs. Explicit-only Browse search is historical; detailed repair conditions remain preserved. |
| `openspec/changes/archive/2026-07-14-unify-reader-screen` | [Shared reader boundary](../concepts/reader-runtime.md#shared-workflow-and-domain-boundary), [explicit route context](../concepts/reader-runtime.md#explicit-route-context), [progress/coverage gaps](./implementation-gaps.md#reader-progress-and-coverage) | [0202: unified reader/type](../adr/0202-unified-reader-boundary.md#choice-and-trade-off) | Detailed records disagree on progress/read semantics and coverage. ViewModels remain independent of `SourceRegistry`. Incorporation does not settle the disputed details. |
| `openspec/changes/archive/2026-07-27-fix-brose-init-fetch-and-reader-scroll` | [Complete API page lists](../concepts/catalog-and-source-parsing.md#asurascans-complete-image-pages), [endpoint uncertainty](./implementation-gaps.md#asura-endpoint-and-completeness) | [0203: API-first fallback](../adr/0203-asura-api-completeness.md#choice-and-trade-off) | The `/api/novels/{slug}/download` design hypothesis differs from reported/source-inspected `/api/series/{series_slug}/chapters/{chapter_slug}`. A sampled chapter is not complete compatibility proof. |
| `openspec/changes/optimize-source-search-cache` | [Cache boundaries](../concepts/source-metadata-performance.md#ownership-and-scope), [eligible snapshot](../concepts/source-metadata-performance.md#samsung-first-library-snapshot-and-fallback), [bounded remainder](../concepts/source-metadata-performance.md#freewebnovel-bounded-remainder-proposal) | [0204: parsed metadata ownership](../adr/0204-parsed-metadata-ownership.md#choice-and-trade-off), [0205: local snapshot](../adr/0205-library-eligible-snapshot.md#choice-and-trade-off), [0206: bounded concurrency](../adr/0206-bounded-chapter-page-concurrency.md#choice-and-trade-off) | Original was unarchived with all tasks checked. Cache/Browse/snapshot inspection supports partial implementation; Library debounce/title-first ranking and FreeWebNovel concurrency remain explicit gaps. |

Conflicting requirements, design claims, completion assertions, and source observations remain qualified knowledge.
The final vault contains synthesized nodes rather than copied workflow artifacts.
Independent source coverage gates control removal; no application code fixes or issue publishing occur in this migration.
