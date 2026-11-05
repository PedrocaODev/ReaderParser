package com.opus.readerparser.fakes

import com.opus.readerparser.domain.DownloadRepository
import com.opus.readerparser.domain.model.DownloadItem
import com.opus.readerparser.domain.model.DownloadState
import com.opus.readerparser.domain.model.DownloadStorageBreakdown
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeDownloadRepository : DownloadRepository {

    var storageBreakdown: DownloadStorageBreakdown = DownloadStorageBreakdown()
    val seriesBytes: MutableMap<Pair<Long, String>, Long> = mutableMapOf()
    var getStorageBreakdownCallCount: Int = 0
    var getSeriesBytesCallCount: Int = 0

    fun setSeriesBytes(sourceId: Long, seriesUrl: String, bytes: Long) {
        seriesBytes[sourceId to seriesUrl] = bytes
    }

    private val _queue = MutableStateFlow<List<DownloadItem>>(emptyList())

    val cancelCalls: MutableList<Pair<Long, String>> = mutableListOf()
    val retryCalls: MutableList<Pair<Long, String>> = mutableListOf()
    val cancelBatchCalls: MutableList<Pair<Long, Set<String>>> = mutableListOf()
    val deleteDownloadCalls: MutableList<Pair<Long, String>> = mutableListOf()

    data class UpdateStateCall(
        val sourceId: Long,
        val chapterUrl: String,
        val state: DownloadState,
        val progress: Float,
        val errorMessage: String?,
    )
    val updateQueueStateCalls: MutableList<UpdateStateCall> = mutableListOf()

    fun setQueue(items: List<DownloadItem>) {
        _queue.value = items
    }

    override fun observeQueue(): Flow<List<DownloadItem>> = _queue

    override suspend fun cancel(sourceId: Long, chapterUrl: String) {
        cancelCalls.add(sourceId to chapterUrl)
        _queue.value = _queue.value.filter { it.sourceId != sourceId || it.chapterUrl != chapterUrl }
    }

    override suspend fun retry(sourceId: Long, chapterUrl: String) {
        retryCalls.add(sourceId to chapterUrl)
        _queue.value = _queue.value.map {
            if (it.sourceId == sourceId && it.chapterUrl == chapterUrl) {
                it.copy(state = DownloadState.QUEUED, progress = 0f, errorMessage = null)
            } else {
                it
            }
        }
    }

    override suspend fun updateQueueState(
        sourceId: Long,
        chapterUrl: String,
        state: DownloadState,
        progress: Float,
        errorMessage: String?,
    ) {
        updateQueueStateCalls.add(UpdateStateCall(sourceId, chapterUrl, state, progress, errorMessage))
        _queue.value = _queue.value.map {
            if (it.sourceId == sourceId && it.chapterUrl == chapterUrl) {
                it.copy(state = state, progress = progress, errorMessage = errorMessage)
            } else {
                it
            }
        }
    }

    override suspend fun cancelBatch(sourceId: Long, chapterUrls: Set<String>) {
        cancelBatchCalls.add(sourceId to chapterUrls)
        _queue.value = _queue.value.filter {
            it.sourceId != sourceId || it.chapterUrl !in chapterUrls
        }
    }

    override suspend fun deleteDownload(sourceId: Long, chapterUrl: String) {
        deleteDownloadCalls.add(sourceId to chapterUrl)
        _queue.value = _queue.value.filter {
            it.sourceId != sourceId || it.chapterUrl != chapterUrl
        }
    }

    override suspend fun getStorageBreakdown(): DownloadStorageBreakdown {
        getStorageBreakdownCallCount++
        return storageBreakdown
    }

    override suspend fun getSeriesBytes(sourceId: Long, seriesUrl: String): Long {
        getSeriesBytesCallCount++
        return seriesBytes[sourceId to seriesUrl] ?: 0L
    }
}
