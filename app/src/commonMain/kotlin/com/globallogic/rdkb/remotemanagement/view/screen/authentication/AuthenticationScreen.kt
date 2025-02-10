package com.globallogic.rdkb.remotemanagement.view.screen.authentication

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.ArrowBackIosNew
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.globallogic.rdkb.remotemanagement.domain.entity.LoginData
import com.globallogic.rdkb.remotemanagement.domain.entity.RegistrationData
import com.globallogic.rdkb.remotemanagement.domain.error.UserError
import com.globallogic.rdkb.remotemanagement.domain.usecase.user.EmailVerification
import com.globallogic.rdkb.remotemanagement.domain.usecase.user.LoginUseCase
import com.globallogic.rdkb.remotemanagement.domain.usecase.user.RegistrationUseCase
import com.globallogic.rdkb.remotemanagement.domain.usecase.user.VerifyEmailForAuthenticationUseCase
import com.globallogic.rdkb.remotemanagement.domain.utils.dataOrElse
import com.globallogic.rdkb.remotemanagement.domain.utils.map
import com.globallogic.rdkb.remotemanagement.view.component.AppButton
import com.globallogic.rdkb.remotemanagement.view.component.AppIconButton
import com.globallogic.rdkb.remotemanagement.view.component.AppLayoutVerticalSections
import com.globallogic.rdkb.remotemanagement.view.component.AppPasswordTextField
import com.globallogic.rdkb.remotemanagement.view.component.AppTextField
import com.globallogic.rdkb.remotemanagement.view.component.AppTitleText
import com.globallogic.rdkb.remotemanagement.view.navigation.LocalNavController
import com.globallogic.rdkb.remotemanagement.view.navigation.Screen
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

    AuthenticationContent(
        uiState = uiState,
        onEmailEntered = authenticationViewModel::onEmailEntered,
        onNameEntered = authenticationViewModel::onNameEntered,
        onPasswordEntered = authenticationViewModel::onPasswordEntered,
        onConfirmPasswordEntered = authenticationViewModel::onConfirmPasswordEntered,
        onEnterClick = authenticationViewModel::onEnterClick,
        onBackClick = authenticationViewModel::onBackClick,
        onLoggedIn = { navController.navigate(Screen.HomeGraph.Topology) {
            popUpTo<Screen.RootGraph>()
        } },
    )
}

@Composable
private fun AuthenticationContent(
    uiState: AuthenticationUiState,
    onEmailEntered: (email: String) -> Unit,
    onNameEntered: (email: String) -> Unit,
    onPasswordEntered: (password: String) -> Unit,
    onConfirmPasswordEntered: (confirmPassword: String) -> Unit,
    onEnterClick: () -> Unit,
    onBackClick: () -> Unit,
    onLoggedIn: () -> Unit,
) {
    SideEffect {
        if (uiState is AuthenticationUiState.LoggedIn) onLoggedIn()
    }

    AppLayoutVerticalSections(
        topSectionWeight = 5F,
        topSection = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top),
                modifier = Modifier.fillMaxSize().padding(top = 64.dp).width(350.dp)
            ) {
                AppTitleText(
                    text = uiState.titleText,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                AppTextField(
                    value = uiState.email,
                    onValueChange = onEmailEntered,
                    label = "Email",
                    placeholder = "Enter your email",
                    isError = uiState.emailError.isNotBlank(),
                    errorMessage = uiState.emailError,
                    enabled = uiState.isEmailEditable,
                )
                AnimatedVisibility(uiState.showNameInput) {
                    AppTextField(
                        value = uiState.name,
                        onValueChange = onNameEntered,
                        label = "Username",
                        placeholder = "Enter your name",
                        isError = uiState.nameError.isNotBlank(),
                        errorMessage = uiState.nameError,
                        enabled = uiState.isNameEditable,
                    )
                }
                AnimatedVisibility(uiState.showPasswordInput) {
                    AppPasswordTextField(
                        value = uiState.password,
                        onValueChange = onPasswordEntered,
                        label = "Password",
                        placeholder = "Enter your password",
                        isError = uiState.passwordError.isNotBlank(),
                        errorMessage = uiState.passwordError,
                    )
                }
                AnimatedVisibility(uiState.showConfirmPasswordInput) {
                    AppPasswordTextField(
                        value = uiState.confirmPassword,
                        onValueChange = onConfirmPasswordEntered,
                        label = "Confirm Password",
                        placeholder = "Re-enter your password",
                        isError = uiState.confirmPasswordError.isNotBlank(),
                        errorMessage = uiState.confirmPasswordError,
                    )
                }
            }
        },
        bottomSection = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
            ) {
                AnimatedVisibility(
                    visible = uiState.showBackButton,
                    enter = fadeIn(),
                    exit = fadeOut(),
                ) {
                    AppIconButton(
                        imageVector = Icons.TwoTone.ArrowBackIosNew,
                        onClick = onBackClick,
                    )
                }
                AppButton(
                    text = uiState.enterButtonText,
                    onClick = onEnterClick,
                )
                AnimatedVisibility(
                    visible = uiState.showBackButton,
                    enter = fadeIn(),
                    exit = fadeOut(),
                ) {
                    Spacer(modifier = Modifier.width(56.dp))
                }
            }
        },
        modifier = Modifier
    )
}

