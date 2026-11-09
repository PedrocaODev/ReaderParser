package com.opus.readerparser.domain.usecase

import com.opus.readerparser.data.source.CatalogHarvester
import com.opus.readerparser.domain.SeriesRepository
import com.opus.readerparser.domain.model.SyncProgress
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

const val DEFAULT_PACING_DELAY_MS: Long = 300L

open class SyncSourceCatalog(
    private val seriesRepository: SeriesRepository,
    private val harvesterResolver: ((Long) -> CatalogHarvester?)? = null,
    private val batchPersister: CatalogBatchPersister? = null,
    private val pacingDelayMs: Long = DEFAULT_PACING_DELAY_MS,
) {
    constructor(
        seriesRepository: SeriesRepository,
        harvester: CatalogHarvester,
        batchPersister: CatalogBatchPersister? = null,
        pacingDelayMs: Long = DEFAULT_PACING_DELAY_MS,
    ) : this(
        seriesRepository = seriesRepository,
        harvesterResolver = { harvester },
        batchPersister = batchPersister,
        pacingDelayMs = pacingDelayMs,
    )

    companion object {
        const val DEFAULT_PACING_DELAY_MS: Long = com.opus.readerparser.domain.usecase.DEFAULT_PACING_DELAY_MS
    }

    open operator fun invoke(sourceId: Long = 1L, fullSync: Boolean = false): Flow<SyncProgress> = flow {
        var pagesCrawled = 0
        var itemsIngested = 0

        try {
            val harvester = harvesterResolver?.invoke(sourceId)
                ?: error("Source $sourceId does not support catalog harvesting")

            invoke(sourceId, harvester, fullSync).collect { progress ->
                pagesCrawled = progress.pagesCrawled
                itemsIngested = progress.itemsIngested
                emit(progress)
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            emit(
                SyncProgress(
                    pagesCrawled = pagesCrawled,
                    itemsIngested = itemsIngested,
                    isFinished = true,
                    error = e,
                ),
            )
        }
    }

    operator fun invoke(harvester: CatalogHarvester, fullSync: Boolean = false): Flow<SyncProgress> =
        invoke(sourceId = 0L, harvester = harvester, fullSync = fullSync)

    operator fun invoke(sourceId: Long, harvester: CatalogHarvester, fullSync: Boolean = false): Flow<SyncProgress> = flow {
        var pagesCrawled = 0
        var itemsIngested = 0

        try {
            var isFirstPage = true
            val catalogFlow = harvester.harvestCatalog()

            catalogFlow.collect { pageBatch ->
                if (!isFirstPage && pacingDelayMs > 0) {
                    delay(pacingDelayMs)
                }
                isFirstPage = false

                if (!fullSync && pageBatch.isNotEmpty()) {
                    val allExist = pageBatch.all { series ->
                        val targetSourceId = if (series.sourceId != 0L) series.sourceId else sourceId
                        seriesRepository.getPersistedSeries(targetSourceId, series.url) != null
                    }
                    if (allExist) {
                        throw EarlyExitException()
                    }
                }

                batchPersister?.persistBatch(pageBatch)
                itemsIngested += pageBatch.size
                pagesCrawled++

                emit(
                    SyncProgress(
                        pagesCrawled = pagesCrawled,
                        itemsIngested = itemsIngested,
                        isFinished = false,
                    ),
                )
            }

            emit(
                SyncProgress(
                    pagesCrawled = pagesCrawled,
                    itemsIngested = itemsIngested,
                    isFinished = true,
                ),
            )
        } catch (e: EarlyExitException) {
            emit(
                SyncProgress(
                    pagesCrawled = pagesCrawled,
                    itemsIngested = itemsIngested,
                    isFinished = true,
                ),
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            emit(
                SyncProgress(
                    pagesCrawled = pagesCrawled,
                    itemsIngested = itemsIngested,
                    isFinished = true,
                    error = e,
                ),
            )
        }
    }

    private class EarlyExitException : CancellationException("Early exit triggered due to 100% catalog overlap")
}
