package com.opus.readerparser.ui.theme

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.opus.readerparser.domain.model.AppTheme

val LightColorScheme = lightColorScheme(
    primary = PrimaryLight,
    onPrimary = OnPrimaryLight,
    primaryContainer = PrimaryContainerLight,
    onPrimaryContainer = OnPrimaryContainerLight,
    secondary = SecondaryLight,
    onSecondary = OnSecondaryLight,
    secondaryContainer = SecondaryContainerLight,
    onSecondaryContainer = OnSecondaryContainerLight,
    tertiary = TertiaryLight,
    onTertiary = OnTertiaryLight,
    background = BackgroundLight,
    onBackground = OnBackgroundLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceContainerLight,
    onSurfaceVariant = OnSurfaceVariantLight,
    surfaceContainer = SurfaceContainerLight,
    surfaceContainerHigh = SurfaceContainerHighLight,
    outline = OutlineLight,
    outlineVariant = OutlineVariantLight,
    error = ErrorLight,
    onError = OnErrorLight,
)

val DarkColorScheme = darkColorScheme(
    primary = PrimaryDark,
    onPrimary = OnPrimaryDark,
    primaryContainer = PrimaryContainerDark,
    onPrimaryContainer = OnPrimaryContainerDark,
    secondary = SecondaryDark,
    onSecondary = OnSecondaryDark,
    secondaryContainer = SecondaryContainerDark,
    onSecondaryContainer = OnSecondaryContainerDark,
    tertiary = TertiaryDark,
    onTertiary = OnTertiaryDark,
    background = BackgroundDark,
    onBackground = OnBackgroundDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceContainerDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    surfaceContainer = SurfaceContainerDark,
    surfaceContainerHigh = SurfaceContainerHighDark,
    outline = OutlineDark,
    outlineVariant = OutlineVariantDark,
    error = ErrorDark,
    onError = OnErrorDark,
)

val WarmDarkColorScheme = darkColorScheme(
    primary = WarmDarkPrimary,
    onPrimary = WarmDarkOnPrimary,
    primaryContainer = WarmDarkPrimaryContainer,
    onPrimaryContainer = WarmDarkOnPrimaryContainer,
    secondary = WarmDarkSecondary,
    onSecondary = WarmDarkOnSecondary,
    secondaryContainer = WarmDarkSecondaryContainer,
    onSecondaryContainer = WarmDarkOnSecondaryContainer,
    background = WarmDarkBackground,
    onBackground = WarmDarkOnBackground,
    surface = WarmDarkSurface,
    onSurface = WarmDarkOnSurface,
    surfaceVariant = WarmDarkSurfaceContainer,
    onSurfaceVariant = WarmDarkOnSurfaceVariant,
    surfaceContainer = WarmDarkSurfaceContainer,
    surfaceContainerHigh = WarmDarkSurfaceContainerHigh,
    outline = WarmDarkOutline,
    outlineVariant = WarmDarkOutlineVariant,
    error = WarmDarkError,
    onError = OnErrorDark,
)

val SepiaColorScheme = lightColorScheme(
    primary = SepiaPrimary,
    onPrimary = SepiaOnPrimary,
    primaryContainer = SepiaPrimaryContainer,
    onPrimaryContainer = SepiaOnPrimaryContainer,
    secondary = SepiaSecondary,
    onSecondary = SepiaOnSecondary,
    secondaryContainer = SepiaSecondaryContainer,
    onSecondaryContainer = SepiaOnSecondaryContainer,
    background = SepiaBackground,
    onBackground = SepiaOnBackground,
    surface = SepiaSurface,
    onSurface = SepiaOnSurface,
    surfaceVariant = SepiaSurfaceContainer,
    onSurfaceVariant = SepiaOnSurfaceVariant,
    surfaceContainer = SepiaSurfaceContainer,
    surfaceContainerHigh = SepiaSurfaceContainerHigh,
    outline = SepiaOutline,
    outlineVariant = SepiaOutlineVariant,
    error = SepiaError,
    onError = OnErrorLight,
)

fun colorSchemeForTheme(
    appTheme: AppTheme,
    systemInDarkTheme: Boolean = false,
): ColorScheme = when (appTheme) {
    AppTheme.WARM_DARK -> WarmDarkColorScheme
    AppTheme.SEPIA -> SepiaColorScheme
    AppTheme.DARK -> DarkColorScheme
    AppTheme.LIGHT -> LightColorScheme
    AppTheme.SYSTEM -> if (systemInDarkTheme) DarkColorScheme else LightColorScheme
}

fun isLightAppTheme(
    appTheme: AppTheme,
    systemInDarkTheme: Boolean = false,
): Boolean = when (appTheme) {
    AppTheme.LIGHT, AppTheme.SEPIA -> true
    AppTheme.DARK, AppTheme.WARM_DARK -> false
    AppTheme.SYSTEM -> !systemInDarkTheme
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

@Composable
fun ReaderParserTheme(
    appTheme: AppTheme = AppTheme.SYSTEM,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val systemInDarkTheme = isSystemInDarkTheme()
    val isLightTheme = isLightAppTheme(appTheme = appTheme, systemInDarkTheme = systemInDarkTheme)
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
                ?: view.context.findActivity()?.window
                ?: return@SideEffect
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = isLightTheme
                isAppearanceLightNavigationBars = isLightTheme
            }
        }
    }
    val useDynamicColor = dynamicColor &&
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
        appTheme != AppTheme.WARM_DARK &&
        appTheme != AppTheme.SEPIA
    val colorScheme = if (useDynamicColor) {
        val context = LocalContext.current
        val darkTheme = when (appTheme) {
            AppTheme.DARK -> true
            AppTheme.LIGHT -> false
            else -> systemInDarkTheme
        }
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    } else {
        colorSchemeForTheme(appTheme = appTheme, systemInDarkTheme = systemInDarkTheme)
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        shapes = AppShapes,
        content = content,
    )
}
