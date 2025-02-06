package com.globallogic.rdkb.remotemanagement.view.navigation

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf

val LocalScaffoldController: ProvidableCompositionLocal<ScaffoldController> = staticCompositionLocalOf {
    error("Can't access ScaffoldController")
}
