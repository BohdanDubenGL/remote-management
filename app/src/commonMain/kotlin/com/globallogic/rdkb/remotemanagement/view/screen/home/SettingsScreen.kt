package com.globallogic.rdkb.remotemanagement.view.screen.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.globallogic.rdkb.remotemanagement.domain.error.UserError
import com.globallogic.rdkb.remotemanagement.domain.usecase.user.GetCurrentLoggedInUserUseCase
import com.globallogic.rdkb.remotemanagement.domain.usecase.user.LogoutUseCase
import com.globallogic.rdkb.remotemanagement.domain.utils.dataOrElse
import com.globallogic.rdkb.remotemanagement.domain.utils.map
import com.globallogic.rdkb.remotemanagement.view.navigation.LocalNavController
import com.globallogic.rdkb.remotemanagement.view.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsScreen(
    navController: NavController = LocalNavController.current,
    settingsViewModel: SettingsViewModel = koinViewModel()
) {
    val uiState by settingsViewModel.uiState.collectAsStateWithLifecycle()
    SettingsContent(
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
private fun SettingsContent(
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
        modifier = Modifier.fillMaxSize().padding(24.dp, 8.dp)
    ) {
        Card(
            shape = RoundedCornerShape(4.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = Modifier
                .padding(24.dp, 8.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(16.dp, 8.dp)
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Row {
                    Text("User email: ")
                    Text(uiState.currentUser?.email.orEmpty())
                }
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            Button(
                modifier = Modifier.fillMaxWidth().height(50.dp),
                onClick = onChangeAccountSettings,
                content = { Text(text = "Change account settings") }
            )
            Button(
                modifier = Modifier.fillMaxWidth().height(50.dp),
                onClick = onLogout,
                content = { Text(text = "Logout") }
            )
        }
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
            _uiState.update { state ->
                getCurrentLoggedInUser()
                    .map { currentUser -> state.copy(currentUser = currentUser) }
                    .dataOrElse { error -> state }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            _uiState.update { state ->
                logoutUser()
                    .map { state.copy(loggedOut = true) }
                    .dataOrElse { error -> state.copy(loggedOut = false) }
            }

        }
    }
}

data class SettingsUiState(
    val currentUser: User? = null,
    val loggedOut: Boolean = false,
)
