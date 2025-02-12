package com.globallogic.rdkb.remotemanagement.view.screen.routerdevice

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice
import com.globallogic.rdkb.remotemanagement.domain.usecase.routerdevice.FactoryResetRouterDeviceUseCase
import com.globallogic.rdkb.remotemanagement.domain.usecase.routerdevice.GetSelectedRouterDeviceUseCase
import com.globallogic.rdkb.remotemanagement.domain.usecase.routerdevice.RemoveRouterDeviceUseCase
import com.globallogic.rdkb.remotemanagement.domain.usecase.routerdevice.RestartRouterDeviceUseCase
import com.globallogic.rdkb.remotemanagement.domain.utils.dataOrElse
import com.globallogic.rdkb.remotemanagement.domain.utils.map
import com.globallogic.rdkb.remotemanagement.view.base.MviViewModel
import com.globallogic.rdkb.remotemanagement.view.component.AppButton
import com.globallogic.rdkb.remotemanagement.view.navigation.LocalNavController
import com.globallogic.rdkb.remotemanagement.view.navigation.Screen
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RouterSettingsScreen(
    navController: NavController = LocalNavController.current,
    routerSettingsViewModel: RouterSettingsViewModel = koinViewModel(),
) {
    val uiState by routerSettingsViewModel.uiState.collectAsStateWithLifecycle()

    RouterSettingsContent(
        uiState = uiState,
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
    restartDevice: () -> Unit,
    factoryResetDevice: () -> Unit,
    removeDevice: () -> Unit,
    deviceRemoved: () -> Unit,
) {
    SideEffect {
        if (uiState.deviceRemoved) deviceRemoved()
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
) : MviViewModel<RouterSettingsUiState>(RouterSettingsUiState()) {

    override suspend fun onInitState() = loadRouterDevice()

    private fun loadRouterDevice() = launchUpdateState { state ->
        getSelectedRouterDevice()
            .map { routerDevice -> state.copy(routerDevice = routerDevice, deviceAvailable = true) }
            .dataOrElse { error -> state }
    }

    fun restartRouterDevice() = launchOnViewModelScope {
        updateState { state -> state.copy(deviceAvailable = false) }
        updateState { state ->
            state.routerDevice ?: return@updateState state
            restartRouterDevice(state.routerDevice)
                .map { state.copy(deviceAvailable = true) }
                .dataOrElse { error ->
                    loadRouterDevice()
                    state
                }
        }
    }

    fun factoryResetRouterDevice() = launchOnViewModelScope {
        updateState { state -> state.copy(deviceAvailable = false) }
        updateState { state ->
            state.routerDevice ?: return@updateState state
            factoryResetRouterDevice(state.routerDevice)
                .map { state.copy(deviceAvailable = true) }
                .dataOrElse { error ->
                    loadRouterDevice()
                    state
                }
        }
    }

    fun removeRouterDevice() {
        launchOnViewModelScope {
            updateState { state -> state.copy(deviceAvailable = false) }
            updateState { state ->
                state.routerDevice ?: return@updateState state
                removeRouterDevice(state.routerDevice)
                    .map { state.copy(deviceAvailable = true, deviceRemoved = true) }
                    .dataOrElse { error ->
                        loadRouterDevice()
                        state
                    }
            }
        }
    }
}

data class RouterSettingsUiState(
    val routerDevice: RouterDevice? = null,
    val deviceAvailable: Boolean = false,
    val deviceRemoved: Boolean = false,
)
