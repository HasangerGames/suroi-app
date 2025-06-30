package io.suroi

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitView
import platform.Foundation.NSURL
import platform.Foundation.NSURLRequest
import platform.UIKit.UIDevice
import platform.UIKit.UIView
import platform.WebKit.WKWebView

actual typealias PlatformContext = UIDevice

@Composable
actual fun Webview(
    url: String,
    modifier: Modifier,
    script: String,
    onURLChange: (String) -> Unit
) {
    UIKitView(
        factory = {
            val webView = WKWebView()
            webView.loadRequest(request = NSURLRequest(uRL = NSURL(string = url)))
            webView as UIView
        },
        modifier = Modifier.fillMaxSize()
    )
}