package com.globallogic.rdkb.remotemanagement.view.screen.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import com.globallogic.rdkb.remotemanagement.domain.entity.ChangeAccountSettingsData
import com.globallogic.rdkb.remotemanagement.domain.error.UserError
import com.globallogic.rdkb.remotemanagement.domain.usecase.user.ChangeAccountSettingsUseCase
import com.globallogic.rdkb.remotemanagement.domain.usecase.user.GetCurrentLoggedInUserUseCase
import com.globallogic.rdkb.remotemanagement.domain.utils.dataOrElse
import com.globallogic.rdkb.remotemanagement.domain.utils.map
import com.globallogic.rdkb.remotemanagement.view.component.AppButton
import com.globallogic.rdkb.remotemanagement.view.component.AppLayoutVerticalSections
import com.globallogic.rdkb.remotemanagement.view.component.AppLoading
import com.globallogic.rdkb.remotemanagement.view.component.AppPasswordTextField
import com.globallogic.rdkb.remotemanagement.view.component.AppTextField
import com.globallogic.rdkb.remotemanagement.view.component.AppTitleText
import com.globallogic.rdkb.remotemanagement.view.component.SetupBottomNavigation
import com.globallogic.rdkb.remotemanagement.view.navigation.BottomNavigationState
import com.globallogic.rdkb.remotemanagement.view.navigation.LocalNavController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ChangeAccountSettingsScreen(
    navController: NavController = LocalNavController.current,
    changeAccountSettingsViewModel: ChangeAccountSettingsViewModel = koinViewModel()
) {
    val uiState by changeAccountSettingsViewModel.uiState.collectAsStateWithLifecycle()

    ChangeAccountSettingsContent(
        uiState = uiState,
        loadCurrentUserData = changeAccountSettingsViewModel::loadCurrentUserData,
        onEmailEntered = changeAccountSettingsViewModel::onEmailEntered,
        onUserNameEntered = changeAccountSettingsViewModel::onUsernameEntered,
        onCurrentPasswordEntered = changeAccountSettingsViewModel::onCurrentPasswordEntered,
        onNewPasswordEntered = changeAccountSettingsViewModel::onNewPasswordEntered,
        onConfirmNewPasswordEntered = changeAccountSettingsViewModel::onConfirmNewPasswordEntered,
        onSaveClicked = changeAccountSettingsViewModel::saveData,
        onDataSaved = { navController.navigateUp() },
    )
}

@Composable
private fun ChangeAccountSettingsContent(
    uiState: ChangeAccountSettingsUiState,
    loadCurrentUserData: () -> Unit,
    onEmailEntered: (String) -> Unit,
    onUserNameEntered: (String) -> Unit,
    onCurrentPasswordEntered: (String) -> Unit,
    onNewPasswordEntered: (String) -> Unit,
    onConfirmNewPasswordEntered: (String) -> Unit,
    onSaveClicked: () -> Unit,
    onDataSaved: () -> Unit,
) {
    SideEffect {
        when {
            uiState.dataSaved -> onDataSaved()
            !uiState.userDataLoaded -> loadCurrentUserData()
        }
    }
    SetupBottomNavigation(bottomNavigationState = BottomNavigationState.Hidden)

    if (uiState.userDataLoaded) {
        AppLayoutVerticalSections(
            modifier = Modifier.fillMaxSize(),
            topSectionWeight = 5F,
            topSection = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top),
                    modifier = Modifier.fillMaxSize()
                ) {
                    AppTextField(
                        value = uiState.email,
                        onValueChange = onEmailEntered,
                        label = "Email",
                        placeholder = "Enter your email",
                        isError = uiState.emailError.isNotBlank(),
                        errorMessage = uiState.emailError,
                        enabled = false,
                    )
                    AppTextField(
                        value = uiState.username,
                        onValueChange = onUserNameEntered,
                        label = "Username",
                        placeholder = "Enter your username",
                        isError = uiState.usernameError.isNotBlank(),
                        errorMessage = uiState.usernameError,
                    )
                    AppPasswordTextField(
                        value = uiState.newPassword,
                        onValueChange = onNewPasswordEntered,
                        label = "Password",
                        placeholder = "Enter password",
                        isError = uiState.newPasswordError.isNotBlank(),
                        errorMessage = uiState.newPasswordError,
                    )
                    AppPasswordTextField(
                        value = uiState.confirmNewPassword,
                        onValueChange = onConfirmNewPasswordEntered,
                        label = "Confirm Password",
                        placeholder = "Re-enter password",
                        isError = uiState.confirmNewPasswordError.isNotBlank(),
                        errorMessage = uiState.confirmNewPasswordError,
                    )
                    AppTitleText(text = "Confirm it is you", modifier = Modifier.padding(top = 16.dp))
                    AppPasswordTextField(
                        value = uiState.currentPassword,
                        onValueChange = onCurrentPasswordEntered,
                        label = "Current Password",
                        placeholder = "Enter your current password",
                        isError = uiState.currentPasswordError.isNotBlank(),
                        errorMessage = uiState.currentPasswordError,
                    )
                }
            },
            bottomSection = {
                AppButton(
                    text = "Save",
                    onClick = onSaveClicked,
                )
            }
        )
    } else {
        AppLoading(
            content = { AppTitleText(text = "Loading...") }
        )
    }
}

