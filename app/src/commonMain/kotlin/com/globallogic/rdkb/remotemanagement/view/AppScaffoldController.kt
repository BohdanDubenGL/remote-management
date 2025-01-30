package com.globallogic.rdkb.remotemanagement.view

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun appScaffoldController(): ScaffoldController = viewModel {
    ScaffoldController(
        Screen.HomeGraph::class to listOf(
            BottomBarRoute(Screen.HomeGraph.Topology, Screen.RootGraph),
            BottomBarRoute(Screen.HomeGraph.RouterDeviceList, Screen.RootGraph),
            BottomBarRoute(Screen.HomeGraph.Settings, Screen.RootGraph),
        ),
        Screen.RouterDeviceGraph::class to listOf(
            BottomBarRoute(Screen.RouterDeviceGraph.RouterDevice, Screen.RouterDeviceGraph),
            BottomBarRoute(Screen.RouterDeviceGraph.ConnectedDevices, Screen.RouterDeviceGraph),
            BottomBarRoute(Screen.RouterDeviceGraph.Setup, Screen.RouterDeviceGraph),
            BottomBarRoute(Screen.RouterDeviceGraph.RouterSettings, Screen.RouterDeviceGraph),
        ),
    )
}
