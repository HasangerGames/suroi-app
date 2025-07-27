package io.suroi

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import io.suroi.ui.components.Dialog
import io.suroi.ui.components.DialogData
import io.suroi.ui.components.ServerDisplay
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
        var ingame by remember { mutableStateOf(false) }
        var showOfflineScreen by remember { mutableStateOf(false) }
        var showSettings by remember { mutableStateOf(false) }
        val scope = rememberCoroutineScope()

        Box(modifier = Modifier.fillMaxSize().background(color = Color.Black)) {
            if (showOfflineScreen) {
                OfflineScreen (onRetry = { scope.launch { showOfflineScreen = !isOnline() } })
            } else if (!ingame) {
                val client = ktorClient()
                val scrollState = rememberScrollState()
                Column(
                    modifier = Modifier
                        .verticalScroll(scrollState)
                        .padding(vertical = 12.dp)
                ) {
                    for (region in listOf("na", "eu", "sa", "as", "ea", "oc")) {
                        ServerDisplay(
                            client,
                            region,
                            onPlay = {
                                showOfflineScreen = false
                                scope.launch {
                                    if (isOnline()) {
                                        ingame = true
                                    } else {
                                        showOfflineScreen = true
                                    }
                                }
                            }
                        )
                    }
                }
            }
            AnimatedVisibility(
                visible = ingame,
                modifier = Modifier.align(Alignment.Center),
                enter = fadeIn() + expandVertically (),
                exit = fadeOut() + shrinkVertically()
            ) {
                var showDialog by remember { mutableStateOf(false) }
                var dialogData by remember { mutableStateOf(DialogData()) }

                val showCustomDialog: (DialogData) -> Unit =
                    { data ->
                        dialogData = data.copy(
                            onConfirm = {
                                data.onConfirm(it)
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
                val webEngine = WebEngine(
                    "https://suroi.io/",
                    onURLChange = { ingame = false },
                    onDialog = showCustomDialog,
                )
                webEngine.executeJS("document.querySelector('.btn-kofi').style.display = 'none';")
                WebFrame(
                    modifier = Modifier.fillMaxWidth(),
                    webEngine = webEngine
                )
                if (showDialog) {
                    Dialog(
                        dialogData
                    )
                }
            }
            AnimatedVisibility(
                visible = !ingame,
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