package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = ShieldCyanPrimary,
    onPrimary = Color(0xFF001A2C),
    primaryContainer = Color(0xFF004975),
    onPrimaryContainer = Color(0xFFC7E7FF),
    secondary = ShieldTeal,
    onSecondary = Color(0xFF00382E),
    secondaryContainer = Color(0xFF005143),
    onSecondaryContainer = Color(0xFF73F8DB),
    tertiary = AccentGold,
    background = ShieldDarkBg,
    onBackground = TextPrimaryDark,
    surface = ShieldDarkSurface,
    onSurface = TextPrimaryDark,
    surfaceVariant = ShieldDarkSurfaceVariant,
    onSurfaceVariant = TextSecondaryDark,
    error = SecurityDangerRed,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = ShieldCyanDark,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD2E4FF),
    onPrimaryContainer = Color(0xFF001C3B),
    secondary = Color(0xFF00796B),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFB2DFDB),
    onSecondaryContainer = Color(0xFF00201C),
    tertiary = Color(0xFF7B2CBF),
    background = ShieldLightBg,
    onBackground = TextPrimaryLight,
    surface = ShieldLightSurface,
    onSurface = TextPrimaryLight,
    surfaceVariant = ShieldLightSurfaceVariant,
    onSurfaceVariant = TextSecondaryLight,
    error = SecurityDangerRed,
    onError = Color.White
)

@Composable
fun ShieldAiTheme(
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
