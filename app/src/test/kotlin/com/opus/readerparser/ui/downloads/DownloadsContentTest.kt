package com.opus.readerparser.ui.downloads

import com.opus.readerparser.domain.model.DownloadItem
import com.opus.readerparser.domain.model.DownloadState
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
        assertFalse(state.isLoading)
        assertNull(state.error)
    }

    @Test
    fun `DownloadsUiState populated state holds downloads`() {
        val item = DownloadItem(
            sourceId = 1L,
            chapterUrl = "https://example.com/c1",
            chapterName = "Chapter 1",
            seriesTitle = "Solo Leveling",
            state = DownloadState.COMPLETED,
            progress = 1.0f,
        )
        val state = DownloadsUiState(
            downloads = listOf(item),
            isLoading = false,
            error = null,
        )
        assertEquals(1, state.downloads.size)
        assertEquals(item, state.downloads.first())
    }

    @Test
    fun `Destinations SETTINGS is available and bottomNavRoutes contains DOWNLOADS`() {
        assertEquals("settings", Destinations.SETTINGS)
        assertTrue(bottomNavRoutes.contains(Destinations.DOWNLOADS))
    }
}
