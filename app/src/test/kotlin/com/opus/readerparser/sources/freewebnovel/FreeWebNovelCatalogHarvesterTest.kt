package com.opus.readerparser.sources.freewebnovel

import com.opus.readerparser.data.source.CatalogHarvester
import com.opus.readerparser.domain.model.ContentType
import com.opus.readerparser.domain.model.SeriesStatus
import com.opus.readerparser.testutil.mockHttpClient
import com.opus.readerparser.testutil.readFixture
import com.opus.readerparser.testutil.respondHtml
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FreeWebNovelCatalogHarvesterTest {

    @Test
    fun `FreeWebNovel implements CatalogHarvester interface`() {
        val client = mockHttpClient { respondHtml("<html></html>") }
        val source = FreeWebNovel(client)
        assertTrue("FreeWebNovel must implement CatalogHarvester", source is CatalogHarvester)
    }

    @Test
    fun `harvestCatalog requests sequential pages from page 1 to terminal page`() = runTest {
        val page1Html = readFixture("fixtures/freewebnovel/latest-novel-page1.html")
        val page2Html = readFixture("fixtures/freewebnovel/latest-novel-page2.html")
        val terminalHtml = readFixture("fixtures/freewebnovel/latest-novel-terminal.html")

        val requestedUrls = mutableListOf<String>()
        val client = mockHttpClient { request ->
            val url = request.url.toString()
            requestedUrls.add(url)
            when {
                url.endsWith("/sort/latest-novel/1") -> respondHtml(page1Html)
                url.endsWith("/sort/latest-novel/2") -> respondHtml(page2Html)
                url.endsWith("/sort/latest-novel/3") -> respondHtml(terminalHtml)
                else -> respondHtml("<html></html>")
            }
        }
        val source = FreeWebNovel(client)
        val harvester = source as CatalogHarvester

        harvester.harvestCatalog().toList()

        assertEquals(
            listOf(
                "https://freewebnovel.com/sort/latest-novel/1",
                "https://freewebnovel.com/sort/latest-novel/2",
                "https://freewebnovel.com/sort/latest-novel/3",
            ),
            requestedUrls,
        )
    }

    @Test
    fun `emitted batches contain extracted Series stubs with correct metadata`() = runTest {
        val page1Html = readFixture("fixtures/freewebnovel/latest-novel-page1.html")
        val page2Html = readFixture("fixtures/freewebnovel/latest-novel-page2.html")
        val terminalHtml = readFixture("fixtures/freewebnovel/latest-novel-terminal.html")

        val client = mockHttpClient { request ->
            val url = request.url.toString()
            when {
                url.endsWith("/sort/latest-novel/1") -> respondHtml(page1Html)
                url.endsWith("/sort/latest-novel/2") -> respondHtml(page2Html)
                url.endsWith("/sort/latest-novel/3") -> respondHtml(terminalHtml)
                else -> respondHtml("<html></html>")
            }
        }
        val source = FreeWebNovel(client)
        val harvester = source as CatalogHarvester

        val batches = harvester.harvestCatalog().toList()

        assertEquals(3, batches.size)

        // Batch 1 (page 1)
        val batch1 = batches[0]
        assertEquals(2, batch1.size)

        val series1 = batch1[0]
        assertEquals(source.id, series1.sourceId)
        assertEquals("https://freewebnovel.com/novel/invincible", series1.url)
        assertEquals("Invincible", series1.title)
        assertEquals("https://freewebnovel.com/files/article/image/1/123/123s.jpg", series1.coverUrl)
        assertEquals(SeriesStatus.COMPLETED, series1.status)
        assertEquals(ContentType.NOVEL, series1.type)

        val series2 = batch1[1]
        assertEquals(source.id, series2.sourceId)
        assertEquals("https://freewebnovel.com/novel/renegade-immortal", series2.url)
        assertEquals("Renegade Immortal", series2.title)
        assertEquals("https://freewebnovel.com/files/article/image/1/456/456s.jpg", series2.coverUrl)
        assertEquals(SeriesStatus.UNKNOWN, series2.status)
        assertEquals(ContentType.NOVEL, series2.type)

        // Batch 2 (page 2)
        val batch2 = batches[1]
        assertEquals(1, batch2.size)

        val series3 = batch2[0]
        assertEquals(source.id, series3.sourceId)
        assertEquals("https://freewebnovel.com/novel/a-regressors-tale-of-cultivation", series3.url)
        assertEquals("A Regressor's Tale of Cultivation", series3.title)
        assertEquals("https://freewebnovel.com/files/article/image/2/789/789s.jpg", series3.coverUrl)
        assertEquals(SeriesStatus.UNKNOWN, series3.status)
        assertEquals(ContentType.NOVEL, series3.type)

        // Batch 3 (terminal page)
        val batch3 = batches[2]
        assertEquals(1, batch3.size)

        val series4 = batch3[0]
        assertEquals(source.id, series4.sourceId)
        assertEquals("https://freewebnovel.com/novel/lord-of-the-mysteries", series4.url)
        assertEquals("Lord of the Mysteries", series4.title)
        assertEquals("https://freewebnovel.com/files/article/image/3/999/999s.jpg", series4.coverUrl)
        assertEquals(SeriesStatus.COMPLETED, series4.status)
        assertEquals(ContentType.NOVEL, series4.type)
    }

    @Test
    fun `harvestCatalog terminates when terminal page pagination is encountered`() = runTest {
        val terminalHtml = readFixture("fixtures/freewebnovel/latest-novel-terminal.html")
        val requestedUrls = mutableListOf<String>()
        val client = mockHttpClient { request ->
            requestedUrls.add(request.url.toString())
            respondHtml(terminalHtml)
        }
        val source = FreeWebNovel(client)
        val harvester = source as CatalogHarvester

        val batches = harvester.harvestCatalog().toList()

        assertEquals(1, requestedUrls.size)
        assertEquals(1, batches.size)
    }

    @Test
    fun `harvestCatalog terminates when next-page link is completely absent`() = runTest {
        val noNextPageHtml = """
            <!DOCTYPE html>
            <html>
            <body>
                <div class="col-content">
                    <div class="ul-list1 ul-list1-2 ss-custom">
                        <div class="li-row">
                            <div class="li">
                                <div class="con">
                                    <div class="pic">
                                        <a href="/novel/single-page-novel">
                                            <img src="https://freewebnovel.com/files/article/image/1/111/111s.jpg" />
                                        </a>
                                    </div>
                                    <div class="txt">
                                        <h3 class="tit"><a href="/novel/single-page-novel">Single Page Novel</a></h3>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="pages">
                        <ul>
                            <li><strong>1</strong></li>
                        </ul>
                    </div>
                </div>
            </body>
            </html>
        """.trimIndent()

        val requestedUrls = mutableListOf<String>()
        val client = mockHttpClient { request ->
            requestedUrls.add(request.url.toString())
            respondHtml(noNextPageHtml)
        }
        val source = FreeWebNovel(client)
        val harvester = source as CatalogHarvester

        val batches = harvester.harvestCatalog().toList()

        assertEquals(1, requestedUrls.size)
        assertEquals(1, batches.size)
        assertEquals("Single Page Novel", batches[0][0].title)
    }

    @Test
    fun `harvestCatalog terminates immediately when a page returns zero series`() = runTest {
        val emptyPageHtml = """
            <!DOCTYPE html>
            <html>
            <body>
                <div class="col-content">
                    <div class="ul-list1 ul-list1-2 ss-custom"></div>
                    <div class="pages">
                        <ul>
                            <li><a href="/sort/latest-novel/2">&gt;&gt;</a></li>
                        </ul>
                    </div>
                </div>
            </body>
            </html>
        """.trimIndent()

        val requestedUrls = mutableListOf<String>()
        val client = mockHttpClient { request ->
            requestedUrls.add(request.url.toString())
            respondHtml(emptyPageHtml)
        }
        val source = FreeWebNovel(client)
        val harvester = source as CatalogHarvester

        val batches = harvester.harvestCatalog().toList()

        assertEquals(1, requestedUrls.size)
        assertTrue("Batches should be empty or contain only empty list", batches.isEmpty() || batches.all { it.isEmpty() })
    }
}
