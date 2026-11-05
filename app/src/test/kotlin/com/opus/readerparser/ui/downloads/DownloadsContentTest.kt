package com.opus.readerparser.ui.downloads

import com.opus.readerparser.domain.model.ContentType
import com.opus.readerparser.domain.model.DEFAULT_AVAILABLE_STORAGE_BYTES
import com.opus.readerparser.domain.model.DownloadItem
import com.opus.readerparser.domain.model.DownloadState
import com.opus.readerparser.domain.model.DownloadedSeriesItem
import com.opus.readerparser.ui.components.formatChapterNumber
import com.opus.readerparser.ui.components.formatChapterRangeSummary
import com.opus.readerparser.ui.components.formatStorageBytes
import com.opus.readerparser.ui.navigation.Destinations
import com.opus.readerparser.ui.navigation.bottomNavRoutes
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DownloadsContentTest {

    @Test
    fun `DownloadsUiState default values are correct`() {
        val state = DownloadsUiState()
        assertTrue(state.downloads.isEmpty())
        assertTrue(state.downloadedSeries.isEmpty())
        assertEquals(0L, state.novelTextBytes)
        assertEquals(0L, state.manhwaImageBytes)
        assertEquals(DEFAULT_AVAILABLE_STORAGE_BYTES, state.availableBytes)
        assertEquals(0L, state.storageBreakdown.usedBytes)
        assertEquals(DEFAULT_AVAILABLE_STORAGE_BYTES, state.storageBreakdown.availableBytes)
        assertFalse(state.isLoading)
        assertNull(state.error)
    }

    @Test
    fun `DownloadsUiState populated state holds downloads and series`() {
        val item = DownloadItem(
            sourceId = 1L,
            chapterUrl = "https://example.com/c1",
            chapterName = "Chapter 1",
            seriesTitle = "Solo Leveling",
            state = DownloadState.COMPLETED,
            progress = 1.0f,
        )
        val seriesItem = DownloadedSeriesItem(
            sourceId = 1L,
            seriesUrl = "https://example.com/s1",
            seriesTitle = "Solo Leveling",
            contentType = ContentType.MANHWA,
            downloadedChapterCount = 10,
            chapterRangeSummary = "10 chapters · Ch. 1–10",
            totalBytes = 30_000_000L,
        )
        val state = DownloadsUiState(
            downloads = listOf(item),
            downloadedSeries = listOf(seriesItem),
            novelTextBytes = 12_800_000L,
            manhwaImageBytes = 30_000_000L,
            availableBytes = 64_000_000_000L,
            isLoading = false,
            error = null,
        )
        assertEquals(1, state.downloads.size)
        assertEquals(item, state.downloads.first())
        assertEquals(1, state.downloadedSeries.size)
        assertEquals(seriesItem, state.downloadedSeries.first())
        assertEquals(1L, seriesItem.sourceId)
        assertEquals("https://example.com/s1", seriesItem.seriesUrl)
        assertEquals(30_000_000L, seriesItem.totalBytes)
        assertEquals(42_800_000L, state.storageBreakdown.usedBytes)
        assertEquals(64_000_000_000L, state.storageBreakdown.availableBytes)
    }

    @Test
    fun `formatChapterNumber formats integer and float values correctly`() {
        assertEquals("1", formatChapterNumber(1f))
        assertEquals("12", formatChapterNumber(12.0f))
        assertEquals("12.5", formatChapterNumber(12.5f))
    }

    @Test
    fun `formatChapterRangeSummary formats correctly for empty, single, and multiple chapters`() {
        assertEquals("0 chapters", formatChapterRangeSummary(0, emptyList()))
        assertEquals("1 chapter · Ch. 5", formatChapterRangeSummary(1, listOf(5f)))
        assertEquals("2 chapters · Ch. 1\u20132", formatChapterRangeSummary(2, listOf(1f, 2f)))
        assertEquals("42 chapters · Ch. 1\u201342", formatChapterRangeSummary(42, listOf(1f, 42f, 10f)))
    }

    @Test
    fun `formatStorageBytes formats various sizes correctly`() {
        assertEquals("0 MB", formatStorageBytes(0L))
        assertEquals("0 MB", formatStorageBytes(-5L))
        assertEquals("500 B", formatStorageBytes(500L))
        assertEquals("142 MB", formatStorageBytes(142L * 1024L * 1024L))
        assertEquals("4.2 GB", formatStorageBytes(DEFAULT_AVAILABLE_STORAGE_BYTES))
        assertEquals("64 GB", formatStorageBytes(64_000_000_000L))
        assertEquals("12.8 MB", formatStorageBytes(12_800_000L))
    }

    @Test
    fun `Destinations SETTINGS is available and bottomNavRoutes contains DOWNLOADS`() {
        assertEquals("settings", Destinations.SETTINGS)
        assertTrue(bottomNavRoutes.contains(Destinations.DOWNLOADS))
    }

    @Test
    fun `DownloadsAction sealed class holds all expected actions`() {
        val cancel = DownloadsAction.Cancel(1L, "c1")
        val retry = DownloadsAction.Retry(1L, "c1")
        val delete = DownloadsAction.Delete(1L, "c1")
        val clearSeries = DownloadsAction.ClearSeriesDownloads(1L, "https://example.com/series-1")
        val retryLoad = DownloadsAction.RetryLoad

        assertEquals(1L, cancel.sourceId)
        assertEquals("c1", retry.chapterUrl)
        assertEquals("c1", delete.chapterUrl)
        assertEquals(1L, clearSeries.sourceId)
        assertEquals("https://example.com/series-1", clearSeries.seriesUrl)
        assertEquals("1:https://example.com/series-1", clearSeries.seriesId)
        assertEquals(DownloadsAction.RetryLoad, retryLoad)
    }
}
