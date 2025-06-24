package io.suroi

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import platform.UIKit.UIDevice

actual typealias PlatformContext = UIDevice

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