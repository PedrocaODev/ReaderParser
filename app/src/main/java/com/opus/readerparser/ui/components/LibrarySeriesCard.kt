package com.opus.readerparser.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.opus.readerparser.domain.model.ContentType
import com.opus.readerparser.domain.model.Series
import com.opus.readerparser.ui.library.LibrarySeriesItem
import com.opus.readerparser.ui.theme.AppSpacing
import com.opus.readerparser.ui.theme.CoverAspectRatio
import com.opus.readerparser.ui.theme.ReaderParserTheme
import kotlin.math.roundToInt

@Composable
fun LibrarySeriesCard(
    item: LibrarySeriesItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onLongClick: (() -> Unit)? = null,
) {
    LibrarySeriesCard(
        series = item.series,
        onClick = onClick,
        modifier = modifier,
        onLongClick = onLongClick,
        unreadCount = item.unreadCount,
        downloadedCount = item.downloadedCount,
        currentChapterNumber = item.currentChapterNumber,
        totalChapters = item.totalChapters,
        progressRatio = item.progressRatio,
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LibrarySeriesCard(
    series: Series,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onLongClick: (() -> Unit)? = null,
    unreadCount: Int = 0,
    downloadedCount: Int = 0,
    currentChapterNumber: Float? = null,
    totalChapters: Int = 0,
    progressRatio: Float = 0f,
) {
    Card(
        modifier = modifier
            .testTag("series_card")
            .combinedClickable(onClick = onClick, onLongClick = onLongClick),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(CoverAspectRatio),
            ) {
                AsyncImage(
                    model = series.coverUrl,
                    contentDescription = series.title,
                    contentScale = ContentScale.Crop,
                    placeholder = ColorPainter(MaterialTheme.colorScheme.surfaceContainer),
                    error = ColorPainter(MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.fillMaxSize(),
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.25f),
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.45f),
                                ),
                            ),
                        ),
                )
                if (unreadCount > 0) {
                    UnreadBadge(
                        count = unreadCount,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(6.dp)
                            .testTag("unread_badge"),
                    )
                }
            }

            Column(
                modifier = Modifier.padding(horizontal = AppSpacing.sm, vertical = AppSpacing.sm),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.xs),
            ) {
                Text(
                    text = series.title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                val percent = (progressRatio.coerceIn(0f, 1f) * 100).roundToInt()
                val progressText = when {
                    totalChapters > 0 -> "Ch. ${formatChapterNumber(currentChapterNumber ?: 1f)} / $totalChapters · $percent%"
                    currentChapterNumber != null -> "Ch. ${formatChapterNumber(currentChapterNumber)} · $percent%"
                    else -> "$percent%"
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = progressText,
                        style = MaterialTheme.typography.labelMedium.copy(fontSize = 11.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(1f, fill = false)
                            .testTag("series_card_progress_text"),
                    )
                    if (downloadedCount > 0) {
                        DownloadedBadge(
                            label = "✓ $downloadedCount",
                            modifier = Modifier.testTag("downloaded_badge"),
                        )
                    }
                }

                MiniProgressBar(
                    progress = progressRatio,
                )
            }
        }
    }
}

private fun formatChapterNumber(number: Float): String =
    if (number % 1f == 0f) number.toInt().toString() else number.toString()

@Composable
fun MiniProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
) {
    val clamped = progress.coerceIn(0f, 1f)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(4.dp)
            .clip(RoundedCornerShape(2.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .testTag("mini_progress_bar")
            .semantics {
                progressBarRangeInfo = ProgressBarRangeInfo(clamped, 0f..1f)
            },
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(fraction = clamped)
                .height(4.dp)
                .background(MaterialTheme.colorScheme.primary),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LibrarySeriesCardPreview() {
    ReaderParserTheme {
        LibrarySeriesCard(
            series = Series(
                sourceId = 1L,
                url = "https://example.com/series",
                title = "Solo Leveling",
                coverUrl = null,
                type = ContentType.MANHWA,
            ),
            unreadCount = 2,
            downloadedCount = 3,
            currentChapterNumber = 42f,
            totalChapters = 1420,
            progressRatio = 0.68f,
            onClick = {},
        )
    }
}
