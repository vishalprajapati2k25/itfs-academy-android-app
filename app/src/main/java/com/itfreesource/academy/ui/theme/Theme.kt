package com.itfreesource.academy.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

private val DarkColorPalette = darkColors(
    primary = BrandIndigo,
    primaryVariant = BrandIndigoDark,
    secondary = AccentViolet,
    background = ObsidianDarkBg,
    surface = ObsidianCardBg,
    onPrimary = ObsidianTextPrimary,
    onSecondary = ObsidianTextPrimary,
    onBackground = ObsidianTextPrimary,
    onSurface = ObsidianTextPrimary
)

private val LightColorPalette = lightColors(
    primary = BrandIndigo,
    primaryVariant = BrandIndigoDark,
    secondary = AccentViolet,
    background = SlateLightBg,
    surface = SlateCardBg,
    onPrimary = SlateLightBg,
    onSecondary = SlateLightBg,
    onBackground = SlateTextPrimary,
    onSurface = SlateTextPrimary
)

@Composable
fun ITFSAcademyTheme(
    darkTheme: Boolean = true, // Default to sleek Obsidian Dark mode for developers
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColorPalette else LightColorPalette

    MaterialTheme(
        colors = colors,
        typography = Typography,
        content = content
    )
}
