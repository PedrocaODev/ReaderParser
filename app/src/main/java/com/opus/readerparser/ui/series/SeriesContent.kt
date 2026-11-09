package com.opus.readerparser.ui.series

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.opus.readerparser.domain.model.Chapter
import com.opus.readerparser.domain.model.ChapterWithState
import com.opus.readerparser.domain.model.ContentType
import com.opus.readerparser.domain.model.Series
import com.opus.readerparser.ui.components.AppTopBar
import com.opus.readerparser.ui.components.ErrorRetryStateView
import com.opus.readerparser.ui.components.LoadingStateView
import com.opus.readerparser.ui.theme.AppSpacing
import com.opus.readerparser.ui.theme.ReaderParserTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeriesContent(
    state: SeriesUiState,
    onAction: (SeriesAction) -> Unit,
    onBack: (() -> Unit)? = null,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    var menuExpanded by remember { mutableStateOf(false) }
    var selectedChapterForSheet by remember { mutableStateOf<ChapterWithState?>(null) }

    Scaffold(
        topBar = {
            AppTopBar(
                title = state.series?.title ?: "",
                onBackClick = onBack,
                actions = {
                    IconButton(onClick = { onAction(SeriesAction.Refresh) }) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Refresh")
                    }
                    Box {
                        IconButton(onClick = { menuExpanded = true }) {
                            Icon(Icons.Filled.MoreVert, contentDescription = "More actions")
                        }
                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false },
                        ) {
                            DropdownMenuItem(
                                text = { Text("Download Unread Chapters") },
                                onClick = {
                                    menuExpanded = false
                                    onAction(SeriesAction.DownloadUnread)
                                },
                            )
                            DropdownMenuItem(
                                text = { Text("Download Chapter Range") },
                                onClick = {
                                    menuExpanded = false
                                    onAction(SeriesAction.ShowRangePicker)
                                },
                            )
                            DropdownMenuItem(
                                text = { Text("Mark All as Read") },
                                onClick = {
                                    menuExpanded = false
                                    onAction(SeriesAction.MarkAllRead)
                                },
                            )
                            DropdownMenuItem(
                                text = { Text("Mark All as Unread") },
                                onClick = {
                                    menuExpanded = false
                                    onAction(SeriesAction.MarkAllUnread)
                                },
                            )
                        }
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            when {
                state.isLoading && state.series == null -> {
                    LoadingStateView()
                }
                state.error != null && state.series == null -> {
                    ErrorRetryStateView(
                        message = state.error,
                        onRetry = { onAction(SeriesAction.Refresh) },
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("chapter_list"),
                    ) {
                        if (state.error != null && state.series != null) {
                            item(key = "series_error_banner") {
                                SeriesErrorBanner(
                                    message = state.error,
                                    onRetry = { onAction(SeriesAction.Refresh) },
                                    onDismiss = { onAction(SeriesAction.DismissError) },
                                    modifier = Modifier.padding(
                                        horizontal = AppSpacing.lg,
                                        vertical = AppSpacing.sm,
                                    ),
                                )
                            }
                        }

                        state.series?.let { series ->
                            item(key = "series_hero") {
                                SeriesHeroCard(
                                    series = series,
                                    inLibrary = state.inLibrary,
                                    onToggleLibrary = {
                                        onAction(SeriesAction.ToggleLibrary(!state.inLibrary))
                                    },
                                    modifier = Modifier.padding(
                                        horizontal = AppSpacing.lg,
                                        vertical = AppSpacing.sm,
                                    ),
                                )
                            }
                        }

                        if (state.resumeState !is ReadingResumeState.None) {
                            item(key = "reading_progress") {
                                ReadingProgressCard(
                                    resumeState = state.resumeState,
                                    readCount = state.readChapterCount,
                                    totalCount = state.totalChapterCount,
                                    onResumeClick = { onAction(SeriesAction.ResumeReading) },
                                    modifier = Modifier.padding(
                                        horizontal = AppSpacing.lg,
                                        vertical = AppSpacing.sm,
                                    ),
                                )
                            }
                        }

                        item(key = "chapter_section_header") {
                            ChapterSectionHeader(
                                chapterCount = state.totalChapterCount,
                                sortAscending = state.sortAscending,
                                unreadOnly = state.unreadOnly,
                                onToggleSort = { onAction(SeriesAction.ToggleSortOrder) },
                                onToggleUnread = { onAction(SeriesAction.ToggleUnreadFilter) },
                                onDownloadUnread = { onAction(SeriesAction.DownloadUnread) },
                            )
                        }

                        items(
                            items = state.displayedChapters,
                            key = { "${it.chapter.sourceId}|${it.chapter.url}" },
                        ) { cws ->
                            ChapterRow(
                                chapterWithState = cws,
                                onClick = { onAction(SeriesAction.OpenChapter(cws.chapter)) },
                                onMoreClick = { selectedChapterForSheet = cws },
                            )
                        }
                    }
                }
            }
        }
    }

    val currentSelectedChapter = selectedChapterForSheet?.let { cur ->
        state.chapters.find { it.chapter.url == cur.chapter.url } ?: cur
    }

    if (currentSelectedChapter != null) {
        ChapterActionSheet(
            chapterWithState = currentSelectedChapter,
            onToggleRead = {
                onAction(SeriesAction.ToggleChapterRead(currentSelectedChapter.chapter))
            },
            onMarkPreviousRead = {
                onAction(SeriesAction.MarkPreviousRead(currentSelectedChapter.chapter))
            },
            onDownloadChapter = {
                onAction(SeriesAction.DownloadChapter(currentSelectedChapter.chapter))
            },
            onDismissRequest = { selectedChapterForSheet = null },
        )
    }

    if (state.showRangePicker) {
        RangePickerDialog(
            chapterCount = state.chapters.size,
            onConfirm = { start, end ->
                onAction(SeriesAction.DownloadRange(start, end))
            },
            onDismiss = { onAction(SeriesAction.DismissRangePicker) },
        )
    }
}

