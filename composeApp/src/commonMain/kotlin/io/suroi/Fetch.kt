package io.suroi

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import suroi.composeapp.generated.resources.Res
import suroi.composeapp.generated.resources.fall
import suroi.composeapp.generated.resources.infection
import suroi.composeapp.generated.resources.normal
import suroi.composeapp.generated.resources.winter

fun ktorClient(): HttpClient {
    return HttpClient {
        install(ContentNegotiation) {
            json(Json {
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
    }
}
@Serializable
data class ServerInfo(
    val protocolVersion: Int? = null,
    val playerCount: Int? = null,
    val teamSize: Int? = null,
    val nextTeamSize: Int? = null,
    val teamSizeSwitchTime: Long? = null,
    val mode: String,
    val nextMode: String? = null,
    val modeSwitchTime: Long? = null
)


suspend fun fetchGameMode(url: String): String {
    val client = ktorClient()
    try {
        val response: ServerInfo = client.get(url).body()
        return response.mode
    } finally {
        client.close()
    }
}

fun getBackgroundFromMode(mode: String): org.jetbrains.compose.resources.DrawableResource {
    return when (mode) {
        "normal" -> Res.drawable.normal
        "fall" -> Res.drawable.fall
        "infection" -> Res.drawable.infection
        "winter" -> Res.drawable.winter
        else -> Res.drawable.normal
    }
}