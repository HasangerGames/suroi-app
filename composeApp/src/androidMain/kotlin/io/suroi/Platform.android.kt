package io.suroi

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.view.ViewGroup
import android.view.Window
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

actual typealias PlatformContext = Activity
class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
    @SuppressLint("SetJavaScriptEnabled")
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
                getPlatform().configureWebView(this)
                loadUrl(url)
            }
        },
        modifier = modifier
    )
}

// todo this returns a snapshot which returns false at startup time
actual fun isOnline(context: PlatformContext): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val capabilities =
        connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)

    return capabilities?.hasTransport(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    ?: false
}

actual fun hideSystemUI(context: PlatformContext) {
    val window: Window = context.window
    val systemUI = WindowCompat.getInsetsController(window, window.decorView)
    systemUI.hide(WindowInsetsCompat.Type.systemBars())
    systemUI.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
}