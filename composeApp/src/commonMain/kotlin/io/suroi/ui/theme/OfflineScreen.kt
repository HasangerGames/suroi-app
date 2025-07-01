package io.suroi.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import suroi.composeapp.generated.resources.Res
import suroi.composeapp.generated.resources.offline

@Composable
fun OfflineScreen(onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(Res.drawable.offline),
            contentDescription = "offline",
            tint = White.copy(0.8f),
            modifier = Modifier.size(120.dp)
        )
        Text(
            text = "You're offline",
            modifier = Modifier.align(Alignment.CenterHorizontally),
            color = White,
            fontSize = 45.sp,
            fontFamily = russoOne()
        )
        Text(
            text = "Connect to the internet to play Suroi!",
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(16.dp),
            color = White.copy(0.8f),
            fontSize = 20.sp,
            fontFamily = inter()
        )
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}