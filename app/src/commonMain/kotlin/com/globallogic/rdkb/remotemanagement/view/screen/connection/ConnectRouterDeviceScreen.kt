package com.globallogic.rdkb.remotemanagement.view.screen.connection

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.globallogic.rdkb.remotemanagement.view.Screen
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ConnectRouterDeviceScreen(navController: NavController) {
    ConnectRouterDevice(
        onRouterDeviceConnected = { navController.navigate(Screen.HomeGraph) }
    )
}

@Composable
private fun ConnectRouterDevice(
    onRouterDeviceConnected: () -> Unit
) {
    Column {
        Text(text = "ConnectRouterDevice")
        Button(
            onClick = onRouterDeviceConnected,
            content = { Text(text = "Home") }
        )
    }
}

@Preview
@Composable
private fun ConnectRouterDevicePreview() {
    ConnectRouterDevice(onRouterDeviceConnected = { })
}
