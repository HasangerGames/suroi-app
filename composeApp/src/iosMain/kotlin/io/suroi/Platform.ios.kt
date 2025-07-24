package io.suroi

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitView
import io.suroi.ui.theme.DialogType
import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreGraphics.CGRectMake
import platform.Foundation.*
import platform.UIKit.UIApplication
import platform.WebKit.*
import platform.darwin.NSObject

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun WebFrame(
    modifier: Modifier,
    webEngine: WebEngine
) {
    UIKitView(
        factory = { webEngine.getWebView() },
        modifier = modifier.fillMaxSize(),
    )
}

@OptIn(ExperimentalForeignApi::class)
actual class WebEngine actual constructor(
    url: String,
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
) : NSObject(), WKScriptMessageHandlerProtocol, WKUIDelegateProtocol, WKNavigationDelegateProtocol {

    private val userContentController = WKUserContentController()
    private val wkWebView: WKWebView

    private val bindings = mutableMapOf<String, (String) -> Unit>()

    init {
        val configuration = WKWebViewConfiguration().apply {
            userContentController = this@WebEngine.userContentController
        }
        val cGRectZero = CGRectMake(0.0, 0.0, 0.0, 0.0)
        wkWebView = WKWebView(
            frame = cGRectZero,
            configuration
        ).apply {
            this.UIDelegate = this@WebEngine
            this.navigationDelegate = this@WebEngine
        }
        loadUrl(url)
    }

    fun getWebView(): WKWebView = wkWebView

    actual fun executeJS(script: String) {
        wkWebView.evaluateJavaScript(script, null)
    }

    actual fun bind(name: String, block: (String) -> Unit) {
        bindings[name] = block
        userContentController.addScriptMessageHandler(this, name)
    }

    actual fun loadUrl(url: String) {
        val nsUrl = NSURL(string = url)
        val request = NSURLRequest(uRL = nsUrl)
        wkWebView.loadRequest(request)
    }

    override fun userContentController(userContentController: WKUserContentController, didReceiveScriptMessage: WKScriptMessage) {
        val name = didReceiveScriptMessage.name
        val body = didReceiveScriptMessage.body as? String ?: ""
        bindings[name]?.invoke(body)
    }

    override fun webView(webView: WKWebView, runJavaScriptAlertPanelWithMessage: String, initiatedByFrame: WKFrameInfo, completionHandler: () -> Unit) {
        onDialog(DialogType.Alert, "Alert", runJavaScriptAlertPanelWithMessage, "", { completionHandler() }, {}, { completionHandler() })
    }

    override fun webView(webView: WKWebView, runJavaScriptConfirmPanelWithMessage: String, initiatedByFrame: WKFrameInfo, completionHandler: (Boolean) -> Unit) {
        onDialog(DialogType.Confirm, "Confirm", runJavaScriptConfirmPanelWithMessage, "", { completionHandler(true) }, { completionHandler(false) }, { completionHandler(false) })
    }

    override fun webView(webView: WKWebView, runJavaScriptTextInputPanelWithPrompt: String, defaultText: String?, initiatedByFrame: WKFrameInfo, completionHandler: (String?) -> Unit) {
        onDialog(DialogType.Prompt, "Prompt", runJavaScriptTextInputPanelWithPrompt, defaultText ?: "", { input -> completionHandler(input) }, { completionHandler(null) }, { completionHandler(null) })
    }

    override fun webView(webView: WKWebView, decidePolicyForNavigationAction: WKNavigationAction, decisionHandler: (WKNavigationActionPolicy) -> Unit) {
        val requestUrl = decidePolicyForNavigationAction.request.URL?.absoluteString
        if (requestUrl != null && !requestUrl.contains("suroi")) {
            onURLChange(requestUrl)
            UIApplication.sharedApplication.openURL(decidePolicyForNavigationAction.request.URL!!)
            decisionHandler(WKNavigationActionPolicy.WKNavigationActionPolicyCancel)
            return
        }
        decisionHandler(WKNavigationActionPolicy.WKNavigationActionPolicyAllow)
    }
}

actual fun getDeviceLanguage(): String {
    return NSLocale.currentLocale.languageCode
}