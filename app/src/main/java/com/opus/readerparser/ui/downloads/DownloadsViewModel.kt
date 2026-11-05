package com.opus.readerparser.ui.downloads

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.opus.readerparser.domain.ChapterRepository
import com.opus.readerparser.domain.DownloadRepository
import com.opus.readerparser.domain.SeriesRepository
import com.opus.readerparser.domain.model.Chapter
import com.opus.readerparser.domain.model.ChapterContent
import com.opus.readerparser.domain.model.ChapterWithState
import com.opus.readerparser.domain.model.ContentType
import com.opus.readerparser.domain.model.DownloadItem
import com.opus.readerparser.domain.model.DownloadState
import com.opus.readerparser.domain.model.DownloadedSeriesItem
import com.opus.readerparser.domain.model.FilterList
import com.opus.readerparser.domain.model.LibrarySearchResult
import com.opus.readerparser.domain.model.Series
import com.opus.readerparser.domain.model.SeriesPage
import com.opus.readerparser.domain.model.formatChapterRangeSummary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class DownloadsViewModel @Inject constructor(
    private val downloadRepository: DownloadRepository,
    private val seriesRepository: SeriesRepository,
    private val chapterRepository: ChapterRepository,
) : ViewModel() {

    constructor(
        downloadRepository: DownloadRepository,
    ) : this(
        downloadRepository = downloadRepository,
        seriesRepository = NoOpSeriesRepository,
        chapterRepository = NoOpChapterRepository,
    )

    private val _state = MutableStateFlow(DownloadsUiState())
    val state: StateFlow<DownloadsUiState> = _state.asStateFlow()

    private val _effects = Channel<DownloadsEffect>(Channel.BUFFERED)
    val effects: Flow<DownloadsEffect> = _effects.receiveAsFlow()

    private var currentSeriesWithChapters: List<Pair<Series, List<ChapterWithState>>> = emptyList()

    init {
        viewModelScope.launch {
            val librarySeriesChaptersFlow: Flow<List<Pair<Series, List<ChapterWithState>>>> =
                seriesRepository.observeLibrary().flatMapLatest { seriesList ->
                    if (seriesList.isEmpty()) {
                        flowOf(emptyList())
                    } else {
                        combine(
                            seriesList.map { series ->
                                chapterRepository.observeChapters(series).map { chapters ->
                                    series to chapters
                                }
                            },
                        ) { it.toList() }
                    }
                }

            combine(
                downloadRepository.observeQueue(),
                librarySeriesChaptersFlow,
            ) { queue, seriesWithChapters ->
                queue to seriesWithChapters
            }.collect { (queue, seriesWithChapters) ->
                currentSeriesWithChapters = seriesWithChapters

                val groupedSeries = mutableListOf<DownloadedSeriesItem>()
                val handledTitles = mutableSetOf<String>()
                val handledUrls = mutableSetOf<String>()

                for ((series, chapters) in seriesWithChapters) {
                    val downloaded = chapters.filter { it.downloaded }
                    if (downloaded.isNotEmpty()) {
                        val chapterNumbers = downloaded.map { it.chapter.number }
                        groupedSeries.add(
                            DownloadedSeriesItem(
                                seriesId = series.url,
                                seriesTitle = series.title,
                                contentType = series.type,
                                downloadedChapterCount = downloaded.size,
                                chapterRangeSummary = formatChapterRangeSummary(downloaded.size, chapterNumbers),
                                totalBytes = 0L,
                                sourceId = series.sourceId,
                                seriesUrl = series.url,
                            ),
                        )
                        handledTitles.add(series.title)
                        handledUrls.add(series.url)
                        for (ch in chapters) {
                            handledUrls.add(ch.chapter.url)
                        }
                    }
                }

                val completedQueueItems = queue.filter { it.state == DownloadState.COMPLETED }
                val fallbackItems = completedQueueItems.filter { item ->
                    item.seriesTitle.isNotBlank() &&
                        item.seriesTitle !in handledTitles &&
                        item.chapterUrl !in handledUrls
                }

                fallbackItems.groupBy { it.seriesTitle }.forEach { (title, items) ->
                    groupedSeries.add(
                        DownloadedSeriesItem(
                            seriesId = title,
                            seriesTitle = title,
                            contentType = ContentType.NOVEL,
                            downloadedChapterCount = items.size,
                            chapterRangeSummary = formatChapterRangeSummary(items.size, emptyList()),
                            totalBytes = 0L,
                            sourceId = items.first().sourceId,
                            seriesUrl = title,
                        ),
                    )
                }

                val breakdown = try {
                    downloadRepository.getStorageBreakdown()
                } catch (_: Exception) {
                    _state.value.storageBreakdown
                }

                _state.update {
                    it.copy(
                        downloads = queue,
                        downloadedSeries = groupedSeries,
                        novelTextBytes = breakdown.novelTextBytes,
                        manhwaImageBytes = breakdown.manhwaImageBytes,
                        availableBytes = breakdown.availableBytes,
                    )
                }
            }
        }
    }

    fun onAction(action: DownloadsAction) {
        when (action) {
            is DownloadsAction.Cancel -> viewModelScope.launch {
                try {
                    downloadRepository.cancel(action.sourceId, action.chapterUrl)
                    refreshStorageBreakdown()
                } catch (e: Exception) {
                    _effects.send(DownloadsEffect.ShowError(e.message ?: "Failed to cancel download"))
                }
            }
            is DownloadsAction.Retry -> viewModelScope.launch {
                try {
                    downloadRepository.retry(action.sourceId, action.chapterUrl)
                    refreshStorageBreakdown()
                } catch (e: Exception) {
                    _effects.send(DownloadsEffect.ShowError(e.message ?: "Failed to retry download"))
                }
            }
            is DownloadsAction.Delete -> viewModelScope.launch {
                try {
                    downloadRepository.deleteDownload(action.sourceId, action.chapterUrl)
                    refreshStorageBreakdown()
                } catch (e: Exception) {
                    _effects.send(DownloadsEffect.ShowError(e.message ?: "Failed to delete download"))
                }
            }
            is DownloadsAction.ClearSeriesDownloads -> clearSeriesDownloads(action.seriesId)
            is DownloadsAction.RetryLoad -> viewModelScope.launch {
                refreshStorageBreakdown()
            }
        }
    }

    fun clearSeriesDownloads(seriesId: String) {
        viewModelScope.launch {
            try {
                val matchedSeries = currentSeriesWithChapters.filter { (series, _) ->
                    series.url == seriesId || "${series.sourceId}:${series.url}" == seriesId || series.title == seriesId
                }

                for ((_, chapters) in matchedSeries) {
                    for (chState in chapters) {
                        if (chState.downloaded) {
                            downloadRepository.deleteDownload(chState.chapter.sourceId, chState.chapter.url)
                            chapterRepository.markDownloaded(chState.chapter, false)
                        }
                    }
                }

                val matchingChapterUrls = matchedSeries.flatMap { (_, chapters) ->
                    chapters.map { it.chapter.url }
                }.toSet()

                val completedItemsToDelete = _state.value.downloads.filter { item ->
                    item.state == DownloadState.COMPLETED && (
                        item.seriesTitle == seriesId ||
                            item.chapterUrl == seriesId ||
                            item.chapterUrl in matchingChapterUrls
                    )
                }

                for (item in completedItemsToDelete) {
                    downloadRepository.deleteDownload(item.sourceId, item.chapterUrl)
                }

                val breakdown = downloadRepository.getStorageBreakdown()
                _state.update { current ->
                    current.copy(
                        downloadedSeries = current.downloadedSeries.filterNot {
                            it.seriesId == seriesId || it.seriesTitle == seriesId || it.seriesUrl == seriesId
                        },
                        novelTextBytes = breakdown.novelTextBytes,
                        manhwaImageBytes = breakdown.manhwaImageBytes,
                        availableBytes = breakdown.availableBytes,
                    )
                }
            } catch (e: Exception) {
                _effects.send(DownloadsEffect.ShowError(e.message ?: "Failed to clear series downloads"))
            }
        }
    }

    private suspend fun refreshStorageBreakdown() {
        try {
            val breakdown = downloadRepository.getStorageBreakdown()
            _state.update {
                it.copy(
                    novelTextBytes = breakdown.novelTextBytes,
                    manhwaImageBytes = breakdown.manhwaImageBytes,
                    availableBytes = breakdown.availableBytes,
                )
            }
        } catch (_: Exception) {}
    }
}

