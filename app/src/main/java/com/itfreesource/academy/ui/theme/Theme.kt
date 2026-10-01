package com.itfreesource.academy.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

private val DarkColorPalette = darkColors(
    primary = DuoGreen,
    primaryVariant = DuoGreenDark,
    secondary = DuoBlue,
    background = DuoDarkBackground,
    surface = DuoDarkSurface,
    onPrimary = DuoWhite,
    onSecondary = DuoWhite,
    onBackground = DuoDarkTextPrimary,
    onSurface = DuoDarkTextPrimary
)

private val LightColorPalette = lightColors(
    primary = DuoGreen,
    primaryVariant = DuoGreenDark,
    secondary = DuoBlue,
    background = DuoGrayBackground,
    surface = DuoWhite,
    onPrimary = DuoWhite,
    onSecondary = DuoWhite,
    onBackground = DuoDarkText,
    onSurface = DuoDarkText
)

@Composable
fun ITFSAcademyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColorPalette else LightColorPalette

    MaterialTheme(
        colors = colors,
        typography = Typography,
        content = content
    )
}
