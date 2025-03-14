package com.globallogic.rdkb.remotemanagement.view.screen.connection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.globallogic.rdkb.remotemanagement.domain.error.DeviceError
import com.globallogic.rdkb.remotemanagement.domain.usecase.routerdeviceconnection.AddRouterDeviceManuallyUseCase
import com.globallogic.rdkb.remotemanagement.domain.utils.dataOrElse
import com.globallogic.rdkb.remotemanagement.domain.utils.map
import com.globallogic.rdkb.remotemanagement.view.base.MviViewModel
import com.globallogic.rdkb.remotemanagement.view.component.AppButton
import com.globallogic.rdkb.remotemanagement.view.component.AppTextField
import com.globallogic.rdkb.remotemanagement.view.navigation.LocalNavController
import com.globallogic.rdkb.remotemanagement.view.navigation.Screen
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
            isError = uiState.deviceMacAddressErrorMessage.isNotBlank(),
            errorMessage = uiState.deviceMacAddressErrorMessage,
            placeholder = "Enter device mac address",
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Go,
            ),
            keyboardActions = KeyboardActions(
                onGo = { connectToDevice() }
            ),
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
) : MviViewModel<AddRouterDeviceManuallyUiState>(AddRouterDeviceManuallyUiState()) {

    fun onMacAddressEntered(macAddress: String) = updateState { state ->
        state.copy(deviceMacAddress = macAddress)
    }

    fun connectToDevice() = launchUpdateStateFromFlow { state ->
        send(state.copy(deviceMacAddressErrorMessage = "", deviceConnected = false))
        val newState = addRouterDeviceManually(state.deviceMacAddress)
            .map { routerDevice -> state.copy(deviceConnected = true) }
            .dataOrElse { error -> when(error) {
                is DeviceError.WrongMacAddressFormat -> state.copy(deviceMacAddressErrorMessage = "Wrong mac address format")
                is DeviceError.CantConnectToRouterDevice -> state.copy(deviceMacAddressErrorMessage = "Can't connect to device")
            } }
        send(newState)
    }
}

data class AddRouterDeviceManuallyUiState(
    val deviceMacAddress: String = "",
    val deviceMacAddressErrorMessage: String = "",
    val deviceConnected: Boolean = false,
)
