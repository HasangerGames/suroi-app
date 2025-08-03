package io.suroi.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import io.suroi.WebEngine
import io.suroi.WebFrame

@Composable
fun GameScreen(
    onExitGame: () -> Unit,
    realm: String,
    region: String
) {
    var showDialog by remember { mutableStateOf(false) }
    var dialogData by remember { mutableStateOf(DialogData()) }

    val showCustomDialog: (DialogData) -> Unit = { data ->
        dialogData = data.copy(
            onConfirm = { username, password ->
                data.onConfirm(username, password)
                showDialog = false
            },
            onCancel = {
                data.onCancel()
                showDialog = false
            },
            onDismiss = {
                data.onDismiss()
                showDialog = false
            }
        )
        showDialog = true
    }

    val webEngine = remember {
        WebEngine(
            "https://$realm/?region=$region",
            onURLChange = {
                onExitGame()
            },
            onDialog = showCustomDialog,
        ).apply {
            addPersistentJS(
                """
                document.readyState === 'complete'
                  ? (function() { document.querySelector('.btn-kofi').style.display = 'none'; })()
                  : window.addEventListener('DOMContentLoaded', function() { 
                    document.querySelector('.btn-kofi').style.display = 'none'; 
                  });
                """.trimIndent()
            )
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            webEngine.destroy()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        WebFrame(
            modifier = Modifier.fillMaxWidth(),
            webEngine = webEngine
        )
        if (showDialog) {
            Dialog(dialogData)
        }
    }
}
