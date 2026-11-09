package com.opus.readerparser.sources.freewebnovel

import com.opus.readerparser.data.local.database.dao.SeriesDao
import com.opus.readerparser.data.local.database.entities.SeriesEntity
import com.opus.readerparser.data.source.CatalogHarvester
import com.opus.readerparser.domain.model.ContentType
import com.opus.readerparser.domain.model.Series
import com.opus.readerparser.domain.model.SeriesStatus
import com.opus.readerparser.testutil.mockHttpClient
import com.opus.readerparser.testutil.readFixture
import com.opus.readerparser.testutil.respondHtml
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FreeWebNovelCatalogBatchPersisterTest {

    class TestSeriesDao : SeriesDao {
        private val store = mutableListOf<SeriesEntity>()
        var updateDetailsCallCount = 0
            private set
        var insertCallCount = 0
            private set

        val entities: List<SeriesEntity> get() = store.toList()

        override suspend fun getByUrl(sourceId: Long, url: String): SeriesEntity? =
            store.find { it.sourceId == sourceId && it.url == url }

        override suspend fun getBySourceId(sourceId: Long): List<SeriesEntity> =
            store.filter { it.sourceId == sourceId }

        override suspend fun searchCatalog(sourceId: Long, query: String): List<SeriesEntity> =
            store.filter { it.sourceId == sourceId && it.title.contains(query, ignoreCase = true) }

        override suspend fun insert(series: SeriesEntity) {
            insertCallCount++
            store.add(series)
        }

        override suspend fun updateDetails(
            sourceId: Long,
            url: String,
            title: String,
            author: String?,
            artist: String?,
            description: String?,
            coverUrl: String?,
            genresJson: String,
            status: String,
            type: String,
        ): Int {
            updateDetailsCallCount++
            val idx = store.indexOfFirst { it.sourceId == sourceId && it.url == url }
            return if (idx >= 0) {
                store[idx] = store[idx].copy(
                    title = title,
                    author = author,
                    artist = artist,
                    description = description,
                    coverUrl = coverUrl,
                    genresJson = genresJson,
                    status = status,
                    type = type,
                )
                1
            } else {
                0
            }
        }

        override suspend fun upsert(series: SeriesEntity) {
            val idx = store.indexOfFirst { it.sourceId == series.sourceId && it.url == series.url }
            if (idx >= 0) store[idx] = series else store.add(series)
        }

        override suspend fun upsertAll(series: List<SeriesEntity>) {
            series.forEach { upsert(it) }
        }

        override fun observeLibrary(): Flow<List<SeriesEntity>> = flowOf(store.filter { it.inLibrary })

        override suspend fun getLibrarySeries(): List<SeriesEntity> = store.filter { it.inLibrary }

        override suspend fun getLibraryIndexableSeries(sourceId: Long, url: String): SeriesEntity? =
            store.find { it.sourceId == sourceId && it.url == url && it.inLibrary }

        override suspend fun getLibraryIndexableSeries(): List<SeriesEntity> =
            store.filter { it.inLibrary }

        override fun observeIndexableSeries(): Flow<List<SeriesEntity>> = flowOf(store)

        override suspend fun getIndexableSeries(): List<SeriesEntity> = store

        override suspend fun addToLibrary(sourceId: Long, url: String, addedAt: Long) {
            val idx = store.indexOfFirst { it.sourceId == sourceId && it.url == url }
            if (idx >= 0) {
                store[idx] = store[idx].copy(inLibrary = true, addedAt = addedAt)
            }
        }

        override suspend fun removeFromLibrary(sourceId: Long, url: String) {
            val idx = store.indexOfFirst { it.sourceId == sourceId && it.url == url }
            if (idx >= 0) {
                store[idx] = store[idx].copy(inLibrary = false, addedAt = null)
            }
        }

        override suspend fun delete(sourceId: Long, url: String) {
            store.removeAll { it.sourceId == sourceId && it.url == url }
        }
    }

    @Test
    fun `persistBatch inserts stubs with inLibrary false and addedAt null`() = runTest {
        val seriesDao = TestSeriesDao()
        val persister = FreeWebNovelCatalogBatchPersister(seriesDao)

        val stubs = listOf(
            Series(
                sourceId = 1L,
                url = "https://freewebnovel.com/novel/invincible",
                title = "Invincible",
                coverUrl = "https://freewebnovel.com/files/article/image/1/123/123s.jpg",
                status = SeriesStatus.COMPLETED,
                type = ContentType.NOVEL,
            ),
            Series(
                sourceId = 1L,
                url = "https://freewebnovel.com/novel/renegade-immortal",
                title = "Renegade Immortal",
                coverUrl = "https://freewebnovel.com/files/article/image/1/456/456s.jpg",
                status = SeriesStatus.UNKNOWN,
                type = ContentType.NOVEL,
            ),
        )

        persister.persistBatch(stubs)

        assertEquals(2, seriesDao.entities.size)
        assertEquals(2, seriesDao.insertCallCount)

        val entity1 = seriesDao.getByUrl(1L, "https://freewebnovel.com/novel/invincible")
        assertNotNull(entity1)
        assertEquals("Invincible", entity1!!.title)
        assertFalse(entity1.inLibrary)
        assertNull(entity1.addedAt)

        val entity2 = seriesDao.getByUrl(1L, "https://freewebnovel.com/novel/renegade-immortal")
        assertNotNull(entity2)
        assertEquals("Renegade Immortal", entity2!!.title)
        assertFalse(entity2.inLibrary)
        assertNull(entity2.addedAt)
    }

    @Test
    fun `persistBatch touches zero chapters`() = runTest {
        val seriesDao = TestSeriesDao()
        val persister = FreeWebNovelCatalogBatchPersister(seriesDao)

        val stubs = listOf(
            Series(
                sourceId = 1L,
                url = "https://freewebnovel.com/novel/invincible",
                title = "Invincible",
                type = ContentType.NOVEL,
            ),
        )

        // Verifies batch persistence only interacts with SeriesDao and does not require or touch chapters
        persister.persistBatch(stubs)

        assertEquals(1, seriesDao.entities.size)
    }

    @Test
    fun `re-persisting an existing bookmarked series updates content columns and preserves inLibrary and addedAt`() = runTest {
        val seriesDao = TestSeriesDao()
        val existingBookmark = SeriesEntity(
            sourceId = 1L,
            url = "https://freewebnovel.com/novel/invincible",
            title = "Invincible Old",
            coverUrl = "https://freewebnovel.com/old.jpg",
            genresJson = "[]",
            status = SeriesStatus.UNKNOWN.name,
            type = ContentType.NOVEL.name,
            inLibrary = true,
            addedAt = 12345L,
        )
        seriesDao.insert(existingBookmark)

        val persister = FreeWebNovelCatalogBatchPersister(seriesDao)

        val updatedStub = Series(
            sourceId = 1L,
            url = "https://freewebnovel.com/novel/invincible",
            title = "Invincible New",
            coverUrl = "https://freewebnovel.com/new.jpg",
            status = SeriesStatus.COMPLETED,
            type = ContentType.NOVEL,
        )

        persister.persistBatch(listOf(updatedStub))

        assertEquals(1, seriesDao.updateDetailsCallCount)
        val persisted = seriesDao.getByUrl(1L, "https://freewebnovel.com/novel/invincible")
        assertNotNull(persisted)
        assertEquals("Invincible New", persisted!!.title)
        assertEquals("https://freewebnovel.com/new.jpg", persisted.coverUrl)
        assertEquals(SeriesStatus.COMPLETED.name, persisted.status)
        assertTrue("inLibrary must remain true", persisted.inLibrary)
        assertEquals(12345L, persisted.addedAt)
    }

    @Test
    fun `FreeWebNovel constructor accepts optional SeriesDao and persists stubs during harvestCatalog when SeriesDao is supplied`() = runTest {
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

        val seriesDao = TestSeriesDao()
        val source = FreeWebNovel(client, seriesDao = seriesDao)
        val harvester = source as CatalogHarvester

        harvester.harvestCatalog().toList()

        assertEquals(4, seriesDao.entities.size)
        assertTrue(seriesDao.entities.all { !it.inLibrary && it.addedAt == null })
    }
}
