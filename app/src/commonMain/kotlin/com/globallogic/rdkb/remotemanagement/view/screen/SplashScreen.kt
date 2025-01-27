package com.globallogic.rdkb.remotemanagement.view.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.globallogic.rdkb.remotemanagement.view.Screen
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun SplashScreen(navController: NavController) {
    Splash(
        onLogin = { navController.navigate(Screen.AutorizationGraph) },
        onHome = {
            navController.navigate(Screen.HomeGraph) {
                popUpTo<Screen.RootGraph>()
            }
        }
    )
}

@Composable
private fun Splash(
    onLogin: () -> Unit,
    onHome: () -> Unit
) {
    Column {
        Text(text = "Splash")
        Button(
            onClick = onLogin,
            content = { Text(text = "Login") }
        )
        Button(
            onClick = onHome,
            content = { Text(text = "Home") }
        )
    }
}

@Preview
@Composable
private fun SplashPreview() {
    Splash(onLogin = { }, onHome = { })
}