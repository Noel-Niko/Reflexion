package com.livingtechusa.reflexion.ui.theme.selectableColorSchemes


import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable


private val LightColors = lightColorScheme(
    primary = md_theme_2_light_primary,
    onPrimary = md_theme_2_light_onPrimary,
    primaryContainer = md_theme_2_light_primaryContainer,
    onPrimaryContainer = md_theme_2_light_onPrimaryContainer,
    secondary = md_theme_2_light_secondary,
    onSecondary = md_theme_2_light_onSecondary,
    secondaryContainer = md_theme_2_light_secondaryContainer,
    onSecondaryContainer = md_theme_2_light_onSecondaryContainer,
    tertiary = md_theme_2_light_tertiary,
    onTertiary = md_theme_2_light_onTertiary,
    tertiaryContainer = md_theme_2_light_tertiaryContainer,
    onTertiaryContainer = md_theme_2_light_onTertiaryContainer,
    error = md_theme_2_light_error,
    errorContainer = md_theme_2_light_errorContainer,
    onError = md_theme_2_light_onError,
    onErrorContainer = md_theme_2_light_onErrorContainer,
    background = md_theme_2_light_background,
    onBackground = md_theme_2_light_onBackground,
    surface = md_theme_2_light_surface,
    onSurface = md_theme_2_light_onSurface,
    surfaceVariant = md_theme_2_light_surfaceVariant,
    onSurfaceVariant = md_theme_2_light_onSurfaceVariant,
    outline = md_theme_2_light_outline,
    inverseOnSurface = md_theme_2_light_inverseOnSurface,
    inverseSurface = md_theme_2_light_inverseSurface,
    inversePrimary = md_theme_2_light_inversePrimary,
    surfaceTint = md_theme_2_light_surfaceTint,
    outlineVariant = md_theme_2_light_outlineVariant,
    scrim = md_theme_2_light_scrim,
)


private val DarkColors = darkColorScheme(
    primary = md_theme_2_dark_primary,
    onPrimary = md_theme_2_dark_onPrimary,
    primaryContainer = md_theme_2_dark_primaryContainer,
    onPrimaryContainer = md_theme_2_dark_onPrimaryContainer,
    secondary = md_theme_2_dark_secondary,
    onSecondary = md_theme_2_dark_onSecondary,
    secondaryContainer = md_theme_2_dark_secondaryContainer,
    onSecondaryContainer = md_theme_2_dark_onSecondaryContainer,
    tertiary = md_theme_2_dark_tertiary,
    onTertiary = md_theme_2_dark_onTertiary,
    tertiaryContainer = md_theme_2_dark_tertiaryContainer,
    onTertiaryContainer = md_theme_2_dark_onTertiaryContainer,
    error = md_theme_2_dark_error,
    errorContainer = md_theme_2_dark_errorContainer,
    onError = md_theme_2_dark_onError,
    onErrorContainer = md_theme_2_dark_onErrorContainer,
    background = md_theme_2_dark_background,
    onBackground = md_theme_2_dark_onBackground,
    surface = md_theme_2_dark_surface,
    onSurface = md_theme_2_dark_onSurface,
    surfaceVariant = md_theme_2_dark_surfaceVariant,
    onSurfaceVariant = md_theme_2_dark_onSurfaceVariant,
    outline = md_theme_2_dark_outline,
    inverseOnSurface = md_theme_2_dark_inverseOnSurface,
    inverseSurface = md_theme_2_dark_inverseSurface,
    inversePrimary = md_theme_2_dark_inversePrimary,
    surfaceTint = md_theme_2_dark_surfaceTint,
    outlineVariant = md_theme_2_dark_outlineVariant,
    scrim = md_theme_2_dark_scrim,
)

@Composable
fun CustomTheme2(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable() () -> Unit
) {
    val colors = if (!useDarkTheme) {
        LightColors
    } else {
        DarkColors
    }

    MaterialTheme(
        colorScheme = colors,
        content = content
    )
}