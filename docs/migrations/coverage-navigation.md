---
id: evidence-coverage-navigation
title: "Architecture navigation and tooling source coverage"
type: Evidence
status: active
valid_from: 2026-10-08
scope:
  system: readerparser
relations:
  - type: relates_to
    target: ../architecture.md
---

# Architecture navigation and tooling source coverage

Migration date: 2026-10-08. Source baseline HEAD: `49ed7cbf277adba16b2ccb3d8589252f8516fe5f`.

Each of the 55 original artifact SHA256 values was captured before routing replacement and checked again immediately before mutation.

All 104 OpenSpec sources passed independent semantic coverage gates: 43 specification/configuration, 28 infrastructure, and 33 UI/source artifacts.
The independent runner_navigation returned `VERDICT: PASSED` for all 55 navigation sources after re-review on 2026-10-08.
The first review found eight bounded incorporation gaps. The corrected destinations passed independent revalidation.
The runner checked 190 substantive mapping rows, 231 destination links, and all 55 original hashes.
Its full graph/link check covered 1042 links, 56 nodes, and 144 edges, with no integrity findings.
This ledger has 190 substantive mapping rows and 55 table separator rows.
Every source below is `incorporated` and `independently-verified`. See [verification](./verification.md#navigation-and-root-documentation-coverage).
The separate four-source generated-snapshot gate also passed before authorized coordinated cleanup.
Cleanup removed 49 original artifacts and installed six reviewed routing replacements.
Independent runner_navigation final delivered-state `VERDICT: PASSED` on 2026-10-08 verified routing, retirement, and the delivered vault's graph and links.

The reviewed root README/AGENTS and four existing scoped AGENTS replacements are installed and authoritative.

Source path/hash pairs are provenance identifiers, not links to files promised after retirement.

Directory tables, ASCII trees, boilerplate headings, repeated diagrams, examples, and status banners are not retained mechanically.

Their durable responsibilities/relationships/conditions are incorporated below. Generic framework examples and generator hash payloads have explicit no-durable-substance dispositions.

## app/codemap.md

SHA256: `f305fa818ff7d34517e82fa06dd7b493c69f4f9fe7a8fb23af0b126718ae7e2e`

Coverage status: `incorporated`. Verification: `independently-verified` (2026-10-08, runner_navigation).

| Extracted source substance | Exact destination sections | Disposition |
| --- | --- | --- |
| Single Android module, APK identity, manifest/Hilt/WorkManager startup, resources | [concepts/runtime-wiring.md#entrypoints-and-production-structure](../concepts/runtime-wiring.md#entrypoints-and-production-structure) | incorporated/deduplicated; historical clauses explicitly qualified |
| SDK/Java/build types, KSP schema export, signing env keys, APK outputs, catalog/wrapper | [runbooks/development.md#setup-and-build](../runbooks/development.md#setup-and-build) | incorporated/deduplicated; historical clauses explicitly qualified |
| Stale claims, illustrative assumptions, obsolete locations, and source-only navigation policy | [evidence/navigation-and-tooling-history.md#current-corrections-and-unresolved-discrepancies](../evidence/navigation-and-tooling-history.md#current-corrections-and-unresolved-discrepancies) | historical; no new app behavior inferred |

## app/src/codemap.md

SHA256: `322852bbe8ee9011c7f72759ede2c65c1f69d5fd69fdf783b87b922a6b9c5da9`

Coverage status: `incorporated`. Verification: `independently-verified` (2026-10-08, runner_navigation).

| Extracted source substance | Exact destination sections | Disposition |
| --- | --- | --- |
| main/test/androidTest split, JVM/device task ownership, shared classpaths/libraries/test naming | [runbooks/development.md#verification-gate-and-tests](../runbooks/development.md#verification-gate-and-tests) | incorporated/deduplicated; historical clauses explicitly qualified |
| Room exported schemas are instrumentation assets via Gradle sourceSets | [runbooks/development.md#setup-and-build](../runbooks/development.md#setup-and-build) | incorporated/deduplicated; historical clauses explicitly qualified |
| Stale claims, illustrative assumptions, obsolete locations, and source-only navigation policy | [evidence/navigation-and-tooling-history.md#current-corrections-and-unresolved-discrepancies](../evidence/navigation-and-tooling-history.md#current-corrections-and-unresolved-discrepancies) | historical; no new app behavior inferred |

## app/src/main/codemap.md

SHA256: `f4011825ff674917ebc7827279875d3bf134edd60b0d7269db405289d190ed9c`

Coverage status: `incorporated`. Verification: `independently-verified` (2026-10-08, runner_navigation).

| Extracted source substance | Exact destination sections | Disposition |
| --- | --- | --- |
| Manifest declarations/startup injection, Kotlin/res/assets, icons/theme/string/backup resources, no flavor overlays | [concepts/runtime-wiring.md#entrypoints-and-production-structure](../concepts/runtime-wiring.md#entrypoints-and-production-structure) | incorporated/deduplicated; historical clauses explicitly qualified |
| Schema exports generated outside main source set | [runbooks/development.md#setup-and-build](../runbooks/development.md#setup-and-build) | incorporated/deduplicated; historical clauses explicitly qualified |
| Stale claims, illustrative assumptions, obsolete locations, and source-only navigation policy | [evidence/navigation-and-tooling-history.md#current-corrections-and-unresolved-discrepancies](../evidence/navigation-and-tooling-history.md#current-corrections-and-unresolved-discrepancies) | historical; no new app behavior inferred |

## app/src/main/java/codemap.md

SHA256: `f0131951e0513285adb64ab6e73f2d198cbb6c88a4691d5418fb26fbc8ef6540`

Coverage status: `incorporated`. Verification: `independently-verified` (2026-10-08, runner_navigation).

| Extracted source substance | Exact destination sections | Disposition |
| --- | --- | --- |
| Pure structural source/package roots, reverse-domain single namespace, compiler traversal/manifest resolution | [concepts/runtime-wiring.md#entrypoints-and-production-structure](../concepts/runtime-wiring.md#entrypoints-and-production-structure) | incorporated/deduplicated; historical clauses explicitly qualified |
| Convention versus compiler-enforcement claim and current test paths | [evidence/navigation-and-tooling-history.md#current-corrections-and-unresolved-discrepancies](../evidence/navigation-and-tooling-history.md#current-corrections-and-unresolved-discrepancies) | incorporated/deduplicated; historical clauses explicitly qualified |
| Stale claims, illustrative assumptions, obsolete locations, and source-only navigation policy | [evidence/navigation-and-tooling-history.md#current-corrections-and-unresolved-discrepancies](../evidence/navigation-and-tooling-history.md#current-corrections-and-unresolved-discrepancies) | historical; no new app behavior inferred |

## app/src/main/java/com/codemap.md

SHA256: `46b35800fc28d49ee144951be18512d684b85d544653ab8857c9f36d209156ef`

Coverage status: `incorporated`. Verification: `independently-verified` (2026-10-08, runner_navigation).

| Extracted source substance | Exact destination sections | Disposition |
| --- | --- | --- |
| Pure structural source/package roots, reverse-domain single namespace, compiler traversal/manifest resolution | [concepts/runtime-wiring.md#entrypoints-and-production-structure](../concepts/runtime-wiring.md#entrypoints-and-production-structure) | incorporated/deduplicated; historical clauses explicitly qualified |
| Convention versus compiler-enforcement claim and current test paths | [evidence/navigation-and-tooling-history.md#current-corrections-and-unresolved-discrepancies](../evidence/navigation-and-tooling-history.md#current-corrections-and-unresolved-discrepancies) | incorporated/deduplicated; historical clauses explicitly qualified |
| Stale claims, illustrative assumptions, obsolete locations, and source-only navigation policy | [evidence/navigation-and-tooling-history.md#current-corrections-and-unresolved-discrepancies](../evidence/navigation-and-tooling-history.md#current-corrections-and-unresolved-discrepancies) | historical; no new app behavior inferred |

## app/src/main/java/com/opus/codemap.md

SHA256: `4e6fc3b7362c066d42fef58763e0a48ec786d411f570eca279c71f2bdbeddc50`

Coverage status: `incorporated`. Verification: `independently-verified` (2026-10-08, runner_navigation).

| Extracted source substance | Exact destination sections | Disposition |
| --- | --- | --- |
| Pure structural source/package roots, reverse-domain single namespace, compiler traversal/manifest resolution | [concepts/runtime-wiring.md#entrypoints-and-production-structure](../concepts/runtime-wiring.md#entrypoints-and-production-structure) | incorporated/deduplicated; historical clauses explicitly qualified |
| Convention versus compiler-enforcement claim and current test paths | [evidence/navigation-and-tooling-history.md#current-corrections-and-unresolved-discrepancies](../evidence/navigation-and-tooling-history.md#current-corrections-and-unresolved-discrepancies) | incorporated/deduplicated; historical clauses explicitly qualified |
| Stale claims, illustrative assumptions, obsolete locations, and source-only navigation policy | [evidence/navigation-and-tooling-history.md#current-corrections-and-unresolved-discrepancies](../evidence/navigation-and-tooling-history.md#current-corrections-and-unresolved-discrepancies) | historical; no new app behavior inferred |

## app/src/main/java/com/opus/readerparser/codemap.md

SHA256: `fec6037d905c064e35206fb9d601cb054b64d38c16a4ff1814a2fab6a6d952f6`

Coverage status: `incorporated`. Verification: `independently-verified` (2026-10-08, runner_navigation).

| Extracted source substance | Exact destination sections | Disposition |
| --- | --- | --- |
| App/MainActivity and six subsystem ownership, Hilt/WorkManager/manifest bootstrap | [concepts/runtime-wiring.md#entrypoints-and-production-structure](../concepts/runtime-wiring.md#entrypoints-and-production-structure) | incorporated/deduplicated; historical clauses explicitly qualified |
| Pure domain/identity and layers, repository boundary, reactive action/state/effect flow | [architecture.md#layer-contracts](../architecture.md#layer-contracts) | incorporated/deduplicated; historical clauses explicitly qualified |
| Four-file screens, current unified Reader and route context, effects | [concepts/presentation.md#screen-contract](../concepts/presentation.md#screen-contract) | incorporated/deduplicated; historical clauses explicitly qualified |
| JVM/instrumented tests, fakes, migrations, previews | [runbooks/development.md#verification-gate-and-tests](../runbooks/development.md#verification-gate-and-tests) | incorporated/deduplicated; historical clauses explicitly qualified |
| Stale claims, illustrative assumptions, obsolete locations, and source-only navigation policy | [evidence/navigation-and-tooling-history.md#current-corrections-and-unresolved-discrepancies](../evidence/navigation-and-tooling-history.md#current-corrections-and-unresolved-discrepancies) | historical; no new app behavior inferred |

## app/src/main/java/com/opus/readerparser/core/codemap.md

SHA256: `c3b08e8b83e530b8f64695fed4d40cfa76cf4fe392202d7f7b12fbc922ec8663`

Coverage status: `incorporated`. Verification: `independently-verified` (2026-10-08, runner_navigation).

| Extracted source substance | Exact destination sections | Disposition |
| --- | --- | --- |
| DI only/no business policy/UI, Singleton graph, per-subsystem modules, Binds/Provides, qualifiers and fake roots | [concepts/runtime-wiring.md#hilt-graph](../concepts/runtime-wiring.md#hilt-graph) | incorporated/deduplicated; historical clauses explicitly qualified |
| Pure JVM helper ownership, reserved empty result historical intent | [concepts/runtime-wiring.md#pure-utility-algorithms](../concepts/runtime-wiring.md#pure-utility-algorithms) | incorporated/deduplicated; historical clauses explicitly qualified |
| Stale claims, illustrative assumptions, obsolete locations, and source-only navigation policy | [evidence/navigation-and-tooling-history.md#current-corrections-and-unresolved-discrepancies](../evidence/navigation-and-tooling-history.md#current-corrections-and-unresolved-discrepancies) | historical; no new app behavior inferred |

## app/src/main/java/com/opus/readerparser/core/di/codemap.md

SHA256: `2686cd17be4afe52f51e5d4e0c9a7273f1a7e47cfd73052c14b990593bab969f`

Coverage status: `incorporated`. Verification: `independently-verified` (2026-10-08, runner_navigation).

| Extracted source substance | Exact destination sections | Disposition |
| --- | --- | --- |
| DI only/no business policy/UI, Singleton graph, per-subsystem modules, Binds/Provides, qualifiers and fake roots | [concepts/runtime-wiring.md#hilt-graph](../concepts/runtime-wiring.md#hilt-graph) | incorporated/deduplicated; historical clauses explicitly qualified |
| Pure JVM helper ownership, reserved empty result historical intent | [concepts/runtime-wiring.md#pure-utility-algorithms](../concepts/runtime-wiring.md#pure-utility-algorithms) | incorporated/deduplicated; historical clauses explicitly qualified |
| Stale claims, illustrative assumptions, obsolete locations, and source-only navigation policy | [evidence/navigation-and-tooling-history.md#current-corrections-and-unresolved-discrepancies](../evidence/navigation-and-tooling-history.md#current-corrections-and-unresolved-discrepancies) | historical; no new app behavior inferred |

## app/src/main/java/com/opus/readerparser/core/util/codemap.md

SHA256: `44c7a021e86dd8a73ed63c94ba546309c45e95f4fdd693606458631f1704120c`

Coverage status: `incorporated`. Verification: `independently-verified` (2026-10-08, runner_navigation).

| Extracted source substance | Exact destination sections | Disposition |
| --- | --- | --- |
| Source identity formula, unsigned mask and stability | [concepts/domain-contracts.md#identity-and-nullable-data](../concepts/domain-contracts.md#identity-and-nullable-data) | incorporated/deduplicated; historical clauses explicitly qualified |
| SHA1 UTF8 lowercase 16-character paths and namespace collision assumption | [concepts/persistence.md#download-storage-and-integrity](../concepts/persistence.md#download-storage-and-integrity) | incorporated/deduplicated; historical clauses explicitly qualified |
| Injectable monotonic bounded cache, normalized substring/fuzzy sliding windows and edit-distance cutoff | [concepts/runtime-wiring.md#pure-utility-algorithms](../concepts/runtime-wiring.md#pure-utility-algorithms) | incorporated/deduplicated; historical clauses explicitly qualified |
| Stale claims, illustrative assumptions, obsolete locations, and source-only navigation policy | [evidence/navigation-and-tooling-history.md#current-corrections-and-unresolved-discrepancies](../evidence/navigation-and-tooling-history.md#current-corrections-and-unresolved-discrepancies) | historical; no new app behavior inferred |

## app/src/main/java/com/opus/readerparser/data/codemap.md

SHA256: `a275acb681f6500fc264891dc71e4cc0b04c4482547e82e1591b3ee6fda73af1`

Coverage status: `incorporated`. Verification: `independently-verified` (2026-10-08, runner_navigation).

| Extracted source substance | Exact destination sections | Disposition |
| --- | --- | --- |
| Repository-only local/remote orchestration, compile-time registry, identity, reactive/suspend network/write/read paths | [concepts/persistence.md#structured-state-and-repositories](../concepts/persistence.md#structured-state-and-repositories) | incorporated/deduplicated; historical clauses explicitly qualified |
| Downloaded content read before source network and no direct UI persistence | [concepts/persistence.md#download-storage-and-integrity](../concepts/persistence.md#download-storage-and-integrity) | incorporated/deduplicated; historical clauses explicitly qualified |
| Empty network-placeholder versus current DI network owner | [concepts/runtime-wiring.md#hilt-graph](../concepts/runtime-wiring.md#hilt-graph) | incorporated/deduplicated; historical clauses explicitly qualified |
| Stale claims, illustrative assumptions, obsolete locations, and source-only navigation policy | [evidence/navigation-and-tooling-history.md#current-corrections-and-unresolved-discrepancies](../evidence/navigation-and-tooling-history.md#current-corrections-and-unresolved-discrepancies) | historical; no new app behavior inferred |

## app/src/main/java/com/opus/readerparser/data/local/codemap.md

SHA256: `3283158a1084b5d23d8195aaa9edac653d7a4a0bfd6b9ca0482bc064eac89fa2`

Coverage status: `incorporated`. Verification: `independently-verified` (2026-10-08, runner_navigation).

| Extracted source substance | Exact destination sections | Disposition |
| --- | --- | --- |
| Room/filesystem/preferences/external-index responsibility, fakeable boundaries, dispatcher/atomicity/reactive versus pull behavior | [concepts/persistence.md#structured-state-and-repositories](../concepts/persistence.md#structured-state-and-repositories) | incorporated/deduplicated; historical clauses explicitly qualified |
| Flow entity/domain conversion and storage type/DAO/download/settings/search connections | [concepts/persistence.md#room-tables-and-queries](../concepts/persistence.md#room-tables-and-queries) | incorporated/deduplicated; historical clauses explicitly qualified |
| Stale claims, illustrative assumptions, obsolete locations, and source-only navigation policy | [evidence/navigation-and-tooling-history.md#current-corrections-and-unresolved-discrepancies](../evidence/navigation-and-tooling-history.md#current-corrections-and-unresolved-discrepancies) | historical; no new app behavior inferred |

## app/src/main/java/com/opus/readerparser/data/local/database/codemap.md

SHA256: `c5c06f04259294509ea647c899a04f618ea9ca2f26d1ff6871f2b70a99424b67`

Coverage status: `incorporated`. Verification: `independently-verified` (2026-10-08, runner_navigation).

| Extracted source substance | Exact destination sections | Disposition |
| --- | --- | --- |
| AppDatabase version1/export/three entities/DAOs/migrations; composite PKs/FK cascade/index/queue noFK | [concepts/persistence.md#room-tables-and-queries](../concepts/persistence.md#room-tables-and-queries) | incorporated/deduplicated; historical clauses explicitly qualified |
| Optional fields/genres JSON/enum-name strings/inLibrary guard; reactive versus suspend queries and SELECTIVE update/REPLACE caveat | [concepts/persistence.md#room-tables-and-queries](../concepts/persistence.md#room-tables-and-queries) | incorporated/deduplicated; historical clauses explicitly qualified |
| Library DESC/chapter ASC order, source lookup, queue LEFT JOIN DTO and state/error/progress updates | [concepts/persistence.md#room-tables-and-queries](../concepts/persistence.md#room-tables-and-queries) | incorporated/deduplicated; historical clauses explicitly qualified |
| Mapper/domain/repository connections and explicit schema/migration ownership | [concepts/persistence.md#conversion-and-chapter-refresh](../concepts/persistence.md#conversion-and-chapter-refresh) | incorporated/deduplicated; historical clauses explicitly qualified |
| Stale claims, illustrative assumptions, obsolete locations, and source-only navigation policy | [evidence/navigation-and-tooling-history.md#current-corrections-and-unresolved-discrepancies](../evidence/navigation-and-tooling-history.md#current-corrections-and-unresolved-discrepancies) | historical; no new app behavior inferred |

## app/src/main/java/com/opus/readerparser/data/local/database/dao/codemap.md

SHA256: `044d3ea46e694a24fb99b06efe66c33bcca4a6c9850e0d82a790241eb25e0e1f`

Coverage status: `incorporated`. Verification: `independently-verified` (2026-10-08, runner_navigation).

| Extracted source substance | Exact destination sections | Disposition |
| --- | --- | --- |
| AppDatabase version1/export/three entities/DAOs/migrations; composite PKs/FK cascade/index/queue noFK | [concepts/persistence.md#room-tables-and-queries](../concepts/persistence.md#room-tables-and-queries) | incorporated/deduplicated; historical clauses explicitly qualified |
| Optional fields/genres JSON/enum-name strings/inLibrary guard; reactive versus suspend queries and SELECTIVE update/REPLACE caveat | [concepts/persistence.md#room-tables-and-queries](../concepts/persistence.md#room-tables-and-queries) | incorporated/deduplicated; historical clauses explicitly qualified |
| Library DESC/chapter ASC order, source lookup, queue LEFT JOIN DTO and state/error/progress updates | [concepts/persistence.md#room-tables-and-queries](../concepts/persistence.md#room-tables-and-queries) | incorporated/deduplicated; historical clauses explicitly qualified |
| Mapper/domain/repository connections and explicit schema/migration ownership | [concepts/persistence.md#conversion-and-chapter-refresh](../concepts/persistence.md#conversion-and-chapter-refresh) | incorporated/deduplicated; historical clauses explicitly qualified |
| Stale claims, illustrative assumptions, obsolete locations, and source-only navigation policy | [evidence/navigation-and-tooling-history.md#current-corrections-and-unresolved-discrepancies](../evidence/navigation-and-tooling-history.md#current-corrections-and-unresolved-discrepancies) | historical; no new app behavior inferred |

## app/src/main/java/com/opus/readerparser/data/local/database/entities/codemap.md

SHA256: `8cd014b80b081d167c37bac8b1127c1bc8189f68aa044b95f2bf13f7ffcbfcee`

Coverage status: `incorporated`. Verification: `independently-verified` (2026-10-08, runner_navigation).

| Extracted source substance | Exact destination sections | Disposition |
| --- | --- | --- |
| AppDatabase version1/export/three entities/DAOs/migrations; composite PKs/FK cascade/index/queue noFK | [concepts/persistence.md#room-tables-and-queries](../concepts/persistence.md#room-tables-and-queries) | incorporated/deduplicated; historical clauses explicitly qualified |
| Optional fields/genres JSON/enum-name strings/inLibrary guard; reactive versus suspend queries and SELECTIVE update/REPLACE caveat | [concepts/persistence.md#room-tables-and-queries](../concepts/persistence.md#room-tables-and-queries) | incorporated/deduplicated; historical clauses explicitly qualified |
| Library DESC/chapter ASC order, source lookup, queue LEFT JOIN DTO and state/error/progress updates | [concepts/persistence.md#room-tables-and-queries](../concepts/persistence.md#room-tables-and-queries) | incorporated/deduplicated; historical clauses explicitly qualified |
| Mapper/domain/repository connections and explicit schema/migration ownership | [concepts/persistence.md#conversion-and-chapter-refresh](../concepts/persistence.md#conversion-and-chapter-refresh) | incorporated/deduplicated; historical clauses explicitly qualified |
| Stale claims, illustrative assumptions, obsolete locations, and source-only navigation policy | [evidence/navigation-and-tooling-history.md#current-corrections-and-unresolved-discrepancies](../evidence/navigation-and-tooling-history.md#current-corrections-and-unresolved-discrepancies) | historical; no new app behavior inferred |

## app/src/main/java/com/opus/readerparser/data/local/database/mappers/codemap.md

SHA256: `297d9309001a17b0a2d7c03d4b919b88d695c28ea15bc01d4eb04f0ab973a1a6`

Coverage status: `incorporated`. Verification: `independently-verified` (2026-10-08, runner_navigation).

| Extracted source substance | Exact destination sections | Disposition |
| --- | --- | --- |
| Top-level Series/Chapter conversions, loss of storage state and ChapterWithState reconstruction, default new chapter values | [concepts/persistence.md#conversion-and-chapter-refresh](../concepts/persistence.md#conversion-and-chapter-refresh) | incorporated/deduplicated; historical clauses explicitly qualified |
| Lenient Series enum decoding and explicit genre encode/decode/blank fallback | [concepts/persistence.md#conversion-and-chapter-refresh](../concepts/persistence.md#conversion-and-chapter-refresh) | incorporated/deduplicated; historical clauses explicitly qualified |
| Stale claims, illustrative assumptions, obsolete locations, and source-only navigation policy | [evidence/navigation-and-tooling-history.md#current-corrections-and-unresolved-discrepancies](../evidence/navigation-and-tooling-history.md#current-corrections-and-unresolved-discrepancies) | historical; no new app behavior inferred |

## app/src/main/java/com/opus/readerparser/data/local/filesystem/codemap.md

SHA256: `d6274253708dc57a65727649ea337ad808c00ab8b7c87ada5439933e99ba94aa`

Coverage status: `incorporated`. Verification: `independently-verified` (2026-10-08, runner_navigation).

| Extracted source substance | Exact destination sections | Disposition |
| --- | --- | --- |
| DownloadStore fakeable interface, injected root, domain shapes, IO dispatcher and caller byte-fetch lambda | [concepts/persistence.md#download-storage-and-integrity](../concepts/persistence.md#download-storage-and-integrity) | incorporated/deduplicated; historical clauses explicitly qualified |
| Source/series/chapter hashes, metadata fields, HTML/numbered jpg files, metadata-last integrity, read miss/fileURI/delete behavior | [concepts/persistence.md#download-storage-and-integrity](../concepts/persistence.md#download-storage-and-integrity) | incorporated/deduplicated; historical clauses explicitly qualified |
| Stale claims, illustrative assumptions, obsolete locations, and source-only navigation policy | [evidence/navigation-and-tooling-history.md#current-corrections-and-unresolved-discrepancies](../evidence/navigation-and-tooling-history.md#current-corrections-and-unresolved-discrepancies) | historical; no new app behavior inferred |

## app/src/main/java/com/opus/readerparser/data/local/prefs/codemap.md

SHA256: `15f9052cad5fb8da81828544979f3b3b45a3168fbf269c447a3cd9f3b1230295`

Coverage status: `incorporated`. Verification: `independently-verified` (2026-10-08, runner_navigation).

| Extracted source substance | Exact destination sections | Disposition |
| --- | --- | --- |
| Named DataStore extension, injectable SettingsStore/repository indirection, Flow/atomic one-setting edits | [concepts/persistence.md#preferences-and-atomic-edits](../concepts/persistence.md#preferences-and-atomic-edits) | incorporated/deduplicated; historical clauses explicitly qualified |
| Five typed preference keys/defaults/name persistence and actual invalid-enum limitation | [concepts/persistence.md#preferences-and-atomic-edits](../concepts/persistence.md#preferences-and-atomic-edits) | incorporated/deduplicated; historical clauses explicitly qualified |
| Stale claims, illustrative assumptions, obsolete locations, and source-only navigation policy | [evidence/navigation-and-tooling-history.md#current-corrections-and-unresolved-discrepancies](../evidence/navigation-and-tooling-history.md#current-corrections-and-unresolved-discrepancies) | historical; no new app behavior inferred |

## app/src/main/java/com/opus/readerparser/data/local/search/codemap.md

SHA256: `cad9beca2a8f57db056f336f6c570bb38439f1a72b381ff03f65dd5f7f634069`

Coverage status: `incorporated`. Verification: `independently-verified` (2026-10-08, runner_navigation).

| Extracted source substance | Exact destination sections | Disposition |
| --- | --- | --- |
| Syncer observe/rebuild, availability-clear-batched insert, delegate/test/schema asset abstractions | [concepts/persistence.md#samsung-index-infrastructure](../concepts/persistence.md#samsung-index-infrastructure) | incorporated/deduplicated; historical clauses explicitly qualified |
| ContentValues ID/title/author/description/genres/status/type/sourceURL and encoded Series deep link mapping | [concepts/persistence.md#samsung-index-infrastructure](../concepts/persistence.md#samsung-index-infrastructure) | incorporated/deduplicated; historical clauses explicitly qualified |
| Stale claims, illustrative assumptions, obsolete locations, and source-only navigation policy | [evidence/navigation-and-tooling-history.md#current-corrections-and-unresolved-discrepancies](../evidence/navigation-and-tooling-history.md#current-corrections-and-unresolved-discrepancies) | historical; no new app behavior inferred |

## app/src/main/java/com/opus/readerparser/data/repository/codemap.md

SHA256: `f18aa9090fa22240e16ac5125cceb5397f01b48aaf1d83d77e2e78b883f59f6a`

Coverage status: `incorporated`. Verification: `independently-verified` (2026-10-08, runner_navigation).

| Extracted source substance | Exact destination sections | Disposition |
| --- | --- | --- |
| Repository implementations/DI/singletons, catalog caches/persist, update-then-insert preserving Library fields | [concepts/persistence.md#structured-state-and-repositories](../concepts/persistence.md#structured-state-and-repositories) | incorporated/deduplicated; historical clauses explicitly qualified |
| Fuzzy page-one local fallback, source mapper, queue join and Library/indexability combined invalidations | [concepts/persistence.md#structured-state-and-repositories](../concepts/persistence.md#structured-state-and-repositories) | incorporated/deduplicated; historical clauses explicitly qualified |
| Chapter refresh historical REPLACE description and current merge/default/delete-stale conversion | [concepts/persistence.md#conversion-and-chapter-refresh](../concepts/persistence.md#conversion-and-chapter-refresh) | incorporated/deduplicated; historical clauses explicitly qualified |
| Stale claims, illustrative assumptions, obsolete locations, and source-only navigation policy | [evidence/navigation-and-tooling-history.md#current-corrections-and-unresolved-discrepancies](../evidence/navigation-and-tooling-history.md#current-corrections-and-unresolved-discrepancies) | historical; no new app behavior inferred |

## app/src/main/java/com/opus/readerparser/data/source/codemap.md

SHA256: `53e6083f7bba341ce84538f60dc8424bf3693996906178a0b38f49752edaad23`

Coverage status: `incorporated`. Verification: `independently-verified` (2026-10-08, runner_navigation).

| Extracted source substance | Exact destination sections | Disposition |
| --- | --- | --- |
| Stable Source interface, HtmlSource default/template/override matrix, six retrieval methods and type dispatch | [concepts/source-plugins.md#contract-and-template-methods](../concepts/source-plugins.md#contract-and-template-methods) | incorporated/deduplicated; historical clauses explicitly qualified |
| Per-site isolation, lowercase class/file registration/static map/deterministic identity | [concepts/source-plugins.md#identity-and-registration](../concepts/source-plugins.md#identity-and-registration) | incorporated/deduplicated; historical clauses explicitly qualified |
| Exception policy, optional null-safe extraction and source-specific recovery limits | [concepts/source-plugins.md#errors-and-extraction-rules](../concepts/source-plugins.md#errors-and-extraction-rules) | incorporated/deduplicated; historical clauses explicitly qualified |
| Saved fixture MockEngine tests and endpoint edge cases | [concepts/source-plugins.md#new-source-workflow-and-tests](../concepts/source-plugins.md#new-source-workflow-and-tests) | incorporated/deduplicated; historical clauses explicitly qualified |
| Stale claims, illustrative assumptions, obsolete locations, and source-only navigation policy | [evidence/navigation-and-tooling-history.md#current-corrections-and-unresolved-discrepancies](../evidence/navigation-and-tooling-history.md#current-corrections-and-unresolved-discrepancies) | historical; no new app behavior inferred |

## app/src/main/java/com/opus/readerparser/domain/codemap.md

SHA256: `11ba607d9e0400610ce5756a7d057ed6c3980977c00bb9d9797d5f97be9cdbdd`

Coverage status: `incorporated`. Verification: `independently-verified` (2026-10-08, runner_navigation).

| Extracted source substance | Exact destination sections | Disposition |
| --- | --- | --- |
| Platform-free immutable models, identity/nullable data, copy/read-only collections | [concepts/domain-contracts.md#identity-and-nullable-data](../concepts/domain-contracts.md#identity-and-nullable-data) | incorporated/deduplicated; historical clauses explicitly qualified |
| Closed Filter/ChapterContent shapes, enums, SeriesPage/ChapterWithState/SourceInfo/DownloadItem/AppSettings aggregates | [concepts/domain-contracts.md#closed-models-and-aggregates](../concepts/domain-contracts.md#closed-models-and-aggregates) | incorporated/deduplicated; historical clauses explicitly qualified |
| Repository ownership/APIs, static SourceRepository, separate injected concerns/no usecases needed | [concepts/domain-contracts.md#repository-apis](../concepts/domain-contracts.md#repository-apis) | incorporated/deduplicated; historical clauses explicitly qualified |
| Flow/suspend reads/writes, 5-second subscription pattern, failure handling and entity/model/UiState transitions | [concepts/domain-contracts.md#reactive-reads-and-failure-boundary](../concepts/domain-contracts.md#reactive-reads-and-failure-boundary) | incorporated/deduplicated; historical clauses explicitly qualified |
| Stale claims, illustrative assumptions, obsolete locations, and source-only navigation policy | [evidence/navigation-and-tooling-history.md#current-corrections-and-unresolved-discrepancies](../evidence/navigation-and-tooling-history.md#current-corrections-and-unresolved-discrepancies) | historical; no new app behavior inferred |

## app/src/main/java/com/opus/readerparser/domain/model/codemap.md

SHA256: `75552665dc4dfe619aa7b5bda64ca82bfc25d13a57766146d9f005aa64938a95`

Coverage status: `incorporated`. Verification: `independently-verified` (2026-10-08, runner_navigation).

| Extracted source substance | Exact destination sections | Disposition |
| --- | --- | --- |
| Platform-free immutable models, identity/nullable data, copy/read-only collections | [concepts/domain-contracts.md#identity-and-nullable-data](../concepts/domain-contracts.md#identity-and-nullable-data) | incorporated/deduplicated; historical clauses explicitly qualified |
| Closed Filter/ChapterContent shapes, enums, SeriesPage/ChapterWithState/SourceInfo/DownloadItem/AppSettings aggregates | [concepts/domain-contracts.md#closed-models-and-aggregates](../concepts/domain-contracts.md#closed-models-and-aggregates) | incorporated/deduplicated; historical clauses explicitly qualified |
| Repository ownership/APIs, static SourceRepository, separate injected concerns/no usecases needed | [concepts/domain-contracts.md#repository-apis](../concepts/domain-contracts.md#repository-apis) | incorporated/deduplicated; historical clauses explicitly qualified |
| Flow/suspend reads/writes, 5-second subscription pattern, failure handling and entity/model/UiState transitions | [concepts/domain-contracts.md#reactive-reads-and-failure-boundary](../concepts/domain-contracts.md#reactive-reads-and-failure-boundary) | incorporated/deduplicated; historical clauses explicitly qualified |
| Stale claims, illustrative assumptions, obsolete locations, and source-only navigation policy | [evidence/navigation-and-tooling-history.md#current-corrections-and-unresolved-discrepancies](../evidence/navigation-and-tooling-history.md#current-corrections-and-unresolved-discrepancies) | historical; no new app behavior inferred |

## app/src/main/java/com/opus/readerparser/sources/asurascans/codemap.md

SHA256: `929403ecf746c7f3ae3fe988fea55e1ccfc1971379b66a5dfbbbed6d57a44d83`

Coverage status: `incorporated`. Verification: `independently-verified` (2026-10-08, runner_navigation).

| Extracted source substance | Exact destination sections | Disposition |
| --- | --- | --- |
| Plugin identity/type/registration/browser headers, popular/search URLs/card/next-page/latest selectors | [concepts/source-plugins.md#asura-listings-and-details](../concepts/source-plugins.md#asura-listings-and-details) | incorporated/deduplicated; historical clauses explicitly qualified |
| Details author/artist/description/genres/status/cover extraction | [concepts/source-plugins.md#asura-listings-and-details](../concepts/source-plugins.md#asura-listings-and-details) | incorporated/deduplicated; historical clauses explicitly qualified |
| Chapter anchors/number/date parsers and historical static-only versus API/HTML current flow | [concepts/source-plugins.md#asura-chapters-and-dates](../concepts/source-plugins.md#asura-chapters-and-dates) | incorporated/deduplicated; historical clauses explicitly qualified |
| Endpoint fixtures/MockEngine/optional fields/URLs/status/number/date edge cases | [concepts/source-plugins.md#new-source-workflow-and-tests](../concepts/source-plugins.md#new-source-workflow-and-tests) | incorporated/deduplicated; historical clauses explicitly qualified |
| Stale claims, illustrative assumptions, obsolete locations, and source-only navigation policy | [evidence/navigation-and-tooling-history.md#current-corrections-and-unresolved-discrepancies](../evidence/navigation-and-tooling-history.md#current-corrections-and-unresolved-discrepancies) | historical; no new app behavior inferred |

## app/src/main/java/com/opus/readerparser/sources/codemap.md

SHA256: `faf575c4a693506df2fce58f3bb14d58b50cc19ffe6abff5c6a3df01baf80c60`

Coverage status: `incorporated`. Verification: `independently-verified` (2026-10-08, runner_navigation).

| Extracted source substance | Exact destination sections | Disposition |
| --- | --- | --- |
| Stable Source interface, HtmlSource default/template/override matrix, six retrieval methods and type dispatch | [concepts/source-plugins.md#contract-and-template-methods](../concepts/source-plugins.md#contract-and-template-methods) | incorporated/deduplicated; historical clauses explicitly qualified |
| Per-site isolation, lowercase class/file registration/static map/deterministic identity | [concepts/source-plugins.md#identity-and-registration](../concepts/source-plugins.md#identity-and-registration) | incorporated/deduplicated; historical clauses explicitly qualified |
| Exception policy, optional null-safe extraction and source-specific recovery limits | [concepts/source-plugins.md#errors-and-extraction-rules](../concepts/source-plugins.md#errors-and-extraction-rules) | incorporated/deduplicated; historical clauses explicitly qualified |
| Saved fixture MockEngine tests and endpoint edge cases | [concepts/source-plugins.md#new-source-workflow-and-tests](../concepts/source-plugins.md#new-source-workflow-and-tests) | incorporated/deduplicated; historical clauses explicitly qualified |
| Stale claims, illustrative assumptions, obsolete locations, and source-only navigation policy | [evidence/navigation-and-tooling-history.md#current-corrections-and-unresolved-discrepancies](../evidence/navigation-and-tooling-history.md#current-corrections-and-unresolved-discrepancies) | historical; no new app behavior inferred |

## app/src/main/java/com/opus/readerparser/ui/browse/codemap.md

SHA256: `21780d9b6593a3b31a9f5c542a6e8263164e27bcc6aaa92427bcccfc4e4a90ab`

Coverage status: `incorporated`. Verification: `independently-verified` (2026-10-08, runner_navigation).

| Extracted source substance | Exact destination sections | Disposition |
| --- | --- | --- |
| Screen files/repository inputs/state/actions/effects, loading/error/empty/populated, settings/detail callbacks | [concepts/presentation.md#library-and-browse-state](../concepts/presentation.md#library-and-browse-state) | incorporated/deduplicated; historical clauses explicitly qualified |
| Library local sorts/reactive invalidations/Samsung nonblank search, cards/longpress/always-visible field; removed Unread intent | [concepts/presentation.md#library-and-browse-state](../concepts/presentation.md#library-and-browse-state) | incorporated/deduplicated; historical clauses explicitly qualified |
| Browse source/mode/reset/append/pagination/initial fetch and current live Search versus historical submission | [specs/browse-discovery.md#live-source-specific-search](../specs/browse-discovery.md#live-source-specific-search) | incorporated/deduplicated; historical clauses explicitly qualified |
| Stale claims, illustrative assumptions, obsolete locations, and source-only navigation policy | [evidence/navigation-and-tooling-history.md#current-corrections-and-unresolved-discrepancies](../evidence/navigation-and-tooling-history.md#current-corrections-and-unresolved-discrepancies) | historical; no new app behavior inferred |

## app/src/main/java/com/opus/readerparser/ui/codemap.md

SHA256: `78e13459039cc3ea20eeabb9c5c1d745a3b4be14a85684a44d3ac31ccc2da5d3`

Coverage status: `incorporated`. Verification: `independently-verified` (2026-10-08, runner_navigation).

| Extracted source substance | Exact destination sections | Disposition |
| --- | --- | --- |
| Four-file/state/action/effect contract, buffered channels/lifecycle collection, stateless previews/Material3/Coil/theme/shared stable-key components | [concepts/presentation.md#screen-contract](../concepts/presentation.md#screen-contract) | incorporated/deduplicated; historical clauses explicitly qualified |
| Six screen responsibilities and current unified contentType Reader dispatch | [concepts/presentation.md#series-and-reader-state](../concepts/presentation.md#series-and-reader-state) | incorporated/deduplicated; historical clauses explicitly qualified |
| Typed callbacks/bottom-navigation/scaffold/theme ownership | [concepts/presentation.md#navigation-and-deep-links](../concepts/presentation.md#navigation-and-deep-links) | incorporated/deduplicated; historical clauses explicitly qualified |
| Stale claims, illustrative assumptions, obsolete locations, and source-only navigation policy | [evidence/navigation-and-tooling-history.md#current-corrections-and-unresolved-discrepancies](../evidence/navigation-and-tooling-history.md#current-corrections-and-unresolved-discrepancies) | historical; no new app behavior inferred |

## app/src/main/java/com/opus/readerparser/ui/downloads/codemap.md

SHA256: `a695e8722722fe14c9fd1c1fc95ed5ddaf17a4c8844373fef0917ef269294a91`

Coverage status: `incorporated`. Verification: `independently-verified` (2026-10-08, runner_navigation).

| Extracted source substance | Exact destination sections | Disposition |
| --- | --- | --- |
| Four files, state/actions/effects and continuous queue observation without polling/refresh | [concepts/presentation.md#downloads-and-settings-state](../concepts/presentation.md#downloads-and-settings-state) | incorporated/deduplicated; historical clauses explicitly qualified |
| Rows/state chips/running progress/cancel-retry, show-error-host historical TODO and test tags/states | [concepts/presentation.md#downloads-and-settings-state](../concepts/presentation.md#downloads-and-settings-state) | incorporated/deduplicated; historical clauses explicitly qualified |
| Stale claims, illustrative assumptions, obsolete locations, and source-only navigation policy | [evidence/navigation-and-tooling-history.md#current-corrections-and-unresolved-discrepancies](../evidence/navigation-and-tooling-history.md#current-corrections-and-unresolved-discrepancies) | historical; no new app behavior inferred |

## app/src/main/java/com/opus/readerparser/ui/library/codemap.md

SHA256: `0d85f0207c80cd6dc26ef769e46a11b48aaac18ac31835b1a0cd1299822b9b0f`

Coverage status: `incorporated`. Verification: `independently-verified` (2026-10-08, runner_navigation).

| Extracted source substance | Exact destination sections | Disposition |
| --- | --- | --- |
| Screen files/repository inputs/state/actions/effects, loading/error/empty/populated, settings/detail callbacks | [concepts/presentation.md#library-and-browse-state](../concepts/presentation.md#library-and-browse-state) | incorporated/deduplicated; historical clauses explicitly qualified |
| Library local sorts/reactive invalidations/Samsung nonblank search, cards/longpress/always-visible field; removed Unread intent | [concepts/presentation.md#library-and-browse-state](../concepts/presentation.md#library-and-browse-state) | incorporated/deduplicated; historical clauses explicitly qualified |
| Browse source/mode/reset/append/pagination/initial fetch and current live Search versus historical submission | [specs/browse-discovery.md#live-source-specific-search](../specs/browse-discovery.md#live-source-specific-search) | incorporated/deduplicated; historical clauses explicitly qualified |
| Stale claims, illustrative assumptions, obsolete locations, and source-only navigation policy | [evidence/navigation-and-tooling-history.md#current-corrections-and-unresolved-discrepancies](../evidence/navigation-and-tooling-history.md#current-corrections-and-unresolved-discrepancies) | historical; no new app behavior inferred |

## app/src/main/java/com/opus/readerparser/ui/navigation/codemap.md

SHA256: `17788928019e2da8766da2587bd6b6394fd71743c8bafc57bfd304a803383c42`

Coverage status: `incorporated`. Verification: `independently-verified` (2026-10-08, runner_navigation).

| Extracted source substance | Exact destination sections | Disposition |
| --- | --- | --- |
| Six route templates/builders/encoded URL args/types, contentType Reader route and back callbacks | [concepts/presentation.md#navigation-and-deep-links](../concepts/presentation.md#navigation-and-deep-links) | incorporated/deduplicated; historical clauses explicitly qualified |
| Single controller/NavHost/start Library/bottom tabs, hide conditions, state-restoration/singleTop/popUpTo Reader replacement | [concepts/presentation.md#navigation-and-deep-links](../concepts/presentation.md#navigation-and-deep-links) | incorporated/deduplicated; historical clauses explicitly qualified |
| Stale claims, illustrative assumptions, obsolete locations, and source-only navigation policy | [evidence/navigation-and-tooling-history.md#current-corrections-and-unresolved-discrepancies](../evidence/navigation-and-tooling-history.md#current-corrections-and-unresolved-discrepancies) | historical; no new app behavior inferred |

## app/src/main/java/com/opus/readerparser/ui/reader/codemap.md

SHA256: `9f96ffaf2edadbfde94e192b09e7f27685e1f60280ee38cc7a1982d9c966d682`

Coverage status: `incorporated`. Verification: `independently-verified` (2026-10-08, runner_navigation).

| Extracted source substance | Exact destination sections | Disposition |
| --- | --- | --- |
| Unified four-file route/state/effects/renderer selection/shared controls and DownloadEnqueuer/sheet integration | [concepts/presentation.md#series-and-reader-state](../concepts/presentation.md#series-and-reader-state) | incorporated/deduplicated; historical clauses explicitly qualified |
| Text persisted normalized progress versus zero-based page state, mismatch forced fetch/retryable error and theme CSS | [specs/reader.md#renderers-and-payload-mismatch](../specs/reader.md#renderers-and-payload-mismatch) | incorporated/deduplicated; historical clauses explicitly qualified |
| Stale claims, illustrative assumptions, obsolete locations, and source-only navigation policy | [evidence/navigation-and-tooling-history.md#current-corrections-and-unresolved-discrepancies](../evidence/navigation-and-tooling-history.md#current-corrections-and-unresolved-discrepancies) | historical; no new app behavior inferred |

## app/src/main/java/com/opus/readerparser/ui/series/codemap.md

SHA256: `10106f63ab6e43f62b847baa9e236b0678acc4b2d6b06b75aafe62c97c003b84`

Coverage status: `incorporated`. Verification: `independently-verified` (2026-10-08, runner_navigation).

| Extracted source substance | Exact destination sections | Disposition |
| --- | --- | --- |
| Saved route/stub-before-detail subscription, refresh/membership/optimistic toggle, newest-first reactive chapter order | [concepts/presentation.md#series-and-reader-state](../concepts/presentation.md#series-and-reader-state) | incorporated/deduplicated; historical clauses explicitly qualified |
| Header expandable detail/chapter read/downloaded indicators, effects, current unified navigation versus old two callbacks | [concepts/presentation.md#series-and-reader-state](../concepts/presentation.md#series-and-reader-state) | incorporated/deduplicated; historical clauses explicitly qualified |
| Stale claims, illustrative assumptions, obsolete locations, and source-only navigation policy | [evidence/navigation-and-tooling-history.md#current-corrections-and-unresolved-discrepancies](../evidence/navigation-and-tooling-history.md#current-corrections-and-unresolved-discrepancies) | historical; no new app behavior inferred |

## app/src/main/java/com/opus/readerparser/ui/settings/codemap.md

SHA256: `c8426ad2afa9160e61baedf1a78751fdbe64e4e38773ee0e0f1115b171bdd5f8`

Coverage status: `incorporated`. Verification: `independently-verified` (2026-10-08, runner_navigation).

| Extracted source substance | Exact destination sections | Disposition |
| --- | --- | --- |
| Reactive preference confirmation/no optimistic state/no effects, five action-to-setter mappings | [concepts/presentation.md#downloads-and-settings-state](../concepts/presentation.md#downloads-and-settings-state) | incorporated/deduplicated; historical clauses explicitly qualified |
| Theme/layout/zoom radios/font12-24 one-sp slider/family dropdown, private section/radio controls, test tags | [concepts/presentation.md#downloads-and-settings-state](../concepts/presentation.md#downloads-and-settings-state) | incorporated/deduplicated; historical clauses explicitly qualified |
| Current theme observation limitations and persisted versus implemented Reader options | [concepts/presentation.md#theme-ownership-and-limits](../concepts/presentation.md#theme-ownership-and-limits) | incorporated/deduplicated; historical clauses explicitly qualified |
| Stale claims, illustrative assumptions, obsolete locations, and source-only navigation policy | [evidence/navigation-and-tooling-history.md#current-corrections-and-unresolved-discrepancies](../evidence/navigation-and-tooling-history.md#current-corrections-and-unresolved-discrepancies) | historical; no new app behavior inferred |

## app/src/main/java/com/opus/readerparser/ui/theme/codemap.md

SHA256: `22b1235c32f963c88f55ebdc8dfc6905ecbbcba8f850f6630bc16e2d0f7d6e7e`

Coverage status: `incorporated`. Verification: `independently-verified` (2026-10-08, runner_navigation).

| Extracted source substance | Exact destination sections | Disposition |
| --- | --- | --- |
| Color/Type/Theme split, forty paired colors, appTheme SYSTEM/LIGHT/DARK mapping and default previews | [concepts/presentation.md#theme-ownership-and-limits](../concepts/presentation.md#theme-ownership-and-limits) | incorporated/deduplicated; historical clauses explicitly qualified |
| Novel body Serif16sp26sp default and runtime overrides, navigation independence, unverified activity settings wiring | [concepts/presentation.md#theme-ownership-and-limits](../concepts/presentation.md#theme-ownership-and-limits) | incorporated/deduplicated; historical clauses explicitly qualified |
| Stale claims, illustrative assumptions, obsolete locations, and source-only navigation policy | [evidence/navigation-and-tooling-history.md#current-corrections-and-unresolved-discrepancies](../evidence/navigation-and-tooling-history.md#current-corrections-and-unresolved-discrepancies) | historical; no new app behavior inferred |

## app/src/main/java/com/opus/readerparser/workers/codemap.md

SHA256: `af49719c950bad203476d885aea86ff993994a297f5968b5fdcca57d6adcd8ea`

Coverage status: `incorporated`. Verification: `independently-verified` (2026-10-08, runner_navigation).

| Extracted source substance | Exact destination sections | Disposition |
| --- | --- | --- |
| Chapter worker inputs/factory/connected constraint/tag, force-network/store/state/progress/backoff/logging/failure | [concepts/runtime-wiring.md#chapter-download-execution](../concepts/runtime-wiring.md#chapter-download-execution) | incorporated/deduplicated; historical clauses explicitly qualified |
| Library six-hour factory/read/refresh/retry and Samsung worker/receiver recovery/action permission | [concepts/runtime-wiring.md#library-and-samsung-workers](../concepts/runtime-wiring.md#library-and-samsung-workers) | incorporated/deduplicated; historical clauses explicitly qualified |
| Historical TestListenableWorkerBuilder/fake repositories/store, success/source-not-found/network-retry/final-failure, and WorkManagerTestInitHelper instrumented lifecycle | [runbooks/development.md#verification-gate-and-tests](../runbooks/development.md#verification-gate-and-tests) | incorporated/deduplicated; historical clauses explicitly qualified |
| Stale claims, illustrative assumptions, obsolete locations, and source-only navigation policy | [evidence/navigation-and-tooling-history.md#current-corrections-and-unresolved-discrepancies](../evidence/navigation-and-tooling-history.md#current-corrections-and-unresolved-discrepancies) | historical; no new app behavior inferred |

## codemap.md

SHA256: `dbd2b797aafed1aca8e4ce5cc522b13aced691c2d3c56404c2bb71a7cda171a9`

Coverage status: `incorporated`. Verification: `independently-verified` (2026-10-08, runner_navigation).

| Extracted source substance | Exact destination sections | Disposition |
| --- | --- | --- |
| Root assets/app/build/catalog/tooling responsibilities and package/subsystem entrypoints | [concepts/runtime-wiring.md#entrypoints-and-production-structure](../concepts/runtime-wiring.md#entrypoints-and-production-structure) | incorporated/deduplicated; historical clauses explicitly qualified |
| Layered snapshot, single module, identity, persistence, unified Reader, action/flow/effect pipeline | [architecture.md#layer-contracts](../architecture.md#layer-contracts) | incorporated/deduplicated; historical clauses explicitly qualified |
| Directory responsibilities and canonical subject navigation | [index.md#canonical-entrypoints](../index.md#canonical-entrypoints) | incorporated/deduplicated; historical clauses explicitly qualified |
| App/DI/worker bootstrap and manifest startup ownership | [concepts/runtime-wiring.md#hilt-graph](../concepts/runtime-wiring.md#hilt-graph) | incorporated/deduplicated; historical clauses explicitly qualified |
| Stale claims, illustrative assumptions, obsolete locations, and source-only navigation policy | [evidence/navigation-and-tooling-history.md#current-corrections-and-unresolved-discrepancies](../evidence/navigation-and-tooling-history.md#current-corrections-and-unresolved-discrepancies) | historical; no new app behavior inferred |

## gradle/codemap.md

SHA256: `939716d8171adf0f18f931255e0318e3a3d410ed3ef83ffd7a1f5dd068bc0744`

Coverage status: `incorporated`. Verification: `independently-verified` (2026-10-08, runner_navigation).

| Extracted source substance | Exact destination sections | Disposition |
| --- | --- | --- |
| Catalog sections/aliases/BOM/shared Ktor/KSP/test coordinates/Hilt integration and add-dependency procedure | [runbooks/development.md#setup-and-build](../runbooks/development.md#setup-and-build) | incorporated/deduplicated; historical clauses explicitly qualified |
| Wrapper/bootstrap/reproducible build, single module/shared-catalog future split, CI catalog dependence | [runbooks/development.md#setup-and-build](../runbooks/development.md#setup-and-build) | incorporated/deduplicated; historical clauses explicitly qualified |
| Historical missing daemon JDK21 foojay-generated intent versus current Java17 build | [evidence/navigation-and-tooling-history.md#current-corrections-and-unresolved-discrepancies](../evidence/navigation-and-tooling-history.md#current-corrections-and-unresolved-discrepancies) | incorporated/deduplicated; historical clauses explicitly qualified |
| Stale claims, illustrative assumptions, obsolete locations, and source-only navigation policy | [evidence/navigation-and-tooling-history.md#current-corrections-and-unresolved-discrepancies](../evidence/navigation-and-tooling-history.md#current-corrections-and-unresolved-discrepancies) | historical; no new app behavior inferred |

## scripts/codemap.md

SHA256: `8b2cbc1804668b0dc4ed2c832d8cc907b48d5a711f8adedf947cba2e2f906011`

Coverage status: `incorporated`. Verification: `independently-verified` (2026-10-08, runner_navigation).

| Extracted source substance | Exact destination sections | Disposition |
| --- | --- | --- |
| CI lint/JVM/optional-device/assemble/journey order, pre-push env/check/abort and optional-prerequisite skips | [runbooks/development.md#developer-scripts-and-journeys](../runbooks/development.md#developer-scripts-and-journeys) | incorporated/deduplicated; historical clauses explicitly qualified |
| Emulator JSON config/defaults/idempotent create/start/stop/list/delete and script dependencies | [runbooks/development.md#developer-scripts-and-journeys](../runbooks/development.md#developer-scripts-and-journeys) | incorporated/deduplicated; historical clauses explicitly qualified |
| Journey list/print/provision rather than action execution, Android agent/results/teardown | [runbooks/development.md#developer-scripts-and-journeys](../runbooks/development.md#developer-scripts-and-journeys) | incorporated/deduplicated; historical clauses explicitly qualified |
| WSL JDK17/CLI/SDK install/shell environment bootstrap and workflow-specific script testing | [runbooks/development.md#developer-scripts-and-journeys](../runbooks/development.md#developer-scripts-and-journeys) | incorporated/deduplicated; historical clauses explicitly qualified |
| Stale claims, illustrative assumptions, obsolete locations, and source-only navigation policy | [evidence/navigation-and-tooling-history.md#current-corrections-and-unresolved-discrepancies](../evidence/navigation-and-tooling-history.md#current-corrections-and-unresolved-discrepancies) | historical; no new app behavior inferred |

## architecture.md

SHA256: `9dde7a417c1b1c7dbbf041e419fd2c98e72c52d4a87827af7dbd050416e14a4c`

Coverage status: `incorporated`. Verification: `independently-verified` (2026-10-08, runner_navigation).

| Extracted source substance | Exact destination sections | Disposition |
| --- | --- | --- |
| Purpose/goals, principles and dependencies | [architecture.md#purpose-and-scope](../architecture.md#purpose-and-scope)<br>[architecture.md#layer-contracts](../architecture.md#layer-contracts) | incorporated |
| Contract shapes and identity/private-download/no-runBlocking invariants | [architecture.md#stable-invariants](../architecture.md#stable-invariants)<br>[architecture.md#source-interface](../architecture.md#source-interface) | incorporated |
| Plugin/persistence ownership and eight-step runtime | [architecture.md#source-interface](../architecture.md#source-interface)<br>[architecture.md#persistence-ownership](../architecture.md#persistence-ownership)<br>[architecture.md#runtime-flow](../architecture.md#runtime-flow) | incorporated |
| Unified Reader/repository/pure-domain/single-module trade-offs | [architecture.md#decisions-and-trade-offs](../architecture.md#decisions-and-trade-offs) | incorporated |
| Old architecture/codemap ownership/loading split | [evidence/navigation-and-tooling-history.md#provenance-and-structural-context](../evidence/navigation-and-tooling-history.md#provenance-and-structural-context) | historical routing relocated |

## README.md

SHA256: `ded2a4b520543375f7ff6460ac6da31bacf0cd955526ee3b92bf9048cae8cee2`

Coverage status: `incorporated`. Verification: `independently-verified` (2026-10-08, runner_navigation).

| Extracted source substance | Exact destination sections | Disposition |
| --- | --- | --- |
| Purpose/features/personal-use/affiliation/license and exclusions | [architecture.md#purpose-and-scope](../architecture.md#purpose-and-scope) | incorporated with historical corrections |
| Stack/SDK/JVM/build/install/verification/OpenCode verify | [runbooks/development.md#setup-and-build](../runbooks/development.md#setup-and-build)<br>[runbooks/development.md#verification-gate-and-tests](../runbooks/development.md#verification-gate-and-tests) | incorporated with historical corrections |
| Source scaffolding/registration/parsers/fixtures/tests and stale kotlin paths | [concepts/source-plugins.md#new-source-workflow-and-tests](../concepts/source-plugins.md#new-source-workflow-and-tests) | incorporated with historical corrections |
| Private directory/files/URL hashing/uninstall | [concepts/persistence.md#download-storage-and-integrity](../concepts/persistence.md#download-storage-and-integrity) | incorporated with historical corrections |
| Old dedicated readers/doc autoload/OpenSpec/config/agents/commands | [evidence/navigation-and-tooling-history.md#current-corrections-and-unresolved-discrepancies](../evidence/navigation-and-tooling-history.md#current-corrections-and-unresolved-discrepancies)<br>[evidence/navigation-and-tooling-history.md#original-blueprint-and-bootstrap-intent](../evidence/navigation-and-tooling-history.md#original-blueprint-and-bootstrap-intent)<br>[evidence/navigation-and-tooling-history.md#reusable-project-tooling-from-the-blueprint](../evidence/navigation-and-tooling-history.md#reusable-project-tooling-from-the-blueprint) | incorporated with historical corrections |

## AGENTS.md

SHA256: `45ce45f4974650e96f30a37a7c43c861bf6bab36bba88a105ec0e7657792235d`

Coverage status: `incorporated`. Verification: `independently-verified` (2026-10-08, runner_navigation).

| Extracted source substance | Exact destination sections | Disposition |
| --- | --- | --- |
| Ten non-negotiables and layer boundaries | [architecture.md#layer-contracts](../architecture.md#layer-contracts)<br>[architecture.md#stable-invariants](../architecture.md#stable-invariants) | incorporated; generated telemetry excluded with reason |
| Placement/source/repository/Room/network/DI/screen responsibilities, including data/network network/JSON/cookie placement versus current DI wiring | [concepts/runtime-wiring.md#entrypoints-and-production-structure](../concepts/runtime-wiring.md#entrypoints-and-production-structure)<br>[concepts/runtime-wiring.md#hilt-graph](../concepts/runtime-wiring.md#hilt-graph) | incorporated; generated telemetry excluded with reason |
| Specialists, delegation, git/gh, approvals, tests/utilities/verification, commit taxonomy | [runbooks/development.md#verification-gate-and-tests](../runbooks/development.md#verification-gate-and-tests)<br>[runbooks/development.md#delivery-and-commit-conventions](../runbooks/development.md#delivery-and-commit-conventions)<br>[runbooks/development.md#current-vault-and-skills-workflow](../runbooks/development.md#current-vault-and-skills-workflow) | incorporated; generated telemetry excluded with reason |
| Six prior approval categories and scoped retrieval | [Approval boundaries](../runbooks/development.md#approval-and-specialist-boundaries)<br>[index.md#canonical-entrypoints](../index.md#canonical-entrypoints) | incorporated; generated telemetry excluded with reason |
| OpenSpec workflow/exemptions/archive and old document ownership | [concepts/repository-workflow.md#historical-change-entry-and-exceptions](../concepts/repository-workflow.md#historical-change-entry-and-exceptions)<br>[concepts/repository-workflow.md#historical-documentation-ownership](../concepts/repository-workflow.md#historical-documentation-ownership)<br>[concepts/repository-workflow.md#historical-completion-and-archive](../concepts/repository-workflow.md#historical-completion-and-archive) | incorporated; generated telemetry excluded with reason |
| All generated Headroom retrieval/git/Gradle/todo/search/OpenSpec/RTK/Python/Headroom/Samsung/release/dashboard lessons | [evidence/agent-maintenance-lessons.md#retrieval-and-bounded-output](../evidence/agent-maintenance-lessons.md#retrieval-and-bounded-output)<br>[evidence/agent-maintenance-lessons.md#state-and-verification-discipline](../evidence/agent-maintenance-lessons.md#state-and-verification-discipline)<br>[evidence/agent-maintenance-lessons.md#runtime-and-tooling-facts](../evidence/agent-maintenance-lessons.md#runtime-and-tooling-facts)<br>[evidence/agent-maintenance-lessons.md#android-platform-and-api-diagnosis](../evidence/agent-maintenance-lessons.md#android-platform-and-api-diagnosis)<br>[evidence/agent-maintenance-lessons.md#retired-openspec-command-lessons](../evidence/agent-maintenance-lessons.md#retired-openspec-command-lessons)<br>[evidence/agent-maintenance-lessons.md#release-and-dashboard-work](../evidence/agent-maintenance-lessons.md#release-and-dashboard-work) | incorporated; generated telemetry excluded with reason |

## .opencode/project-structure.md

SHA256: `a0ad0dcfc396fed04468f14113a39a486b390c68caa108ebd90347adbe572f99`

Coverage status: `incorporated`. Verification: `independently-verified` (2026-10-08, runner_navigation).

| Extracted source substance | Exact destination sections | Disposition |
| --- | --- | --- |
| Illustrative full tree/source sets/placeholders/separate readers and scope of nested rules | [evidence/navigation-and-tooling-history.md#original-blueprint-and-bootstrap-intent](../evidence/navigation-and-tooling-history.md#original-blueprint-and-bootstrap-intent)<br>[evidence/navigation-and-tooling-history.md#current-corrections-and-unresolved-discrepancies](../evidence/navigation-and-tooling-history.md#current-corrections-and-unresolved-discrepancies) | historical intent with reusable constraints incorporated by concept |
| Config instructions/permissions/model inheritance/autoupdate and gitignore/bootstrap | [evidence/navigation-and-tooling-history.md#original-blueprint-and-bootstrap-intent](../evidence/navigation-and-tooling-history.md#original-blueprint-and-bootstrap-intent) | historical intent with reusable constraints incorporated by concept |
| Four command templates, specialist role/selector/screen/migration/reviewer rules and local-rule examples | [evidence/navigation-and-tooling-history.md#reusable-project-tooling-from-the-blueprint](../evidence/navigation-and-tooling-history.md#reusable-project-tooling-from-the-blueprint) | historical intent with reusable constraints incorporated by concept |

## .slim/codemap.json

SHA256: `cce59e705ebd4f7053431bb11b06783be9d273171d22c578768ca0e7d6bc1dec`

Coverage status: `incorporated`. Verification: `independently-verified` (2026-10-08, runner_navigation).

| Extracted source substance | Exact destination sections | Disposition |
| --- | --- | --- |
| Generator version/date/root/include/exclude scope/no exceptions | [evidence/navigation-and-tooling-history.md#codemap-generation-cache](../evidence/navigation-and-tooling-history.md#codemap-generation-cache) | historical |
| Every file_hashes/folder_hashes cache entry | [evidence/navigation-and-tooling-history.md#codemap-generation-cache](../evidence/navigation-and-tooling-history.md#codemap-generation-cache) | no-durable-substance: incremental-generation state and old hashes, no runtime/project requirement |

## app/src/main/java/com/opus/readerparser/ui/AGENTS.md

SHA256: `a89d2b15ae06ac1719409dc5acbb439e90fcf32f6d958eb4a9b8d5b58096fe65`

Coverage status: `incorporated`. Verification: `independently-verified` (2026-10-08, runner_navigation).

| Extracted source substance | Exact destination sections | Disposition |
| --- | --- | --- |
| All local constraints and distinct implementation/test/approval rules | [concepts/presentation.md#screen-contract](../concepts/presentation.md#screen-contract)<br>[concepts/presentation.md#library-and-browse-state](../concepts/presentation.md#library-and-browse-state)<br>[concepts/presentation.md#series-and-reader-state](../concepts/presentation.md#series-and-reader-state)<br>[concepts/presentation.md#downloads-and-settings-state](../concepts/presentation.md#downloads-and-settings-state)<br>[concepts/presentation.md#navigation-and-deep-links](../concepts/presentation.md#navigation-and-deep-links)<br>[concepts/presentation.md#theme-ownership-and-limits](../concepts/presentation.md#theme-ownership-and-limits) | incorporated; installed routing retains reminders and canonical pointers |

## app/src/main/java/com/opus/readerparser/sources/AGENTS.md

SHA256: `8786545339e10d2beb7e61bd526ee0e9367d8c8f200886f8d6da758545c0514c`

Coverage status: `incorporated`. Verification: `independently-verified` (2026-10-08, runner_navigation).

| Extracted source substance | Exact destination sections | Disposition |
| --- | --- | --- |
| All local constraints and distinct implementation/test/approval rules | [concepts/source-plugins.md#contract-and-template-methods](../concepts/source-plugins.md#contract-and-template-methods)<br>[concepts/source-plugins.md#identity-and-registration](../concepts/source-plugins.md#identity-and-registration)<br>[concepts/source-plugins.md#errors-and-extraction-rules](../concepts/source-plugins.md#errors-and-extraction-rules)<br>[concepts/source-plugins.md#asura-listings-and-details](../concepts/source-plugins.md#asura-listings-and-details)<br>[concepts/source-plugins.md#asura-chapters-and-dates](../concepts/source-plugins.md#asura-chapters-and-dates)<br>[concepts/source-plugins.md#new-source-workflow-and-tests](../concepts/source-plugins.md#new-source-workflow-and-tests) | incorporated; installed routing retains reminders and canonical pointers |

## app/src/main/java/com/opus/readerparser/data/source/AGENTS.md

SHA256: `3b42ca3e3e588227f280b687da3ddf3a61c888cd191a7abed206243605e5de03`

Coverage status: `incorporated`. Verification: `independently-verified` (2026-10-08, runner_navigation).

| Extracted source substance | Exact destination sections | Disposition |
| --- | --- | --- |
| All local constraints and distinct implementation/test/approval rules | [concepts/source-plugins.md#contract-and-template-methods](../concepts/source-plugins.md#contract-and-template-methods)<br>[concepts/source-plugins.md#identity-and-registration](../concepts/source-plugins.md#identity-and-registration)<br>[concepts/source-plugins.md#errors-and-extraction-rules](../concepts/source-plugins.md#errors-and-extraction-rules)<br>[concepts/source-plugins.md#asura-listings-and-details](../concepts/source-plugins.md#asura-listings-and-details)<br>[concepts/source-plugins.md#asura-chapters-and-dates](../concepts/source-plugins.md#asura-chapters-and-dates)<br>[concepts/source-plugins.md#new-source-workflow-and-tests](../concepts/source-plugins.md#new-source-workflow-and-tests) | incorporated; installed routing retains reminders and canonical pointers |

## app/src/main/java/com/opus/readerparser/data/local/database/AGENTS.md

SHA256: `28d29c1cafaa0e8afbd3fe7b5c4b3b575cdf43a691bf27aeae1ef86773d2b0d5`

Coverage status: `incorporated`. Verification: `independently-verified` (2026-10-08, runner_navigation).

| Extracted source substance | Exact destination sections | Disposition |
| --- | --- | --- |
| All local constraints and distinct implementation/test/approval rules | [concepts/persistence.md#structured-state-and-repositories](../concepts/persistence.md#structured-state-and-repositories)<br>[concepts/persistence.md#room-tables-and-queries](../concepts/persistence.md#room-tables-and-queries)<br>[concepts/persistence.md#conversion-and-chapter-refresh](../concepts/persistence.md#conversion-and-chapter-refresh)<br>[concepts/persistence.md#download-storage-and-integrity](../concepts/persistence.md#download-storage-and-integrity)<br>[concepts/persistence.md#preferences-and-atomic-edits](../concepts/persistence.md#preferences-and-atomic-edits)<br>[concepts/persistence.md#samsung-index-infrastructure](../concepts/persistence.md#samsung-index-infrastructure)<br>[concepts/persistence.md#migration-discipline](../concepts/persistence.md#migration-discipline) | incorporated; installed routing retains reminders and canonical pointers |

## .opencode/skills/openspec-apply-change/SKILL.md

SHA256: `77150795414bc1d84ad268271b27f896d0f03ece28ab1ecaacd30da229539749`

Coverage status: `incorporated`. Verification: `independently-verified` (2026-10-08, runner_navigation).

| Extracted source substance | Exact destination sections | Disposition |
| --- | --- | --- |
| Input selection, numbered procedures, statuses, clarification/guardrails, and completion conditions | [evidence/navigation-and-tooling-history.md#retired-apply-procedures](../evidence/navigation-and-tooling-history.md#retired-apply-procedures) | historical workflow, explicitly retired |
| Project-specific runner/reviewer correction or integrator/git/PR/CI/confirmation knowledge | [specs/agent-lanes.md#historical-verification-correction-cycle](../specs/agent-lanes.md#historical-verification-correction-cycle) | deduplicated historical contract |
| Generated metadata/license/version and generic examples/output decorations | [evidence/navigation-and-tooling-history.md#retired-apply-procedures](../evidence/navigation-and-tooling-history.md#retired-apply-procedures) | no-durable-substance: generic tool packaging/examples, no extra ReaderParser claim |

## .opencode/skills/openspec-archive-change/SKILL.md

SHA256: `1f208e1d21b5217a4d5fc598e430ff618adbfddcd2b6f46b68e305009cb3199a`

Coverage status: `incorporated`. Verification: `independently-verified` (2026-10-08, runner_navigation).

| Extracted source substance | Exact destination sections | Disposition |
| --- | --- | --- |
| Input selection, numbered procedures, statuses, clarification/guardrails, and completion conditions | [evidence/navigation-and-tooling-history.md#retired-archive-procedures](../evidence/navigation-and-tooling-history.md#retired-archive-procedures) | historical workflow, explicitly retired |
| Project-specific runner/reviewer correction or integrator/git/PR/CI/confirmation knowledge | [specs/agent-lanes.md#historical-archive-delivery](../specs/agent-lanes.md#historical-archive-delivery) | deduplicated historical contract |
| Generated metadata/license/version and generic examples/output decorations | [evidence/navigation-and-tooling-history.md#retired-archive-procedures](../evidence/navigation-and-tooling-history.md#retired-archive-procedures) | no-durable-substance: generic tool packaging/examples, no extra ReaderParser claim |

## .opencode/skills/openspec-explore/SKILL.md

SHA256: `d377459327732a5466f6ae7e48ceaeaf3ad33c46be75a088495aa66a791fde32`

Coverage status: `incorporated`. Verification: `independently-verified` (2026-10-08, runner_navigation).

| Extracted source substance | Exact destination sections | Disposition |
| --- | --- | --- |
| Input selection, numbered procedures, statuses, clarification/guardrails, and completion conditions | [evidence/navigation-and-tooling-history.md#retired-exploration-procedures](../evidence/navigation-and-tooling-history.md#retired-exploration-procedures) | historical workflow, explicitly retired |
| Generated metadata/license/version and generic examples/output decorations | [evidence/navigation-and-tooling-history.md#retired-exploration-procedures](../evidence/navigation-and-tooling-history.md#retired-exploration-procedures) | no-durable-substance: generic tool packaging/examples, no extra ReaderParser claim |

## .opencode/skills/openspec-propose/SKILL.md

SHA256: `7cb1ff97265c8fb21441a36457a206b7d0f78eaf7467c986eac41439ce98dfbd`

Coverage status: `incorporated`. Verification: `independently-verified` (2026-10-08, runner_navigation).

| Extracted source substance | Exact destination sections | Disposition |
| --- | --- | --- |
| Input selection, numbered procedures, statuses, clarification/guardrails, and completion conditions | [evidence/navigation-and-tooling-history.md#retired-propose-procedures](../evidence/navigation-and-tooling-history.md#retired-propose-procedures) | historical workflow, explicitly retired |
| Generated metadata/license/version and generic examples/output decorations | [evidence/navigation-and-tooling-history.md#retired-propose-procedures](../evidence/navigation-and-tooling-history.md#retired-propose-procedures) | no-durable-substance: generic tool packaging/examples, no extra ReaderParser claim |

## .opencode/commands/opsx-apply.md

SHA256: `bdfede9855048b4d909e08362b75031786f700051362537decf1e36c50a647e6`

Coverage status: `incorporated`. Verification: `independently-verified` (2026-10-08, runner_navigation).

| Extracted source substance | Exact destination sections | Disposition |
| --- | --- | --- |
| Input selection, numbered procedures, statuses, clarification/guardrails, and completion conditions | [evidence/navigation-and-tooling-history.md#retired-apply-procedures](../evidence/navigation-and-tooling-history.md#retired-apply-procedures) | historical workflow, explicitly retired |
| Project-specific runner/reviewer correction or integrator/git/PR/CI/confirmation knowledge | [specs/agent-lanes.md#historical-verification-correction-cycle](../specs/agent-lanes.md#historical-verification-correction-cycle) | deduplicated historical contract |
| Generated metadata/license/version and generic examples/output decorations | [evidence/navigation-and-tooling-history.md#retired-apply-procedures](../evidence/navigation-and-tooling-history.md#retired-apply-procedures) | no-durable-substance: generic tool packaging/examples, no extra ReaderParser claim |

## .opencode/commands/opsx-archive.md

SHA256: `aba38d5878062a79a7c81de911fda705a024a78a6b293a24daac55b424c88f4e`

Coverage status: `incorporated`. Verification: `independently-verified` (2026-10-08, runner_navigation).

| Extracted source substance | Exact destination sections | Disposition |
| --- | --- | --- |
| Input selection, numbered procedures, statuses, clarification/guardrails, and completion conditions | [evidence/navigation-and-tooling-history.md#retired-archive-procedures](../evidence/navigation-and-tooling-history.md#retired-archive-procedures) | historical workflow, explicitly retired |
| Project-specific runner/reviewer correction or integrator/git/PR/CI/confirmation knowledge | [specs/agent-lanes.md#historical-archive-delivery](../specs/agent-lanes.md#historical-archive-delivery) | deduplicated historical contract |
| Generated metadata/license/version and generic examples/output decorations | [evidence/navigation-and-tooling-history.md#retired-archive-procedures](../evidence/navigation-and-tooling-history.md#retired-archive-procedures) | no-durable-substance: generic tool packaging/examples, no extra ReaderParser claim |

## .opencode/commands/opsx-explore.md

SHA256: `7cb8e85bb238f1b6a7321c63e92c6181970bdd2ecc9089053d6a2ab8084c21f9`

Coverage status: `incorporated`. Verification: `independently-verified` (2026-10-08, runner_navigation).

| Extracted source substance | Exact destination sections | Disposition |
| --- | --- | --- |
| Input selection, numbered procedures, statuses, clarification/guardrails, and completion conditions | [evidence/navigation-and-tooling-history.md#retired-exploration-procedures](../evidence/navigation-and-tooling-history.md#retired-exploration-procedures) | historical workflow, explicitly retired |
| Generated metadata/license/version and generic examples/output decorations | [evidence/navigation-and-tooling-history.md#retired-exploration-procedures](../evidence/navigation-and-tooling-history.md#retired-exploration-procedures) | no-durable-substance: generic tool packaging/examples, no extra ReaderParser claim |

## .opencode/commands/opsx-propose.md

SHA256: `38c79d395a4e3d3e748ca7cbd4229f47a9279d53dcc9d8a87665ceca4322222f`

Coverage status: `incorporated`. Verification: `independently-verified` (2026-10-08, runner_navigation).

| Extracted source substance | Exact destination sections | Disposition |
| --- | --- | --- |
| Input selection, numbered procedures, statuses, clarification/guardrails, and completion conditions | [evidence/navigation-and-tooling-history.md#retired-propose-procedures](../evidence/navigation-and-tooling-history.md#retired-propose-procedures) | historical workflow, explicitly retired |
| Generated metadata/license/version and generic examples/output decorations | [evidence/navigation-and-tooling-history.md#retired-propose-procedures](../evidence/navigation-and-tooling-history.md#retired-propose-procedures) | no-durable-substance: generic tool packaging/examples, no extra ReaderParser claim |
