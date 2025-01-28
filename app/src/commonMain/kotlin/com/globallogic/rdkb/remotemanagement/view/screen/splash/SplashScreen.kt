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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.globallogic.rdkb.remotemanagement.domain.entity.User
import com.globallogic.rdkb.remotemanagement.view.Screen
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SplashScreen(
    navController: NavController,
    splashViewModel: SplashViewModel = koinViewModel()
) {
    val uiState by splashViewModel.uiState.collectAsStateWithLifecycle()
    Splash(
        uiState = uiState,
        loadCurrentLoggedInUser = splashViewModel::checkCurrentUser,
        onLoggedInUser = { loggedInUser ->
            when(loggedInUser) {
                User.empty -> navController.navigate(Screen.Authentication)
                else -> navController.navigate(Screen.HomeGraph) {
                    popUpTo<Screen.RootGraph>()
                }
            }
        }
    )
}

@Composable
private fun Splash(
    uiState: SplashUiState,
    loadCurrentLoggedInUser: () -> Unit,
    onLoggedInUser: (loggedInUser: User) -> Unit
) {
    SideEffect {
        if (uiState.loggedInUser != null) onLoggedInUser(uiState.loggedInUser)
        else loadCurrentLoggedInUser()
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(text = "RDK-B Remote Management")
    }
}

@Preview
@Composable
private fun SplashPreview() {
    Splash(uiState = SplashUiState(), loadCurrentLoggedInUser = { }, onLoggedInUser = { })
}
