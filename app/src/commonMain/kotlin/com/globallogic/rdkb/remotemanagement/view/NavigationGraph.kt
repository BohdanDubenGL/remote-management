package com.globallogic.rdkb.remotemanagement.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.compose.composable
import androidx.navigation.createGraph
import com.globallogic.rdkb.remotemanagement.view.screen.splash.SplashScreen
import com.globallogic.rdkb.remotemanagement.view.screen.connection.ConnectRouterDeviceScreen
import com.globallogic.rdkb.remotemanagement.view.screen.connection.SearchRouterDeviceScreen
import com.globallogic.rdkb.remotemanagement.view.screen.home.RouterDeviceListScreen
import com.globallogic.rdkb.remotemanagement.view.screen.home.SettingsScreen
import com.globallogic.rdkb.remotemanagement.view.screen.authentication.AuthenticationScreen
import com.globallogic.rdkb.remotemanagement.view.screen.home.ChangeAccountSettingsScreen
import com.globallogic.rdkb.remotemanagement.view.screen.home.TopologyScreen
import com.globallogic.rdkb.remotemanagement.view.screen.routerdevice.ConnectedDeviceListScreen
import com.globallogic.rdkb.remotemanagement.view.screen.routerdevice.RouterDeviceScreen
import com.globallogic.rdkb.remotemanagement.view.screen.routerdevice.RouterSettingsScreen
import com.globallogic.rdkb.remotemanagement.view.screen.routerdevice.SetupRouterDeviceScreen

@Composable
fun rememberApplicationNavGraph(navController: NavController): NavGraph {
    return remember(navController) {
        navController.createGraph(route = Screen.RootGraph::class, startDestination = Screen.Splash) {
            composable<Screen.Splash> { SplashScreen(navController) }
            composable<Screen.Authentication> { AuthenticationScreen(navController) }

            composable<Screen.ConnectionGraph.SearchRouterDevice> { SearchRouterDeviceScreen(navController) }
            composable<Screen.ConnectionGraph.ConnectRouterDevice> { ConnectRouterDeviceScreen(navController) }

            composable<Screen.HomeGraph.Topology> { TopologyScreen(navController) }
            composable<Screen.HomeGraph.RouterDeviceList> { RouterDeviceListScreen(navController) }
            composable<Screen.HomeGraph.Settings> { SettingsScreen(navController) }
            composable<Screen.HomeGraph.ChangeAccountSettings> { ChangeAccountSettingsScreen(navController) }

            composable<Screen.RouterDeviceGraph.RouterDevice> { RouterDeviceScreen(navController) }
            composable<Screen.RouterDeviceGraph.Setup> { SetupRouterDeviceScreen(navController) }
            composable<Screen.RouterDeviceGraph.ConnectedDevices> { ConnectedDeviceListScreen(navController) }
            composable<Screen.RouterDeviceGraph.RouterSettings> { RouterSettingsScreen(navController) }
        }
    }
}
