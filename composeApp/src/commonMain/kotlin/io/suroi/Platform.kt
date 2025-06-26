package io.suroi

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

const val SCRIPT = "document.querySelector('.btn-kofi').style.display = 'none';"

expect class PlatformContext

@Composable
expect fun Webview(
    url: String,
    modifier: Modifier,
    onURLChange: (String) -> Unit
)