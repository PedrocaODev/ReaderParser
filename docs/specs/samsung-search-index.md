---
id: specs-samsung-search-index
title: "Samsung Search series index"
type: Concept
status: active
valid_from: 2026-10-08
scope:
  system: readerparser
relations:
  - type: supported_by
    target: ../evidence/specification-evolution.md
---

# Samsung Search series index

The downloaded-series search index differs from Library result eligibility. The index can include downloaded series outside Library.

## Availability and schema registration

Use `ContentResolver.call()` against `content://com.samsung.android.smartsuggestions.search/v2` to probe and register.
Probe with `request_search_api_version`. Availability requires a non-null bundle and present `response_search_api_version >= 1`.
A null bundle, missing value, or value below one means `isAvailable()=false`, with schema registration and sync skipped.
On probe exceptions, log a warning and return false.
`getType()` is unsuitable because this provider returns null from that method.
On first launch with an available provider, call `register_schema` and expect status code `0` (`SUCCESS`).
Set `arg=null`. Set extras `name=com.opus.readerparser.series` and `schema-content` to bundled asset XML as `byte[]`.
The schema name begins with the app package to satisfy `validateSchemaName()`.
The XML uses `<schema>` with `name`, `package`, `version`, and `keyFieldName` attributes.
Declare each used `<fieldType>`, including types such as `StringField` and `TextField`.
The former `<search-scheme>` root is rejected by Samsung Search.
On another launch, idempotent re-registration overwrites the schema and returns SUCCESS.
If Samsung Search is unavailable, catch failures, log warnings, and continue normal app operation without the integration.

## Indexable projection

A Room DAO must return `DISTINCT series.*` with at least one downloaded chapter, ordered by series title ascending.
Join `(series.sourceId, series.url) = (chapters.sourceId, chapters.seriesUrl)`.
A series with one downloaded chapter among three is included. Five chapters with none downloaded excludes the series.
`inLibrary=false` does not exclude a series with a downloaded chapter. Download state defines indexability.

## Series document fields

| Field | Type and behavior |
| --- | --- |
| `_id` | String key, `{sourceId}:{seriesUrl}` |
| `title` | Text |
| `author` | Text |
| `description` | Text, `stored=false` |
| `genres` | Text, comma-separated |
| `status` | String |
| `type` | String |
| `source_url` | String, `stored=true`, Series deep link |

Every document contains these fields. Exclude `chapter_name`, `chapter_number`, and `doc_type`.
For source 1, URL `https://example.com/tog`, title Tower of God, author SIU, genres action/fantasy, and an ongoing manhwa,
map `_id` to `1:https://example.com/tog`, genres to `action,fantasy`, status to `ONGOING`, and type to `MANHWA`.
Retain title `Tower of God`, author `SIU`, and a nonempty `source_url`.

## Deep-link destination

Tapping a Samsung Smart Suggestions result opens the existing ReaderParser Series detail screen for that series.
Its `source_url` must not open the Novel Reader, Manga Reader, or unified Reader directly.

## Rebuild and batching

Observe Room chapter-table changes and rebuild when any chapter's downloaded flag changes.
Query the indexable projection, delete all indexed documents, and bulk-insert the current set.
Debounce rebuilds, using a reasonable window such as two seconds.
After false becomes true, rebuild within that window.
After `deleteDownload()` resets the flag, rebuild and remove a series that has no remaining downloaded chapters.
Ten chapters downloaded in rapid succession cause at most one post-debounce rebuild rather than ten.
Bulk insert at most 100 documents per `ContentResolver.bulkInsert()` call.
A 250-series set uses batches 100, 100, 50. A 15-series set uses one batch.

## Broadcast recovery and permissions

Declare a receiver for `com.samsung.android.smartsuggestions.search.ACTION_UPDATE_INDEX`.
Protect it with `com.samsung.android.smartsuggestions.search.permission.SEND_ACTION_UPDATE_INDEX`.
On receipt, schedule a WorkManager `OneTimeWorkRequest` for a full delete/reinsert of the indexable projection.
This restores a corrupted index. WorkManager must execute the background request even if the app process is killed.
Declare Samsung Search permissions `com.samsung.android.smartsuggestions.search.permission.WRITE`,
`com.samsung.android.smartsuggestions.search.permission.READ`, and `com.samsung.android.smartsuggestions.search.permission.UPDATE_INDEX` in AndroidManifest.xml.

## Graceful degradation

Wrap all provider calls in try/catch. Log failed calls without crashing or blocking user interaction.
When Samsung Search is not installed, start normally, log disabled integration, and preserve every non-search feature.
