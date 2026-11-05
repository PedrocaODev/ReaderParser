package com.opus.readerparser.ui.reader

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.ImageLoader
import com.opus.readerparser.domain.model.AppSettings
import com.opus.readerparser.domain.model.ContentType
import com.opus.readerparser.domain.model.ManhwaLayout
import com.opus.readerparser.domain.model.ManhwaZoom
import com.opus.readerparser.ui.components.FloatingProgressPill
import com.opus.readerparser.ui.components.LoadingStateView
import com.opus.readerparser.ui.components.ManhwaMismatchBanner
import com.opus.readerparser.ui.components.ManhwaPageItem
import com.opus.readerparser.ui.components.ManhwaSettingsSheet
import com.opus.readerparser.ui.components.NovelChapterEndCard
import com.opus.readerparser.ui.components.NovelProseBlock
import com.opus.readerparser.ui.components.NovelTypographySheet
import com.opus.readerparser.ui.components.ReaderChapterListSheet
import com.opus.readerparser.ui.components.ReaderImmersiveBars
import com.opus.readerparser.ui.theme.ReaderParserTheme
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlin.math.abs

@Composable
fun NovelTextEngine(
    chapterKey: String,
    html: String,
    settings: AppSettings,
    progress: Float,
    onProgressChanged: (Float) -> Unit,
    onTap: () -> Unit,
    modifier: Modifier = Modifier,
    hasNextChapter: Boolean = false,
    nextChapterTitle: String? = null,
    onNextChapter: () -> Unit = {},
) {
    val blocks = remember(html) { parseNovelHtml(html) }
    if (blocks.isEmpty()) {
        Box(
            modifier = modifier
                .background(MaterialTheme.colorScheme.background)
                .testTag("novel_webview")
                .testTag("novel_reader"),
        )
        return
    }

    var hasRestoredScroll by rememberSaveable(chapterKey) { mutableStateOf(false) }
    var restoredIndex by rememberSaveable(chapterKey) {
        mutableIntStateOf(if (progress > 0f) targetBlockIndexForProgress(progress, blocks.size) else 0)
    }

    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = if (!hasRestoredScroll && progress > 0f) {
            targetBlockIndexForProgress(progress, blocks.size)
        } else {
            0
        },
    )

    LaunchedEffect(chapterKey, blocks.size, progress) {
        if (!hasRestoredScroll && blocks.isNotEmpty()) {
            if (progress > 0f) {
                val targetIndex = targetBlockIndexForProgress(progress, blocks.size)
                restoredIndex = targetIndex
                if (listState.firstVisibleItemIndex != targetIndex) {
                    listState.scrollToItem(targetIndex)
                }
                hasRestoredScroll = true
            } else {
                restoredIndex = 0
                hasRestoredScroll = true
            }
        }
    }

    var userHasScrolled by remember(chapterKey) { mutableStateOf(false) }

    LaunchedEffect(listState, blocks.size, hasRestoredScroll) {
        if (!hasRestoredScroll || blocks.isEmpty()) return@LaunchedEffect
        snapshotFlow {
            val layoutInfo = listState.layoutInfo
            val visibleItems = layoutInfo.visibleItemsInfo
            if (visibleItems.isEmpty()) return@snapshotFlow null
            val firstIdx = listState.firstVisibleItemIndex
            val firstOffset = listState.firstVisibleItemScrollOffset
            val scrolling = listState.isScrollInProgress
            if (!userHasScrolled) {
                if (scrolling || firstIdx != restoredIndex || firstOffset != 0) {
                    userHasScrolled = true
                } else {
                    return@snapshotFlow null
                }
            }
            val firstItem = visibleItems.first()
            val lastItem = visibleItems.last()
            val clampedFirstIndex = firstIdx.coerceIn(0, blocks.lastIndex)
            val clampedLastIndex = lastItem.index.coerceIn(0, blocks.lastIndex)
            val bottomReached = !listState.canScrollForward ||
                (lastItem.index >= blocks.lastIndex && lastItem.offset + lastItem.size <= layoutInfo.viewportEndOffset)
            calculateNovelScrollProgress(
                firstVisibleIndex = clampedFirstIndex,
                firstVisibleOffset = firstOffset,
                firstVisibleSize = firstItem.size,
                lastVisibleIndex = clampedLastIndex,
                lastVisibleBottomReached = bottomReached,
                totalBlocks = blocks.size,
            )
        }
            .filterNotNull()
            .distinctUntilChanged()
            .collect { currentProgress ->
                onProgressChanged(currentProgress)
            }
    }

    Box(modifier = modifier.testTag("novel_webview")) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .pointerInput(onTap) {
                    detectTapGestures(onTap = { onTap() })
                }
                .testTag("novel_reader"),
            contentPadding = PaddingValues(
                horizontal = settings.novelHorizontalPaddingDp.dp,
                vertical = 24.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(settings.novelParagraphSpacingDp.dp),
        ) {
            itemsIndexed(
                items = blocks,
                key = { index, _ -> index },
            ) { _, block ->
                NovelProseBlock(
                    block = block,
                    settings = settings,
                )
            }
            item(key = "novel_chapter_end_card") {
                NovelChapterEndCard(
                    hasNextChapter = hasNextChapter,
                    nextChapterTitle = nextChapterTitle,
                    onNextChapter = onNextChapter,
                )
            }
        }
    }
}

