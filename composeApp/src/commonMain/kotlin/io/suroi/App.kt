package io.suroi

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import io.suroi.ui.theme.SuroiTheme
import kotlinx.coroutines.launch

@Composable
fun App() {
    SuroiTheme {
        var showContent by remember { mutableStateOf(false) }
        var backgroundIsLoading by remember { mutableStateOf(true) }
        val backgroundImageURL = remember { mutableStateOf<String?>(null) }

        var connecting by remember { mutableStateOf(false) }
        var showOfflineScreen by remember { mutableStateOf(false) }
        val coroutineScope = rememberCoroutineScope()
        LaunchedEffect(Unit) {
            try {
                val mode = fetchGameMode("https://na.suroi.io/api/serverInfo")
                backgroundImageURL.value = "https://suroi.io/img/backgrounds/menu/${mode}.png"
            } catch (e: Exception) {
                println("Error fetching mode: ${e.message}")
            } finally {
                backgroundIsLoading = false
            }
        }
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
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (showOfflineScreen) {
                Text(text = "You're offline")
                Button(onClick = {
                    showOfflineScreen = false
                }) {
                    Text("Retry")
                }
            } else if (!showContent) {
                Button(
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
                    Text("Let's play")
                }
            }
            AnimatedVisibility(showContent) {
                Webview(
                    "https://suroi.io",
                    modifier = Modifier.fillMaxSize(),
                    script = "document.querySelector('.btn-kofi').style.display = 'none';",
                    onURLChange = { showContent = false }
                )
            }
        }
    }
}

/* enum class DialogType { todo
    ALERT,
    CONFIRM,
    PROMPT,
    UNLOAD
}
@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun Dialog(modifier: Modifier = Modifier) {
    var abc by remember { mutableStateOf(false) }
    BasicAlertDialog({ abc = !abc }, modifier = modifier) {}
} */