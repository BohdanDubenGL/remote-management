package com.globallogic.rdkb.remotemanagement.view.screen.routerdevice

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.globallogic.rdkb.remotemanagement.view.Screen
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun SetupRouterDeviceScreen(navController: NavController) {
    SetupRouterDevice(
        onConnectedDevices = { navController.navigate(Screen.RouterDeviceGraph.ConnectedDevices) },
        onRouterSettings = { navController.navigate(Screen.RouterDeviceGraph.RouterSettings) },
        onBack = { navController.navigate(Screen.RouterDeviceGraph) }
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

@Preview
@Composable
private fun SetupRouterDevicePreview() {
    SetupRouterDevice(onConnectedDevices = { }, onRouterSettings = { }, onBack = { })
}
