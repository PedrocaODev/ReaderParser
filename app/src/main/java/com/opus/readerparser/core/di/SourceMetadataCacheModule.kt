package com.opus.readerparser.core.di

import com.opus.readerparser.core.util.SourceMetadataCache
import com.opus.readerparser.domain.model.Chapter
import com.opus.readerparser.domain.model.Series
import com.opus.readerparser.domain.model.SeriesPage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SourceMetadataCacheModule {

    @Provides
    @Singleton
    fun provideSeriesPageCache(): SourceMetadataCache<SeriesPage> = SourceMetadataCache(
        maxEntries = 100,
        ttlMs = TimeUnit.MINUTES.toMillis(5),
    )

    @Provides
    @Singleton
    fun provideSeriesDetailsCache(): SourceMetadataCache<Series> = SourceMetadataCache(
        maxEntries = 50,
        ttlMs = TimeUnit.MINUTES.toMillis(15),
    )

    @Provides
    @Singleton
    fun provideChapterListCache(): SourceMetadataCache<List<Chapter>> = SourceMetadataCache(
        maxEntries = 20,
        ttlMs = TimeUnit.MINUTES.toMillis(2),
    )
}
