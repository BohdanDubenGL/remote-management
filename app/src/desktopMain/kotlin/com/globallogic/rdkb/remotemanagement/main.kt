package com.globallogic.rdkb.remotemanagement

import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.globallogic.rdkb.remotemanagement.di.initializeKoin
import com.globallogic.rdkb.remotemanagement.view.App

fun main() {
    initializeKoin()
    application {
        val windowState = rememberWindowState()
        windowState.size = DpSize(450.dp, 900.dp)
        windowState.position = WindowPosition(Alignment.BottomEnd)
        Window(
            state = windowState,
            onCloseRequest = ::exitApplication,
            title = "RdkB RemoteManagement",
//            resizable = false,
            content = { App() },
        )
    }
}
