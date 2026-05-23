package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF8DCDFF),     // lighter blue for readability in dark mode
    onPrimary = Color(0xFF003260),
    primaryContainer = ImmersiveBlue,
    onPrimaryContainer = Color.White,
    secondary = StateInOffice,
    tertiary = StateWFH,
    background = SlateBackground,
    surface = SlateCard,
    onBackground = TextPrimaryDark,
    onSurface = TextPrimaryDark,
    outline = SlateBorder
)

private val LightColorScheme = lightColorScheme(
    primary = ImmersiveBlue,
    onPrimary = Color.White,
    primaryContainer = ImmersiveBlueLight,
    onPrimaryContainer = ImmersiveBlueDark,
    secondary = StateInOffice,
    tertiary = StateWFH,
    background = WhiteBackground,
    surface = WhiteCard,
    onBackground = TextPrimaryLight,
    onSurface = TextPrimaryLight,
    outline = WhiteBorder
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
