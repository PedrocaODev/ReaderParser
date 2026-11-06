package com.opus.readerparser.ui.library

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.opus.readerparser.domain.model.ContentType
import com.opus.readerparser.domain.model.Series
import com.opus.readerparser.ui.theme.ReaderParserTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LibraryContentTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun loadingState_showsProgressIndicator() {
        composeRule.setContent {
            ReaderParserTheme {
                LibraryContent(state = LibraryUiState(isLoading = true), onAction = {})
            }
        }
        composeRule.onNodeWithTag("loading").assertIsDisplayed()
    }

    @Test
    fun errorState_showsErrorAndRetryButton() {
        var retryQuery: String? = null
        composeRule.setContent {
            ReaderParserTheme {
                LibraryContent(
                    state = LibraryUiState(error = "Something went wrong", searchQuery = "test query"),
                    onAction = { action ->
                        if (action is LibraryAction.SetSearchQuery) {
                            retryQuery = action.query
                        }
                    },
                )
            }
        }
        composeRule.onNodeWithTag("error_message").assertIsDisplayed()
        composeRule.onNodeWithText("Something went wrong").assertIsDisplayed()
        composeRule.onNodeWithTag("retry_button").assertIsDisplayed().performClick()
        assertThat(retryQuery).isEqualTo("test query")
    }

    @Test
    fun emptyState_unfiltered_showsBrowseSources() {
        var browseSourcesClicked = false
        composeRule.setContent {
            ReaderParserTheme {
                LibraryContent(
                    state = LibraryUiState(),
                    onAction = {},
                    onBrowseSources = { browseSourcesClicked = true },
                )
            }
        }
        composeRule.onNodeWithTag("empty_state").assertIsDisplayed()
        composeRule.onNodeWithText("Your library is empty").assertIsDisplayed()
        composeRule.onNodeWithText("Browse Sources").assertIsDisplayed().performClick()
        assertThat(browseSourcesClicked).isTrue()
    }

    @Test
    fun emptyState_unfiltered_withoutBrowseCallback_hidesBrowseSourcesCTA() {
        composeRule.setContent {
            ReaderParserTheme {
                LibraryContent(
                    state = LibraryUiState(),
                    onAction = {},
                    onBrowseSources = null,
                )
            }
        }
        composeRule.onNodeWithTag("empty_state").assertIsDisplayed()
        composeRule.onNodeWithText("Browse Sources").assertDoesNotExist()
    }

    @Test
    fun emptyState_filteredOrSearch_showsClearFiltersCTA() {
        val actions = mutableListOf<LibraryAction>()
        composeRule.setContent {
            ReaderParserTheme {
                LibraryContent(
                    state = LibraryUiState(
                        searchQuery = "Hunter",
                        selectedFilter = LibraryFilter.READING,
                        items = emptyList(),
                        series = emptyList(),
                    ),
                    onAction = actions::add,
                )
            }
        }
        composeRule.onNodeWithTag("empty_state").assertIsDisplayed()
        composeRule.onNodeWithText("No matching series").assertIsDisplayed()
        composeRule.onNodeWithText("Clear Filters").assertIsDisplayed().performClick()
        assertThat(actions.any { it is LibraryAction.SetSearchQuery && it.query.isEmpty() }).isTrue()
        assertThat(actions.any { it is LibraryAction.SetFilter && it.filter == LibraryFilter.ALL }).isTrue()
    }

    @Test
    fun chipFilterRow_rendersCounts_andDispatchesSetFilter() {
        var selectedFilter: LibraryFilter? = null
        val sampleItem = LibrarySeriesItem(
            series = Series(sourceId = 1L, url = "url1", title = "Test Series", type = ContentType.NOVEL),
            unreadCount = 1,
            totalChapters = 2,
            readCount = 1,
        )
        composeRule.setContent {
            ReaderParserTheme {
                LibraryContent(
                    state = LibraryUiState(
                        items = listOf(sampleItem),
                        filterCounts = mapOf(
                            LibraryFilter.ALL to 1,
                            LibraryFilter.READING to 1,
                            LibraryFilter.DOWNLOADED to 0,
                            LibraryFilter.COMPLETED to 0,
                        ),
                    ),
                    onAction = { action ->
                        if (action is LibraryAction.SetFilter) {
                            selectedFilter = action.filter
                        }
                    },
                )
            }
        }
        composeRule.onNodeWithTag("library_filter_row").assertIsDisplayed()
        composeRule.onNodeWithTag("filter_chip_all").assertIsDisplayed()
        composeRule.onNodeWithText("All (1)").assertIsDisplayed()
        composeRule.onNodeWithTag("filter_chip_reading").assertIsDisplayed()
        composeRule.onNodeWithText("Reading (1)").assertIsDisplayed()
        composeRule.onNodeWithTag("filter_chip_downloaded").assertIsDisplayed()
        composeRule.onNodeWithText("Downloaded (0)").assertIsDisplayed()
        composeRule.onNodeWithTag("filter_chip_completed").assertIsDisplayed()
        composeRule.onNodeWithText("Completed (0)").assertIsDisplayed()

        composeRule.onNodeWithTag("filter_chip_reading").performClick()
        assertThat(selectedFilter).isEqualTo(LibraryFilter.READING)
    }

    @Test
    fun sortButton_opensBottomSheet_andDispatchesSetSortBy() {
        var selectedSort: LibrarySortOption? = null
        composeRule.setContent {
            ReaderParserTheme {
                LibraryContent(
                    state = LibraryUiState(),
                    onAction = { action ->
                        if (action is LibraryAction.SetSortBy) {
                            selectedSort = action.sortBy
                        }
                    },
                )
            }
        }
        composeRule.onNodeWithTag("sort_button").performClick()
        composeRule.onNodeWithTag("sort_bottom_sheet").assertIsDisplayed()
        composeRule.onNodeWithText("Sort Library").assertIsDisplayed()
        composeRule.onNodeWithTag("sort_option_default").assertIsDisplayed()
        composeRule.onNodeWithTag("sort_option_title").assertIsDisplayed().performClick()
        assertThat(selectedSort).isEqualTo(LibrarySortOption.TITLE_ASC)
    }

    @Test
    fun searchInputPill_dispatchesQueryChange_andClear() {
        val queries = mutableListOf<String>()
        composeRule.setContent {
            ReaderParserTheme {
                LibraryContent(
                    state = LibraryUiState(searchQuery = "Hunter"),
                    onAction = { action ->
                        if (action is LibraryAction.SetSearchQuery) {
                            queries.add(action.query)
                        }
                    },
                )
            }
        }
        composeRule.onNodeWithTag("search_input").assertIsDisplayed()
        composeRule.onNodeWithTag("clear_search_button").assertIsDisplayed().performClick()
        assertThat(queries).contains("")
    }

    @Test
    fun librarySeriesCard_rendersBadgesProgressAndRespondsToClicks() {
        val series = Series(sourceId = 1L, url = "url1", title = "Solo Leveling", type = ContentType.MANHWA)
        val item = LibrarySeriesItem(
            series = series,
            unreadCount = 2,
            downloadedCount = 3,
            currentChapterNumber = 42f,
            totalChapters = 1420,
            progressRatio = 0.68f,
        )
        var openedSeries: Series? = null
        var removedSeries: Series? = null

        composeRule.setContent {
            ReaderParserTheme {
                LibraryContent(
                    state = LibraryUiState(items = listOf(item)),
                    onAction = { action ->
                        when (action) {
                            is LibraryAction.OpenSeries -> openedSeries = action.series
                            is LibraryAction.RemoveFromLibrary -> removedSeries = action.series
                            else -> Unit
                        }
                    },
                )
            }
        }

        composeRule.onNodeWithText("Solo Leveling").assertIsDisplayed()
        composeRule.onNodeWithTag("unread_badge").assertIsDisplayed()
        composeRule.onNodeWithText("2 unread").assertIsDisplayed()
        composeRule.onNodeWithTag("series_card_progress_text").assertIsDisplayed()
        composeRule.onNodeWithText("Ch. 42 / 1420 · 68%").assertIsDisplayed()
        composeRule.onNodeWithTag("downloaded_badge").assertIsDisplayed()
        composeRule.onNodeWithText("✓ 3").assertIsDisplayed()
        composeRule.onNodeWithTag("mini_progress_bar").assertIsDisplayed()

        composeRule.onNodeWithTag("series_card").performClick()
        assertThat(openedSeries).isEqualTo(series)

        composeRule.onNodeWithTag("series_card").performTouchInput { longClick() }
        assertThat(removedSeries).isEqualTo(series)
    }

    @Test
    fun librarySeriesCard_formatsFractionalChapterNumberWithSharedFormatter() {
        val series = Series(sourceId = 1L, url = "url1", title = "Fractional Progress Series", type = ContentType.NOVEL)
        val item = LibrarySeriesItem(
            series = series,
            unreadCount = 10,
            downloadedCount = 0,
            currentChapterNumber = 42.5f,
            totalChapters = 100,
            progressRatio = 0.425f,
        )

        composeRule.setContent {
            ReaderParserTheme {
                LibraryContent(
                    state = LibraryUiState(items = listOf(item)),
                    onAction = {},
                )
            }
        }

        composeRule.onNodeWithTag("series_card_progress_text").assertIsDisplayed()
        composeRule.onNodeWithText("Ch. 42.5 / 100 · 43%").assertIsDisplayed()
    }

    @Test
    fun compactGrid_rendersCardsInSingleColumn() {
        val series = listOf(
            Series(sourceId = 1L, url = "url1", title = "First", type = ContentType.NOVEL),
            Series(sourceId = 1L, url = "url2", title = "Second", type = ContentType.MANHWA),
        )
        composeRule.setContent {
            ReaderParserTheme {
                Box(modifier = Modifier.requiredWidth(180.dp).requiredHeight(1000.dp)) {
                    LibraryContent(state = LibraryUiState(series = series), onAction = {})
                }
            }
        }

        val bounds = composeRule.onAllNodesWithTag("series_card").fetchSemanticsNodes().map { it.boundsInRoot }
        assertThat(bounds).hasSize(2)
        assertThat(bounds[1].top).isGreaterThan(bounds[0].top)
        assertThat(bounds[1].left).isEqualTo(bounds[0].left)
    }

    @Test
    fun expandedGrid_usesAdditionalColumns() {
        val series = listOf(
            Series(sourceId = 1L, url = "url1", title = "First", type = ContentType.NOVEL),
            Series(sourceId = 1L, url = "url2", title = "Second", type = ContentType.MANHWA),
            Series(sourceId = 1L, url = "url3", title = "Third", type = ContentType.NOVEL),
        )
        composeRule.setContent {
            ReaderParserTheme {
                Box(modifier = Modifier.requiredWidth(520.dp).requiredHeight(600.dp)) {
                    LibraryContent(state = LibraryUiState(series = series), onAction = {})
                }
            }
        }

        val bounds = composeRule.onAllNodesWithTag("series_card").fetchSemanticsNodes().map { it.boundsInRoot }
        assertThat(bounds).hasSize(3)
        assertThat(bounds[1].top).isEqualTo(bounds[0].top)
        assertThat(bounds[2].top).isEqualTo(bounds[0].top)
    }
}
