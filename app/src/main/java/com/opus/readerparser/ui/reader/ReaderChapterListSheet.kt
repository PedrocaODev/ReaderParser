package com.opus.readerparser.ui.reader

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.opus.readerparser.domain.model.Chapter
import com.opus.readerparser.domain.model.ChapterWithState
import com.opus.readerparser.ui.components.DownloadedBadge
import com.opus.readerparser.ui.theme.PillShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderChapterListSheet(
    chapters: List<ChapterWithState>,
    currentChapterUrl: String?,
    onChapterSelected: (Chapter) -> Unit = {},
    onDismissRequest: () -> Unit = {},
    modifier: Modifier = Modifier,
    onSelectChapter: ((String) -> Unit)? = null,
) {
    val currentChapterIndex = chapters.indexOfFirst { it.chapter.url == currentChapterUrl }
    val listState = rememberLazyListState()

    LaunchedEffect(currentChapterIndex) {
        if (currentChapterIndex >= 0) {
            listState.scrollToItem(currentChapterIndex)
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = modifier.testTag("reader_chapter_list_sheet"),
    ) {
        Text(
            text = "Chapters",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .testTag("reader_chapter_list_title"),
        )
        HorizontalDivider(modifier = Modifier.padding(top = 12.dp, bottom = 8.dp))

        if (chapters.isEmpty()) {
            Text(
                text = "No chapters available",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 24.dp),
            )
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier.testTag("reader_chapter_list"),
            ) {
                itemsIndexed(
                    items = chapters,
                    key = { _, item -> "${item.chapter.sourceId}|${item.chapter.url}" },
                ) { index, item ->
                    val isCurrentChapter = item.chapter.url == currentChapterUrl
                    Card(
                        onClick = {
                            onChapterSelected(item.chapter)
                            onSelectChapter?.invoke(item.chapter.url)
                        },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isCurrentChapter) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                MaterialTheme.colorScheme.surface
                            },
                        ),
                        border = if (isCurrentChapter) {
                            BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                        } else null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .testTag("reader_chapter_item_$index"),
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.chapter.name,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = if (isCurrentChapter) {
                                        MaterialTheme.colorScheme.onPrimaryContainer
                                    } else if (item.read) {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    } else {
                                        MaterialTheme.colorScheme.onSurface
                                    },
                                )
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(top = 4.dp),
                                ) {
                                    if (item.downloaded) {
                                        DownloadedBadge(modifier = Modifier.testTag("downloaded_indicator"))
                                    }
                                    if (item.read) {
                                        Text(
                                            text = "Read",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.outline,
                                            modifier = Modifier.testTag("read_indicator"),
                                        )
                                    }
                                }
                            }
                            if (isCurrentChapter) {
                                Surface(
                                    shape = PillShape,
                                    color = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.testTag("active_chapter_indicator"),
                                ) {
                                    Text(
                                        text = "Active",
                                        style = MaterialTheme.typography.labelSmall,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