class AuthenticationViewModel(
    private val login: LoginUseCase,
    private val registration: RegistrationUseCase,
    private val verifyEmailForAuthentication: VerifyEmailForAuthenticationUseCase,
) : ViewModel() {
    private val _uiState: MutableStateFlow<AuthenticationUiState> = MutableStateFlow(AuthenticationUiState.EnterEmail())
    val uiState: StateFlow<AuthenticationUiState> get() = _uiState.asStateFlow()

    fun onEmailEntered(email: String) {
        _uiState.update {
            when (it) {
                is AuthenticationUiState.EnterEmail -> it.copy(email = email, emailError = "")
                else -> it
            }
        }
    }

    fun onNameEntered(name: String) {
        _uiState.update {
            when (it) {
                is AuthenticationUiState.Register -> it.copy(name = name, nameError = "")
                else -> it
            }
        }
    }

    fun onPasswordEntered(password: String) {
        _uiState.update {
            when (it) {
                is AuthenticationUiState.Login -> it.copy(password = password, passwordError = "")
                is AuthenticationUiState.Register -> it.copy(password = password, passwordError = "", confirmPasswordError = "")
                else -> it
            }
        }
    }

    fun onConfirmPasswordEntered(confirmPassword: String) {
        _uiState.update {
            when (it) {
                is AuthenticationUiState.Register -> it.copy(confirmPassword = confirmPassword, passwordError = "", confirmPasswordError = "")
                else -> it
            }
        }
    }

    fun onBackClick() {
        viewModelScope.launch {
            _uiState.update { state ->
                when(state) {
                    is AuthenticationUiState.Login -> AuthenticationUiState.EnterEmail(email = state.email)
                    is AuthenticationUiState.Register -> AuthenticationUiState.EnterEmail(email = state.email)
                    else -> state
                }
            }
        }
    }

    fun onEnterClick() {
        viewModelScope.launch {
            _uiState.update { state ->
                when(state) {
                    is AuthenticationUiState.EnterEmail -> {
                        verifyEmailForAuthentication(state.email)
                            .map { emailVerification ->
                                when(emailVerification) {
                                    is EmailVerification.EmailIsUsed -> AuthenticationUiState.Login(
                                        email = emailVerification.email,
                                        name = emailVerification.name
                                    )
                                    is EmailVerification.EmailIsFree -> AuthenticationUiState.Register(
                                        email = emailVerification.email,
                                        name = emailVerification.nameSuggestion
                                    )
                                }
                            }
                            .dataOrElse { error -> state.copy(emailError = "wrong email format") }
                    }
                    is AuthenticationUiState.Login -> {
                        login(LoginData(state.name, state.email, state.password))
                            .map { user -> AuthenticationUiState.LoggedIn }
                            .dataOrElse { error ->
                                when (error) {
                                    is UserError.WrongCredentials -> state.copy(passwordError = "wrong password")
                                    is UserError.WrongPasswordFormat -> state.copy(passwordError = "wrong password format")
                                    is UserError.WrongPasswordLength -> state.copy(passwordError = "password length should be in ${error.min} and ${error.max}")
                                }
                            }
                    }
                    is AuthenticationUiState.Register -> {
                        registration(RegistrationData(state.name, state.email, state.password, state.confirmPassword))
                            .map { AuthenticationUiState.LoggedIn }
                            .dataOrElse { error ->
                                when (error) {
                                    is UserError.UserAlreadyExist -> AuthenticationUiState.Error("User already exist")
                                    is UserError.ConfirmPasswordDoesntMatch -> state.copy(confirmPasswordError = "confirm password doesn't match", passwordError = "", nameError = "")
                                    is UserError.WrongPasswordFormat -> state.copy(passwordError = "wrong password format", confirmPasswordError = "", nameError = "")
                                    is UserError.WrongPasswordLength -> state.copy(passwordError = "password length should be in ${error.min} and ${error.max}", confirmPasswordError = "", nameError = "")
                                    is UserError.WrongUsernameFormat -> state.copy(nameError = "wrong username format", passwordError = "", confirmPasswordError = "")
                                    is UserError.WrongUsernameLength -> state.copy(nameError = "username length should be in ${error.min} and ${error.max}", passwordError = "", confirmPasswordError = "")
                                }
                            }
                    }
                    else -> state
                }
            }
        }
    }
}

sealed interface AuthenticationUiState {
    val titleText: String get() = ""
    val email: String get() = ""
    val name: String get() = ""
    val password: String get() = ""
    val confirmPassword: String get() = ""
    val enterButtonText: String get() = ""

    val emailError: String get() = ""
    val nameError: String get() = ""
    val passwordError: String get() = ""
    val confirmPasswordError: String get() = ""

    val isEmailEditable: Boolean get() = this is EnterEmail
    val isNameEditable: Boolean get() = this is Register

    val showBackButton: Boolean get() = this is Login || this is Register
    val showNameInput: Boolean get() = this is Login || this is Register
    val showPasswordInput: Boolean get() = this is Login || this is Register
    val showConfirmPasswordInput: Boolean get() = this is Register

    data class EnterEmail(
        override val titleText: String = "Authentication",
        override val email: String = "",
        override val emailError: String = "",
        override val enterButtonText: String = "Enter",
    ): AuthenticationUiState

    data class Login(
        override val titleText: String = "Login",
        override val email: String = "",
        override val name: String = "",
        override val password: String = "",
        override val passwordError: String = "",
        override val enterButtonText: String = "Login",
    ): AuthenticationUiState

    data class Register(
        override val titleText: String = "Registration",
        override val email: String = "",
        override val name: String = "",
        override val nameError: String = "",
        override val password: String = "",
        override val passwordError: String = "",
        override val confirmPassword: String = "",
        override val confirmPasswordError: String = "",
        override val enterButtonText: String = "Register",
    ): AuthenticationUiState

    data class Error(
        val errorMessage: String = "",
    ): AuthenticationUiState

    data object LoggedIn: AuthenticationUiState
}
