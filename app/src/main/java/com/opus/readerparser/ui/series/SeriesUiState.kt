package com.opus.readerparser.ui.series

import com.opus.readerparser.domain.model.Chapter
import com.opus.readerparser.domain.model.ChapterWithState
import com.opus.readerparser.domain.model.ContentType
import com.opus.readerparser.domain.model.Series

sealed interface ReadingResumeState {
    data object None : ReadingResumeState
    data class Start(val targetChapter: Chapter) : ReadingResumeState
    data class Resume(val targetChapter: Chapter, val percent: Int) : ReadingResumeState
    data class Continue(val targetChapter: Chapter, val lastCompletedNumber: Float) : ReadingResumeState
    data class Completed(val firstChapter: Chapter) : ReadingResumeState
}

data class SeriesUiState(
    val series: Series? = null,
    val chapters: List<ChapterWithState> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val inLibrary: Boolean = false,
    val showRangePicker: Boolean = false,
) {
    val readChapterCount: Int get() = chapters.count { it.read }
    val totalChapterCount: Int get() = chapters.size
    val resumeState: ReadingResumeState get() = computeResumeState(chapters)
}

internal fun computeResumeState(chapters: List<ChapterWithState>): ReadingResumeState {
    if (chapters.isEmpty()) return ReadingResumeState.None
    val sorted = chapters.sortedBy { it.chapter.number }
    val inProgress = sorted.firstOrNull { !it.read && it.progress > 0f }
    if (inProgress != null) {
        return ReadingResumeState.Resume(inProgress.chapter, (inProgress.progress * 100).toInt())
    }
    if (sorted.all { it.read }) {
        return ReadingResumeState.Completed(sorted.first().chapter)
    }
    if (sorted.any { it.read }) {
        val lastCompleted = sorted.filter { it.read }.maxByOrNull { it.chapter.number }!!
        val nextUnread = sorted.firstOrNull { !it.read && it.chapter.number > lastCompleted.chapter.number }
            ?: sorted.firstOrNull { !it.read }
        if (nextUnread != null) {
            return ReadingResumeState.Continue(nextUnread.chapter, lastCompleted.chapter.number)
        }
    }
    return ReadingResumeState.Start(sorted.first().chapter)
}

sealed interface SeriesAction {
    data object Refresh : SeriesAction
    data class ToggleLibrary(val inLibrary: Boolean) : SeriesAction
    data class OpenChapter(val chapter: Chapter) : SeriesAction
    data object ResumeReading : SeriesAction
    data object DownloadUnread : SeriesAction
    data object ShowRangePicker : SeriesAction
    data object DismissRangePicker : SeriesAction
    data class DownloadRange(val startIndex: Int, val endIndex: Int) : SeriesAction
    data class ToggleChapterRead(val chapter: Chapter) : SeriesAction
    data class MarkPreviousRead(val chapter: Chapter) : SeriesAction
    data object MarkAllRead : SeriesAction
    data object MarkAllUnread : SeriesAction
}

sealed interface SeriesEffect {
    data class NavigateToReader(val chapter: Chapter, val type: ContentType) : SeriesEffect
    data class ShowError(val message: String) : SeriesEffect
    data class ShowSnackbar(val message: String) : SeriesEffect
}
