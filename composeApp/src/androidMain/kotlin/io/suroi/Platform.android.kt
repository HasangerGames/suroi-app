package io.suroi

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import org.mozilla.geckoview.GeckoResult
import org.mozilla.geckoview.GeckoRuntime
import org.mozilla.geckoview.GeckoSession
import org.mozilla.geckoview.GeckoView

actual typealias PlatformContext = Activity

@Composable
actual fun Webview(
    url: String,
    modifier: Modifier,
    script: String,
    onURLChange: (String) -> Unit
) {
    val context = LocalContext.current
    val isWearOS = context.packageManager.hasSystemFeature(PackageManager.FEATURE_WATCH)
            || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && Build.DEVICE.contains("wear"))
    val below10 = Build.VERSION.SDK_INT < Build.VERSION_CODES.R
    if (isWearOS || below10) {
        GeckoWebview(url, modifier, script, onURLChange)
    } else {
        AndroidWebview(url, modifier, script, onURLChange)
    }
}

@Composable
private fun AndroidWebview(
    url: String,
    modifier: Modifier,
    script: String,
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
                        evaluateJavascript(script, null)
                    }

                    override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                        val newURL = request.url.toString()
                        if (!newURL.startsWith("https://suroi.io")) {
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

@Composable
private fun GeckoWebview(
    url: String,
    modifier: Modifier,
    script: String,
    onURLChange: (String) -> Unit
) {
    AndroidView(
        factory = { context ->
            val geckoView = GeckoView(context)
            val runtime = GeckoRuntime.create(context)
            val session = GeckoSession()

            session.setContentDelegate(object : GeckoSession.ContentDelegate {})

            session.setProgressDelegate(object : GeckoSession.ProgressDelegate {
                override fun onPageStop(session: GeckoSession, success: Boolean) {
                    if (success) {
                        session.loadUri("javascript:(function() { $script } })();")
                    } else {
                        println("unsuccessful page stop")
                    }
                }
            })

            session.open(runtime)
            geckoView.setSession(session)

            session.navigationDelegate = object : GeckoSession.NavigationDelegate {
                override fun onNewSession(session: GeckoSession, uri: String): GeckoResult<GeckoSession?>? {
                    if (!uri.startsWith("https://suroi.io")) {
                        onURLChange(uri)
                        context.startActivity(Intent(Intent.ACTION_VIEW, android.net.Uri.parse(uri)))
                        return null
                    }
                    return null
                }
            }

            session.loadUri(url)

            geckoView.apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
        },
        modifier = modifier
    )
}