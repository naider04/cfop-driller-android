package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = CyberCyan,
    onPrimary = Color(0xFF061019),
    primaryContainer = Color(0xFF004D59),
    onPrimaryContainer = Color(0xFF9CF4FF),
    secondary = SpeedYellow,
    onSecondary = Color(0xFF241A00),
    secondaryContainer = Color(0xFF4A3800),
    onSecondaryContainer = Color(0xFFFFEE99),
    tertiary = SpeedOrange,
    background = DarkBackground,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = DarkCardBorder,
    error = SpeedRed
)

@Composable
fun CFOPDrillTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
