package com.globallogic.rdkb.remotemanagement.view.screen.login

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.globallogic.rdkb.remotemanagement.view.Screen
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun RegistrationScreen(navController: NavController) {
    Registration(
        onLogin = { navController.navigate(Screen.AutorizationGraph.Login) },
        onRegistrationDone = {
            navController.navigate(Screen.HomeGraph) {
                popUpTo<Screen.RootGraph>()
            }
        }
    )
}

@Composable
private fun Registration(
    onLogin: () -> Unit,
    onRegistrationDone: () -> Unit,
) {
    Column {
        Text(text = "Registration")
        Button(
            onClick = onLogin,
            content = { Text(text = "Login") }
        )
        Button(
            onClick = onRegistrationDone,
            content = { Text(text = "Home") }
        )
    }
}

@Preview
@Composable
private fun RegistrationPreview() {
    Registration(onLogin = { }, onRegistrationDone = { })
}
