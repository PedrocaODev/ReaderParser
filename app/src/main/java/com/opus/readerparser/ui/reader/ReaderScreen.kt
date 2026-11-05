package com.opus.readerparser.ui.reader

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.opus.readerparser.domain.model.Chapter
import kotlinx.coroutines.launch

@Composable
fun ReaderScreen(
    onBack: () -> Unit,
    onNavigateToChapter: (Chapter) -> Unit = {},
    viewModel: ReaderViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val isDarkTheme = isSystemInDarkTheme()
    val snackbarHostState = remember { SnackbarHostState() }

    BackHandler {
        viewModel.flushProgress()
        onBack()
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.flushProgress()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is ReaderEffect.NavigateToChapter -> onNavigateToChapter(effect.chapter)
                is ReaderEffect.ShowChapterList -> Unit
                is ReaderEffect.ShowError -> {
                    launch { snackbarHostState.showSnackbar(effect.message) }
                }
                is ReaderEffect.ShowSnackbar -> {
                    launch { snackbarHostState.showSnackbar(effect.message) }
                }
            }
        }
    }

    ReaderContent(
        state = state,
        isDarkTheme = isDarkTheme,
        onAction = viewModel::onAction,
        onBack = {
            viewModel.flushProgress()
            onBack()
        },
        snackbarHostState = snackbarHostState,
    )
}
