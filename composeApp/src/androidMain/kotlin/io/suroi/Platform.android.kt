package io.suroi

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.view.ViewGroup
import android.view.Window
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

actual typealias PlatformContext = Activity

@Composable
actual fun Webview(
    url: String,
    modifier: Modifier,
    onURLChange: (String) -> Unit
) {
    AndroidView(
        factory = { context ->
            WebView(context).apply {
                settings.domStorageEnabled = true
                settings.javaScriptEnabled = true
                settings.useWideViewPort = true
                settings.loadWithOverviewMode = true
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView, url: String) {
                        super.onPageFinished(view, url)
                        evaluateJavascript(SCRIPT, null)
                    }
                    override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                        val newURL = request.url.toString()
                        if (!newURL.contains("suroi.io")) {
                            onURLChange(newURL)
                            view.context.startActivity(Intent(Intent.ACTION_VIEW, request.url))
                            return true
                        }
                        return false
                    }
                }
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