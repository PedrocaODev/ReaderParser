package com.opus.readerparser.ui.reader

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import coil3.compose.LocalPlatformContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.compose.AsyncImage
import com.opus.readerparser.domain.model.AppSettings
import com.opus.readerparser.domain.model.ContentType
import com.opus.readerparser.domain.model.ManhwaLayout
import com.opus.readerparser.domain.model.ManhwaZoom
import com.opus.readerparser.ui.theme.ReaderParserTheme
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlin.math.abs

private fun headingScale(level: Int): Float = when (level) {
    1 -> 1.4f
    2 -> 1.25f
    3 -> 1.15f
    else -> 1.05f
}

@Composable
fun NovelTextEngine(
    chapterKey: String,
    html: String,
    settings: AppSettings,
    progress: Float,
    onProgressChanged: (Float) -> Unit,
    onTap: () -> Unit,
    modifier: Modifier = Modifier,
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
            val bottomReached = !listState.canScrollForward ||
                (lastItem.index == blocks.lastIndex && lastItem.offset + lastItem.size <= layoutInfo.viewportEndOffset)
            calculateNovelScrollProgress(
                firstVisibleIndex = firstIdx,
                firstVisibleOffset = firstOffset,
                firstVisibleSize = firstItem.size,
                lastVisibleIndex = lastItem.index,
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

    val fontFamily = remember(settings.novelFontFamily) {
        resolveNovelFontFamily(settings.novelFontFamily)
    }
    val fontSizeSp = settings.novelFontSize.sp
    val lineHeightSp = (settings.novelFontSize * settings.novelLineHeightMultiplier).sp

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
                when (block) {
                    is NovelBlock.Heading -> {
                        val scale = headingScale(block.level)
                        Text(
                            text = block.text,
                            color = MaterialTheme.colorScheme.onBackground,
                            fontFamily = fontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = (settings.novelFontSize * scale).sp,
                            lineHeight = (settings.novelFontSize * scale * 1.3f).sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp, bottom = 4.dp),
                        )
                    }
                    is NovelBlock.Paragraph -> {
                        Text(
                            text = block.text,
                            color = MaterialTheme.colorScheme.onBackground,
                            fontFamily = fontFamily,
                            fontSize = fontSizeSp,
                            lineHeight = lineHeightSp,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                    is NovelBlock.Divider -> {
                        HorizontalDivider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                        )
                    }
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
    var showControls by remember { mutableStateOf(true) }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = { showControls = !showControls })
                },
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .testTag("loading"),
                    )
                }
                state.error != null -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = state.error,
                            modifier = Modifier.testTag("error_message"),
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center,
                        )
                        TextButton(onClick = { onAction(ReaderAction.Retry) }) {
                            Text("Retry")
                        }
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
                        onTap = { showControls = !showControls },
                        modifier = Modifier.fillMaxSize(),
                    )
                }
                state.contentType == ContentType.MANHWA && state.pages.isNotEmpty() -> {
                    key(state.chapter?.url, state.settings.manhwaLayout) {
                        ManhwaPageList(
                            pages = state.pages,
                            currentPage = state.currentPage,
                            layout = state.settings.manhwaLayout,
                            zoom = state.settings.manhwaZoom,
                            onPageChanged = { page -> onAction(ReaderAction.SetPage(page)) },
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

            // Top Bar Overlay
            AnimatedVisibility(
                visible = showControls,
                enter = slideInVertically { -it },
                exit = slideOutVertically { -it },
                modifier = Modifier.align(Alignment.TopCenter),
            ) {
                TopAppBar(
                    title = {
                        Text(
                            text = state.chapter?.name ?: "",
                            maxLines = 1,
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                    ),
                )
            }

            // Bottom Bar Overlay
            AnimatedVisibility(
                visible = showControls,
                enter = slideInVertically { it },
                exit = slideOutVertically { it },
                modifier = Modifier.align(Alignment.BottomCenter),
            ) {
                BottomAppBar(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                    contentPadding = PaddingValues(horizontal = 8.dp),
                ) {
                    IconButton(
                        onClick = { onAction(ReaderAction.PreviousChapter) },
                        enabled = state.hasPreviousChapter,
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous chapter")
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = when (state.contentType) {
                            ContentType.NOVEL -> if (state.progress > 0f) "${(state.progress * 100).toInt()}%" else ""
                            ContentType.MANHWA -> if (state.pages.isNotEmpty()) "${state.currentPage + 1} / ${state.pages.size}" else ""
                        },
                        style = MaterialTheme.typography.labelLarge,
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    IconButton(
                        onClick = { onAction(ReaderAction.NextChapter) },
                        enabled = state.hasNextChapter,
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next chapter")
                    }
                    IconButton(onClick = { onAction(ReaderAction.OpenChapterList) }) {
                        Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Chapter list")
                    }
                    IconButton(onClick = { onAction(ReaderAction.DownloadChapter) }) {
                        Icon(Icons.Filled.Download, contentDescription = "Download chapter")
                    }
                }
            }

            // Floating progress indicator when controls are hidden
            AnimatedVisibility(
                visible = !showControls && (state.pages.isNotEmpty() || state.progress > 0f),
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
                    .navigationBarsPadding(),
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f),
                    shape = MaterialTheme.shapes.small,
                ) {
                    Text(
                        text = when (state.contentType) {
                            ContentType.NOVEL -> "${(state.progress * 100).toInt()}%"
                            ContentType.MANHWA -> "${state.currentPage + 1} / ${state.pages.size}"
                        },
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    )
                }
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
    onPageChanged: (Int) -> Unit,
    imageLoader: ImageLoader? = null,
    modifier: Modifier = Modifier,
) {
    val contentScale = when (zoom) {
        ManhwaZoom.FIT_WIDTH -> ContentScale.FillWidth
        ManhwaZoom.FIT_HEIGHT -> ContentScale.Fit
        ManhwaZoom.ORIGINAL -> ContentScale.Inside
    }
    val placeholderPainter = ColorPainter(MaterialTheme.colorScheme.surfaceVariant)
    val errorPainter = ColorPainter(MaterialTheme.colorScheme.errorContainer)

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
                    AsyncImage(
                        model = pageUrl,
                        contentDescription = "Page ${index + 1}",
                        imageLoader = imageLoader
                            ?: SingletonImageLoader.get(LocalPlatformContext.current),
                        contentScale = contentScale,
                        placeholder = placeholderPainter,
                        error = errorPainter,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 150.dp),
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
                    AsyncImage(
                        model = pages[pageIndex],
                        contentDescription = "Page ${pageIndex + 1}",
                        imageLoader = imageLoader
                            ?: SingletonImageLoader.get(LocalPlatformContext.current),
                        contentScale = contentScale,
                        placeholder = placeholderPainter,
                        error = errorPainter,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
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
