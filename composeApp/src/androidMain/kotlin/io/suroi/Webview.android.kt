package io.suroi

import android.annotation.SuppressLint
import android.content.Intent
import android.view.ViewGroup
import android.webkit.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import io.suroi.ui.theme.DialogType

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun AndroidWebview(
    url: String,
    modifier: Modifier,
    script: String,
    onURLChange: (String) -> Unit,
    onDialog: (
        DialogType,
        String,
        String,
        String,
        (String?) -> Unit,
        () -> Unit,
        () -> Unit
    ) -> Unit
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
                webChromeClient = object : WebChromeClient() {
                    override fun onJsAlert(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean {
                        onDialog(
                            DialogType.Alert,
                            "Alert",
                            message ?: "",
                            "",
                            { result?.confirm() },
                            {},
                            { result?.confirm() }
                        )
                        return true
                    }
                    override fun onJsConfirm(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean {
                        onDialog(
                            DialogType.Confirm,
                            "Confirm",
                            message ?: "",
                            "",
                            { result?.confirm() },
                            { result?.cancel() },
                            { result?.cancel() }
                        )
                        return true
                    }
                    override fun onJsPrompt(view: WebView?, url: String?, message: String?, defaultValue: String?, result: JsPromptResult?): Boolean {
                        onDialog(
                            DialogType.Prompt,
                            "Prompt",
                            message ?: "",
                            defaultValue ?: "",
                            { input ->
                                if (input != null) result?.confirm(input)
                                else result?.cancel()
                            },
                            { result?.cancel() },
                            { result?.cancel() }
                        )
                        return true
                    }
                    override fun onJsBeforeUnload(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean {
                        onDialog(
                            DialogType.Unload,
                            "Leave page?",
                            message ?: "Changes you made may not be saved.",
                            "",
                            { result?.confirm() },
                            { result?.cancel() },
                            { result?.cancel() }
                        )
                        return true
                    }
                }
                loadUrl(url)
            }
        },
        modifier = modifier
    )
}