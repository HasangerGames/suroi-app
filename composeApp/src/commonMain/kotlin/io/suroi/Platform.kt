package io.suroi

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun Webview(
    url: String,
    modifier: Modifier,
    script: String,
    onURLChange: (String) -> Unit
)

expect fun getDeviceLanguage(): String