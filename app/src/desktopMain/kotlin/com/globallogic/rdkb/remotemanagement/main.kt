package com.globallogic.rdkb.remotemanagement

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.globallogic.rdkb.remotemanagement.view.App

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "RdkB RemoteManagement",
    ) {
        App()
    }
}