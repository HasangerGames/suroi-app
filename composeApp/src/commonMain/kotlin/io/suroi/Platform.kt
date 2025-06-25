package io.suroi

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier

const val SCRIPT = "document.querySelector('.btn-kofi').style.display = 'none';"
@Composable
expect fun Webview(
    url: String,
    modifier: Modifier,
    onURLChange: (String) -> Unit
)

expect fun isOnline(): Boolean