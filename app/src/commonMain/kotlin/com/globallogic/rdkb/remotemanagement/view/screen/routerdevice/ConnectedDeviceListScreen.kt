package com.globallogic.rdkb.remotemanagement.view.screen.routerdevice

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.globallogic.rdkb.remotemanagement.domain.entity.ConnectedDevice
import com.globallogic.rdkb.remotemanagement.domain.usecase.routerdevice.GetRouterDeviceConnectedDevicesUseCase
import com.globallogic.rdkb.remotemanagement.domain.usecase.routerdevice.GetSelectedRouterDeviceUseCase
import com.globallogic.rdkb.remotemanagement.domain.utils.dataOrElse
import com.globallogic.rdkb.remotemanagement.view.base.MviViewModel
import com.globallogic.rdkb.remotemanagement.view.component.AppCard
import com.globallogic.rdkb.remotemanagement.view.component.AppIcon
import com.globallogic.rdkb.remotemanagement.view.component.AppTextProperty
import com.globallogic.rdkb.remotemanagement.view.component.AppTitleText
import com.globallogic.rdkb.remotemanagement.view.navigation.LocalNavController
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ConnectedDeviceListScreen(
    navController: NavController = LocalNavController.current,
    connectedDeviceListViewModel: ConnectedDeviceListViewModel = koinViewModel(),
) {
    val uiState by connectedDeviceListViewModel.uiState.collectAsStateWithLifecycle()
    ConnectedDeviceListContent(
        uiState = uiState,
    )
}

@Composable
private fun ConnectedDeviceListContent(
    uiState: ConnectedDeviceListUiState,
) {
    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.Top),
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
    ) {
        items(uiState.connectedDevices, ConnectedDevice::macAddress) { connectedDevice ->
            AppCard(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp).fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        AppIcon(
                            imageVector = Icons.Default.Devices,
                            contentColor = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.weight(1F))
                        AppTitleText(text = connectedDevice.hostName, color = MaterialTheme.colorScheme.tertiary)
                    }

                    AppTextProperty(name = "macAddress:", value = connectedDevice.macAddress)
                    AppTextProperty(name = "hostName:", value = connectedDevice.hostName)
                    AppTextProperty(name = "ssid:", value = connectedDevice.ssid)
                    AppTextProperty(name = "channel:", value = connectedDevice.channel.toString())
                    AppTextProperty(name = "rssi:", value = connectedDevice.rssi.toString())
                    AppTextProperty(name = "bandWidth:", value = connectedDevice.bandWidth)
                }
            }
        }
    }
}

class ConnectedDeviceListViewModel(
    private val getSelectedRouterDevice: GetSelectedRouterDeviceUseCase,
    private val getRouterDeviceConnectedDevices: GetRouterDeviceConnectedDevicesUseCase,
) : MviViewModel<ConnectedDeviceListUiState>(ConnectedDeviceListUiState()) {

    override suspend fun onInitState() = loadConnectedDevices()

    private fun loadConnectedDevices() = launchUpdateState { state ->
        val routerDevice = getSelectedRouterDevice()
            .dataOrElse { error -> return@launchUpdateState state }
        val connectedDevices = getRouterDeviceConnectedDevices(routerDevice)
            .dataOrElse { error -> return@launchUpdateState state }
        state.copy(connectedDevices = connectedDevices)
    }
}

data class ConnectedDeviceListUiState(
    val connectedDevices: List<ConnectedDevice> = emptyList(),
)
