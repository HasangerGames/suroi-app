package io.suroi

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
actual fun Webview(
    url: String,
    modifier: Modifier,
    onUrlChange: (String) -> Unit
) {
}