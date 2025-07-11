package io.suroi

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import io.suroi.ui.theme.*
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import suroi.composeapp.generated.resources.Res
import suroi.composeapp.generated.resources.settings

@Composable
fun App() {
    SuroiTheme {
        lateinit var serverInfo: ServerInfo
        var showContent by remember { mutableStateOf(false) }
        var backgroundIsLoading by remember { mutableStateOf(true) }
        val backgroundImageURL = remember { mutableStateOf<String?>(null) }

        var connecting by remember { mutableStateOf(false) }
        var showOfflineScreen by remember { mutableStateOf(false) }
        val coroutineScope = rememberCoroutineScope()
        LaunchedEffect(Unit) {
            serverInfo = getServerInfo("https://na.suroi.io/api/serverInfo")
            try {
                val mode = serverInfo.mode
                backgroundImageURL.value = "https://suroi.io/img/backgrounds/menu/${mode}.png"
            } catch (e: Exception) {
                println("Error fetching mode: ${e.message}")
            } finally {
                backgroundIsLoading = false
            }
        }
        Box(modifier = Modifier.fillMaxSize().background(color = Gray)) {
            if (backgroundIsLoading || connecting) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(96.dp))
                }
            } else {
                AsyncImage(
                    model = backgroundImageURL.value,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
            }

            if (showOfflineScreen) {
                OfflineScreen (onRetry = {
                    connecting = true
                    coroutineScope.launch { showOfflineScreen = !isOnline() }
                    connecting = false
                }
                )
            } else if (!showContent) {
                Button(
                    modifier = Modifier.align(Alignment.Center),
                    onClick = {
                        showOfflineScreen = false
                        connecting = true
                        coroutineScope.launch {
                            if (isOnline()) {
                                showContent = true
                            } else {
                                showOfflineScreen = true
                            }
                        }
                        connecting = false
                    },
                    enabled = !connecting
                ) {
                    Text("Play")
                }
            }
            AnimatedVisibility(
                visible = showContent,
                modifier = Modifier.align(Alignment.Center),
                enter = fadeIn() + expandVertically (),
                exit = fadeOut() + shrinkVertically()
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
                Webview(
                    "https://suroi.io",
                    modifier = Modifier.fillMaxSize(),
                    script = "document.querySelector('.btn-kofi').style.display = 'none';",
                    onURLChange = { showContent = false },
                    onDialog = showCustomDialog
                )
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
            AnimatedVisibility(
                visible = !showContent,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)) {
                Box(
                    modifier = Modifier
                        .background(
                            color = DarkTransparent,
                            shape = RoundedCornerShape(24.dp)
                        )
                ) {
                    IconButton(
                        onClick = {},
                        modifier = Modifier
                            .size(48.dp)
                            .padding(4.dp)
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.settings),
                            contentDescription = "settings",
                            tint = White,
                            modifier = Modifier,
                        )
                    }

                }
            }
        }
    }
}