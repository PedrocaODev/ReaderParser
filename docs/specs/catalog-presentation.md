---
id: specs-catalog-presentation
title: "Catalog presentation and title integrity"
type: Concept
status: active
valid_from: 2026-10-08
scope:
  system: readerparser
relations:
  - type: supported_by
    target: ../evidence/specification-evolution.md
---

# Catalog presentation and title integrity

These are consolidated intended requirements. Migration metadata dates this synthesis, not original implementation or acceptance.

## Shared cards and adaptive layout

Library and Browse must use the same reusable cover-first series card. Each card shows the cover and title.
Library retains its existing series interaction. Browse selection retains navigation to the existing series detail destination.
The adaptive grid must remain usable at compact and expanded widths. Compact cards retain readable titles and available interactions.
Expanded widths increase the practical card layout to use available space.
Cards and grids use the active Material theme's color, typography, shape, and spacing tokens.

## Supported Library controls

Library must omit the Unread filter and toggle because unread-state semantics are unsupported.
The remaining supported Library behavior stays available, including sort, text search, removal, and Samsung-first search.
Browse retains source selection, pagination, and series-detail navigation. Search policy differences are recorded in [Browse discovery](browse-discovery.md).
The removed Unread control is a removed capability, not a new requirement to implement unread semantics.

## Detail title selection

Every source detail parser must prefer a nonblank title extracted from the detail page, even when the incoming series already has a title.
If extraction is absent or blank, the parser uses the incoming title only when that title is nonblank.
If both titles are blank, the parser must not represent the incoming title as a usable extracted title.

## Blank bookmark repair

During one Library screen lifecycle, attempt source-detail refresh and persistence for each bookmarked series with a blank stored title.
A successful refresh with a nonblank title must persist the repaired title while retaining bookmark data and `(sourceId, url)` identity.
If source retrieval, response handling, or detail parsing fails, leave the existing bookmark data unchanged.
Attempt repair at most once for the same blank bookmark in that lifecycle, including refreshes and recompositions.
A new Library lifecycle may attempt repair once again after a prior lifecycle's failure.
