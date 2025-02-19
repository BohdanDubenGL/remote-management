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
import com.globallogic.rdkb.remotemanagement.domain.usecase.routerdevice.DoRouterDeviceActionUseCase
import com.globallogic.rdkb.remotemanagement.domain.usecase.routerdevice.GetSelectedRouterDeviceUseCase
import com.globallogic.rdkb.remotemanagement.domain.usecase.routerdevice.RouterDeviceAction
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource
import com.globallogic.rdkb.remotemanagement.domain.utils.ResourceState
import com.globallogic.rdkb.remotemanagement.domain.utils.dataOrElse
import com.globallogic.rdkb.remotemanagement.domain.utils.flatMapData
import com.globallogic.rdkb.remotemanagement.domain.utils.flatMapError
import com.globallogic.rdkb.remotemanagement.domain.utils.map
import com.globallogic.rdkb.remotemanagement.domain.utils.onFailure
import com.globallogic.rdkb.remotemanagement.view.base.MviViewModel
import com.globallogic.rdkb.remotemanagement.view.component.AppButton
import com.globallogic.rdkb.remotemanagement.view.component.AppDrawResourceState
import com.globallogic.rdkb.remotemanagement.view.error.UiResourceError
import com.globallogic.rdkb.remotemanagement.view.navigation.LocalNavController
import com.globallogic.rdkb.remotemanagement.view.navigation.Screen
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RouterSettingsScreen(
    navController: NavController = LocalNavController.current,
    routerSettingsViewModel: RouterSettingsViewModel = koinViewModel(),
) {
    val uiState by routerSettingsViewModel.uiState.collectAsStateWithLifecycle()

    AppDrawResourceState(
        resourceState = uiState,
        onSuccess = { state ->
            when (state) {
                is RouterSettingsUiState.DeviceRemoved -> SideEffect {
                    navController.navigate(Screen.HomeGraph.Topology) {
                        popUpTo<Screen.RootGraph>()
                    }
                }
                is RouterSettingsUiState.DeviceLoaded -> RouterSettingsContent(
                    restartDevice = routerSettingsViewModel::restartRouterDevice,
                    factoryResetDevice = routerSettingsViewModel::factoryResetRouterDevice,
                    removeDevice = routerSettingsViewModel::removeRouterDevice
                )
            }
        }
    )
}

@Composable
private fun RouterSettingsContent(
    restartDevice: () -> Unit,
    factoryResetDevice: () -> Unit,
    removeDevice: () -> Unit,
) {
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
                onClick = restartDevice,
                modifier = Modifier.fillMaxWidth(),
            )
            AppButton(
                text = "Factory reset device",
                onClick = factoryResetDevice,
                modifier = Modifier.fillMaxWidth(),
            )
            AppButton(
                text = "Remove device",
                onClick = removeDevice,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

class RouterSettingsViewModel(
    private val getSelectedRouterDevice: GetSelectedRouterDeviceUseCase,
    private val doRouterDeviceAction: DoRouterDeviceActionUseCase
) : MviViewModel<ResourceState<RouterSettingsUiState, UiResourceError>>(ResourceState.None) {

    override suspend fun onInitState() = loadRouterDevice()

    private fun loadRouterDevice() = launchUpdateState { state ->
        getSelectedRouterDevice()
            .map { routerDevice -> Resource.Success(RouterSettingsUiState.DeviceLoaded(routerDevice = routerDevice)) }
            .dataOrElse { error -> state }
    }

    fun restartRouterDevice() = launchOnViewModelScope {
        val state = uiState.value
        updateState { ResourceState.Loading }
        if (state is Resource.Success) {
            val data = state.data
            if (data is RouterSettingsUiState.DeviceLoaded) updateState {
                doRouterDeviceAction(data.routerDevice, RouterDeviceAction.Restart)
                    .onFailure { loadRouterDevice() }
                    .flatMapData { state }
                    .flatMapError { error -> state }
            }
        }
    }

    fun factoryResetRouterDevice() = launchOnViewModelScope {
        val state = uiState.value
        updateState { ResourceState.Loading }
        if (state is Resource.Success) {
            val data = state.data
            if (data is RouterSettingsUiState.DeviceLoaded) updateState {
                doRouterDeviceAction(data.routerDevice, RouterDeviceAction.FactoryReset)
                    .onFailure { loadRouterDevice() }
                    .flatMapData { state }
                    .flatMapError { error -> state }
            }
        }
    }

    fun removeRouterDevice() = launchOnViewModelScope {
        val state = uiState.value
        updateState { ResourceState.Loading }
        if (state is Resource.Success) {
            val data = state.data
            if (data is RouterSettingsUiState.DeviceLoaded) updateState {
                doRouterDeviceAction(data.routerDevice, RouterDeviceAction.Remove)
                    .onFailure { loadRouterDevice() }
                    .map { RouterSettingsUiState.DeviceRemoved }
                    .flatMapError { error -> state }
            }
        }
    }
}

sealed interface RouterSettingsUiState {
    data class DeviceLoaded(
        val routerDevice: RouterDevice,
        val deviceRemoved: Boolean = false,
    ): RouterSettingsUiState
    data object DeviceRemoved : RouterSettingsUiState
}
