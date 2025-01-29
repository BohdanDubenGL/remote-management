package com.globallogic.rdkb.remotemanagement.view.screen.routerdevice

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.globallogic.rdkb.remotemanagement.view.LocalNavController
import com.globallogic.rdkb.remotemanagement.view.Screen

@Composable
fun RouterSettingsScreen(
    navController: NavController = LocalNavController.current,
) {
    RouterSettings(
        onSetupRouterSettings = { navController.navigate(Screen.RouterDeviceGraph.Setup) },
        onConnectedDevices = { navController.navigate(Screen.RouterDeviceGraph.ConnectedDevices) },
        onBack = { navController.navigate(Screen.RouterDeviceGraph.RouterDevice) }
    )
}

@Composable
private fun RouterSettings(
    onSetupRouterSettings: () -> Unit,
    onConnectedDevices: () -> Unit,
    onBack: () -> Unit
) {
    Column {
        Text(text = "Settings")
        Button(
            onClick = onSetupRouterSettings,
            content = { Text(text = "Setup") }
        )
        Button(
            onClick = onConnectedDevices,
            content = { Text(text = "ConnectedDevices") }
        )
        Button(
            onClick = onBack,
            content = { Text(text = "Back") }
        )
    }
}
