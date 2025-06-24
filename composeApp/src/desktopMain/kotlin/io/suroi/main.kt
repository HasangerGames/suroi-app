package io.suroi

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Suroi",
        state = WindowState(placement = WindowPlacement.Maximized),
        undecorated = true,
    ) {
        val desktopPlatformContext = PlatformContext()
        CompositionLocalProvider(LocalPlatformContext provides desktopPlatformContext) {
            App()
        }
    }
}