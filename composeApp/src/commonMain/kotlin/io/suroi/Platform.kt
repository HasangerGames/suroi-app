package io.suroi

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.suroi.ui.components.DialogData

@Composable
expect fun WebFrame(
    modifier: Modifier,
    webEngine: WebEngine
)

expect class WebEngine(
    url: String,
    onURLChange: (String) -> Unit,
    onDialog: (DialogData) -> Unit
) {
    fun executeJS(script: String)
    fun addPersistentJS(script: String)
    fun bind(name: String, block: (String) -> String)
    fun loadUrl(url: String)
}



expect fun getDeviceLanguage(): String