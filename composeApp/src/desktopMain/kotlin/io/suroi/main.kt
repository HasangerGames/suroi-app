package io.suroi

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import org.jetbrains.compose.resources.painterResource
import suroi.composeapp.generated.resources.Res
import suroi.composeapp.generated.resources.icon

fun main() = application {
    val datastore = createDataStore { DATASTORE_FILE_NAME }
    Window(
        onCloseRequest = ::exitApplication,
        icon = painterResource(Res.drawable.icon),
        title = "Suroi",
        state = WindowState(placement = WindowPlacement.Fullscreen),
    ) {
        App(
            datastore = datastore
        )
    }
}