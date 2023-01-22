package com.livingtechusa.reflexion.ui.theme.selectableColorSchemes


import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable


private val LightColors = lightColorScheme(
    primary = md_theme_6_light_primary,
    onPrimary = md_theme_6_light_onPrimary,
    primaryContainer = md_theme_6_light_primaryContainer,
    onPrimaryContainer = md_theme_6_light_onPrimaryContainer,
    secondary = md_theme_6_light_secondary,
    onSecondary = md_theme_6_light_onSecondary,
    secondaryContainer = md_theme_6_light_secondaryContainer,
    onSecondaryContainer = md_theme_6_light_onSecondaryContainer,
    tertiary = md_theme_6_light_tertiary,
    onTertiary = md_theme_6_light_onTertiary,
    tertiaryContainer = md_theme_6_light_tertiaryContainer,
    onTertiaryContainer = md_theme_6_light_onTertiaryContainer,
    error = md_theme_6_light_error,
    errorContainer = md_theme_6_light_errorContainer,
    onError = md_theme_6_light_onError,
    onErrorContainer = md_theme_6_light_onErrorContainer,
    background = md_theme_6_light_background,
    onBackground = md_theme_6_light_onBackground,
    surface = md_theme_6_light_surface,
    onSurface = md_theme_6_light_onSurface,
    surfaceVariant = md_theme_6_light_surfaceVariant,
    onSurfaceVariant = md_theme_6_light_onSurfaceVariant,
    outline = md_theme_6_light_outline,
    inverseOnSurface = md_theme_6_light_inverseOnSurface,
    inverseSurface = md_theme_6_light_inverseSurface,
    inversePrimary = md_theme_6_light_inversePrimary,
    surfaceTint = md_theme_6_light_surfaceTint,
    outlineVariant = md_theme_6_light_outlineVariant,
    scrim = md_theme_6_light_scrim,
)


private val DarkColors = darkColorScheme(
    primary = md_theme_6_dark_primary,
    onPrimary = md_theme_6_dark_onPrimary,
    primaryContainer = md_theme_6_dark_primaryContainer,
    onPrimaryContainer = md_theme_6_dark_onPrimaryContainer,
    secondary = md_theme_6_dark_secondary,
    onSecondary = md_theme_6_dark_onSecondary,
    secondaryContainer = md_theme_6_dark_secondaryContainer,
    onSecondaryContainer = md_theme_6_dark_onSecondaryContainer,
    tertiary = md_theme_6_dark_tertiary,
    onTertiary = md_theme_6_dark_onTertiary,
    tertiaryContainer = md_theme_6_dark_tertiaryContainer,
    onTertiaryContainer = md_theme_6_dark_onTertiaryContainer,
    error = md_theme_6_dark_error,
    errorContainer = md_theme_6_dark_errorContainer,
    onError = md_theme_6_dark_onError,
    onErrorContainer = md_theme_6_dark_onErrorContainer,
    background = md_theme_6_dark_background,
    onBackground = md_theme_6_dark_onBackground,
    surface = md_theme_6_dark_surface,
    onSurface = md_theme_6_dark_onSurface,
    surfaceVariant = md_theme_6_dark_surfaceVariant,
    onSurfaceVariant = md_theme_6_dark_onSurfaceVariant,
    outline = md_theme_6_dark_outline,
    inverseOnSurface = md_theme_6_dark_inverseOnSurface,
    inverseSurface = md_theme_6_dark_inverseSurface,
    inversePrimary = md_theme_6_dark_inversePrimary,
    surfaceTint = md_theme_6_dark_surfaceTint,
    outlineVariant = md_theme_6_dark_outlineVariant,
    scrim = md_theme_6_dark_scrim,
)

@Composable
fun CustomTheme6(
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