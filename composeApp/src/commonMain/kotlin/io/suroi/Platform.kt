package io.suroi

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.suroi.ui.theme.DialogType

@Composable
expect fun Webview(
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
)

expect fun getDeviceLanguage(): String