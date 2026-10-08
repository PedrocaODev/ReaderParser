---
id: evidence-coverage-ui-source
title: "Substantive coverage of catalog, reader, and performance artifacts"
type: Evidence
status: active
valid_from: 2026-10-08
scope:
  system: readerparser
relations:
  - type: relates_to
    target: ../concepts/catalog-and-source-parsing.md
  - type: relates_to
    target: ../concepts/reader-runtime.md
  - type: relates_to
    target: ../concepts/source-metadata-performance.md
  - type: relates_to
    target: ../evidence/ui-source-history.md
  - type: relates_to
    target: ../evidence/implementation-gaps.md
---

# Substantive coverage of catalog, reader, and performance artifacts

This matrix covers 33 non-specification source artifacts in five changes, including five lifecycle YAML files.
The specification owner separately covers delta specifications. Original paths/hashes are provenance identifiers, not links after deletion.
Claims are consolidated by subject. Equivalent claims share destinations without weakening distinct conditions or outcomes.
No workflow artifact is copied wholesale. Historical rationale, alternatives, consequential tasks, verification claims/limits, and lifecycle facts remain knowledge.

## Verification state

The independent runner returned `VERDICT: PASSED` for all 33 sources' substantive coverage on 2026-10-08.
The runner checked 33 SHA256 values and 141 claim groups against snapshot `49ed7cbf277adba16b2ccb3d8589252f8516fe5f`.
Two bounded corrections added the controls-only rejection rationale and corrected disposal-report attribution before targeted re-review passed.
Coverage is `incorporated` and `independently-verified`. See the [verification record](./verification.md#ui-and-source-coverage).
This is documentary completeness evidence, not application runtime proof.
Authorized coordinated cleanup removed all 33 originals after coverage passed and pre-mutation hashes matched. Final delivered-state validation is pending.
Qualified behavioral gaps can have complete documentary coverage without code fixes. Uncertain or unmapped substance still blocks deletion.
Every owned source is mapped below. None was found to have no durable substance.

## ADR candidate destinations

- Title authority/lifecycle repair: [0201](../adr/0201-source-title-authority.md#choice-and-trade-off).
- Unified Reader/typed route: [0202](../adr/0202-unified-reader-boundary.md#choice-and-trade-off).
- API completeness/fallback: [0203](../adr/0203-asura-api-completeness.md#choice-and-trade-off).
- Parsed metadata ownership: [0204](../adr/0204-parsed-metadata-ownership.md#choice-and-trade-off).
- Eligible snapshot/fallback: [0205](../adr/0205-library-eligible-snapshot.md#choice-and-trade-off).
- Bounded page windows: [0206](../adr/0206-bounded-chapter-page-concurrency.md#choice-and-trade-off).

All ADRs remain candidates. Pagination repair alone does not clearly meet ADR criteria.
Migration dates do not establish original acceptance/effective dates or rerun historical tests.

## 2026-07-09-fix-source-listing-pagination

### .openspec.yaml

Source identifier: `openspec/changes/archive/2026-07-09-fix-source-listing-pagination/.openspec.yaml`\
SHA256: `30edf0ca55422e0dcc715fdb5c42a40468522be86da4a70e2bc9d684e8da02e6`\
Coverage: `incorporated`. Verification: `independently-verified`.\
Reviewer: `runner`; date: `2026-10-08`; snapshot: `49ed7cbf277adba16b2ccb3d8589252f8516fe5f`.\
Evidence: [independent source coverage verdict](./verification.md#ui-and-source-coverage).

| Substantive source claim group | Exact destination section | Reconciliation/disposition |
| --- | --- | --- |
| house-style schema; created 2026-07-08; archive naming does not establish acceptance/effective date | [Knowledge section](../evidence/ui-source-history.md#lifecycle-facts) | historical facts incorporated |

### design.md

Source identifier: `openspec/changes/archive/2026-07-09-fix-source-listing-pagination/design.md`\
SHA256: `7abeb02fdd9ebc107abf009d6a6e68adfbd5ba737537b7a17a8bcee5cecd5223`\
Coverage: `incorporated`. Verification: `independently-verified`.\
Reviewer: `runner`; date: `2026-10-08`; snapshot: `49ed7cbf277adba16b2ccb3d8589252f8516fe5f`.\
Evidence: [independent source coverage verdict](./verification.md#ui-and-source-coverage).

| Substantive source claim group | Exact destination section | Reconciliation/disposition |
| --- | --- | --- |
| Existing page increments/isLoading/hasNextPage; auto-end sentinel/manual footer and over-trigger risk | [Knowledge section](../concepts/catalog-and-source-parsing.md#browse-discovery-and-request-identity) | incorporated; independently verified |
| All seven source/operation page-two expectations, prior findings and evidence before changing terminal modes | [Knowledge section](../concepts/catalog-and-source-parsing.md#source-pagination-contracts) | incorporated; independently verified |
| Contracts stable; no generalized HtmlSource expansion/schema/permission/dependency changes | [Knowledge section](../concepts/catalog-and-source-parsing.md#source-pagination-contracts) | incorporated; independently verified |
| Ordered Browse/novel/Asura slices with page-specific fixtures and explicit request URL/page/terminal assertions | [Knowledge section](../concepts/catalog-and-source-parsing.md#test-and-review-obligations) | incorporated; independently verified |
| Bound chapter loop to avoid duplicates/infinite requests and retain source-specific special cases | [Knowledge section](../concepts/catalog-and-source-parsing.md#freewebnovel-complete-chapter-lists) | incorporated; independently verified |

### plan.md

Source identifier: `openspec/changes/archive/2026-07-09-fix-source-listing-pagination/plan.md`\
SHA256: `c5086370845845f1dceab6a37c355bfe6f52d5f0b2634d5d44e683f87fa25376`\
Coverage: `incorporated`. Verification: `independently-verified`.\
Reviewer: `runner`; date: `2026-10-08`; snapshot: `49ed7cbf277adba16b2ccb3d8589252f8516fe5f`.\
Evidence: [independent source coverage verdict](./verification.md#ui-and-source-coverage).

| Substantive source claim group | Exact destination section | Reconciliation/disposition |
| --- | --- | --- |
| Test-first auto-load/manual fallback; existing ViewModel guard coverage exception | [Knowledge section](../concepts/catalog-and-source-parsing.md#test-and-review-obligations) | incorporated; independently verified |
| Page-one/page-two chapter aggregation proof; Asura distinct URLs/results before conditional parser change | [Knowledge section](../concepts/catalog-and-source-parsing.md#source-pagination-contracts) | incorporated; independently verified |
| Slice reviews minimality/completeness/contracts/actual parser need before next slice | [Knowledge section](../concepts/catalog-and-source-parsing.md#test-and-review-obligations) | incorporated; independently verified |
| Targeted UI/VM/source then full unit; APK only if needed; four separate commit purposes | [Knowledge section](../evidence/ui-source-history.md#pagination-implementation-and-verification) | historical validation/delivery plan |

### proposal.md

Source identifier: `openspec/changes/archive/2026-07-09-fix-source-listing-pagination/proposal.md`\
SHA256: `d39dac0ec0d5aeda47bcfb711c4094c0d14b8ae457bd1e29ce79a79baac37df2`\
Coverage: `incorporated`. Verification: `independently-verified`.\
Reviewer: `runner`; date: `2026-10-08`; snapshot: `49ed7cbf277adba16b2ccb3d8589252f8516fe5f`.\
Evidence: [independent source coverage verdict](./verification.md#ui-and-source-coverage).

| Substantive source claim group | Exact destination section | Reconciliation/disposition |
| --- | --- | --- |
| Combined infinite-scroll/manual-fallback scope and existing mode guards | [Knowledge section](../concepts/catalog-and-source-parsing.md#browse-discovery-and-request-identity) | incorporated; independently verified |
| Complete novel chapter aggregation with unchanged Source and retained intentionally single-page modes | [Knowledge section](../concepts/catalog-and-source-parsing.md#source-pagination-contracts) | incorporated; independently verified |
| Distinct Asura latest/popular URLs and parsed results; tests before production; limited UI/source impact | [Knowledge section](../concepts/catalog-and-source-parsing.md#test-and-review-obligations) | incorporated; independently verified |

### retrospective.md

Source identifier: `openspec/changes/archive/2026-07-09-fix-source-listing-pagination/retrospective.md`\
SHA256: `7d3800ba8681eb8cc34fa084cf089c46b46b7d986ffd1d5857d945bdbf7586d1`\
Coverage: `incorporated`. Verification: `independently-verified`.\
Reviewer: `runner`; date: `2026-10-08`; snapshot: `49ed7cbf277adba16b2ccb3d8589252f8516fe5f`.\
Evidence: [independent source coverage verdict](./verification.md#ui-and-source-coverage).

| Substantive source claim group | Exact destination section | Reconciliation/disposition |
| --- | --- | --- |
| Reported AJAX pageSize=200/totalPage, first-page HTML fallback, malformed stop, cancellation and unchanged Source | [Knowledge section](../concepts/catalog-and-source-parsing.md#freewebnovel-complete-chapter-lists) | incorporated; independently verified |
| Targeted tests caught edges; residual latency/markup drift; retain fixture selectors and investigate fresh slowdowns | [Knowledge section](../evidence/ui-source-history.md#pagination-implementation-and-verification) | incorporated; independently verified |

### tasks.md

Source identifier: `openspec/changes/archive/2026-07-09-fix-source-listing-pagination/tasks.md`\
SHA256: `095dca22478045fdb8b72dfa90869d4dbcd77ad790798ec02ad7470cdc8c50e4`\
Coverage: `incorporated`. Verification: `independently-verified`.\
Reviewer: `runner`; date: `2026-10-08`; snapshot: `49ed7cbf277adba16b2ccb3d8589252f8516fe5f`.\
Evidence: [independent source coverage verdict](./verification.md#ui-and-source-coverage).

| Substantive source claim group | Exact destination section | Reconciliation/disposition |
| --- | --- | --- |
| Checked auto-end and clickable manual-footer tasks with exact named tests and prior six-test adb/compile/VM claims | [Knowledge section](../evidence/ui-source-history.md#pagination-implementation-and-verification) | historical completion/test testimony |
| Checked complete novel page aggregation without changing one-page source semantics | [Knowledge section](../concepts/catalog-and-source-parsing.md#freewebnovel-complete-chapter-lists) | incorporated; independently verified |
| Checked latest/popular distinct URL/result proof and parser changes only on proven drift | [Knowledge section](../concepts/catalog-and-source-parsing.md#source-pagination-contracts) | incorporated; independently verified |
| Checked targeted/full unit and conditional APK assembly | [Knowledge section](../evidence/ui-source-history.md#pagination-implementation-and-verification) | historical task/test scope |

### verify.md

Source identifier: `openspec/changes/archive/2026-07-09-fix-source-listing-pagination/verify.md`\
SHA256: `14d1d7235169e814de5fdfc9b1db4b33b8f2d03ec8f9a1fb37d826439a23171d`\
Coverage: `incorporated`. Verification: `independently-verified`.\
Reviewer: `runner`; date: `2026-10-08`; snapshot: `49ed7cbf277adba16b2ccb3d8589252f8516fe5f`.\
Evidence: [independent source coverage verdict](./verification.md#ui-and-source-coverage).

| Substantive source claim group | Exact destination section | Reconciliation/disposition |
| --- | --- | --- |
| AndroidTest compilation, targeted BrowseViewModel/FreeWebNovel/Asura and six-test connected Browse passes | [Knowledge section](../evidence/ui-source-history.md#pagination-implementation-and-verification) | historical test testimony |
| Full unit15s/assemble33s/final AJAX edge tests; exact device APK install success | [Knowledge section](../evidence/ui-source-history.md#pagination-implementation-and-verification) | incorporated; independently verified |
| Historical SDK/rtk/device aliases and availability limitations | [Knowledge section](../evidence/ui-source-history.md#historical-command-environment) | incorporated; independently verified |

## 2026-07-11-refactor-library-browse-ui

### .openspec.yaml

Source identifier: `openspec/changes/archive/2026-07-11-refactor-library-browse-ui/.openspec.yaml`\
SHA256: `a34bc21f9d31425f26c0cf9ff4e567e01711e3cc2af572843f5065f03604971e`\
Coverage: `incorporated`. Verification: `independently-verified`.\
Reviewer: `runner`; date: `2026-10-08`; snapshot: `49ed7cbf277adba16b2ccb3d8589252f8516fe5f`.\
Evidence: [independent source coverage verdict](./verification.md#ui-and-source-coverage).

| Substantive source claim group | Exact destination section | Reconciliation/disposition |
| --- | --- | --- |
| house-style schema and created 2026-07-10; directory date not acceptance | [Knowledge section](../evidence/ui-source-history.md#lifecycle-facts) | historical facts incorporated |

### design.md

Source identifier: `openspec/changes/archive/2026-07-11-refactor-library-browse-ui/design.md`\
SHA256: `61f7ae61b30cce4d7dbc234c333a11900541ee302b30cbb6cc619e7baada1a6a`\
Coverage: `incorporated`. Verification: `independently-verified`.\
Reviewer: `runner`; date: `2026-10-08`; snapshot: `49ed7cbf277adba16b2ccb3d8589252f8516fe5f`.\
Evidence: [independent source coverage verdict](./verification.md#ui-and-source-coverage).

| Substantive source claim group | Exact destination section | Reconciliation/disposition |
| --- | --- | --- |
| Prefer extracted nonblank title; incoming only nonblank fallback; identity unchanged | [Knowledge section](../concepts/catalog-and-source-parsing.md#title-authority-and-bookmark-repair) | incorporated; independently verified |
| Success through existing refresh/persistence; retrieval/response/parse failure preserves bookmark; once per lifecycle/new lifecycle retry | [Knowledge section](../concepts/catalog-and-source-parsing.md#title-authority-and-bookmark-repair) | incorporated; independently verified |
| Reject incoming-before-parsing/schema/migration/background/bulk repair; failure may leave blanks | [Knowledge section](../concepts/catalog-and-source-parsing.md#title-authority-and-bookmark-repair) | incorporated; independently verified |
| Shared cover/title/action card, adaptive compact/expanded grid, Material tokens, density trade-off; reject duplicate cards | [Knowledge section](../concepts/catalog-and-source-parsing.md#shared-adaptive-catalog) | incorporated; independently verified |
| Historical explicit submission/no blank mode or source fetch; selected context/loading/retry/stale query/source/mode/page guards | [Knowledge section](../concepts/catalog-and-source-parsing.md#browse-discovery-and-request-identity) | historical policy with current evolution qualified |
| Remove undefined Unread/unused state; retain Library sort/search/removal/Samsung and Browse source/nav/paging/empty meaning; exclusions | [Knowledge section](../concepts/catalog-and-source-parsing.md#shared-adaptive-catalog) | incorporated; independently verified |
| Parser/repair/lifecycle/card/layout/Library/Browse testing requirements and request-coordination risk | [Knowledge section](../concepts/catalog-and-source-parsing.md#test-and-review-obligations) | incorporated; independently verified |

### plan.md

Source identifier: `openspec/changes/archive/2026-07-11-refactor-library-browse-ui/plan.md`\
SHA256: `ecb87714a03c0467714cab8df1f79d6e9171688978deffc5fa3db3ae8ba1f4da`\
Coverage: `incorporated`. Verification: `independently-verified`.\
Reviewer: `runner`; date: `2026-10-08`; snapshot: `49ed7cbf277adba16b2ccb3d8589252f8516fe5f`.\
Evidence: [independent source coverage verdict](./verification.md#ui-and-source-coverage).

| Substantive source claim group | Exact destination section | Reconciliation/disposition |
| --- | --- | --- |
| Five no-exception test-first slices and exact parser title/fallback cases | [Knowledge section](../concepts/catalog-and-source-parsing.md#test-and-review-obligations) | incorporated; independently verified |
| Repository success/retrieval/response/parse failure; VM identity/data and lifecycle retry tests | [Knowledge section](../concepts/catalog-and-source-parsing.md#title-authority-and-bookmark-repair) | incorporated; independently verified |
| Cover/title/caller action/compact-expanded/absent Unread plus retained interactions tests | [Knowledge section](../concepts/catalog-and-source-parsing.md#shared-adaptive-catalog) | incorporated; independently verified |
| Explicit submit/loading/retry/no blank fetch/navigation/paging/stale-response tests | [Knowledge section](../concepts/catalog-and-source-parsing.md#browse-discovery-and-request-identity) | historical intended cases |
| Fix or disposition findings after repair, UI cleanup, Browse before dependent slices | [Knowledge section](../concepts/catalog-and-source-parsing.md#test-and-review-obligations) | incorporated; independently verified |
| Targeted parser/repo/VM/content, unit/assemble/lint, conditional connected, separate fix/refactor purposes | [Knowledge section](../evidence/ui-source-history.md#catalog-refactor-verification-and-limits) | incorporated; independently verified |

### proposal.md

Source identifier: `openspec/changes/archive/2026-07-11-refactor-library-browse-ui/proposal.md`\
SHA256: `7de7509cc0d815587e14fea08834cdb4fee8fe7185d87ce15b52e6c24bc550b7`\
Coverage: `incorporated`. Verification: `independently-verified`.\
Reviewer: `runner`; date: `2026-10-08`; snapshot: `49ed7cbf277adba16b2ccb3d8589252f8516fe5f`.\
Evidence: [independent source coverage verdict](./verification.md#ui-and-source-coverage).

| Substantive source claim group | Exact destination section | Reconciliation/disposition |
| --- | --- | --- |
| Inconsistent list-first surfaces, blank saved titles and inert Unread motivation | [Knowledge section](../evidence/ui-source-history.md#catalog-refactor-verification-and-limits) | incorporated; independently verified |
| Cover-first adaptive shared UI retaining surface data/navigation; new catalog and repair capabilities | [Knowledge section](../concepts/catalog-and-source-parsing.md#shared-adaptive-catalog) | incorporated; independently verified |
| Source parsing title authority and existing bookmark repair without data loss | [Knowledge section](../concepts/catalog-and-source-parsing.md#title-authority-and-bookmark-repair) | incorporated; independently verified |
| Keep identity/Source/schema/dependencies/Samsung/save semantics; exclude Series detail; compact/expanded usability risk | [Knowledge section](../concepts/catalog-and-source-parsing.md#shared-adaptive-catalog) | incorporated; independently verified |

### tasks.md

Source identifier: `openspec/changes/archive/2026-07-11-refactor-library-browse-ui/tasks.md`\
SHA256: `94fb47376b6f10a0ee4da5f56a9a74594d401c1e5e3f121436ddacf43bcf37e5`\
Coverage: `incorporated`. Verification: `independently-verified`.\
Reviewer: `runner`; date: `2026-10-08`; snapshot: `49ed7cbf277adba16b2ccb3d8589252f8516fe5f`.\
Evidence: [independent source coverage verdict](./verification.md#ui-and-source-coverage).

| Substantive source claim group | Exact destination section | Reconciliation/disposition |
| --- | --- | --- |
| All five slice tasks checked; title preference/nonblank fallback/no blank fallback implemented/tested claims | [Knowledge section](../concepts/catalog-and-source-parsing.md#title-authority-and-bookmark-repair) | incorporated; independently verified |
| Checked identity/data preservation, failure boundaries, once/new lifecycle and refresh wiring | [Knowledge section](../concepts/catalog-and-source-parsing.md#title-authority-and-bookmark-repair) | incorporated; independently verified |
| Checked shared Material card/grid/actions/layouts and Unread removal retaining Library support | [Knowledge section](../concepts/catalog-and-source-parsing.md#shared-adaptive-catalog) | incorporated; independently verified |
| Checked explicit Browse submit/no blank/loading/retry/source/paging/stale/nav/UI work | [Knowledge section](../concepts/catalog-and-source-parsing.md#browse-discovery-and-request-identity) | historical conditions and testimony |
| Targeted content/parser/VM/repos and full unit/assemble complete claims | [Knowledge section](../evidence/ui-source-history.md#catalog-refactor-verification-and-limits) | historical completion, not new verification |

### verify.md

Source identifier: `openspec/changes/archive/2026-07-11-refactor-library-browse-ui/verify.md`\
SHA256: `942499a492d199d480906d886239bfdb03dfe376d5212e4309adb35cab96db2e`\
Coverage: `incorporated`. Verification: `independently-verified`.\
Reviewer: `runner`; date: `2026-10-08`; snapshot: `49ed7cbf277adba16b2ccb3d8589252f8516fe5f`.\
Evidence: [independent source coverage verdict](./verification.md#ui-and-source-coverage).

| Substantive source claim group | Exact destination section | Reconciliation/disposition |
| --- | --- | --- |
| Date2026-07-10; unit/assemble/lint successes; five slices/no regression and three gates fixed1/4/3 findings | [Knowledge section](../evidence/ui-source-history.md#catalog-refactor-verification-and-limits) | incorporated; independently verified |
| Offline emulator no connected tests; assemble alleged Compose pass does not prove execution | [Knowledge section](../evidence/implementation-gaps.md#catalog-connected-test-claim) | explicit test limit |
| JDK fallback/deprecated Sort/component dimensions pre-existing/deferred nonblocking | [Knowledge section](../evidence/ui-source-history.md#catalog-refactor-verification-and-limits) | incorporated; independently verified |

## 2026-07-14-unify-reader-screen

### .openspec.yaml

Source identifier: `openspec/changes/archive/2026-07-14-unify-reader-screen/.openspec.yaml`\
SHA256: `3759f69e2e5c3a68349b4487279f8453f0e61fa08056dc2be4abe2f4b4614792`\
Coverage: `incorporated`. Verification: `independently-verified`.\
Reviewer: `runner`; date: `2026-10-08`; snapshot: `49ed7cbf277adba16b2ccb3d8589252f8516fe5f`.\
Evidence: [independent source coverage verdict](./verification.md#ui-and-source-coverage).

| Substantive source claim group | Exact destination section | Reconciliation/disposition |
| --- | --- | --- |
| house-style schema and created2026-07-12; archived location not acceptance | [Knowledge section](../evidence/ui-source-history.md#lifecycle-facts) | incorporated; independently verified |

### design.md

Source identifier: `openspec/changes/archive/2026-07-14-unify-reader-screen/design.md`\
SHA256: `310c73580ed895bf2f9c92f162fece9ea414c46d0d7eac841624ff1d94833193`\
Coverage: `incorporated`. Verification: `independently-verified`.\
Reviewer: `runner`; date: `2026-10-08`; snapshot: `49ed7cbf277adba16b2ccb3d8589252f8516fe5f`.\
Evidence: [independent source coverage verdict](./verification.md#ui-and-source-coverage).

| Substantive source claim group | Exact destination section | Reconciliation/disposition |
| --- | --- | --- |
| One screen/state/control workflow and exactly Text(html)/Pages(imageUrls); JS-disabled WebView/ordered lazy pages; no shape flattening | [Knowledge section](../concepts/reader-runtime.md#shared-workflow-and-domain-boundary) | incorporated; independently verified |
| Explicit source/series/chapter/type route/stub; ViewModel excludes SourceRegistry; reject late inference | [Knowledge section](../concepts/reader-runtime.md#explicit-route-context) | incorporated; independently verified |
| Remove undocumented internal aliases; Series deep links unchanged; restored-route risk | [Knowledge section](../concepts/reader-runtime.md#explicit-route-context) | incorporated; independently verified |
| One forced network bypass after download/type mismatch; retryable remaining mismatch; cancel/loading/error clearing | [Knowledge section](../concepts/reader-runtime.md#content-loading-and-recovery) | incorporated; independently verified |
| Tap controls top chapter/Back bottom actions percentage/page/hidden progress; private renderers and gesture/accessibility | [Knowledge section](../concepts/reader-runtime.md#shared-controls-and-gestures) | incorporated; independently verified |
| Text clamp/distinct source-chapter persistence/restore after layout/immediate-read; pages zero/center/no persistence/final-read | [Knowledge section](../concepts/reader-runtime.md#progress-and-read-semantics-requiring-reconciliation) | detailed intent and conflicts retained |
| Reject controls-only extraction/synthetic pages/new renderer hierarchy; conditional fields vs polymorphic-state trade-off | [Knowledge section](../concepts/reader-runtime.md#shared-workflow-and-domain-boundary) | incorporated; independently verified |
| No preferences/orientation/gesture/prefetch/sheet/order/schema/format/dependency changes; VM/content/route/repo tests and timing-lock-before-removal | [Knowledge section](../concepts/reader-runtime.md#verification-and-implementation-lessons) | incorporated; independently verified |

### plan.md

Source identifier: `openspec/changes/archive/2026-07-14-unify-reader-screen/plan.md`\
SHA256: `54a620e9b7d792f29dd3d73ac9879c5393c87df381a091098625fec650f0946a`\
Coverage: `incorporated`. Verification: `independently-verified`.\
Reviewer: `runner`; date: `2026-10-08`; snapshot: `49ed7cbf277adba16b2ccb3d8589252f8516fe5f`.\
Evidence: [independent source coverage verdict](./verification.md#ui-and-source-coverage).

| Substantive source claim group | Exact destination section | Reconciliation/disposition |
| --- | --- | --- |
| State test-first Text/Pages/type/mismatch/dead Retry/cancel/actions/download/progress/read conditions | [Knowledge section](../concepts/reader-runtime.md#verification-and-implementation-lessons) | incorporated; independently verified |
| Shared renderer/control/labels/Back/Retry tests before deletion | [Knowledge section](../concepts/reader-runtime.md#shared-controls-and-gestures) | incorporated; independently verified |
| Route/SavedStateHandle/adjacent/sheet identity before callback/destination/wiring migration; remove only after unified tests | [Knowledge section](../concepts/reader-runtime.md#explicit-route-context) | incorporated; independently verified |
| Documentation sync after established shape; docs-only testing exception; retired codemap technique | [Knowledge section](../concepts/reader-runtime.md#verification-and-implementation-lessons) | incorporated; independently verified |
| Four checkpoints state/gesture/routes/docs fix-disposition before next; targeted unit/repo/nav/content and full unit/assemble/lint/conditional connected | [Knowledge section](../evidence/ui-source-history.md#reader-verification-and-retrospective) | incorporated; independently verified |
| Historical state-content/routes/docs commit grouping | [Knowledge section](../evidence/ui-source-history.md#reader-verification-and-retrospective) | incorporated; independently verified |

### proposal.md

Source identifier: `openspec/changes/archive/2026-07-14-unify-reader-screen/proposal.md`\
SHA256: `f4450b708c00e43561a5a29d4f2e39980f25a5748b300554b1d73e934d1a73f5`\
Coverage: `incorporated`. Verification: `independently-verified`.\
Reviewer: `runner`; date: `2026-10-08`; snapshot: `49ed7cbf277adba16b2ccb3d8589252f8516fe5f`.\
Evidence: [independent source coverage verdict](./verification.md#ui-and-source-coverage).

| Substantive source claim group | Exact destination section | Reconciliation/disposition |
| --- | --- | --- |
| Duplicated NOVEL/MANHWA routes/state/control drift and inert Back/Retry motivation | [Knowledge section](../concepts/reader-runtime.md#shared-workflow-and-domain-boundary) | incorporated; independently verified |
| One four-file Reader with Text WebView/Pages images, immersive chrome and unchanged shapes | [Knowledge section](../concepts/reader-runtime.md#shared-controls-and-gestures) | incorporated; independently verified |
| Read/progress/offline/enqueue behavior and functional Back/Retry; changed caller not queue | [Knowledge section](../concepts/reader-runtime.md#content-loading-and-recovery) | incorporated; independently verified |
| ui/reader route content-type context; source/repo/schema/identity/storage/dependencies stable; architecture/index knowledge sync | [Knowledge section](../concepts/reader-runtime.md#explicit-route-context) | incorporated; independently verified |

### retrospective.md

Source identifier: `openspec/changes/archive/2026-07-14-unify-reader-screen/retrospective.md`\
SHA256: `c6a97aaf5b820742d21c9506620a5badc516a688b7d3d8dd0f0d8235e9e159a2`\
Coverage: `incorporated`. Verification: `independently-verified`.\
Reviewer: `runner`; date: `2026-10-08`; snapshot: `49ed7cbf277adba16b2ccb3d8589252f8516fe5f`.\
Evidence: [independent source coverage verdict](./verification.md#ui-and-source-coverage).

| Substantive source claim group | Exact destination section | Reconciliation/disposition |
| --- | --- | --- |
| Unified VM/content/screen/route/callback,6new16deleted129added2167removed-net2038/no dependency and docs sync | [Knowledge section](../evidence/ui-source-history.md#reader-verification-and-retrospective) | incorporated; independently verified |
| Test-first mismatch/clamp, parallel VM-content dispatch, reviews caught reload/Retry/progress race | [Knowledge section](../evidence/ui-source-history.md#reader-verification-and-retrospective) | incorporated; independently verified |
| Debounced persistence vs design, restoreProgress/pendingProgress, post after page finish and very-long-page concern | [Knowledge section](../concepts/reader-runtime.md#progress-and-read-semantics-requiring-reconciliation) | incorporated; independently verified |
| processLoadedContent normal/forced reuse indirection; ManhwaPageList extraction with~300 overlay lines | [Knowledge section](../concepts/reader-runtime.md#verification-and-implementation-lessons) | incorporated; independently verified |
| Follow-up tap/Back content/route-builder/destruction/sealed-state suggestions and contradictory complete claims | [Knowledge section](../evidence/implementation-gaps.md#reader-progress-and-coverage) | incorporated; independently verified |

### tasks.md

Source identifier: `openspec/changes/archive/2026-07-14-unify-reader-screen/tasks.md`\
SHA256: `bed7e4014e70c3d0a67424ac3c1c6a4b3bd88df3af5424f7624f6af8fa031918`\
Coverage: `incorporated`. Verification: `independently-verified`.\
Reviewer: `runner`; date: `2026-10-08`; snapshot: `49ed7cbf277adba16b2ccb3d8589252f8516fe5f`.\
Evidence: [independent source coverage verdict](./verification.md#ui-and-source-coverage).

| Substantive source claim group | Exact destination section | Reconciliation/disposition |
| --- | --- | --- |
| Checked single state/type/identity and unchanged contracts; mismatch/dead Retry/loading/errors/cancel/chapter/download conditions | [Knowledge section](../concepts/reader-runtime.md#content-loading-and-recovery) | incorporated; independently verified |
| Checked distinct clamp/write/restore/immediate-read/page-zero/nonpersist/final-read despite conflicting reports | [Knowledge section](../evidence/implementation-gaps.md#reader-progress-and-coverage) | incorporated; independently verified |
| Checked loading/error/actions/tap/render labels and scroll-compatible private renderers | [Knowledge section](../concepts/reader-runtime.md#shared-controls-and-gestures) | incorporated; independently verified |
| Checked typed callbacks/destinations/identity/screen/effects/sheet/old screens/routes/tests deletion after coverage | [Knowledge section](../concepts/reader-runtime.md#explicit-route-context) | incorporated; independently verified |
| Checked architecture/AGENTS/codemap/spec sync, reviews and full unit/assemble/lint plus targeted Compose claims | [Knowledge section](../evidence/ui-source-history.md#reader-verification-and-retrospective) | historical testimony with explicit coverage limits |

### verify.md

Source identifier: `openspec/changes/archive/2026-07-14-unify-reader-screen/verify.md`\
SHA256: `4fe8eb62d933016a3e886b49b929c833f8b4cd86fe98c6a2acc5e66b7f1276bf`\
Coverage: `incorporated`. Verification: `independently-verified`.\
Reviewer: `runner`; date: `2026-10-08`; snapshot: `49ed7cbf277adba16b2ccb3d8589252f8516fe5f`.\
Evidence: [independent source coverage verdict](./verification.md#ui-and-source-coverage).

| Substantive source claim group | Exact destination section | Reconciliation/disposition |
| --- | --- | --- |
| Historical unit/assemble/lint PASS and no blockers/warnings oracle quote | [Knowledge section](../evidence/ui-source-history.md#reader-verification-and-retrospective) | incorporated; independently verified |
| Nine resolved findings: restoration/dead Retry/initial errors/write success/remembered HTML/disposal/extraction/helper/task overclaims | [Knowledge section](../evidence/ui-source-history.md#reader-verification-and-retrospective) | incorporated; independently verified |
| Precision of persistence/disposal/test claims contested by retrospective | [Knowledge section](../evidence/implementation-gaps.md#reader-progress-and-coverage) | incorporated; independently verified |

## 2026-07-27-fix-brose-init-fetch-and-reader-scroll

### .openspec.yaml

Source identifier: `openspec/changes/archive/2026-07-27-fix-brose-init-fetch-and-reader-scroll/.openspec.yaml`\
SHA256: `c9a4d0d2b9fe34ff607dbb646c6ee7acff47459d8a121a243bb20350f06dff91`\
Coverage: `incorporated`. Verification: `independently-verified`.\
Reviewer: `runner`; date: `2026-10-08`; snapshot: `49ed7cbf277adba16b2ccb3d8589252f8516fe5f`.\
Evidence: [independent source coverage verdict](./verification.md#ui-and-source-coverage).

| Substantive source claim group | Exact destination section | Reconciliation/disposition |
| --- | --- | --- |
| house-style schema and created2026-07-27; archive directory does not prove acceptance | [Knowledge section](../evidence/ui-source-history.md#lifecycle-facts) | incorporated; independently verified |

### design.md

Source identifier: `openspec/changes/archive/2026-07-27-fix-brose-init-fetch-and-reader-scroll/design.md`\
SHA256: `a680946e4b86a1cceeca31ca562de0165f0d2e06044c5b89564764facc2c711b`\
Coverage: `incorporated`. Verification: `independently-verified`.\
Reviewer: `runner`; date: `2026-10-08`; snapshot: `49ed7cbf277adba16b2ccb3d8589252f8516fe5f`.\
Evidence: [independent source coverage verdict](./verification.md#ui-and-source-coverage).

| Substantive source claim group | Exact destination section | Reconciliation/disposition |
| --- | --- | --- |
| Mode POPULAR/helper works on source/mode but absent init; tests must reflect first fetch | [Knowledge section](../concepts/catalog-and-source-parsing.md#browse-discovery-and-request-identity) | incorporated; independently verified |
| Astro div[data-page] HTML/Jsoup limitation; ColorPainter/FillWidth zero-size collapse | [Knowledge section](../concepts/reader-runtime.md#measurable-image-pages) | incorporated; independently verified |
| Conditional /api/novels/{slug}/download hypothesis rather than accepted complete API | [Knowledge section](../evidence/implementation-gaps.md#asura-endpoint-and-completeness) | incorporated; independently verified |
| API format/auth/reliability risk; HTML attributes/metadata alternative; reconsider scope if not complete | [Knowledge section](../concepts/catalog-and-source-parsing.md#asurascans-complete-image-pages) | incorporated; independently verified |
| Viewport minimum/example100dp/varied screen sizes,403-expired failures vs actual150dp | [Knowledge section](../concepts/reader-runtime.md#measurable-image-pages) | incorporated; independently verified |
| No Source/HtmlSource/architecture/novel/image-performance broadening | [Knowledge section](../concepts/catalog-and-source-parsing.md#asurascans-complete-image-pages) | incorporated; independently verified |

### plan.md

Source identifier: `openspec/changes/archive/2026-07-27-fix-brose-init-fetch-and-reader-scroll/plan.md`\
SHA256: `6a0aee95924338ae24a2444dcc8996548afe9882a9be076c95d08882366aed02`\
Coverage: `incorporated`. Verification: `independently-verified`.\
Reviewer: `runner`; date: `2026-10-08`; snapshot: `49ed7cbf277adba16b2ccb3d8589252f8516fe5f`.\
Evidence: [independent source coverage verdict](./verification.md#ui-and-source-coverage).

| Substantive source claim group | Exact destination section | Reconciliation/disposition |
| --- | --- | --- |
| Failing renamed init fetch-Popular test before production | [Knowledge section](../concepts/catalog-and-source-parsing.md#test-and-review-obligations) | incorporated; independently verified |
| All item scroll range test/snapshot measurement/manual TDD exception for uncontrollable loading | [Knowledge section](../concepts/reader-runtime.md#verification-and-implementation-lessons) | incorporated; independently verified |
| API/HTML exploratory sample and fixture completeness before integration; three ordered slices | [Knowledge section](../evidence/ui-source-history.md#browse-launch-and-image-completeness-evidence) | incorporated; independently verified |
| Initial/API review gates; full unit verification intent; separate three bug-fix commits | [Knowledge section](../evidence/ui-source-history.md#browse-launch-and-image-completeness-evidence) | incorporated; independently verified |

### proposal.md

Source identifier: `openspec/changes/archive/2026-07-27-fix-brose-init-fetch-and-reader-scroll/proposal.md`\
SHA256: `cd9c08cb163d9eac047aed0a504dd9bb07a2124c32bce77c5be2c90c11469634`\
Coverage: `incorporated`. Verification: `independently-verified`.\
Reviewer: `runner`; date: `2026-10-08`; snapshot: `49ed7cbf277adba16b2ccb3d8589252f8516fe5f`.\
Evidence: [independent source coverage verdict](./verification.md#ui-and-source-coverage).

| Substantive source claim group | Exact destination section | Reconciliation/disposition |
| --- | --- | --- |
| Initial Popular empty/no loading due selected source without fetch; old no-fetch assertion must change | [Knowledge section](../concepts/catalog-and-source-parsing.md#browse-discovery-and-request-identity) | incorporated; independently verified |
| Scroll truncation JS lazy-loading hypothesis plus zero-intrinsic placeholder/error collapse | [Knowledge section](../concepts/catalog-and-source-parsing.md#asurascans-complete-image-pages) | incorporated; independently verified |
| Restore initial fetch/full source pages/measurable items; scope source/composable and no contract/dataflow changes | [Knowledge section](../concepts/reader-runtime.md#measurable-image-pages) | incorporated; independently verified |
| API only if verified; expected repairs not new capabilities; source-scoped risk | [Knowledge section](../concepts/catalog-and-source-parsing.md#asurascans-complete-image-pages) | incorporated; independently verified |

### retrospective.md

Source identifier: `openspec/changes/archive/2026-07-27-fix-brose-init-fetch-and-reader-scroll/retrospective.md`\
SHA256: `adbcd5d8acd6372633cb513c610374db6a5cedf02cea578d770525b54efb2250`\
Coverage: `incorporated`. Verification: `independently-verified`.\
Reviewer: `runner`; date: `2026-10-08`; snapshot: `49ed7cbf277adba16b2ccb3d8589252f8516fe5f`.\
Evidence: [independent source coverage verdict](./verification.md#ui-and-source-coverage).

| Substantive source claim group | Exact destination section | Reconciliation/disposition |
| --- | --- | --- |
| Shipped init auto-fetch and actual150dp measurable height | [Knowledge section](../evidence/ui-source-history.md#browse-launch-and-image-completeness-evidence) | incorporated; independently verified |
| Correct series_slug/chapter_slug endpoint vs design; HTML on failure; dimensions future | [Knowledge section](../concepts/catalog-and-source-parsing.md#asurascans-complete-image-pages) | incorporated; independently verified |
| API discovery/delegated clean slices/existing tests with no new fixtures | [Knowledge section](../evidence/ui-source-history.md#browse-launch-and-image-completeness-evidence) | incorporated; independently verified |
| chapter-N vs old UUID fallback; extreme900x16000 images; no follow-ups asserted | [Knowledge section](../concepts/reader-runtime.md#measurable-image-pages) | incorporated; independently verified |

### tasks.md

Source identifier: `openspec/changes/archive/2026-07-27-fix-brose-init-fetch-and-reader-scroll/tasks.md`\
SHA256: `38f3bc720e19ecd62404774b8a6f22e6e97620c23ade146bf743196b06daa3af`\
Coverage: `incorporated`. Verification: `independently-verified`.\
Reviewer: `runner`; date: `2026-10-08`; snapshot: `49ed7cbf277adba16b2ccb3d8589252f8516fe5f`.\
Evidence: [independent source coverage verdict](./verification.md#ui-and-source-coverage).

| Substantive source claim group | Exact destination section | Reconciliation/disposition |
| --- | --- | --- |
| Checked autoFetchCurrentMode(first.id,POPULAR), renamed fetch assertion, VM green | [Knowledge section](../concepts/catalog-and-source-parsing.md#browse-discovery-and-request-identity) | incorporated; independently verified |
| Actual series/chapter API sample completeness and getChapterContent HTML fallback | [Knowledge section](../concepts/catalog-and-source-parsing.md#asurascans-complete-image-pages) | incorporated; independently verified |
| HTML enhancement N/A because API works; old parser tests retained and green | [Knowledge section](../evidence/ui-source-history.md#browse-launch-and-image-completeness-evidence) | explicit task disposition |
| Checked150dp/manual measurable-height verification/reader regression | [Knowledge section](../concepts/reader-runtime.md#measurable-image-pages) | incorporated; independently verified |

### verify.md

Source identifier: `openspec/changes/archive/2026-07-27-fix-brose-init-fetch-and-reader-scroll/verify.md`\
SHA256: `dcb444fcaeac23e51c7b1fff38f6e805027896dacbb50b809cf206b101c6660d`\
Coverage: `incorporated`. Verification: `independently-verified`.\
Reviewer: `runner`; date: `2026-10-08`; snapshot: `49ed7cbf277adba16b2ccb3d8589252f8516fe5f`.\
Evidence: [independent source coverage verdict](./verification.md#ui-and-source-coverage).

| Substantive source claim group | Exact destination section | Reconciliation/disposition |
| --- | --- | --- |
| Review no actions/all tests/live exploratory preconditions asserted | [Knowledge section](../evidence/ui-source-history.md#browse-launch-and-image-completeness-evidence) | incorporated; independently verified |
| Fullunit6s34tasks1executed33uptodate370zero failed/skipped;Asura20zero failures;Browse pass | [Knowledge section](../evidence/ui-source-history.md#browse-launch-and-image-completeness-evidence) | incorporated; independently verified |
| Exact curl player-who-cant-level-up/chapter-237 returned16pages+dims; one sample limitation | [Knowledge section](../evidence/ui-source-history.md#browse-launch-and-image-completeness-evidence) | incorporated; independently verified |
| No lint issues claim but no lint command; not full fallback/device assurance | [Knowledge section](../evidence/implementation-gaps.md#asura-endpoint-and-completeness) | incorporated; independently verified |
| Historical env identifiers preserved, not current availability | [Knowledge section](../evidence/ui-source-history.md#historical-command-environment) | incorporated; independently verified |

## optimize-source-search-cache

### .openspec.yaml

Source identifier: `openspec/changes/optimize-source-search-cache/.openspec.yaml`\
SHA256: `3759f69e2e5c3a68349b4487279f8453f0e61fa08056dc2be4abe2f4b4614792`\
Coverage: `incorporated`. Verification: `independently-verified`.\
Reviewer: `runner`; date: `2026-10-08`; snapshot: `49ed7cbf277adba16b2ccb3d8589252f8516fe5f`.\
Evidence: [independent source coverage verdict](./verification.md#ui-and-source-coverage).

| Substantive source claim group | Exact destination section | Reconciliation/disposition |
| --- | --- | --- |
| house-style schema/created2026-07-12; active unarchived despite checked tasks | [Knowledge section](../evidence/ui-source-history.md#lifecycle-facts) | incorporated; independently verified |

### design.md

Source identifier: `openspec/changes/optimize-source-search-cache/design.md`\
SHA256: `f3a54fce02231061b3aaad0c406bd58490adf57a2e2e5a23de5a28ac7f462ee4`\
Coverage: `incorporated`. Verification: `independently-verified`.\
Reviewer: `runner`; date: `2026-10-08`; snapshot: `49ed7cbf277adba16b2ccb3d8589252f8516fe5f`.\
Evidence: [independent source coverage verdict](./verification.md#ui-and-source-coverage).

| Substantive source claim group | Exact destination section | Reconciliation/disposition |
| --- | --- | --- |
| HTTP50MB/server-header reuse vs parsed freshness; Room no ordering/freshness and DownloadStore/Coil ownership | [Knowledge section](../concepts/source-metadata-performance.md#ownership-and-scope) | incorporated; independently verified |
| Synchronized access-order monotonic keys source/op/normalizedquery/page/immutable orderedfilters; filter significance/type isolation | [Knowledge section](../concepts/source-metadata-performance.md#exact-fresh-metadata-cache) | incorporated; independently verified |
| Exact fresh-hit no Source,expired purge,successful-only remote truth,100/50/20capacities5/15/2minuteTTL | [Knowledge section](../concepts/source-metadata-performance.md#exact-fresh-metadata-cache) | incorporated; independently verified |
| Entry-vs-byte policy/process loss/staleness and persistent/Ktor/schema/nooffline exclusions | [Knowledge section](../concepts/source-metadata-performance.md#ownership-and-scope) | incorporated; independently verified |
| Browse300ms actual debounce/request cancellation/finalidentity/blankclear/newsource rerun/paging/cache state | [Knowledge section](../concepts/source-metadata-performance.md#browse-live-search-and-local-fallback) | incorporated; independently verified |
| Only pageone remoteempty/failure selectedsource TitleMatcher; nonempty no next; emptymatch failure propagated; no later fallback | [Knowledge section](../concepts/source-metadata-performance.md#browse-live-search-and-local-fallback) | incorporated; independently verified |
| One Library+downloadindexable snapshot/providerorder mapping/direct query without availability probe; title-author-genre/titlefirst/nomatch | [Knowledge section](../concepts/source-metadata-performance.md#samsung-first-library-snapshot-and-fallback) | incorporated; independently verified |
| Library300ms cancellation/immediate active-invalidation/blank behavior and ContentResolver limitation | [Knowledge section](../concepts/source-metadata-performance.md#samsung-first-library-snapshot-and-fallback) | incorporated; independently verified |
| First-page discovery then3-window pageorderURLdedup/cancel/failureblankzerounseenstop/ignorelater/no-next/max2avoidables | [Knowledge section](../concepts/source-metadata-performance.md#freewebnovel-bounded-remainder-proposal) | incorporated; independently verified |
| Reject unboundedasync/persistentquery/Ktor/error-hiding/crosssource/FTS; preserve source ranking/timeout/retry/provider lifecycle | [Knowledge section](../concepts/source-metadata-performance.md#ownership-and-scope) | incorporated; independently verified |
| Cache/Browse/repo/Library/concurrency testing and memory/stale/local-corpus/requestburst trade-offs | [Knowledge section](../concepts/source-metadata-performance.md#verification-obligations-and-review-findings) | incorporated; independently verified |

### plan.md

Source identifier: `openspec/changes/optimize-source-search-cache/plan.md`\
SHA256: `0107bee44ce9c015389c3addacf3c8118af1dd6f13c2c7f0c47baece36a34eb6`\
Coverage: `incorporated`. Verification: `independently-verified`.\
Reviewer: `runner`; date: `2026-10-08`; snapshot: `49ed7cbf277adba16b2ccb3d8589252f8516fe5f`.\
Evidence: [independent source coverage verdict](./verification.md#ui-and-source-coverage).

| Substantive source claim group | Exact destination section | Reconciliation/disposition |
| --- | --- | --- |
| Controlled clock exact TTL/capacity/expiry/LRU/keyorderedfilter/failure exclusions before smallest cache | [Knowledge section](../concepts/source-metadata-performance.md#verification-obligations-and-review-findings) | incorporated; independently verified |
| Browse job/debounce/source/blank/late/cache/pagination and repo fallback/error boundaries test-first | [Knowledge section](../concepts/source-metadata-performance.md#browse-live-search-and-local-fallback) | incorporated; independently verified |
| Snapshot/order/fields/eligibility/unavailability/nomatch plus VMdebounce/cancel/invalidation tests | [Knowledge section](../concepts/source-metadata-performance.md#samsung-first-library-snapshot-and-fallback) | incorporated; independently verified |
| Deferred MockEngine3-window/count/pageorder/dedup/zerounseen/cancel/ignorelater/contiguous tests before serial replacement | [Knowledge section](../concepts/source-metadata-performance.md#freewebnovel-bounded-remainder-proposal) | incorporated; independently verified |
| Four no-exception test-first slices and fix-disposition checkpoints ownership/cancellation/eligibility/concurrency | [Knowledge section](../concepts/source-metadata-performance.md#verification-obligations-and-review-findings) | incorporated; independently verified |
| Targeted cache/repos/VMs/TitleMatcher/source,unit/assemble/lint,conditional connected; four commit purposes including historical refactor concurrency label | [Knowledge section](../evidence/ui-source-history.md#performance-verification-and-overclaims) | incorporated; independently verified |

### proposal.md

Source identifier: `openspec/changes/optimize-source-search-cache/proposal.md`\
SHA256: `226a9c190fa45840eac27b1d9c6561a87cfed19f4d883e7325fadbfb1e49e7b5`\
Coverage: `incorporated`. Verification: `independently-verified`.\
Reviewer: `runner`; date: `2026-10-08`; snapshot: `49ed7cbf277adba16b2ccb3d8589252f8516fe5f`.\
Evidence: [independent source coverage verdict](./verification.md#ui-and-source-coverage).

| Substantive source claim group | Exact destination section | Reconciliation/disposition |
| --- | --- | --- |
| Explicit submission/obsolete work/freshness/provider unavailable/N+1/serial latency motivation | [Knowledge section](../evidence/ui-source-history.md#performance-verification-and-overclaims) | incorporated; independently verified |
| Debounced source-specific cancellation and local page-one empty/failure fallback | [Knowledge section](../concepts/source-metadata-performance.md#browse-live-search-and-local-fallback) | incorporated; independently verified |
| Cross-type parsed catalog/search/detail/list cache while identity/download/Coil/schema/dependencies unchanged | [Knowledge section](../concepts/source-metadata-performance.md#ownership-and-scope) | incorporated; independently verified |
| Samsung-first eligible snapshot local-failure fallback without provider-order changes | [Knowledge section](../concepts/source-metadata-performance.md#samsung-first-library-snapshot-and-fallback) | incorporated; independently verified |
| Bounded remainder ordering/dedup/source parsing-interface compatibility | [Knowledge section](../concepts/source-metadata-performance.md#freewebnovel-bounded-remainder-proposal) | incorporated; independently verified |

### tasks.md

Source identifier: `openspec/changes/optimize-source-search-cache/tasks.md`\
SHA256: `7dda21f178aa7ec1de8901d9db0b399d6f9962d6ffec12a0c966c3b0eb648176`\
Coverage: `incorporated`. Verification: `independently-verified`.\
Reviewer: `runner`; date: `2026-10-08`; snapshot: `49ed7cbf277adba16b2ccb3d8589252f8516fe5f`.\
Evidence: [independent source coverage verdict](./verification.md#ui-and-source-coverage).

| Substantive source claim group | Exact destination section | Reconciliation/disposition |
| --- | --- | --- |
| Checked exact TTL/capacity/clock/LRU/orderedkeys/exclusion and metadata operation caches without reader ownership change | [Knowledge section](../concepts/source-metadata-performance.md#exact-fresh-metadata-cache) | incorporated; independently verified |
| Checked Browse300ms/cancel/blank/source rerun/cache/fallback/error/nolater/loading/retry/keyboard/paging | [Knowledge section](../concepts/source-metadata-performance.md#browse-live-search-and-local-fallback) | incorporated; independently verified |
| Checked eligible batch/noN+1/providerorder/fields/titlefirst/nomatch and VMdebounce/cancel/invalidation/blank despite gaps | [Knowledge section](../evidence/implementation-gaps.md#library-debounce-and-ranking) | incorporated; independently verified |
| Checked3-windoworder/dedup/cancel/failblankzerounseen/contiguous/ignorelater/no-nextwindow/detailHTMLfallback despite serial source | [Knowledge section](../evidence/implementation-gaps.md#freewebnovel-concurrency) | incorporated; independently verified |
| Checked reviews every finding resolved and targeted/fullunit/assemble/lint | [Knowledge section](../evidence/ui-source-history.md#performance-verification-and-overclaims) | incorporated; independently verified |

### verify.md

Source identifier: `openspec/changes/optimize-source-search-cache/verify.md`\
SHA256: `30c09781027fba801d5879fb50b016348a7b2b8fbca3da4143827e70d249fdb1`\
Coverage: `incorporated`. Verification: `independently-verified`.\
Reviewer: `runner`; date: `2026-10-08`; snapshot: `49ed7cbf277adba16b2ccb3d8589252f8516fe5f`.\
Evidence: [independent source coverage verdict](./verification.md#ui-and-source-coverage).

| Substantive source claim group | Exact destination section | Reconciliation/disposition |
| --- | --- | --- |
| Historical SDK unit/assemble/lint PASS and all-slices assertion | [Knowledge section](../evidence/ui-source-history.md#performance-verification-and-overclaims) | incorporated; independently verified |
| Cache monotonicTTL/LRU/keyencoding and Browse300ms/source/cancel/blank/cache plus Library Samsungfirst batchfallback claims | [Knowledge section](../concepts/source-metadata-performance.md#verification-obligations-and-review-findings) | incorporated; independently verified |
| Sequential AJAX dedup falsely substitutes bounded concurrency | [Knowledge section](../evidence/implementation-gaps.md#freewebnovel-concurrency) | incorporated; independently verified |
| Reported wraparound/keycollision/coverage/jobs/nonLibrarysnapshot fixed/allfindings resolved | [Knowledge section](../evidence/ui-source-history.md#performance-verification-and-overclaims) | incorporated; independently verified |
| Missing Library debounce/title-first cannot be established by green checks | [Knowledge section](../evidence/implementation-gaps.md#library-debounce-and-ranking) | incorporated; independently verified |
