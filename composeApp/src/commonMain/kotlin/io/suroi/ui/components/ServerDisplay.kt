package io.suroi.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import io.ktor.client.*
import io.suroi.SVGImage
import io.suroi.ServerInfo
import io.suroi.getServerInfo
import io.suroi.ui.theme.*
import org.jetbrains.compose.resources.DrawableResource
import suroi.composeapp.generated.resources.*

@Composable
fun ServerDisplay(httpClient: HttpClient, region: String, onPlay: () -> Unit) {
    var serverInfo by remember { mutableStateOf(ServerInfo()) }
    var loadingModeImage by remember { mutableStateOf(true) }
    LaunchedEffect(region) {
        try {
            loadingModeImage = true
            serverInfo = getServerInfo(httpClient, "https://$region.suroi.io/api/serverInfo")
        } catch (e: Exception) {
            println(e.toString())
        } finally {
            loadingModeImage = false
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
            contentDescription = "Game mode background",
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
            if (loadingModeImage) {
                Box(
                    modifier = Modifier.size(128.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = White,
                        strokeWidth = 4.dp,
                        modifier = Modifier.padding(24.dp).size(200.dp)
                    )
                }
            } else {
                SVGImage(
                    uri = getModeSVGUri(serverInfo.mode),
                    resource = gameModeImage(serverInfo.mode),
                    description = "Game mode icon",
                    modifier = Modifier.padding(8.dp).size(160.dp)
                )
            }
            Text(
                text = humanReadableRegion(region),
                color = White,
                style = suroiTypography().displayMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${serverInfo.playerCount} players · ${serverInfo.mode.replaceFirstChar { it.titlecase() }} · ${
                    humanReadableTeamMode(
                        serverInfo.teamMode
                    )
                }",
                color = White,
                fontFamily = inter(),
                style = suroiTypography().titleMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row {
                Button(
                    onClick = { onPlay() },
                    colors = ButtonDefaults.buttonColors(containerColor = White),
                    contentPadding = ButtonDefaults.ButtonWithIconContentPadding
                ) {
                    SVGImage(
                        uri = Res.getUri("drawable/play.svg"),
                        resource = Res.drawable.play,
                        description = "Play",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Play",
                        color = Dark,
                        style = suroiTypography().titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(
                    onClick = { },
                    colors = ButtonDefaults.buttonColors(containerColor = Gray.copy(alpha = 0.7f)),
                ) {
                    Text(
                        "Server info",
                        color = White,
                        style = suroiTypography().titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
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
        "test" -> "Test Server"
        else -> "Unknown"
    }
}

fun gameModeImage(mode: String): DrawableResource {
    return when (mode) {
        "birthday" -> Res.drawable.birthday
        "fall" -> Res.drawable.fall
        "halloween" -> Res.drawable.halloween
        "hunted" -> Res.drawable.hunted
        "infection" -> Res.drawable.infection
        "normal" -> Res.drawable.normal
        "winter" -> Res.drawable.winter
        else -> Res.drawable.normal
    }
}

fun getModeSVGUri(mode: String): String {
    val validModes = setOf("birthday", "fall", "halloween", "hunted", "infection", "normal", "winter")
    return Res.getUri("drawable/${if (mode in validModes) mode else "normal"}.svg")
}
