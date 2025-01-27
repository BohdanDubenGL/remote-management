package com.globallogic.rdkb.remotemanagement.view.screen.home

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.globallogic.rdkb.remotemanagement.view.Screen
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun RouterDeviceListScreen(navController: NavController) {
    RouterDeviceList(
        onRouterDeviceSelected = { navController.navigate(Screen.RouterDeviceGraph) },
        onSettings = { navController.navigate(Screen.HomeGraph.Settings) },
        onBack = { navController.navigate(Screen.HomeGraph) },
    )
}

@Composable
private fun RouterDeviceList(
    onRouterDeviceSelected: () -> Unit,
    onSettings: () -> Unit,
    onBack: () -> Unit,
) {
    Column {
        Text(text = "RouterDeviceList")
        Button(
            onClick = onRouterDeviceSelected,
            content = { Text(text = "RouterDetails") }
        )
        Button(
            onClick = onSettings,
            content = { Text(text = "Settings") }
        )
    }
}

@Preview
@Composable
private fun RouterDeviceListPreview() {
    RouterDeviceList(onRouterDeviceSelected = { }, onSettings = { }, onBack = { })
}
