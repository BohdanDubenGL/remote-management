package com.globallogic.rdkb.remotemanagement.view.screen.login

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.globallogic.rdkb.remotemanagement.view.Screen
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun LoginScreen(navController: NavController) {
    Login(
        onRegister = { navController.navigate(Screen.AutorizationGraph.Registration) },
        onLoginDone = {
            navController.navigate(Screen.HomeGraph) {
                popUpTo<Screen.RootGraph>()
            }
        }
    )
}

@Composable
private fun Login(
    onRegister: () -> Unit,
    onLoginDone: () -> Unit,
) {
    Column {
        Text(text = "Login")
        Button(
            onClick = onRegister,
            content = { Text(text = "Registration") }
        )
        Button(
            onClick = onLoginDone,
            content = { Text(text = "Home") }
        )
    }
}

@Preview
@Composable
private fun LoginPreview() {
    Login(onRegister = { }, onLoginDone = { })
}
