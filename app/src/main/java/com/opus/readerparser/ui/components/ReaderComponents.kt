package com.opus.readerparser.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import com.opus.readerparser.domain.model.AppSettings
import com.opus.readerparser.domain.model.ContentType
import com.opus.readerparser.domain.model.ManhwaLayout
import com.opus.readerparser.domain.model.ManhwaZoom
import com.opus.readerparser.ui.reader.NovelBlock
import com.opus.readerparser.ui.reader.ReaderAction
import com.opus.readerparser.ui.reader.ReaderUiState
import com.opus.readerparser.ui.reader.resolveNovelFontFamily
import com.opus.readerparser.ui.theme.AppSpacing
import com.opus.readerparser.ui.theme.ManhwaMinPlaceholderHeight
import com.opus.readerparser.ui.theme.PillShape
import java.util.Locale
import kotlin.math.roundToInt

enum class ManhwaPageLoadState {
    LOADING,
    LOADED,
    ERROR,
}

@Composable
fun ReaderImmersiveTopBar(
    chapterTitle: String,
    seriesTitle: String,
    sourceName: String,
    contentType: ContentType,
    onBack: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val subtitle = when {
        seriesTitle.isNotEmpty() && sourceName.isNotEmpty() -> "$seriesTitle • $sourceName"
        seriesTitle.isNotEmpty() -> seriesTitle
        else -> sourceName
    }
    val outlineVariant = MaterialTheme.colorScheme.outlineVariant
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.95f),
        modifier = modifier
            .fillMaxWidth()
            .drawBehind {
                drawLine(
                    color = outlineVariant,
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = 1.dp.toPx(),
                )
            }
            .testTag("reader_top_bar"),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.testTag("reader_back_button"),
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
            ) {
                Text(
                    text = chapterTitle,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.testTag("reader_chapter_title"),
                )
                if (subtitle.isNotEmpty()) {
                    Text(
                        text = subtitle,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.testTag("reader_subtitle"),
                    )
                }
            }

            IconButton(
                onClick = onOpenSettings,
                modifier = Modifier.testTag("reader_settings_button"),
            ) {
                if (contentType == ContentType.NOVEL) {
                    Text(
                        text = "Aa",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.semantics {
                            contentDescription = "Reader settings"
                        },
                    )
                } else {
                    Icon(
                        imageVector = Icons.Filled.Tune,
                        contentDescription = "Reader settings",
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }
    }
}

@Composable
fun ReaderImmersiveBottomBar(
    contentType: ContentType,
    progress: Float,
    currentPage: Int,
    totalPages: Int,
    hasPreviousChapter: Boolean,
    hasNextChapter: Boolean,
    isDownloaded: Boolean,
    onProgressChange: (Float) -> Unit,
    onPageChange: (Int) -> Unit,
    onPreviousChapter: () -> Unit,
    onNextChapter: () -> Unit,
    onOpenChapterList: () -> Unit,
    onDownloadChapter: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val outlineVariant = MaterialTheme.colorScheme.outlineVariant
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.95f),
        modifier = modifier
            .fillMaxWidth()
            .drawBehind {
                drawLine(
                    color = outlineVariant,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = 1.dp.toPx(),
                )
            }
            .testTag("reader_bottom_bar"),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                TextButton(
                    onClick = onPreviousChapter,
                    enabled = hasPreviousChapter,
                    modifier = Modifier.testTag("reader_prev_button"),
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous chapter")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Prev")
                }

                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center,
                ) {
                    when (contentType) {
                        ContentType.NOVEL -> {
                            Slider(
                                value = progress.coerceIn(0f, 1f),
                                onValueChange = onProgressChange,
                                valueRange = 0f..1f,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("reader_scrub_slider"),
                            )
                        }
                        ContentType.MANHWA -> {
                            val maxPageFloat = (totalPages - 1).coerceAtLeast(1).toFloat()
                            val curPageFloat = if (totalPages > 1) {
                                currentPage.coerceIn(0, totalPages - 1).toFloat()
                            } else {
                                0f
                            }
                            val stepsCount = (totalPages - 2).coerceAtLeast(0)
                            Slider(
                                value = curPageFloat,
                                onValueChange = {
                                    if (totalPages > 0) {
                                        onPageChange(it.roundToInt().coerceIn(0, totalPages - 1))
                                    }
                                },
                                valueRange = 0f..maxPageFloat,
                                steps = stepsCount,
                                enabled = totalPages > 1,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("reader_scrub_slider"),
                            )
                        }
                    }
                }

                when (contentType) {
                    ContentType.NOVEL -> {
                        Text(
                            text = "${(progress.coerceIn(0f, 1f) * 100).roundToInt()}%",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.testTag("reader_progress_label"),
                        )
                    }
                    ContentType.MANHWA -> {
                        val displayPage = if (totalPages > 0) (currentPage + 1).coerceIn(1, totalPages) else 0
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Text(
                                text = "Page $displayPage of $totalPages",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.testTag("reader_progress_label"),
                            )
                            if (totalPages > 0) {
                                Text(
                                    text = "$displayPage / $totalPages",
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.testTag("reader_page_fraction_label"),
                                )
                            }
                        }
                    }
                }

                TextButton(
                    onClick = onNextChapter,
                    enabled = hasNextChapter,
                    modifier = Modifier.testTag("reader_next_button"),
                ) {
                    Text("Next")
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next chapter")
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TextButton(
                    onClick = onOpenChapterList,
                    modifier = Modifier.testTag("reader_chapter_list_button"),
                ) {
                    Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Chapter list")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Chapter List")
                }

                TextButton(
                    onClick = onDownloadChapter,
                    modifier = Modifier.testTag("reader_download_button"),
                ) {
                    Icon(Icons.Filled.Download, contentDescription = "Download chapter")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (isDownloaded) "Offline" else "Download")
                }
            }
        }
    }
}

