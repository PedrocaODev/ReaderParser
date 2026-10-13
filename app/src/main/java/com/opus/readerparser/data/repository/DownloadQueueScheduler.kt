package com.opus.readerparser.data.repository

import androidx.work.ExistingWorkPolicy
import com.opus.readerparser.core.util.hashUrl
import com.opus.readerparser.data.local.database.dao.DownloadQueueDao
import com.opus.readerparser.domain.model.DownloadState
import com.opus.readerparser.workers.ChapterDownloadWorker
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DownloadQueueScheduler @Inject constructor(
    private val dao: DownloadQueueDao,
    private val workManager: WorkManagerHelper,
) {
    private val mutex = Mutex()
    private var scheduledChapter: Pair<Long, String>? = null

    suspend fun scheduleNext() = mutex.withLock {
        val running = dao.getRunning()
        if (running != null) {
            scheduledChapter = running.sourceId to running.chapterUrl
            return
        }

        val active = scheduledChapter
        if (active != null) {
            val state = dao.getState(active.first, active.second)
            if (state == DownloadState.QUEUED.name || state == DownloadState.RUNNING.name) {
                return
            }
            scheduledChapter = null
        }

        val next = dao.getNextQueued() ?: return

        scheduledChapter = next.sourceId to next.chapterUrl
        val workName = "download-${next.sourceId}-${hashUrl(next.chapterUrl)}"
        val request = ChapterDownloadWorker.buildRequest(next.sourceId, next.chapterUrl)
        workManager.enqueueUniqueWork(workName, ExistingWorkPolicy.KEEP, request)
    }

    suspend fun onChapterFinished(sourceId: Long, chapterUrl: String) = mutex.withLock {
        if (scheduledChapter == (sourceId to chapterUrl)) {
            scheduledChapter = null
        }
    }

    suspend fun onChapterCancelled(sourceId: Long, chapterUrl: String) = mutex.withLock {
        if (scheduledChapter == (sourceId to chapterUrl)) {
            scheduledChapter = null
        }
    }
}
