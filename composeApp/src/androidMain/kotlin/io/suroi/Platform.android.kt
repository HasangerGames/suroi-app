package io.suroi

import android.annotation.SuppressLint
import android.os.Build
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView


class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
    override fun configureWebView(webView: Any) {
        if (webView is WebView) {
            webView.apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                WebView.setWebContentsDebuggingEnabled(true)
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )

                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView, url: String) {
                        super.onPageFinished(view, url)
                        view.evaluateJavascript(
                            """
                            document.querySelectorAll('.btn-kofi').forEach(function(element) {
                                element.style.display = 'none';
                            });
                            """,
                            null
                        )
                    }
                }
            }
        }
    }
}

actual fun getPlatform(): Platform = AndroidPlatform()

@SuppressLint("SetJavaScriptEnabled")
@Composable
actual fun Webview(
    url: String,
    modifier: Modifier,
    onUrlChange: (String) -> Unit
) {
    AndroidView(
        factory = { context ->
            WebView(context).apply {
                // Delegate configuration to the platform-specific method
                getPlatform().configureWebView(this)
                loadUrl(url)
            }
        },
        modifier = modifier
    )
}