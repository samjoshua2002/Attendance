package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = MonoWhite,
    onPrimary = MonoBlack,
    primaryContainer = MonoGrayDark,
    onPrimaryContainer = MonoWhite,
    secondary = MonoWhite,
    surface = MonoBlack,
    background = MonoBlack,
    onSurface = MonoWhite,
    onBackground = MonoWhite,
    surfaceVariant = MonoGrayDark,
    onSurfaceVariant = Color.Gray,
    outline = MonoBorderDark
)

private val LightColorScheme = lightColorScheme(
    primary = MonoBlack,
    onPrimary = MonoWhite,
    primaryContainer = MonoGrayLight,
    onPrimaryContainer = MonoBlack,
    secondary = MonoBlack,
    surface = MonoWhite,
    background = MonoWhite,
    onSurface = MonoBlack,
    onBackground = MonoBlack,
    surfaceVariant = MonoGrayLight,
    onSurfaceVariant = Color.Gray,
    outline = MonoBorder
)

@Composable
fun SkillskapesTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