@Composable
private fun RangePickerDialog(
    chapterCount: Int,
    onConfirm: (startIndex: Int, endIndex: Int) -> Unit,
    onDismiss: () -> Unit,
) {
    var startIndex by remember { mutableIntStateOf(0) }
    var endIndex by remember { mutableIntStateOf((chapterCount - 1).coerceAtLeast(0)) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Download range") },
        text = {
            Column {
                Text(
                    text = "Select chapter range (1–$chapterCount)",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Spacer(Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("From", style = MaterialTheme.typography.labelMedium)
                        OutlinedTextField(
                            value = (startIndex + 1).toString(),
                            onValueChange = { value ->
                                value.toIntOrNull()?.let { idx ->
                                    startIndex = (idx - 1).coerceIn(0, chapterCount - 1)
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("To", style = MaterialTheme.typography.labelMedium)
                        OutlinedTextField(
                            value = (endIndex + 1).toString(),
                            onValueChange = { value ->
                                value.toIntOrNull()?.let { idx ->
                                    endIndex = (idx - 1).coerceIn(0, chapterCount - 1)
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(startIndex, endIndex) }) {
                Text("Download")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    )
}

@Preview(showBackground = true)
@Composable
private fun SeriesContentLoadingPreview() {
    ReaderParserTheme {
        SeriesContent(
            state = SeriesUiState(isLoading = true),
            onAction = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SeriesContentErrorPreview() {
    ReaderParserTheme {
        SeriesContent(
            state = SeriesUiState(error = "Failed to load series"),
            onAction = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SeriesContentPopulatedPreview() {
    val series = Series(
        sourceId = 1L,
        url = "https://example.com/series",
        title = "Solo Leveling",
        author = "Chu-Gong",
        description = "In this world where Hunters with various special powers battle monsters from portals...",
        type = ContentType.MANHWA,
    )
    val chapters = listOf(
        ChapterWithState(
            chapter = Chapter(seriesUrl = series.url, sourceId = 1L, url = "c1", name = "Chapter 1", number = 1f),
            read = true,
            downloaded = false,
            progress = 1f,
        ),
        ChapterWithState(
            chapter = Chapter(seriesUrl = series.url, sourceId = 1L, url = "c2", name = "Chapter 2", number = 2f),
            read = false,
            downloaded = true,
            progress = 0f,
        ),
    )
    ReaderParserTheme {
        SeriesContent(
            state = SeriesUiState(series = series, chapters = chapters, inLibrary = true),
            onAction = {},
        )
    }
}
