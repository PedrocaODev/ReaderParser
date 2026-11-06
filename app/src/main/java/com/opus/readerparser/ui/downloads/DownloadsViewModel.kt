package com.opus.readerparser.ui.downloads

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.opus.readerparser.domain.ChapterRepository
import com.opus.readerparser.domain.DownloadRepository
import com.opus.readerparser.domain.SeriesRepository
import com.opus.readerparser.domain.model.ContentType
import com.opus.readerparser.domain.model.DownloadItem
import com.opus.readerparser.domain.model.DownloadState
import com.opus.readerparser.domain.model.DownloadedSeriesItem
import com.opus.readerparser.domain.model.Series
import com.opus.readerparser.domain.model.ChapterWithState
import com.opus.readerparser.ui.components.formatChapterRangeSummary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
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

    private data class StorageSnapshotKey(
        val queueStates: List<Triple<Long, String, DownloadState>>,
        val downloadedSeriesChapters: List<Triple<Long, String, List<String>>>,
    )

    private var lastStorageSnapshotKey: StorageSnapshotKey? = null
    private var lastRawSeriesWithChapters: List<Pair<Series, List<ChapterWithState>>>? = null
    private var cachedDownloadedSeriesChapters: List<Triple<Long, String, List<String>>> = emptyList()

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

                if (seriesWithChapters !== lastRawSeriesWithChapters) {
                    cachedDownloadedSeriesChapters = seriesWithChapters.mapNotNull { (series, chapters) ->
                        val downloadedUrls = chapters.filter { it.downloaded }.map { it.chapter.url }
                        if (downloadedUrls.isNotEmpty()) {
                            Triple(series.sourceId, series.url, downloadedUrls)
                        } else {
                            null
                        }
                    }
                    lastRawSeriesWithChapters = seriesWithChapters
                }

                val snapshotKey = StorageSnapshotKey(
                    queueStates = queue.map { Triple(it.sourceId, it.chapterUrl, it.state) },
                    downloadedSeriesChapters = cachedDownloadedSeriesChapters,
                )

                if (snapshotKey == lastStorageSnapshotKey) {
                    _state.update { it.copy(downloads = queue) }
                    return@collect
                }
                lastStorageSnapshotKey = snapshotKey

                val groupedSeries = mutableListOf<DownloadedSeriesItem>()
                val handledTitles = mutableSetOf<Pair<Long, String>>()
                val handledUrls = mutableSetOf<Pair<Long, String>>()

                for ((series, chapters) in seriesWithChapters) {
                    val downloaded = chapters.filter { it.downloaded }
                    if (downloaded.isNotEmpty()) {
                        val chapterNumbers = downloaded.map { it.chapter.number }
                        val seriesBytes = try {
                            downloadRepository.getSeriesBytes(series.sourceId, series.url)
                        } catch (_: Exception) {
                            0L
                        }
                        groupedSeries.add(
                            DownloadedSeriesItem(
                                sourceId = series.sourceId,
                                seriesUrl = series.url,
                                seriesTitle = series.title,
                                contentType = series.type,
                                downloadedChapterCount = downloaded.size,
                                chapterRangeSummary = formatChapterRangeSummary(downloaded.size, chapterNumbers),
                                totalBytes = seriesBytes,
                            ),
                        )
                        handledTitles.add(series.sourceId to series.title)
                        handledUrls.add(series.sourceId to series.url)
                        for (ch in chapters) {
                            handledUrls.add(ch.chapter.sourceId to ch.chapter.url)
                        }
                    }
                }

                val completedQueueItems = queue.filter { it.state == DownloadState.COMPLETED }
                val fallbackItems = completedQueueItems.filter { item ->
                    item.seriesTitle.isNotBlank() &&
                        (item.sourceId to item.seriesTitle) !in handledTitles &&
                        (item.sourceId to item.chapterUrl) !in handledUrls
                }

                fallbackItems.groupBy { it.sourceId to it.seriesTitle }.forEach { (key, items) ->
                    val (sourceId, title) = key
                    val seriesBytes = try {
                        downloadRepository.getSeriesBytes(sourceId, title)
                    } catch (_: Exception) {
                        0L
                    }
                    groupedSeries.add(
                        DownloadedSeriesItem(
                            sourceId = sourceId,
                            seriesUrl = title,
                            seriesTitle = title,
                            contentType = ContentType.NOVEL,
                            downloadedChapterCount = items.size,
                            chapterRangeSummary = formatChapterRangeSummary(items.size, emptyList()),
                            totalBytes = seriesBytes,
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
            is DownloadsAction.Cancel -> performQueueMutation("Failed to cancel download") {
                downloadRepository.cancel(action.sourceId, action.chapterUrl)
            }
            is DownloadsAction.Retry -> performQueueMutation("Failed to retry download") {
                downloadRepository.retry(action.sourceId, action.chapterUrl)
            }
            is DownloadsAction.Delete -> performQueueMutation("Failed to delete download") {
                downloadRepository.deleteDownload(action.sourceId, action.chapterUrl)
            }
            is DownloadsAction.ClearSeriesDownloads -> clearSeriesDownloads(action.sourceId, action.seriesUrl)
            is DownloadsAction.RetryLoad -> viewModelScope.launch {
                refreshStorageBreakdown()
            }
        }
    }

    private fun performQueueMutation(defaultError: String, block: suspend () -> Unit) {
        viewModelScope.launch {
            val snapshotBefore = lastStorageSnapshotKey
            try {
                block()
                if (lastStorageSnapshotKey == snapshotBefore) {
                    refreshStorageBreakdown()
                }
            } catch (e: Exception) {
                _effects.send(DownloadsEffect.ShowError(e.message ?: defaultError))
            }
        }
    }

    fun clearSeriesDownloads(seriesId: String) = clearSeriesDownloads(0L, seriesId)

    fun clearSeriesDownloads(sourceId: Long, seriesUrl: String) {
        viewModelScope.launch {
            try {
                val matchedSeries = currentSeriesWithChapters.filter { (series, _) ->
                    if (sourceId != 0L) {
                        series.sourceId == sourceId && series.url == seriesUrl
                    } else {
                        series.url == seriesUrl || "${series.sourceId}:${series.url}" == seriesUrl
                    }
                }

                for ((_, chapters) in matchedSeries) {
                    for (chState in chapters) {
                        if (chState.downloaded) {
                            downloadRepository.deleteDownload(chState.chapter.sourceId, chState.chapter.url)
                            chapterRepository.markDownloaded(chState.chapter, false)
                        }
                    }
                }

                val matchingChapterKeys = matchedSeries.flatMap { (series, chapters) ->
                    chapters.map { series.sourceId to it.chapter.url }
                }.toSet()
                val matchedSeriesTitles = matchedSeries.map { (series, _) ->
                    series.sourceId to series.title
                }.toSet()

                val completedItemsToDelete = _state.value.downloads.filter { item ->
                    item.state == DownloadState.COMPLETED && (
                        (item.sourceId to item.chapterUrl) in matchingChapterKeys ||
                            (item.sourceId to item.seriesTitle) in matchedSeriesTitles ||
                            (matchedSeries.isEmpty() && (sourceId == 0L || item.sourceId == sourceId) && item.seriesTitle == seriesUrl)
                    )
                }

                for (item in completedItemsToDelete) {
                    downloadRepository.deleteDownload(item.sourceId, item.chapterUrl)
                }

                val breakdown = downloadRepository.getStorageBreakdown()
                _state.update { current ->
                    current.copy(
                        downloadedSeries = current.downloadedSeries.filterNot {
                            if (sourceId != 0L) {
                                it.sourceId == sourceId && it.seriesUrl == seriesUrl
                            } else {
                                it.seriesUrl == seriesUrl || it.seriesId == seriesUrl
                            }
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
            val updatedSeries = _state.value.downloadedSeries.map { item ->
                val bytes = try {
                    downloadRepository.getSeriesBytes(item.sourceId, item.seriesUrl)
                } catch (_: Exception) {
                    item.totalBytes
                }
                item.copy(totalBytes = bytes)
            }
            _state.update {
                it.copy(
                    downloadedSeries = updatedSeries,
                    novelTextBytes = breakdown.novelTextBytes,
                    manhwaImageBytes = breakdown.manhwaImageBytes,
                    availableBytes = breakdown.availableBytes,
                )
            }
        } catch (_: Exception) {}
    }
}

