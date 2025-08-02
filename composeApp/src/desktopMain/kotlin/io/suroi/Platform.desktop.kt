package io.suroi


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import io.suroi.ui.components.DialogData
import me.friwi.jcefmaven.CefAppBuilder
import me.friwi.jcefmaven.MavenCefAppHandlerAdapter
import org.cef.CefApp.CefAppState
import org.cef.CefClient
import org.cef.CefSettings
import org.cef.browser.CefBrowser
import org.cef.browser.CefFrame
import org.cef.browser.CefMessageRouter
import org.cef.callback.CefQueryCallback
import org.cef.handler.CefDisplayHandler
import org.cef.handler.CefLifeSpanHandler
import org.cef.handler.CefLoadHandler
import org.cef.handler.CefMessageRouterHandler
import org.cef.network.CefRequest
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import java.awt.Component
import java.util.*
import kotlin.system.exitProcess


@Composable
actual fun WebFrame(
    modifier: Modifier,
    webEngine: WebEngine
) {
    SwingPanel(
        modifier = modifier.fillMaxSize(),
        factory = remember { { webEngine.component } }
    )
}


actual class WebEngine actual constructor(
    private val url: String,
    private val onURLChange: (String) -> Unit,
    private val onDialog: (DialogData) -> Unit
) {
    private var browser: CefBrowser? = null
    private var client: CefClient? = null
    private var initialized = false
    private val bindings: MutableMap<String, (String) -> String> = mutableMapOf()
    private var scriptQueue = mutableListOf<String>()
    private val persistentScripts = mutableListOf<String>()
    private var messageRouter: CefMessageRouter? = null
    init {
        initializeCef()
    }
    private fun initializeCef() {
        val builder = CefAppBuilder()
        builder.cefSettings.windowless_rendering_enabled = false
        builder.setAppHandler(object : MavenCefAppHandlerAdapter() {
            override fun stateHasChanged(state: CefAppState?) {
                if (state == CefAppState.TERMINATED) exitProcess(0)
            }
        })
        val cefApp = builder.build()
        client = cefApp.createClient()
        messageRouter = CefMessageRouter.create()
        messageRouter?.addHandler(object : CefMessageRouterHandler {
            override fun onQuery(
                browser: CefBrowser?,
                frame: CefFrame?,
                queryId: Long,
                request: String?,
                persistent: Boolean,
                callback: CefQueryCallback?
            ): Boolean {
                request?.let { req ->
                    try {
                        val parts = req.split(Regex(":"), 2)
                        if (parts.size == 2) {
                            val functionName = parts[0]
                            val parameter = parts[1]
                            bindings[functionName]?.let { binding ->
                                val result = binding(parameter)
                                callback?.success(result)
                                return true
                            }
                        }
                    } catch (e: Exception) {
                        callback?.failure(-1, e.message ?: "Unknown error")
                    }
                }
                return false
            }
            override fun onQueryCanceled(browser: CefBrowser?, frame: CefFrame?, queryId: Long) {}
            override fun setNativeRef(identifier: String?, nativeRef: Long) {}
            override fun getNativeRef(identifier: String?): Long {
                return 0L
            }
        }, true)
        client?.addMessageRouter(messageRouter)
        client?.addDisplayHandler(object : CefDisplayHandler {
            override fun onAddressChange(browser: CefBrowser?, frame: CefFrame?, url: String?) {
                url?.let {
                    if (!it.contains("suroi.io")) {
                        onURLChange(it)
                    }
                }
            }
            override fun onTitleChange(browser: CefBrowser?, title: String?) {}
            override fun onTooltip(browser: CefBrowser?, text: String?): Boolean {
                return false
            }
            override fun onStatusMessage(browser: CefBrowser?, value: String?) {}
            override fun onConsoleMessage(
                browser: CefBrowser?,
                level: CefSettings.LogSeverity?,
                message: String?,
                source: String?,
                line: Int
            ): Boolean {
                return false
            }
            override fun onFullscreenModeChange(browser: CefBrowser?, fullscreen: Boolean) {}
            override fun onCursorChange(browser: CefBrowser?, cursorType: Int): Boolean {
                return false
            }
        })

        client?.addLifeSpanHandler(object : CefLifeSpanHandler {
            override fun onBeforePopup(
                browser: CefBrowser?,
                frame: CefFrame?,
                target_url: String?,
                target_frame_name: String?
            ): Boolean {
                return false
            }

            override fun onAfterCreated(browser: CefBrowser?) {
                browser?.let { br ->
                    initialized = true
                    synchronized(scriptQueue) {
                        scriptQueue.forEach { script ->
                            br.executeJavaScript(script, br.url, 0)
                        }
                        scriptQueue.clear()
                    }
                }
            }
            override fun onAfterParentChanged(browser: CefBrowser?) {}
            override fun doClose(browser: CefBrowser?): Boolean {
                return false
            }
            override fun onBeforeClose(browser: CefBrowser?) {}
        })
        client?.addLoadHandler(object : CefLoadHandler {
            override fun onLoadingStateChange(
                browser: CefBrowser?,
                isLoading: Boolean,
                canGoBack: Boolean,
                canGoForward: Boolean
            ) {
            }

            override fun onLoadStart(
                browser: CefBrowser?,
                frame: CefFrame?,
                transitionType: CefRequest.TransitionType?
            ) {
                persistentScripts.forEach { script ->
                    browser?.executeJavaScript(script, browser.url, 0)
                }
            }

            override fun onLoadEnd(
                browser: CefBrowser?,
                frame: CefFrame?,
                httpStatusCode: Int
            ) {
            }

            override fun onLoadError(
                browser: CefBrowser?,
                frame: CefFrame?,
                errorCode: CefLoadHandler.ErrorCode?,
                errorText: String?,
                failedUrl: String?
            ) {
            }
        })
        browser = client?.createBrowser(url, false, false)
    }


    actual fun executeJS(script: String) {
        browser?.executeJavaScript(script, browser?.url ?: "", 0) ?: scriptQueue.add(script)
    }


    actual fun addPersistentJS(script: String) {
        persistentScripts += script
        executeJS(script)
    }


    actual fun bind(name: String, block: (String) -> String) {
        bindings[name] = block
        val bindingScript = """
           window.$name = function(param) {
               return new Promise(function(resolve, reject) {
                   window.cefQuery({
                       request: '$name:' + (typeof param === 'string' ? param : JSON.stringify(param)),
                       onSuccess: function(response) {
                           resolve(response);
                       },
                       onFailure: function(error_code, error_message) {
                           reject(new Error(error_message));
                       }
                   });
               });
           };
       """.trimIndent()
        executeJS(bindingScript)
    }
    actual fun loadUrl(url: String) {
        browser?.loadURL(url)
    }
    val component: Component
        get() = browser?.uiComponent ?: throw IllegalStateException("Browser not initialized")
    fun dispose() {
        browser?.let { br ->
            client?.removeMessageRouter(messageRouter)
            br.close(true)
        }
        client?.dispose()
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
    Image(
        painter = painterResource(resource),
        contentDescription = description,
        modifier = modifier,
        colorFilter = color?.let { ColorFilter.tint(color) }
    )
}

