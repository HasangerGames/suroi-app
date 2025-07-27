package io.suroi.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import io.ktor.client.*
import io.suroi.ServerInfo
import io.suroi.getServerInfo
import io.suroi.ui.theme.Black
import io.suroi.ui.theme.Dark
import io.suroi.ui.theme.Gray
import io.suroi.ui.theme.White
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import suroi.composeapp.generated.resources.Res
import suroi.composeapp.generated.resources.play

@Composable
fun ServerDisplay(httpClient: HttpClient, region: String, onPlay: () -> Unit) {
    val scope = rememberCoroutineScope()
    var serverInfo by remember { mutableStateOf(ServerInfo()) }
    scope.launch {
        try {
        serverInfo = getServerInfo(httpClient, "https://$region.suroi.io/api/serverInfo")
        } catch (e: Exception) {
            println(e.toString())
        }
    }
    Box(
        modifier = Modifier
            .padding(horizontal = 24.dp, vertical = 12.dp)
            .clip(RoundedCornerShape(24.dp))
            .border(
                width = 3.dp,
                color = White.copy(alpha = 0.2f),
                shape = RoundedCornerShape(24.dp)
            )
    ) {
        AsyncImage(
            model = "https://suroi.io/img/backgrounds/menu/${serverInfo.mode}.png",
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .matchParentSize()
                .clip(RoundedCornerShape(24.dp))
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Black.copy(alpha = 0.95f),
                            Black.copy(alpha = 0.1f),
                        )
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
        )

        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()

        ) {
            Spacer(modifier = Modifier.height(96.dp))
            Text(
                text = humanReadableRegion(region),
                color = White,
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "${serverInfo.playerCount} players · ${serverInfo.mode.replaceFirstChar { it.titlecase() }} · ${
                    humanReadableTeamMode(
                        serverInfo.teamMode
                    )
                }",
                color = White,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row {
                Button(
                    onClick = { onPlay() },
                    colors = ButtonDefaults.buttonColors(containerColor = White),
                    contentPadding = ButtonDefaults.ButtonWithIconContentPadding
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.play),
                        contentDescription = "Play",
                        tint = Dark,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Play", color = Dark, style = MaterialTheme.typography.titleMedium)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(
                    onClick = { },
                    colors = ButtonDefaults.buttonColors(containerColor = Gray.copy(alpha = 0.7f)),
                ) {
                    Text("Server info", color = White, style = MaterialTheme.typography.titleMedium)
                }
            }
        }

    }
}

fun humanReadableTeamMode(teamMode: Int): String {
    return when (teamMode) {
        1 -> "Solo"
        2 -> "Duos"
        4 -> "Squads"
        else -> "Unknown"
    }
}

fun humanReadableRegion(region: String): String {
    return when (region) {
        "na" -> "North America"
        "sa" -> "South America"
        "eu" -> "Europe"
        "as" -> "Asia"
        "ea" -> "East Asia"
        "oc" -> "Oceania"
        "1v1" -> "1v1 North America"
        "ea1v1" -> "1v1 East Asia"
        "test" -> "Test"
        else -> "Unknown"
    }
}
