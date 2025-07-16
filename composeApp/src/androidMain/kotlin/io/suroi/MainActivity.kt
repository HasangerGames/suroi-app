package io.suroi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.remember
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val systemUI = WindowCompat.getInsetsController(window, window.decorView)
        systemUI.hide(WindowInsetsCompat.Type.systemBars())
        systemUI.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        setContent {
            App(
                datastore = remember {
                    createDataStore(applicationContext)
                }
            )
        }

    }
}