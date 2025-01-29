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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.globallogic.rdkb.remotemanagement.domain.entity.LoginData
import com.globallogic.rdkb.remotemanagement.domain.entity.RegistrationData
import com.globallogic.rdkb.remotemanagement.domain.entity.User
import com.globallogic.rdkb.remotemanagement.domain.usecase.user.LoginUseCase
import com.globallogic.rdkb.remotemanagement.domain.usecase.user.RegistrationUseCase
import com.globallogic.rdkb.remotemanagement.view.LocalNavController
import com.globallogic.rdkb.remotemanagement.view.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AuthenticationScreen(
    navController: NavController = LocalNavController.current,
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
            navController.navigate(Screen.HomeGraph.Topology) {
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

class AuthenticationViewModel(
    private val login: LoginUseCase,
    private val registration: RegistrationUseCase
) : ViewModel() {
    private val _uiState: MutableStateFlow<AuthenticationUiState> = MutableStateFlow(AuthenticationUiState())
    val uiState: StateFlow<AuthenticationUiState> get() = _uiState.asStateFlow()

    fun onEmailEntered(email: String) {
        _uiState.update { it.copy(email = email) }
    }

    fun onPasswordEntered(password: String) {
        _uiState.update { it.copy(password = password) }
    }

    fun onConfirmPasswordEntered(confirmPassword: String) {
        _uiState.update { it.copy(confirmPassword = confirmPassword) }
    }

    fun onEnterClick() {
        val email = _uiState.value.email
        val userExists = email == "user@user.com"
        when {
            userExists -> _uiState.update { it.copy(isEmailEditable = false, showPasswordInput = true) }
            else -> _uiState.update { it.copy(isEmailEditable = false, showPasswordInput = true, showConfirmPasswordInput = true, isRegistering = true) }
        }
    }

    fun onAuthenticateClick() {
        viewModelScope.launch {
            val state = _uiState.value
            val user = when {
                state.isRegistering -> registration(RegistrationData(state.email, state.email, state.password, state.confirmPassword))
                else -> login(LoginData(state.email, state.email, state.password))
            }
            _uiState.update { it.copy(loggedInUser = user) }
        }
    }
}

data class AuthenticationUiState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isEmailEditable: Boolean = true,
    val showPasswordInput: Boolean = false,
    val showConfirmPasswordInput: Boolean = false,
    val isRegistering: Boolean = false,
    val loggedInUser: User = User.empty
)
