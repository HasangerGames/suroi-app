package io.suroi

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

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

suspend fun isOnline(): Boolean {
    val client = ktorClient()
    return try {
        val response = client.head("https://suroi.io")
        response.status.isSuccess()
    } catch (e: Exception) {
        false
    } finally {
        client.close()
    }
}