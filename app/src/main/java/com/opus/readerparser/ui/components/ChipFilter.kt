package com.opus.readerparser.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.opus.readerparser.ui.theme.AppSpacing
import com.opus.readerparser.ui.theme.PillShape

data class FilterChipOption<T>(
    val value: T,
    val label: String,
    val count: Int? = null,
    val testTag: String? = null,
)

@Composable
fun ChipFilter(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    count: Int? = null,
    testTag: String? = null,
) {
    val displayText = if (count != null) "$label ($count)" else label
    val chipModifier = if (testTag != null) modifier.testTag(testTag) else modifier

    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(displayText) },
        shape = PillShape,
        modifier = chipModifier,
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
        ),
    )
}

@Composable
fun <T> ChipFilterRow(
    options: List<FilterChipOption<T>>,
    selectedValue: T,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm),
    ) {
        items(options) { option ->
            ChipFilter(
                label = option.label,
                selected = option.value == selectedValue,
                onClick = { onSelect(option.value) },
                count = option.count,
                testTag = option.testTag,
            )
        }
    }
}

