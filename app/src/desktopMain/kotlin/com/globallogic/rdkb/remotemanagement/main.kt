package com.globallogic.rdkb.remotemanagement

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.globallogic.rdkb.remotemanagement.di.initializeKoin
import com.globallogic.rdkb.remotemanagement.view.App

fun main() {
    initializeKoin()
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "RdkB RemoteManagement",
            content = { App() },
        )
    }
}
