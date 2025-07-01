package io.suroi

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreGraphics.CGRectMake
import platform.Foundation.*
import platform.UIKit.UIView
import platform.WebKit.*

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun Webview(
    url: String,
    modifier: Modifier,
    script: String,
    onURLChange: (String) -> Unit
) {
    UIKitView(
        factory = {
            val userScript = WKUserScript(
                source = script,
                injectionTime = WKUserScriptInjectionTime.WKUserScriptInjectionTimeAtDocumentEnd,
                forMainFrameOnly = true
            )
            val userContentController = WKUserContentController()
            userContentController.addUserScript(userScript = userScript)
            val configuration = WKWebViewConfiguration()
            configuration.userContentController = userContentController

            val webView = WKWebView(
                frame = CGRectMake(
                0.0, 0.0, 0.0, 0.0
                ),
                configuration = configuration
            )
            webView.loadRequest(request = NSURLRequest(uRL = NSURL(string = url)))
            webView as UIView
        },
        modifier = Modifier.fillMaxSize()
    )
}

actual fun getDeviceLanguage(): String {
    return NSLocale.currentLocale.languageCode
}