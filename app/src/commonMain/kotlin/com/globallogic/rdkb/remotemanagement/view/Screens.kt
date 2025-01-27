package com.globallogic.rdkb.remotemanagement.view

import kotlinx.serialization.Serializable

interface Graph : Screen

interface Screen {
    @Serializable data object RootGraph : Graph

    @Serializable data object Splash : Screen

    @Serializable
    data object AutorizationGraph : Graph {
        @Serializable data object Login : Screen
        @Serializable data object Registration : Screen
    }

    @Serializable
    data object ConnectionGraph : Graph {
        @Serializable data object SearchRouterDevice : Screen
        @Serializable data object ConnectRouterDevice : Screen
    }

    @Serializable
    data object HomeGraph : Graph {
        @Serializable data object Topography : Screen
        @Serializable data object RouterDeviceList : Screen
        @Serializable data object Settings : Screen
    }

    @Serializable
    data object RouterDeviceGraph : Graph {
        @Serializable data object RouterDevice : Screen
        @Serializable data object Setup : Screen
        @Serializable data object ConnectedDevices : Screen
        @Serializable data object RouterSettings : Screen
    }
}
