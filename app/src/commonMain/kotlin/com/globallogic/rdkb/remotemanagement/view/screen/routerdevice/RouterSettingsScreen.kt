package com.globallogic.rdkb.remotemanagement.view.screen.routerdevice

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice
import com.globallogic.rdkb.remotemanagement.domain.usecase.routerdevice.FactoryResetRouterDeviceUseCase
import com.globallogic.rdkb.remotemanagement.domain.usecase.routerdevice.GetSelectedRouterDeviceUseCase
import com.globallogic.rdkb.remotemanagement.domain.usecase.routerdevice.RemoveRouterDeviceUseCase
import com.globallogic.rdkb.remotemanagement.domain.usecase.routerdevice.RestartRouterDeviceUseCase
import com.globallogic.rdkb.remotemanagement.domain.utils.dataOrElse
import com.globallogic.rdkb.remotemanagement.domain.utils.map
import com.globallogic.rdkb.remotemanagement.view.component.AppButton
import com.globallogic.rdkb.remotemanagement.view.navigation.LocalNavController
import com.globallogic.rdkb.remotemanagement.view.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RouterSettingsScreen(
    navController: NavController = LocalNavController.current,
    routerSettingsViewModel: RouterSettingsViewModel = koinViewModel(),
) {
    val uiState by routerSettingsViewModel.uiState.collectAsStateWithLifecycle()

    RouterSettingsContent(
        uiState = uiState,
        loadRouterDevice = routerSettingsViewModel::loadRouterDevice,
        restartDevice = routerSettingsViewModel::restartRouterDevice,
        factoryResetDevice = routerSettingsViewModel::factoryResetRouterDevice,
        removeDevice = routerSettingsViewModel::removeRouterDevice,
        deviceRemoved = {
            navController.navigate(Screen.HomeGraph.Topology) {
                popUpTo<Screen.RootGraph>()
            }
        }
    )
}

@Composable
private fun RouterSettingsContent(
    uiState: RouterSettingsUiState,
    loadRouterDevice: () -> Unit,
    restartDevice: () -> Unit,
    factoryResetDevice: () -> Unit,
    removeDevice: () -> Unit,
    deviceRemoved: () -> Unit,
) {
    SideEffect {
        when {
            uiState.deviceRemoved -> deviceRemoved()
            uiState.routerDevice == null || !uiState.deviceAvailable -> loadRouterDevice()
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize().padding(24.dp, 8.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            AppButton(
                text = "Restart device",
                enabled = uiState.deviceAvailable,
                onClick = restartDevice,
                modifier = Modifier.fillMaxWidth(),
            )
            AppButton(
                text = "Factory reset device",
                enabled = uiState.deviceAvailable,
                onClick = factoryResetDevice,
                modifier = Modifier.fillMaxWidth(),
            )
            AppButton(
                text = "Remove device",
                enabled = uiState.deviceAvailable,
                onClick = removeDevice,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

class RouterSettingsViewModel(
    private val getSelectedRouterDevice: GetSelectedRouterDeviceUseCase,
    private val restartRouterDevice: RestartRouterDeviceUseCase,
    private val factoryResetRouterDevice: FactoryResetRouterDeviceUseCase,
    private val removeRouterDevice: RemoveRouterDeviceUseCase,
) : ViewModel() {
    private val _uiState: MutableStateFlow<RouterSettingsUiState> = MutableStateFlow(RouterSettingsUiState())
    val uiState: StateFlow<RouterSettingsUiState> get() = _uiState.asStateFlow()

    fun loadRouterDevice() {
        viewModelScope.launch {
            _uiState.update { state ->
                getSelectedRouterDevice()
                    .map { routerDevice -> state.copy(routerDevice = routerDevice, deviceAvailable = true) }
                    .dataOrElse { error -> state }
            }
        }
    }

    fun restartRouterDevice() {
        viewModelScope.launch {
            _uiState.update { state -> state.copy(deviceAvailable = false) }
            _uiState.update { state ->
                state.routerDevice ?: return@update state
                restartRouterDevice(state.routerDevice)
                    .map { state.copy(deviceAvailable = true) }
                    .dataOrElse { error -> state }
            }
        }
    }

    fun factoryResetRouterDevice() {
        viewModelScope.launch {
            _uiState.update { it.copy(deviceAvailable = false) }

            _uiState.update { state ->
                state.routerDevice ?: return@update state
                factoryResetRouterDevice(state.routerDevice)
                    .map { state.copy(deviceAvailable = true) }
                    .dataOrElse { error -> state }
            }
        }
    }

    fun removeRouterDevice() {
        viewModelScope.launch {
            _uiState.update { state -> state.copy(deviceAvailable = false) }

            _uiState.update { state ->
                state.routerDevice ?: return@update state
                removeRouterDevice(state.routerDevice)
                    .map { state.copy(deviceAvailable = true, deviceRemoved = true) }
                    .dataOrElse { error -> state }
            }
        }
    }
}

data class RouterSettingsUiState(
    val routerDevice: RouterDevice? = null,
    val deviceAvailable: Boolean = false,
    val deviceRemoved: Boolean = false,
)
