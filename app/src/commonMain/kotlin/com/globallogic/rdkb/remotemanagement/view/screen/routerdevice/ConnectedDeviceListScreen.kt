package com.globallogic.rdkb.remotemanagement.view.screen.routerdevice

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.globallogic.rdkb.remotemanagement.domain.entity.ConnectedDevice
import com.globallogic.rdkb.remotemanagement.domain.usecase.routerdevice.GetRouterDeviceConnectedDevicesUseCase
import com.globallogic.rdkb.remotemanagement.domain.usecase.routerdevice.GetSelectedRouterDeviceUseCase
import com.globallogic.rdkb.remotemanagement.view.LocalNavController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ConnectedDeviceListScreen(
    navController: NavController = LocalNavController.current,
    connectedDeviceListViewModel: ConnectedDeviceListViewModel = koinViewModel(),
) {
    val uiState by connectedDeviceListViewModel.uiState.collectAsStateWithLifecycle()
    ConnectedDeviceListContent(
        uiState = uiState,
        loadConnectedDevices = connectedDeviceListViewModel::loadConnectedDevices,
    )
}

@Composable
private fun ConnectedDeviceListContent(
    uiState: ConnectedDeviceListUiState,
    loadConnectedDevices: () -> Unit
) {
    SideEffect {
        loadConnectedDevices()
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier.fillMaxSize(),
    ) {
        LazyColumn {
            items(uiState.connectedDevices, ConnectedDevice::macAddress) { connectedDevice ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    modifier = Modifier.padding(16.dp, 8.dp),
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp, 8.dp).fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = "macAddress: ${connectedDevice.macAddress}")
                        Text(text = "hostName: ${connectedDevice.hostName}")
                        Text(text = "ssid: ${connectedDevice.ssid}")
                        Text(text = "channel: ${connectedDevice.channel}")
                        Text(text = "rssi: ${connectedDevice.rssi}")
                        Text(text = "bandWidth: ${connectedDevice.bandWidth}")
                    }
                }
            }
        }
    }
}

class ConnectedDeviceListViewModel(
    private val getSelectedRouterDevice: GetSelectedRouterDeviceUseCase,
    private val getRouterDeviceConnectedDevices: GetRouterDeviceConnectedDevicesUseCase,
) : ViewModel() {
    private val _uiState: MutableStateFlow<ConnectedDeviceListUiState> = MutableStateFlow(ConnectedDeviceListUiState())
    val uiState: StateFlow<ConnectedDeviceListUiState> get() = _uiState.asStateFlow()

    fun loadConnectedDevices() {
        viewModelScope.launch {
            val routerDevice = getSelectedRouterDevice()
            val connectedDevices = getRouterDeviceConnectedDevices(routerDevice)
            _uiState.update { it.copy(connectedDevices = connectedDevices) }
        }
    }
}

data class ConnectedDeviceListUiState(
    val connectedDevices: List<ConnectedDevice> = emptyList(),
)
