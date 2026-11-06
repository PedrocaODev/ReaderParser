package com.opus.readerparser.ui.browse

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CollectionsBookmark
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Explore
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.opus.readerparser.domain.model.ContentType
import com.opus.readerparser.domain.model.Series
import com.opus.readerparser.domain.model.SourceInfo
import com.opus.readerparser.ui.components.BottomNavBar
import com.opus.readerparser.ui.components.BottomNavDestination
import com.opus.readerparser.ui.theme.BottomNavBarHeight
import com.opus.readerparser.ui.theme.ReaderParserTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BrowseContentTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun loadingState_showsProgressIndicator() {
        composeRule.setContent {
            ReaderParserTheme {
                BrowseContent(state = BrowseUiState(isLoading = true), onAction = {})
            }
        }
        composeRule.onNodeWithTag("loading").assertIsDisplayed()
    }

    @Test
    fun errorState_showsErrorMessage() {
        composeRule.setContent {
            ReaderParserTheme {
                BrowseContent(state = BrowseUiState(error = "Network error", retryAvailable = true), onAction = {})
            }
        }
        composeRule.onNodeWithTag("error_message").assertIsDisplayed()
        composeRule.onNodeWithText("Network error").assertIsDisplayed()
        composeRule.onNodeWithText("Retry").assertIsDisplayed()
    }

    @Test
    fun emptyState_showsEmptyStateView() {
        composeRule.setContent {
            ReaderParserTheme {
                BrowseContent(
                    state = BrowseUiState(series = emptyList(), isLoading = false),
                    onAction = {},
                )
            }
        }
        composeRule.onNodeWithTag("empty_state").assertIsDisplayed()
    }

    @Test
    fun populatedState_showsSeriesList() {
        val source = SourceInfo(id = 1L, name = "AsuraScans", lang = "en", type = ContentType.MANHWA)
        val series = listOf(
            Series(sourceId = 1L, url = "url1", title = "Eleceed", type = ContentType.MANHWA),
            Series(sourceId = 1L, url = "url2", title = "Omniscient Reader", type = ContentType.MANHWA),
        )
        composeRule.setContent {
            ReaderParserTheme {
                BrowseContent(
                    state = BrowseUiState(sources = listOf(source), selectedSourceId = 1L, series = series),
                    onAction = {},
                )
            }
        }
        composeRule.onNodeWithTag("series_list").assertIsDisplayed()
        composeRule.onNodeWithText("Eleceed").assertIsDisplayed()
    }

    @Test
    fun sourceSelectorPill_opensSourcePickerSheet_andSelectsSource() {
        val source1 = SourceInfo(id = 1L, name = "FreeWebNovel", lang = "en", type = ContentType.NOVEL)
        val source2 = SourceInfo(id = 2L, name = "AsuraScans", lang = "en", type = ContentType.MANHWA)
        var selectedAction: BrowseAction? = null

        composeRule.setContent {
            ReaderParserTheme {
                BrowseContent(
                    state = BrowseUiState(
                        sources = listOf(source1, source2),
                        selectedSourceId = 1L,
                    ),
                    onAction = { selectedAction = it },
                )
            }
        }

        composeRule.onNodeWithTag("source_selector").assertIsDisplayed().performClick()
        composeRule.onNodeWithTag("source_picker_sheet").assertIsDisplayed()
        composeRule.onNodeWithText("AsuraScans").assertIsDisplayed()
        composeRule.onNodeWithText("EN · Manhwa").assertIsDisplayed()

        composeRule.onNodeWithText("AsuraScans").performClick()
        assertThat(selectedAction).isEqualTo(BrowseAction.SelectSource(2L))
    }

    @Test
    fun modeTabs_switchBetweenPopularLatestAndSearch() {
        val actions = mutableListOf<BrowseAction>()
        composeRule.setContent {
            ReaderParserTheme {
                BrowseContent(
                    state = BrowseUiState(mode = BrowseMode.POPULAR),
                    onAction = actions::add,
                )
            }
        }

        composeRule.onNodeWithTag("mode_tab_latest").assertIsDisplayed().performClick()
        assertThat(actions.last()).isEqualTo(BrowseAction.SetMode(BrowseMode.LATEST))

        composeRule.onNodeWithTag("mode_tab_search").assertIsDisplayed().performClick()
        assertThat(actions.last()).isEqualTo(BrowseAction.SetMode(BrowseMode.SEARCH))

        composeRule.onNodeWithTag("mode_tab_popular").assertIsDisplayed().performClick()
        assertThat(actions.last()).isEqualTo(BrowseAction.SetMode(BrowseMode.POPULAR))
    }

    @Test
    fun searchInputPill_typingClearAndImeSearch_dispatchActions() {
        val actions = mutableListOf<BrowseAction>()
        var uiState by mutableStateOf(BrowseUiState(mode = BrowseMode.POPULAR, searchQuery = ""))

        composeRule.setContent {
            ReaderParserTheme {
                BrowseContent(
                    state = uiState,
                    onAction = { action ->
                        actions.add(action)
                        if (action is BrowseAction.SetSearchQuery) {
                            uiState = uiState.copy(searchQuery = action.query)
                        }
                    },
                )
            }
        }

        composeRule.onNodeWithTag("search_input").assertIsDisplayed()
        composeRule.onNodeWithTag("search_input").performTextInput("dragon")
        assertThat(actions).contains(BrowseAction.SetSearchQuery("dragon"))

        composeRule.onNodeWithTag("search_input").performImeAction()
        assertThat(actions).contains(BrowseAction.Search)

        composeRule.onNodeWithTag("clear_search_button").assertIsDisplayed().performClick()
        assertThat(actions).contains(BrowseAction.SetSearchQuery(""))
    }

    @Test
    fun searchInputPill_clearButton_dispatchesEmptySearchQuery() {
        var dispatchedAction: BrowseAction? = null
        composeRule.setContent {
            ReaderParserTheme {
                BrowseContent(
                    state = BrowseUiState(mode = BrowseMode.POPULAR, searchQuery = "dragon"),
                    onAction = { dispatchedAction = it },
                )
            }
        }

        composeRule.onNodeWithTag("clear_search_button").assertIsDisplayed().performClick()
        assertThat(dispatchedAction).isEqualTo(BrowseAction.SetSearchQuery(""))
    }

    @Test
    fun catalogSeriesCard_rendersTypeBadgeSubtitleAndInLibraryBadge() {
        val series1 = Series(
            sourceId = 1L,
            url = "url1",
            title = "Shadow Slave",
            author = "Guiltythree",
            type = ContentType.NOVEL,
        )
        val series2 = Series(
            sourceId = 1L,
            url = "url2",
            title = "Eleceed",
            author = null,
            type = ContentType.MANHWA,
        )

        composeRule.setContent {
            ReaderParserTheme {
                BrowseContent(
                    state = BrowseUiState(
                        series = listOf(series1, series2),
                        inLibraryUrls = setOf("url2"),
                    ),
                    onAction = {},
                )
            }
        }

        composeRule.onNodeWithText("NOVEL").assertIsDisplayed()
        composeRule.onNodeWithText("Guiltythree").assertIsDisplayed()
        composeRule.onNodeWithText("In Library").assertIsDisplayed()
        composeRule.onNodeWithText("Manhwa").assertIsDisplayed()
    }

    @Test
    fun catalogSeriesCard_omitsRedundantTypeSubtitleWhenNotInLibrary_andAlignsRowHeights() {
        val series1 = Series(
            sourceId = 1L,
            url = "url1",
            title = "Cyberpunk Chivalry",
            author = null,
            type = ContentType.MANHWA,
        )
        val series2 = Series(
            sourceId = 1L,
            url = "url2",
            title = "Logging 10,000 Years into the Future",
            author = null,
            type = ContentType.MANHWA,
        )

        composeRule.setContent {
            ReaderParserTheme {
                Box(modifier = Modifier.requiredWidth(360.dp).requiredHeight(600.dp)) {
                    BrowseContent(
                        state = BrowseUiState(series = listOf(series1, series2)),
                        onAction = {},
                    )
                }
            }
        }

        composeRule.onNodeWithText("Manhwa").assertDoesNotExist()
        val bounds = composeRule.onAllNodesWithTag("series_card").fetchSemanticsNodes().map { it.boundsInRoot }
        assertThat(bounds).hasSize(2)
        assertThat(bounds[0].top).isEqualTo(bounds[1].top)
        assertThat(bounds[0].bottom).isEqualTo(bounds[1].bottom)
    }

    @Test
    fun sharedCard_rendersCoverTitle_andInvokesOpenAction() {
        val series = Series(sourceId = 1L, url = "url1", title = "Click Me", type = ContentType.MANHWA)
        var opened: Series? = null
        composeRule.setContent {
            ReaderParserTheme {
                Box(modifier = Modifier.requiredWidth(300.dp).requiredHeight(500.dp)) {
                    BrowseContent(
                        state = BrowseUiState(series = listOf(series), hasNextPage = true),
                        onAction = { action ->
                            if (action is BrowseAction.OpenSeries) opened = action.series
                        },
                    )
                }
            }
        }

        composeRule.onNodeWithContentDescription("Click Me").assertIsDisplayed()
        composeRule.onNodeWithText("Click Me").assertIsDisplayed()
        composeRule.onNodeWithTag("series_card").performClick()

        assertThat(opened).isEqualTo(series)
    }

    @Test
    fun expandedGrid_usesMoreThanOneColumn() {
        val series = listOf(
            Series(sourceId = 1L, url = "url1", title = "First", type = ContentType.MANHWA),
            Series(sourceId = 1L, url = "url2", title = "Second", type = ContentType.MANHWA),
            Series(sourceId = 1L, url = "url3", title = "Third", type = ContentType.MANHWA),
        )
        composeRule.setContent {
            ReaderParserTheme {
                Box(modifier = Modifier.requiredWidth(520.dp).requiredHeight(600.dp)) {
                    BrowseContent(state = BrowseUiState(series = series), onAction = {})
                }
            }
        }

        val bounds = composeRule.onAllNodesWithTag("series_card").fetchSemanticsNodes().map { it.boundsInRoot }
        assertThat(bounds).hasSize(3)
        assertThat(bounds[1].top).isEqualTo(bounds[0].top)
        assertThat(bounds[2].top).isEqualTo(bounds[0].top)
    }

    @Test
    fun compactGrid_keepsCardsReadable() {
        val series = listOf(
            Series(sourceId = 1L, url = "url1", title = "First", type = ContentType.MANHWA),
            Series(sourceId = 1L, url = "url2", title = "Second", type = ContentType.MANHWA),
        )
        composeRule.setContent {
            ReaderParserTheme {
                Box(modifier = Modifier.requiredWidth(280.dp).requiredHeight(1000.dp)) {
                    BrowseContent(state = BrowseUiState(series = series), onAction = {})
                }
            }
        }

        val bounds = composeRule.onAllNodesWithTag("series_card").fetchSemanticsNodes().map { it.boundsInRoot }
        assertThat(bounds).hasSize(2)
        assertThat(bounds[1].top).isGreaterThan(bounds[0].top)
        assertThat(bounds[1].left).isEqualTo(bounds[0].left)
    }

    @Test
    fun retryButton_dispatchesRetryAction() {
        var action: BrowseAction? = null
        composeRule.setContent {
            ReaderParserTheme {
                BrowseContent(
                    state = BrowseUiState(error = "Network error", retryAvailable = true),
                    onAction = { action = it },
                )
            }
        }

        composeRule.onNodeWithText("Retry").performClick()
        assertThat(action).isEqualTo(BrowseAction.Retry)
    }

    @Test
    fun paginationFooter_idleState_rendersLoadMoreAndDispatchesAction() {
        var action: BrowseAction? = null
        composeRule.setContent {
            ReaderParserTheme {
                BrowseContent(
                    state = BrowseUiState(
                        series = listOf(Series(sourceId = 1L, url = "url1", title = "Eleceed", type = ContentType.MANHWA)),
                        hasNextPage = true,
                        isLoading = false,
                        error = null,
                    ),
                    onAction = { action = it },
                )
            }
        }

        composeRule.onNodeWithText("Load More").assertIsDisplayed().performClick()
        assertThat(action).isEqualTo(BrowseAction.LoadMore)
    }

    @Test
    fun paginationLoading_showsSpinnerNearLoadMore() {
        val source = SourceInfo(id = 1L, name = "AsuraScans", lang = "en", type = ContentType.MANHWA)
        val series = listOf(
            Series(sourceId = 1L, url = "url1", title = "Eleceed", type = ContentType.MANHWA),
        )
        composeRule.setContent {
            ReaderParserTheme {
                BrowseContent(
                    state = BrowseUiState(
                        sources = listOf(source),
                        selectedSourceId = 1L,
                        series = series,
                        hasNextPage = true,
                        isLoading = true,
                    ),
                    onAction = {},
                )
            }
        }

        composeRule.onNodeWithTag("pagination_loading").assertIsDisplayed()
        composeRule.onNodeWithText("Loading more...").assertIsDisplayed()
    }

    @Test
    fun paginationFooter_errorState_rendersInlineErrorAndRetryButton() {
        var action: BrowseAction? = null
        composeRule.setContent {
            ReaderParserTheme {
                BrowseContent(
                    state = BrowseUiState(
                        series = listOf(Series(sourceId = 1L, url = "url1", title = "Eleceed", type = ContentType.MANHWA)),
                        error = "Failed page 2",
                        retryAvailable = true,
                    ),
                    onAction = { action = it },
                )
            }
        }

        composeRule.onNodeWithTag("pagination_error").assertIsDisplayed()
        composeRule.onNodeWithText("Failed page 2").assertIsDisplayed()
        composeRule.onNodeWithTag("pagination_retry_button").assertIsDisplayed().performClick()
        assertThat(action).isEqualTo(BrowseAction.Retry)
    }

    @Test
    fun scrollToEnd_dispatchesLoadMoreAutomatically() {
        val actions = mutableListOf<BrowseAction>()
        val series = List(12) { index ->
            Series(sourceId = 1L, url = "url$index", title = "Series $index", type = ContentType.MANHWA)
        }

        composeRule.setContent {
            ReaderParserTheme {
                Box(modifier = Modifier.requiredWidth(400.dp).requiredHeight(800.dp)) {
                    BrowseContent(
                        state = BrowseUiState(series = series, hasNextPage = true),
                        onAction = actions::add,
                    )
                }
            }
        }

        assertThat(actions.any { it is BrowseAction.LoadMore }).isFalse()
        composeRule.onNodeWithTag("series_list").performScrollToIndex(series.lastIndex)
        composeRule.waitUntil(timeoutMillis = 5_000) {
            actions.any { it is BrowseAction.LoadMore }
        }
        assertThat(actions.any { it is BrowseAction.LoadMore }).isTrue()
    }

    @Test
    fun bottomNavBar_doesNotConstrainMaxHeightBelowItems() {
        val destinations = listOf(
            BottomNavDestination(
                route = "library",
                label = "Library",
                icon = Icons.Filled.CollectionsBookmark,
            ),
            BottomNavDestination(
                route = "browse",
                label = "Browse",
                icon = Icons.Filled.Explore,
            ),
            BottomNavDestination(
                route = "downloads",
                label = "Downloads",
                icon = Icons.Filled.Download,
            ),
        )

        composeRule.setContent {
            ReaderParserTheme {
                BottomNavBar(
                    destinations = destinations,
                    currentRoute = "browse",
                    onNavigate = {},
                )
            }
        }

        val navBar = composeRule.onNodeWithTag("bottom_nav_bar")
        navBar.assertIsDisplayed()

        val navBarBounds = navBar.fetchSemanticsNode().boundsInRoot
        val navBarHeightDp = with(composeRule.density) { navBarBounds.height.toDp() }
        assertThat(navBarHeightDp >= BottomNavBarHeight).isTrue()

        listOf("Library", "Browse", "Downloads").forEach { label ->
            val itemNode = composeRule.onNodeWithText(label)
            itemNode.assertIsDisplayed()
            val itemBounds = itemNode.fetchSemanticsNode().boundsInRoot
            assertThat(itemBounds.top >= navBarBounds.top).isTrue()
            assertThat(itemBounds.bottom <= navBarBounds.bottom).isTrue()
        }
    }
}
