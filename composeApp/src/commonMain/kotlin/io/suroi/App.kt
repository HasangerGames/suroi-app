package io.suroi

import androidx.compose.runtime.Composable
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.suroi.navigation.Screen
import io.suroi.ui.components.GameScreen
import io.suroi.ui.components.ServerListScreen
import io.suroi.ui.theme.SuroiTheme

@Composable
fun App(
    datastore: DataStore<Preferences>,
) {
    val navController = rememberNavController()
    SuroiTheme {
        NavHost(navController, startDestination = Screen.Main.route) {
            composable(Screen.Main.route) {
                ServerListScreen(
                    regions = listOf("na", "eu", "sa", "as", "ea", "oc"),
                    onPlay = { region ->
                        navController.navigate(Screen.Game.play("suroi.io", region))
                    }
                )
            }
            composable(Screen.Game.route) { backStackEntry ->
                val realm = backStackEntry.savedStateHandle.get<String>("realm")!!
                val region = backStackEntry.savedStateHandle.get<String>("region")!!
                GameScreen({}, realm, region)
            }
        }
    }
}