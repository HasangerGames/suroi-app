package io.suroi

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier

expect class PlatformContext

val LocalPlatformContext = staticCompositionLocalOf<PlatformContext> {
    error("No PlatformContext provided")
}

const val SCRIPT = "document.querySelector('.btn-kofi').style.display = 'none';"
@Composable
expect fun Webview(
    url: String,
    modifier: Modifier,
    onUrlChange: (String) -> Unit
)

expect fun isOnline(context: PlatformContext): Boolean

expect fun hideSystemUI(context: PlatformContext)