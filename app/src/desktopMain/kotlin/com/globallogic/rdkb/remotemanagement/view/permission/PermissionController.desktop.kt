package com.globallogic.rdkb.remotemanagement.view.permission

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.globallogic.rdkb.remotemanagement.data.permission.PermissionController

@Composable
actual fun rememberPermissionController(): PermissionController {
    return remember { PermissionController() }
}