@Composable
fun ReaderImmersiveBars(
    state: ReaderUiState,
    onAction: (ReaderAction) -> Unit,
    onBack: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenChapterList: () -> Unit = { onAction(ReaderAction.OpenChapterList) },
    modifier: Modifier = Modifier,
) {
    val currentChapterWithState = state.seriesChapters.find { it.chapter.url == state.chapter?.url }
    val isDownloaded = currentChapterWithState?.downloaded ?: false

    Box(modifier = modifier.fillMaxSize()) {
        ReaderImmersiveTopBar(
            chapterTitle = state.chapter?.name ?: "",
            seriesTitle = state.seriesTitle,
            sourceName = state.sourceName,
            contentType = state.contentType,
            onBack = onBack,
            onOpenSettings = onOpenSettings,
            modifier = Modifier.align(Alignment.TopCenter),
        )

        ReaderImmersiveBottomBar(
            contentType = state.contentType,
            progress = state.progress,
            currentPage = state.currentPage,
            totalPages = state.pages.size,
            hasPreviousChapter = state.hasPreviousChapter,
            hasNextChapter = state.hasNextChapter,
            isDownloaded = isDownloaded,
            onProgressChange = { onAction(ReaderAction.SetProgress(it)) },
            onPageChange = { onAction(ReaderAction.SetPage(it)) },
            onPreviousChapter = { onAction(ReaderAction.PreviousChapter) },
            onNextChapter = { onAction(ReaderAction.NextChapter) },
            onOpenChapterList = onOpenChapterList,
            onDownloadChapter = { onAction(ReaderAction.DownloadChapter) },
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

@Composable
fun FloatingProgressPill(
    contentType: ContentType,
    progress: Float,
    currentPage: Int,
    totalPages: Int,
    modifier: Modifier = Modifier,
) {
    val text = when (contentType) {
        ContentType.NOVEL -> "${(progress.coerceIn(0f, 1f) * 100).roundToInt()}%"
        ContentType.MANHWA -> "${(currentPage + 1).coerceIn(1, totalPages.coerceAtLeast(1))} / ${totalPages.coerceAtLeast(1)}"
    }

    Surface(
        shape = PillShape,
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = modifier
            .padding(bottom = 12.dp, end = 14.dp)
            .testTag("floating_progress_pill"),
    ) {
        Text(
            text = text,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
        )
    }
}

private fun uppercaseAnnotatedString(annotated: AnnotatedString): AnnotatedString {
    val upper = annotated.text.uppercase()
    return if (upper.length == annotated.length) {
        AnnotatedString(
            text = upper,
            spanStyles = annotated.spanStyles,
            paragraphStyles = annotated.paragraphStyles,
        )
    } else {
        AnnotatedString(upper)
    }
}

@Composable
fun NovelProseBlock(
    block: NovelBlock,
    settings: AppSettings,
    modifier: Modifier = Modifier,
) {
    when (block) {
        is NovelBlock.Heading -> {
            if (block.level == 1) {
                Column(
                    modifier = modifier
                        .fillMaxWidth()
                        .testTag("novel_heading_1"),
                ) {
                    Text(
                        text = block.text,
                        fontSize = 22.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 12.dp),
                    )
                    HorizontalDivider(
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.outline,
                    )
                }
            } else {
                Text(
                    text = uppercaseAnnotatedString(block.text),
                    fontSize = 15.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 0.08.em,
                    modifier = modifier
                        .fillMaxWidth()
                        .testTag("novel_heading_${block.level}"),
                )
            }
        }
        is NovelBlock.Paragraph -> {
            Text(
                text = block.text,
                textAlign = TextAlign.Justify,
                fontFamily = resolveNovelFontFamily(settings.novelFontFamily),
                fontSize = settings.novelFontSize.sp,
                lineHeight = (settings.novelFontSize * settings.novelLineHeightMultiplier).sp,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = modifier
                    .fillMaxWidth()
                    .testTag("novel_paragraph"),
            )
        }
        is NovelBlock.Divider -> {
            Box(
                contentAlignment = Alignment.Center,
                modifier = modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp)
                    .testTag("novel_scene_divider"),
            ) {
                Text(
                    text = "* * *",
                    fontSize = 14.sp,
                    letterSpacing = 0.4.em,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
fun NovelProseBlocks(
    blocks: List<NovelBlock>,
    settings: AppSettings,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(settings.novelParagraphSpacingDp.dp),
    ) {
        for (block in blocks) {
            NovelProseBlock(block = block, settings = settings)
        }
    }
}

@Composable
fun NovelChapterEndCard(
    hasNextChapter: Boolean,
    nextChapterTitle: String? = null,
    onNextChapter: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp)
            .testTag("novel_chapter_end_card"),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "End of Chapter",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            if (nextChapterTitle != null) {
                Text(
                    text = "Next: $nextChapterTitle",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }
            if (hasNextChapter) {
                Button(
                    onClick = onNextChapter,
                    shape = PillShape,
                    modifier = Modifier.testTag("novel_next_chapter_cta"),
                ) {
                    Text("Next Chapter →")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NovelTypographySheet(
    settings: AppSettings,
    onAction: (ReaderAction) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = modifier.testTag("novel_typography_sheet"),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = "Novel Typography",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Text(
                text = "Font Family",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            val fontOptions = listOf(
                FilterChipOption("Serif", "Serif", testTag = "font_family_serif"),
                FilterChipOption("Sans-Serif", "Sans-Serif", testTag = "font_family_sans_serif"),
                FilterChipOption("Monospace", "Monospace", testTag = "font_family_monospace"),
            )
            ChipFilterRow(
                options = fontOptions,
                selectedValue = settings.novelFontFamily,
                onSelect = { onAction(ReaderAction.SetNovelFontFamily(it)) },
            )

            SliderControl(
                label = "Font Size",
                valueText = "${settings.novelFontSize} sp",
                value = settings.novelFontSize.toFloat(),
                onValueChange = { onAction(ReaderAction.SetNovelFontSize(it.roundToInt())) },
                valueRange = 12f..32f,
                steps = 19,
                sliderTestTag = "novel_font_size_slider",
            )

            SliderControl(
                label = "Line Height",
                valueText = "${String.format(Locale.US, "%.1f", settings.novelLineHeightMultiplier)}x",
                value = settings.novelLineHeightMultiplier,
                onValueChange = { onAction(ReaderAction.SetNovelLineHeightMultiplier((it * 10f).roundToInt() / 10f)) },
                valueRange = 1.2f..2.0f,
                steps = 7,
                sliderTestTag = "novel_line_height_slider",
            )

            SliderControl(
                label = "Paragraph Spacing",
                valueText = "${settings.novelParagraphSpacingDp} dp",
                value = settings.novelParagraphSpacingDp.toFloat(),
                onValueChange = { onAction(ReaderAction.SetNovelParagraphSpacingDp(it.roundToInt())) },
                valueRange = 4f..28f,
                steps = 23,
                sliderTestTag = "novel_paragraph_spacing_slider",
            )

            SliderControl(
                label = "Horizontal Margins",
                valueText = "${settings.novelHorizontalPaddingDp} dp",
                value = settings.novelHorizontalPaddingDp.toFloat(),
                onValueChange = { onAction(ReaderAction.SetNovelHorizontalPaddingDp(it.roundToInt())) },
                valueRange = 8f..40f,
                steps = 31,
                sliderTestTag = "novel_horizontal_margins_slider",
            )

            OutlinedButton(
                onClick = { onAction(ReaderAction.ResetNovelTypographyDefaults) },
                shape = PillShape,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("novel_reset_defaults_button"),
            ) {
                Text("Reset Defaults")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManhwaSettingsSheet(
    settings: AppSettings,
    onAction: (ReaderAction) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = modifier.testTag("manhwa_settings_sheet"),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = "Manhwa Reader Settings",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Text(
                text = "Reading Layout",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            val layoutOptions = listOf(
                FilterChipOption(ManhwaLayout.WEBTOON, "Webtoon", testTag = "layout_webtoon"),
                FilterChipOption(ManhwaLayout.PAGED_LTR, "Paged LTR", testTag = "layout_paged_ltr"),
                FilterChipOption(ManhwaLayout.PAGED_RTL, "Paged RTL", testTag = "layout_paged_rtl"),
            )
            ChipFilterRow(
                options = layoutOptions,
                selectedValue = settings.manhwaLayout,
                onSelect = { onAction(ReaderAction.SetManhwaLayout(it)) },
            )

            Text(
                text = "Image Scale",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            val zoomOptions = listOf(
                FilterChipOption(ManhwaZoom.FIT_WIDTH, "Fit Width", testTag = "zoom_fit_width"),
                FilterChipOption(ManhwaZoom.FIT_HEIGHT, "Fit Height", testTag = "zoom_fit_height"),
                FilterChipOption(ManhwaZoom.ORIGINAL, "Original Scale", testTag = "zoom_original"),
            )
            ChipFilterRow(
                options = zoomOptions,
                selectedValue = settings.manhwaZoom,
                onSelect = { onAction(ReaderAction.SetManhwaZoom(it)) },
            )
        }
    }
}

@Composable
fun ManhwaPageItem(
    pageIndex: Int,
    pageUrl: String,
    zoom: ManhwaZoom,
    modifier: Modifier = Modifier,
    onRetryPage: (Int) -> Unit = {},
    onTap: () -> Unit = {},
    overrideLoadState: ManhwaPageLoadState? = null,
    retryToken: Int = 0,
    imageLoader: ImageLoader? = null,
) {
    val contentScale = when (zoom) {
        ManhwaZoom.FIT_WIDTH -> ContentScale.FillWidth
        ManhwaZoom.FIT_HEIGHT -> ContentScale.Fit
        ManhwaZoom.ORIGINAL -> ContentScale.Inside
    }
    var loadState by remember(pageUrl, retryToken) {
        mutableStateOf(overrideLoadState ?: ManhwaPageLoadState.LOADING)
    }
    val effectiveState = overrideLoadState ?: loadState

    Box(
        modifier = modifier
            .fillMaxWidth()
            .pointerInput(onTap) {
                detectTapGestures(onTap = { onTap() })
            },
        contentAlignment = Alignment.Center,
    ) {
        if ((overrideLoadState == null && loadState != ManhwaPageLoadState.ERROR) || overrideLoadState == ManhwaPageLoadState.LOADED) {
            val context = LocalPlatformContext.current
            val actualLoader = imageLoader ?: SingletonImageLoader.get(context)
            AsyncImage(
                model = pageUrl,
                contentDescription = "Page ${pageIndex + 1}",
                imageLoader = actualLoader,
                contentScale = contentScale,
                onLoading = { loadState = ManhwaPageLoadState.LOADING },
                onSuccess = { loadState = ManhwaPageLoadState.LOADED },
                onError = { loadState = ManhwaPageLoadState.ERROR },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = if (effectiveState == ManhwaPageLoadState.LOADED) ManhwaMinPlaceholderHeight else 0.dp),
            )
        }

        when (effectiveState) {
            ManhwaPageLoadState.LOADING -> {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = ManhwaMinPlaceholderHeight)
                        .testTag("manhwa_page_loading_$pageIndex"),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = ManhwaMinPlaceholderHeight)
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(36.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Loading page ${pageIndex + 1}...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
            ManhwaPageLoadState.ERROR -> {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = ManhwaMinPlaceholderHeight)
                        .testTag("manhwa_page_error_$pageIndex"),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = ManhwaMinPlaceholderHeight)
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            text = "Failed to load page ${pageIndex + 1}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { onRetryPage(pageIndex) },
                            modifier = Modifier.testTag("manhwa_page_retry_$pageIndex"),
                        ) {
                            Text("Retry")
                        }
                    }
                }
            }
            ManhwaPageLoadState.LOADED -> {
                // Image is displayed
            }
        }
    }
}

@Composable
fun ManhwaMismatchBanner(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
        ),
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .testTag("manhwa_mismatch_banner"),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "Content Format Mismatch",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onErrorContainer,
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer,
                textAlign = TextAlign.Center,
                modifier = Modifier.testTag("error_message"),
            )
            Button(
                onClick = onRetry,
                modifier = Modifier.testTag("retry_button"),
            ) {
                Text("Retry")
            }
        }
    }
}
