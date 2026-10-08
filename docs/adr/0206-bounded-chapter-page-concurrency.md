---
id: decision-candidate-bounded-chapter-page-concurrency
title: "Candidate: ordered bounded FreeWebNovel chapter-page windows"
type: Decision
status: proposed
valid_from: 2026-10-08
scope:
  system: readerparser
  source: freewebnovel
relations:
  - type: supported_by
    target: ../evidence/ui-source-history.md
  - type: relates_to
    target: ../concepts/source-metadata-performance.md
  - type: relates_to
    target: ../specs/source-pagination.md
---

# Candidate: ordered bounded FreeWebNovel chapter-page windows

## Choice and trade-off

Discover total pages serially on page one, then use ascending windows of at most three requests.
Merge in page order, deduplicate URLs, and stop at the first failed/blank/zero-unseen page with contiguous partial output.
Ignore later completed results in the window and schedule no later window.
This lowers serial latency with bounded pressure, accepting up to two avoidable requests after a terminal page.
Unbounded async is rejected because large novels could create throttling-inducing bursts.

## Candidate assessment

This is a proposal rather than a verified implemented decision. Migration inspection reports serial remainder fetching.
The historical verification report incorrectly treats sequential AJAX deduplication as fulfilling concurrency.
Terminal/partial-result compatibility can make reversal costly, but the candidate needs further ADR criteria assessment before acceptance.
The original proposal was created 2026-07-12. 2026-10-08 dates synthesis, not acceptance.

## Source knowledge

See [complete window/termination conditions](../concepts/source-metadata-performance.md#freewebnovel-bounded-remainder-proposal) and [implementation gap](../evidence/implementation-gaps.md#freewebnovel-concurrency).
