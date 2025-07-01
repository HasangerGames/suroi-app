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
                Webview(
                    "https://suroi.io",
                    modifier = Modifier.fillMaxSize(),
                    script = "document.querySelector('.btn-kofi').style.display = 'none';",
                    onURLChange = { showContent = false }
                )
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
                        onClick = {
                            println("settings clicked")
                        },
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