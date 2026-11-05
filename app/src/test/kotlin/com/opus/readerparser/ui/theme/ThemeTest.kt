package com.opus.readerparser.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.opus.readerparser.domain.model.AppTheme
import org.junit.Assert.assertEquals
import org.junit.Test

class ThemeTest {

    @Test
    fun warmDarkColorSchemeMatchesSpecification() {
        val scheme = colorSchemeForTheme(AppTheme.WARM_DARK)
        assertEquals(WarmDarkColorScheme, scheme)

        assertEquals(Color(0xFF181614), scheme.background)
        assertEquals(Color(0xFF211E1B), scheme.surface)
        assertEquals(Color(0xFF2A2521), scheme.surfaceContainer)
        assertEquals(Color(0xFF352F2A), scheme.surfaceContainerHigh)
        assertEquals(Color(0xFFE6DFD5), scheme.onBackground)
        assertEquals(Color(0xFFE6DFD5), scheme.onSurface)
        assertEquals(Color(0xFFA89F93), scheme.onSurfaceVariant)
        assertEquals(Color(0xFF4D453E), scheme.outline)
        assertEquals(Color(0xFF3A342E), scheme.outlineVariant)
        assertEquals(Color(0xFFD98A4E), scheme.primary)
        assertEquals(Color(0xFF2B1708), scheme.onPrimary)
        assertEquals(Color(0xFF4A2E16), scheme.primaryContainer)
        assertEquals(Color(0xFFFFDCC4), scheme.onPrimaryContainer)
        assertEquals(Color(0xFF7A9A8B), scheme.secondary)
        assertEquals(Color(0xFF12261D), scheme.onSecondary)
        assertEquals(Color(0xFF263D33), scheme.secondaryContainer)
        assertEquals(Color(0xFFBCE0CF), scheme.onSecondaryContainer)
        assertEquals(Color(0xFFE06C75), scheme.error)
    }

    @Test
    fun sepiaColorSchemeMatchesSpecification() {
        val scheme = colorSchemeForTheme(AppTheme.SEPIA)
        assertEquals(SepiaColorScheme, scheme)

        assertEquals(Color(0xFFF4ECD8), scheme.background)
        assertEquals(Color(0xFFEFE4C8), scheme.surface)
        assertEquals(Color(0xFFE4D5B7), scheme.surfaceContainer)
        assertEquals(Color(0xFFDAC7A4), scheme.surfaceContainerHigh)
        assertEquals(Color(0xFF2C221E), scheme.onBackground)
        assertEquals(Color(0xFF2C221E), scheme.onSurface)
        assertEquals(Color(0xFF5C4B3B), scheme.onSurfaceVariant)
        assertEquals(Color(0xFFBCAB94), scheme.outline)
        assertEquals(Color(0xFFD1C2AC), scheme.outlineVariant)
        assertEquals(Color(0xFF8C5327), scheme.primary)
        assertEquals(Color(0xFFFFFFFF), scheme.onPrimary)
        assertEquals(Color(0xFFF5D9C1), scheme.primaryContainer)
        assertEquals(Color(0xFF361B06), scheme.onPrimaryContainer)
        assertEquals(Color(0xFF4F6D5A), scheme.secondary)
        assertEquals(Color(0xFFFFFFFF), scheme.onSecondary)
        assertEquals(Color(0xFFCFE4D6), scheme.secondaryContainer)
        assertEquals(Color(0xFF14291C), scheme.onSecondaryContainer)
        assertEquals(Color(0xFFBA1A1A), scheme.error)
    }

    @Test
    fun lightColorSchemeMatchesSpecification() {
        val scheme = colorSchemeForTheme(AppTheme.LIGHT)
        assertEquals(LightColorScheme, scheme)

        assertEquals(Color(0xFFFAF8F5), scheme.background)
        assertEquals(Color(0xFFFFFFFF), scheme.surface)
        assertEquals(Color(0xFFF0ECE1), scheme.surfaceContainer)
        assertEquals(Color(0xFFE6E1D4), scheme.surfaceContainerHigh)
        assertEquals(Color(0xFF1C1B1A), scheme.onBackground)
        assertEquals(Color(0xFF1C1B1A), scheme.onSurface)
        assertEquals(Color(0xFF605C56), scheme.onSurfaceVariant)
        assertEquals(Color(0xFFC8C2B8), scheme.outline)
        assertEquals(Color(0xFFDDD8CE), scheme.outlineVariant)
        assertEquals(Color(0xFFB86528), scheme.primary)
        assertEquals(Color(0xFFFFFFFF), scheme.onPrimary)
        assertEquals(Color(0xFFFDE8D7), scheme.primaryContainer)
        assertEquals(Color(0xFF3D1C05), scheme.onPrimaryContainer)
        assertEquals(Color(0xFF536358), scheme.secondary)
        assertEquals(Color(0xFFFFFFFF), scheme.onSecondary)
        assertEquals(Color(0xFFD6E0D9), scheme.secondaryContainer)
        assertEquals(Color(0xFF101E15), scheme.onSecondaryContainer)
        assertEquals(Color(0xFFBA1A1A), scheme.error)
    }

