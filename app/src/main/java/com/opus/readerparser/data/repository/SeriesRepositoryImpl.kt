package com.opus.readerparser.data.repository

import com.opus.readerparser.core.util.TitleMatcher
import com.opus.readerparser.core.util.SourceMetadataCache
import com.opus.readerparser.data.local.database.dao.SeriesDao
import com.opus.readerparser.data.local.database.mappers.toDomain
import com.opus.readerparser.data.local.database.mappers.toEntity
import com.opus.readerparser.data.local.search.SamsungSearchClient
import com.opus.readerparser.data.local.search.SamsungSearchHit
import com.opus.readerparser.data.local.search.SamsungSearchQueryResult
import com.opus.readerparser.data.source.SourceRegistry
import com.opus.readerparser.domain.SeriesRepository
import com.opus.readerparser.domain.model.Filter
import com.opus.readerparser.domain.model.FilterList
import com.opus.readerparser.domain.model.LibrarySearchResult
import com.opus.readerparser.domain.model.Series
import com.opus.readerparser.domain.model.SeriesPage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SeriesRepositoryImpl @Inject constructor(
    private val sourceRegistry: SourceRegistry,
    private val seriesDao: SeriesDao,
    private val samsungSearchClient: SamsungSearchClient,
    private val catalogSearchCache: SourceMetadataCache<SeriesPage> = defaultCatalogSearchCache(),
    private val detailCache: SourceMetadataCache<Series> = defaultDetailCache(),
) : SeriesRepository {

    override fun observeLibrary(): Flow<List<Series>> =
        seriesDao.observeLibrary().map { entities ->
            entities.map { it.toDomain() }
        }

    override fun observeLibrarySearchInvalidations(): Flow<Unit> =
        combine(seriesDao.observeLibrary(), seriesDao.observeIndexableSeries()) { _, _ -> Unit }
            .drop(1)

    override suspend fun fetchPopular(sourceId: Long, page: Int): SeriesPage {
        val cacheKey = catalogCacheKey(sourceId, "popular", page)
        catalogSearchCache.get(cacheKey)?.let { return it }

        val result = sourceRegistry[sourceId].getPopular(page)
        result.series.forEach { saveSeries(it) }
        if (result.series.isNotEmpty()) {
            catalogSearchCache.put(cacheKey, result.snapshot())
        }
        return result
    }

    override suspend fun fetchLatest(sourceId: Long, page: Int): SeriesPage {
        val cacheKey = catalogCacheKey(sourceId, "latest", page)
        catalogSearchCache.get(cacheKey)?.let { return it }

        val result = sourceRegistry[sourceId].getLatest(page)
        result.series.forEach { saveSeries(it) }
        if (result.series.isNotEmpty()) {
            catalogSearchCache.put(cacheKey, result.snapshot())
        }
        return result
    }

    override suspend fun search(
        sourceId: Long,
        query: String,
        page: Int,
        filters: FilterList,
    ): SeriesPage {
        val cacheKey = searchCacheKey(sourceId, query, page, filters)
        catalogSearchCache.get(cacheKey)?.let { return it }

        val result = sourceRegistry[sourceId].search(query, page, filters)
        result.series.forEach { saveSeries(it) }

        // Fallback: if remote returned empty for page 1 with a non-blank query,
        // search locally cached series for this source using the title matcher.
        if (result.series.isEmpty() && page == 1 && query.isNotBlank()) {
            val cached = seriesDao.getBySourceId(sourceId).map { it.toDomain() }
            val matched = cached
                .filter { TitleMatcher.matches(query, it.title) }
                .sortedBy { it.title }
            return SeriesPage(matched, hasNextPage = false)
        }

        if (result.series.isNotEmpty()) {
            catalogSearchCache.put(cacheKey, result.snapshot())
        }

        return result
    }

    override suspend fun searchLibrary(query: String): LibrarySearchResult {
        val trimmedQuery = query.trim()
        if (trimmedQuery.isBlank()) return LibrarySearchResult.Success(emptyList())

        val eligibleSeries = seriesDao.getLibraryIndexableSeries()

        return when (val result = samsungSearchClient.query(trimmedQuery)) {
            is SamsungSearchQueryResult.Success -> LibrarySearchResult.Success(
                eligibleSeries
                    .associateBy { it.sourceId to it.url }
                    .let { eligible ->
                        result.hits.mapNotNull { hit ->
                            hit.toLookupKey()?.let { (sourceId, url) ->
                                eligible[sourceId to url]?.toDomain()
                            }
                        }
                    },
            )
            is SamsungSearchQueryResult.Failure -> LibrarySearchResult.Success(
                eligibleSeries
                    .map { it.toDomain() }
                    .filter { it.matchesLibraryQuery(trimmedQuery) },
            )
        }
    }

    override suspend fun refreshDetails(series: Series): Series {
        val cacheKey = detailCacheKey(series.sourceId, series.url)
        detailCache.get(cacheKey)?.let { return it }

        val updated = sourceRegistry[series.sourceId].getSeriesDetails(series)
        if (updated.title.isNotBlank()) {
            saveSeries(updated)
            detailCache.put(cacheKey, updated.snapshot())
        }
        return updated
    }

    override suspend fun addToLibrary(series: Series) {
        val toSave = if (series.title.isBlank()) refreshDetails(series) else series
        if (toSave.title.isBlank() && seriesDao.getByUrl(toSave.sourceId, toSave.url) == null) {
            seriesDao.insert(toSave.toEntity())
        }
        seriesDao.addToLibrary(toSave.sourceId, toSave.url, System.currentTimeMillis())
    }

    override suspend fun removeFromLibrary(series: Series) {
        seriesDao.removeFromLibrary(series.sourceId, series.url)
    }

    override suspend fun isInLibrary(sourceId: Long, url: String): Boolean =
        seriesDao.getByUrl(sourceId, url)?.inLibrary ?: false

    private suspend fun saveSeries(series: Series) {
        val entity = series.toEntity()
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

    private fun SamsungSearchHit.toLookupKey(): Pair<Long, String>? {
        val sourceId = id.substringBefore(':').toLongOrNull() ?: return null
        val url = id.substringAfter(':', missingDelimiterValue = "")
        if (url.isBlank()) return null
        return sourceId to url
    }

    private fun Series.matchesLibraryQuery(query: String): Boolean =
        TitleMatcher.matches(query, title) ||
            author?.let { TitleMatcher.matches(query, it) } == true ||
            genres.any { TitleMatcher.matches(query, it) }
}

private fun catalogCacheKey(sourceId: Long, operation: String, page: Int): String =
    "$sourceId:$operation:$page"

private fun searchCacheKey(
    sourceId: Long,
    query: String,
    page: Int,
    filters: FilterList,
): String {
    val nq = normalizedQuery(query)
    val fs = filters.toSnapshot()
    return "$sourceId:search:${nq.length}:$nq:$page:${fs.length}:$fs"
}

private fun detailCacheKey(sourceId: Long, url: String): String = "$sourceId:$url"

private fun normalizedQuery(query: String): String =
    query.trim().lowercase().replace(WHITESPACE, " ")

private fun FilterList.toSnapshot(): String = filters.joinToString(separator = "\u001f") { filter ->
    when (filter) {
        is Filter.Text -> filter.snapshot("text")
        is Filter.Select -> filter.snapshot("select")
        is Filter.Toggle -> "toggle:${filter.key.length}:${filter.key}:${filter.value}"
    }
}

private fun Filter.Text.snapshot(type: String): String =
    "$type:${key.length}:$key:${value.length}:$value"

private fun Filter.Select.snapshot(type: String): String =
    "$type:${key.length}:$key:${value.length}:$value"

private fun SeriesPage.snapshot(): SeriesPage = copy(series = series.toList())

private fun Series.snapshot(): Series = copy(genres = genres.toList())

private val WHITESPACE = Regex("\\s+")

private fun defaultCatalogSearchCache(): SourceMetadataCache<SeriesPage> = SourceMetadataCache(
    maxEntries = 100,
    ttlMs = java.util.concurrent.TimeUnit.MINUTES.toMillis(5),
)

private fun defaultDetailCache(): SourceMetadataCache<Series> = SourceMetadataCache(
    maxEntries = 50,
    ttlMs = java.util.concurrent.TimeUnit.MINUTES.toMillis(15),
)
