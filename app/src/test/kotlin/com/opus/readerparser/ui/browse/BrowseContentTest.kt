package com.opus.readerparser.ui.browse

import com.opus.readerparser.domain.model.ContentType
import com.opus.readerparser.domain.model.Series
import com.opus.readerparser.domain.model.SourceInfo
import com.opus.readerparser.ui.components.displayLabel
import com.opus.readerparser.ui.navigation.Destinations
import com.opus.readerparser.ui.navigation.bottomNavRoutes
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BrowseContentTest {

    @Test
    fun `SourceInfo displayLabel formats novel source correctly`() {
        val source = SourceInfo(
            id = 1L,
            name = "FreeWebNovel",
            lang = "en",
            type = ContentType.NOVEL,
        )
        assertEquals("FreeWebNovel (EN) · Novel", source.displayLabel())
    }

    @Test
    fun `SourceInfo displayLabel formats manhwa source correctly`() {
        val source = SourceInfo(
            id = 2L,
            name = "AsuraScans",
            lang = "en",
            type = ContentType.MANHWA,
        )
        assertEquals("AsuraScans (EN) · Manhwa", source.displayLabel())
    }

    @Test
    fun `bottomNavRoutes contains exactly the 3 top-level routes`() {
        assertEquals(
            setOf(Destinations.LIBRARY, Destinations.BROWSE, Destinations.DOWNLOADS),
            bottomNavRoutes,
        )
    }

    @Test
    fun `BrowseMode displayLabel returns correct labels`() {
        assertEquals("Popular", BrowseMode.POPULAR.displayLabel())
        assertEquals("Latest", BrowseMode.LATEST.displayLabel())
        assertEquals("Search", BrowseMode.SEARCH.displayLabel())
    }

    @Test
    fun `ContentType displayLabel returns correct labels`() {
        assertEquals("Novel", ContentType.NOVEL.displayLabel())
        assertEquals("Manhwa", ContentType.MANHWA.displayLabel())
    }

    @Test
    fun `BrowseUiState isSeriesInLibrary matches by inLibraryUrls or inLibraryKeys`() {
        val series1 = Series(sourceId = 1L, url = "https://example.com/s1", title = "S1", type = ContentType.NOVEL)
        val series2 = Series(sourceId = 2L, url = "https://example.com/s2", title = "S2", type = ContentType.MANHWA)
        val series3 = Series(sourceId = 3L, url = "https://example.com/s3", title = "S3", type = ContentType.NOVEL)

        val state = BrowseUiState(
            inLibraryUrls = setOf(series1.url),
            inLibraryKeys = setOf(series2.sourceId to series2.url),
        )

        assertTrue(state.isSeriesInLibrary(series1))
        assertTrue(state.isSeriesInLibrary(series2))
        assertFalse(state.isSeriesInLibrary(series3))
    }
}
