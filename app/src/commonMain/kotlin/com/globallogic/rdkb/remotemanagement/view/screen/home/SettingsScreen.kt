package com.globallogic.rdkb.remotemanagement.view.screen.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
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
import com.globallogic.rdkb.remotemanagement.domain.entity.User
import com.globallogic.rdkb.remotemanagement.domain.usecase.user.GetCurrentLoggedInUserUseCase
import com.globallogic.rdkb.remotemanagement.domain.usecase.user.LogoutUseCase
import com.globallogic.rdkb.remotemanagement.view.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsScreen(
    navController: NavController,
    settingsViewModel: SettingsViewModel = koinViewModel()
) {
    val uiState by settingsViewModel.uiState.collectAsStateWithLifecycle()
    Settings(
        uiState = uiState,
        loadCurrentUser = settingsViewModel::loadCurrentUser,
        onLogout = settingsViewModel::logout,
        onLoggedOut = {
            navController.navigate(Screen.Authentication) {
                popUpTo<Screen.RootGraph>()
            }
        },
        onChangeAccountSettings = { navController.navigate(Screen.HomeGraph.ChangeAccountSettings) }
    )
}

@Composable
private fun Settings(
    uiState: SettingsUiState,
    loadCurrentUser: () -> Unit,
    onLogout: () -> Unit,
    onLoggedOut: () -> Unit,
    onChangeAccountSettings: () -> Unit
) {
    SideEffect {
        if (uiState.loggedOut) onLoggedOut()
        else loadCurrentUser()
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Row {
            Text("User email: ")
            Text(uiState.currentUser.email)
        }
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onChangeAccountSettings,
            content = { Text(text = "Change account settings") }
        )
        Button(
            onClick = onLogout,
            content = { Text(text = "Logout") }
        )
    }
}

class SettingsViewModel(
    private val getCurrentLoggedInUser: GetCurrentLoggedInUserUseCase,
    private val logoutUser: LogoutUseCase,
) : ViewModel() {
    private val _uiState: MutableStateFlow<SettingsUiState> = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> get() = _uiState.asStateFlow()

    fun loadCurrentUser() {
        viewModelScope.launch {
            val currentUser = getCurrentLoggedInUser()
            _uiState.update { it.copy(currentUser = currentUser) }
        }
    }

    fun logout() {
        viewModelScope.launch {
            val loggedOut = logoutUser()
            _uiState.update { it.copy(loggedOut = loggedOut) }
        }
    }
}

data class SettingsUiState(
    val currentUser: User = User.empty,
    val loggedOut: Boolean = false,
)
