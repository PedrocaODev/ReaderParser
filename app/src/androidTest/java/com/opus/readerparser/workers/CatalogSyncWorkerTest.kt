package com.opus.readerparser.workers

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.work.ListenableWorker
import androidx.work.WorkManager
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import androidx.work.testing.TestListenableWorkerBuilder
import androidx.work.testing.WorkManagerTestInitHelper
import com.opus.readerparser.domain.SeriesRepository
import com.opus.readerparser.domain.model.FilterList
import com.opus.readerparser.domain.model.LibrarySearchResult
import com.opus.readerparser.domain.model.Series
import com.opus.readerparser.domain.model.SeriesPage
import com.opus.readerparser.domain.model.SyncProgress
import com.opus.readerparser.domain.usecase.SyncSourceCatalog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CatalogSyncWorkerTest {

    private class DummySeriesRepository : SeriesRepository {
        override fun observeLibrary(): Flow<List<Series>> = emptyFlow()
        override fun observeLibrarySearchInvalidations(): Flow<Unit> = emptyFlow()
        override suspend fun fetchPopular(sourceId: Long, page: Int): SeriesPage = SeriesPage(emptyList(), false)
        override suspend fun fetchLatest(sourceId: Long, page: Int): SeriesPage = SeriesPage(emptyList(), false)
        override suspend fun search(sourceId: Long, query: String, page: Int, filters: FilterList): SeriesPage = SeriesPage(emptyList(), false)
        override suspend fun searchLibrary(query: String): LibrarySearchResult = LibrarySearchResult(emptyList(), emptyList())
        override suspend fun refreshDetails(series: Series): Series = series
        override suspend fun getPersistedSeries(sourceId: Long, url: String): Series? = null
        override suspend fun addToLibrary(series: Series) {}
        override suspend fun removeFromLibrary(series: Series) {}
        override suspend fun isInLibrary(sourceId: Long, url: String): Boolean = false
        override suspend fun searchCatalog(sourceId: Long, query: String): List<Series> = emptyList()
    }

    private class FakeSyncSourceCatalog(
        var progressFlow: Flow<SyncProgress>? = null,
        var exceptionToThrow: Throwable? = null,
    ) : SyncSourceCatalog(seriesRepository = DummySeriesRepository()) {

        var invokedWithSourceId: Long? = null
        var invokedWithFullSync: Boolean? = null

        override fun invoke(sourceId: Long, fullSync: Boolean): Flow<SyncProgress> {
            invokedWithSourceId = sourceId
            invokedWithFullSync = fullSync
            exceptionToThrow?.let { throw it }
            return progressFlow ?: flowOf(
                SyncProgress(
                    pagesCrawled = 1,
                    itemsIngested = 10,
                    isFinished = true,
                ),
            )
        }
    }

    private fun createWorker(
        fakeSyncSourceCatalog: FakeSyncSourceCatalog,
        runAttemptCount: Int = 0,
    ): CatalogSyncWorker {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val factory = object : WorkerFactory() {
            override fun createWorker(
                appContext: Context,
                workerClassName: String,
                workerParameters: WorkerParameters,
            ): ListenableWorker = CatalogSyncWorker(context, workerParameters, fakeSyncSourceCatalog)
        }
        return TestListenableWorkerBuilder.from(
            context,
            CatalogSyncWorker::class.java,
        ).setWorkerFactory(factory).setRunAttemptCount(runAttemptCount).build()
    }

    @Test
    fun success_returnsResultSuccess() = runTest {
        val fake = FakeSyncSourceCatalog()
        val worker = createWorker(fake)
        val result = worker.startWork().get()
        assertEquals(ListenableWorker.Result.success(), result)
        assertEquals(CatalogSyncWorker.DEFAULT_SOURCE_ID, fake.invokedWithSourceId)
        assertEquals(false, fake.invokedWithFullSync)
    }

    @Test
    fun errorProgress_firstAttempt_returnsRetry() = runTest {
        val fake = FakeSyncSourceCatalog(
            progressFlow = flowOf(
                SyncProgress(
                    pagesCrawled = 1,
                    itemsIngested = 0,
                    isFinished = true,
                    error = RuntimeException("Network error"),
                ),
            ),
        )
        val worker = createWorker(fake, runAttemptCount = 0)
        val result = worker.startWork().get()
        assertEquals(ListenableWorker.Result.retry(), result)
    }

    @Test
    fun errorProgress_secondAttempt_returnsRetry() = runTest {
        val fake = FakeSyncSourceCatalog(
            progressFlow = flowOf(
                SyncProgress(
                    pagesCrawled = 1,
                    itemsIngested = 0,
                    isFinished = true,
                    error = RuntimeException("Network error"),
                ),
            ),
        )
        val worker = createWorker(fake, runAttemptCount = 1)
        val result = worker.startWork().get()
        assertEquals(ListenableWorker.Result.retry(), result)
    }

    @Test
    fun errorProgress_thirdAttempt_returnsFailure() = runTest {
        val fake = FakeSyncSourceCatalog(
            progressFlow = flowOf(
                SyncProgress(
                    pagesCrawled = 1,
                    itemsIngested = 0,
                    isFinished = true,
                    error = RuntimeException("Network error"),
                ),
            ),
        )
        val worker = createWorker(fake, runAttemptCount = 2)
        val result = worker.startWork().get()
        assertEquals(ListenableWorker.Result.failure(), result)
    }

    @Test
    fun uncaughtException_firstAttempt_returnsRetry() = runTest {
        val fake = FakeSyncSourceCatalog(
            exceptionToThrow = RuntimeException("Fatal crash"),
        )
        val worker = createWorker(fake, runAttemptCount = 0)
        val result = worker.startWork().get()
        assertEquals(ListenableWorker.Result.retry(), result)
    }

    @Test
    fun uncaughtException_thirdAttempt_returnsFailure() = runTest {
        val fake = FakeSyncSourceCatalog(
            exceptionToThrow = RuntimeException("Fatal crash"),
        )
        val worker = createWorker(fake, runAttemptCount = 2)
        val result = worker.startWork().get()
        assertEquals(ListenableWorker.Result.failure(), result)
    }

    @Test
    fun schedule_enqueuesUniqueWork() = runTest {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        WorkManagerTestInitHelper.initializeTestWorkManager(context)

        CatalogSyncWorker.schedule(context)

        val workManager = WorkManager.getInstance(context)
        val workInfos = workManager.getWorkInfosByTag(CatalogSyncWorker.TAG).get()
        assertTrue(workInfos.isNotEmpty())
        assertTrue(workInfos.any { it.tags.contains(CatalogSyncWorker.TAG) })

        val uniqueWorkInfos = workManager.getWorkInfosForUniqueWork(CatalogSyncWorker.WORK_NAME).get()
        assertTrue(uniqueWorkInfos.isNotEmpty())
    }
}
