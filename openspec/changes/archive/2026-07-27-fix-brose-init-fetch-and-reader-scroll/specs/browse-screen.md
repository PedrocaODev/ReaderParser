# Browse Screen — Init Fetch Specification

## MODIFIED Requirements

### Requirement: Browse screen auto-fetches popular series on launch

On first launch, the Browse screen selects the first available source and the "Popular" mode, and immediately triggers a fetch of popular series.

#### Scenario: Init fetches popular series for the first source

Given a user opens the Browse screen
And the source list loads with at least one source
When the ViewModel initializes
Then the first source is selected as `selectedSourceId`
And a fetch is triggered for `BrowseMode.POPULAR` on that source
And `isLoading` is `true` until the fetch completes
And `series` is populated with results

#### Scenario: Init shows empty state when no sources available

Given a user opens the Browse screen
And the source list loads with zero sources
When the ViewModel initializes
Then no source is selected
And no fetch is triggered
And the grid remains empty

### Requirement: Existing test updated

#### Scenario: Test asserts init triggers fetch

Given the existing test `init loads sources and selects first source without fetching`
When the change is applied
Then the test is renamed to `init loads sources, selects first source, and fetches popular`
And it asserts that `seriesRepository.fetchPopular` is called with the first source's ID