@Composable
private fun ManhwaPageList(
    pages: List<String>,
    currentPage: Int,
    layout: ManhwaLayout,
    zoom: ManhwaZoom,
    pageRetryTokens: Map<Int, Int>,
    onPageChanged: (Int) -> Unit,
    onRetryPage: (Int) -> Unit,
    onTap: () -> Unit,
    imageLoader: ImageLoader? = null,
    modifier: Modifier = Modifier,
) {
    when (layout) {
        ManhwaLayout.WEBTOON -> {
            val listState = rememberLazyListState(
                initialFirstVisibleItemIndex = currentPage.coerceIn(0, pages.lastIndex),
            )

            LaunchedEffect(listState) {
                snapshotFlow {
                    val layoutInfo = listState.layoutInfo
                    val items = layoutInfo.visibleItemsInfo
                    if (items.isEmpty()) return@snapshotFlow null
                    val viewportCenter =
                        (layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset) / 2
                    items.minBy {
                        abs(it.offset + it.size / 2 - viewportCenter)
                    }.index
                }
                    .filterNotNull()
                    .distinctUntilChanged()
                    .collect { page -> onPageChanged(page) }
            }

            LazyColumn(
                state = listState,
                modifier = modifier.testTag("pages_list"),
            ) {
                itemsIndexed(
                    items = pages,
                    key = { index, pageUrl -> "$index-$pageUrl" },
                ) { index, pageUrl ->
                    ManhwaPageItem(
                        pageIndex = index,
                        pageUrl = pageUrl,
                        zoom = zoom,
                        onRetryPage = onRetryPage,
                        onTap = onTap,
                        retryToken = pageRetryTokens[index] ?: 0,
                        imageLoader = imageLoader,
                    )
                }
            }
        }
        ManhwaLayout.PAGED_LTR, ManhwaLayout.PAGED_RTL -> {
            val pagerState = rememberPagerState(
                initialPage = currentPage.coerceIn(0, pages.lastIndex),
            ) { pages.size }

            LaunchedEffect(pagerState) {
                snapshotFlow { pagerState.currentPage }
                    .distinctUntilChanged()
                    .collect { page -> onPageChanged(page) }
            }

            HorizontalPager(
                state = pagerState,
                reverseLayout = (layout == ManhwaLayout.PAGED_RTL),
                modifier = modifier.testTag("pages_list"),
            ) { pageIndex ->
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    ManhwaPageItem(
                        pageIndex = pageIndex,
                        pageUrl = pages[pageIndex],
                        zoom = zoom,
                        onRetryPage = onRetryPage,
                        onTap = onTap,
                        retryToken = pageRetryTokens[pageIndex] ?: 0,
                        imageLoader = imageLoader,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderContent(
    state: ReaderUiState,
    isDarkTheme: Boolean,
    onAction: (ReaderAction) -> Unit,
    onBack: () -> Unit = {},
    imageLoader: ImageLoader? = null,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    var showChrome by rememberSaveable { mutableStateOf(true) }
    var showSettingsSheet by rememberSaveable { mutableStateOf(false) }
    var showChapterListSheet by rememberSaveable { mutableStateOf(false) }

    val currentChapterIndex = state.seriesChapters.indexOfFirst { it.chapter.url == state.chapter?.url }
    val nextChapter = if (currentChapterIndex in 0 until state.seriesChapters.lastIndex) {
        state.seriesChapters.getOrNull(currentChapterIndex + 1)?.chapter
    } else {
        null
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = { showChrome = !showChrome })
                },
        ) {
            when {
                state.isLoading -> {
                    LoadingStateView()
                }
                state.error != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        ManhwaMismatchBanner(
                            message = state.error,
                            onRetry = { onAction(ReaderAction.Retry) },
                        )
                    }
                }
                state.contentType == ContentType.NOVEL && state.html.isNotEmpty() -> {
                    NovelTextEngine(
                        chapterKey = state.chapter?.url ?: "",
                        html = state.html,
                        settings = state.settings,
                        progress = state.progress,
                        onProgressChanged = { progress ->
                            onAction(ReaderAction.SetProgress(progress))
                        },
                        onTap = { showChrome = !showChrome },
                        modifier = Modifier.fillMaxSize(),
                        hasNextChapter = state.hasNextChapter,
                        nextChapterTitle = nextChapter?.name,
                        onNextChapter = { onAction(ReaderAction.NextChapter) },
                    )
                }
                state.contentType == ContentType.MANHWA && state.pages.isNotEmpty() -> {
                    key(state.chapter?.url, state.settings.manhwaLayout) {
                        ManhwaPageList(
                            pages = state.pages,
                            currentPage = state.currentPage,
                            layout = state.settings.manhwaLayout,
                            zoom = state.settings.manhwaZoom,
                            pageRetryTokens = state.pageRetryTokens,
                            onPageChanged = { page -> onAction(ReaderAction.SetPage(page)) },
                            onRetryPage = { pageIndex -> onAction(ReaderAction.RetryPage(pageIndex)) },
                            onTap = { showChrome = !showChrome },
                            imageLoader = imageLoader,
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
                }
                else -> {
                    Text(
                        text = "No content available",
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            // Top and Bottom Bars
            AnimatedVisibility(
                visible = showChrome,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                ReaderImmersiveBars(
                    state = state,
                    onAction = onAction,
                    onBack = onBack,
                    onOpenSettings = { showSettingsSheet = true },
                    onOpenChapterList = {
                        showChapterListSheet = true
                        onAction(ReaderAction.OpenChapterList)
                    },
                )
            }

            // Floating progress indicator when chrome is hidden
            AnimatedVisibility(
                visible = !showChrome && !state.isLoading && state.error == null && (state.pages.isNotEmpty() || state.html.isNotEmpty() || state.progress > 0f),
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .navigationBarsPadding(),
            ) {
                FloatingProgressPill(
                    contentType = state.contentType,
                    progress = state.progress,
                    currentPage = state.currentPage,
                    totalPages = state.pages.size,
                )
            }

            // Settings sheets
            if (showSettingsSheet && state.contentType == ContentType.NOVEL) {
                NovelTypographySheet(
                    settings = state.settings,
                    onAction = onAction,
                    onDismissRequest = { showSettingsSheet = false },
                )
            }

            if (showSettingsSheet && state.contentType == ContentType.MANHWA) {
                ManhwaSettingsSheet(
                    settings = state.settings,
                    onAction = onAction,
                    onDismissRequest = { showSettingsSheet = false },
                )
            }

            // Chapter list sheet
            if (showChapterListSheet) {
                ReaderChapterListSheet(
                    chapters = state.seriesChapters,
                    currentChapterUrl = state.chapter?.url,
                    onChapterSelected = { chapter ->
                        showChapterListSheet = false
                        onAction(ReaderAction.SelectChapter(chapter))
                    },
                    onDismissRequest = {
                        showChapterListSheet = false
                    },
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ReaderContentLoadingPreview() {
    ReaderParserTheme {
        ReaderContent(
            state = ReaderUiState(contentType = ContentType.NOVEL, isLoading = true),
            isDarkTheme = false,
            onAction = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ReaderContentErrorPreview() {
    ReaderParserTheme {
        ReaderContent(
            state = ReaderUiState(contentType = ContentType.MANHWA, error = "Failed to load chapter"),
            isDarkTheme = false,
            onAction = {},
        )
    }
}
