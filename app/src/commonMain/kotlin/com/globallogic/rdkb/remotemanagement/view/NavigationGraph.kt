package com.globallogic.rdkb.remotemanagement.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.createGraph
import com.globallogic.rdkb.remotemanagement.view.screen.SplashScreen
import com.globallogic.rdkb.remotemanagement.view.screen.connection.ConnectRouterDeviceScreen
import com.globallogic.rdkb.remotemanagement.view.screen.connection.SearchRouterDeviceScreen
import com.globallogic.rdkb.remotemanagement.view.screen.home.RouterDeviceListScreen
import com.globallogic.rdkb.remotemanagement.view.screen.home.SettingsScreen
import com.globallogic.rdkb.remotemanagement.view.screen.home.TopographyScreen
import com.globallogic.rdkb.remotemanagement.view.screen.login.LoginScreen
import com.globallogic.rdkb.remotemanagement.view.screen.login.RegistrationScreen
import com.globallogic.rdkb.remotemanagement.view.screen.routerdevice.ConnectedDeviceListScreen
import com.globallogic.rdkb.remotemanagement.view.screen.routerdevice.RouterDeviceScreen
import com.globallogic.rdkb.remotemanagement.view.screen.routerdevice.RouterSettingsScreen
import com.globallogic.rdkb.remotemanagement.view.screen.routerdevice.SetupRouterDeviceScreen

@Composable
fun rememberApplicationNavGraph(navController: NavController): NavGraph {
    return remember(navController) {
        navController.createGraph(route = Screen.RootGraph::class, startDestination = Screen.Splash) {
            composable<Screen.Splash> { SplashScreen(navController) }
            navigation<Screen.AutorizationGraph>(startDestination = Screen.AutorizationGraph.Login) {
                composable<Screen.AutorizationGraph.Login> { LoginScreen(navController) }
                composable<Screen.AutorizationGraph.Registration> { RegistrationScreen(navController) }
            }
            navigation<Screen.ConnectionGraph>(startDestination = Screen.ConnectionGraph.SearchRouterDevice) {
                composable<Screen.ConnectionGraph.SearchRouterDevice> { SearchRouterDeviceScreen(navController) }
                composable<Screen.ConnectionGraph.ConnectRouterDevice> { ConnectRouterDeviceScreen(navController) }
            }
            navigation<Screen.HomeGraph>(startDestination = Screen.HomeGraph.Topography) {
                composable<Screen.HomeGraph.Topography> { TopographyScreen(navController) }
                composable<Screen.HomeGraph.RouterDeviceList> { RouterDeviceListScreen(navController) }
                composable<Screen.HomeGraph.Settings> { SettingsScreen(navController) }
            }
            navigation<Screen.RouterDeviceGraph>(startDestination = Screen.RouterDeviceGraph.RouterDevice) {
                composable<Screen.RouterDeviceGraph.RouterDevice> { RouterDeviceScreen(navController) }
                composable<Screen.RouterDeviceGraph.Setup> { SetupRouterDeviceScreen(navController) }
                composable<Screen.RouterDeviceGraph.ConnectedDevices> { ConnectedDeviceListScreen(navController) }
                composable<Screen.RouterDeviceGraph.RouterSettings> { RouterSettingsScreen(navController) }
            }
        }
    }
}
