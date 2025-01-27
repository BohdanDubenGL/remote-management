package com.globallogic.rdkb.remotemanagement.view.screen.home

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun SettingsScreen(navController: NavController) {
    Settings(
        onBack = { navController.navigateUp() }
    )
}

@Composable
private fun Settings(
    onBack: () -> Unit
) {
    Column {
        Text(text = "Settings")
        Button(
            onClick = onBack,
            content = { Text(text = "Back") }
        )
    }
}

@Preview
@Composable
private fun SettingsPreview() {
    Settings(onBack = { })
}
