package io.suroi

import androidx.compose.ui.Modifier
import platform.UIKit.UIDevice
import platform.WebKit.WKWebView

class IOSPlatform: Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}

actual fun getPlatform(): Platform = IOSPlatform()