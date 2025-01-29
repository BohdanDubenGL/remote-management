package com.globallogic.rdkb.remotemanagement.view.screen.home

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.globallogic.rdkb.remotemanagement.view.LocalNavController
import com.globallogic.rdkb.remotemanagement.view.Screen

@Composable
fun TopologyScreen(
    navController: NavController = LocalNavController.current,
) {
    Topology(
        onRouterDeviceSelected = { navController.navigate(Screen.RouterDeviceGraph.RouterDevice) },
        onRouterDeviceList = { navController.navigate(Screen.HomeGraph.RouterDeviceList) },
        onSettings = { navController.navigate(Screen.HomeGraph.Settings) }
    )
}

@Composable
private fun Topology(
    onRouterDeviceSelected: () -> Unit,
    onRouterDeviceList: () -> Unit,
    onSettings: () -> Unit
) {
    Column {
        Text(text = "Topology")
        Button(
            onClick = onRouterDeviceSelected,
            content = { Text(text = "RouterDetails") }
        )
        Button(
            onClick = onRouterDeviceList,
            content = { Text(text = "RouterDeviceList") }
        )
        Button(
            onClick = onSettings,
            content = { Text(text = "Settings") }
        )
    }
}
