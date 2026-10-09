package com.jac.hambrientos.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryOrange,
    secondary = AccentYellow,
    tertiary = AccentGreen,
    background = TextPrimary,
    surface = TextPrimary,
    onPrimary = WarmSurface,
    onBackground = WarmBackground,
    onSurface = WarmBackground
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryOrange,
    onPrimary = WarmSurface,
    primaryContainer = SoftOrange,
    onPrimaryContainer = PrimaryDark,
    secondary = AccentGreen,
    tertiary = AccentYellow,
    background = WarmBackground,
    onBackground = TextPrimary,
    surface = WarmSurface,
    onSurface = TextPrimary,
    onSurfaceVariant = TextSecondary,
    outline = OutlineWarm
)

@Composable
fun HambrientosTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    // Dynamic colors stay disabled by default to preserve the Bocado Sansi identity.
    val colorScheme = if (darkTheme && dynamicColor) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