    @Test
    fun darkColorSchemeMatchesSpecification() {
        val scheme = colorSchemeForTheme(AppTheme.DARK)
        assertEquals(DarkColorScheme, scheme)

        assertEquals(Color(0xFF111318), scheme.background)
        assertEquals(Color(0xFF191C22), scheme.surface)
        assertEquals(Color(0xFF22262F), scheme.surfaceContainer)
        assertEquals(Color(0xFF2C313C), scheme.surfaceContainerHigh)
        assertEquals(Color(0xFFE2E2E9), scheme.onBackground)
        assertEquals(Color(0xFFE2E2E9), scheme.onSurface)
        assertEquals(Color(0xFF939AA6), scheme.onSurfaceVariant)
        assertEquals(Color(0xFF444A57), scheme.outline)
        assertEquals(Color(0xFF323741), scheme.outlineVariant)
        assertEquals(Color(0xFFA2C8FF), scheme.primary)
        assertEquals(Color(0xFF00325B), scheme.onPrimary)
        assertEquals(Color(0xFF004880), scheme.primaryContainer)
        assertEquals(Color(0xFFD3E4FF), scheme.onPrimaryContainer)
        assertEquals(Color(0xFFBBC7DB), scheme.secondary)
        assertEquals(Color(0xFF253140), scheme.onSecondary)
        assertEquals(Color(0xFF3B4858), scheme.secondaryContainer)
        assertEquals(Color(0xFFD7E3F7), scheme.onSecondaryContainer)
        assertEquals(Color(0xFFE06C75), scheme.error)
    }

    @Test
    fun systemThemeResolvesCorrectly() {
        assertEquals(LightColorScheme, colorSchemeForTheme(AppTheme.SYSTEM, systemInDarkTheme = false))
        assertEquals(DarkColorScheme, colorSchemeForTheme(AppTheme.SYSTEM, systemInDarkTheme = true))
    }

    @Test
    fun successColorMatchesSpecification() {
        assertEquals(Color(0xFF6CB687), SuccessColor)
    }

    @Test
    fun shapesMatchSpecification() {
        assertEquals(RoundedCornerShape(6.dp), AppShapes.extraSmall)
        assertEquals(RoundedCornerShape(6.dp), AppShapes.small)
        assertEquals(RoundedCornerShape(10.dp), AppShapes.medium)
        assertEquals(RoundedCornerShape(16.dp), AppShapes.large)
        assertEquals(RoundedCornerShape(24.dp), AppShapes.extraLarge)
        assertEquals(RoundedCornerShape(percent = 50), PillShape)
    }

    @Test
    fun spacingMatchesSpecification() {
        assertEquals(4.dp, AppSpacing.xs)
        assertEquals(8.dp, AppSpacing.sm)
        assertEquals(12.dp, AppSpacing.md)
        assertEquals(16.dp, AppSpacing.lg)
        assertEquals(20.dp, AppSpacing.xl)
        assertEquals(24.dp, AppSpacing.xxl)
        assertEquals(2f / 3f, AppSpacing.CoverAspectRatio, 0.0001f)
        assertEquals(56.dp, AppSpacing.TopBarHeight)
        assertEquals(64.dp, AppSpacing.BottomNavBarHeight)
        assertEquals(150.dp, AppSpacing.ManhwaMinPlaceholderHeight)

        assertEquals(2f / 3f, CoverAspectRatio, 0.0001f)
        assertEquals(56.dp, TopBarHeight)
        assertEquals(64.dp, BottomNavBarHeight)
        assertEquals(150.dp, ManhwaMinPlaceholderHeight)
    }

    @Test
    fun typographyScaleRangesMatchSpecification() {
        // Hero / Display (18–20sp bold)
        assertEquals(20.sp, AppTypography.headlineLarge.fontSize)
        assertEquals(FontWeight.Bold, AppTypography.headlineLarge.fontWeight)

        assertEquals(19.sp, AppTypography.headlineMedium.fontSize)
        assertEquals(FontWeight.Bold, AppTypography.headlineMedium.fontWeight)

        assertEquals(18.sp, AppTypography.headlineSmall.fontSize)
        assertEquals(FontWeight.Bold, AppTypography.headlineSmall.fontWeight)

        assertEquals(18.sp, AppTypography.titleLarge.fontSize)
        assertEquals(FontWeight.Bold, AppTypography.titleLarge.fontWeight)

        // Section / Card Title (14–16sp bold)
        assertEquals(16.sp, AppTypography.titleMedium.fontSize)
        assertEquals(FontWeight.Bold, AppTypography.titleMedium.fontWeight)

        assertEquals(14.sp, AppTypography.titleSmall.fontSize)
        assertEquals(FontWeight.Bold, AppTypography.titleSmall.fontWeight)

        // Body (13–14sp normal)
        assertEquals(14.sp, AppTypography.bodyLarge.fontSize)
        assertEquals(FontWeight.Normal, AppTypography.bodyLarge.fontWeight)

        assertEquals(13.sp, AppTypography.bodyMedium.fontSize)
        assertEquals(FontWeight.Normal, AppTypography.bodyMedium.fontWeight)

        assertEquals(12.sp, AppTypography.bodySmall.fontSize)
        assertEquals(FontWeight.Normal, AppTypography.bodySmall.fontWeight)

        // Metadata / Badge Label (11–12sp semi-bold / bold)
        assertEquals(12.sp, AppTypography.labelLarge.fontSize)
        assertEquals(FontWeight.SemiBold, AppTypography.labelLarge.fontWeight)

        assertEquals(11.sp, AppTypography.labelMedium.fontSize)
        assertEquals(FontWeight.SemiBold, AppTypography.labelMedium.fontWeight)

        assertEquals(11.sp, AppTypography.labelSmall.fontSize)
        assertEquals(FontWeight.Bold, AppTypography.labelSmall.fontWeight)
    }

    @Test
    fun novelReaderBodyStyleMatchesSpecification() {
        assertEquals(FontFamily.Serif, NovelReaderBodyStyle.fontFamily)
        assertEquals(FontWeight.Normal, NovelReaderBodyStyle.fontWeight)
        assertEquals(18.sp, NovelReaderBodyStyle.fontSize)
        assertEquals(28.8.sp, NovelReaderBodyStyle.lineHeight)
        assertEquals(0.15.sp, NovelReaderBodyStyle.letterSpacing)
    }
}
