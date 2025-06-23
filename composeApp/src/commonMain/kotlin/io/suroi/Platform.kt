package io.suroi

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier

expect class PlatformContext

val LocalPlatformContext = staticCompositionLocalOf<PlatformContext> {
    error("No PlatformContext provided")
}
interface Platform {
    val name: String
    fun configureWebView(webView: Any)
}

expect fun getPlatform(): Platform

@Composable
expect fun Webview(
    url: String,
    modifier: Modifier,
    onUrlChange: (String) -> Unit
)

// expect fun isOnline(context: PlatformContext): Boolean todo

expect fun hideSystemUI(context: PlatformContext)