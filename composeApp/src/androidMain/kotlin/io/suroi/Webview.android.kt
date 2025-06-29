package io.suroi

import android.annotation.SuppressLint
import android.content.Intent
import android.view.ViewGroup
import android.webkit.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import io.suroi.ui.theme.DialogType
import kotlinx.coroutines.CompletableDeferred
import org.mozilla.geckoview.*
import org.mozilla.geckoview.GeckoSession.PromptDelegate.ButtonPrompt.Type.NEGATIVE
import org.mozilla.geckoview.GeckoSession.PromptDelegate.ButtonPrompt.Type.POSITIVE

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

@Composable
fun GeckoWebview(
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
            val geckoView = GeckoView(context)
            val runtime = GeckoRuntime.create(context)
            val session = GeckoSession()

            session.contentDelegate = object : GeckoSession.ContentDelegate {}

            session.progressDelegate = object : GeckoSession.ProgressDelegate {
                override fun onPageStop(session: GeckoSession, success: Boolean) {
                    if (success) {
                        session.loadUri("javascript:(function() { $script } })();")
                    } else {
                        println("unsuccessful page stop")
                    }
                }
            }

            session.promptDelegate = object : GeckoSession.PromptDelegate {
                val deferredResponse = CompletableDeferred<GeckoSession.PromptDelegate.PromptResponse?>()
                override fun onAlertPrompt(
                    session: GeckoSession,
                    prompt: GeckoSession.PromptDelegate.AlertPrompt
                ): GeckoResult<GeckoSession.PromptDelegate.PromptResponse?>? {
                    onDialog(
                        DialogType.Alert,
                        "Alert",
                        prompt.message ?: "",
                        "",
                        { deferredResponse.complete(prompt.dismiss()) },
                        {},
                        { deferredResponse.complete(prompt.dismiss()) }
                    )
                    return super.onAlertPrompt(session, prompt)
                }
                override fun onButtonPrompt(
                    session: GeckoSession,
                    prompt: GeckoSession.PromptDelegate.ButtonPrompt
                ): GeckoResult<GeckoSession.PromptDelegate.PromptResponse?>? {
                    onDialog(
                        DialogType.Confirm,
                        "Confirm",
                        prompt.message ?: "",
                        "",
                        { deferredResponse.complete(prompt.confirm(POSITIVE)) },
                        { deferredResponse.complete(prompt.confirm(NEGATIVE)) },
                        { deferredResponse.complete(prompt.dismiss()) }
                    )
                    return super.onButtonPrompt(session, prompt)
                }
                // TODO() text prompt does not show up at all
                override fun onTextPrompt(
                    session: GeckoSession,
                    prompt: GeckoSession.PromptDelegate.TextPrompt
                ): GeckoResult<GeckoSession.PromptDelegate.PromptResponse?>? {
                    onDialog(
                        DialogType.Prompt,
                        "Prompt",
                        prompt.message ?: "",
                        prompt.defaultValue ?: "",
                        { input -> deferredResponse.complete(prompt.confirm(input ?: "")) },
                        { deferredResponse.complete(prompt.dismiss()) },
                        { deferredResponse.complete(prompt.dismiss()) }
                    )
                    return super.onTextPrompt(session, prompt)
                }
                override fun onBeforeUnloadPrompt(
                    session: GeckoSession,
                    prompt: GeckoSession.PromptDelegate.BeforeUnloadPrompt
                ): GeckoResult<GeckoSession.PromptDelegate.PromptResponse?>? {
                    onDialog(
                        DialogType.Unload,
                        prompt.title ?: "Reload page?",
                        "Changes you made may not be saved.",
                        "",
                        // TODO() pressing OK button does nothing here
                        { deferredResponse.complete(prompt.confirm(AllowOrDeny.ALLOW)) },
                        { deferredResponse.complete(prompt.confirm(AllowOrDeny.DENY)) },
                        { deferredResponse.complete(prompt.dismiss()) }
                    )
                    return super.onBeforeUnloadPrompt(session, prompt)
                }
            }

            session.open(runtime)
            geckoView.setSession(session)

            session.navigationDelegate = object : GeckoSession.NavigationDelegate {
                override fun onNewSession(session: GeckoSession, uri: String): GeckoResult<GeckoSession?>? {
                    if (!uri.startsWith("https://suroi.io")) {
                        onURLChange(uri)
                        context.startActivity(Intent(Intent.ACTION_VIEW, uri.toUri()))
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