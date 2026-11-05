package com.opus.readerparser.ui.components

import org.junit.Assert.assertEquals
import org.junit.Test

class FormattingUtilsTest {

    @Test
    fun `formatChapterNumber formats integer numbers without decimals`() {
        assertEquals("1", formatChapterNumber(1f))
        assertEquals("12", formatChapterNumber(12.0f))
    }

    @Test
    fun `formatChapterNumber formats fractional numbers with decimals`() {
        assertEquals("12.5", formatChapterNumber(12.5f))
        assertEquals("0.5", formatChapterNumber(0.5f))
    }

    @Test
    fun `formatChapterRangeSummary handles empty list`() {
        assertEquals("0 chapters", formatChapterRangeSummary(0, emptyList()))
    }

    @Test
    fun `formatChapterRangeSummary handles single chapter`() {
        assertEquals("1 chapter · Ch. 5", formatChapterRangeSummary(1, listOf(5f)))
    }

    @Test
    fun `formatChapterRangeSummary handles unsorted multi-chapter list`() {
        assertEquals("42 chapters · Ch. 1\u201342", formatChapterRangeSummary(42, listOf(10f, 1f, 42f, 7.5f)))
    }

    @Test
    fun `formatChapterRangeSummary filters non-positive, NaN, and infinite numbers`() {
        assertEquals(
            "2 chapters · Ch. 3",
            formatChapterRangeSummary(2, listOf(-1f, 0f, Float.NaN, Float.POSITIVE_INFINITY, 3f, 3f)),
        )
        assertEquals(
            "3 chapters",
            formatChapterRangeSummary(3, listOf(-1f, 0f, Float.NaN)),
        )
    }

    @Test
    fun `formatStorageBytes handles zero and negative values`() {
        assertEquals("0 MB", formatStorageBytes(0L))
        assertEquals("0 MB", formatStorageBytes(-5L))
    }

    @Test
    fun `formatStorageBytes handles bytes`() {
        assertEquals("500 B", formatStorageBytes(500L))
    }

    @Test
    fun `formatStorageBytes handles kilobytes`() {
        assertEquals("2 KB", formatStorageBytes(2048L))
    }

    @Test
    fun `formatStorageBytes handles megabytes`() {
        assertEquals("142 MB", formatStorageBytes(142L * 1024L * 1024L))
        assertEquals("12.8 MB", formatStorageBytes(12_800_000L))
    }

    @Test
    fun `formatStorageBytes handles gigabytes`() {
        assertEquals("4.2 GB", formatStorageBytes(4_200_000_000L))
        assertEquals("64 GB", formatStorageBytes(64_000_000_000L))
    }
}
