package com.globallogic.rdkb.remotemanagement.view.screen.routerdevice

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.globallogic.rdkb.remotemanagement.view.LocalNavController
import com.globallogic.rdkb.remotemanagement.view.Screen

@Composable
fun SetupRouterDeviceScreen(
    navController: NavController = LocalNavController.current,
) {
    SetupRouterDevice(
        onConnectedDevices = { navController.navigate(Screen.RouterDeviceGraph.ConnectedDevices) },
        onRouterSettings = { navController.navigate(Screen.RouterDeviceGraph.RouterSettings) },
        onBack = { navController.navigate(Screen.RouterDeviceGraph.RouterDevice) }
    )
}

@Composable
private fun SetupRouterDevice(
    onConnectedDevices: () -> Unit,
    onRouterSettings: () -> Unit,
    onBack: () -> Unit
) {
    Column {
        Text(text = "Setup")
        Button(
            onClick = onConnectedDevices,
            content = { Text(text = "ConnectedDevices") }
        )
        Button(
            onClick = onRouterSettings,
            content = { Text(text = "RouterSettings") }
        )
        Button(
            onClick = onBack,
            content = { Text(text = "Back") }
        )
    }
}
