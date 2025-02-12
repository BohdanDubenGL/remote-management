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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.globallogic.rdkb.remotemanagement.domain.usecase.user.GetCurrentLoggedInUserUseCase
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource
import com.globallogic.rdkb.remotemanagement.domain.utils.ResourceState
import com.globallogic.rdkb.remotemanagement.domain.utils.dataOrElse
import com.globallogic.rdkb.remotemanagement.domain.utils.map
import com.globallogic.rdkb.remotemanagement.view.base.MviViewModel
import com.globallogic.rdkb.remotemanagement.view.component.AppDrawResourceState
import com.globallogic.rdkb.remotemanagement.view.error.UiResourceError
import com.globallogic.rdkb.remotemanagement.view.navigation.LocalNavController
import com.globallogic.rdkb.remotemanagement.view.navigation.Screen
import kotlinx.coroutines.delay
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

    AppDrawResourceState(
        resourceState = uiState,
        onLoading = {
            SplashContent()
        },
        onSuccess = { state ->
            SideEffect {
                when (state) {
                    SplashUiState.LoggedIn -> navController.navigate(Screen.HomeGraph.Topology) {
                        popUpTo<Screen.RootGraph>()
                    }
                    SplashUiState.NoLoggedInUser -> navController.navigate(Screen.Authentication) {
                        popUpTo<Screen.RootGraph>()
                    }
                }
            }
        }
    )
}

@Composable
private fun SplashContent() {
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
): MviViewModel<ResourceState<SplashUiState, UiResourceError>>(ResourceState.None) {

    override suspend fun onInitState() = loadCurrentUser()

    fun loadCurrentUser() = launchOnViewModelScope {
        updateState { state -> ResourceState.Loading }
        delay(1.seconds.inWholeMilliseconds)
        updateState { state ->
            getCurrentLoggedInUser()
                .map { loggedInUser -> Resource.Success(SplashUiState.LoggedIn) }
                .dataOrElse { error -> Resource.Success(SplashUiState.NoLoggedInUser) }
        }
    }
}

sealed interface SplashUiState {
    data object LoggedIn : SplashUiState
    data object NoLoggedInUser : SplashUiState
}
