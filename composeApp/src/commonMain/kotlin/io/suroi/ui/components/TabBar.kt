package io.suroi.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import io.suroi.SVGImage
import io.suroi.navigation.Screen
import io.suroi.ui.theme.Black
import io.suroi.ui.theme.White
import io.suroi.ui.theme.suroiTypography
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.stringResource
import suroi.composeapp.generated.resources.Res
import suroi.composeapp.generated.resources.tab_1v1
import suroi.composeapp.generated.resources.tab_main
import suroi.composeapp.generated.resources.tab_test
import suroi.composeapp.generated.resources.unknown

@Composable
fun TabBar(navController: NavController) {
    val items = listOf(Screen.Main, Screen.Test, Screen.Duel)
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val selectedTabIndex = items.indexOfFirst { it.route == currentRoute }.takeIf { it >= 0 } ?: 0

    TabRow(
        selectedTabIndex = selectedTabIndex,
        divider = {},
        containerColor = Black,
        modifier = Modifier.padding(4.dp).fillMaxWidth(),
        indicator = { tabPositions ->
            val tabPosition = tabPositions[selectedTabIndex]
            Box(
                Modifier
                    .tabIndicatorOffset(tabPosition)
                    .height(3.dp)
                    .background(color = White, shape = RoundedCornerShape(4.dp))
            )
        }
    ) {
        items.forEachIndexed { index, screen ->
            val title = when (screen) {
                Screen.Main -> stringResource(Res.string.tab_main)
                Screen.Duel -> stringResource(Res.string.tab_1v1)
                Screen.Test -> stringResource(Res.string.tab_test)
                else -> stringResource(Res.string.unknown)
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
                content = {
                    androidx.compose.foundation.layout.Row(
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        SVGImage(
                            uri = Res.getUri("drawable/tab_${screen.route}.svg"),
                            resource = tabIcon(screen.route),
                            modifier = Modifier.size(24.dp),
                            color = textColor
                        )
                        Text(
                            text = title,
                            style = suroiTypography().titleMedium,
                            color = textColor,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            )
        }
    }
}

fun tabIcon(tab: String): DrawableResource {
    return when (tab) {
        "main" -> Res.drawable.tab_main
        "test" -> Res.drawable.tab_test
        "1v1" -> Res.drawable.tab_1v1
        else -> Res.drawable.tab_main
    }
}
