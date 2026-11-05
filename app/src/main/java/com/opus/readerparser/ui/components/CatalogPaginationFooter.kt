package com.opus.readerparser.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.opus.readerparser.ui.theme.AppSpacing
import com.opus.readerparser.ui.theme.PillShape
import com.opus.readerparser.ui.theme.ReaderParserTheme

@Composable
fun CatalogPaginationFooter(
    canLoadMore: Boolean,
    isLoadingMore: Boolean,
    paginationError: String? = null,
    onLoadMore: () -> Unit = {},
    onRetryLoadMore: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    if (!canLoadMore && !isLoadingMore && paginationError == null) {
        return
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = AppSpacing.md, horizontal = AppSpacing.lg)
            .testTag("catalog_pagination_footer"),
        contentAlignment = Alignment.Center,
    ) {
        if (paginationError != null) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
            ) {
                Text(
                    text = paginationError,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.testTag("pagination_error"),
                )
                OutlinedButton(
                    onClick = onRetryLoadMore,
                    shape = PillShape,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                    ),
                    modifier = Modifier.testTag("pagination_retry_button"),
                ) {
                    Text(
                        text = "Retry",
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }
        } else if (isLoadingMore) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm),
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(20.dp)
                        .testTag("pagination_loading"),
                    strokeWidth = 2.dp,
                )
                Text(
                    text = "Loading more...",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        } else if (canLoadMore) {
            OutlinedButton(
                onClick = onLoadMore,
                shape = PillShape,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                ),
                modifier = Modifier.testTag("load_more_button"),
            ) {
                Text(
                    text = "Load More",
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CatalogPaginationFooterIdlePreview() {
    ReaderParserTheme {
        CatalogPaginationFooter(
            canLoadMore = true,
            isLoadingMore = false,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CatalogPaginationFooterLoadingPreview() {
    ReaderParserTheme {
        CatalogPaginationFooter(
            canLoadMore = true,
            isLoadingMore = true,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CatalogPaginationFooterErrorPreview() {
    ReaderParserTheme {
        CatalogPaginationFooter(
            canLoadMore = true,
            isLoadingMore = false,
            paginationError = "Failed to load more series",
        )
    }
}
