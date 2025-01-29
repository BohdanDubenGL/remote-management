package com.globallogic.rdkb.remotemanagement.view

import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.StringResource
import rdkbremotemanagement.app.generated.resources.Res
import rdkbremotemanagement.app.generated.resources.title

@Serializable
sealed interface Graph : Screen

@Serializable
sealed interface Screen {
    @Serializable data object RootGraph : Graph

    @Serializable data object Splash : Screen
    @Serializable data object Authentication : Screen

    @Serializable
    data object ConnectionGraph : Graph {
        @Serializable data object SearchRouterDevice : Screen
        @Serializable data object ConnectRouterDevice : Screen
    }

    @Serializable
    data object HomeGraph : Graph {
        @Serializable data object Topology : Screen
        @Serializable data object RouterDeviceList : Screen
        @Serializable data object Settings : Screen
        @Serializable data object ChangeAccountSettings : Screen
    }

    @Serializable
    data object RouterDeviceGraph : Graph {
        @Serializable data class RouterDevice(val routerDeviceMac: String) : Screen
        @Serializable data object Setup : Screen
        @Serializable data object ConnectedDevices : Screen
        @Serializable data object RouterSettings : Screen
    }
}

val Screen.title: StringResource? get() = when(this) {
    is Screen.RootGraph -> null

    is Screen.Splash -> null
    is Screen.Authentication -> Res.string.title

    is Screen.ConnectionGraph -> Res.string.title
    is Screen.ConnectionGraph.SearchRouterDevice -> Res.string.title
    is Screen.ConnectionGraph.ConnectRouterDevice -> Res.string.title

    is Screen.HomeGraph -> Res.string.title
    is Screen.HomeGraph.Topology -> Res.string.title
    is Screen.HomeGraph.RouterDeviceList -> Res.string.title
    is Screen.HomeGraph.Settings -> Res.string.title
    is Screen.HomeGraph.ChangeAccountSettings -> Res.string.title

    is Screen.RouterDeviceGraph -> Res.string.title
    is Screen.RouterDeviceGraph.RouterDevice -> Res.string.title
    is Screen.RouterDeviceGraph.ConnectedDevices -> Res.string.title
    is Screen.RouterDeviceGraph.Setup -> Res.string.title
    is Screen.RouterDeviceGraph.RouterSettings -> Res.string.title
}
