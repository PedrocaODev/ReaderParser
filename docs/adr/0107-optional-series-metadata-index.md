---
id: decision-optional-series-metadata-index
title: "Publish an optional downloaded-series metadata index"
type: Decision
status: proposed
valid_from: 2026-06-10
scope:
  system: readerparser
relations:
  - type: supported_by
    target: ../evidence/infrastructure-history.md
---

# Publish an optional downloaded-series metadata index

## Historical choice and alternatives

The Samsung design chose an optional public metadata index for series with downloaded chapters, excluding chapter documents and library-only or browse-only series. Results open Series detail. Provider absence and errors cannot block normal reading or app startup.

## Status and provenance

This proposed candidate preserves the deliberate narrowed scope from the older full series/chapter onboarding idea. It does not assert accepted integration behavior.
The source creation date is 2026-06-10. This candidate was incorporated on 2026-10-08.
Source provenance and independent coverage status are in the [coverage ledger](../migrations/coverage-infrastructure.md).
Historical completion claims are in [Samsung verification claims](../evidence/infrastructure-history.md#samsung-verification-claims).

## Consequences and reversal cost

Expanding scope changes exposed documents, external navigation, and eligibility expectations. Excluding chapters from a reading app's search is surprising without the narrow MVP rationale. Broad library-driven series/chapter indexing was a real alternative.

## Limits and open questions

A user searching a chapter title will not find chapter documents. Metadata fields omit chapter payload. Registration repair still had unchecked instrumentation and device activation tasks.
