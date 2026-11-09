package com.opus.readerparser.sources.freewebnovel

import com.opus.readerparser.data.local.database.dao.SeriesDao
import com.opus.readerparser.data.local.database.mappers.toEntity
import com.opus.readerparser.domain.model.Series

/**
 * Batch persistence adapter for FreeWebNovel catalog stubs into Room.
 *
 * Persists series stubs directly into the Room series table with [inLibrary] = false
 * and 0 chapters fetched or inserted (Zero Chapter Invariant).
 * Content-only updates via [SeriesDao.updateDetails] protect existing bookmarked series
 * ([inLibrary] = true), [addedAt] timestamps, and cascading chapter foreign keys.
 */
class FreeWebNovelCatalogBatchPersister(
    private val seriesDao: SeriesDao,
) {
    /**
     * Batch-persists a list of [Series] stubs directly into Room.
     *
     * @return the number of series persisted/updated.
     */
    suspend fun persistBatch(stubs: List<Series>): Int {
        for (series in stubs) {
            val entity = series.toEntity().copy(inLibrary = false, addedAt = null)
            val updated = seriesDao.updateDetails(
                sourceId = entity.sourceId,
                url = entity.url,
                title = entity.title,
                author = entity.author,
                artist = entity.artist,
                description = entity.description,
                coverUrl = entity.coverUrl,
                genresJson = entity.genresJson,
                status = entity.status,
                type = entity.type,
            )
            if (updated == 0) {
                seriesDao.insert(entity)
            }
        }
        return stubs.size
    }
}
