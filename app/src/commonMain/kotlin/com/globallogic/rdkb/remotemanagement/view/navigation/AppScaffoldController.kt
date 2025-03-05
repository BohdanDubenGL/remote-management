package com.globallogic.rdkb.remotemanagement.view.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.PhonelinkSetup
import androidx.compose.material.icons.filled.Router
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Devices
import androidx.compose.material.icons.outlined.Hub
import androidx.compose.material.icons.outlined.PhonelinkSetup
import androidx.compose.material.icons.outlined.Router
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun appScaffoldController(): ScaffoldController = viewModel {
    ScaffoldController(
        Screen.HomeGraph::class to listOf(
            BottomBarRoute(Screen.HomeGraph.Topology, Screen.RootGraph, Icons.Filled.Hub, Icons.Outlined.Hub),
            BottomBarRoute(Screen.HomeGraph.RouterDeviceList, Screen.RootGraph, Icons.Filled.Router, Icons.Outlined.Router),
            BottomBarRoute(Screen.HomeGraph.Settings, Screen.RootGraph, Icons.Filled.Settings, Icons.Outlined.Settings),
        ),
        Screen.RouterDeviceGraph::class to listOf(
            BottomBarRoute(Screen.RouterDeviceGraph.RouterDevice, Screen.RouterDeviceGraph, Icons.Filled.Router, Icons.Outlined.Router),
            BottomBarRoute(Screen.RouterDeviceGraph.ConnectedDevices, Screen.RouterDeviceGraph, Icons.Filled.Devices, Icons.Outlined.Devices),
            BottomBarRoute(Screen.RouterDeviceGraph.Setup, Screen.RouterDeviceGraph, Icons.Filled.PhonelinkSetup, Icons.Outlined.PhonelinkSetup),
        ),
    )
}
