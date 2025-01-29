package com.globallogic.rdkb.remotemanagement.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.createGraph
import androidx.navigation.toRoute
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
fun rememberApplicationNavGraph(): NavGraph {
    val navController: NavController = LocalNavController.current
    return remember(navController) {
        navController.createGraph(route = Screen.RootGraph::class, startDestination = Screen.Splash) {
            composable<Screen.Splash> { SplashScreen() }
            composable<Screen.Authentication> { AuthenticationScreen() }

            composable<Screen.ConnectionGraph.SearchRouterDevice> { SearchRouterDeviceScreen() }
            composable<Screen.ConnectionGraph.ConnectRouterDevice> { ConnectRouterDeviceScreen() }

            composable<Screen.HomeGraph.Topology> { TopologyScreen() }
            composable<Screen.HomeGraph.RouterDeviceList> { RouterDeviceListScreen() }
            composable<Screen.HomeGraph.Settings> { SettingsScreen() }
            composable<Screen.HomeGraph.ChangeAccountSettings> { ChangeAccountSettingsScreen() }

            navigation<Screen.RouterDeviceGraph>(startDestination = Screen.RouterDeviceGraph.RouterDevice::class) {
                composable<Screen.RouterDeviceGraph.RouterDevice> { RouterDeviceScreen() }
                composable<Screen.RouterDeviceGraph.Setup> { SetupRouterDeviceScreen() }
                composable<Screen.RouterDeviceGraph.ConnectedDevices> { ConnectedDeviceListScreen() }
                composable<Screen.RouterDeviceGraph.RouterSettings> { RouterSettingsScreen() }
            }
        }
    }
}
