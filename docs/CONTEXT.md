---
id: concept-project-glossary
title: "ReaderParser project glossary"
type: Concept
status: active
valid_from: 2026-10-08
scope:
  system: readerparser
relations:
  - type: relates_to
    target: ./architecture.md
---

# ReaderParser project glossary

| Term | Project meaning |
| --- | --- |
| Source | A site-specific plugin implementing the common retrieval contract. |
| Source ID | A stable identifier for a site's name, language, and content type. |
| Series | A novel or manhwa title from one source. Identity is source ID plus series URL. |
| Chapter | One readable unit of a series. Identity is source ID plus chapter URL. |
| Novel | Text content represented by chapter HTML. |
| Manhwa | Image content represented by page URLs in reading order. |
| Chapter content | Either Text HTML or Pages image URLs, with no third variant. |
| Series page | A source listing result plus an indication that another page exists. |
| Library | Locally bookmarked series for the single app user. |
| Indexable series | A series with at least one downloaded chapter, regardless of library membership. |
| Eligible Library search row | A series both in Library and indexable through downloaded chapters. |
| Download queue | Chapter download work with queued, running, completed, or failed state and normalized progress. |
| Downloaded chapter | Chapter content explicitly saved in app-private storage for offline reading. |
| Reader | The shared reading surface for novels and manhwa with content-specific renderers. |
| Filter | A text, select, or toggle value passed to source search. |
| UiState | The single state value that a screen renders. |
| Action | A UI event forwarded to the ViewModel. |
| Effect | A one-time presentation event such as navigation or a snackbar. |
| Vault | The canonical project knowledge under docs, with typed Akashic nodes and relations. |
