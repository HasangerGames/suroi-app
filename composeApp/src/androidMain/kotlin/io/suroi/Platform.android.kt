package io.suroi

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import io.suroi.ui.theme.Dialog
import io.suroi.ui.theme.DialogType
import java.util.*

@Composable
actual fun Webview(
    url: String,
    modifier: Modifier,
    script: String,
    onURLChange: (String) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var dialogType by remember { mutableStateOf(DialogType.Alert) }
    var dialogTitle by remember { mutableStateOf("") }
    var dialogMessage by remember { mutableStateOf("") }
    var dialogDefaultValue by remember { mutableStateOf("") }
    var onDialogConfirm: (String?) -> Unit by remember { mutableStateOf({}) }
    var onDialogCancel: () -> Unit by remember { mutableStateOf({}) }
    var onDialogDismiss: () -> Unit by remember { mutableStateOf({}) }

    val showCustomDialog: (DialogType, String, String, String, (String?) -> Unit, () -> Unit, () -> Unit) -> Unit =
        { type, title, message, defaultValue, onConfirm, onCancel, onDismiss ->
            dialogType = type
            dialogTitle = title
            dialogMessage = message
            dialogDefaultValue = defaultValue
            onDialogConfirm = { input ->
                onConfirm(input)
                showDialog = false
            }
            onDialogCancel = {
                onCancel()
                showDialog = false
            }
            onDialogDismiss = {
                onDismiss()
                showDialog = false
            }
            showDialog = true
        }
    AndroidWebview(url, modifier, script, onURLChange, onDialog = showCustomDialog)

    if (showDialog) {
        Dialog(
            type = dialogType,
            title = dialogTitle,
            message = dialogMessage,
            defaultValue = dialogDefaultValue,
            onDismissRequest = onDialogDismiss,
            onConfirm = onDialogConfirm,
            onCancel = onDialogCancel
        )
    }
}

actual fun getDeviceLanguage(): String {
    return Locale.getDefault().language
}