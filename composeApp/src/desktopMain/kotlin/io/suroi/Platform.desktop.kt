package io.suroi

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.suroi.ui.components.DialogData
import java.util.*

@Composable
actual fun WebFrame(
    modifier: Modifier,
    webEngine: WebEngine
) {
}

actual class WebEngine actual constructor(
    private val url: String,
    private val onURLChange: (String) -> Unit,
    private val onDialog: (DialogData) -> Unit
) {
    actual fun executeJS(script: String) {
    }

    actual fun bind(name: String, block: (String) -> String) {
    }

    actual fun loadUrl(url: String) {
    }

}

actual fun getDeviceLanguage(): String {
    return Locale.getDefault().language
}