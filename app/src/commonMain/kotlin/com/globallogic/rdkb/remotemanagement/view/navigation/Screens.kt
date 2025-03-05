package com.globallogic.rdkb.remotemanagement.view.navigation

import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.StringResource
import rdkbremotemanagement.app.generated.resources.Res
import rdkbremotemanagement.app.generated.resources.screen_bottom_nav_title_connected_devices
import rdkbremotemanagement.app.generated.resources.screen_bottom_nav_title_router_device
import rdkbremotemanagement.app.generated.resources.screen_bottom_nav_title_router_device_list
import rdkbremotemanagement.app.generated.resources.screen_bottom_nav_title_router_device_setup
import rdkbremotemanagement.app.generated.resources.screen_bottom_nav_title_settings
import rdkbremotemanagement.app.generated.resources.screen_bottom_nav_title_topology
import rdkbremotemanagement.app.generated.resources.screen_title_add_router_device_manually
import rdkbremotemanagement.app.generated.resources.screen_title_change_account_settings
import rdkbremotemanagement.app.generated.resources.screen_title_connected_devices
import rdkbremotemanagement.app.generated.resources.screen_title_router_device
import rdkbremotemanagement.app.generated.resources.screen_title_router_device_list
import rdkbremotemanagement.app.generated.resources.screen_title_router_device_setup
import rdkbremotemanagement.app.generated.resources.screen_title_search_router_device
import rdkbremotemanagement.app.generated.resources.screen_title_settings
import rdkbremotemanagement.app.generated.resources.screen_title_topology

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
        @Serializable data object AddRouterDeviceManually : Screen
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
        @Serializable data object RouterDevice : Screen
        @Serializable data object Setup : Screen
        @Serializable data object ConnectedDevices : Screen
    }
}

fun getRouteTitle(route: String?): StringResource? = when (route) {
    null -> null

    routeString<Screen.ConnectionGraph.SearchRouterDevice>() -> Res.string.screen_title_search_router_device
    routeString<Screen.ConnectionGraph.AddRouterDeviceManually>() -> Res.string.screen_title_add_router_device_manually

    routeString<Screen.HomeGraph.Topology>() -> Res.string.screen_title_topology
    routeString<Screen.HomeGraph.RouterDeviceList>() -> Res.string.screen_title_router_device_list
    routeString<Screen.HomeGraph.Settings>() -> Res.string.screen_title_settings
    routeString<Screen.HomeGraph.ChangeAccountSettings>() -> Res.string.screen_title_change_account_settings

    routeString<Screen.RouterDeviceGraph.RouterDevice>() -> Res.string.screen_title_router_device
    routeString<Screen.RouterDeviceGraph.ConnectedDevices>() -> Res.string.screen_title_connected_devices
    routeString<Screen.RouterDeviceGraph.Setup>() -> Res.string.screen_title_router_device_setup

    else -> null
}

fun getRouteBottomBarTitle(route: String?): StringResource? = when (route) {
    null -> null

    routeString<Screen.ConnectionGraph.SearchRouterDevice>() -> Res.string.screen_title_search_router_device
    routeString<Screen.ConnectionGraph.AddRouterDeviceManually>() -> Res.string.screen_title_add_router_device_manually

    routeString<Screen.HomeGraph.Topology>() -> Res.string.screen_bottom_nav_title_topology
    routeString<Screen.HomeGraph.RouterDeviceList>() -> Res.string.screen_bottom_nav_title_router_device_list
    routeString<Screen.HomeGraph.Settings>() -> Res.string.screen_bottom_nav_title_settings

    routeString<Screen.RouterDeviceGraph.RouterDevice>() -> Res.string.screen_bottom_nav_title_router_device
    routeString<Screen.RouterDeviceGraph.ConnectedDevices>() -> Res.string.screen_bottom_nav_title_connected_devices
    routeString<Screen.RouterDeviceGraph.Setup>() -> Res.string.screen_bottom_nav_title_router_device_setup

    else -> null
}

private inline fun <reified S: Screen> routeString(): String = S::class.qualifiedName.orEmpty()
