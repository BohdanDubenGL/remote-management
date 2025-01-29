package com.globallogic.rdkb.remotemanagement.view.screen.routerdevice

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.globallogic.rdkb.remotemanagement.view.LocalNavController
import com.globallogic.rdkb.remotemanagement.view.Screen

@Composable
fun ConnectedDeviceListScreen(
    navController: NavController = LocalNavController.current,
) {
    ConnectedDeviceList(
        onSetupConnectedDeviceList = { navController.navigate(Screen.RouterDeviceGraph.Setup) },
        onRouterSettings = { navController.navigate(Screen.RouterDeviceGraph.RouterSettings) },
        onBack = { navController.navigate(Screen.RouterDeviceGraph.RouterDevice) }
    )
}

@Composable
private fun ConnectedDeviceList(
    onSetupConnectedDeviceList: () -> Unit,
    onRouterSettings: () -> Unit,
    onBack: () -> Unit
) {
    Column {
        Text(text = "ConnectedDevices")
        Button(
            onClick = onSetupConnectedDeviceList,
            content = { Text(text = "Setup") }
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
