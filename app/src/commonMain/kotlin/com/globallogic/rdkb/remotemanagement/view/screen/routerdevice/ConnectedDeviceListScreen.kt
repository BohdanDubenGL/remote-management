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
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource
import com.globallogic.rdkb.remotemanagement.domain.utils.ResourceState
import com.globallogic.rdkb.remotemanagement.domain.utils.dataOrElse
import com.globallogic.rdkb.remotemanagement.view.base.MviViewModel
import com.globallogic.rdkb.remotemanagement.view.component.AppCard
import com.globallogic.rdkb.remotemanagement.view.component.AppDrawResourceState
import com.globallogic.rdkb.remotemanagement.view.component.AppIcon
import com.globallogic.rdkb.remotemanagement.view.component.AppTextProperty
import com.globallogic.rdkb.remotemanagement.view.component.AppTitleText
import com.globallogic.rdkb.remotemanagement.view.component.AppTitleTextWithIcon
import com.globallogic.rdkb.remotemanagement.view.error.UiResourceError
import com.globallogic.rdkb.remotemanagement.view.navigation.LocalNavController
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ConnectedDeviceListScreen(
    navController: NavController = LocalNavController.current,
    connectedDeviceListViewModel: ConnectedDeviceListViewModel = koinViewModel(),
) {
    val uiState by connectedDeviceListViewModel.uiState.collectAsStateWithLifecycle()

    AppDrawResourceState(
        resourceState = uiState,
        onSuccess = { state ->
            ConnectedDeviceListContent(
                uiState = state,
            )
        }
    )
}

@Composable
private fun ConnectedDeviceListContent(
    uiState: ConnectedDeviceListUiState,
) {
    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.Top),
        modifier = Modifier.fillMaxSize(),
    ) {
        items(uiState.connectedDevices, ConnectedDevice::macAddress) { connectedDevice ->
            AppCard(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp).fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AppTitleTextWithIcon(
                        text = connectedDevice.hostName,
                        imageVector = Icons.Default.Devices,
                    )
                    AppTextProperty(name = "Online:", value = connectedDevice.isActive)
                    AppTextProperty(name = "Mac address:", value = connectedDevice.macAddress)
                    AppTextProperty(name = "Host name:", value = connectedDevice.hostName)
                    AppTextProperty(name = "Ip address:", value = connectedDevice.ipAddress)
                    AppTextProperty(name = "Vendor class:", value = connectedDevice.vendorClassId)
                }
            }
        }
    }
}

class ConnectedDeviceListViewModel(
    private val getSelectedRouterDevice: GetSelectedRouterDeviceUseCase,
    private val getRouterDeviceConnectedDevices: GetRouterDeviceConnectedDevicesUseCase,
) : MviViewModel<ResourceState<ConnectedDeviceListUiState, UiResourceError>>(ResourceState.None) {

    override suspend fun onInitState() = loadConnectedDevices()

    private fun loadConnectedDevices() = launchOnViewModelScope {
        updateState { state -> ResourceState.Loading }
        updateState { state ->
            val routerDevice = getSelectedRouterDevice()
                .dataOrElse { error -> return@updateState state }
            val connectedDevices = getRouterDeviceConnectedDevices(routerDevice)
                .dataOrElse { error -> return@updateState state }
            Resource.Success(ConnectedDeviceListUiState(connectedDevices = connectedDevices))
        }
    }
}

data class ConnectedDeviceListUiState(
    val connectedDevices: List<ConnectedDevice> = emptyList(),
)
