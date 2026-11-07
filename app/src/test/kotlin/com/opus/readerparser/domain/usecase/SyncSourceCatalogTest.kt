package com.opus.readerparser.domain.usecase

import com.opus.readerparser.data.source.CatalogHarvester
import com.opus.readerparser.data.source.Source
import com.opus.readerparser.data.source.SourceRegistry
import com.opus.readerparser.domain.SeriesRepository
import com.opus.readerparser.domain.model.Chapter
import com.opus.readerparser.domain.model.ChapterContent
import com.opus.readerparser.domain.model.ContentType
import com.opus.readerparser.domain.model.FilterList
import com.opus.readerparser.domain.model.LibrarySearchResult
import com.opus.readerparser.domain.model.Series
import com.opus.readerparser.domain.model.SeriesPage
import com.opus.readerparser.domain.model.SeriesStatus
import com.opus.readerparser.domain.model.SyncProgress
import java.io.IOException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SyncSourceCatalogTest {

    private class FakeHarvesterSource(
        override val id: Long = 1L,
        override val name: String = "FakeHarvester",
        override val lang: String = "en",
        override val baseUrl: String = "https://fake.com",
        override val type: ContentType = ContentType.NOVEL,
        private val pages: List<List<Series>> = emptyList(),
        private val failure: Throwable? = null,
    ) : Source, CatalogHarvester {
        var pagesRequested: Int = 0
            private set

        override suspend fun harvestCatalog(): Flow<List<Series>> = flow {
            failure?.let { throw it }
            for (page in pages) {
                pagesRequested++
                emit(page)
            }
        }

        override suspend fun getPopular(page: Int): SeriesPage = error("Not implemented in test")
        override suspend fun getLatest(page: Int): SeriesPage = error("Not implemented in test")
        override suspend fun search(query: String, page: Int, filters: FilterList): SeriesPage =
            error("Not implemented in test")
        override suspend fun getSeriesDetails(series: Series): Series = error("Not implemented in test")
        override suspend fun getChapterList(series: Series): List<Chapter> = error("Not implemented in test")
        override suspend fun getChapterContent(chapter: Chapter): ChapterContent = error("Not implemented in test")
    }

    private class NonHarvesterSource(
        override val id: Long = 2L,
        override val name: String = "NonHarvester",
        override val lang: String = "en",
        override val baseUrl: String = "https://nonharvester.com",
        override val type: ContentType = ContentType.NOVEL,
    ) : Source {
        override suspend fun getPopular(page: Int): SeriesPage = error("Not implemented in test")
        override suspend fun getLatest(page: Int): SeriesPage = error("Not implemented in test")
        override suspend fun search(query: String, page: Int, filters: FilterList): SeriesPage =
            error("Not implemented in test")
        override suspend fun getSeriesDetails(series: Series): Series = error("Not implemented in test")
        override suspend fun getChapterList(series: Series): List<Chapter> = error("Not implemented in test")
        override suspend fun getChapterContent(chapter: Chapter): ChapterContent = error("Not implemented in test")
    }

    private class FakeSeriesRepository : SeriesRepository {
        private val persisted = mutableMapOf<Pair<Long, String>, Series>()

        fun addPersisted(series: Series) {
            persisted[series.sourceId to series.url] = series
        }

        override suspend fun getPersistedSeries(sourceId: Long, url: String): Series? =
            persisted[sourceId to url]

        override fun observeLibrary(): Flow<List<Series>> = emptyFlow()
        override fun observeLibrarySearchInvalidations(): Flow<Unit> = emptyFlow()
        override suspend fun fetchPopular(sourceId: Long, page: Int): SeriesPage = error("Not implemented in test")
        override suspend fun fetchLatest(sourceId: Long, page: Int): SeriesPage = error("Not implemented in test")
        override suspend fun search(sourceId: Long, query: String, page: Int, filters: FilterList): SeriesPage =
            error("Not implemented in test")
        override suspend fun searchLibrary(query: String): LibrarySearchResult = error("Not implemented in test")
        override suspend fun refreshDetails(series: Series): Series = error("Not implemented in test")
        override suspend fun addToLibrary(series: Series) {}
        override suspend fun removeFromLibrary(series: Series) {}
        override suspend fun isInLibrary(sourceId: Long, url: String): Boolean = false
        override suspend fun searchCatalog(sourceId: Long, query: String): List<Series> = emptyList()
    }

    private class FakeBatchPersister : CatalogBatchPersister {
        val persistedBatches = mutableListOf<List<Series>>()

        override suspend fun persistBatch(stubs: List<Series>): Int {
            persistedBatches.add(stubs)
            return stubs.size
        }
    }

    private fun createSeries(sourceId: Long, id: String, title: String): Series =
        Series(
            sourceId = sourceId,
            url = "https://fake.com/novel/$id",
            title = title,
            coverUrl = "https://fake.com/cover/$id.jpg",
            status = SeriesStatus.ONGOING,
            type = ContentType.NOVEL,
        )

    @Test
    fun `incremental early-exit halts traversal when page batch has 100% overlap`() = runTest {
        val sourceId = 1L
        val series1 = createSeries(sourceId, "1", "Series 1")
        val series2 = createSeries(sourceId, "2", "Series 2")
        val series3 = createSeries(sourceId, "3", "Series 3")

        val repository = FakeSeriesRepository().apply {
            addPersisted(series1)
            addPersisted(series2)
        }
        val persister = FakeBatchPersister()
        val source = FakeHarvesterSource(
            id = sourceId,
            pages = listOf(
                listOf(series1, series2),
                listOf(series3),
            ),
        )
        val registry = SourceRegistry(mapOf(sourceId to source))
        val useCase = SyncSourceCatalog(
            seriesRepository = repository,
            harvesterResolver = { id -> registry[id] as? CatalogHarvester },
            batchPersister = persister,
        )

        val progress = useCase(sourceId, fullSync = false).toList()

        assertEquals(1, progress.size)
        val finalProgress = progress.last()
        assertTrue("Final progress must have isFinished true", finalProgress.isFinished)
        assertFalse("Final progress must not have error", finalProgress.isError)
        assertEquals(0, finalProgress.pagesCrawled)
        assertEquals(0, finalProgress.itemsIngested)

        // Traversal halts immediately on page 1 without requesting page 2
        assertEquals(1, source.pagesRequested)
        assertTrue("Persister must not be called on early exit", persister.persistedBatches.isEmpty())
    }

    @Test
    fun `early-exit does not halt traversal when fullSync is true even with 100% overlap`() = runTest {
        val sourceId = 1L
        val series1 = createSeries(sourceId, "1", "Series 1")
        val series2 = createSeries(sourceId, "2", "Series 2")

        val repository = FakeSeriesRepository().apply {
            addPersisted(series1)
            addPersisted(series2)
        }
        val persister = FakeBatchPersister()
        val source = FakeHarvesterSource(
            id = sourceId,
            pages = listOf(
                listOf(series1),
                listOf(series2),
            ),
        )
        val useCase = SyncSourceCatalog(
            seriesRepository = repository,
            harvester = source,
            batchPersister = persister,
            pacingDelayMs = 0L,
        )

        val progress = useCase(sourceId, fullSync = true).toList()

        val finalProgress = progress.last()
        assertTrue(finalProgress.isFinished)
        assertEquals(2, finalProgress.pagesCrawled)
        assertEquals(2, finalProgress.itemsIngested)
        assertEquals(2, source.pagesRequested)
        assertEquals(2, persister.persistedBatches.size)
    }

    @Test
    fun `incremental early-exit does not halt on partial overlap (some series new)`() = runTest {
        val sourceId = 1L
        val series1 = createSeries(sourceId, "1", "Series 1")
        val series2 = createSeries(sourceId, "2", "Series 2")
        val series3 = createSeries(sourceId, "3", "Series 3")

        val repository = FakeSeriesRepository().apply {
            addPersisted(series1) // Only series1 exists; series2 is new
        }
        val persister = FakeBatchPersister()
        val source = FakeHarvesterSource(
            id = sourceId,
            pages = listOf(
                listOf(series1, series2),
                listOf(series3),
            ),
        )
        val useCase = SyncSourceCatalog(
            seriesRepository = repository,
            harvester = source,
            batchPersister = persister,
            pacingDelayMs = 0L,
        )

        val progress = useCase(harvester = source, fullSync = false).toList()

        val finalProgress = progress.last()
        assertTrue(finalProgress.isFinished)
        assertEquals(2, finalProgress.pagesCrawled)
        assertEquals(3, finalProgress.itemsIngested)
        assertEquals(2, source.pagesRequested)
        assertEquals(2, persister.persistedBatches.size)
    }

    @Test
    fun `incremental early-exit crawls page 1 with new series and halts on page 2 with 100% overlap`() = runTest {
        val sourceId = 1L
        val series1 = createSeries(sourceId, "1", "Series 1")
        val series2 = createSeries(sourceId, "2", "Series 2")
        val series3 = createSeries(sourceId, "3", "Series 3")

        val repository = FakeSeriesRepository().apply {
            addPersisted(series2) // Page 2 has 100% overlap
        }
        val persister = FakeBatchPersister()
        val source = FakeHarvesterSource(
            id = sourceId,
            pages = listOf(
                listOf(series1),
                listOf(series2),
                listOf(series3),
            ),
        )
        val useCase = SyncSourceCatalog(
            seriesRepository = repository,
            batchPersister = persister,
            pacingDelayMs = 0L,
        )

        val progress = useCase(sourceId = sourceId, harvester = source, fullSync = false).toList()

        val finalProgress = progress.last()
        assertTrue(finalProgress.isFinished)
        assertEquals(1, finalProgress.pagesCrawled)
        assertEquals(1, finalProgress.itemsIngested)
        assertEquals(2, source.pagesRequested) // Page 1 and Page 2 evaluated; Page 3 never requested
        assertEquals(1, persister.persistedBatches.size)
        assertEquals(listOf(series1), persister.persistedBatches[0])
    }

    @Test
    fun `polite pacing enforces 300ms delay between consecutive page requests`() = runTest {
        val sourceId = 1L
        val series1 = createSeries(sourceId, "1", "Series 1")
        val series2 = createSeries(sourceId, "2", "Series 2")

        val repository = FakeSeriesRepository()
        val persister = FakeBatchPersister()
        val source = FakeHarvesterSource(
            id = sourceId,
            pages = listOf(
                listOf(series1),
                listOf(series2),
            ),
        )
        val useCase = SyncSourceCatalog(
            seriesRepository = repository,
            harvester = source,
            batchPersister = persister,
            pacingDelayMs = 300L,
        )

        val progressList = mutableListOf<SyncProgress>()
        val job = launch {
            useCase(sourceId, fullSync = true).toList(progressList)
        }

        // Before 300ms, only page 1 has been processed
        advanceTimeBy(299)
        runCurrent()
        assertEquals(1, progressList.filter { !it.isFinished }.size)
        assertEquals(1, progressList.last().pagesCrawled)

        // At 300ms, pacing delay expires and page 2 is processed
        advanceTimeBy(1)
        runCurrent()
        assertEquals(2, progressList.filter { !it.isFinished }.size)
        assertTrue(progressList.last().isFinished)
        assertEquals(2, progressList.last().pagesCrawled)

        job.join()
    }

    @Test
    fun `network error during harvestCatalog emits progress with error and isFinished true`() = runTest {
        val sourceId = 1L
        val networkError = IOException("Failed to connect to FreeWebNovel")
        val repository = FakeSeriesRepository()
        val persister = FakeBatchPersister()
        val source = FakeHarvesterSource(
            id = sourceId,
            failure = networkError,
        )
        val useCase = SyncSourceCatalog(
            seriesRepository = repository,
            harvester = source,
            batchPersister = persister,
        )

        val progress = useCase(sourceId).toList()

        assertEquals(1, progress.size)
        val errorProgress = progress.first()
        assertTrue(errorProgress.isFinished)
        assertTrue(errorProgress.isError)
        assertEquals(networkError, errorProgress.error)
        assertEquals(0, errorProgress.pagesCrawled)
        assertEquals(0, errorProgress.itemsIngested)
    }

    @Test
    fun `coroutine cancellation cleanly propagates CancellationException`() = runTest {
        val sourceId = 1L
        val cancellation = CancellationException("Sync job was cancelled")
        val repository = FakeSeriesRepository()
        val persister = FakeBatchPersister()
        val source = FakeHarvesterSource(
            id = sourceId,
            failure = cancellation,
        )
        val useCase = SyncSourceCatalog(
            seriesRepository = repository,
            harvester = source,
            batchPersister = persister,
        )

        var caughtCancellation: CancellationException? = null
        val job = launch {
            try {
                useCase(sourceId).toList()
            } catch (e: CancellationException) {
                caughtCancellation = e
                throw e
            }
        }
        job.join()
        assertTrue("Job must be cancelled when CancellationException is thrown", job.isCancelled)
        assertEquals("Sync job was cancelled", caughtCancellation?.message)
    }

    @Test
    fun `batch persister is invoked for non-early-exit pages`() = runTest {
        val sourceId = 1L
        val series1 = createSeries(sourceId, "1", "Series 1")
        val series2 = createSeries(sourceId, "2", "Series 2")

        val repository = FakeSeriesRepository().apply {
            addPersisted(series2)
        }
        val persister = FakeBatchPersister()
        val source = FakeHarvesterSource(
            id = sourceId,
            pages = listOf(
                listOf(series1),
                listOf(series2),
            ),
        )
        val useCase = SyncSourceCatalog(
            seriesRepository = repository,
            harvester = source,
            batchPersister = persister,
            pacingDelayMs = 0L,
        )

        useCase(sourceId, fullSync = false).toList()

        assertEquals(1, persister.persistedBatches.size)
        assertEquals(listOf(series1), persister.persistedBatches[0])
    }

    @Test
    fun `resolving harvester by sourceId works with registered source and emits error for unregistered source`() = runTest {
        val registeredId = 1L
        val unregisteredId = 999L
        val nonHarvesterId = 2L

        val series1 = createSeries(registeredId, "1", "Series 1")
        val repository = FakeSeriesRepository()
        val persister = FakeBatchPersister()
        val harvesterSource = FakeHarvesterSource(id = registeredId, pages = listOf(listOf(series1)))
        val nonHarvesterSource = NonHarvesterSource(id = nonHarvesterId)
        val registry = SourceRegistry(
            mapOf(
                registeredId to harvesterSource,
                nonHarvesterId to nonHarvesterSource,
            ),
        )
        val useCase = SyncSourceCatalog(
            seriesRepository = repository,
            harvesterResolver = { id -> registry[id] as? CatalogHarvester },
            batchPersister = persister,
            pacingDelayMs = 0L,
        )

        // 1. Registered harvester source works as expected
        val registeredProgress = useCase(registeredId).toList()
        assertTrue(registeredProgress.last().isFinished)
        assertFalse(registeredProgress.last().isError)
        assertNull(registeredProgress.last().error)

        // 2. Unregistered source emits error
        val unregisteredProgress = useCase(unregisteredId).toList()
        assertEquals(1, unregisteredProgress.size)
        val unregError = unregisteredProgress.first()
        assertTrue(unregError.isFinished)
        assertTrue(unregError.isError)
        assertNotNull(unregError.error)

        // 3. Registered non-harvester source emits error
        val nonHarvesterProgress = useCase(nonHarvesterId).toList()
        assertEquals(1, nonHarvesterProgress.size)
        val nonHarvesterError = nonHarvesterProgress.first()
        assertTrue(nonHarvesterError.isFinished)
        assertTrue(nonHarvesterError.isError)
        assertNotNull(nonHarvesterError.error)
    }
}
