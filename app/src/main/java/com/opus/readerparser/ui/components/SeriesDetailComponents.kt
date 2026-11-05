package com.opus.readerparser.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.opus.readerparser.domain.model.ChapterWithState
import com.opus.readerparser.domain.model.Series
import com.opus.readerparser.ui.series.ReadingResumeState
import com.opus.readerparser.ui.theme.AppSpacing
import com.opus.readerparser.ui.theme.PillShape
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatChapterNumber(number: Float): String =
    if (number % 1f == 0f) number.toInt().toString() else number.toString()

fun formatUploadDate(epochMillis: Long): String =
    SimpleDateFormat("MMM d, yyyy", Locale.US).format(Date(epochMillis))

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SeriesHeroCard(
    series: Series,
    inLibrary: Boolean,
    onToggleLibrary: () -> Unit,
    modifier: Modifier = Modifier,
    sourceName: String? = null,
) {
    Surface(
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = modifier
            .fillMaxWidth()
            .testTag("series_hero_card"),
    ) {
        Column(modifier = Modifier.padding(AppSpacing.lg)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.md),
                modifier = Modifier.fillMaxWidth(),
            ) {
                AsyncImage(
                    model = series.coverUrl,
                    contentDescription = series.title,
                    contentScale = ContentScale.Crop,
                    placeholder = ColorPainter(Color.LightGray),
                    error = ColorPainter(Color.Gray),
                    modifier = Modifier
                        .size(width = 96.dp, height = 140.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .testTag("series_cover"),
                )
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(AppSpacing.xs),
                ) {
                    Text(
                        text = series.title,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                    )

                    val subtitle = listOfNotNull(
                        series.author?.takeIf { it.isNotBlank() },
                        sourceName?.takeIf { it.isNotBlank() },
                    ).joinToString(" · ")

                    if (subtitle.isNotBlank()) {
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs),
                        verticalArrangement = Arrangement.spacedBy(AppSpacing.xs),
                    ) {
                        StatusBadge(status = series.status)
                        series.genres.forEach { genre ->
                            Surface(
                                shape = PillShape,
                                color = MaterialTheme.colorScheme.surfaceContainer,
                                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            ) {
                                Text(
                                    text = genre,
                                    style = MaterialTheme.typography.labelMedium.copy(fontSize = 11.sp),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                )
                            }
                        }
                    }

                    Surface(
                        onClick = onToggleLibrary,
                        shape = PillShape,
                        color = if (inLibrary) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.surfaceContainer
                        },
                        contentColor = if (inLibrary) {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                        border = if (inLibrary) {
                            BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                        } else {
                            BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        },
                        modifier = Modifier.testTag("library_toggle_button"),
                    ) {
                        Text(
                            text = if (inLibrary) "✓ In Library" else "+ Add to Library",
                            style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        )
                    }
                }
            }

            if (!series.description.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(AppSpacing.md))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(modifier = Modifier.height(AppSpacing.sm))
                ExpandableSynopsis(description = series.description)
            }
        }
    }
}

@Composable
fun ExpandableSynopsis(
    description: String,
    modifier: Modifier = Modifier,
    initiallyExpanded: Boolean = false,
) {
    var expanded by remember { mutableStateOf(initiallyExpanded) }

    Column(modifier = modifier) {
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = if (expanded) Int.MAX_VALUE else 3,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.testTag("synopsis_text"),
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = if (expanded) "Show less" else "Show more",
            style = MaterialTheme.typography.labelMedium.copy(
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
            ),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .clickable { expanded = !expanded }
                .testTag("synopsis_toggle_button"),
        )
    }
}

