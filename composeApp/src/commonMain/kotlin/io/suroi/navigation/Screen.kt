package io.suroi.navigation

sealed class Screen(val route: String) {
    object Main : Screen("main")
    object Duel : Screen("1v1")
    object Test : Screen("test")
    object Game : Screen("game/{realm}/{region}") {
        fun play(realm: String, region: String): String = "game/$realm/$region"
    }
    object Settings : Screen("settings")
}