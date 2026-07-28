# Manhwa Page List — Scroll Fix Specification

## MODIFIED Requirements

### Requirement: Image items maintain minimum height during loading/error

The ManhwaPageList composable prevents LazyColumn from truncating the scroll by ensuring each image item has measurable height even when no image is loaded.

#### Scenario: Image during loading has measurable height

Given a ManhwaPageList with pending image loads
When the list is composed
Then each `AsyncImage` item has at least a minimum height (e.g., 100dp or a viewport fraction)
And the LazyColumn scroll range includes all items, regardless of load state

#### Scenario: Image on error has measurable height

Given a ManhwaPageList with an image that fails to load
When the error painter is displayed
Then the item maintains at least the minimum height
And the LazyColumn scroll range is not reduced

#### Scenario: Loaded image replaces minimum height

Given a ManhwaPageList item with the minimum-height placeholder
When the image successfully loads
Then the item resizes to the natural image dimensions
And the scroll range adjusts accordingly
