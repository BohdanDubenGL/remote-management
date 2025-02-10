package com.globallogic.rdkb.remotemanagement.view.screen.connection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import com.globallogic.rdkb.remotemanagement.domain.usecase.routerdeviceconnection.AddRouterDeviceManuallyUseCase
import com.globallogic.rdkb.remotemanagement.view.component.AppButton
import com.globallogic.rdkb.remotemanagement.view.component.AppTextField
import com.globallogic.rdkb.remotemanagement.view.navigation.LocalNavController
import com.globallogic.rdkb.remotemanagement.view.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AddRouterDeviceManuallyScreen(
    navController: NavController = LocalNavController.current,
    addRouterDeviceManuallyViewModel: AddRouterDeviceManuallyViewModel = koinViewModel(),
) {
    val uiState by addRouterDeviceManuallyViewModel.uiState.collectAsStateWithLifecycle()
    AddRouterDeviceManuallyContent(
        uiState = uiState,
        onMacAddressEntered = addRouterDeviceManuallyViewModel::onMacAddressEntered,
        connectToDevice = addRouterDeviceManuallyViewModel::connectToDevice,
        onDeviceConnected = { navController.navigate(Screen.HomeGraph.Topology) {
            popUpTo<Screen.HomeGraph.Topology> {
                inclusive = true
            }
        } },
    )
}

@Composable
private fun AddRouterDeviceManuallyContent(
    uiState: AddRouterDeviceManuallyUiState,
    onMacAddressEntered: (String) -> Unit,
    connectToDevice: () -> Unit,
    onDeviceConnected: () -> Unit,
) {
    SideEffect {
        if (uiState.deviceConnected) onDeviceConnected()
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
    ) {
        AppTextField(
            value = uiState.deviceMacAddress,
            onValueChange = onMacAddressEntered,
            label = "Mac address",
            placeholder = "Enter device mac address",
        )
        Spacer(modifier = Modifier.height(16.dp))
        AppButton(
            text = "Connect",
            onClick = connectToDevice,
        )
    }
}

class AddRouterDeviceManuallyViewModel(
    private val addRouterDeviceManually: AddRouterDeviceManuallyUseCase,
) : ViewModel() {
    private val _uiState: MutableStateFlow<AddRouterDeviceManuallyUiState> = MutableStateFlow(AddRouterDeviceManuallyUiState())
    val uiState: StateFlow<AddRouterDeviceManuallyUiState> get() = _uiState.asStateFlow()

    fun onMacAddressEntered(macAddress: String) {
        _uiState.update { it.copy(deviceMacAddress = macAddress) }
    }

    fun connectToDevice() {
        viewModelScope.launch {
            addRouterDeviceManually(_uiState.value.deviceMacAddress)
                .onSuccess { routerDevice ->
                    if (routerDevice != null) {
                        _uiState.update { it.copy(deviceConnected = true) }
                    }
                }
                .onFailure { it.printStackTrace() }
        }
    }
}

data class AddRouterDeviceManuallyUiState(
    val deviceMacAddress: String = "",
    val deviceConnected: Boolean = false,
)
