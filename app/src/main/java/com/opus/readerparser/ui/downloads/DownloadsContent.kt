package com.opus.readerparser.ui.downloads

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.opus.readerparser.domain.model.ContentType
import com.opus.readerparser.domain.model.DownloadItem
import com.opus.readerparser.domain.model.DownloadState
import com.opus.readerparser.domain.model.DownloadedSeriesItem
import com.opus.readerparser.ui.components.AppTopBar
import com.opus.readerparser.ui.components.EmptyStateView
import com.opus.readerparser.ui.components.ErrorRetryStateView
import com.opus.readerparser.ui.components.LoadingStateView
import com.opus.readerparser.ui.components.SectionHeader
import com.opus.readerparser.ui.theme.ReaderParserTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadsContent(
    state: DownloadsUiState,
    onAction: (DownloadsAction) -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    onNavigateToSettings: () -> Unit = {},
    onOpenSettings: () -> Unit = onNavigateToSettings,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            AppTopBar(
                title = "Downloads",
                actions = {
                    IconButton(onClick = onOpenSettings) {
                        Icon(Icons.Filled.Settings, contentDescription = "Settings")
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
                state.isLoading -> {
                    LoadingStateView()
                }
                state.error != null -> {
                    ErrorRetryStateView(
                        message = state.error,
                        onRetry = { onAction(DownloadsAction.RetryLoad) },
                    )
                }
                state.downloads.isEmpty() && state.downloadedSeries.isEmpty() -> {
                    EmptyStateView(
                        title = "No downloads yet.",
                        subtitle = "Downloaded chapters and active queue items will appear here.",
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("downloads_list"),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        item(key = "storage_breakdown") {
                            StorageBreakdownCard(
                                novelTextBytes = state.novelTextBytes,
                                manhwaImageBytes = state.manhwaImageBytes,
                                availableBytes = state.availableBytes,
                            )
                        }

                        item(key = "active_queue_header") {
                            SectionHeader(title = "ACTIVE QUEUE (${state.downloads.size})")
                        }

                        items(
                            state.downloads,
                            key = { "queue|${it.sourceId}|${it.chapterUrl}" },
                        ) { item ->
                            DownloadQueueItemCard(
                                item = item,
                                onCancel = { onAction(DownloadsAction.Cancel(item.sourceId, item.chapterUrl)) },
                                onRetry = { onAction(DownloadsAction.Retry(item.sourceId, item.chapterUrl)) },
                                onDelete = { onAction(DownloadsAction.Delete(item.sourceId, item.chapterUrl)) },
                            )
                        }

                        item(key = "saved_offline_header") {
                            SectionHeader(title = "SAVED OFFLINE (BY SERIES)")
                        }

                        items(
                            state.downloadedSeries,
                            key = { "series|${it.sourceId}|${it.seriesUrl}" },
                        ) { seriesItem ->
                            DownloadedSeriesCard(
                                item = seriesItem,
                                onClearSeriesDownloads = { sourceId, seriesUrl ->
                                    onAction(DownloadsAction.ClearSeriesDownloads(sourceId, seriesUrl))
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DownloadsContentLoadingPreview() {
    ReaderParserTheme {
        DownloadsContent(
            state = DownloadsUiState(isLoading = true),
            onAction = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DownloadsContentErrorPreview() {
    ReaderParserTheme {
        DownloadsContent(
            state = DownloadsUiState(error = "Failed to load downloads"),
            onAction = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DownloadsContentPopulatedPreview() {
    val items = listOf(
        DownloadItem(
            sourceId = 1L,
            chapterUrl = "c1",
            chapterName = "Chapter 1",
            seriesTitle = "Solo Leveling",
            state = DownloadState.COMPLETED,
            progress = 1f,
        ),
        DownloadItem(
            sourceId = 1L,
            chapterUrl = "c2",
            chapterName = "Chapter 2",
            seriesTitle = "Solo Leveling",
            state = DownloadState.RUNNING,
            progress = 0.45f,
        ),
        DownloadItem(
            sourceId = 1L,
            chapterUrl = "c3",
            chapterName = "Chapter 3",
            seriesTitle = "Solo Leveling",
            state = DownloadState.FAILED,
            progress = 0f,
        ),
    )
    val series = listOf(
        DownloadedSeriesItem(
            sourceId = 1L,
            seriesUrl = "s1",
            seriesTitle = "Shadow Slave",
            contentType = ContentType.NOVEL,
            downloadedChapterCount = 4,
            chapterRangeSummary = "4 chapters · Ch. 40–43",
            totalBytes = 12_800_000L,
        ),
    )
    ReaderParserTheme {
        DownloadsContent(
            state = DownloadsUiState(
                downloads = items,
                downloadedSeries = series,
                novelTextBytes = 12_800_000L,
                manhwaImageBytes = 30_000_000L,
            ),
            onAction = {},
        )
    }
}
