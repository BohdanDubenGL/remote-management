package com.globallogic.rdkb.remotemanagement.view.screen.splash

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.globallogic.rdkb.remotemanagement.domain.entity.User
import com.globallogic.rdkb.remotemanagement.domain.usecase.user.GetCurrentLoggedInUserUseCase
import com.globallogic.rdkb.remotemanagement.view.LocalNavController
import com.globallogic.rdkb.remotemanagement.view.Screen
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import rdkbremotemanagement.app.generated.resources.Res
import rdkbremotemanagement.app.generated.resources.app_name
import kotlin.time.Duration.Companion.seconds

@Composable
fun SplashScreen(
    navController: NavController = LocalNavController.current,
    splashViewModel: SplashViewModel = koinViewModel()
) {
    val uiState by splashViewModel.uiState.collectAsStateWithLifecycle()
    SplashContent(
        uiState = uiState,
        loadCurrentLoggedInUser = splashViewModel::checkCurrentUser,
        onLoggedInUser = { loggedInUser ->
            when(loggedInUser) {
                null -> navController.navigate(Screen.Authentication) {
                    popUpTo<Screen.RootGraph>()
                }
                else -> navController.navigate(Screen.HomeGraph.Topology) {
                    popUpTo<Screen.RootGraph>()
                }
            }
        }
    )
}

@Composable
private fun SplashContent(
    uiState: SplashUiState,
    loadCurrentLoggedInUser: () -> Unit,
    onLoggedInUser: (loggedInUser: User?) -> Unit
) {
    SideEffect {
        if (uiState.userDataLoaded) onLoggedInUser(uiState.loggedInUser)
        else loadCurrentLoggedInUser()
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(text = stringResource(Res.string.app_name))
    }
}

class SplashViewModel(
    private val getCurrentLoggedInUser: GetCurrentLoggedInUserUseCase
): ViewModel() {
    private val _uiState: MutableStateFlow<SplashUiState> = MutableStateFlow(SplashUiState())
    val uiState: StateFlow<SplashUiState> get() = _uiState

    fun checkCurrentUser() {
        viewModelScope.launch {
            delay(1.seconds.inWholeMilliseconds)
            getCurrentLoggedInUser()
                .onSuccess { loggedInUser -> _uiState.update { it.copy(loggedInUser = loggedInUser, userDataLoaded = true) } }
                .onFailure {
                    it.printStackTrace()
                    _uiState.update { it.copy(userDataLoaded = true) }
                }

        }
    }
}

data class SplashUiState(
    val loggedInUser: User? = null,
    val userDataLoaded: Boolean = false,
)
