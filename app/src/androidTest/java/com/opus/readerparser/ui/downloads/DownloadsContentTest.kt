package com.opus.readerparser.ui.downloads

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.opus.readerparser.domain.model.ContentType
import com.opus.readerparser.domain.model.DownloadItem
import com.opus.readerparser.domain.model.DownloadState
import com.opus.readerparser.domain.model.DownloadedSeriesItem
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DownloadsContentTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun loadingState_showsProgressIndicator() {
        composeRule.setContent {
            DownloadsContent(state = DownloadsUiState(isLoading = true), onAction = {})
        }
        composeRule.onNodeWithTag("loading").assertIsDisplayed()
    }

    @Test
    fun errorState_showsErrorMessage() {
        composeRule.setContent {
            DownloadsContent(state = DownloadsUiState(error = "Load error"), onAction = {})
        }
        composeRule.onNodeWithTag("error_message").assertIsDisplayed()
        composeRule.onNodeWithText("Load error").assertIsDisplayed()
    }

    @Test
    fun emptyState_showsEmptyMessage() {
        composeRule.setContent {
            DownloadsContent(
                state = DownloadsUiState(
                    isLoading = false,
                    error = null,
                    downloads = emptyList(),
                    downloadedSeries = emptyList(),
                ),
                onAction = {},
            )
        }
        composeRule.onNodeWithText("No downloads yet.").assertIsDisplayed()
        composeRule.onNodeWithText("Downloaded chapters and active queue items will appear here.").assertIsDisplayed()
    }

    @Test
    fun populatedState_showsDownloadsListAndStorageBreakdown() {
        val items = listOf(
            DownloadItem(
                sourceId = 1L,
                chapterUrl = "c1",
                chapterName = "Chapter 1",
                seriesTitle = "Solo Leveling",
                state = DownloadState.COMPLETED,
                progress = 1f,
            ),
        )
        val series = listOf(
            DownloadedSeriesItem(
                seriesId = "series-1",
                seriesTitle = "Solo Leveling",
                contentType = ContentType.MANHWA,
                downloadedChapterCount = 42,
                chapterRangeSummary = "42 chapters · Ch. 1–42",
                totalBytes = 250_000_000L,
            ),
        )

        composeRule.setContent {
            DownloadsContent(
                state = DownloadsUiState(
                    downloads = items,
                    downloadedSeries = series,
                    novelTextBytes = 10_000_000L,
                    manhwaImageBytes = 250_000_000L,
                    availableBytes = 4_200_000_000L,
                ),
                onAction = {},
            )
        }

        composeRule.onNodeWithTag("downloads_list").assertIsDisplayed()
        composeRule.onNodeWithTag("storage_breakdown_card").assertIsDisplayed()
        composeRule.onNodeWithText("LOCAL STORAGE").assertIsDisplayed()
        composeRule.onNodeWithText("Novels (10 MB)").assertIsDisplayed()
        composeRule.onNodeWithText("Manhwa (250 MB)").assertIsDisplayed()

        composeRule.onNodeWithText("ACTIVE QUEUE (1)").assertIsDisplayed()
        composeRule.onNodeWithText("Chapter 1").assertIsDisplayed()

        composeRule.onNodeWithText("SAVED OFFLINE (BY SERIES)").assertIsDisplayed()
        composeRule.onNodeWithText("42 chapters · Ch. 1–42").assertIsDisplayed()
    }

    @Test
    fun retryButton_dispatchesRetryAction() {
        val item = DownloadItem(
            sourceId = 1L,
            chapterUrl = "c1",
            chapterName = "Chapter 1",
            seriesTitle = "Solo Leveling",
            state = DownloadState.FAILED,
            progress = 0f,
        )
        var retried = false
        composeRule.setContent {
            DownloadsContent(
                state = DownloadsUiState(downloads = listOf(item)),
                onAction = { action ->
                    if (action is DownloadsAction.Retry) retried = true
                },
            )
        }
        composeRule.onNodeWithText("Retry").performClick()
        assert(retried)
    }

    @Test
    fun cancelButton_dispatchesCancelAction() {
        val item = DownloadItem(
            sourceId = 1L,
            chapterUrl = "c1",
            chapterName = "Chapter 1",
            seriesTitle = "Solo Leveling",
            state = DownloadState.RUNNING,
            progress = 0.5f,
        )
        var cancelled = false
        composeRule.setContent {
            DownloadsContent(
                state = DownloadsUiState(downloads = listOf(item)),
                onAction = { action ->
                    if (action is DownloadsAction.Cancel) cancelled = true
                },
            )
        }
        composeRule.onNodeWithContentDescription("Cancel download").performClick()
        assert(cancelled)
    }

    @Test
    fun deleteButton_dispatchesDeleteAction() {
        val item = DownloadItem(
            sourceId = 1L,
            chapterUrl = "c1",
            chapterName = "Chapter 1",
            seriesTitle = "Solo Leveling",
            state = DownloadState.COMPLETED,
            progress = 1f,
        )
        var deleted = false
        composeRule.setContent {
            DownloadsContent(
                state = DownloadsUiState(downloads = listOf(item)),
                onAction = { action ->
                    if (action is DownloadsAction.Delete) deleted = true
                },
            )
        }
        composeRule.onNodeWithContentDescription("Delete download").performClick()
        assert(deleted)
    }

    @Test
    fun clearSeriesDownloadsButton_dispatchesClearAction() {
        val series = DownloadedSeriesItem(
            seriesId = "series-123",
            seriesTitle = "Omniscient Reader",
            contentType = ContentType.NOVEL,
            downloadedChapterCount = 10,
            chapterRangeSummary = "10 chapters · Ch. 1–10",
            totalBytes = 5_000_000L,
        )
        var clearedSeriesId: String? = null
        composeRule.setContent {
            DownloadsContent(
                state = DownloadsUiState(downloadedSeries = listOf(series)),
                onAction = { action ->
                    if (action is DownloadsAction.ClearSeriesDownloads) {
                        clearedSeriesId = action.seriesId
                    }
                },
            )
        }
        composeRule.onNodeWithText("Clear").performClick()
        assert(clearedSeriesId == "series-123")
    }

    @Test
    fun runningItemWithProgress_showsPercentageBadge() {
        val item = DownloadItem(
            sourceId = 1L,
            chapterUrl = "c1",
            chapterName = "Chapter 1",
            seriesTitle = "Solo Leveling",
            state = DownloadState.RUNNING,
            progress = 0.45f,
        )
        composeRule.setContent {
            DownloadsContent(state = DownloadsUiState(downloads = listOf(item)), onAction = {})
        }
        composeRule.onNodeWithText("45%").assertIsDisplayed()
    }

    @Test
    fun runningItemWithZeroProgress_showsDownloadingBadge() {
        val item = DownloadItem(
            sourceId = 1L,
            chapterUrl = "c1",
            chapterName = "Chapter 1",
            seriesTitle = "Solo Leveling",
            state = DownloadState.RUNNING,
            progress = 0f,
        )
        composeRule.setContent {
            DownloadsContent(state = DownloadsUiState(downloads = listOf(item)), onAction = {})
        }
        composeRule.onNodeWithText("Downloading").assertIsDisplayed()
    }
}
