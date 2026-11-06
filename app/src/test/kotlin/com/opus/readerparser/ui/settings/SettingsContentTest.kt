package com.opus.readerparser.ui.settings

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.navOptions
import com.opus.readerparser.domain.model.AppSettings
import com.opus.readerparser.domain.model.AppTheme
import com.opus.readerparser.domain.model.ManhwaLayout
import com.opus.readerparser.domain.model.ManhwaZoom
import com.opus.readerparser.ui.navigation.Destinations
import com.opus.readerparser.ui.navigation.OuterScaffoldWindowInsets
import com.opus.readerparser.ui.navigation.bottomNavRoutes
import com.opus.readerparser.ui.navigation.configureTopLevelNavigation
import com.opus.readerparser.ui.theme.DarkColorScheme
import com.opus.readerparser.ui.theme.LightColorScheme
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
    fun `ManhwaLayout displayName returns expected names`() {
        assertEquals("Webtoon", ManhwaLayout.WEBTOON.displayName())
        assertEquals("Paged LTR", ManhwaLayout.PAGED_LTR.displayName())
        assertEquals("Paged RTL", ManhwaLayout.PAGED_RTL.displayName())
    }

    @Test
    fun `ManhwaZoom displayName returns expected names`() {
        assertEquals("Fit Width", ManhwaZoom.FIT_WIDTH.displayName())
        assertEquals("Fit Height", ManhwaZoom.FIT_HEIGHT.displayName())
        assertEquals("Original Scale", ManhwaZoom.ORIGINAL.displayName())
    }

    @Test
    fun `colorSchemeForTheme returns expected LightColorScheme tokens`() {
        val scheme = colorSchemeForTheme(AppTheme.LIGHT)
        assertEquals(LightColorScheme, scheme)
        assertEquals(Color(0xFFFAF8F5), scheme.background)
        assertEquals(Color(0xFFFFFFFF), scheme.surface)
        assertEquals(Color(0xFFF0ECE1), scheme.surfaceContainer)
        assertEquals(Color(0xFF1C1B1A), scheme.onSurface)
        assertEquals(Color(0xFF605C56), scheme.onSurfaceVariant)
        assertEquals(Color(0xFFB86528), scheme.primary)
        assertEquals(Color(0xFF536358), scheme.secondary)
    }

    @Test
    fun `colorSchemeForTheme returns expected DarkColorScheme tokens`() {
        val scheme = colorSchemeForTheme(AppTheme.DARK)
        assertEquals(DarkColorScheme, scheme)
        assertEquals(Color(0xFF111318), scheme.background)
        assertEquals(Color(0xFF191C22), scheme.surface)
        assertEquals(Color(0xFF22262F), scheme.surfaceContainer)
        assertEquals(Color(0xFFE2E2E9), scheme.onSurface)
        assertEquals(Color(0xFF939AA6), scheme.onSurfaceVariant)
        assertEquals(Color(0xFFA2C8FF), scheme.primary)
        assertEquals(Color(0xFFBBC7DB), scheme.secondary)
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

    @Test
    fun `OuterScaffoldWindowInsets has zero insets on all sides to prevent double status bar padding`() {
        val density = Density(1f)
        assertEquals(0, OuterScaffoldWindowInsets.getTop(density))
        assertEquals(0, OuterScaffoldWindowInsets.getBottom(density))
        assertEquals(0, OuterScaffoldWindowInsets.getLeft(density, LayoutDirection.Ltr))
        assertEquals(0, OuterScaffoldWindowInsets.getRight(density, LayoutDirection.Ltr))
    }

    @Test
    fun `configureTopLevelNavigation configures popUpTo start destination with saveState singleTop and restoreState for Browse CTA`() {
        val options = navOptions {
            configureTopLevelNavigation(101)
        }
        assertEquals(101, options.popUpToId)
        assertEquals(true, options.shouldPopUpToSaveState())
        assertEquals(false, options.isPopUpToInclusive())
        assertEquals(true, options.shouldLaunchSingleTop())
        assertEquals(true, options.shouldRestoreState())
        assertEquals(true, bottomNavRoutes.contains(Destinations.BROWSE))
    }

    @Test
    fun `SettingsListContentPadding provides 24dp bottom breathing room`() {
        assertEquals(24.dp, SettingsListContentPadding.calculateBottomPadding())
        assertEquals(0.dp, SettingsListContentPadding.calculateTopPadding())
    }
}
