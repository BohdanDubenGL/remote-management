package com.globallogic.rdkb.remotemanagement.view.permission

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import com.globallogic.rdkb.remotemanagement.data.permission.PermissionController

val LocalPermissionController: ProvidableCompositionLocal<PermissionController> = staticCompositionLocalOf {
    error("Can't access PermissionController")
}
