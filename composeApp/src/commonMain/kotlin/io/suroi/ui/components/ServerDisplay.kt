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
import org.jetbrains.compose.resources.stringResource
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
                color = White.copy(alpha = 0.3f),
                shape = RoundedCornerShape(24.dp)
            )
    ) {
        val gamemode = serverInfo.mode
        AsyncImage(
            model = "https://suroi.io/img/backgrounds/menu/$gamemode.png",
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
                    modifier = Modifier.padding(8.dp).size(160.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = White,
                        strokeWidth = 4.dp,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            } else {
                SVGImage(
                    uri = Res.getUri("drawable/mode_$gamemode.svg"),
                    resource = gameModeImage(gamemode),
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
                text = stringResource(
                    Res.string.server_details,
                    serverInfo.playerCount,
                    humanReadableGameMode(gamemode),
                    humanReadableTeamMode(serverInfo.teamMode)
                ),
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
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        stringResource(Res.string.button_play),
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
                        stringResource(Res.string.button_server_info),
                        color = White,
                        style = suroiTypography().titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

    }
}

@Composable
fun humanReadableTeamMode(teamMode: Int): String {
    return when (teamMode) {
        1 -> stringResource(Res.string.team_mode_solo)
        2 -> stringResource(Res.string.team_mode_duos)
        4 -> stringResource(Res.string.team_mode_squads)
        else -> stringResource(Res.string.unknown)
    }
}

@Composable
fun humanReadableRegion(region: String): String {
    return when (region) {
        "na" -> stringResource(Res.string.region_na)
        "sa" -> stringResource(Res.string.region_sa)
        "eu" -> stringResource(Res.string.region_eu)
        "as" -> stringResource(Res.string.region_as)
        "ea" -> stringResource(Res.string.region_ea)
        "oc" -> stringResource(Res.string.region_oc)
        "1v1" -> stringResource(Res.string.region_1v1)
        "ea1v1" -> stringResource(Res.string.region_ea1v1)
        "test" -> stringResource(Res.string.region_test)
        else -> stringResource(Res.string.unknown)
    }
}

@Composable
fun humanReadableGameMode(mode: String): String {
    return when (mode) {
        "birthday" -> stringResource(Res.string.gamemode_birthday)
        "fall" -> stringResource(Res.string.gamemode_fall)
        "halloween" -> stringResource(Res.string.gamemode_halloween)
        "hunted" -> stringResource(Res.string.gamemode_hunted)
        "infection" -> stringResource(Res.string.gamemode_infection)
        "normal" -> stringResource(Res.string.gamemode_normal)
        "winter" -> stringResource(Res.string.gamemode_winter)
        else -> stringResource(Res.string.unknown)
    }
}

fun gameModeImage(mode: String): DrawableResource {
    return when (mode) {
        "birthday" -> Res.drawable.mode_birthday
        "fall" -> Res.drawable.mode_fall
        "halloween" -> Res.drawable.mode_halloween
        "hunted" -> Res.drawable.mode_hunted
        "infection" -> Res.drawable.mode_infection
        "normal" -> Res.drawable.mode_normal
        "winter" -> Res.drawable.mode_winter
        else -> Res.drawable.mode_unknown
    }
}
