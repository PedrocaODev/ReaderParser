package com.opus.readerparser.ui.browse

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.opus.readerparser.domain.SeriesRepository
import com.opus.readerparser.domain.SourceRepository
import com.opus.readerparser.domain.model.FilterList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BrowseViewModel @Inject constructor(
    private val sourceRepository: SourceRepository,
    private val seriesRepository: SeriesRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(BrowseUiState())
    val state: StateFlow<BrowseUiState> = _state.asStateFlow()

    private val _effects = Channel<BrowseEffect>(Channel.BUFFERED)
    val effects: Flow<BrowseEffect> = _effects.receiveAsFlow()

    private data class BrowseRequest(
        val sourceId: Long,
        val mode: BrowseMode,
        val query: String,
        val page: Int,
        val reset: Boolean,
    )

    private var requestId = 0L
    private var nextRequestId = 0L
    private var lastRequest: BrowseRequest? = null
    private var requestJob: Job? = null
    private var debounceJob: Job? = null
    private var submittedSourceId: Long? = null
    private var submittedMode: BrowseMode = BrowseMode.POPULAR
    private var submittedQuery: String = ""

    init {
        val sources = sourceRepository.getSources()
        _state.update { it.copy(sources = sources) }
        sources.firstOrNull()?.let { first ->
            _state.update { it.copy(selectedSourceId = first.id) }
            autoFetchCurrentMode(first.id, BrowseMode.POPULAR)
        }
    }

    fun onAction(action: BrowseAction) {
        when (action) {
            is BrowseAction.SelectSource -> {
                cancelDebounce()
                val query = _state.value.searchQuery.trim()
                _state.update {
                    it.copy(
                        selectedSourceId = action.sourceId,
                        error = null,
                        retryAvailable = false,
                        hasNextPage = false,
                        currentPage = 1,
                    )
                }
                invalidateActiveRequests()
                _state.update { it.copy(isLoading = false, error = null) }
                clearSubmittedContext()
                if (query.isNotBlank()) {
                    _state.update { it.copy(series = emptyList(), hasNextPage = false, currentPage = 1) }
                    scheduleDebouncedSearch(action.sourceId, query)
                } else {
                    autoFetchCurrentMode(action.sourceId, _state.value.mode)
                }
            }
            is BrowseAction.SetMode -> {
                cancelDebounce()
                _state.update {
                    it.copy(
                        mode = action.mode,
                        searchQuery = "",
                        error = null,
                        retryAvailable = false,
                        hasNextPage = false,
                        currentPage = 1,
                    )
                }
                invalidateActiveRequests()
                _state.update { it.copy(isLoading = false, error = null) }
                clearSubmittedContext()
                autoFetchSelectedSourceIfNeeded(action.mode)
            }
            is BrowseAction.LoadMore -> {
                val sourceId = submittedSourceId ?: return
                val current = _state.value
                if (!current.hasNextPage || current.isLoading) return
                startRequest(
                    BrowseRequest(
                        sourceId = sourceId,
                        mode = submittedMode,
                        query = submittedQuery,
                        page = current.currentPage + 1,
                        reset = false,
                    ),
                )
            }
            is BrowseAction.SetSearchQuery -> {
                cancelDebounce()
                invalidateActiveRequests()
                val query = action.query.trim()
                if (query.isBlank()) {
                    clearSubmittedContext()
                    _state.update {
                        it.copy(
                            searchQuery = action.query,
                            series = emptyList(),
                            isLoading = false,
                            error = null,
                            retryAvailable = false,
                            hasNextPage = false,
                            currentPage = 1,
                        )
                    }
                    return
                }
                _state.update {
                    it.copy(
                        searchQuery = action.query,
                        isLoading = false,
                        error = null,
                        retryAvailable = false,
                    )
                }
                val sourceId = _state.value.selectedSourceId ?: return
                scheduleDebouncedSearch(sourceId, query)
            }
            is BrowseAction.Search -> {
                cancelDebounce()
                val sourceId = _state.value.selectedSourceId ?: return
                val query = _state.value.searchQuery.trim()
                _state.update {
                    it.copy(
                        mode = BrowseMode.SEARCH,
                        error = null,
                        retryAvailable = false,
                        hasNextPage = false,
                        currentPage = 1,
                    )
                }
                invalidateActiveRequests()
                if (query.isBlank()) {
                    clearSubmittedContext()
                    _state.update {
                        it.copy(
                            series = emptyList(),
                            isLoading = false,
                            error = null,
                            retryAvailable = false,
                            hasNextPage = false,
                            currentPage = 1,
                        )
                    }
                    return
                }
                clearSubmittedContext()
                submitAndStart(
                    BrowseRequest(
                        sourceId = sourceId,
                        mode = BrowseMode.SEARCH,
                        query = query,
                        page = 1,
                        reset = true,
                    ),
                )
            }
            is BrowseAction.Retry -> {
                retryCurrentRequest()
            }
            is BrowseAction.OpenSeries -> viewModelScope.launch {
                _effects.send(BrowseEffect.NavigateToSeries(action.series))
            }
        }
    }

    private fun cancelDebounce() {
        debounceJob?.cancel()
        debounceJob = null
    }

    private fun scheduleDebouncedSearch(sourceId: Long, query: String) {
        debounceJob = viewModelScope.launch {
            delay(300)
            if (_state.value.selectedSourceId == sourceId && _state.value.searchQuery.trim() == query) {
                startSearch(sourceId, query)
            }
        }
    }

    private fun startSearch(sourceId: Long, query: String) {
        _state.update {
            it.copy(
                mode = BrowseMode.SEARCH,
                error = null,
                retryAvailable = false,
                hasNextPage = false,
                currentPage = 1,
            )
        }
        invalidateActiveRequests()
        clearSubmittedContext()
        submitAndStart(
            BrowseRequest(
                sourceId = sourceId,
                mode = BrowseMode.SEARCH,
                query = query,
                page = 1,
                reset = true,
            ),
        )
    }

    private fun retryCurrentRequest() {
        val request = lastRequest ?: return
        submitAndStart(request)
    }

    private fun autoFetchCurrentMode(sourceId: Long, mode: BrowseMode) {
        when (mode) {
            BrowseMode.POPULAR, BrowseMode.LATEST -> submitAndStart(
                BrowseRequest(sourceId, mode, query = "", page = 1, reset = true),
            )
            BrowseMode.SEARCH -> Unit
        }
    }

    private fun autoFetchSelectedSourceIfNeeded(mode: BrowseMode) {
        val sourceId = _state.value.selectedSourceId ?: return
        autoFetchCurrentMode(sourceId, mode)
    }

    private fun submitAndStart(request: BrowseRequest) {
        submittedSourceId = request.sourceId
        submittedMode = request.mode
        submittedQuery = request.query
        startRequest(request)
    }

    private fun invalidateActiveRequests() {
        requestId = 0L
        lastRequest = null
        requestJob?.cancel()
        requestJob = null
    }

    private fun clearSubmittedContext() {
        submittedSourceId = null
        submittedMode = BrowseMode.POPULAR
        submittedQuery = ""
    }

    private fun startRequest(request: BrowseRequest) {
        val id = ++nextRequestId
        requestId = id
        lastRequest = request
        requestJob?.cancel()
        requestJob = viewModelScope.launch {
            _state.update { current ->
                if (request.reset) {
                    current.copy(
                        series = emptyList(),
                        hasNextPage = false,
                        currentPage = 1,
                        isLoading = true,
                        error = null,
                        retryAvailable = false,
                    )
                } else {
                    current.copy(isLoading = true, error = null, retryAvailable = false)
                }
            }
            try {
                val result = when (request.mode) {
                    BrowseMode.POPULAR -> seriesRepository.fetchPopular(request.sourceId, request.page)
                    BrowseMode.LATEST -> seriesRepository.fetchLatest(request.sourceId, request.page)
                    BrowseMode.SEARCH -> seriesRepository.search(request.sourceId, request.query, request.page, FilterList())
                }
                if (id != requestId) return@launch
                _state.update { current ->
                    current.copy(
                        series = if (request.reset) result.series else current.series + result.series,
                        hasNextPage = result.hasNextPage,
                        currentPage = request.page,
                        isLoading = false,
                        error = null,
                        retryAvailable = false,
                    )
                }
            } catch (e: Exception) {
                if (id != requestId) return@launch
                val message = e.message ?: "Failed to load"
                _state.update { it.copy(isLoading = false, error = message, retryAvailable = true) }
                clearSubmittedContext()
                _effects.send(BrowseEffect.ShowError(message))
            }
        }
    }
}
