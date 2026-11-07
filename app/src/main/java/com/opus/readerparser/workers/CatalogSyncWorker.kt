package com.opus.readerparser.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.opus.readerparser.domain.usecase.SyncSourceCatalog
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

/**
 * Background WorkManager worker that periodically syncs the catalog for [DEFAULT_SOURCE_ID].
 *
 * Runs under unmetered Wi-Fi and charging constraints every [REPEAT_INTERVAL_HOURS] hours.
 * Emits no notifications or UI blocks, updating Room series stubs silently.
 */
@HiltWorker
class CatalogSyncWorker @AssistedInject constructor(
    @Assisted ctx: Context,
    @Assisted params: WorkerParameters,
    private val syncSourceCatalog: SyncSourceCatalog,
) : CoroutineWorker(ctx, params) {

    override suspend fun doWork(): Result = try {
        var hasError = false
        syncSourceCatalog(sourceId = DEFAULT_SOURCE_ID, fullSync = false).collect { progress ->
            if (progress.error != null) {
                Log.w(TAG, "Catalog sync emitted error progress: ${progress.error}")
                hasError = true
            }
        }
        if (hasError) {
            if (runAttemptCount < 2) Result.retry() else Result.failure()
        } else {
            Result.success()
        }
    } catch (e: Exception) {
        Log.e(TAG, "Catalog sync worker encountered uncaught exception", e)
        if (runAttemptCount < 2) Result.retry() else Result.failure()
    }

    companion object {
        const val TAG = "catalog-sync"
        const val WORK_NAME = "catalog-sync"
        const val DEFAULT_SOURCE_ID: Long = 1L
        const val REPEAT_INTERVAL_HOURS: Long = 24L
        const val BACKOFF_DELAY_SECONDS: Long = 30L

        val CONSTRAINTS: Constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .setRequiresCharging(true)
            .build()

        fun buildPeriodicRequest(): PeriodicWorkRequest =
            PeriodicWorkRequestBuilder<CatalogSyncWorker>(REPEAT_INTERVAL_HOURS, TimeUnit.HOURS)
                .setConstraints(CONSTRAINTS)
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, BACKOFF_DELAY_SECONDS, TimeUnit.SECONDS)
                .addTag(TAG)
                .build()

        fun schedule(
            context: Context,
            policy: ExistingPeriodicWorkPolicy = ExistingPeriodicWorkPolicy.KEEP,
        ) {
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                policy,
                buildPeriodicRequest(),
            )
        }
    }
}
