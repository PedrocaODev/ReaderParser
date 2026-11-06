package com.opus.readerparser.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.opus.readerparser.domain.model.Series

@Composable
fun SeriesCatalogGrid(
    series: List<Series>,
    onSeriesClick: (Series) -> Unit,
    modifier: Modifier = Modifier.testTag("series_list"),
    onSeriesLongClick: ((Series) -> Unit)? = null,
    inLibraryUrls: Set<String> = emptySet(),
    isSeriesInLibrary: ((Series) -> Boolean)? = null,
    canLoadMore: Boolean = false,
    isLoadingMore: Boolean = false,
    paginationError: String? = null,
    onLoadMore: () -> Unit = {},
    onRetryLoadMore: () -> Unit = {},
    gridState: LazyGridState? = null,
    content: LazyGridScope.() -> Unit = {},
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(SeriesCatalogGridMinSize),
        contentPadding = SeriesCatalogGridContentPadding,
        horizontalArrangement = Arrangement.spacedBy(SeriesCatalogGridItemSpacing),
        verticalArrangement = Arrangement.spacedBy(SeriesCatalogGridItemSpacing),
        state = gridState ?: rememberLazyGridState(),
        modifier = modifier,
    ) {
        items(series, key = { "${it.sourceId}|${it.url}" }) { item ->
            val inLibrary = isSeriesInLibrary?.invoke(item) ?: (item.url in inLibraryUrls)
            SeriesCard(
                series = item,
                onClick = { onSeriesClick(item) },
                onLongClick = onSeriesLongClick?.let { longClick -> { longClick(item) } },
                inLibrary = inLibrary,
            )
        }
        if (canLoadMore || isLoadingMore || paginationError != null) {
            item(
                key = "catalog_pagination_footer",
                span = { GridItemSpan(maxLineSpan) },
            ) {
                CatalogPaginationFooter(
                    canLoadMore = canLoadMore,
                    isLoadingMore = isLoadingMore,
                    paginationError = paginationError,
                    onLoadMore = onLoadMore,
                    onRetryLoadMore = onRetryLoadMore,
                )
            }
        }
        content()
    }
}
