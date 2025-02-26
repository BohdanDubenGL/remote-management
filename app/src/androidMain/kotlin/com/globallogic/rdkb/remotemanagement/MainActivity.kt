package com.globallogic.rdkb.remotemanagement

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import com.globallogic.rdkb.remotemanagement.data.permission.PermissionController
import com.globallogic.rdkb.remotemanagement.view.App
import com.globallogic.rdkb.remotemanagement.view.permission.LocalPermissionController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val permissionController = PermissionController(this)

        enableEdgeToEdge()
        setContent {
            CompositionLocalProvider(
                LocalPermissionController provides permissionController,
            ) {
                App()
            }
        }
    }
}
