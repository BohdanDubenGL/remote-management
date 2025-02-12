package com.globallogic.rdkb.remotemanagement.view.screen.splash

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
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
import com.globallogic.rdkb.remotemanagement.domain.usecase.user.GetCurrentLoggedInUserUseCase
import com.globallogic.rdkb.remotemanagement.domain.utils.dataOrElse
import com.globallogic.rdkb.remotemanagement.domain.utils.map
import com.globallogic.rdkb.remotemanagement.view.base.MviViewModel
import com.globallogic.rdkb.remotemanagement.view.navigation.LocalNavController
import com.globallogic.rdkb.remotemanagement.view.navigation.Screen
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import rdkbremotemanagement.app.generated.resources.Res
import rdkbremotemanagement.app.generated.resources.app_splash_logo
import kotlin.time.Duration.Companion.seconds

@Composable
fun SplashScreen(
    navController: NavController = LocalNavController.current,
    splashViewModel: SplashViewModel = koinViewModel()
) {
    val uiState by splashViewModel.uiState.collectAsStateWithLifecycle()

    SplashContent(
        uiState = uiState,
        onLoggedInUser = {
            navController.navigate(Screen.HomeGraph.Topology) {
                popUpTo<Screen.RootGraph>()
            }
        },
        onNoLoggedInUser = {
            navController.navigate(Screen.Authentication) {
                popUpTo<Screen.RootGraph>()
            }
        }
    )
}

@Composable
private fun SplashContent(
    uiState: SplashUiState,
    onLoggedInUser: () -> Unit,
    onNoLoggedInUser: () -> Unit,
) {
    SideEffect {
        when (uiState) {
            SplashUiState.LoggedIn -> onLoggedInUser()
            SplashUiState.NoLoggedInUser -> onNoLoggedInUser()
            SplashUiState.None -> Unit
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 16.dp)
            .padding(bottom = 96.dp)
    ) {
        Icon(
            painter = painterResource(Res.drawable.app_splash_logo),
            contentDescription = "Remote Management",
            modifier = Modifier.fillMaxSize()
        )
    }
}

class SplashViewModel(
    private val getCurrentLoggedInUser: GetCurrentLoggedInUserUseCase
): MviViewModel<SplashUiState>(SplashUiState.None) {

    override suspend fun onInitState() = checkCurrentUser()

    private fun checkCurrentUser() = launchUpdateState { state ->
        delay(1.seconds.inWholeMilliseconds)
        getCurrentLoggedInUser()
            .map { loggedInUser -> SplashUiState.LoggedIn }
            .dataOrElse { error -> SplashUiState.NoLoggedInUser }
    }
}

sealed interface SplashUiState {
    data object None : SplashUiState
    data object LoggedIn : SplashUiState
    data object NoLoggedInUser : SplashUiState
}
