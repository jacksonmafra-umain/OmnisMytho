package com.umain.omnismytho.presentation.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

private val DarkColorScheme = darkColorScheme(
    primary = DarkColorPrimary,
    secondary = DarkColorSecondary,
    tertiary = DarkColorAccent,
    background = DarkBgPrimary,
    surface = DarkBgSurface,
    surfaceVariant = DarkBgCard,
    onPrimary = DarkBgPrimary,
    onSecondary = DarkBgPrimary,
    onTertiary = DarkBgPrimary,
    onBackground = DarkTextPrimary,
    onSurface = DarkTextPrimary,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkBorder,
    outlineVariant = DarkBorderSubtle,
    error = DarkError,
    onError = DarkBgPrimary,
)

private val LightColorScheme = lightColorScheme(
    primary = LightColorPrimary,
    secondary = LightColorSecondary,
    tertiary = LightColorAccent,
    background = LightBgPrimary,
    surface = LightBgSurface,
    surfaceVariant = LightBgCard,
    onPrimary = LightBgPrimary,
    onSecondary = LightBgPrimary,
    onTertiary = LightBgPrimary,
    onBackground = LightTextPrimary,
    onSurface = LightTextPrimary,
    onSurfaceVariant = LightTextSecondary,
    outline = LightBorder,
    outlineVariant = LightBorderSubtle,
    error = LightError,
    onError = LightBgPrimary,
)

@Composable
fun OmnisMythoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    CompositionLocalProvider(LocalSpacing provides Spacing()) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = OmnisMythoTypography,
            shapes = OmnisMythoShapes,
            content = content,
        )
    }
}

val MaterialTheme.spacing: Spacing
    @Composable get() = LocalSpacing.current
