package com.opus.readerparser.ui.downloads

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.opus.readerparser.domain.model.ContentType
import com.opus.readerparser.domain.model.DownloadItem
import com.opus.readerparser.domain.model.DownloadState
import com.opus.readerparser.domain.model.DownloadStorageBreakdown
import com.opus.readerparser.domain.model.DownloadedSeriesItem
import com.opus.readerparser.ui.components.QueueStateBadge
import com.opus.readerparser.ui.components.formatStorageBytes
import com.opus.readerparser.ui.theme.PillShape

@Composable
fun StorageBreakdownCard(
    novelTextBytes: Long,
    manhwaImageBytes: Long,
    availableBytes: Long,
    modifier: Modifier = Modifier,
) {
    StorageBreakdownCard(
        breakdown = DownloadStorageBreakdown(
            novelTextBytes = novelTextBytes,
            manhwaImageBytes = manhwaImageBytes,
            availableBytes = availableBytes,
        ),
        modifier = modifier,
    )
}

@Composable
fun StorageBreakdownCard(
    breakdown: DownloadStorageBreakdown,
    modifier: Modifier = Modifier,
) {
    val novelTextBytes = breakdown.novelTextBytes
    val manhwaImageBytes = breakdown.manhwaImageBytes
    val availableBytes = breakdown.availableBytes
    val usedBytes = breakdown.usedBytes

    Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = modifier
            .fillMaxWidth()
            .testTag("storage_breakdown_card"),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "LOCAL STORAGE",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "${formatStorageBytes(usedBytes)} used",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = "${formatStorageBytes(availableBytes)} free",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            // Proportional segmented bar
            val totalCapacity = (usedBytes + availableBytes.coerceAtLeast(0L)).coerceAtLeast(1L)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                    .testTag("storage_breakdown_bar"),
            ) {
                if (usedBytes > 0L) {
                    var novelWeight = if (novelTextBytes > 0L) {
                        (novelTextBytes.toFloat() / totalCapacity.toFloat()).coerceAtLeast(0.04f)
                    } else 0f
                    var manhwaWeight = if (manhwaImageBytes > 0L) {
                        (manhwaImageBytes.toFloat() / totalCapacity.toFloat()).coerceAtLeast(0.04f)
                    } else 0f

                    val sum = novelWeight + manhwaWeight
                    if (sum > 1f) {
                        novelWeight /= sum
                        manhwaWeight /= sum
                    }

                    if (novelWeight > 0f) {
                        Box(
                            modifier = Modifier
                                .weight(novelWeight)
                                .fillMaxHeight()
                                .background(MaterialTheme.colorScheme.primary),
                        )
                    }
                    if (manhwaWeight > 0f) {
                        Box(
                            modifier = Modifier
                                .weight(manhwaWeight)
                                .fillMaxHeight()
                                .background(MaterialTheme.colorScheme.secondary),
                        )
                    }
                    val remainingWeight = (1f - (novelWeight + manhwaWeight)).coerceAtLeast(0f)
                    if (remainingWeight > 0.001f) {
                        Spacer(modifier = Modifier.weight(remainingWeight))
                    }
                }
            }

            // Legend row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(MaterialTheme.colorScheme.primary),
                    )
                    Text(
                        text = "Novels (${formatStorageBytes(novelTextBytes)})",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(MaterialTheme.colorScheme.secondary),
                    )
                    Text(
                        text = "Manhwa (${formatStorageBytes(manhwaImageBytes)})",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
fun DownloadQueueItemCard(
    item: DownloadItem,
    onCancel: () -> Unit,
    onRetry: () -> Unit,
    onDelete: () -> Unit = onCancel,
    modifier: Modifier = Modifier,
) {
    Card(
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = item.seriesTitle,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f, fill = false),
                )
                Spacer(Modifier.width(8.dp))
                QueueStateBadge(state = item.state, progress = item.progress)
            }

            Text(
                text = item.chapterName,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            if (item.state == DownloadState.RUNNING) {
                Spacer(Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { item.progress.coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                )
            }

            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                when (item.state) {
                    DownloadState.QUEUED, DownloadState.RUNNING -> {
                        TextButton(
                            onClick = onCancel,
                            modifier = Modifier.semantics { contentDescription = "Cancel download" },
                        ) {
                            Text("Cancel")
                        }
                    }
                    DownloadState.FAILED -> {
                        OutlinedButton(
                            onClick = onRetry,
                            shape = PillShape,
                        ) {
                            Text("Retry")
                        }
                        Spacer(Modifier.width(8.dp))
                        TextButton(
                            onClick = onCancel,
                            modifier = Modifier.semantics { contentDescription = "Cancel download" },
                        ) {
                            Text("Cancel")
                        }
                    }
                    DownloadState.COMPLETED -> {
                        OutlinedButton(
                            onClick = onDelete,
                            shape = PillShape,
                            modifier = Modifier.semantics { contentDescription = "Delete download" },
                        ) {
                            Text("Delete")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DownloadedSeriesCard(
    item: DownloadedSeriesItem,
    onClearSeriesDownloads: (sourceId: Long, seriesUrl: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            val isNovel = item.contentType == ContentType.NOVEL
            Surface(
                shape = MaterialTheme.shapes.small,
                color = if (isNovel) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer,
                contentColor = if (isNovel) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.size(36.dp),
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Icon(
                        imageVector = if (isNovel) Icons.AutoMirrored.Filled.MenuBook else Icons.Filled.Image,
                        contentDescription = if (isNovel) "Novel" else "Manhwa",
                        modifier = Modifier.size(20.dp),
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.seriesTitle,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = item.chapterRangeSummary,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = formatStorageBytes(item.totalBytes),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.testTag("series_bytes_${item.seriesId}"),
                )
            }

            OutlinedButton(
                onClick = { onClearSeriesDownloads(item.sourceId, item.seriesUrl) },
                shape = PillShape,
                modifier = Modifier.testTag("clear_series_${item.seriesId}"),
            ) {
                Text("Clear")
            }
        }
    }
}
