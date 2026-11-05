package com.opus.readerparser.ui.reader

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.opus.readerparser.domain.model.AppSettings
import com.opus.readerparser.domain.model.Chapter
import com.opus.readerparser.domain.model.ChapterWithState
import com.opus.readerparser.domain.model.ContentType
import com.opus.readerparser.domain.model.ManhwaLayout
import com.opus.readerparser.domain.model.ManhwaZoom
import com.opus.readerparser.testutil.FakeCoilRule
import com.opus.readerparser.ui.components.ManhwaMismatchBanner
import com.opus.readerparser.ui.components.ManhwaPageItem
import com.opus.readerparser.ui.components.ManhwaPageLoadState
import com.opus.readerparser.ui.components.NovelChapterEndCard
import com.opus.readerparser.ui.components.NovelProseBlocks
import com.opus.readerparser.ui.components.ReaderChapterListSheet
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ReaderContentTest {

    @get:Rule
    val composeRule = createComposeRule()

    @get:Rule
    val fakeCoilRule = FakeCoilRule()

    private val chapter = Chapter(
        seriesUrl = "https://example.com/series/test",
        sourceId = 1L,
        url = "https://example.com/chapter/1",
        name = "Chapter 1",
        number = 1f,
    )

    @Test
    fun loadingState_showsProgressIndicator() {
        composeRule.setContent {
            ReaderContent(
                state = ReaderUiState(contentType = ContentType.NOVEL, isLoading = true),
                isDarkTheme = false,
                onAction = {},
            )
        }
        composeRule.onNodeWithTag("loading").assertIsDisplayed()
    }

    @Test
    fun errorState_showsErrorMessage() {
        composeRule.setContent {
            ReaderContent(
                state = ReaderUiState(contentType = ContentType.NOVEL, error = "Failed to load"),
                isDarkTheme = false,
                onAction = {},
            )
        }
        composeRule.onNodeWithTag("error_message").assertIsDisplayed()
        composeRule.onNodeWithText("Failed to load").assertIsDisplayed()
    }

    @Test
    fun novelContent_showsWebView() {
        composeRule.setContent {
            ReaderContent(
                state = ReaderUiState(
                    contentType = ContentType.NOVEL,
                    html = "<p>Hello world</p>",
                    chapter = chapter,
                ),
                isDarkTheme = false,
                onAction = {},
            )
        }
        composeRule.onNodeWithTag("novel_webview").assertIsDisplayed()
    }

    @Test
    fun manhwaContent_showsPagesList() {
        composeRule.setContent {
            Box(Modifier.size(400.dp, 800.dp)) {
                ReaderContent(
                    state = ReaderUiState(
                        contentType = ContentType.MANHWA,
                        pages = listOf("https://example.com/p1.jpg", "https://example.com/p2.jpg"),
                        chapter = chapter,
                    ),
                    isDarkTheme = false,
                    onAction = {},
                    imageLoader = fakeCoilRule.imageLoader,
                )
            }
        }
        composeRule.onNodeWithTag("pages_list").assertIsDisplayed()
    }

    @Test
    fun novelProgress_showsPercentage() {
        composeRule.setContent {
            ReaderContent(
                state = ReaderUiState(
                    contentType = ContentType.NOVEL,
                    html = "<p>Content</p>",
                    chapter = chapter,
                    progress = 0.42f,
                ),
                isDarkTheme = false,
                onAction = {},
            )
        }
        composeRule.onNodeWithText("42%").assertIsDisplayed()
    }

    @Test
    fun manhwaProgress_showsPageCount() {
        composeRule.setContent {
            ReaderContent(
                state = ReaderUiState(
                    contentType = ContentType.MANHWA,
                    pages = listOf("https://example.com/p1.jpg", "https://example.com/p2.jpg"),
                    chapter = chapter,
                    currentPage = 1,
                ),
                isDarkTheme = false,
                onAction = {},
                imageLoader = fakeCoilRule.imageLoader,
            )
        }
        composeRule.onNodeWithText("2 / 2").assertIsDisplayed()
    }

    @Test
    fun nextChapterButton_dispatchesAction() {
        var dispatched = false
        composeRule.setContent {
            ReaderContent(
                state = ReaderUiState(
                    contentType = ContentType.NOVEL,
                    html = "<p>Content</p>",
                    chapter = chapter,
                    hasNextChapter = true,
                ),
                isDarkTheme = false,
                onAction = { if (it is ReaderAction.NextChapter) dispatched = true },
            )
        }
        composeRule.onNodeWithContentDescription("Next chapter").performClick()
        assertThat(dispatched).isTrue()
    }

    @Test
    fun previousChapterButton_dispatchesAction() {
        var dispatched = false
        composeRule.setContent {
            ReaderContent(
                state = ReaderUiState(
                    contentType = ContentType.NOVEL,
                    html = "<p>Content</p>",
                    chapter = chapter,
                    hasPreviousChapter = true,
                ),
                isDarkTheme = false,
                onAction = { if (it is ReaderAction.PreviousChapter) dispatched = true },
            )
        }
        composeRule.onNodeWithContentDescription("Previous chapter").performClick()
        assertThat(dispatched).isTrue()
    }

    @Test
    fun chapterListButton_dispatchesAction() {
        var dispatched = false
        composeRule.setContent {
            ReaderContent(
                state = ReaderUiState(
                    contentType = ContentType.NOVEL,
                    html = "<p>Content</p>",
                    chapter = chapter,
                ),
                isDarkTheme = false,
                onAction = { if (it is ReaderAction.OpenChapterList) dispatched = true },
            )
        }
        composeRule.onNodeWithContentDescription("Chapter list").performClick()
        assertThat(dispatched).isTrue()
    }

    @Test
    fun downloadButton_dispatchesAction() {
        var dispatched = false
        composeRule.setContent {
            ReaderContent(
                state = ReaderUiState(
                    contentType = ContentType.NOVEL,
                    html = "<p>Content</p>",
                    chapter = chapter,
                ),
                isDarkTheme = false,
                onAction = { if (it is ReaderAction.DownloadChapter) dispatched = true },
            )
        }
        composeRule.onNodeWithContentDescription("Download chapter").performClick()
        assertThat(dispatched).isTrue()
    }

    @Test
    fun retryButton_dispatchesRetryAction() {
        var dispatched = false
        composeRule.setContent {
            ReaderContent(
                state = ReaderUiState(
                    contentType = ContentType.NOVEL,
                    error = "Failed to load",
                ),
                isDarkTheme = false,
                onAction = { if (it is ReaderAction.Retry) dispatched = true },
            )
        }
        composeRule.onNodeWithText("Retry").performClick()
        assertThat(dispatched).isTrue()
    }

    @Test
    fun immersiveChromeToggling_novelShowsFloatingProgressPillWhenHidden() {
        composeRule.setContent {
            ReaderContent(
                state = ReaderUiState(
                    contentType = ContentType.NOVEL,
                    html = "<p>Novel chapter paragraph 1</p><p>Novel chapter paragraph 2</p>",
                    chapter = chapter,
                    progress = 0.68f,
                ),
                isDarkTheme = false,
                onAction = {},
            )
        }

        composeRule.onNodeWithTag("reader_top_bar").assertIsDisplayed()
        composeRule.onNodeWithTag("novel_reader").performClick()

        composeRule.onNodeWithTag("reader_top_bar").assertDoesNotExist()
        composeRule.onNodeWithTag("floating_progress_pill").assertIsDisplayed()
        composeRule.onNodeWithText("68%").assertIsDisplayed()
    }

    @Test
    fun immersiveChromeToggling_manhwaShowsFloatingProgressPillWhenHidden() {
        composeRule.setContent {
            Box(Modifier.size(400.dp, 800.dp)) {
                ReaderContent(
                    state = ReaderUiState(
                        contentType = ContentType.MANHWA,
                        pages = List(8) { "https://example.com/p$it.jpg" },
                        chapter = chapter,
                        currentPage = 0,
                    ),
                    isDarkTheme = false,
                    onAction = {},
                    imageLoader = fakeCoilRule.imageLoader,
                )
            }
        }

        composeRule.onNodeWithTag("reader_top_bar").assertIsDisplayed()
        composeRule.onNodeWithTag("pages_list").performClick()

        composeRule.onNodeWithTag("reader_top_bar").assertDoesNotExist()
        composeRule.onNodeWithTag("floating_progress_pill").assertIsDisplayed()
        composeRule.onNodeWithText("1 / 8").assertIsDisplayed()
    }

    @Test
    fun scrubSliderNavigation_dispatchesSetProgressForNovel() {
        var dispatchedAction: ReaderAction? = null
        composeRule.setContent {
            ReaderContent(
                state = ReaderUiState(
                    contentType = ContentType.NOVEL,
                    html = "<p>Content</p>",
                    chapter = chapter,
                    progress = 0.2f,
                ),
                isDarkTheme = false,
                onAction = { dispatchedAction = it },
            )
        }

        composeRule.onNodeWithTag("reader_scrub_slider")
            .performSemanticsAction(SemanticsActions.SetProgress) { it(0.75f) }

        assertThat(dispatchedAction).isInstanceOf(ReaderAction.SetProgress::class.java)
        assertThat((dispatchedAction as ReaderAction.SetProgress).progress).isWithin(0.01f).of(0.75f)
    }

    @Test
    fun scrubSliderNavigation_dispatchesSetPageForManhwa() {
        var dispatchedAction: ReaderAction? = null
        composeRule.setContent {
            Box(Modifier.size(400.dp, 800.dp)) {
                ReaderContent(
                    state = ReaderUiState(
                        contentType = ContentType.MANHWA,
                        pages = List(8) { "https://example.com/p$it.jpg" },
                        chapter = chapter,
                        currentPage = 0,
                    ),
                    isDarkTheme = false,
                    onAction = { dispatchedAction = it },
                    imageLoader = fakeCoilRule.imageLoader,
                )
            }
        }

        composeRule.onNodeWithTag("reader_scrub_slider")
            .performSemanticsAction(SemanticsActions.SetProgress) { it(3f) }

        assertThat(dispatchedAction).isEqualTo(ReaderAction.SetPage(3))
    }

    @Test
    fun openingNovelTypographySheet_selectsFontAndResetsDefaults() {
        var lastAction: ReaderAction? = null
        composeRule.setContent {
            ReaderContent(
                state = ReaderUiState(
                    contentType = ContentType.NOVEL,
                    html = "<p>Content</p>",
                    chapter = chapter,
                ),
                isDarkTheme = false,
                onAction = { lastAction = it },
            )
        }

        composeRule.onNodeWithTag("reader_settings_button").performClick()
        composeRule.onNodeWithTag("novel_typography_sheet").assertIsDisplayed()

        composeRule.onNodeWithTag("font_family_monospace").performClick()
        assertThat(lastAction).isEqualTo(ReaderAction.SetNovelFontFamily("Monospace"))

        composeRule.onNodeWithTag("novel_reset_defaults_button").performClick()
        assertThat(lastAction).isEqualTo(ReaderAction.ResetNovelTypographyDefaults)
    }

    @Test
    fun openingManhwaSettingsSheet_selectsLayoutAndScale() {
        var lastAction: ReaderAction? = null
        composeRule.setContent {
            Box(Modifier.size(400.dp, 800.dp)) {
                ReaderContent(
                    state = ReaderUiState(
                        contentType = ContentType.MANHWA,
                        pages = listOf("https://example.com/p1.jpg"),
                        chapter = chapter,
                    ),
                    isDarkTheme = false,
                    onAction = { lastAction = it },
                    imageLoader = fakeCoilRule.imageLoader,
                )
            }
        }

        composeRule.onNodeWithTag("reader_settings_button").performClick()
        composeRule.onNodeWithTag("manhwa_settings_sheet").assertIsDisplayed()

        composeRule.onNodeWithTag("layout_paged_ltr").performClick()
        assertThat(lastAction).isEqualTo(ReaderAction.SetManhwaLayout(ManhwaLayout.PAGED_LTR))

        composeRule.onNodeWithTag("layout_paged_rtl").performClick()
        assertThat(lastAction).isEqualTo(ReaderAction.SetManhwaLayout(ManhwaLayout.PAGED_RTL))

        composeRule.onNodeWithTag("zoom_fit_height").performClick()
        assertThat(lastAction).isEqualTo(ReaderAction.SetManhwaZoom(ManhwaZoom.FIT_HEIGHT))

        composeRule.onNodeWithTag("zoom_original").performClick()
        assertThat(lastAction).isEqualTo(ReaderAction.SetManhwaZoom(ManhwaZoom.ORIGINAL))
    }

    @Test
    fun openingReaderChapterListSheet_verifiesHighlightAndIndicatorsAndSelection() {
        val c1 = chapter.copy(url = "https://example.com/chapter/1", name = "Chapter 1")
        val c2 = chapter.copy(url = "https://example.com/chapter/2", name = "Chapter 2")
        val c3 = chapter.copy(url = "https://example.com/chapter/3", name = "Chapter 3")
        val chaptersWithState = listOf(
            ChapterWithState(c1, read = true, downloaded = false, progress = 1f),
            ChapterWithState(c2, read = false, downloaded = true, progress = 0f),
            ChapterWithState(c3, read = false, downloaded = false, progress = 0f),
        )
        var selectedChapter: Chapter? = null

        composeRule.setContent {
            ReaderChapterListSheet(
                chapters = chaptersWithState,
                currentChapterUrl = c2.url,
                onChapterSelected = { selectedChapter = it },
                onDismissRequest = {},
            )
        }

        composeRule.onNodeWithTag("reader_chapter_list_sheet").assertIsDisplayed()
        composeRule.onNodeWithTag("active_chapter_indicator").assertIsDisplayed()
        composeRule.onNodeWithTag("read_indicator").assertIsDisplayed()
        composeRule.onNodeWithTag("downloaded_indicator").assertIsDisplayed()

        composeRule.onNodeWithTag("reader_chapter_item_2").performClick()
        assertThat(selectedChapter).isEqualTo(c3)
    }

    @Test
    fun novelProseBlocks_rendersAllBlockTypesAndEndCard() {
        val blocks = listOf(
            NovelBlock.Heading(AnnotatedString("Chapter Title"), level = 1),
            NovelBlock.Heading(AnnotatedString("Subheading"), level = 2),
            NovelBlock.Paragraph(AnnotatedString("Paragraph text here.")),
            NovelBlock.Divider,
        )
        var nextClicked = false

        composeRule.setContent {
            Column {
                NovelProseBlocks(
                    blocks = blocks,
                    settings = AppSettings(),
                )
                NovelChapterEndCard(
                    hasNextChapter = true,
                    nextChapterTitle = "Chapter 2",
                    onNextChapter = { nextClicked = true },
                )
            }
        }

        composeRule.onNodeWithTag("novel_heading_1").assertIsDisplayed()
        composeRule.onNodeWithText("Chapter Title").assertIsDisplayed()
        composeRule.onNodeWithTag("novel_heading_2").assertIsDisplayed()
        composeRule.onNodeWithText("SUBHEADING").assertIsDisplayed()
        composeRule.onNodeWithTag("novel_paragraph").assertIsDisplayed()
        composeRule.onNodeWithText("Paragraph text here.").assertIsDisplayed()
        composeRule.onNodeWithTag("novel_scene_divider").assertIsDisplayed()
        composeRule.onNodeWithText("* * *").assertIsDisplayed()
        composeRule.onNodeWithTag("novel_chapter_end_card").assertIsDisplayed()
        composeRule.onNodeWithText("End of Chapter").assertIsDisplayed()

        composeRule.onNodeWithTag("novel_next_chapter_cta").assertIsDisplayed().performClick()
        assertThat(nextClicked).isTrue()
    }

    @Test
    fun manhwaPageItem_rendersLoadingState() {
        composeRule.setContent {
            ManhwaPageItem(
                pageIndex = 0,
                pageUrl = "https://example.com/p1.jpg",
                zoom = ManhwaZoom.FIT_WIDTH,
                overrideLoadState = ManhwaPageLoadState.LOADING,
            )
        }

        composeRule.onNodeWithTag("manhwa_page_loading_0").assertIsDisplayed()
        composeRule.onNodeWithText("Loading page 1...").assertIsDisplayed()
    }

    @Test
    fun manhwaPageItem_rendersErrorStateAndRetries() {
        var retriedPageIndex: Int? = null
        composeRule.setContent {
            ManhwaPageItem(
                pageIndex = 0,
                pageUrl = "https://example.com/p1.jpg",
                zoom = ManhwaZoom.FIT_WIDTH,
                overrideLoadState = ManhwaPageLoadState.ERROR,
                onRetryPage = { retriedPageIndex = it },
            )
        }

        composeRule.onNodeWithTag("manhwa_page_error_0").assertIsDisplayed()
        composeRule.onNodeWithText("Failed to load page 1").assertIsDisplayed()
        composeRule.onNodeWithTag("manhwa_page_retry_0").assertIsDisplayed().performClick()
        assertThat(retriedPageIndex).isEqualTo(0)
    }

    @Test
    fun manhwaMismatchBanner_rendersBannerAndRetries() {
        var retried = false
        composeRule.setContent {
            ManhwaMismatchBanner(
                message = "Expected pages but found text",
                onRetry = { retried = true },
            )
        }

        composeRule.onNodeWithTag("manhwa_mismatch_banner").assertIsDisplayed()
        composeRule.onNodeWithText("Content Format Mismatch").assertIsDisplayed()
        composeRule.onNodeWithTag("error_message").assertIsDisplayed()
        composeRule.onNodeWithText("Expected pages but found text").assertIsDisplayed()
        composeRule.onNodeWithTag("retry_button").assertIsDisplayed().performClick()
        assertThat(retried).isTrue()
    }
}
