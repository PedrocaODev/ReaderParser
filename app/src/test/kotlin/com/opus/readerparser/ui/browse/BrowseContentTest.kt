package com.opus.readerparser.ui.browse

import com.opus.readerparser.domain.model.ContentType
import com.opus.readerparser.domain.model.SourceInfo
import com.opus.readerparser.ui.navigation.Destinations
import com.opus.readerparser.ui.navigation.bottomNavRoutes
import org.junit.Assert.assertEquals
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
}
