package io.suroi

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.suroi.ui.theme.DialogType

@Composable
expect fun WebFrame(
    modifier: Modifier,
    webEngine: WebEngine
)

expect class WebEngine(
    url: String,
    onURLChange: (String) -> Unit,
    onDialog: (
        type: DialogType,
        title: String,
        message: String,
        defaultValue: String,
        onConfirm: (String?) -> Unit,
        onCancel: () -> Unit,
        onDismiss: () -> Unit
    ) -> Unit
) {
    fun executeJS(script: String)
    fun bind(name: String, block: (String) -> Unit)
    fun loadUrl(url: String)
}



expect fun getDeviceLanguage(): String