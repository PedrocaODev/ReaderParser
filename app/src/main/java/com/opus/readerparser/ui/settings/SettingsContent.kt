package com.opus.readerparser.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.opus.readerparser.domain.model.AppSettings
import com.opus.readerparser.domain.model.AppTheme
import com.opus.readerparser.domain.model.ManhwaLayout
import com.opus.readerparser.domain.model.ManhwaZoom
import com.opus.readerparser.ui.components.AppTopBar
import com.opus.readerparser.ui.components.RadioRow
import com.opus.readerparser.ui.components.SectionDivider
import com.opus.readerparser.ui.components.SectionHeader
import com.opus.readerparser.ui.components.SegmentedOptionGroup
import com.opus.readerparser.ui.components.SliderControl
import com.opus.readerparser.ui.theme.ReaderParserTheme
import kotlin.math.roundToInt

private val novelFontFamilies = listOf("Serif", "Sans-Serif", "Monospace", "Default")

fun AppTheme.displayName(): String = when (this) {
    AppTheme.SYSTEM -> "System"
    AppTheme.LIGHT -> "Light"
    AppTheme.DARK -> "Dark"
    AppTheme.WARM_DARK -> "Warm Dark"
    AppTheme.SEPIA -> "Sepia"
}

fun ManhwaLayout.displayName(): String = when (this) {
    ManhwaLayout.WEBTOON -> "Webtoon"
    ManhwaLayout.PAGED_LTR -> "Paged LTR"
    ManhwaLayout.PAGED_RTL -> "Paged RTL"
}

fun ManhwaZoom.displayName(): String = when (this) {
    ManhwaZoom.FIT_WIDTH -> "Fit Width"
    ManhwaZoom.FIT_HEIGHT -> "Fit Height"
    ManhwaZoom.ORIGINAL -> "Original Scale"
}

internal val SettingsListContentPadding = PaddingValues(bottom = 24.dp)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsContent(
    state: SettingsUiState,
    onAction: (SettingsAction) -> Unit,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
) {
    Scaffold(
        topBar = {
            AppTopBar(
                title = "Settings",
                onBackClick = onBack,
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("settings_list"),
            contentPadding = SettingsListContentPadding,
        ) {
            // Appearance section
            item {
                SectionHeader("Appearance")
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    AppTheme.entries.forEach { theme ->
                        RadioRow(
                            label = theme.displayName(),
                            selected = state.settings.theme == theme,
                            onClick = { onAction(SettingsAction.SetTheme(theme)) },
                        )
                    }
                }
            }

            item { SectionDivider() }

            // Novel reader defaults section
            item {
                SectionHeader("Novel Reader Defaults")
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    SegmentedOptionGroup(
                        options = novelFontFamilies.map { it to it },
                        selectedValue = state.settings.novelFontFamily,
                        onSelect = { onAction(SettingsAction.SetNovelFontFamily(it)) },
                        label = "Font Family",
                    )

                    SliderControl(
                        label = "Font Size",
                        valueText = "${state.settings.novelFontSize} sp",
                        value = state.settings.novelFontSize.toFloat(),
                        onValueChange = { onAction(SettingsAction.SetNovelFontSize(it.toInt())) },
                        valueRange = 12f..32f,
                        steps = 19,
                        sliderTestTag = "font_size_slider",
                    )

                    SliderControl(
                        label = "Line Height",
                        valueText = "${String.format(java.util.Locale.US, "%.1f", state.settings.novelLineHeightMultiplier)}x",
                        value = state.settings.novelLineHeightMultiplier,
                        onValueChange = {
                            onAction(
                                SettingsAction.SetNovelLineHeightMultiplier(
                                    (it * 10f).roundToInt() / 10f
                                )
                            )
                        },
                        valueRange = 1.2f..2.0f,
                        steps = 7,
                        sliderTestTag = "line_height_slider",
                    )

                    SliderControl(
                        label = "Paragraph Spacing",
                        valueText = "${state.settings.novelParagraphSpacingDp} dp",
                        value = state.settings.novelParagraphSpacingDp.toFloat(),
                        onValueChange = {
                            onAction(SettingsAction.SetNovelParagraphSpacingDp(it.roundToInt()))
                        },
                        valueRange = 4f..28f,
                        steps = 23,
                        sliderTestTag = "paragraph_spacing_slider",
                    )

                    SliderControl(
                        label = "Horizontal Margins",
                        valueText = "${state.settings.novelHorizontalPaddingDp} dp",
                        value = state.settings.novelHorizontalPaddingDp.toFloat(),
                        onValueChange = {
                            onAction(SettingsAction.SetNovelHorizontalPaddingDp(it.roundToInt()))
                        },
                        valueRange = 8f..40f,
                        steps = 31,
                        sliderTestTag = "horizontal_padding_slider",
                    )
                }
            }

            item { SectionDivider() }

            // Manhwa reader defaults section
            item {
                SectionHeader("Manhwa Reader Defaults")
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    SegmentedOptionGroup(
                        options = ManhwaLayout.entries.map { it to it.displayName() },
                        selectedValue = state.settings.manhwaLayout,
                        onSelect = { onAction(SettingsAction.SetManhwaLayout(it)) },
                        label = "Reading Layout",
                    )

                    SegmentedOptionGroup(
                        options = ManhwaZoom.entries.map { it to it.displayName() },
                        selectedValue = state.settings.manhwaZoom,
                        onSelect = { onAction(SettingsAction.SetManhwaZoom(it)) },
                        label = "Image Scale",
                    )
                }
            }

            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsContentDefaultPreview() {
    ReaderParserTheme {
        SettingsContent(
            state = SettingsUiState(settings = AppSettings()),
            onAction = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsContentDarkThemeSelectedPreview() {
    ReaderParserTheme {
        SettingsContent(
            state = SettingsUiState(
                settings = AppSettings(
                    theme = AppTheme.DARK,
                    novelFontSize = 20,
                    novelFontFamily = "Serif",
                    manhwaLayout = ManhwaLayout.WEBTOON,
                    manhwaZoom = ManhwaZoom.FIT_HEIGHT,
                ),
            ),
            onAction = {},
        )
    }
}
