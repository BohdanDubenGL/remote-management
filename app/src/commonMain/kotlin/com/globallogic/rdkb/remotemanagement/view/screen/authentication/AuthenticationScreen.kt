package com.globallogic.rdkb.remotemanagement.view.screen.authentication

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.globallogic.rdkb.remotemanagement.domain.entity.User
import com.globallogic.rdkb.remotemanagement.view.Screen
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AuthenticationScreen(
    navController: NavController,
    authenticationViewModel: AuthenticationViewModel = koinViewModel(),
) {
    val uiState: AuthenticationUiState by authenticationViewModel.uiState.collectAsStateWithLifecycle()

    Authentication(
        uiState = uiState,
        onEmailEntered = authenticationViewModel::onEmailEntered,
        onPasswordEntered = authenticationViewModel::onPasswordEntered,
        onConfirmPasswordEntered = authenticationViewModel::onConfirmPasswordEntered,
        onAuthenticateClick = authenticationViewModel::onAuthenticateClick,
        onEnterClick = authenticationViewModel::onEnterClick,
        onLoggedIn = { loggedInUser ->
            navController.navigate(Screen.HomeGraph) {
                popUpTo<Screen.RootGraph>()
            }
        }
    )
}

@Composable
fun Authentication(
    uiState: AuthenticationUiState,
    onEmailEntered: (email: String) -> Unit,
    onPasswordEntered: (password: String) -> Unit,
    onConfirmPasswordEntered: (confirmPassword: String) -> Unit,
    onAuthenticateClick: () -> Unit,
    onEnterClick: () -> Unit,
    onLoggedIn: (loggedInUser: User) -> Unit,
) {
    SideEffect {
        if (uiState.loggedInUser != User.empty) onLoggedIn(uiState.loggedInUser)
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        TextField(
            value = uiState.email,
            onValueChange = onEmailEntered,
            label = { Text(text = "Email") },
            placeholder = { Text(text = "Enter your email") },
            enabled = uiState.isEmailEditable
        )
        if (uiState.showPasswordInput) {
            TextField(
                value = uiState.password,
                onValueChange = onPasswordEntered,
                label = { Text(text = "Password") },
                placeholder = { Text(text = "Enter your password") },
                visualTransformation = PasswordVisualTransformation()
            )
        }
        if (uiState.showConfirmPasswordInput) {
            TextField(
                value = uiState.confirmPassword,
                onValueChange = onConfirmPasswordEntered,
                label = { Text(text = "Confirm Password") },
                placeholder = { Text(text = "Re-enter your password") },
                visualTransformation = PasswordVisualTransformation()
            )
        }
        Button(
            onClick = {
                if (uiState.showPasswordInput) {
                    onAuthenticateClick()
                } else {
                    onEnterClick()
                }
            },
            content = { Text(text = if(uiState.isRegistering) "Register" else if (uiState.showPasswordInput) "Login" else "Enter") }
        )
    }
}

@Preview
@Composable
private fun AuthenticationPreview() {
    Authentication(
        uiState = AuthenticationUiState(),
        onEmailEntered = { },
        onPasswordEntered = { },
        onConfirmPasswordEntered = { },
        onAuthenticateClick = { },
        onEnterClick = { },
        onLoggedIn = { },
    )
}