@Composable
fun ReadingProgressCard(
    resumeState: ReadingResumeState,
    readCount: Int,
    totalCount: Int,
    onResumeClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (resumeState is ReadingResumeState.None) return

    val overline: String
    val chapterTitle: String
    val buttonLabel: String
    val percentPill: String?
    val progressFraction: Float?
    val countText: String?

    when (resumeState) {
        is ReadingResumeState.Start -> {
            overline = "START READING"
            chapterTitle = resumeState.targetChapter.name
            buttonLabel = "Start Chapter ${formatChapterNumber(resumeState.targetChapter.number)}"
            percentPill = null
            progressFraction = null
            countText = null
        }
        is ReadingResumeState.Resume -> {
            overline = "CONTINUE READING"
            chapterTitle = resumeState.targetChapter.name
            buttonLabel = "Resume Ch. ${formatChapterNumber(resumeState.targetChapter.number)}"
            percentPill = "${resumeState.percent}%"
            progressFraction = (resumeState.percent / 100f).coerceIn(0f, 1f)
            countText = null
        }
        is ReadingResumeState.Continue -> {
            overline = "CONTINUE READING"
            chapterTitle = resumeState.targetChapter.name
            buttonLabel = "Continue Ch. ${formatChapterNumber(resumeState.targetChapter.number)}"
            percentPill = null
            progressFraction = if (totalCount > 0) (readCount.toFloat() / totalCount).coerceIn(0f, 1f) else 0f
            val overallPercent = if (totalCount > 0) (readCount * 100) / totalCount else 0
            countText = "$readCount / $totalCount read ($overallPercent%)"
        }
        is ReadingResumeState.Completed -> {
            overline = "COMPLETED"
            chapterTitle = resumeState.lastChapter.name
            buttonLabel = "Read Again"
            percentPill = "100%"
            progressFraction = 1f
            countText = null
        }
        ReadingResumeState.None -> return
    }

    Surface(
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
        modifier = modifier
            .fillMaxWidth()
            .testTag("reading_progress_card"),
    ) {
        Column(
            modifier = Modifier.padding(AppSpacing.lg),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = overline,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                    ),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
                if (percentPill != null) {
                    Surface(
                        shape = PillShape,
                        color = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.testTag("reading_progress_pill"),
                    ) {
                        Text(
                            text = percentPill,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        )
                    }
                }
            }

            Text(
                text = chapterTitle,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            if (countText != null) {
                Text(
                    text = countText,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.testTag("read_progress_count"),
                )
            }

            if (progressFraction != null) {
                LinearProgressIndicator(
                    progress = { progressFraction },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(PillShape)
                        .testTag("reading_progress_bar"),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                )
            }

            Button(
                onClick = onResumeClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("resume_reading_button"),
            ) {
                Text(text = buttonLabel)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChapterSectionHeader(
    chapterCount: Int,
    sortAscending: Boolean,
    unreadOnly: Boolean,
    onToggleSort: () -> Unit,
    onToggleUnread: () -> Unit,
    onDownloadUnread: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = AppSpacing.lg, vertical = AppSpacing.sm),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Chapters ($chapterCount)",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                ),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.testTag("chapter_count_header"),
            )

            Surface(
                onClick = onDownloadUnread,
                shape = PillShape,
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                contentColor = MaterialTheme.colorScheme.onSurface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.testTag("download_unread_button"),
            ) {
                Text(
                    text = "Download Unread",
                    style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp),
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ChipFilter(
                label = if (sortAscending) "↑ Asc" else "↓ Desc",
                selected = sortAscending,
                onClick = onToggleSort,
                testTag = "chapter_sort_chip",
            )
            ChipFilter(
                label = "Unread",
                selected = unreadOnly,
                onClick = onToggleUnread,
                testTag = "chapter_unread_filter_chip",
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChapterRow(
    chapterWithState: ChapterWithState,
    onClick: () -> Unit,
    onMoreClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isCompleted = chapterWithState.read
    val isInProgress = !chapterWithState.read && chapterWithState.progress > 0f
    val isUnread = !chapterWithState.read && chapterWithState.progress <= 0f

    val borderColor = if (isInProgress) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.outlineVariant
    }

    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, borderColor),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = AppSpacing.lg, vertical = 4.dp)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onMoreClick,
            ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppSpacing.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.md),
        ) {
            when {
                isCompleted -> {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "Read",
                        modifier = Modifier
                            .size(16.dp)
                            .testTag("read_indicator"),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                isInProgress -> {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(color = MaterialTheme.colorScheme.primary, shape = CircleShape)
                            .testTag("in_progress_indicator"),
                    )
                }
                isUnread -> {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(color = MaterialTheme.colorScheme.primary, shape = CircleShape)
                            .testTag("unread_indicator"),
                    )
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.xs),
            ) {
                Text(
                    text = chapterWithState.chapter.name,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 13.sp,
                        fontWeight = if (isCompleted) FontWeight.Normal else FontWeight.Medium,
                    ),
                    color = if (isCompleted) {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    val metadata = buildList {
                        add("Ch. ${formatChapterNumber(chapterWithState.chapter.number)}")
                        val upload = chapterWithState.chapter.uploadDate
                        if (upload != null && upload > 0L) {
                            add(formatUploadDate(upload))
                        }
                    }.joinToString(" · ")

                    Text(
                        text = metadata,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                    if (chapterWithState.downloaded) {
                        DownloadedBadge()
                    }
                }

                if (isInProgress) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "${(chapterWithState.progress * 100).toInt()}%",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.testTag("progress_indicator"),
                        )
                        LinearProgressIndicator(
                            progress = { chapterWithState.progress.coerceIn(0f, 1f) },
                            modifier = Modifier
                                .width(44.dp)
                                .height(3.dp)
                                .clip(PillShape)
                                .testTag("chapter_progress_bar"),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                        )
                    }
                }
            }

            IconButton(
                onClick = onMoreClick,
                modifier = Modifier.testTag("chapter_more_button"),
            ) {
                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = "Chapter actions",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChapterActionSheet(
    chapterWithState: ChapterWithState,
    onToggleRead: () -> Unit,
    onMarkPreviousRead: () -> Unit,
    onDownloadChapter: () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
        ) {
            Text(
                text = chapterWithState.chapter.name,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            Text(
                text = if (chapterWithState.read) "Mark as Unread" else "Mark as Read",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onToggleRead()
                        onDismissRequest()
                    }
                    .padding(horizontal = 24.dp, vertical = 16.dp),
            )

            Text(
                text = "Mark Previous Chapters as Read",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onMarkPreviousRead()
                        onDismissRequest()
                    }
                    .padding(horizontal = 24.dp, vertical = 16.dp),
            )

            if (!chapterWithState.downloaded) {
                Text(
                    text = "Download Chapter",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onDownloadChapter()
                            onDismissRequest()
                        }
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                )
            }
        }
    }
}
