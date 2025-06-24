package io.suroi

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import platform.UIKit.UIDevice
import platform.WebKit.WKWebView

actual typealias PlatformContext = UIDevice
class IOSPlatform: Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
    override fun configureWebView(webView: Any) {
        TODO()
    }
}

actual fun getPlatform(): Platform = IOSPlatform()

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