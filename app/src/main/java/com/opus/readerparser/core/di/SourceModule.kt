package com.opus.readerparser.core.di

import com.opus.readerparser.data.local.database.dao.SeriesDao
import com.opus.readerparser.data.source.CatalogHarvester
import com.opus.readerparser.data.source.SourceRegistry
import com.opus.readerparser.domain.SeriesRepository
import com.opus.readerparser.domain.usecase.SyncSourceCatalog
import com.opus.readerparser.sources.asurascans.AsuraScans
import com.opus.readerparser.sources.freewebnovel.FreeWebNovel
import com.opus.readerparser.sources.freewebnovel.FreeWebNovelCatalogBatchPersister
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import javax.inject.Singleton

/**
 * Hilt module providing the compile-time [SourceRegistry].
 *
 * Add new [com.opus.readerparser.data.source.Source] implementations in the
 * [registry] provider below.
 */
@Module
@InstallIn(SingletonComponent::class)
object SourceModule {

    @Provides
    @Singleton
    fun registry(client: HttpClient): SourceRegistry = SourceRegistry(
        listOf(
            AsuraScans(client),
            FreeWebNovel(client),
        ).associateBy { it.id },
    )

    @Provides
    @Singleton
    fun provideSyncSourceCatalog(
        seriesRepository: SeriesRepository,
        sourceRegistry: SourceRegistry,
        seriesDao: SeriesDao,
    ): SyncSourceCatalog = SyncSourceCatalog(
        seriesRepository = seriesRepository,
        harvesterResolver = { id ->
            (sourceRegistry.all().firstOrNull { it is CatalogHarvester && (id == 1L || it.id == id) } as? CatalogHarvester)
                ?: runCatching { sourceRegistry[id] as? CatalogHarvester }.getOrNull()
        },
        batchPersister = { stubs -> FreeWebNovelCatalogBatchPersister(seriesDao).persistBatch(stubs) },
    )
}
