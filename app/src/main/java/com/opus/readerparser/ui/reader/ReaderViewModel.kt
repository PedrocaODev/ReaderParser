package com.opus.readerparser.ui.reader

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.opus.readerparser.domain.ChapterRepository
import com.opus.readerparser.domain.DownloadEnqueuer
import com.opus.readerparser.domain.SettingsRepository
import com.opus.readerparser.domain.model.Chapter
import com.opus.readerparser.domain.model.ChapterContent
import com.opus.readerparser.domain.model.ChapterWithState
import com.opus.readerparser.domain.model.ContentType
import com.opus.readerparser.domain.model.Series
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReaderViewModel @Inject constructor(
    savedState: SavedStateHandle,
    private val chapterRepository: ChapterRepository,
    private val downloadEnqueuer: DownloadEnqueuer,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    private val sourceId: Long = checkNotNull(savedState["sourceId"])
    private val seriesUrl: String = checkNotNull(savedState["seriesUrl"])
    private val chapterUrl: String = checkNotNull(savedState["chapterUrl"])
    private val routeContentType: ContentType = ContentType.valueOf(
        checkNotNull(savedState["contentType"])
    )

    private val _state = MutableStateFlow(ReaderUiState(contentType = routeContentType))
    val state: StateFlow<ReaderUiState> = _state.asStateFlow()

    private val _effects = Channel<ReaderEffect>(Channel.BUFFERED)
    val effects: Flow<ReaderEffect> = _effects.receiveAsFlow()

    private var pendingProgress: Float = -1f
    private var progressJob: Job? = null
    private var lastPersistedProgress: Float = -1f

    private val seriesStub: Series
        get() = Series(sourceId = sourceId, url = seriesUrl, title = "", type = routeContentType)

    init {
        viewModelScope.launch {
            chapterRepository.observeChapters(seriesStub)
                .catch { /* Chapter loading errors handled in loadCurrentChapter */ }
                .collect { chapters ->
                    _state.update { it.copy(seriesChapters = chapters) }
                }
        }
        viewModelScope.launch {
            settingsRepository.observeSettings()
                .catch { /* Fallback to default settings if observation fails */ }
                .collect { settings ->
                    _state.update { it.copy(settings = settings) }
                }
        }
        loadCurrentChapter()
    }

    fun onAction(action: ReaderAction) {
        when (action) {
            is ReaderAction.Load -> loadChapter(action.chapter)
            is ReaderAction.SelectChapter -> navigateToSelectedChapter(action.chapter)
            is ReaderAction.SetProgress -> {
                val chapter = _state.value.chapter ?: return
                val clamped = action.progress.coerceIn(0f, 1f)
                if (clamped == lastPersistedProgress) return
                _state.update { it.copy(progress = clamped) }
                pendingProgress = clamped
                progressJob?.cancel()
                progressJob = viewModelScope.launch {
                    delay(300)
                    saveProgress(chapter, clamped)
                }
            }
            is ReaderAction.FlushProgress -> flushProgress()
            is ReaderAction.SetPage -> {
                val pages = _state.value.pages
                _state.update { it.copy(currentPage = action.page) }
                if (action.page == pages.lastIndex) {
                    markReadIfLoaded()
                }
            }
            is ReaderAction.PreviousChapter -> navigateChapter(forward = false)
            is ReaderAction.NextChapter -> navigateChapter(forward = true)
            is ReaderAction.OpenChapterList -> viewModelScope.launch {
                _effects.send(ReaderEffect.ShowChapterList)
            }
            is ReaderAction.DownloadChapter -> viewModelScope.launch {
                val chapter = _state.value.chapter ?: return@launch
                try {
                    downloadEnqueuer.enqueueChapter(chapter.sourceId, chapter.url)
                    _effects.send(ReaderEffect.ShowSnackbar("Queued \"${chapter.name}\" for download"))
                } catch (e: Exception) {
                    _effects.send(ReaderEffect.ShowError(e.message ?: "Failed to queue download"))
                }
            }
            is ReaderAction.Retry -> {
                val chapter = _state.value.chapter
                if (chapter != null) {
                    loadChapter(chapter)
                } else {
                    val url = _state.value.chapterUrl ?: return
                    _state.update { it.copy(isLoading = true, error = null) }
                    viewModelScope.launch {
                        val resolved = chapterRepository.findByUrl(sourceId, url)
                        if (resolved != null) {
                            loadChapter(resolved)
                        } else {
                            _state.update { it.copy(isLoading = false, error = "Chapter not available") }
                        }
                    }
                }
            }
        }
    }

    fun flushProgress() {
        val chapter = _state.value.chapter ?: return
        val progress = pendingProgress
        if (progress >= 0f && progress != lastPersistedProgress) {
            progressJob?.cancel()
            progressJob = null
            saveProgressNonCancellable(chapter, progress)
        }
    }

    override fun onCleared() {
        flushProgress()
        super.onCleared()
    }

    private suspend fun saveProgress(chapter: Chapter, progress: Float) {
        try {
            chapterRepository.setProgress(chapter, progress)
            lastPersistedProgress = progress
            if (progress > 0.98f) {
                chapterRepository.markRead(chapter, true)
            }
        } catch (e: Exception) {
            if (e is kotlinx.coroutines.CancellationException) throw e
            // Progress stays in state for retry, but don't mark as persisted.
        }
    }

    private fun saveProgressNonCancellable(chapter: Chapter, progress: Float) {
        val previous = lastPersistedProgress
        lastPersistedProgress = progress
        viewModelScope.launch(NonCancellable) {
            try {
                chapterRepository.setProgress(chapter, progress)
                if (progress > 0.98f) {
                    chapterRepository.markRead(chapter, true)
                }
            } catch (e: Exception) {
                lastPersistedProgress = previous
                if (e is kotlinx.coroutines.CancellationException) throw e
            }
        }
    }

    private fun loadCurrentChapter() {
        viewModelScope.launch {
            try {
                val chapters = loadSeriesChapters()
                val chapterInList = chapters.find { it.chapter.url == chapterUrl }
                if (chapterInList == null) {
                    _state.update { it.copy(isLoading = false, error = "Chapter not available", chapterUrl = chapterUrl) }
                    return@launch
                }
                val chapter = chapterInList.chapter
                loadChapter(chapter)
            } catch (e: Exception) {
                if (e is kotlinx.coroutines.CancellationException) throw e
                _state.update { it.copy(isLoading = false, error = e.message ?: "Failed to load chapter") }
                _effects.send(ReaderEffect.ShowError(e.message ?: "Failed to load chapter"))
            }
        }
    }

    private fun loadChapter(chapter: Chapter) {
        viewModelScope.launch {
            progressJob?.cancel()
            progressJob = null
            pendingProgress = -1f
            lastPersistedProgress = 0f
            _state.update {
                it.copy(
                    isLoading = true,
                    error = null,
                    chapter = chapter,
                    chapterUrl = chapter.url,
                    html = "",
                    pages = emptyList(),
                    progress = 0f,
                    currentPage = 0,
                )
            }
            try {
                val content = chapterRepository.getContent(chapter)
                val mismatch = when {
                    routeContentType == ContentType.NOVEL && content !is ChapterContent.Text -> true
                    routeContentType == ContentType.MANHWA && content !is ChapterContent.Pages -> true
                    else -> false
                }
                if (mismatch) {
                    val forcedContent = chapterRepository.getContent(chapter, forceNetwork = true)
                    processLoadedContent(forcedContent, chapter)
                } else {
                    processLoadedContent(content, chapter)
                }
            } catch (e: Exception) {
                if (e is kotlinx.coroutines.CancellationException) throw e
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load chapter",
                    )
                }
                _effects.send(ReaderEffect.ShowError(e.message ?: "Failed to load chapter"))
            }
        }
    }

    private suspend fun processLoadedContent(content: ChapterContent, chapter: Chapter) {
        when {
            routeContentType == ContentType.NOVEL && content is ChapterContent.Text -> {
                chapterRepository.markRead(chapter, true)
                val chapters = loadSeriesChapters()
                val idx = chapters.indexOfFirst { it.chapter.url == chapter.url }
                val progress = chapters.getOrNull(idx)?.progress ?: 0f
                lastPersistedProgress = progress
                _state.update {
                    it.copy(
                        html = content.html,
                        isLoading = false,
                        progress = progress,
                        hasPreviousChapter = idx > 0,
                        hasNextChapter = idx in 0 until chapters.lastIndex,
                    )
                }
            }
            routeContentType == ContentType.MANHWA && content is ChapterContent.Pages -> {
                val chapters = loadSeriesChapters()
                val idx = chapters.indexOfFirst { it.chapter.url == chapter.url }
                _state.update {
                    it.copy(
                        pages = content.imageUrls,
                        isLoading = false,
                        hasPreviousChapter = idx > 0,
                        hasNextChapter = idx in 0 until chapters.lastIndex,
                    )
                }
            }
            else -> {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Unexpected content type",
                    )
                }
            }
        }
    }

    private fun markReadIfLoaded() {
        viewModelScope.launch {
            val chapter = _state.value.chapter ?: return@launch
            chapterRepository.markRead(chapter, true)
        }
    }

    private fun navigateChapter(forward: Boolean) {
        flushProgress()
        viewModelScope.launch {
            val current = _state.value.chapter ?: return@launch
            val chapters = loadSeriesChapters()
            val idx = chapters.indexOfFirst { it.chapter.url == current.url }
            val target = if (forward) chapters.getOrNull(idx + 1) else chapters.getOrNull(idx - 1)
            if (target != null) {
                _effects.send(ReaderEffect.NavigateToChapter(target.chapter))
            }
        }
    }

    private fun navigateToSelectedChapter(chapter: Chapter) {
        if (chapter.url == _state.value.chapter?.url) return
        flushProgress()
        viewModelScope.launch {
            _effects.send(ReaderEffect.NavigateToChapter(chapter))
        }
    }

    private suspend fun loadSeriesChapters(): List<ChapterWithState> {
        val current = _state.value.seriesChapters
        if (current.isNotEmpty()) return current
        return chapterRepository.observeChapters(seriesStub).first().also { chapters ->
            _state.update { it.copy(seriesChapters = chapters) }
        }
    }
}
