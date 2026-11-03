package com.opus.readerparser.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.opus.readerparser.domain.model.AppTheme

private val LightColorScheme = lightColorScheme(
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
    error = ErrorLight,
    onError = OnErrorLight,
)

private val DarkColorScheme = darkColorScheme(
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
    onBackground = WarmDarkOnSurface,
    surface = WarmDarkSurface,
    onSurface = WarmDarkOnSurface,
    surfaceVariant = WarmDarkSurfaceContainer,
    onSurfaceVariant = WarmDarkOnSurfaceVariant,
    surfaceContainer = WarmDarkSurfaceContainer,
    surfaceContainerHigh = WarmDarkSurfaceContainer,
    error = ErrorDark,
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
    onBackground = SepiaOnSurface,
    surface = SepiaSurface,
    onSurface = SepiaOnSurface,
    surfaceVariant = SepiaSurfaceContainer,
    onSurfaceVariant = SepiaOnSurfaceVariant,
    surfaceContainer = SepiaSurfaceContainer,
    surfaceContainerHigh = SepiaSurfaceContainer,
    error = ErrorLight,
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

@Composable
fun ReaderParserTheme(
    appTheme: AppTheme = AppTheme.SYSTEM,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val systemInDarkTheme = isSystemInDarkTheme()
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
        content = content,
    )
}
