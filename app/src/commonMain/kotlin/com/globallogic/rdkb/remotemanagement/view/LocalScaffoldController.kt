package com.globallogic.rdkb.remotemanagement.view

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavController

val LocalScaffoldController: ProvidableCompositionLocal<ScaffoldController> = staticCompositionLocalOf {
    error("Can't access ScaffoldController")
}
