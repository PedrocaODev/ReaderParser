---
id: decision-candidate-asura-api-completeness
title: "Candidate: API-first Asura image discovery with HTML fallback"
type: Decision
status: proposed
valid_from: 2026-10-08
scope:
  system: readerparser
  source: asurascans
relations:
  - type: supported_by
    target: ../evidence/ui-source-history.md
  - type: relates_to
    target: ../concepts/catalog-and-source-parsing.md
  - type: relates_to
    target: ../evidence/implementation-gaps.md
---

# Candidate: API-first Asura image discovery with HTML fallback

## Choice and trade-off

Use complete API page lists to avoid relying solely on server-rendered lazy-image markup.
Keep HTML fallback for request/format failures and chapter slugs not supported by the constructed API path.
API format/authentication compatibility becomes a maintenance commitment, balanced against complete page discovery.
HTML enhancement remains the rejected alternative when API completeness is established.

## Candidate assessment

An external retrieval contract can require broad fixture/fallback changes to reverse and is surprising without lazy-loading context.
The original record was created 2026-07-27. Acceptance and complete runtime coverage are not established by the archived task list.
The design's `/api/novels/{slug}/download` hypothesis is not the shipped endpoint.
Reported/source-inspected implementation uses `/api/series/{series_slug}/chapters/{chapter_slug}`.
The sampled sixteen-page chapter and retained old parser tests do not prove every chapter or fallback condition.

## Source knowledge

See [page completeness and alternatives](../concepts/catalog-and-source-parsing.md#asurascans-complete-image-pages) and [endpoint gaps](../evidence/implementation-gaps.md#asura-endpoint-and-completeness).
