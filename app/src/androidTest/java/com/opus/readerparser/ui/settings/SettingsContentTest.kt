package com.opus.readerparser.ui.settings

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.opus.readerparser.domain.model.AppSettings
import com.opus.readerparser.domain.model.AppTheme
import com.opus.readerparser.domain.model.ManhwaLayout
import com.opus.readerparser.domain.model.ManhwaZoom
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SettingsContentTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun defaultSettings_settingsListIsVisible() {
        composeRule.setContent {
            SettingsContent(state = SettingsUiState(settings = AppSettings()), onAction = {})
        }
        composeRule.onNodeWithTag("settings_list").assertIsDisplayed()
    }

    @Test
    fun themeOptions_areAllVisible() {
        composeRule.setContent {
            SettingsContent(state = SettingsUiState(settings = AppSettings()), onAction = {})
        }
        composeRule.onNodeWithText("System").assertIsDisplayed()
        composeRule.onNodeWithText("Light").assertIsDisplayed()
        composeRule.onNodeWithText("Dark").assertIsDisplayed()
        composeRule.onNodeWithText("Warm Dark").assertIsDisplayed()
        composeRule.onNodeWithText("Sepia").assertIsDisplayed()

        composeRule.onNodeWithText("Appearance").assertIsDisplayed()
        composeRule.onNodeWithText("Novel Reader Defaults").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithText("Manhwa Reader Defaults").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun selectDarkTheme_dispatchesSetThemeAction() {
        var selectedTheme: AppTheme? = null
        composeRule.setContent {
            SettingsContent(
                state = SettingsUiState(settings = AppSettings(theme = AppTheme.SYSTEM)),
                onAction = { action ->
                    if (action is SettingsAction.SetTheme) selectedTheme = action.theme
                },
            )
        }
        composeRule.onNodeWithText("Dark").performClick()
        assertEquals(AppTheme.DARK, selectedTheme)
    }

    @Test
    fun fontSizeSlider_isVisible() {
        composeRule.setContent {
            SettingsContent(state = SettingsUiState(settings = AppSettings()), onAction = {})
        }
        composeRule.onNodeWithTag("font_size_slider").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun readerSliders_areVisible() {
        composeRule.setContent {
            SettingsContent(state = SettingsUiState(settings = AppSettings()), onAction = {})
        }
        composeRule.onNodeWithTag("line_height_slider").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithTag("paragraph_spacing_slider").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithTag("horizontal_padding_slider").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun selectFontFamily_dispatchesSetNovelFontFamilyAction() {
        var selectedFontFamily: String? = null
        composeRule.setContent {
            SettingsContent(
                state = SettingsUiState(settings = AppSettings(novelFontFamily = "Serif")),
                onAction = { action ->
                    if (action is SettingsAction.SetNovelFontFamily) selectedFontFamily = action.family
                },
            )
        }
        composeRule.onNodeWithText("Monospace").performScrollTo().performClick()
        assertEquals("Monospace", selectedFontFamily)
    }

    @Test
    fun selectManhwaLayout_dispatchesSetManhwaLayoutAction() {
        var selectedLayout: ManhwaLayout? = null
        composeRule.setContent {
            SettingsContent(
                state = SettingsUiState(settings = AppSettings(manhwaLayout = ManhwaLayout.WEBTOON)),
                onAction = { action ->
                    if (action is SettingsAction.SetManhwaLayout) selectedLayout = action.layout
                },
            )
        }
        composeRule.onNodeWithText("Paged LTR").performScrollTo().performClick()
        assertEquals(ManhwaLayout.PAGED_LTR, selectedLayout)
    }

    @Test
    fun selectManhwaZoom_dispatchesSetManhwaZoomAction() {
        var selectedZoom: ManhwaZoom? = null
        composeRule.setContent {
            SettingsContent(
                state = SettingsUiState(settings = AppSettings(manhwaZoom = ManhwaZoom.FIT_WIDTH)),
                onAction = { action ->
                    if (action is SettingsAction.SetManhwaZoom) selectedZoom = action.zoom
                },
            )
        }
        composeRule.onNodeWithText("Fit Height").performScrollTo().performClick()
        assertEquals(ManhwaZoom.FIT_HEIGHT, selectedZoom)
    }
}
