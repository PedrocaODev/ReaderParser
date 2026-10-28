package com.opus.readerparser.ui.settings

import androidx.compose.ui.graphics.Color
import com.opus.readerparser.domain.model.AppSettings
import com.opus.readerparser.domain.model.AppTheme
import com.opus.readerparser.ui.theme.SepiaColorScheme
import com.opus.readerparser.ui.theme.WarmDarkColorScheme
import com.opus.readerparser.ui.theme.colorSchemeForTheme
import org.junit.Assert.assertEquals
import org.junit.Test

class SettingsContentTest {

    @Test
    fun `AppSettings typography defaults and ranges match spec`() {
        val settings = AppSettings()
        assertEquals(18, settings.novelFontSize)
        assertEquals("Serif", settings.novelFontFamily)
        assertEquals(1.6f, settings.novelLineHeightMultiplier)
        assertEquals(14, settings.novelParagraphSpacingDp)
        assertEquals(20, settings.novelHorizontalPaddingDp)

        assertEquals(12..32, AppSettings.NOVEL_FONT_SIZE_RANGE)
        assertEquals(1.2f..2.0f, AppSettings.NOVEL_LINE_HEIGHT_RANGE)
        assertEquals(4..28, AppSettings.NOVEL_PARAGRAPH_SPACING_RANGE)
        assertEquals(8..40, AppSettings.NOVEL_HORIZONTAL_PADDING_RANGE)
    }

    @Test
    fun `AppTheme entries contains all themes in order`() {
        assertEquals(
            listOf(
                AppTheme.SYSTEM,
                AppTheme.LIGHT,
                AppTheme.DARK,
                AppTheme.WARM_DARK,
                AppTheme.SEPIA,
            ),
            AppTheme.entries,
        )
    }

    @Test
    fun `AppTheme displayName returns expected names`() {
        assertEquals("System", AppTheme.SYSTEM.displayName())
        assertEquals("Light", AppTheme.LIGHT.displayName())
        assertEquals("Dark", AppTheme.DARK.displayName())
        assertEquals("Warm Dark", AppTheme.WARM_DARK.displayName())
        assertEquals("Sepia", AppTheme.SEPIA.displayName())
    }

    @Test
    fun `colorSchemeForTheme returns expected WarmDarkColorScheme tokens`() {
        val scheme = colorSchemeForTheme(AppTheme.WARM_DARK)
        assertEquals(WarmDarkColorScheme, scheme)
        assertEquals(Color(0xFF181614), scheme.background)
        assertEquals(Color(0xFF211E1B), scheme.surface)
        assertEquals(Color(0xFF2A2521), scheme.surfaceContainer)
        assertEquals(Color(0xFFE6DFD5), scheme.onSurface)
        assertEquals(Color(0xFFA89F93), scheme.onSurfaceVariant)
        assertEquals(Color(0xFFD98A4E), scheme.primary)
        assertEquals(Color(0xFF7A9A8B), scheme.secondary)
    }

    @Test
    fun `colorSchemeForTheme returns expected SepiaColorScheme tokens`() {
        val scheme = colorSchemeForTheme(AppTheme.SEPIA)
        assertEquals(SepiaColorScheme, scheme)
        assertEquals(Color(0xFFF4ECD8), scheme.background)
        assertEquals(Color(0xFFEFE4C8), scheme.surface)
        assertEquals(Color(0xFFE4D5B7), scheme.surfaceContainer)
        assertEquals(Color(0xFF2C221E), scheme.onSurface)
        assertEquals(Color(0xFF5C4B3B), scheme.onSurfaceVariant)
        assertEquals(Color(0xFF8C5327), scheme.primary)
        assertEquals(Color(0xFF4F6D5A), scheme.secondary)
    }
}
