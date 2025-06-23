package io.suroi

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

import suroi.composeapp.generated.resources.Res
import suroi.composeapp.generated.resources.compose_multiplatform

@Composable
fun App() {
    // hideSystemUI(LocalPlatformContext.current)
    MaterialTheme {
        var showContent by remember { mutableStateOf(false) }
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

enum class DialogType {
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
    BasicAlertDialog({ abc = !abc }, modifier = modifier) {
        Text("Hi! This is an alert")
        // Button(onclick = )
    }
}