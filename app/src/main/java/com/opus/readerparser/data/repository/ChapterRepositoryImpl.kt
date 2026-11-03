package com.opus.readerparser.data.repository

import android.util.Log
import com.opus.readerparser.core.util.SourceMetadataCache
import com.opus.readerparser.data.local.database.dao.ChapterDao
import com.opus.readerparser.data.local.database.entities.ChapterEntity
import com.opus.readerparser.data.local.database.mappers.toChapterWithState
import com.opus.readerparser.data.local.database.mappers.toDomain
import com.opus.readerparser.data.local.database.mappers.toEntity
import com.opus.readerparser.data.local.filesystem.DownloadStore
import com.opus.readerparser.data.source.SourceRegistry
import com.opus.readerparser.domain.ChapterRepository
import com.opus.readerparser.domain.model.Chapter
import com.opus.readerparser.domain.model.ChapterContent
import com.opus.readerparser.domain.model.ChapterWithState
import com.opus.readerparser.domain.model.Series
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChapterRepositoryImpl @Inject constructor(
    private val sourceRegistry: SourceRegistry,
    private val chapterDao: ChapterDao,
    private val downloadStore: DownloadStore,
    private val chapterListCache: SourceMetadataCache<List<Chapter>> = defaultChapterListCache(),
) : ChapterRepository {

    override fun observeChapters(series: Series): Flow<List<ChapterWithState>> =
        chapterDao.observeChapters(series.sourceId, series.url).map { entities ->
            entities.map { it.toChapterWithState() }
        }

    override suspend fun refreshChapters(series: Series) {
        val cacheKey = chapterListCacheKey(series.sourceId, series.url)
        chapterListCache.get(cacheKey)?.let { return }

        val source = sourceRegistry[series.sourceId]
        val initialExisting = chapterDao.getChaptersForSeries(series.sourceId, series.url)
        var latestChapters = emptyList<Chapter>()

        source.fetchChapterListProgressive(series).collect { remoteChapters ->
            val remoteEntities = mapToEntitiesPreservingState(remoteChapters, initialExisting)
            chapterDao.replaceChaptersForSeries(series.sourceId, series.url, remoteEntities)
            if (remoteChapters.isNotEmpty()) {
                latestChapters = remoteChapters
            }
        }

        if (latestChapters.isNotEmpty()) {
            chapterListCache.put(cacheKey, latestChapters.toList())
        }
    }

    override suspend fun findByUrl(sourceId: Long, url: String): Chapter? =
        chapterDao.getByUrl(sourceId, url)?.toDomain()

    override suspend fun getContent(chapter: Chapter, forceNetwork: Boolean): ChapterContent {
        if (!forceNetwork) {
            // Offline-first: try local cache before hitting the network.
            // Corrupt or unreadable cache entries are treated as a miss
            // so the caller falls back to the remote source.
            try {
                val cached = downloadStore.read(chapter)
                if (cached != null) return cached
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.w(TAG, "Failed to read cached content for ${chapter.url}, falling back to network", e)
            }
        }
        return sourceRegistry[chapter.sourceId].getChapterContent(chapter)
    }

    override suspend fun markRead(chapter: Chapter, read: Boolean) {
        chapterDao.markRead(chapter.sourceId, chapter.url, read)
    }

    override suspend fun markPreviousAsRead(series: Series, targetChapterNumber: Float) {
        chapterDao.markPreviousAsRead(series.sourceId, series.url, targetChapterNumber)
    }

    override suspend fun markAllRead(series: Series, read: Boolean) {
        chapterDao.markAllRead(series.sourceId, series.url, read)
    }

    override suspend fun setProgress(chapter: Chapter, progress: Float) {
        chapterDao.setProgress(chapter.sourceId, chapter.url, progress)
    }

    override suspend fun markDownloaded(chapter: Chapter, downloaded: Boolean) {
        chapterDao.markDownloaded(chapter.sourceId, chapter.url, downloaded)
    }

    companion object {
        private const val TAG = "ChapterRepositoryImpl"
    }
}

private fun chapterListCacheKey(sourceId: Long, seriesUrl: String): String = "$sourceId:$seriesUrl"

private fun defaultChapterListCache(): SourceMetadataCache<List<Chapter>> = SourceMetadataCache(
    maxEntries = 20,
    ttlMs = java.util.concurrent.TimeUnit.MINUTES.toMillis(2),
)

private fun mapToEntitiesPreservingState(
    remoteChapters: List<Chapter>,
    initialExisting: List<ChapterEntity>,
): List<ChapterEntity> {
    val existingByUrl = initialExisting.associateBy { it.url }
    val remoteUrls = remoteChapters.map { it.url }.toSet()

    val unmatchedExistingByNumber = initialExisting
        .filter { it.url !in remoteUrls && it.number >= 0f }
        .groupBy { it.number }
        .mapValues { (_, list) -> list.singleOrNull() }
        .filterValues { it != null }
        .mapValues { it.value!! }

    val unmatchedIncomingNumberCounts = remoteChapters
        .filter { it.url !in existingByUrl && it.number >= 0f }
        .groupingBy { it.number }
        .eachCount()

    return remoteChapters.map { chapter ->
        val entity = chapter.toEntity()
        val prev = existingByUrl[entity.url]
            ?: if (entity.number >= 0f && unmatchedIncomingNumberCounts[entity.number] == 1) {
                unmatchedExistingByNumber[entity.number]
            } else {
                null
            }
        if (prev != null) {
            entity.copy(
                read = prev.read,
                progress = prev.progress,
                downloaded = prev.downloaded,
            )
        } else {
            entity
        }
    }
}
