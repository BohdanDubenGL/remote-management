package com.globallogic.rdkb.remotemanagement.view.screen.home

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
import com.globallogic.rdkb.remotemanagement.domain.entity.ChangeAccountSettingsData
import com.globallogic.rdkb.remotemanagement.domain.usecase.user.ChangeAccountSettingsUseCase
import com.globallogic.rdkb.remotemanagement.domain.usecase.user.GetCurrentLoggedInUserUseCase
import com.globallogic.rdkb.remotemanagement.view.LocalNavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.Duration.Companion.seconds

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

    if (uiState.userDataLoaded) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            TextField(
                value = uiState.email,
                onValueChange = onEmailEntered,
                label = { Text(text = "Email") },
                placeholder = { Text(text = "Enter your email") }
            )
            TextField(
                value = uiState.currentPassword,
                onValueChange = onCurrentPasswordEntered,
                label = { Text(text = "Current Password") },
                placeholder = { Text(text = "Enter your current password") },
                visualTransformation = PasswordVisualTransformation()
            )
            TextField(
                value = uiState.newPassword,
                onValueChange = onNewPasswordEntered,
                label = { Text(text = "New Password") },
                placeholder = { Text(text = "Enter new password") },
                visualTransformation = PasswordVisualTransformation()
            )
            TextField(
                value = uiState.confirmNewPassword,
                onValueChange = onConfirmNewPasswordEntered,
                label = { Text(text = "Confirm New Password") },
                placeholder = { Text(text = "Re-enter new password") },
                visualTransformation = PasswordVisualTransformation()
            )
            Button(
                onClick = onSaveClicked,
                content = { Text("Save") }
            )
        }
    } else {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text("Loading...")
        }
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
            getCurrentLoggedInUser()
                .onSuccess { currentUser -> _uiState.update { it.copy(email = currentUser?.email.orEmpty(), userDataLoaded = true) } }
                .onFailure { it.printStackTrace() }
        }
    }

    fun onEmailEntered(email: String) {
        _uiState.update { it.copy(email = email) }
    }

    fun onCurrentPasswordEntered(currentPassword: String) {
        _uiState.update { it.copy(currentPassword = currentPassword) }
    }

    fun onNewPasswordEntered(newPassword: String) {
        _uiState.update { it.copy(newPassword = newPassword) }
    }

    fun onConfirmNewPasswordEntered(confirmNewPassword: String) {
        _uiState.update { it.copy(confirmNewPassword = confirmNewPassword) }
    }

    fun saveData() {
        viewModelScope.launch {
            val state = _uiState.value
            changeAccountSettings(ChangeAccountSettingsData(state.email, state.newPassword, state.confirmNewPassword))
                .onSuccess { _uiState.update { it.copy(dataSaved = true) } }
                .onFailure { it.printStackTrace() }
        }
    }
}

data class ChangeAccountSettingsUiState(
    val email: String = "",
    val currentPassword: String = "",
    val newPassword: String = "",
    val confirmNewPassword: String = "",
    val userDataLoaded: Boolean = false,
    val dataSaved: Boolean = false,
)
