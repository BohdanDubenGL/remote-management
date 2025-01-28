package com.globallogic.rdkb.remotemanagement.view.screen.authentication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.globallogic.rdkb.remotemanagement.domain.entity.LoginData
import com.globallogic.rdkb.remotemanagement.domain.entity.RegistrationData
import com.globallogic.rdkb.remotemanagement.domain.entity.User
import com.globallogic.rdkb.remotemanagement.domain.usecase.user.LoginUseCase
import com.globallogic.rdkb.remotemanagement.domain.usecase.user.RegistrationUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthenticationViewModel(
    private val login: LoginUseCase,
    private val registration: RegistrationUseCase
) : ViewModel() {
    private val _uiState: MutableStateFlow<AuthenticationUiState> = MutableStateFlow(AuthenticationUiState())
    val uiState: StateFlow<AuthenticationUiState> get() = _uiState.asStateFlow()

    fun onEmailEntered(email: String) {
        _uiState.value = _uiState.value.copy(email = email)
    }

    fun onPasswordEntered(password: String) {
        _uiState.value = _uiState.value.copy(password = password)
    }

    fun onConfirmPasswordEntered(confirmPassword: String) {
        _uiState.value = _uiState.value.copy(confirmPassword = confirmPassword)
    }

    fun onEnterClick() {
        val email = _uiState.value.email
        val userExists = email == "user@user.com"
        when {
            userExists -> _uiState.value = _uiState.value.copy(isEmailEditable = false, showPasswordInput = true)
            else -> _uiState.value = _uiState.value.copy(isEmailEditable = false, showPasswordInput = true, showConfirmPasswordInput = true, isRegistering = true)
        }
    }

    fun onAuthenticateClick() {
        viewModelScope.launch {
            val state = _uiState.value
            val user = when {
                state.isRegistering -> registration(RegistrationData(state.email, state.email, state.password, state.confirmPassword))
                else -> login(LoginData(state.email, state.email, state.password))
            }
            _uiState.value = _uiState.value.copy(loggedInUser = user)
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
