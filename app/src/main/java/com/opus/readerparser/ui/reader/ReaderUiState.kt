package com.opus.readerparser.ui.reader

import com.opus.readerparser.domain.model.AppSettings
import com.opus.readerparser.domain.model.Chapter
import com.opus.readerparser.domain.model.ChapterWithState
import com.opus.readerparser.domain.model.ContentType
import com.opus.readerparser.domain.model.ManhwaLayout
import com.opus.readerparser.domain.model.ManhwaZoom

data class ReaderUiState(
    val chapter: Chapter? = null,
    val chapterUrl: String? = null,
    val seriesChapters: List<ChapterWithState> = emptyList(),
    val settings: AppSettings = AppSettings(),
    val contentType: ContentType = ContentType.NOVEL,
    val html: String = "",
    val pages: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val progress: Float = 0f,
    val currentPage: Int = 0,
    val hasPreviousChapter: Boolean = false,
    val hasNextChapter: Boolean = false,
    val seriesTitle: String = "",
    val sourceName: String = "",
    val pageRetryTokens: Map<Int, Int> = emptyMap(),
)

sealed interface ReaderAction {
    data class Load(val chapter: Chapter) : ReaderAction
    data class SelectChapter(val chapter: Chapter) : ReaderAction
    data class SetProgress(val progress: Float) : ReaderAction
    data class SetPage(val page: Int) : ReaderAction
    data object PreviousChapter : ReaderAction
    data object NextChapter : ReaderAction
    data object OpenChapterList : ReaderAction
    data object DownloadChapter : ReaderAction
    data object Retry : ReaderAction
    data object FlushProgress : ReaderAction
    data class RetryPage(val pageIndex: Int) : ReaderAction
    data class SetNovelFontFamily(val family: String) : ReaderAction
    data class SetNovelFontSize(val size: Int) : ReaderAction
    data class SetNovelLineHeightMultiplier(val multiplier: Float) : ReaderAction
    data class SetNovelParagraphSpacingDp(val dp: Int) : ReaderAction
    data class SetNovelHorizontalPaddingDp(val dp: Int) : ReaderAction
    data object ResetNovelTypographyDefaults : ReaderAction
    data class SetManhwaLayout(val layout: ManhwaLayout) : ReaderAction
    data class SetManhwaZoom(val zoom: ManhwaZoom) : ReaderAction
}

sealed interface ReaderEffect {
    data class NavigateToChapter(val chapter: Chapter) : ReaderEffect
    data object ShowChapterList : ReaderEffect
    data class ShowError(val message: String) : ReaderEffect
    data class ShowSnackbar(val message: String) : ReaderEffect
}
