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
    navController: NavController,
    changeAccountSettingsViewModel: ChangeAccountSettingsViewModel = koinViewModel()
) {
    val uiState by changeAccountSettingsViewModel.uiState.collectAsStateWithLifecycle()

    ChangeAccountSettings(
        uiState = uiState,
        loadCurrentUserData = changeAccountSettingsViewModel::loadCurrentUserData,
        onEmailEntered = changeAccountSettingsViewModel::onEmailEntered,
        onPasswordEntered = changeAccountSettingsViewModel::onPasswordEntered,
        onConfirmPasswordEntered = changeAccountSettingsViewModel::onConfirmPasswordEntered,
        onSaveClicked = changeAccountSettingsViewModel::saveData,
        onDataSaved = { navController.navigateUp() },
    )
}

@Composable
fun ChangeAccountSettings(
    uiState: ChangeAccountSettingsUiState,
    loadCurrentUserData: () -> Unit,
    onEmailEntered: (String) -> Unit,
    onPasswordEntered: (String) -> Unit,
    onConfirmPasswordEntered: (String) -> Unit,
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
                value = uiState.password,
                onValueChange = onPasswordEntered,
                label = { Text(text = "Password") },
                placeholder = { Text(text = "Enter your password") },
                visualTransformation = PasswordVisualTransformation()
            )
            TextField(
                value = uiState.confirmPassword,
                onValueChange = onConfirmPasswordEntered,
                label = { Text(text = "Confirm Password") },
                placeholder = { Text(text = "Re-enter your password") },
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
            delay(1.seconds.inWholeMilliseconds) // todo: remove
            val currentUser = getCurrentLoggedInUser()
            _uiState.update { it.copy(email = currentUser.email, userDataLoaded = true) }
        }
    }

    fun onEmailEntered(email: String) {
        _uiState.update { it.copy(email = email) }
    }

    fun onPasswordEntered(password: String) {
        _uiState.update { it.copy(password = password) }
    }

    fun onConfirmPasswordEntered(confirmPassword: String) {
        _uiState.update { it.copy(confirmPassword = confirmPassword) }
    }

    fun saveData() {
        viewModelScope.launch {
            val state = _uiState.value
            changeAccountSettings(ChangeAccountSettingsData(state.email, state.password, state.confirmPassword))
            _uiState.update { it.copy(dataSaved = true) }
        }
    }
}

data class ChangeAccountSettingsUiState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val userDataLoaded: Boolean = false,
    val dataSaved: Boolean = false,
)
