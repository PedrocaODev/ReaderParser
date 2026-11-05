package com.opus.readerparser.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.opus.readerparser.domain.model.ContentType
import com.opus.readerparser.domain.model.Series
import com.opus.readerparser.ui.theme.CoverAspectRatio
import com.opus.readerparser.ui.theme.ReaderParserTheme

internal val SeriesCatalogGridContentPadding = PaddingValues(8.dp)
internal val SeriesCatalogGridItemSpacing = 8.dp
internal val SeriesCatalogGridMinSize = 150.dp
internal val SeriesCardAspectRatio = CoverAspectRatio
internal val SeriesCardTitlePadding = 8.dp

fun ContentType.displayLabel(): String = when (this) {
    ContentType.NOVEL -> "Novel"
    ContentType.MANHWA -> "Manhwa"
}

@Composable
fun ContentTypeBadge(type: ContentType, modifier: Modifier = Modifier) {
    Surface(
        shape = MaterialTheme.shapes.extraSmall,
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        contentColor = MaterialTheme.colorScheme.onSurface,
        modifier = modifier.testTag("content_type_badge"),
    ) {
        Text(
            text = type.name,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
        )
    }
}

@Composable
fun InLibraryBadge(modifier: Modifier = Modifier, label: String = "In Library") {
    Surface(
        shape = MaterialTheme.shapes.extraSmall,
        color = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        modifier = modifier.testTag("in_library_badge"),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
        )
    }
}

@Composable
fun StatusBadge(label: String, modifier: Modifier = Modifier) {
    InLibraryBadge(modifier = modifier, label = label)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SeriesCard(
    series: Series,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onLongClick: (() -> Unit)? = null,
    inLibrary: Boolean = false,
) {
    Card(
        modifier = modifier
            .testTag("series_card")
            .combinedClickable(onClick = onClick, onLongClick = onLongClick),
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(SeriesCardAspectRatio)
                    .clip(MaterialTheme.shapes.medium),
            ) {
                AsyncImage(
                    model = series.coverUrl,
                    contentDescription = series.title,
                    contentScale = ContentScale.Crop,
                    placeholder = ColorPainter(MaterialTheme.colorScheme.surfaceVariant),
                    error = ColorPainter(MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.fillMaxSize(),
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.35f),
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.45f),
                                ),
                            ),
                        ),
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(6.dp),
                ) {
                    if (inLibrary) {
                        StatusBadge(label = "In Library")
                    } else {
                        ContentTypeBadge(type = series.type)
                    }
                }
            }
            Column(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = series.title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 17.sp,
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                val subtitle = series.author?.takeIf { it.isNotBlank() } ?: series.type.displayLabel()
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelMedium.copy(fontSize = 11.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.testTag("series_card_subtitle"),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SeriesCardPreview() {
    ReaderParserTheme {
        SeriesCard(
            series = Series(
                sourceId = 1L,
                url = "https://example.com/series",
                title = "Solo Leveling",
                author = "Chugong",
                coverUrl = null,
                type = ContentType.MANHWA,
            ),
            onClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SeriesCardInLibraryPreview() {
    ReaderParserTheme {
        SeriesCard(
            series = Series(
                sourceId = 1L,
                url = "https://example.com/novel",
                title = "Shadow Slave",
                author = "Guiltythree",
                coverUrl = null,
                type = ContentType.NOVEL,
            ),
            onClick = {},
            inLibrary = true,
        )
    }
}
