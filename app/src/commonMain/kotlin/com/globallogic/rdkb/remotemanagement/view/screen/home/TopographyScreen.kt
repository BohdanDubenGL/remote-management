package com.globallogic.rdkb.remotemanagement.view.screen.home

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.globallogic.rdkb.remotemanagement.view.Screen
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun TopographyScreen(navController: NavController) {
    Topography(
        onRouterDeviceSelected = { navController.navigate(Screen.RouterDeviceGraph) },
        onRouterDeviceList = { navController.navigate(Screen.HomeGraph.RouterDeviceList) },
        onSettings = { navController.navigate(Screen.HomeGraph.Settings) }
    )
}

@Composable
private fun Topography(
    onRouterDeviceSelected: () -> Unit,
    onRouterDeviceList: () -> Unit,
    onSettings: () -> Unit
) {
    Column {
        Text(text = "Topography")
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

@Preview
@Composable
private fun TopographyPreview() {
    Topography(onRouterDeviceSelected = { }, onRouterDeviceList = { }, onSettings = { })
}
