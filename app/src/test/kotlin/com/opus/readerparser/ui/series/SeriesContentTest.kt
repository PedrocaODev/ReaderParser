package com.opus.readerparser.ui.series

import com.opus.readerparser.domain.model.Chapter
import com.opus.readerparser.domain.model.ChapterWithState
import com.opus.readerparser.domain.model.ContentType
import com.opus.readerparser.domain.model.Series
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SeriesContentTest {

    private val testSeries = Series(
        sourceId = 1L,
        url = "https://example.com/series/1",
        title = "Solo Leveling",
        type = ContentType.MANHWA,
    )

    private val testChapters = listOf(
        ChapterWithState(
            chapter = Chapter(
                seriesUrl = "https://example.com/series/1",
                sourceId = 1L,
                url = "https://example.com/c1",
                name = "Chapter 1",
                number = 1f,
            ),
            read = true,
            downloaded = false,
            progress = 1f,
        ),
        ChapterWithState(
            chapter = Chapter(
                seriesUrl = "https://example.com/series/1",
                sourceId = 1L,
                url = "https://example.com/c2",
                name = "Chapter 2",
                number = 2f,
            ),
            read = false,
            downloaded = true,
            progress = 0f,
        ),
    )

    @Test
    fun `SeriesUiState isInitialError is true when series is null and error is present`() {
        val state = SeriesUiState(series = null, error = "Failed to load series")
        assertTrue(state.isInitialError)
    }

    @Test
    fun `SeriesUiState isInitialError is false when series is non-null even if error is present`() {
        val state = SeriesUiState(series = testSeries, error = "Background refresh failed")
        assertFalse(state.isInitialError)
    }

    @Test
    fun `SeriesUiState isInitialLoading is true when series is null and isLoading is true`() {
        val state = SeriesUiState(series = null, isLoading = true)
        assertTrue(state.isInitialLoading)
    }

    @Test
    fun `SeriesUiState isInitialLoading is false when series is non-null and isLoading is true`() {
        val state = SeriesUiState(series = testSeries, isLoading = true)
        assertFalse(state.isInitialLoading)
    }

    @Test
    fun `SeriesUiState with cached series and background error retains displayedChapters and resumeState`() {
        val state = SeriesUiState(
            series = testSeries,
            chapters = testChapters,
            error = "Network timeout on refresh",
            isLoading = false,
        )
        assertFalse(state.isInitialError)
        assertEquals(testSeries, state.series)
        assertEquals(2, state.displayedChapters.size)
        assertEquals("Network timeout on refresh", state.error)
        assertTrue(state.resumeState is ReadingResumeState.Continue)
    }

    @Test
    fun `SeriesAction DismissError and ClearError can be dispatched`() {
        var dispatchedAction: SeriesAction? = null
        val onAction: (SeriesAction) -> Unit = { action -> dispatchedAction = action }

        onAction(SeriesAction.DismissError)
        assertEquals(SeriesAction.DismissError, dispatchedAction)

        onAction(SeriesAction.ClearError)
        assertEquals(SeriesAction.ClearError, dispatchedAction)
    }
}
