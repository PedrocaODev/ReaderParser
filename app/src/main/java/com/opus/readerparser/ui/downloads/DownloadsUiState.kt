package com.opus.readerparser.ui.downloads

import com.opus.readerparser.domain.model.DEFAULT_AVAILABLE_STORAGE_BYTES
import com.opus.readerparser.domain.model.DownloadItem

typealias DownloadedSeriesItem = com.opus.readerparser.domain.model.DownloadedSeriesItem
typealias DownloadStorageBreakdown = com.opus.readerparser.domain.model.DownloadStorageBreakdown

data class DownloadsUiState(
    val downloads: List<DownloadItem> = emptyList(),
    val downloadedSeries: List<DownloadedSeriesItem> = emptyList(),
    val novelTextBytes: Long = 0L,
    val manhwaImageBytes: Long = 0L,
    val availableBytes: Long = DEFAULT_AVAILABLE_STORAGE_BYTES,
    val isLoading: Boolean = false,
    val error: String? = null,
) {
    val storageBreakdown: DownloadStorageBreakdown
        get() = DownloadStorageBreakdown(
            novelTextBytes = novelTextBytes,
            manhwaImageBytes = manhwaImageBytes,
            availableBytes = availableBytes,
        )
}

sealed interface DownloadsAction {
    data class Cancel(val sourceId: Long, val chapterUrl: String) : DownloadsAction
    data class Retry(val sourceId: Long, val chapterUrl: String) : DownloadsAction
    data class Delete(val sourceId: Long, val chapterUrl: String) : DownloadsAction
    data class ClearSeriesDownloads(val seriesId: String) : DownloadsAction
    data object RetryLoad : DownloadsAction
}

sealed interface DownloadsEffect {
    data class ShowError(val message: String) : DownloadsEffect
}
