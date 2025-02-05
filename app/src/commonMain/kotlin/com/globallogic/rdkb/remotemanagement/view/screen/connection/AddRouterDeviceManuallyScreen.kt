package com.globallogic.rdkb.remotemanagement.view.screen.connection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice
import com.globallogic.rdkb.remotemanagement.domain.usecase.routerdeviceconnection.AddRouterDeviceManuallyUseCase
import com.globallogic.rdkb.remotemanagement.view.LocalNavController
import com.globallogic.rdkb.remotemanagement.view.Screen
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
        modifier = Modifier.fillMaxSize(),
    ) {
        TextField(
            value = uiState.deviceMacAddress,
            onValueChange = onMacAddressEntered,
            label = { Text(text = "Mac address") },
            placeholder = { Text(text = "Enter device mac address") },
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = connectToDevice,
            content = { Text(text = "Connect") }
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
