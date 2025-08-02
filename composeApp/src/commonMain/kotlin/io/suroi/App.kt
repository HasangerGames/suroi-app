package io.suroi

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
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
        Scaffold { innerPadding ->

            NavHost(navController, startDestination = Screen.Main.route, modifier = Modifier.padding(innerPadding)) {
                composable(Screen.Main.route) {
                    ServerListScreen(
                        regions = listOf("na", "eu", "sa", "as", "ea", "oc"),
                        onPlay = { region ->
                            navController.navigate(Screen.Game.play("suroi.io", region))
                        },
                        navController = navController
                    )
                }
                composable(Screen.Duel.route) {
                    ServerListScreen(
                        regions = listOf("1v1", "ea1v1"),
                        onPlay = { region ->
                            navController.navigate(Screen.Game.play(
                                realm = "1v1.suroi.io",
                                region = when(region) {
                                    "1v1" -> "na"
                                    "ea1v1" -> "ea"
                                    else -> region
                                }
                            ))
                        },
                        navController = navController
                    )
                }
                composable(Screen.Test.route) {
                    ServerListScreen(
                        regions = listOf("test"),
                        onPlay = { region ->
                            navController.navigate(Screen.Game.play("test.suroi.io", region))
                        },
                        navController = navController
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
}