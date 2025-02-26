package com.globallogic.rdkb.remotemanagement.view.permission

import android.content.Context
import android.content.ContextWrapper
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.globallogic.rdkb.remotemanagement.data.permission.PermissionController

@Composable
actual fun rememberPermissionController(): PermissionController {
    val localPermissionController = runCatching { LocalPermissionController.current }.getOrNull()
    if (localPermissionController != null) return localPermissionController

    val context = LocalContext.current
    return remember(context) { PermissionController(context.findHostComponentActivity()) }
}

private fun Context.findHostComponentActivity(): ComponentActivity {
    return when(this) {
        is ComponentActivity -> this
        is ContextWrapper -> baseContext.findHostComponentActivity()
        else -> error("Can't find host ComponentActivity")
    }
}
