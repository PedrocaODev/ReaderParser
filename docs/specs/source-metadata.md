---
id: specs-source-metadata
title: "Source metadata cache contract"
type: Concept
status: proposed
valid_from: 2026-10-08
scope:
  system: readerparser
relations:
  - type: supported_by
    target: ../evidence/specification-evolution.md
---

# Source metadata cache contract

This node consolidates the unarchived optimization's metadata requirements.
The change is partially implemented. These requirements alone do not certify every cache operation.

## Bounded parsed metadata

Keep successful parsed catalog pages, search pages, series details, and chapter lists in bounded in-memory LRU caches.
Use monotonic elapsed time for expiry.

| Operation | TTL | Maximum entries |
| --- | --- | --- |
| Catalog and search | Five minutes | 100 |
| Series detail | Fifteen minutes | 50 |
| Chapter list | Two minutes | 20 |

A fresh exact request returns its parsed cache entry without a source call.
An expired request calls the source and replaces the entry only after success.
Insertion at capacity evicts the least-recently-used entry.
At two minutes after simultaneous insertion, the chapter-list entry is expired while catalog/search and detail entries remain fresh.

## Request identity

Catalog/search keys include source ID, operation, normalized query, page, and an immutable ordered `FilterList` snapshot.
Use structural equality for filter values. Equal values in equal order produce equal keys.
Filter order remains significant: equal filters in a different order produce different keys.
Later mutation of the caller's filter collection must not change the stored key.
Detail/chapter-list keys include source ID and series URL.
One source, operation, query, page, or filter set cannot satisfy a different request.
A cached NOVEL source result cannot satisfy the same query for a MANHWA source. Page one cannot satisfy page two.

## Successful remote truth

Failed requests and local fallback rows must not become successful remote cache entries.
A thrown source request creates no successful entry.
Fallback after empty or failed remote search must not suppress later remote retry after connectivity recovers.

## Payload ownership

Metadata caches exclude opened chapter HTML, manhwa image payloads, and image files.
`DownloadStore` owns explicitly downloaded chapter content. Coil owns images. OkHttp owns raw HTTP transport caching.
Opening an undownloaded chapter must not persist its payload as a downloaded chapter through the metadata cache.
