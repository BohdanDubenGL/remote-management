package com.globallogic.rdkb.remotemanagement

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.globallogic.rdkb.remotemanagement.di.initializeKoin
import com.globallogic.rdkb.remotemanagement.view.App
import kotlinx.browser.document

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    initializeKoin()
    ComposeViewport(document.body!!) {
        App()
    }
}