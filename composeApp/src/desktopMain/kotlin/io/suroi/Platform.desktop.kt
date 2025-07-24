package io.suroi

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import io.suroi.ui.theme.DialogType
import org.cef.CefApp
import org.cef.CefClient
import org.cef.CefSettings
import org.cef.browser.CefBrowser
import org.cef.browser.CefMessageRouter
import org.cef.callback.CefJSDialogCallback
import org.cef.callback.CefQueryCallback
import org.cef.handler.CefJSDialogHandler
import org.cef.handler.CefJSDialogHandlerAdapter
import org.cef.handler.CefMessageRouterHandlerAdapter
import org.cef.handler.CefRequestHandlerAdapter
import org.cef.misc.BoolRef
import java.awt.Desktop
import java.net.URI
import java.util.*
import javax.swing.SwingUtilities

@Composable
actual fun WebFrame(
    modifier: Modifier,
    webEngine: WebEngine
) {
    val browser = remember { webEngine.getBrowser() }
    DisposableEffect(Unit) {
        onDispose {
            webEngine.dispose()
        }
    }
    SwingPanel(
        factory = { browser.uiComponent },
        modifier = modifier.fillMaxSize()
    )
}

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
    private val cefClient: CefClient
    private val cefBrowser: CefBrowser
    private val messageRouter: CefMessageRouter

    private val bindings = mutableMapOf<String, (String) -> Unit>()

    init {
        initializeCef()

        cefClient = CefApp.getInstance().createClient()
        messageRouter = CefMessageRouter.create()

        val messageHandler = object : CefMessageRouterHandlerAdapter() {
            override fun onQuery(
                browser: CefBrowser?,
                frame: org.cef.browser.CefFrame?,
                queryId: Long,
                request: String?,
                persistent: Boolean,
                callback: CefQueryCallback?
            ): Boolean {
                request?.let {
                    val parts = it.split(":", limit = 2)
                    val name = parts.getOrNull(0)
                    val payload = parts.getOrNull(1) ?: ""

                    bindings[name]?.invoke(payload)
                    callback?.success("OK")
                    return true
                }
                callback?.failure(0, "Invalid request")
                return false
            }
        }
        messageRouter.addHandler(messageHandler, true)
        cefClient.addMessageRouter(messageRouter)

        cefClient.addJSDialogHandler(object : CefJSDialogHandlerAdapter() {
            override fun onJSDialog(
                browser: CefBrowser?,
                origin_url: String?,
                dialog_type: CefJSDialogHandler.JSDialogType?,
                message_text: String?,
                default_prompt_text: String?,
                callback: CefJSDialogCallback?,
                suppress_message: BoolRef?
            ): Boolean {
                val type = when (dialog_type) {
                    CefJSDialogHandler.JSDialogType.JSDIALOGTYPE_ALERT -> DialogType.Alert
                    CefJSDialogHandler.JSDialogType.JSDIALOGTYPE_CONFIRM -> DialogType.Confirm
                    CefJSDialogHandler.JSDialogType.JSDIALOGTYPE_PROMPT -> DialogType.Prompt
                    else -> return false
                }

                onDialog(
                    type,
                    origin_url ?: "Page Dialog",
                    message_text ?: "",
                    default_prompt_text ?: "",
                    { input -> callback?.Continue(true, input) },
                    { callback?.Continue(false, "") },
                    { callback?.Continue(false, "") }
                )
                return true
            }
        })

        cefClient.addRequestHandler(object : CefRequestHandlerAdapter() {
            override fun onBeforeBrowse(
                browser: CefBrowser?,
                frame: org.cef.browser.CefFrame?,
                request: org.cef.network.CefRequest?,
                userGesture: Boolean,
                isRedirect: Boolean
            ): Boolean {
                val targetUrl = request?.url ?: return false
                if (targetUrl.startsWith("https://suroi.io")) {
                    return false
                }
                onURLChange(targetUrl)
                try {
                    if (Desktop.isDesktopSupported()) {
                        Desktop.getDesktop().browse(URI(targetUrl))
                    }
                } catch (e: Exception) {
                    println("Failed to open external link: $e")
                }
                return true
            }
        })

        cefBrowser = cefClient.createBrowser(url, false, false)
    }

    fun getBrowser(): CefBrowser = cefBrowser

    actual fun executeJS(script: String) {
        cefBrowser.executeJavaScript(script, cefBrowser.url, 0)
    }

    actual fun bind(name: String, block: (String) -> Unit) {
        bindings[name] = block
        val script = """
            if (!window.kotlin) { window.kotlin = {}; }
            window.kotlin['$name'] = function(payload) {
                window.cefQuery({
                    request: '$name:' + JSON.stringify(payload),
                    onSuccess: function(response) {},
                    onFailure: function(error_code, error_message) {}
                });
            };
        """.trimIndent()
        executeJS(script)
    }

    actual fun loadUrl(url: String) {
        cefBrowser.loadURL(url)
    }

    fun dispose() {
        messageRouter.dispose()
        cefClient.dispose()
        cefBrowser.close(true)
    }

    companion object {
        private var isCefInitialized = false

        @Synchronized
        fun initializeCef() {
            if (isCefInitialized) return
            SwingUtilities.invokeLater {
                if (!isCefInitialized) {
                    val settings = CefSettings()
                    settings.windowless_rendering_enabled = false
                    isCefInitialized = true
                }
            }
        }
    }
}

actual fun getDeviceLanguage(): String {
    return Locale.getDefault().language
}