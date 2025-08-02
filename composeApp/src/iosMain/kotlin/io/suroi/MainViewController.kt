@file:OptIn(ExperimentalForeignApi::class)

package io.suroi

import androidx.compose.runtime.remember
import androidx.compose.ui.window.ComposeUIViewController
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask
import platform.UIKit.UIApplication
import platform.UIKit.UIStatusBarAnimation
import platform.UIKit.setStatusBarHidden

fun MainViewController() = ComposeUIViewController {
    UIApplication.sharedApplication.setStatusBarHidden(
        true,
        UIStatusBarAnimation.UIStatusBarAnimationNone
    )
    App(
        datastore = remember {
            createDataStore {
                val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
                    directory = NSDocumentDirectory,
                    inDomain = NSUserDomainMask,
                    appropriateForURL = null,
                    create = false,
                    error = null
                )
                requireNotNull(documentDirectory).path + "/$DATASTORE_FILE_NAME"
            }
        }
    )
}