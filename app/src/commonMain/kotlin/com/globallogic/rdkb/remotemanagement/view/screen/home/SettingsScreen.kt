package com.globallogic.rdkb.remotemanagement.view.screen.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
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
import com.globallogic.rdkb.remotemanagement.domain.utils.dataOrElse
import com.globallogic.rdkb.remotemanagement.domain.utils.map
import com.globallogic.rdkb.remotemanagement.view.component.AppButton
import com.globallogic.rdkb.remotemanagement.view.component.AppCard
import com.globallogic.rdkb.remotemanagement.view.component.AppIcon
import com.globallogic.rdkb.remotemanagement.view.component.AppIconButton
import com.globallogic.rdkb.remotemanagement.view.component.AppLayoutVerticalSections
import com.globallogic.rdkb.remotemanagement.view.component.AppTextProperty
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

    AppLayoutVerticalSections(
        topSection = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize(),
            ) {
                AppIcon(
                    imageVector = Icons.Default.Person,
                    modifier = Modifier.size(128.dp),
                )
                AppCard {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .fillMaxWidth()
                    ) {
                        AppTextProperty(
                            name = "User email: ",
                            value = uiState.currentUser?.email.orEmpty(),
                        )
                        AppTextProperty(
                            name = "User name: ",
                            value = uiState.currentUser?.username.orEmpty(),
                        )
                    }
                }
            }
        },
        bottomSection = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
                modifier = Modifier.fillMaxSize().padding(horizontal = 32.dp)
            ) {
                AppButton(
                    text = "Change account settings",
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onChangeAccountSettings
                )
                AppButton(
                    text = "Logout",
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onLogout,
                )
            }
        },
        modifier = Modifier.fillMaxSize()
    )
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
