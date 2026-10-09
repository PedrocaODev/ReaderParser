---
id: decision-candidate-parsed-metadata-ownership
title: "Candidate: bounded parsed metadata caching in repositories"
type: Decision
status: proposed
valid_from: 2026-10-08
scope:
  system: readerparser
relations:
  - type: supported_by
    target: ../evidence/ui-source-history.md
  - type: relates_to
    target: ../concepts/source-metadata-performance.md
  - type: relates_to
    target: ../specs/source-metadata.md
---

# Candidate: bounded parsed metadata caching in repositories

## Choice and trade-off

Repository-owned synchronized monotonic TTL/LRU caches reuse only fresh exact successful metadata requests.
Keep OkHttp transport caching, DownloadStore downloaded payloads, and Coil image caching as separate ownership boundaries.
Use immutable ordered request filters and entry-based limits, with five/fifteen/two-minute TTLs and 100/50/20 entry capacities.
Reject persistent query snapshots, duplicate Ktor HttpCache, and byte-weighted complexity before measured need.
Process-local reuse and brief metadata staleness trade against no schema migration and bounded memory/freshness.

## Candidate assessment

Changing caching ownership can affect repository correctness, request identity, and invalidation across both content types.
Separate transport and parsed freshness is surprising without the original limitation of server cache headers.
The proposal created 2026-07-12 remains unarchived despite checked tasks.
Migration inspection reports metadata caching present, but historical green checks are not new proof of every cache condition.
2026-10-08 is synthesis date, not asserted acceptance/effective date.

## Source knowledge

See [ownership](../concepts/source-metadata-performance.md#ownership-and-scope), [exact cache conditions](../concepts/source-metadata-performance.md#exact-fresh-metadata-cache), and [reported verification](../evidence/ui-source-history.md#performance-verification-and-overclaims).
