package com.opus.readerparser.ui.library

import com.opus.readerparser.domain.model.Series
import com.opus.readerparser.domain.model.SeriesStatus

enum class LibraryFilter(val label: String) {
    ALL("All"),
    READING("Reading"),
    DOWNLOADED("Downloaded"),
    COMPLETED("Completed"),
}

enum class LibrarySortOption(val label: String) {
    RECENTLY_ADDED("Default (Recently Added)"),
    TITLE_ASC("Title (A–Z)");

    companion object {
        val DEFAULT: LibrarySortOption get() = RECENTLY_ADDED
        val TITLE: LibrarySortOption get() = TITLE_ASC
    }
}

typealias LibrarySortBy = LibrarySortOption

data class LibrarySeriesItem(
    val series: Series,
    val unreadCount: Int = 0,
    val downloadedCount: Int = 0,
    val currentChapterNumber: Float? = null,
    val totalChapters: Int = 0,
    val progressRatio: Float = 0f,
    val readCount: Int = (totalChapters - unreadCount).coerceAtLeast(0),
    val hasInProgressChapter: Boolean = progressRatio > 0f && progressRatio < 1f,
) {
    fun matchesFilter(filter: LibraryFilter): Boolean = when (filter) {
        LibraryFilter.ALL -> true
        LibraryFilter.READING ->
            (readCount > 0 || hasInProgressChapter) && (totalChapters == 0 || readCount < totalChapters)
        LibraryFilter.DOWNLOADED -> downloadedCount > 0
        LibraryFilter.COMPLETED ->
            (totalChapters > 0 && readCount == totalChapters) ||
                (totalChapters == 0 && series.status == SeriesStatus.COMPLETED)
    }
}

fun computeLibraryFilterCounts(items: List<LibrarySeriesItem>): Map<LibraryFilter, Int> =
    LibraryFilter.entries.associateWith { filter ->
        items.count { it.matchesFilter(filter) }
    }

data class LibraryUiState(
    val series: List<Series> = emptyList(),
    val items: List<LibrarySeriesItem> = series.map { LibrarySeriesItem(series = it) },
    val isLoading: Boolean = false,
    val error: String? = null,
    val sortBy: LibrarySortOption = LibrarySortOption.RECENTLY_ADDED,
    val searchQuery: String = "",
    val selectedFilter: LibraryFilter = LibraryFilter.ALL,
    val filterCounts: Map<LibraryFilter, Int> = computeLibraryFilterCounts(items),
) {
    val sortOption: LibrarySortOption get() = sortBy
}

sealed interface LibraryAction {
    data class OpenSeries(val series: Series) : LibraryAction
    data class RemoveFromLibrary(val series: Series) : LibraryAction
    data class SetSortBy(val sortBy: LibrarySortOption) : LibraryAction
    data class SetFilter(val filter: LibraryFilter) : LibraryAction
    data class SetSearchQuery(val query: String) : LibraryAction
}

sealed interface LibraryEffect {
    data class NavigateToSeries(val series: Series) : LibraryEffect
    data class ShowError(val message: String) : LibraryEffect
}
