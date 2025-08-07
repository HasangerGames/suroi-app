package io.suroi.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.stringResource
import suroi.composeapp.generated.resources.*
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Composable
fun ServerDisplay(httpClient: HttpClient, region: String, onPlay: () -> Unit) {
    var serverInfo by remember { mutableStateOf(ServerInfo()) }
    var loadingModeImage by remember { mutableStateOf(true) }
    var showServerInfo by remember { mutableStateOf(false) }
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
                if (serverInfo.nextTeamMode != null || serverInfo.nextMode != null) {
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(
                        onClick = {
                            showServerInfo = !showServerInfo
                        },
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
            AnimatedVisibility (
                showServerInfo
            ) {
                Column {
                    Spacer(modifier = Modifier.height(16.dp))
                    if (serverInfo.nextTeamMode != null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = stringResource(
                                    Res.string.info_next_team_mode,
                                    humanReadableTeamMode(serverInfo.nextTeamMode!!)
                                ),
                                color = White.copy(alpha = 0.9f),
                                style = suroiTypography().titleMedium,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            SVGImage(
                                uri = Res.getUri("drawable/teammode_${serverInfo.nextTeamMode}.svg"),
                                resource = teamModeImage(serverInfo.nextTeamMode!!),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    if (serverInfo.teamModeSwitchTime != null) {
                        Text(
                            text = "Switch time: ${formatUnixTimestamp(serverInfo.teamModeSwitchTime!!)}",
                            color = White.copy(alpha = 0.7f),
                            style = suroiTypography().bodyMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    if (serverInfo.nextMode != null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = stringResource(
                                    Res.string.info_next_game_mode,
                                    humanReadableGameMode(serverInfo.nextMode!!)
                                ),
                                color = White.copy(alpha = 0.9f),
                                style = suroiTypography().titleMedium,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            SVGImage(
                                uri = Res.getUri("drawable/mode_${serverInfo.nextMode}.svg"),
                                resource = gameModeImage(serverInfo.nextMode!!),
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                    if (serverInfo.modeSwitchTime != null) {
                        Text(
                            text = "Switch time: ${formatUnixTimestamp(serverInfo.modeSwitchTime!!)}",
                            color = White.copy(alpha = 0.7f),
                            style = suroiTypography().bodyMedium
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalTime::class)
private fun formatUnixTimestamp(unixTimestamp: Long): String {
    return try {
        val instant = Instant.fromEpochMilliseconds(unixTimestamp + Clock.System.now().toEpochMilliseconds())
        val timeZone: TimeZone = TimeZone.currentSystemDefault()
        val localDateTime: LocalDateTime = instant.toLocalDateTime(timeZone)

        val year = localDateTime.year.toString().takeLast(2)
        val month = localDateTime.month.number
        val day = localDateTime.day
        val hour = localDateTime.hour
        val minute = localDateTime.minute

        val ampm = if (hour >= 12) "PM" else "AM"
        val displayHour = if (hour == 0) 12 else if (hour > 12) hour - 12 else hour
        val displayMinute = minute.toString().padStart(2, '0')

        "$month/$day/$year $displayHour:$displayMinute $ampm"
    } catch (_: Exception) {
        "unknown"
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

fun teamModeImage(teamMode: Int): DrawableResource {
    return when (teamMode) {
        1 -> Res.drawable.teammode_1
        2 -> Res.drawable.teammode_2
        4 -> Res.drawable.teammode_4
        else -> Res.drawable.teammode_1
    }
}
