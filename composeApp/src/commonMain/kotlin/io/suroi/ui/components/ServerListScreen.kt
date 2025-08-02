package io.suroi.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import io.suroi.ktorClient
import io.suroi.ui.theme.Black

@Composable
fun ServerListScreen(
    regions: List<String>,
    // datastore: DataStore<Preferences>,
    onPlay: (region: String) -> Unit,
    navController: NavController
) {
    val client = ktorClient()
    val scrollState = rememberScrollState()

    Box(modifier = Modifier.fillMaxSize().background(Black)) {
        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .padding(vertical = 12.dp)
        ) {
            TabBar(navController = navController)
            for (region in regions) {
                ServerDisplay(
                    httpClient = client,
                    region = region,
                    onPlay = { onPlay(region) }
                )
            }
        }
    }
}