private object NoOpSeriesRepository : SeriesRepository {
    override fun observeLibrary(): Flow<List<Series>> = flowOf(emptyList())
    override fun observeLibrarySearchInvalidations(): Flow<Unit> = emptyFlow()
    override suspend fun fetchPopular(sourceId: Long, page: Int): SeriesPage = SeriesPage(emptyList(), false)
    override suspend fun fetchLatest(sourceId: Long, page: Int): SeriesPage = SeriesPage(emptyList(), false)
    override suspend fun search(sourceId: Long, query: String, page: Int, filters: FilterList): SeriesPage = SeriesPage(emptyList(), false)
    override suspend fun searchLibrary(query: String): LibrarySearchResult = LibrarySearchResult.Success(emptyList())
    override suspend fun refreshDetails(series: Series): Series = series
    override suspend fun getPersistedSeries(sourceId: Long, url: String): Series? = null
    override suspend fun addToLibrary(series: Series) {}
    override suspend fun removeFromLibrary(series: Series) {}
    override suspend fun isInLibrary(sourceId: Long, url: String): Boolean = false
}

private object NoOpChapterRepository : ChapterRepository {
    override fun observeChapters(series: Series): Flow<List<ChapterWithState>> = flowOf(emptyList())
    override suspend fun refreshChapters(series: Series) {}
    override suspend fun findByUrl(sourceId: Long, url: String): Chapter? = null
    override suspend fun getContent(chapter: Chapter, forceNetwork: Boolean): ChapterContent =
        ChapterContent.Text("")
    override suspend fun markRead(chapter: Chapter, read: Boolean) {}
    override suspend fun setProgress(chapter: Chapter, progress: Float) {}
    override suspend fun markDownloaded(chapter: Chapter, downloaded: Boolean) {}
}
