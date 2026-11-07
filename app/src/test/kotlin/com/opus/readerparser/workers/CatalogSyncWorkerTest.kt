package com.opus.readerparser.workers

import androidx.work.BackoffPolicy
import androidx.work.NetworkType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.concurrent.TimeUnit

class CatalogSyncWorkerTest {

    @Test
    fun constantsIntegrity() {
        assertEquals("catalog-sync", CatalogSyncWorker.TAG)
        assertEquals("catalog-sync", CatalogSyncWorker.WORK_NAME)
        assertEquals(1L, CatalogSyncWorker.DEFAULT_SOURCE_ID)
        assertEquals(24L, CatalogSyncWorker.REPEAT_INTERVAL_HOURS)
        assertEquals(30L, CatalogSyncWorker.BACKOFF_DELAY_SECONDS)
    }

    @Test
    fun constraintsIntegrity() {
        val constraints = CatalogSyncWorker.CONSTRAINTS
        assertEquals(NetworkType.UNMETERED, constraints.requiredNetworkType)
        assertTrue(constraints.requiresCharging())
    }

    @Test
    fun buildPeriodicRequest_hasCorrectConstraintsAndWorkSpec() {
        val request = CatalogSyncWorker.buildPeriodicRequest()
        val workSpec = request.workSpec

        assertEquals(NetworkType.UNMETERED, workSpec.constraints.requiredNetworkType)
        assertTrue(workSpec.constraints.requiresCharging())
        assertTrue(request.tags.contains(CatalogSyncWorker.TAG))
        assertEquals(
            TimeUnit.HOURS.toMillis(CatalogSyncWorker.REPEAT_INTERVAL_HOURS),
            workSpec.intervalDuration,
        )
        assertEquals(BackoffPolicy.EXPONENTIAL, workSpec.backoffPolicy)
        assertEquals(
            TimeUnit.SECONDS.toMillis(CatalogSyncWorker.BACKOFF_DELAY_SECONDS),
            workSpec.backoffDelayDuration,
        )
    }
}
