package com.opus.readerparser.domain.model

import kotlin.math.roundToLong

const val DEFAULT_AVAILABLE_STORAGE_BYTES: Long = 4_200_000_000L

data class DownloadStorageBreakdown(
    val novelTextBytes: Long = 0L,
    val manhwaImageBytes: Long = 0L,
    val availableBytes: Long = DEFAULT_AVAILABLE_STORAGE_BYTES,
) {
    val usedBytes: Long get() = (novelTextBytes + manhwaImageBytes).coerceAtLeast(0L)
}

data class DownloadedSeriesItem(
    val seriesId: String,
    val seriesTitle: String,
    val contentType: ContentType = ContentType.NOVEL,
    val downloadedChapterCount: Int = 0,
    val chapterRangeSummary: String = formatChapterRangeSummary(downloadedChapterCount, emptyList()),
    val totalBytes: Long = 0L,
    val sourceId: Long = 0L,
    val seriesUrl: String = seriesId,
)

fun formatChapterNumber(number: Float): String =
    if (number % 1f == 0f) number.toInt().toString() else number.toString()

fun formatChapterRangeSummary(count: Int, chapterNumbers: List<Float>): String {
    val countLabel = if (count == 1) "1 chapter" else "$count chapters"
    val validNumbers = chapterNumbers.filter { it > 0f }.sorted()
    return when {
        validNumbers.isEmpty() -> countLabel
        validNumbers.first() == validNumbers.last() -> "$countLabel · Ch. ${formatChapterNumber(validNumbers.first())}"
        else -> "$countLabel · Ch. ${formatChapterNumber(validNumbers.first())}\u2013${formatChapterNumber(validNumbers.last())}"
    }
}

fun formatStorageBytes(bytes: Long): String {
    if (bytes <= 0L) return "0 MB"
    val useDecimal = (bytes % (1024L * 1024L) != 0L) && (bytes % 100_000L == 0L)
    val unit = if (useDecimal) 1000.0 else 1024.0
    val kb = unit
    val mb = unit * unit
    val gb = unit * unit * unit

    return when {
        bytes >= gb -> formatUnit(bytes / gb, "GB")
        bytes >= mb -> formatUnit(bytes / mb, "MB")
        bytes >= kb -> formatUnit(bytes / kb, "KB")
        else -> "$bytes B"
    }
}

private fun formatUnit(value: Double, suffix: String): String {
    val roundedTenths = (value * 10.0).roundToLong()
    return if (roundedTenths % 10L != 0L) {
        String.format(java.util.Locale.US, "%.1f %s", roundedTenths / 10.0, suffix)
    } else {
        "${roundedTenths / 10L} $suffix"
    }
}
