package io.suroi

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key.Companion.R
import androidx.compose.ui.layout.ContentScale
import org.jetbrains.compose.resources.painterResource
import suroi.composeapp.generated.resources.Res
import suroi.composeapp.generated.resources.normal

@Composable
fun App() {
    hideSystemUI(LocalPlatformContext.current)
    MaterialTheme {
        /* if (!isOnline(LocalPlatformContext.current)) {
            Text(text = "no internet")
            TODO, make a composable function for offline screen
        } */
        var showContent by remember { mutableStateOf(false) }
        val backgroundImage = remember { mutableStateOf(Res.drawable.normal) }
        LaunchedEffect(Unit) {
            try {
                val mode = fetchGameMode("https://na.suroi.io/api/serverInfo")
                backgroundImage.value = getBackgroundFromMode(mode)
            } catch (e: Exception) {
                println("Error fetching mode: ${e.message}")
            }
        }
        Image(
            painter = painterResource(backgroundImage.value),
            contentDescription = "Background by mode",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
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
                Webview("https://suroi.io", modifier = Modifier.fillMaxSize(), onUrlChange = {})
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