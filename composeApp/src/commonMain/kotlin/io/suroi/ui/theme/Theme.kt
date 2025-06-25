package io.suroi.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

@Composable
fun SuroiTheme(
    darkMode: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkMode) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colors,
        content = content
    )
}

private val LightColorScheme = lightColorScheme(
    primary = Orange,
    onPrimary = Light,
    primaryContainer = Light,
    onPrimaryContainer = Dark,
    surface = Light,
    onSurface = Dark,
    secondary = Blue,
    onSecondary = Light,
    tertiary = Purple,
    onTertiary = Light,
    error = Danger,
    onError = Light,
)

private val DarkColorScheme = darkColorScheme(
    primary = Orange,
    onPrimary = Dark,
    primaryContainer = DarkGray,
    onPrimaryContainer = Light,
    surface = DarkGray,
    onSurface = Light,
    secondary = Blue,
    onSecondary = Dark,
    tertiary = Purple,
    onTertiary = Dark,
    error = Dark,
    onError = Danger,
)