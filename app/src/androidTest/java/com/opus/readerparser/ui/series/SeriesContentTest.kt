package com.opus.readerparser.ui.series

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.opus.readerparser.domain.model.Chapter
import com.opus.readerparser.domain.model.ChapterWithState
import com.opus.readerparser.domain.model.ContentType
import com.opus.readerparser.domain.model.Series
import com.opus.readerparser.domain.model.SeriesStatus
import com.opus.readerparser.ui.components.ChapterActionSheet
import com.opus.readerparser.ui.components.ChapterRow
import com.opus.readerparser.ui.components.ChapterSectionHeader
import com.opus.readerparser.ui.components.ExpandableSynopsis
import com.opus.readerparser.ui.components.ReadingProgressCard
import com.opus.readerparser.ui.components.SeriesHeroCard
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SeriesContentTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun loadingState_showsProgressIndicator() {
        composeRule.setContent {
            SeriesContent(state = SeriesUiState(isLoading = true), onAction = {})
        }
        composeRule.onNodeWithTag("loading").assertIsDisplayed()
    }

    @Test
    fun errorState_showsErrorMessageAndRetry() {
        composeRule.setContent {
            SeriesContent(state = SeriesUiState(error = "Load failed"), onAction = {})
        }
        composeRule.onNodeWithTag("error_message").assertIsDisplayed()
        composeRule.onNodeWithText("Load failed").assertIsDisplayed()
        composeRule.onNodeWithText("Retry").assertIsDisplayed()
    }

    @Test
    fun populatedState_showsChapterList() {
        val series = Series(
            sourceId = 1L,
            url = "https://example.com/s",
            title = "Test Series",
            type = ContentType.MANHWA,
        )
        val chapters = listOf(
            ChapterWithState(
                chapter = Chapter(seriesUrl = series.url, sourceId = 1L, url = "c1", name = "Chapter 1", number = 1f),
                read = false,
                downloaded = false,
                progress = 0f,
            ),
        )
        composeRule.setContent {
            SeriesContent(state = SeriesUiState(series = series, chapters = chapters), onAction = {})
        }
        composeRule.onNodeWithTag("chapter_list").assertIsDisplayed()
        composeRule.onNodeWithText("Chapter 1").assertIsDisplayed()
    }

    @Test
    fun clickChapter_dispatchesOpenChapterAction() {
        val series = Series(
            sourceId = 1L,
            url = "https://example.com/s",
            title = "Test Series",
            type = ContentType.MANHWA,
        )
        val chapter = Chapter(seriesUrl = series.url, sourceId = 1L, url = "c1", name = "Chapter 42", number = 42f)
        var openedChapter: Chapter? = null
        composeRule.setContent {
            SeriesContent(
                state = SeriesUiState(
                    series = series,
                    chapters = listOf(
                        ChapterWithState(chapter = chapter, read = false, downloaded = false, progress = 0f),
                    ),
                ),
                onAction = { action ->
                    if (action is SeriesAction.OpenChapter) openedChapter = action.chapter
                },
            )
        }
        composeRule.onNodeWithText("Chapter 42").performClick()
        assert(openedChapter == chapter)
    }

    @Test
    fun seriesHeroCard_rendersMetadataAndTogglesLibrary() {
        val testSeries = Series(
            sourceId = 1L,
            url = "https://example.com/s",
            title = "Solo Leveling",
            author = "Chugong",
            genres = listOf("Action", "Fantasy"),
            status = SeriesStatus.COMPLETED,
            type = ContentType.MANHWA,
        )
        var toggled = false
        composeRule.setContent {
            SeriesHeroCard(
                series = testSeries,
                inLibrary = false,
                onToggleLibrary = { toggled = true },
                sourceName = "Asura",
            )
        }
        composeRule.onNodeWithTag("series_hero_card").assertIsDisplayed()
        composeRule.onNodeWithText("Solo Leveling").assertIsDisplayed()
        composeRule.onNodeWithText("Chugong · Asura").assertIsDisplayed()
        composeRule.onNodeWithText("Completed").assertIsDisplayed()
        composeRule.onNodeWithText("Action").assertIsDisplayed()
        composeRule.onNodeWithText("Fantasy").assertIsDisplayed()

        val button = composeRule.onNodeWithTag("library_toggle_button")
        button.assertIsDisplayed()
        composeRule.onNodeWithText("+ Add to Library").assertIsDisplayed()
        button.performClick()
        assert(toggled)
    }

    @Test
    fun seriesHeroCard_showsInLibraryStateWhenTrue() {
        val testSeries = Series(
            sourceId = 1L,
            url = "https://example.com/s",
            title = "Solo Leveling",
            type = ContentType.MANHWA,
        )
        composeRule.setContent {
            SeriesHeroCard(
                series = testSeries,
                inLibrary = true,
                onToggleLibrary = {},
            )
        }
        composeRule.onNodeWithText("✓ In Library").assertIsDisplayed()
    }

    @Test
    fun expandableSynopsis_togglesBetweenShowMoreAndShowLess() {
        composeRule.setContent {
            ExpandableSynopsis(
                description = "A long synopsis detailing the epic adventure of hunters fighting monsters.",
            )
        }
        composeRule.onNodeWithTag("synopsis_text").assertIsDisplayed()
        val toggle = composeRule.onNodeWithTag("synopsis_toggle_button")
        toggle.assertIsDisplayed()
        composeRule.onNodeWithText("Show more").assertIsDisplayed()

        toggle.performClick()
        composeRule.onNodeWithText("Show less").assertIsDisplayed()

        toggle.performClick()
        composeRule.onNodeWithText("Show more").assertIsDisplayed()
    }

    @Test
    fun topBarOverflowMenu_opensAndDispatchesActions() {
        val dispatchedActions = mutableListOf<SeriesAction>()
        val series = Series(
            sourceId = 1L,
            url = "https://example.com/s",
            title = "Test Series",
            type = ContentType.MANHWA,
        )
        composeRule.setContent {
            SeriesContent(
                state = SeriesUiState(series = series),
                onAction = { dispatchedActions.add(it) },
            )
        }

        // Open overflow menu
        composeRule.onNodeWithContentDescription("More actions").performClick()
        composeRule.onNodeWithText("Download Unread Chapters").assertIsDisplayed()
        composeRule.onNodeWithText("Download Chapter Range").assertIsDisplayed()
        composeRule.onNodeWithText("Mark All as Read").assertIsDisplayed()
        composeRule.onNodeWithText("Mark All as Unread").assertIsDisplayed()

        // Click Download Unread
        composeRule.onNodeWithText("Download Unread Chapters").performClick()
        assert(dispatchedActions.lastOrNull() == SeriesAction.DownloadUnread)

        // Click Download Chapter Range
        composeRule.onNodeWithContentDescription("More actions").performClick()
        composeRule.onNodeWithText("Download Chapter Range").performClick()
        assert(dispatchedActions.lastOrNull() == SeriesAction.ShowRangePicker)

        // Click Mark All as Read
        composeRule.onNodeWithContentDescription("More actions").performClick()
        composeRule.onNodeWithText("Mark All as Read").performClick()
        assert(dispatchedActions.lastOrNull() == SeriesAction.MarkAllRead)

        // Click Mark All as Unread
        composeRule.onNodeWithContentDescription("More actions").performClick()
        composeRule.onNodeWithText("Mark All as Unread").performClick()
        assert(dispatchedActions.lastOrNull() == SeriesAction.MarkAllUnread)
    }

    @Test
    fun readingProgressCard_startState_rendersAndDispatchesResumeClick() {
        val chapter1 = Chapter(seriesUrl = "s", sourceId = 1L, url = "c1", name = "Chapter 1: The Beginning", number = 1f)
        var resumed = false
        composeRule.setContent {
            ReadingProgressCard(
                resumeState = ReadingResumeState.Start(chapter1),
                readCount = 0,
                totalCount = 10,
                onResumeClick = { resumed = true },
            )
        }
        composeRule.onNodeWithTag("reading_progress_card").assertIsDisplayed()
        composeRule.onNodeWithText("START READING").assertIsDisplayed()
        composeRule.onNodeWithText("Chapter 1: The Beginning").assertIsDisplayed()
        composeRule.onNodeWithText("Start Chapter 1").assertIsDisplayed()

        composeRule.onNodeWithTag("resume_reading_button").performClick()
        assert(resumed)
    }

    @Test
    fun readingProgressCard_resumeState_rendersProgressPillAndBar() {
        val chapter1 = Chapter(seriesUrl = "s", sourceId = 1L, url = "c1", name = "Chapter 1", number = 1f)
        var resumed = false
        composeRule.setContent {
            ReadingProgressCard(
                resumeState = ReadingResumeState.Resume(chapter1, percent = 45),
                readCount = 0,
                totalCount = 10,
                onResumeClick = { resumed = true },
            )
        }
        composeRule.onNodeWithText("CONTINUE READING").assertIsDisplayed()
        composeRule.onNodeWithTag("reading_progress_pill").assertIsDisplayed()
        composeRule.onNodeWithText("45%").assertIsDisplayed()
        composeRule.onNodeWithTag("reading_progress_bar").assertIsDisplayed()
        composeRule.onNodeWithText("Resume Ch. 1").assertIsDisplayed()

        composeRule.onNodeWithTag("resume_reading_button").performClick()
        assert(resumed)
    }

    @Test
    fun readingProgressCard_continueState_rendersCountAndDispatches() {
        val chapter2 = Chapter(seriesUrl = "s", sourceId = 1L, url = "c2", name = "Chapter 2", number = 2f)
        var resumed = false
        composeRule.setContent {
            ReadingProgressCard(
                resumeState = ReadingResumeState.Continue(targetChapter = chapter2, lastCompletedNumber = 1f),
                readCount = 1,
                totalCount = 2,
                onResumeClick = { resumed = true },
            )
        }
        composeRule.onNodeWithText("CONTINUE READING").assertIsDisplayed()
        composeRule.onNodeWithTag("read_progress_count").assertIsDisplayed()
        composeRule.onNodeWithText("1 / 2 read (50%)").assertIsDisplayed()
        composeRule.onNodeWithTag("reading_progress_bar").assertIsDisplayed()
        composeRule.onNodeWithText("Continue Ch. 2").assertIsDisplayed()

        composeRule.onNodeWithTag("resume_reading_button").performClick()
        assert(resumed)
    }

    @Test
    fun readingProgressCard_completedState_rendersCompletedAndReadAgain() {
        val chapter1 = Chapter(seriesUrl = "s", sourceId = 1L, url = "c1", name = "Chapter 1", number = 1f)
        val chapter2 = Chapter(seriesUrl = "s", sourceId = 1L, url = "c2", name = "Chapter 2: Finale", number = 2f)
        var resumed = false
        composeRule.setContent {
            ReadingProgressCard(
                resumeState = ReadingResumeState.Completed(firstChapter = chapter1, lastChapter = chapter2),
                readCount = 2,
                totalCount = 2,
                onResumeClick = { resumed = true },
            )
        }
        composeRule.onNodeWithText("COMPLETED").assertIsDisplayed()
        composeRule.onNodeWithText("Chapter 2: Finale").assertIsDisplayed()
        composeRule.onNodeWithTag("reading_progress_pill").assertIsDisplayed()
        composeRule.onNodeWithText("100%").assertIsDisplayed()
        composeRule.onNodeWithTag("reading_progress_bar").assertIsDisplayed()
        composeRule.onNodeWithText("Read Again").assertIsDisplayed()

        composeRule.onNodeWithTag("resume_reading_button").performClick()
        assert(resumed)
    }

    @Test
    fun chapterSectionHeader_rendersAndDispatchesActions() {
        var sortToggled = false
        var unreadToggled = false
        var downloadUnreadClicked = false

        composeRule.setContent {
            ChapterSectionHeader(
                chapterCount = 25,
                sortAscending = false,
                unreadOnly = false,
                onToggleSort = { sortToggled = true },
                onToggleUnread = { unreadToggled = true },
                onDownloadUnread = { downloadUnreadClicked = true },
            )
        }

        composeRule.onNodeWithTag("chapter_count_header").assertIsDisplayed()
        composeRule.onNodeWithText("Chapters (25)").assertIsDisplayed()

        val sortChip = composeRule.onNodeWithTag("chapter_sort_chip")
        sortChip.assertIsDisplayed()
        composeRule.onNodeWithText("↓ Desc").assertIsDisplayed()
        sortChip.performClick()
        assert(sortToggled)

        val unreadChip = composeRule.onNodeWithTag("chapter_unread_filter_chip")
        unreadChip.assertIsDisplayed()
        composeRule.onNodeWithText("Unread").assertIsDisplayed()
        unreadChip.performClick()
        assert(unreadToggled)

        val downloadButton = composeRule.onNodeWithTag("download_unread_button")
        downloadButton.assertIsDisplayed()
        composeRule.onNodeWithText("Download Unread").assertIsDisplayed()
        downloadButton.performClick()
        assert(downloadUnreadClicked)
    }

    @Test
    fun chapterRow_rendersThreeStatesAndDownloadedBadge() {
        val unreadCh = ChapterWithState(
            chapter = Chapter(seriesUrl = "s", sourceId = 1L, url = "c1", name = "Ch 1 Unread", number = 1f),
            read = false,
            downloaded = true,
            progress = 0f,
        )
        val inProgressCh = ChapterWithState(
            chapter = Chapter(seriesUrl = "s", sourceId = 1L, url = "c2", name = "Ch 2 InProgress", number = 2f),
            read = false,
            downloaded = false,
            progress = 0.5f,
        )
        val completedCh = ChapterWithState(
            chapter = Chapter(seriesUrl = "s", sourceId = 1L, url = "c3", name = "Ch 3 Completed", number = 3f),
            read = true,
            downloaded = false,
            progress = 1f,
        )

        composeRule.setContent {
            ChapterRow(chapterWithState = unreadCh, onClick = {}, onMoreClick = {})
        }
        composeRule.onNodeWithTag("unread_indicator").assertIsDisplayed()
        composeRule.onNodeWithText("Downloaded").assertIsDisplayed()

        composeRule.setContent {
            ChapterRow(chapterWithState = inProgressCh, onClick = {}, onMoreClick = {})
        }
        composeRule.onNodeWithTag("in_progress_indicator").assertIsDisplayed()
        composeRule.onNodeWithTag("progress_indicator").assertIsDisplayed()
        composeRule.onNodeWithText("50%").assertIsDisplayed()
        composeRule.onNodeWithTag("chapter_progress_bar").assertIsDisplayed()

        composeRule.setContent {
            ChapterRow(chapterWithState = completedCh, onClick = {}, onMoreClick = {})
        }
        composeRule.onNodeWithTag("read_indicator").assertIsDisplayed()
    }

    @Test
    fun chapterActionSheet_rendersAndDispatchesActions() {
        val testCh = ChapterWithState(
            chapter = Chapter(seriesUrl = "s", sourceId = 1L, url = "c1", name = "Chapter 1", number = 1f),
            read = false,
            downloaded = false,
            progress = 0f,
        )
        var toggledRead = false
        var markedPrevious = false
        var downloaded = false
        var dismissed = false

        composeRule.setContent {
            ChapterActionSheet(
                chapterWithState = testCh,
                onToggleRead = { toggledRead = true },
                onMarkPreviousRead = { markedPrevious = true },
                onDownloadChapter = { downloaded = true },
                onDismissRequest = { dismissed = true },
            )
        }

        composeRule.onNodeWithText("Chapter 1").assertIsDisplayed()
        composeRule.onNodeWithText("Mark as Read").assertIsDisplayed()
        composeRule.onNodeWithText("Mark Previous Chapters as Read").assertIsDisplayed()
        composeRule.onNodeWithText("Download Chapter").assertIsDisplayed()

        composeRule.onNodeWithText("Mark as Read").performClick()
        assert(toggledRead)
    }
}
