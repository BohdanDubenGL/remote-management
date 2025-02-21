package com.globallogic.rdkb.remotemanagement.view.permission

import androidx.compose.runtime.Composable
import com.globallogic.rdkb.remotemanagement.data.permission.PermissionController

@Composable
expect fun rememberPermissionController(): PermissionController
