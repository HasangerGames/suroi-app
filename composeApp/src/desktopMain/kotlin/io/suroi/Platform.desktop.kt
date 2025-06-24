package io.suroi

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

actual class PlatformContext

@Composable
actual fun Webview(
    url: String,
    modifier: Modifier,
    onUrlChange: (String) -> Unit
) {
}

actual fun isOnline(context: PlatformContext): Boolean {
    TODO()
}

actual fun hideSystemUI(context: PlatformContext) {
}