class ChangeAccountSettingsViewModel(
    private val getCurrentLoggedInUser: GetCurrentLoggedInUserUseCase,
    private val changeAccountSettings: ChangeAccountSettingsUseCase,
) : ViewModel() {
    private val _uiState: MutableStateFlow<ChangeAccountSettingsUiState> = MutableStateFlow(ChangeAccountSettingsUiState())
    val uiState: StateFlow<ChangeAccountSettingsUiState> get() = _uiState.asStateFlow()

    fun loadCurrentUserData() {
        viewModelScope.launch {
            _uiState.update { state ->
                getCurrentLoggedInUser()
                    .map { currentUser -> state.copy(email = currentUser.email, username = currentUser.username, userDataLoaded = true) }
                    .dataOrElse { error -> state.copy(userDataLoaded = true) }
            }
        }
    }

    fun onEmailEntered(email: String) {
        _uiState.update { it.copy(email = email, emailError = "") }
    }

    fun onUsernameEntered(username: String) {
        _uiState.update { it.copy(username = username, usernameError = "") }
    }

    fun onCurrentPasswordEntered(currentPassword: String) {
        _uiState.update { it.copy(currentPassword = currentPassword, currentPasswordError = "") }
    }

    fun onNewPasswordEntered(newPassword: String) {
        _uiState.update { it.copy(newPassword = newPassword, newPasswordError = "") }
    }

    fun onConfirmNewPasswordEntered(confirmNewPassword: String) {
        _uiState.update { it.copy(confirmNewPassword = confirmNewPassword, confirmNewPasswordError = "") }
    }

    fun saveData() {
        viewModelScope.launch {
            _uiState.update { state ->
                changeAccountSettings(ChangeAccountSettingsData(state.email, state.username, state.newPassword, state.confirmNewPassword, state.currentPassword))
                    .map { state.copy(dataSaved = true) }
                    .dataOrElse { error ->
                        when(error) {
                            is UserError.UserNotFound -> state.copy(emailError = "user not found", usernameError = "", newPasswordError = "", confirmNewPasswordError = "", currentPasswordError = "")
                            is UserError.WrongEmailFormat -> state.copy(emailError = "wrong email format", usernameError = "", newPasswordError = "", confirmNewPasswordError = "", currentPasswordError = "")
                            is UserError.WrongUsernameFormat -> state.copy(usernameError = "wrong username format", emailError = "", newPasswordError = "", confirmNewPasswordError = "", currentPasswordError = "")
                            is UserError.WrongUsernameLength -> state.copy(usernameError = "username length should be in ${error.min} and ${error.max}", emailError = "", newPasswordError = "", confirmNewPasswordError = "", currentPasswordError = "")
                            is UserError.WrongPasswordFormat -> state.copy(newPasswordError = "wrong password format", emailError = "", usernameError = "", confirmNewPasswordError = "", currentPasswordError = "")
                            is UserError.WrongPasswordLength -> state.copy(newPasswordError = "password length should be in ${error.min} and ${error.max}", emailError = "", usernameError = "", confirmNewPasswordError = "", currentPasswordError = "")
                            is UserError.ConfirmPasswordDoesntMatch -> state.copy(confirmNewPasswordError = "confirm password doesn't match", emailError = "", usernameError = "", newPasswordError = "", currentPasswordError = "")
                            is UserError.WrongCredentials -> state.copy(currentPasswordError = "wrong password", emailError = "", usernameError = "", newPasswordError = "", confirmNewPasswordError = "")
                        }
                    }
            }
        }
    }
}

data class ChangeAccountSettingsUiState(
    val username: String = "",
    val usernameError: String = "",
    val email: String = "",
    val emailError: String = "",
    val newPassword: String = "",
    val newPasswordError: String = "",
    val confirmNewPassword: String = "",
    val confirmNewPasswordError: String = "",
    val currentPassword: String = "",
    val currentPasswordError: String = "",
    val userDataLoaded: Boolean = false,
    val dataSaved: Boolean = false,
)
