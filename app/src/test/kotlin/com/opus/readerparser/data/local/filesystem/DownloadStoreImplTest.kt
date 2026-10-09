package com.opus.readerparser.data.local.filesystem

import com.opus.readerparser.core.util.hashUrl
import com.opus.readerparser.domain.model.ChapterContent
import com.opus.readerparser.testutil.TestFixtures
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class DownloadStoreImplTest {

    @get:Rule
    val tmpDir = TemporaryFolder()

    private val chapter = TestFixtures.testChapter(
        seriesUrl = "https://test.invalid/series/test",
        url = "https://test.invalid/chapter/1",
        sourceId = 1L,
    )

    // Fixed fake image bytes (JPEG magic bytes) returned by the fetchBytes stub
    private val fakeImageBytes = byteArrayOf(0xFF.toByte(), 0xD8.toByte())

    private val json = Json { ignoreUnknownKeys = true }

    private fun makeStore(): DownloadStoreImpl = DownloadStoreImpl(root = tmpDir.root, json = json)

    /** Stub fetchBytes lambda that returns [fakeImageBytes] for any URL. */
    private val fetchBytes: suspend (String) -> ByteArray = { fakeImageBytes }

    @Test
    fun `read on empty store returns null`() = runTest {
        val store = makeStore()
        assertNull(store.read(chapter))
    }

    @Test
    fun `writeNovel then read returns ChapterContent Text with original html`() = runTest {
        val store = makeStore()
        val html = "<p>Hello world</p>"

        store.writeNovel(chapter, html)

        val result = store.read(chapter)
        assertNotNull(result)
        assertTrue(result is ChapterContent.Text)
        assertEquals(html, (result as ChapterContent.Text).html)
    }

    @Test
    fun `writeManhwa then read returns ChapterContent Pages with matching count`() = runTest {
        val imageUrls = listOf(
            "https://cdn.invalid/page1.jpg",
            "https://cdn.invalid/page2.jpg",
            "https://cdn.invalid/page3.jpg",
        )
        val store = makeStore()

        store.writeManhwa(chapter, imageUrls, fetchBytes)

        val result = store.read(chapter)
        assertNotNull(result)
        assertTrue(result is ChapterContent.Pages)
        assertEquals(imageUrls.size, (result as ChapterContent.Pages).imageUrls.size)
    }

    @Test
    fun `delete after writeNovel causes read to return null`() = runTest {
        val store = makeStore()
        store.writeNovel(chapter, "<p>content</p>")

        store.delete(chapter)

        assertNull(store.read(chapter))
    }

    @Test
    fun `delete after writeManhwa causes read to return null`() = runTest {
        val store = makeStore()
        store.writeManhwa(chapter, listOf("https://cdn.invalid/page1.jpg"), fetchBytes)

        store.delete(chapter)

        assertNull(store.read(chapter))
    }

    @Test
    fun `writeNovel creates expected file layout`() = runTest {
        val store = makeStore()
        store.writeNovel(chapter, "<p>content</p>")

        val dir = tmpDir.root
            .resolve("${chapter.sourceId}")
            .resolve(hashUrl(chapter.seriesUrl))
            .resolve(hashUrl(chapter.url))

        assertTrue("Expected chapter directory to exist", dir.exists())
        assertTrue("Expected meta.json", dir.resolve("meta.json").exists())
        assertTrue("Expected content.html", dir.resolve("content.html").exists())
    }

    @Test
    fun `directory structure uses hashed urls as path components`() = runTest {
        val store = makeStore()
        store.writeNovel(chapter, "<p>content</p>")

        val seriesHash = hashUrl(chapter.seriesUrl)
        val chapterHash = hashUrl(chapter.url)

        val expectedDir = tmpDir.root
            .resolve("${chapter.sourceId}")
            .resolve(seriesHash)
            .resolve(chapterHash)

        assertTrue(
            "Expected dir $expectedDir to exist",
            expectedDir.isDirectory,
        )
    }

    @Test
    fun `writeManhwa writes pages with 1-based 3-digit zero-padded names`() = runTest {
        val imageUrls = listOf(
            "https://cdn.invalid/page1.jpg",
            "https://cdn.invalid/page2.jpg",
        )
        val store = makeStore()
        store.writeManhwa(chapter, imageUrls, fetchBytes)

        val dir = tmpDir.root
            .resolve("${chapter.sourceId}")
            .resolve(hashUrl(chapter.seriesUrl))
            .resolve(hashUrl(chapter.url))

        assertTrue("001.jpg should exist", dir.resolve("001.jpg").exists())
        assertTrue("002.jpg should exist", dir.resolve("002.jpg").exists())
    }

    @Test
    fun `delete on non-existent chapter does not throw`() = runTest {
        val store = makeStore()
        // Should complete without exception even when nothing was written
        store.delete(chapter)
    }

    @Test
    fun `writeManhwa invokes onPageDownloaded with correct progress values`() = runTest {
        val imageUrls = listOf(
            "https://cdn.invalid/page1.jpg",
            "https://cdn.invalid/page2.jpg",
            "https://cdn.invalid/page3.jpg",
        )
        val store = makeStore()
        val progressUpdates = mutableListOf<Pair<Int, Int>>()

        store.writeManhwa(chapter, imageUrls, fetchBytes) { downloaded, total ->
            progressUpdates.add(downloaded to total)
        }

        assertEquals(
            listOf(1 to 3, 2 to 3, 3 to 3),
            progressUpdates,
        )
    }

    @Test
    fun `writeManhwa does not invoke onPageDownloaded for empty imageUrls`() = runTest {
        val store = makeStore()
        var callbackInvoked = false

        store.writeManhwa(chapter, emptyList(), fetchBytes) { _, _ ->
            callbackInvoked = true
        }

        assertTrue("onPageDownloaded should not be called for empty list", !callbackInvoked)
    }

    @Test
    fun `interrupted first write leaves no valid download and read returns null`() = runTest {
        val store = makeStore()
        val imageUrls = listOf(
            "https://cdn.invalid/page1.jpg",
            "https://cdn.invalid/page2.jpg",
            "https://cdn.invalid/page3.jpg",
        )
        val failingFetchBytes: suspend (String) -> ByteArray = { url ->
            if (url.contains("page2")) throw java.io.IOException("Network connection dropped")
            fakeImageBytes
        }

        try {
            store.writeManhwa(chapter, imageUrls, failingFetchBytes)
            org.junit.Assert.fail("Expected IOException")
        } catch (e: java.io.IOException) {
            assertEquals("Network connection dropped", e.message)
        }

        assertNull(store.read(chapter))

        val seriesDir = tmpDir.root
            .resolve("${chapter.sourceId}")
            .resolve(hashUrl(chapter.seriesUrl))
        val leftoverStaging = seriesDir.listFiles { f -> f.name.startsWith(".staging") }
        assertTrue(leftoverStaging.isNullOrEmpty())
    }

    @Test
    fun `failed replacement preserves previous complete version`() = runTest {
        val store = makeStore()
        val initialUrls = listOf(
            "https://cdn.invalid/page1.jpg",
            "https://cdn.invalid/page2.jpg",
            "https://cdn.invalid/page3.jpg",
        )
        store.writeManhwa(chapter, initialUrls, fetchBytes)

        val beforeResult = store.read(chapter)
        assertNotNull(beforeResult)
        assertEquals(3, (beforeResult as ChapterContent.Pages).imageUrls.size)

        val newUrls = listOf(
            "https://cdn.invalid/new-page1.jpg",
            "https://cdn.invalid/new-page2.jpg",
            "https://cdn.invalid/new-page3.jpg",
        )
        val failingFetchBytes: suspend (String) -> ByteArray = { url ->
            if (url.contains("new-page2")) throw java.io.IOException("Network failure on page 2")
            byteArrayOf(1, 2, 3)
        }

        try {
            store.writeManhwa(chapter, newUrls, failingFetchBytes)
            org.junit.Assert.fail("Expected IOException")
        } catch (e: java.io.IOException) {
            assertEquals("Network failure on page 2", e.message)
        }

        val afterResult = store.read(chapter)
        assertNotNull(afterResult)
        assertTrue(afterResult is ChapterContent.Pages)
        val pages = afterResult as ChapterContent.Pages
        assertEquals(3, pages.imageUrls.size)

        val dir = tmpDir.root
            .resolve("${chapter.sourceId}")
            .resolve(hashUrl(chapter.seriesUrl))
            .resolve(hashUrl(chapter.url))
        assertTrue(dir.resolve("001.jpg").readBytes().contentEquals(fakeImageBytes))
    }

    @Test
    fun `replacing 3-page download with 1-page download returns exactly 1 page on read`() = runTest {
        val store = makeStore()
        val initialUrls = listOf(
            "https://cdn.invalid/page1.jpg",
            "https://cdn.invalid/page2.jpg",
            "https://cdn.invalid/page3.jpg",
        )
        store.writeManhwa(chapter, initialUrls, fetchBytes)

        val singlePageUrl = listOf("https://cdn.invalid/single.jpg")
        val singleBytes = byteArrayOf(9, 9, 9)
        store.writeManhwa(chapter, singlePageUrl, fetchBytes = { singleBytes })

        val result = store.read(chapter)
        assertNotNull(result)
        assertTrue(result is ChapterContent.Pages)
        val pages = result as ChapterContent.Pages
        assertEquals(1, pages.imageUrls.size)
        assertTrue(pages.imageUrls[0].endsWith("001.jpg"))

        val dir = tmpDir.root
            .resolve("${chapter.sourceId}")
            .resolve(hashUrl(chapter.seriesUrl))
            .resolve(hashUrl(chapter.url))
        assertTrue(dir.resolve("001.jpg").exists())
        assertTrue(!dir.resolve("002.jpg").exists())
        assertTrue(!dir.resolve("003.jpg").exists())
    }

    @Test
    fun `missing page file causes read to return null`() = runTest {
        val store = makeStore()
        val imageUrls = listOf(
            "https://cdn.invalid/page1.jpg",
            "https://cdn.invalid/page2.jpg",
            "https://cdn.invalid/page3.jpg",
        )
        store.writeManhwa(chapter, imageUrls, fetchBytes)

        val dir = tmpDir.root
            .resolve("${chapter.sourceId}")
            .resolve(hashUrl(chapter.seriesUrl))
            .resolve(hashUrl(chapter.url))

        dir.resolve("002.jpg").delete()

        assertNull("Missing page should return null to allow network fallback", store.read(chapter))
    }

    @Test
    fun `empty page file causes read to return null`() = runTest {
        val store = makeStore()
        val imageUrls = listOf(
            "https://cdn.invalid/page1.jpg",
            "https://cdn.invalid/page2.jpg",
            "https://cdn.invalid/page3.jpg",
        )
        store.writeManhwa(chapter, imageUrls, fetchBytes)

        val dir = tmpDir.root
            .resolve("${chapter.sourceId}")
            .resolve(hashUrl(chapter.seriesUrl))
            .resolve(hashUrl(chapter.url))

        dir.resolve("002.jpg").writeBytes(byteArrayOf())

        assertNull("0-byte page should return null to allow network fallback", store.read(chapter))
    }

    @Test
    fun `page order is strictly maintained`() = runTest {
        val store = makeStore()
        val imageUrls = (1..15).map { "https://cdn.invalid/page$it.jpg" }
        store.writeManhwa(chapter, imageUrls, fetchBytes)

        val result = store.read(chapter)
        assertNotNull(result)
        assertTrue(result is ChapterContent.Pages)
        val pages = (result as ChapterContent.Pages).imageUrls

        assertEquals(15, pages.size)
        pages.forEachIndexed { index, uri ->
            val expectedSuffix = "%03d.jpg".format(index + 1)
            assertTrue("Expected URI to end with $expectedSuffix, but was $uri", uri.endsWith(expectedSuffix))
        }
    }

    @Test
    fun `stale extra files in directory are ignored by read`() = runTest {
        val store = makeStore()
        val imageUrls = listOf(
            "https://cdn.invalid/page1.jpg",
            "https://cdn.invalid/page2.jpg",
        )
        store.writeManhwa(chapter, imageUrls, fetchBytes)

        val dir = tmpDir.root
            .resolve("${chapter.sourceId}")
            .resolve(hashUrl(chapter.seriesUrl))
            .resolve(hashUrl(chapter.url))

        // Create a stale extra file
        dir.resolve("003.jpg").writeBytes(byteArrayOf(5, 5))
        dir.resolve("stale.tmp").writeBytes(byteArrayOf(1))

        val result = store.read(chapter)
        assertNotNull(result)
        assertTrue(result is ChapterContent.Pages)
        val pages = (result as ChapterContent.Pages).imageUrls
        assertEquals(2, pages.size)
        assertTrue(pages[0].endsWith("001.jpg"))
        assertTrue(pages[1].endsWith("002.jpg"))
    }

    @Test
    fun `corrupted or empty meta file causes read to return null`() = runTest {
        val store = makeStore()
        store.writeNovel(chapter, "<p>content</p>")

        val dir = tmpDir.root
            .resolve("${chapter.sourceId}")
            .resolve(hashUrl(chapter.seriesUrl))
            .resolve(hashUrl(chapter.url))

        // Corrupt meta.json
        dir.resolve("meta.json").writeText("{ not-valid-json }")
        assertNull(store.read(chapter))

        // Truncate meta.json to empty
        dir.resolve("meta.json").writeText("")
        assertNull(store.read(chapter))
    }

    @Test
    fun `empty novel content file causes read to return null`() = runTest {
        val store = makeStore()
        store.writeNovel(chapter, "<p>content</p>")

        val dir = tmpDir.root
            .resolve("${chapter.sourceId}")
            .resolve(hashUrl(chapter.seriesUrl))
            .resolve(hashUrl(chapter.url))

        // Truncate content.html to empty
        dir.resolve("content.html").writeText("")
        assertNull(store.read(chapter))

        // Delete content.html
        dir.resolve("content.html").delete()
        assertNull(store.read(chapter))
    }
}
