package io.suroi

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.suroi.ui.theme.DialogType
import java.util.*

@Composable
actual fun Webview(
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
    AndroidWebview(url, modifier, script, onURLChange, onDialog = onDialog)
}

actual fun getDeviceLanguage(): String {
    return Locale.getDefault().language
}