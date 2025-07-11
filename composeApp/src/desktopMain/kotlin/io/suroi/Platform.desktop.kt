package io.suroi

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.winterreisender.webviewko.WebviewKoCompose
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
    WebviewKoCompose.Webview(
        url = url,
        modifier = modifier.fillMaxSize(),
        init = "document.addEventListener('DOMContentLoaded', function() { $script });"
    )
}

actual fun getDeviceLanguage(): String {
    return Locale.getDefault().language
}