package com.opus.readerparser.ui.library

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.opus.readerparser.domain.model.ContentType
import com.opus.readerparser.domain.model.Series
import com.opus.readerparser.ui.components.AppTopBar
import com.opus.readerparser.ui.components.ChipFilterRow
import com.opus.readerparser.ui.components.EmptyStateView
import com.opus.readerparser.ui.components.ErrorRetryStateView
import com.opus.readerparser.ui.components.FilterChipOption
import com.opus.readerparser.ui.components.LoadingStateView
import com.opus.readerparser.ui.components.RadioRow
import com.opus.readerparser.ui.components.SearchInputPill
import com.opus.readerparser.ui.theme.AppSpacing
import com.opus.readerparser.ui.theme.ReaderParserTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryContent(
    state: LibraryUiState,
    onAction: (LibraryAction) -> Unit,
    modifier: Modifier = Modifier,
    onNavigateToSettings: () -> Unit = {},
    onOpenSettings: () -> Unit = onNavigateToSettings,
    onBrowseSources: (() -> Unit)? = null,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    var showSortSheet by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        topBar = {
            AppTopBar(
                title = "Library",
                actions = {
                    IconButton(
                        onClick = { showSortSheet = true },
                        modifier = Modifier.testTag("sort_button"),
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Sort, contentDescription = "Sort")
                    }
                    IconButton(
                        onClick = onOpenSettings,
                        modifier = Modifier.testTag("settings_button"),
                    ) {
                        Icon(Icons.Filled.Settings, contentDescription = "Settings")
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            SearchInputPill(
                query = state.searchQuery,
                onQueryChange = { onAction(LibraryAction.SetSearchQuery(it)) },
                placeholder = "Search library...",
                onSearch = { onAction(LibraryAction.SetSearchQuery(state.searchQuery)) },
                onClear = { onAction(LibraryAction.SetSearchQuery("")) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AppSpacing.md, vertical = AppSpacing.xs),
            )

            val filterOptions = remember(state.filterCounts) {
                LibraryFilter.entries.map { filter ->
                    FilterChipOption(
                        value = filter,
                        label = filter.label,
                        count = state.filterCounts[filter] ?: 0,
                        testTag = "filter_chip_${filter.name.lowercase()}",
                    )
                }
            }
            ChipFilterRow(
                options = filterOptions,
                selectedValue = state.selectedFilter,
                onSelect = { onAction(LibraryAction.SetFilter(it)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AppSpacing.md, vertical = AppSpacing.xs)
                    .testTag("library_filter_row"),
            )

            Box(modifier = Modifier.weight(1f)) {
                val displayItems = if (state.items.isNotEmpty() || state.series.isEmpty()) {
                    state.items
                } else {
                    state.series.map { LibrarySeriesItem(series = it) }
                }

                when {
                    state.isLoading -> {
                        LoadingStateView()
                    }
                    state.error != null -> {
                        ErrorRetryStateView(
                            message = state.error,
                            onRetry = { onAction(LibraryAction.SetSearchQuery(state.searchQuery)) },
                        )
                    }
                    displayItems.isEmpty() -> {
                        if (state.searchQuery.isNotBlank() || state.selectedFilter != LibraryFilter.ALL) {
                            EmptyStateView(
                                title = "No matching series",
                                subtitle = if (state.searchQuery.isNotBlank()) {
                                    "No series match \"${state.searchQuery}\"."
                                } else {
                                    "No series match the selected filter."
                                },
                                actionLabel = "Clear Filters",
                                onActionClick = {
                                    if (state.searchQuery.isNotBlank()) onAction(LibraryAction.SetSearchQuery(""))
                                    if (state.selectedFilter != LibraryFilter.ALL) onAction(LibraryAction.SetFilter(LibraryFilter.ALL))
                                },
                            )
                        } else {
                            EmptyStateView(
                                title = "Your library is empty",
                                subtitle = "Browse sources to add series to your collection.",
                                actionLabel = if (onBrowseSources != null) "Browse Sources" else null,
                                onActionClick = onBrowseSources,
                            )
                        }
                    }
                    else -> {
                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(minSize = 104.dp),
                            contentPadding = PaddingValues(AppSpacing.md),
                            horizontalArrangement = Arrangement.spacedBy(AppSpacing.md),
                            verticalArrangement = Arrangement.spacedBy(AppSpacing.md),
                            modifier = Modifier
                                .fillMaxSize()
                                .testTag("series_list"),
                        ) {
                            items(
                                items = displayItems,
                                key = { "${it.series.sourceId}|${it.series.url}" },
                            ) { item ->
                                LibrarySeriesCard(
                                    item = item,
                                    onClick = { onAction(LibraryAction.OpenSeries(item.series)) },
                                    onLongClick = { onAction(LibraryAction.RemoveFromLibrary(item.series)) },
                                    modifier = Modifier.fillMaxWidth(),
                                )
                            }
                        }
                    }
                }
            }
        }

        if (showSortSheet) {
            ModalBottomSheet(
                onDismissRequest = { showSortSheet = false },
                modifier = Modifier.testTag("sort_bottom_sheet"),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = AppSpacing.xl),
                ) {
                    Text(
                        text = "Sort Library",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(horizontal = AppSpacing.lg, vertical = AppSpacing.sm),
                    )
                    RadioRow(
                        label = "Default (Recently Added)",
                        selected = state.sortBy == LibrarySortOption.RECENTLY_ADDED,
                        onClick = {
                            onAction(LibraryAction.SetSortBy(LibrarySortOption.RECENTLY_ADDED))
                            showSortSheet = false
                        },
                        modifier = Modifier
                            .padding(horizontal = AppSpacing.lg)
                            .testTag("sort_option_default"),
                    )
                    RadioRow(
                        label = "Title (A–Z)",
                        selected = state.sortBy == LibrarySortOption.TITLE_ASC,
                        onClick = {
                            onAction(LibraryAction.SetSortBy(LibrarySortOption.TITLE_ASC))
                            showSortSheet = false
                        },
                        modifier = Modifier
                            .padding(horizontal = AppSpacing.lg)
                            .testTag("sort_option_title"),
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LibraryContentLoadingPreview() {
    ReaderParserTheme {
        LibraryContent(
            state = LibraryUiState(isLoading = true),
            onAction = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LibraryContentErrorPreview() {
    ReaderParserTheme {
        LibraryContent(
            state = LibraryUiState(error = "Failed to load library"),
            onAction = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LibraryContentPopulatedPreview() {
    val sample = listOf(
        Series(sourceId = 1L, url = "https://example.com/s1", title = "The Wandering Inn", type = ContentType.NOVEL),
        Series(sourceId = 1L, url = "https://example.com/s2", title = "Solo Leveling", type = ContentType.MANHWA),
    )
    ReaderParserTheme {
        LibraryContent(
            state = LibraryUiState(series = sample),
            onAction = {},
        )
    }
}
