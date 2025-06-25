package io.suroi

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.winterreisender.webviewko.WebviewKoCompose

@Composable
actual fun Webview(
    url: String,
    modifier: Modifier,
    onURLChange: (String) -> Unit
) {
    WebviewKoCompose.Webview(
        url = url,
        modifier = modifier.fillMaxSize(),
        init = "document.addEventListener('DOMContentLoaded', function() { $SCRIPT });"
    )
}

actual fun isOnline(): Boolean {
    TODO()
}