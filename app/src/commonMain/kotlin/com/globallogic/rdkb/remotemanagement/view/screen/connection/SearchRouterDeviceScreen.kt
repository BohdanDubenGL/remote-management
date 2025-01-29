package com.globallogic.rdkb.remotemanagement.view.screen.connection

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.globallogic.rdkb.remotemanagement.view.Screen

@Composable
fun SearchRouterDeviceScreen(navController: NavController) {
    SearchRouterDevice(
        onRouterDeviceFound = { navController.navigate(Screen.ConnectionGraph.ConnectRouterDevice) }
    )
}

@Composable
private fun SearchRouterDevice(
    onRouterDeviceFound: () -> Unit
) {
    Column {
        Text(text = "SearchRouterDevice")
        Button(
            onClick = onRouterDeviceFound,
            content = { Text(text = "Connect") }
        )
    }
}
