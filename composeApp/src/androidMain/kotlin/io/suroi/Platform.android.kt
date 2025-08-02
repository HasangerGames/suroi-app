package io.suroi

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.view.ViewGroup
import android.webkit.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.viewinterop.AndroidView
import coil3.compose.AsyncImage
import io.suroi.ui.components.DialogData
import io.suroi.ui.components.DialogType
import org.jetbrains.compose.resources.DrawableResource
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
    private val onDialog: (DialogData) -> Unit
) {
    internal var androidWebView: WebView? = null
        set(value) {
            field = value
            field?.let { loadUrl(this.url) }
        }

    private val persistentScripts = mutableListOf<String>()
    private val bindings = mutableMapOf<String, (String) -> String>()
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
                override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                    val newURL = request.url.toString()
                    if (!newURL.contains("suroi.io")) {
                        onURLChange(newURL)
                        view.context.startActivity(Intent(Intent.ACTION_VIEW, request.url))
                        return true
                    }
                    return false
                }


                override fun onReceivedHttpAuthRequest(
                    view: WebView?,
                    handler: HttpAuthHandler?,
                    host: String?,
                    realm: String?
                ) {
                    if (view == null || handler == null) {
                        return
                    }

                    var username: String? = null
                    var password: String? = null

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        val webViewDatabase = WebViewDatabase.getInstance(view.context)
                        val credentials = webViewDatabase.getHttpAuthUsernamePassword(host, realm)
                        if (credentials != null && credentials.size == 2) {
                            username = credentials[0]
                            password = credentials[1]
                        }
                    } else {
                        val credentials = view.getHttpAuthUsernamePassword(host, realm)
                        if (credentials != null && credentials.size == 2) {
                            username = credentials[0]
                            password = credentials[1]
                        }
                    }
                    if (username != null && password != null) {
                        handler.proceed(username, password)
                    } else {
                        onDialog(DialogData(
                            type = DialogType.Auth,
                            title = "Sign in",
                            message = "$realm",
                            onConfirm = { inputUsername, inputPassword ->
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    WebViewDatabase.getInstance(view.context).setHttpAuthUsernamePassword(host, realm, inputUsername, inputPassword)
                                } else {
                                    view.setHttpAuthUsernamePassword(host, realm, inputUsername, inputPassword)
                                }
                                handler.proceed(inputUsername, inputPassword)
                            },
                            onCancel = {
                                handler.cancel()
                            },
                            onDismiss = {
                                handler.cancel()
                                androidWebView?.reload()
                            }
                        ))
                    }
                }
            }
            webChromeClient = object : WebChromeClient() {
                override fun onJsAlert(v: WebView?, u: String?, m: String?, r: JsResult?): Boolean {
                    onDialog(DialogData(
                        DialogType.Alert,
                        "Alert",
                        m ?: "",
                        "",
                        { _, _ -> r?.confirm()  },
                        {},
                        { r?.confirm() }
                    ))
                    return true
                }
                override fun onJsConfirm(v: WebView?, u: String?, m: String?, r: JsResult?): Boolean {
                    onDialog(DialogData(
                        DialogType.Confirm,
                        "Confirm",
                        m ?: "",
                        "",
                        { _, _ -> r?.confirm() },
                        { r?.cancel() },
                        { r?.cancel() }
                    ))
                    return true
                }
                override fun onJsPrompt(v: WebView?, u: String?, m: String?, d: String?, r: JsPromptResult?): Boolean {
                    onDialog(DialogData(
                        DialogType.Prompt,
                        "Prompt",
                        m ?: "",
                        d ?: "",
                        { i, _ -> if (i != null) r?.confirm(i) else r?.cancel() },
                        { r?.cancel() }, { r?.cancel() }
                    ))
                    return true
                }
                override fun onJsBeforeUnload(v: WebView?, u: String?, m: String?, r: JsResult?): Boolean {
                    onDialog(DialogData(
                        DialogType.Unload,
                        "Leave page?",
                        m ?: "Changes you made may not be saved.",
                        "",
                        { _, _ -> r?.confirm() },
                        { r?.cancel() },
                        { r?.cancel() }
                    ))
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
        androidWebView?.post {
            androidWebView?.evaluateJavascript(script, null)
        }
    }

    actual fun addPersistentJS(script: String) {
        persistentScripts += script
        executeJS(script)
    }

    actual fun bind(name: String, block: (String) -> String) {
        bindings[name] = block
        addPersistentJS("window['$name'] = function(data) { kotlinBridge.postMessage('$name', JSON.stringify(data)); };")
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

@Composable
actual fun SVGImage(
    uri: String,
    resource: DrawableResource,
    description: String,
    modifier: Modifier,
    color: Color?
) {
    AsyncImage(
        model = uri,
        contentDescription = description,
        modifier = modifier,
        colorFilter = color?.let { ColorFilter.tint(color) }
    )
}