package com.opus.readerparser.ui.browse

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import com.opus.readerparser.domain.model.ContentType
import com.opus.readerparser.domain.model.Series
import com.opus.readerparser.domain.model.SourceInfo
import com.opus.readerparser.ui.components.AppTopBar
import com.opus.readerparser.ui.components.EmptyStateView
import com.opus.readerparser.ui.components.ErrorRetryStateView
import com.opus.readerparser.ui.components.LoadingStateView
import com.opus.readerparser.ui.components.SearchInputPill
import com.opus.readerparser.ui.components.SegmentedModeTabs
import com.opus.readerparser.ui.components.SegmentedTabOption
import com.opus.readerparser.ui.components.SeriesCatalogGrid
import com.opus.readerparser.ui.components.SourcePickerSheet
import com.opus.readerparser.ui.components.SourceSelectorPill
import com.opus.readerparser.ui.theme.AppSpacing
import com.opus.readerparser.ui.theme.ReaderParserTheme

fun SourceInfo.displayLabel(): String {
    val typeLabel = type.name.lowercase().replaceFirstChar { it.uppercase() }
    return "$name (${lang.uppercase()}) · $typeLabel"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowseContent(
    state: BrowseUiState,
    onAction: (BrowseAction) -> Unit,
    modifier: Modifier = Modifier,
    onOpenSettings: () -> Unit = {},
    onNavigateToSettings: () -> Unit = onOpenSettings,
) {
    var showSourcePicker by remember { mutableStateOf(false) }
    val selectedSource = state.sources.find { it.id == state.selectedSourceId }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Browse",
                actions = {
                    SourceSelectorPill(
                        selectedSource = selectedSource,
                        onClick = { showSourcePicker = true },
                    )
                    IconButton(
                        onClick = onNavigateToSettings,
                        modifier = Modifier.testTag("settings_button"),
                    ) {
                        Icon(Icons.Filled.Settings, contentDescription = "Settings")
                    }
                },
            )
        },
    ) { innerPadding ->
        if (showSourcePicker) {
            SourcePickerSheet(
                sources = state.sources,
                selectedSourceId = state.selectedSourceId,
                onSelectSource = { sourceId ->
                    onAction(BrowseAction.SelectSource(sourceId))
                    showSourcePicker = false
                },
                onDismissRequest = { showSourcePicker = false },
            )
        }

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            Surface(
                color = MaterialTheme.colorScheme.surface,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = AppSpacing.lg, vertical = AppSpacing.sm),
                    verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
                ) {
                    SearchInputPill(
                        query = state.searchQuery,
                        onQueryChange = { onAction(BrowseAction.SetSearchQuery(it)) },
                        placeholder = "Search series, authors, tags...",
                        onSearch = { onAction(BrowseAction.Search) },
                        onClear = { onAction(BrowseAction.SetSearchQuery("")) },
                        modifier = Modifier.fillMaxWidth(),
                    )

                    val modeOptions = remember {
                        BrowseMode.entries.map { mode ->
                            SegmentedTabOption(
                                value = mode,
                                label = mode.displayLabel(),
                                testTag = "mode_tab_${mode.name.lowercase()}",
                            )
                        }
                    }
                    SegmentedModeTabs(
                        options = modeOptions,
                        selectedValue = state.mode,
                        onSelect = { mode -> onAction(BrowseAction.SetMode(mode)) },
                        modifier = Modifier.testTag("browse_mode_tabs"),
                    )
                }
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            ) {
                when {
                    state.isLoading && state.series.isEmpty() -> {
                        LoadingStateView(message = "Loading catalog...")
                    }
                    state.error != null && state.series.isEmpty() -> {
                        ErrorRetryStateView(
                            message = state.error,
                            onRetry = if (state.retryAvailable) ({ onAction(BrowseAction.Retry) }) else null,
                        )
                    }
                    state.series.isEmpty() -> {
                        val (emptyTitle, emptySubtitle) = when {
                            state.sources.isEmpty() -> "No sources available" to "Install or enable a source plugin to browse series."
                            state.mode == BrowseMode.SEARCH && state.searchQuery.isBlank() -> "Search catalog" to "Enter a title, author, or keyword above to search."
                            state.mode == BrowseMode.SEARCH -> "No results found" to "No series matched \"${state.searchQuery}\"."
                            else -> "No series found" to "This source returned an empty catalog."
                        }
                        EmptyStateView(
                            title = emptyTitle,
                            subtitle = emptySubtitle,
                        )
                    }
                    else -> {
                        val gridState = rememberLazyGridState()

                        LaunchedEffect(gridState, state.hasNextPage, state.isLoading, state.series.size, state.currentPage) {
                            if (!state.hasNextPage) return@LaunchedEffect

                            snapshotFlow {
                                val layoutInfo = gridState.layoutInfo
                                val lastVisibleIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
                                lastVisibleIndex to layoutInfo.totalItemsCount
                            }.collect { (lastVisibleIndex, totalItemsCount) ->
                                if (state.error == null && state.hasNextPage && !state.isLoading && totalItemsCount > 0 && lastVisibleIndex >= totalItemsCount - 2) {
                                    onAction(BrowseAction.LoadMore)
                                }
                            }
                        }

                        SeriesCatalogGrid(
                            series = state.series,
                            onSeriesClick = { series -> onAction(BrowseAction.OpenSeries(series)) },
                            inLibraryUrls = state.inLibraryUrls,
                            isSeriesInLibrary = state::isSeriesInLibrary,
                            canLoadMore = state.hasNextPage,
                            isLoadingMore = state.isLoading,
                            paginationError = if (state.retryAvailable) state.error else null,
                            onLoadMore = { onAction(BrowseAction.LoadMore) },
                            onRetryLoadMore = { onAction(BrowseAction.Retry) },
                            gridState = gridState,
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BrowseContentLoadingPreview() {
    ReaderParserTheme {
        BrowseContent(
            state = BrowseUiState(isLoading = true),
            onAction = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BrowseContentErrorPreview() {
    ReaderParserTheme {
        BrowseContent(
            state = BrowseUiState(error = "Network error", retryAvailable = true),
            onAction = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BrowseContentEmptyPreview() {
    ReaderParserTheme {
        BrowseContent(
            state = BrowseUiState(),
            onAction = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BrowseContentPopulatedPreview() {
    val sources = listOf(SourceInfo(id = 1L, name = "AsuraScans", lang = "en", type = ContentType.MANHWA))
    val series = listOf(
        Series(sourceId = 1L, url = "https://example.com/s1", title = "Reaper of the Drifting Moon", type = ContentType.MANHWA),
        Series(sourceId = 1L, url = "https://example.com/s2", title = "Eleceed", type = ContentType.MANHWA),
    )
    ReaderParserTheme {
        BrowseContent(
            state = BrowseUiState(sources = sources, selectedSourceId = 1L, series = series),
            onAction = {},
        )
    }
}
