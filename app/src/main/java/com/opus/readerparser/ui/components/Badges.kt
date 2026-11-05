package com.opus.readerparser.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.opus.readerparser.domain.model.DownloadState
import com.opus.readerparser.domain.model.SeriesStatus
import com.opus.readerparser.ui.theme.PillShape
import com.opus.readerparser.ui.theme.SuccessColor

@Composable
fun UnreadBadge(
    count: Int,
    modifier: Modifier = Modifier,
) {
    if (count <= 0) return

    Surface(
        shape = PillShape,
        color = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        modifier = modifier,
    ) {
        Text(
            text = "$count unread",
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
        )
    }
}

@Composable
fun DownloadedBadge(
    modifier: Modifier = Modifier,
    label: String = "Downloaded",
) {
    Surface(
        shape = PillShape,
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        modifier = modifier,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
        )
    }
}

@Composable
fun StatusBadge(
    status: SeriesStatus,
    modifier: Modifier = Modifier,
) {
    val formattedStatus = status.name.lowercase().replace('_', ' ').replaceFirstChar { it.uppercase() }
    val (bgColor, textColor) = when (status) {
        SeriesStatus.COMPLETED -> MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
        SeriesStatus.ONGOING -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
        SeriesStatus.HIATUS, SeriesStatus.CANCELLED -> MaterialTheme.colorScheme.surfaceContainerHigh to MaterialTheme.colorScheme.onSurfaceVariant
        SeriesStatus.UNKNOWN -> MaterialTheme.colorScheme.surfaceContainer to MaterialTheme.colorScheme.onSurfaceVariant
    }

    Surface(
        shape = PillShape,
        color = bgColor,
        contentColor = textColor,
        modifier = modifier,
    ) {
        Text(
            text = formattedStatus,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
        )
    }
}

@Composable
fun QueueStateBadge(
    state: DownloadState,
    progress: Float = 0f,
    modifier: Modifier = Modifier,
) {
    val text = when (state) {
        DownloadState.QUEUED -> "Queued"
        DownloadState.RUNNING -> if (progress > 0f) "${(progress * 100).toInt()}%" else "Downloading"
        DownloadState.COMPLETED -> "Done"
        DownloadState.FAILED -> "Failed"
    }

    val (bgColor, textColor) = when (state) {
        DownloadState.COMPLETED -> MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
        DownloadState.RUNNING -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
        DownloadState.FAILED -> MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
        DownloadState.QUEUED -> MaterialTheme.colorScheme.surfaceContainerHigh to MaterialTheme.colorScheme.onSurfaceVariant
    }

    Surface(
        shape = PillShape,
        color = bgColor,
        contentColor = textColor,
        modifier = modifier,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
        )
    }
}
