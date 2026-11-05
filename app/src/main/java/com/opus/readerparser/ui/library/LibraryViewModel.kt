package com.opus.readerparser.ui.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.opus.readerparser.domain.ChapterRepository
import com.opus.readerparser.domain.SeriesRepository
import com.opus.readerparser.domain.model.Chapter
import com.opus.readerparser.domain.model.ChapterContent
import com.opus.readerparser.domain.model.ChapterWithState
import com.opus.readerparser.domain.model.LibrarySearchResult
import com.opus.readerparser.domain.model.Series
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val seriesRepository: SeriesRepository,
    private val chapterRepository: ChapterRepository,
) : ViewModel() {

    constructor(seriesRepository: SeriesRepository) : this(
        seriesRepository = seriesRepository,
        chapterRepository = EmptyChapterRepository,
    )

    private val _state = MutableStateFlow(LibraryUiState())
    val state: StateFlow<LibraryUiState> = _state.asStateFlow()

    private val _effects = Channel<LibraryEffect>(Channel.BUFFERED)
    val effects: Flow<LibraryEffect> = _effects.receiveAsFlow()

    private var allLibraryItems: List<LibrarySeriesItem> = emptyList()
    private var activeSearchSeries: List<Series>? = null
    private var searchGeneration: Long = 0
    private var searchJob: Job? = null
    private var debounceJob: Job? = null
    private val attemptedRepairs = mutableSetOf<Pair<Long, String>>()

    init {
        viewModelScope.launch {
            seriesRepository.observeLibrary()
                .onEach { library ->
                    // Repair blank bookmarked titles once per lifecycle
                    val blankBooks = library.filter { it.title.isBlank() }
                    for (blankBook in blankBooks) {
                        val key = blankBook.sourceId to blankBook.url
                        if (key !in attemptedRepairs) {
                            attemptedRepairs.add(key)
                            launch {
                                try {
                                    seriesRepository.refreshDetails(blankBook)
                                } catch (_: Exception) {
                                    // Swallow — bookmark stays unchanged; retry next lifecycle
                                }
                            }
                        }
                    }
                }
                .flatMapLatest { library ->
                    if (library.isEmpty()) {
                        flowOf(emptyList())
                    } else {
                        val flows = library.map { series ->
                            chapterRepository.observeChapters(series).map { chapters ->
                                buildLibrarySeriesItem(series, chapters)
                            }
                        }
                        combine(flows) { it.toList() }
                    }
                }
                .collect { items ->
                    allLibraryItems = items
                    updateDisplayedItems()
                }
        }

        viewModelScope.launch {
            seriesRepository.observeLibrarySearchInvalidations().collect {
                val currentQuery = _state.value.searchQuery.trim()
                if (currentQuery.isNotBlank()) {
                    val requestId = ++searchGeneration
                    refreshSearch(currentQuery, requestId, showLoading = false)
                }
            }
        }
    }

    fun onAction(action: LibraryAction) {
        when (action) {
            is LibraryAction.OpenSeries -> viewModelScope.launch {
                _effects.send(LibraryEffect.NavigateToSeries(action.series))
            }
            is LibraryAction.RemoveFromLibrary -> viewModelScope.launch {
                try {
                    seriesRepository.removeFromLibrary(action.series)
                } catch (e: Exception) {
                    _effects.send(LibraryEffect.ShowError(e.message ?: "Failed to remove from library"))
                }
            }
            is LibraryAction.SetSortBy -> updateDisplayedItems(sortBy = action.sortBy)
            is LibraryAction.SetFilter -> updateDisplayedItems(selectedFilter = action.filter)
            is LibraryAction.SetSearchQuery -> handleSearchQuery(action.query)
        }
    }

    private fun handleSearchQuery(query: String) {
        debounceJob?.cancel()
        val trimmedQuery = query.trim()

        if (trimmedQuery.isBlank()) {
            searchJob?.cancel()
            activeSearchSeries = null
            _state.update { current ->
                current.copy(
                    searchQuery = query,
                    isLoading = false,
                    error = null,
                )
            }
            updateDisplayedItems()
            return
        }

        _state.update { current ->
            current.copy(searchQuery = query)
        }

        debounceJob = viewModelScope.launch {
            delay(300)
            if (_state.value.searchQuery.trim() == trimmedQuery) {
                val requestId = ++searchGeneration
                refreshSearch(trimmedQuery, requestId, showLoading = true)
            }
        }
    }

    private fun refreshSearch(query: String, requestId: Long, showLoading: Boolean) {
        if (showLoading) {
            _state.update { current -> current.copy(isLoading = true, error = null) }
        }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            try {
                when (val result = seriesRepository.searchLibrary(query)) {
                    is LibrarySearchResult.Success -> if (requestId == searchGeneration && _state.value.searchQuery.trim() == query) {
                        activeSearchSeries = result.series
                        applySearchItems(result.series)
                    }
                    is LibrarySearchResult.Failure -> if (requestId == searchGeneration && _state.value.searchQuery.trim() == query) {
                        activeSearchSeries = emptyList()
                        val filterCounts = computeLibraryFilterCounts(allLibraryItems)
                        _state.update { current ->
                            current.copy(
                                isLoading = false,
                                error = result.message,
                                items = emptyList(),
                                series = emptyList(),
                                filterCounts = filterCounts,
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                if (requestId == searchGeneration && _state.value.searchQuery.trim() == query) {
                    activeSearchSeries = emptyList()
                    val filterCounts = computeLibraryFilterCounts(allLibraryItems)
                    _state.update { current ->
                        current.copy(
                            isLoading = false,
                            error = e.message ?: "Failed to search library",
                            items = emptyList(),
                            series = emptyList(),
                            filterCounts = filterCounts,
                        )
                    }
                }
            }
        }
    }

    private fun applySearchItems(
        searchSeriesList: List<Series>,
        sortBy: LibrarySortOption = _state.value.sortBy,
        selectedFilter: LibraryFilter = _state.value.selectedFilter,
    ) {
        val filterCounts = computeLibraryFilterCounts(allLibraryItems)

        val mapped = searchSeriesList.map { searchSeries ->
            val existing = allLibraryItems.firstOrNull {
                it.series.sourceId == searchSeries.sourceId && it.series.url == searchSeries.url
            }
            existing?.copy(series = searchSeries) ?: LibrarySeriesItem(series = searchSeries)
        }
        val filtered = mapped.filter { it.matchesFilter(selectedFilter) }
        val sorted = when (sortBy) {
            LibrarySortOption.TITLE_ASC -> filtered.sortedBy { it.series.title }
            LibrarySortOption.RECENTLY_ADDED -> filtered
        }

        _state.update { current ->
            current.copy(
                isLoading = false,
                error = null,
                sortBy = sortBy,
                selectedFilter = selectedFilter,
                items = sorted,
                series = sorted.map { it.series },
                filterCounts = filterCounts,
            )
        }
    }

    private fun updateDisplayedItems(
        sortBy: LibrarySortOption = _state.value.sortBy,
        selectedFilter: LibraryFilter = _state.value.selectedFilter,
    ) {
        val filterCounts = computeLibraryFilterCounts(allLibraryItems)
        val query = _state.value.searchQuery.trim()

        if (query.isBlank()) {
            val filtered = allLibraryItems.filter { it.matchesFilter(selectedFilter) }
            val sorted = when (sortBy) {
                LibrarySortOption.TITLE_ASC -> filtered.sortedBy { it.series.title }
                LibrarySortOption.RECENTLY_ADDED -> filtered
            }
            _state.update { current ->
                current.copy(
                    sortBy = sortBy,
                    selectedFilter = selectedFilter,
                    items = sorted,
                    series = sorted.map { it.series },
                    filterCounts = filterCounts,
                )
            }
        } else {
            val searchList = activeSearchSeries
            if (searchList != null) {
                applySearchItems(searchList, sortBy = sortBy, selectedFilter = selectedFilter)
            } else {
                _state.update { current ->
                    current.copy(
                        sortBy = sortBy,
                        selectedFilter = selectedFilter,
                        filterCounts = filterCounts,
                    )
                }
            }
        }
    }

    private fun buildLibrarySeriesItem(
        series: Series,
        chapters: List<ChapterWithState>,
    ): LibrarySeriesItem {
        val totalChapters = chapters.size
        val readCount = chapters.count { it.read }
        val unreadCount = (totalChapters - readCount).coerceAtLeast(0)
        val downloadedCount = chapters.count { it.downloaded }
        val hasInProgressChapter = chapters.any { !it.read && it.progress > 0f }
        val progressRatio = if (totalChapters > 0) {
            (readCount.toFloat() / totalChapters.toFloat()).coerceIn(0f, 1f)
        } else 0f
        val currentChapterNumber = chapters.filter { it.read || it.progress > 0f }
            .maxByOrNull { it.chapter.number }?.chapter?.number
            ?: chapters.minByOrNull { it.chapter.number }?.chapter?.number

        return LibrarySeriesItem(
            series = series,
            unreadCount = unreadCount,
            downloadedCount = downloadedCount,
            currentChapterNumber = currentChapterNumber,
            totalChapters = totalChapters,
            progressRatio = progressRatio,
            readCount = readCount,
            hasInProgressChapter = hasInProgressChapter,
        )
    }

    private object EmptyChapterRepository : ChapterRepository {
        override fun observeChapters(series: Series): Flow<List<ChapterWithState>> =
            flowOf(emptyList())

        override suspend fun refreshChapters(series: Series) {}
        override suspend fun findByUrl(sourceId: Long, url: String): Chapter? = null
        override suspend fun getContent(chapter: Chapter, forceNetwork: Boolean): ChapterContent =
            ChapterContent.Text("")

        override suspend fun markRead(chapter: Chapter, read: Boolean) {}
        override suspend fun markPreviousAsRead(series: Series, targetChapterNumber: Float) {}
        override suspend fun markAllRead(series: Series, read: Boolean) {}
        override suspend fun setProgress(chapter: Chapter, progress: Float) {}
        override suspend fun markDownloaded(chapter: Chapter, downloaded: Boolean) {}
    }
}
