package com.globallogic.rdkb.remotemanagement

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.unit.dp
import com.globallogic.rdkb.remotemanagement.data.permission.PermissionController
import com.globallogic.rdkb.remotemanagement.view.App

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val permissionController = PermissionController(this)

        enableEdgeToEdge()
        setContent { App(permissionController = permissionController, topBarHeight = 104.dp) }
    }
}
