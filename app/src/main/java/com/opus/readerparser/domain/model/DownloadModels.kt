package com.opus.readerparser.domain.model

const val DEFAULT_AVAILABLE_STORAGE_BYTES: Long = 4_200_000_000L

data class DownloadStorageBreakdown(
    val novelTextBytes: Long = 0L,
    val manhwaImageBytes: Long = 0L,
    val availableBytes: Long = DEFAULT_AVAILABLE_STORAGE_BYTES,
) {
    val usedBytes: Long get() = (novelTextBytes + manhwaImageBytes).coerceAtLeast(0L)
}

data class DownloadedSeriesItem(
    val sourceId: Long = 0L,
    val seriesUrl: String = "",
    val seriesTitle: String,
    val contentType: ContentType = ContentType.NOVEL,
    val downloadedChapterCount: Int = 0,
    val chapterRangeSummary: String = "",
    val totalBytes: Long = 0L,
    val seriesId: String = if (sourceId != 0L) "$sourceId:$seriesUrl" else seriesUrl,
)
