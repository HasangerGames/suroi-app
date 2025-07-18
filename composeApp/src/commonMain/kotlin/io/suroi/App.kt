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
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import coil3.compose.AsyncImage
import io.suroi.ui.theme.*
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import suroi.composeapp.generated.resources.Res
import suroi.composeapp.generated.resources.back
import suroi.composeapp.generated.resources.settings

@Composable
fun App(
    datastore: DataStore<Preferences>,
) {
    SuroiTheme {
        var showContent by remember { mutableStateOf(false) }
        var backgroundIsLoading by remember { mutableStateOf(true) }
        var backgroundImageURL by remember { mutableStateOf("") }
        val deviceLanguage = remember { getDeviceLanguage() }

        var connecting by remember { mutableStateOf(false) }
        var showOfflineScreen by remember { mutableStateOf(false) }
        var showSettings by remember { mutableStateOf(false) }
        val scope = rememberCoroutineScope()
        LaunchedEffect(Unit) {
            var mode = "normal"
            try {
                val serverInfo = getServerInfo("https://na.suroi.io/api/serverInfo")
                mode = serverInfo.mode
            } catch (e: Exception) {
                println("Error fetching mode: ${e.message}")
            } finally {
                backgroundIsLoading = false
                backgroundImageURL = "https://suroi.io/img/backgrounds/menu/$mode.png"
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
                    model = backgroundImageURL,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
            }

            if (showOfflineScreen) {
                OfflineScreen (onRetry = {
                    connecting = true
                    scope.launch { showOfflineScreen = !isOnline() }
                    connecting = false
                }
                )
            } else if (!showContent) {
                Button(
                    modifier = Modifier.align(Alignment.Center),
                    onClick = {
                        showOfflineScreen = false
                        connecting = true
                        scope.launch {
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
                    "https://suroi.io/?language=$deviceLanguage",
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
                        onClick = { showSettings = true },
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
            AnimatedVisibility(
                visible = showSettings,
                enter = expandIn(expandFrom = Alignment.TopEnd) + fadeIn(),
                exit = shrinkOut(shrinkTowards = Alignment.TopEnd) + fadeOut(),
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Settings(
                    onBackClicked = { showSettings = false },
                    modifier = Modifier
                ){
                    // TODO TODO TODO TODO TODO TODO
                    val settingExample: Boolean by datastore.data.map {
                        val exampleKey = booleanPreferencesKey("hi")
                        it[exampleKey] ?: false
                    }.collectAsState(initial = false)

                    Text(
                        text = "setting state is: $settingExample",
                        color = White
                    )
                    Switch(
                        checked = settingExample,
                        onCheckedChange = {
                            scope.launch {
                                datastore.edit { datastore ->
                                    val exampleKey = booleanPreferencesKey("hi")
                                    datastore[exampleKey] = !settingExample
                                }
                            }
                        },
                        modifier = Modifier.padding(16.dp),
                    )
                    // TODO TODO TODO TODO TODO TODO
                }
            }
        }
    }
}

@Composable
fun Settings(onBackClicked: () -> Unit, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Gray),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .padding(16.dp)
                .background(
                    color = DarkTransparent,
                    shape = RoundedCornerShape(24.dp)
                )
                .align(Alignment.TopStart)
        ) {
            IconButton(
                onClick = onBackClicked,
                modifier = modifier
                    .size(48.dp)
                    .padding(4.dp)
            ) {
                Icon(
                    painter = painterResource(Res.drawable.back),
                    contentDescription = "back",
                    tint = White,
                    modifier = Modifier,
                )
            }
            content()
        }
    }
}