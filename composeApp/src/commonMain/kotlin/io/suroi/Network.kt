package io.suroi

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
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
    val playerCount: Int = 0,
    val teamMode: Int = 0,
    val mode: String = "Unknown"
)
suspend fun getServerInfo(client: HttpClient, url: String): ServerInfo {
    try {
        return client.get(url).body()
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