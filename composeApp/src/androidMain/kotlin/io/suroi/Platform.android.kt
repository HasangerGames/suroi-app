package io.suroi

import android.annotation.SuppressLint
import android.content.Intent
import android.view.ViewGroup
import android.webkit.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import io.suroi.ui.theme.DialogType
import java.util.*

@Composable
actual fun WebFrame(
    modifier: Modifier,
    webEngine: WebEngine
) {
    AndroidView(
        factory = { context ->
            val androidWebView = WebView(context)
            webEngine.setupWebView(androidWebView)
            webEngine.androidWebView = androidWebView
            androidWebView
        },
        modifier = modifier
    )
}

@SuppressLint("SetJavaScriptEnabled")
actual class WebEngine actual constructor(
    private val url: String,
    private val onURLChange: (String) -> Unit,
    private val onDialog: (
        type: DialogType,
        title: String,
        message: String,
        defaultValue: String,
        onConfirm: (String?) -> Unit,
        onCancel: () -> Unit,
        onDismiss: () -> Unit
    ) -> Unit
) {
    internal var androidWebView: WebView? = null
        set(value) {
            field = value
            field?.let { loadUrl(this.url) }
        }

    private val scriptQueue = mutableListOf<String>()
    private var pageLoaded = false

    private val bindings = mutableMapOf<String, (String) -> Unit>()
    fun setupWebView(webView: WebView) {
        webView.apply {
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
                    pageLoaded = true
                    scriptQueue.forEach { executeJS(it) }
                    scriptQueue.clear()
                    bindings.forEach { (name, _) ->
                        val script = "window['$name'] = function(data) { kotlinBridge.postMessage('$name', JSON.stringify(data)); };"
                        executeJS(script)
                    }
                }

                override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                    val newURL = request.url.toString()
                    if (!newURL.contains("suroi")) {
                        onURLChange(newURL)
                        view.context.startActivity(Intent(Intent.ACTION_VIEW, request.url))
                        return true
                    }
                    return false
                }
            }
            webChromeClient = object : WebChromeClient() {
                override fun onJsAlert(v: WebView?, u: String?, m: String?, r: JsResult?): Boolean {
                    onDialog(DialogType.Alert, "Alert", m ?: "", "", { r?.confirm() }, {}, { r?.confirm() })
                    return true
                }
                override fun onJsConfirm(v: WebView?, u: String?, m: String?, r: JsResult?): Boolean {
                    onDialog(DialogType.Confirm, "Confirm", m ?: "", "", { r?.confirm() }, { r?.cancel() }, { r?.cancel() })
                    return true
                }
                override fun onJsPrompt(v: WebView?, u: String?, m: String?, d: String?, r: JsPromptResult?): Boolean {
                    onDialog(DialogType.Prompt, "Prompt", m ?: "", d ?: "", { i -> if (i != null) r?.confirm(i) else r?.cancel() }, { r?.cancel() }, { r?.cancel() })
                    return true
                }
                override fun onJsBeforeUnload(v: WebView?, u: String?, m: String?, r: JsResult?): Boolean {
                    onDialog(DialogType.Unload, "Leave page?", m ?: "Changes you made may not be saved.", "", { r?.confirm() }, { r?.cancel() }, { r?.cancel() })
                    return true
                }
            }
            addJavascriptInterface(object {
                @JavascriptInterface
                fun postMessage(name: String, data: String) {
                    bindings[name]?.invoke(data)
                }
            }, "kotlinBridge")
        }
    }

    actual fun executeJS(script: String) {
        if (pageLoaded) {
            androidWebView?.post {
                androidWebView?.evaluateJavascript(script, null)
            }
        } else {
            scriptQueue += script
        }
    }

    actual fun bind(name: String, block: (String) -> Unit) {
        bindings[name] = block
        val script = "window['$name'] = function(data) { kotlinBridge.postMessage('$name', JSON.stringify(data)); };"
        executeJS(script)
    }

    actual fun loadUrl(url: String) {
        androidWebView?.post {
            androidWebView?.loadUrl(url)
        }
    }
}

actual fun getDeviceLanguage(): String {
    return Locale.getDefault().language
}