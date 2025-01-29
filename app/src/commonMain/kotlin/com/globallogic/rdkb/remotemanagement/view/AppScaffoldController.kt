package com.globallogic.rdkb.remotemanagement.view

fun appScaffoldController(): ScaffoldController = ScaffoldController(
    Screen.HomeGraph to listOf(
        BottomBarRoute(Screen.HomeGraph.Topology),
        BottomBarRoute(Screen.HomeGraph.RouterDeviceList),
        BottomBarRoute(Screen.HomeGraph.Settings)
    ),
    Screen.RouterDeviceGraph to listOf(
        BottomBarRoute(Screen.RouterDeviceGraph.RouterDevice("")),
        BottomBarRoute(Screen.RouterDeviceGraph.ConnectedDevices),
        BottomBarRoute(Screen.RouterDeviceGraph.Setup),
        BottomBarRoute(Screen.RouterDeviceGraph.RouterSettings),
    ),
)
