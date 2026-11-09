package com.opus.readerparser.ui.series

import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch

@Composable
fun SeriesScreen(
    onNavigateToReader: (sourceId: Long, seriesUrl: String, chapterUrl: String, contentType: String) -> Unit,
    onBack: () -> Unit,
    viewModel: SeriesViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is SeriesEffect.NavigateToReader -> {
                    val chapter = effect.chapter
                    onNavigateToReader(
                        chapter.sourceId,
                        chapter.seriesUrl,
                        chapter.url,
                        effect.type.name,
                    )
                }
                is SeriesEffect.ShowError -> {
                    launch {
                        val result = snackbarHostState.showSnackbar(
                            message = effect.message,
                            actionLabel = "Retry",
                        )
                        if (result == SnackbarResult.ActionPerformed) {
                            viewModel.onAction(SeriesAction.Refresh)
                        }
                    }
                }
                is SeriesEffect.ShowSnackbar -> {
                    launch { snackbarHostState.showSnackbar(effect.message) }
                }
            }
        }
    }

    SeriesContent(
        state = state,
        onAction = viewModel::onAction,
        onBack = onBack,
        snackbarHostState = snackbarHostState,
    )
}
