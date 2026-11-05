package com.opus.readerparser.ui.browse

import com.opus.readerparser.domain.model.Series
import com.opus.readerparser.domain.model.SourceInfo

enum class BrowseMode { POPULAR, LATEST, SEARCH }

fun BrowseMode.displayLabel(): String = when (this) {
    BrowseMode.POPULAR -> "Popular"
    BrowseMode.LATEST -> "Latest"
    BrowseMode.SEARCH -> "Search"
}

data class BrowseUiState(
    val sources: List<SourceInfo> = emptyList(),
    val selectedSourceId: Long? = null,
    val mode: BrowseMode = BrowseMode.POPULAR,
    val series: List<Series> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val retryAvailable: Boolean = false,
    val hasNextPage: Boolean = false,
    val currentPage: Int = 1,
    val searchQuery: String = "",
    val inLibraryUrls: Set<String> = emptySet(),
    val inLibraryKeys: Set<Pair<Long, String>> = emptySet(),
) {
    fun isSeriesInLibrary(item: Series): Boolean =
        (item.sourceId to item.url) in inLibraryKeys || item.url in inLibraryUrls
}

sealed interface BrowseAction {
    data class SelectSource(val sourceId: Long) : BrowseAction
    data class SetMode(val mode: BrowseMode) : BrowseAction
    data object LoadMore : BrowseAction
    data class SetSearchQuery(val query: String) : BrowseAction
    data object Search : BrowseAction
    data object Retry : BrowseAction
    data class OpenSeries(val series: Series) : BrowseAction
}

sealed interface BrowseEffect {
    data class NavigateToSeries(val series: Series) : BrowseEffect
    data class ShowError(val message: String) : BrowseEffect
}
