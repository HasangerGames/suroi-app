package io.suroi

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import io.suroi.ui.theme.Dialog
import io.suroi.ui.theme.DialogType

actual typealias PlatformContext = Activity

@Composable
actual fun Webview(
    url: String,
    modifier: Modifier,
    script: String,
    onURLChange: (String) -> Unit
) {
    val context = LocalContext.current
    val isWearOS = context.packageManager.hasSystemFeature(PackageManager.FEATURE_WATCH)
            || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && Build.DEVICE.contains("wear"))
    val below10 = Build.VERSION.SDK_INT < Build.VERSION_CODES.R

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

    if (isWearOS || below10) {
        GeckoWebview(url, modifier, script, onURLChange)
    } else {
        AndroidWebview(url, modifier, script, onURLChange, onDialog = showCustomDialog)
    }

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