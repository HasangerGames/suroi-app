package io.suroi

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import com.github.winterreisender.webviewko.WebviewKo
import com.sun.jna.Native
import com.sun.jna.ptr.PointerByReference
import io.suroi.ui.components.DialogData
import java.awt.Canvas
import java.awt.Graphics
import java.awt.GraphicsEnvironment
import java.util.*


@Composable
actual fun WebFrame(
    modifier: Modifier,
    webEngine: WebEngine
) {
    SwingPanel(
        modifier = modifier.fillMaxSize(),
        factory = remember { { webEngine.canvas } }
    )
}

actual class WebEngine actual constructor(
    private val url: String,
    private val onURLChange: (String) -> Unit,
    private val onDialog: (DialogData) -> Unit
) {
    private var webview: WebviewKo? = null
    private var initialized = false
    private val bindings: MutableMap<String, (String) -> String> = mutableMapOf()
    private var scriptQueue: MutableList<String> = mutableListOf()

    actual fun executeJS(script: String) {
        webview?.eval(script) ?: scriptQueue.add(script)
    }

    actual fun bind(name: String, block: (String) -> String) {
        webview?.bind(name) { block(it) }
    }

    actual fun loadUrl(url: String) {
        webview?.navigate(url)
    }

    val canvas = object : Canvas() {
        override fun paint(g: Graphics?) {
            if (!initialized) {
                initialized = true
                Thread {
                    webview = WebviewKo(1, null, PointerByReference(Native.getComponentPointer(this)))
                    val gd = GraphicsEnvironment.getLocalGraphicsEnvironment().defaultScreenDevice
                    var width = gd.displayMode.width
                    var height = gd.displayMode.height
                    if (System.getProperty("os.name").lowercase().contains("windows")) {
                        width -= 16
                        height -= 39
                    }
                    webview?.size(width, height, WebviewKo.WindowHint.Fixed)

                    synchronized(scriptQueue) {
                        scriptQueue.forEach { script ->
                            webview?.dispatch { init(
                        """
                            document.readyState === 'complete'
                                ? (function() { $script })()
                                : window.addEventListener('DOMContentLoaded', function() { $script });
                            """.trimIndent()
                            ) }
                        }
                        scriptQueue.clear()
                    }

                    bindings.forEach { (k, v) ->
                        webview?.dispatch { bind(k, v) }
                    }
                    webview?.dispatch { navigate(url) }
                    webview?.show()
                }.start()
            }
        }
    }



}

actual fun getDeviceLanguage(): String {
    return Locale.getDefault().language
}