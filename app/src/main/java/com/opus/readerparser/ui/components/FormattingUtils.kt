package com.opus.readerparser.ui.components

import java.util.Locale
import kotlin.math.roundToLong

fun formatChapterNumber(number: Float): String =
    if (number % 1f == 0f) number.toInt().toString() else number.toString()

fun formatChapterRangeSummary(count: Int, chapterNumbers: List<Float>): String {
    val countLabel = if (count == 1) "1 chapter" else "$count chapters"
    var min = Float.POSITIVE_INFINITY
    var max = Float.NEGATIVE_INFINITY
    for (number in chapterNumbers) {
        if (number > 0f && number.isFinite()) {
            if (number < min) min = number
            if (number > max) max = number
        }
    }
    return when {
        !min.isFinite() -> countLabel
        min == max -> "$countLabel · Ch. ${formatChapterNumber(min)}"
        else -> "$countLabel · Ch. ${formatChapterNumber(min)}\u2013${formatChapterNumber(max)}"
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
        String.format(Locale.US, "%.1f %s", roundedTenths / 10.0, suffix)
    } else {
        "${roundedTenths / 10L} $suffix"
    }
}
