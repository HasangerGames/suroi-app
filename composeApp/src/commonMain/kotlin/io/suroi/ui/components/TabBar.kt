package io.suroi.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import io.suroi.navigation.Screen
import io.suroi.ui.theme.Black
import io.suroi.ui.theme.White
import io.suroi.ui.theme.suroiTypography

@Composable
fun TabBar(navController: NavController) {
    val items = listOf(Screen.Main, Screen.Test, Screen.Duel)
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val selectedTabIndex = items.indexOfFirst { it.route == currentRoute }.takeIf { it >= 0 } ?: 0

    TabRow(
        selectedTabIndex = selectedTabIndex,
        divider = {},
        containerColor = Black,
        modifier = Modifier.padding(4.dp).fillMaxWidth()
    ) {
        items.forEachIndexed { index, screen ->
            val title = when (screen) {
                Screen.Main -> "Main"
                Screen.Duel -> "1v1"
                Screen.Test -> "Test"
                else -> "Other"
            }

            val isSelected = currentRoute == screen.route
            val backgroundColor = if (isSelected) White else White.copy(0.2f)
            val textColor =  if (isSelected) Black else White

            Tab(
                modifier = Modifier
                    .padding(vertical = 12.dp, horizontal = 6.dp)
                    .background(color = backgroundColor, shape = RoundedCornerShape(32.dp)),
                selected = currentRoute == screen.route,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                selectedContentColor = White,
                text = {
                    Text(
                        text = title,
                        style = suroiTypography().titleLarge,
                        color = textColor,
                    )
                }
            )
        }
    }
}
