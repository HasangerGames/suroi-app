package io.suroi.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

@Composable
fun SuroiTheme(
    content: @Composable () -> Unit
) {
    val colors = DarkColorScheme
    MaterialTheme(
        colorScheme = colors,
        content = content
    )
}


private val DarkColorScheme = darkColorScheme(
    primary = Orange,
    onPrimary = Dark,
    primaryContainer = Gray,
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