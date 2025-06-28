package io.suroi

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext

actual typealias PlatformContext = Activity

@Composable
actual fun Webview(
    url: String,
    modifier: Modifier,
    script: String,
    onURLChange: (String) -> Unit
) {
    val context = LocalContext.current
    val isWearOS = context.packageManager.hasSystemFeature(PackageManager.FEATURE_WATCH)
            || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && Build.DEVICE.contains("wear"))
    val below10 = Build.VERSION.SDK_INT < Build.VERSION_CODES.R
    if (isWearOS || below10) {
        GeckoWebview(url, modifier, script, onURLChange)
    } else {
        AndroidWebview(url, modifier, script, onURLChange)
    }
}