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

@Composable
fun App() {
    hideSystemUI(LocalPlatformContext.current)
    SuroiTheme {
        /* if (!isOnline(LocalPlatformContext.current)) {
            Text(text = "no internet")
            TODO, make a composable function for offline screen
        } */
        var showContent by remember { mutableStateOf(false) }
        var backgroundIsLoading by remember { mutableStateOf(true) }
        val backgroundImageURL = remember { mutableStateOf<String?>(null) }
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
        if (backgroundIsLoading) {
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
                contentScale = ContentScale.Crop,
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (!showContent) {
                Button(onClick = {
                    showContent = !showContent
                }) {
                    Text("Let's play")
                }
            }
            AnimatedVisibility(showContent) {
                Webview(
                    "https://suroi.io",
                    modifier = Modifier.fillMaxSize(),
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