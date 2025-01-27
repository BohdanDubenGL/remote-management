package com.globallogic.rdkb.remotemanagement

import androidx.compose.ui.window.ComposeUIViewController
import com.globallogic.rdkb.remotemanagement.di.initializeKoin
import com.globallogic.rdkb.remotemanagement.view.App

fun MainViewController() = ComposeUIViewController(
    configure = { initializeKoin() },
    content = { App() }
)
