package com.globallogic.rdkb.remotemanagement.view

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavController

val LocalNavController: ProvidableCompositionLocal<NavController> = staticCompositionLocalOf {
    error("Can't access NavController")
}
