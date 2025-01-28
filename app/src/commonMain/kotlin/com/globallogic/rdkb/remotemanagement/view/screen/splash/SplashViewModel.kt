package com.globallogic.rdkb.remotemanagement.view.screen.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.globallogic.rdkb.remotemanagement.domain.entity.User
import com.globallogic.rdkb.remotemanagement.domain.usecase.user.GetCurrentLoggedInUserUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class SplashViewModel(
    private val getCurrentLoggedInUser: GetCurrentLoggedInUserUseCase
): ViewModel() {
    private val _uiState: MutableStateFlow<SplashUiState> = MutableStateFlow(SplashUiState())
    val uiState: StateFlow<SplashUiState> get() = _uiState

    fun checkCurrentUser() {
        viewModelScope.launch {
            delay(1.seconds.inWholeMilliseconds) // todo: remove
            val loggedInUser = getCurrentLoggedInUser()
            _uiState.value = _uiState.value.copy(loggedInUser = loggedInUser)
        }
    }
}

data class SplashUiState(
    val loggedInUser: User? = null
)
