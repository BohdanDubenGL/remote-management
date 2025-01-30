package com.globallogic.rdkb.remotemanagement.view.screen.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDeviceTopologyData
import com.globallogic.rdkb.remotemanagement.domain.usecase.routerdevice.GetLocalRouterDeviceUseCase
import com.globallogic.rdkb.remotemanagement.domain.usecase.routerdevice.GetRouterDeviceTopologyDataUseCase
import com.globallogic.rdkb.remotemanagement.view.FloatingActionButtonState
import com.globallogic.rdkb.remotemanagement.view.LocalNavController
import com.globallogic.rdkb.remotemanagement.view.Screen
import com.globallogic.rdkb.remotemanagement.view.component.SetupFloatingActionButton
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.Duration.Companion.seconds

@Composable
fun TopologyScreen(
    navController: NavController = LocalNavController.current,
    topologyViewModel: TopologyViewModel = koinViewModel(),
) {
    val uiState by topologyViewModel.uiState.collectAsStateWithLifecycle()
    TopologyContent(
        uiState = uiState,
        loadTopologyData = topologyViewModel::loadTopologyData,
        searchRouterDevices = { navController.navigate(Screen.ConnectionGraph.SearchRouterDevice) }
    )
}

@Composable
private fun TopologyContent(
    uiState: TopologyUiState,
    loadTopologyData: () -> Unit,
    searchRouterDevices: () -> Unit,
) {
    SideEffect {
        if (!uiState.topologyDataLoaded) loadTopologyData()
    }
    SetupFloatingActionButton(
        floatingActionButtonState = FloatingActionButtonState.Shown(
            buttonIcon = Icons.Default.Search,
            iconDescription = "",
            buttonAction = searchRouterDevices
        )
    )
    if (!uiState.topologyDataLoaded) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize(),
        ) {
            Text(text = "Loading...")
        }
    } else if (uiState.topologyData == RouterDeviceTopologyData.empty) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize(),
        ) {
            Text(text = "No data")
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = searchRouterDevices,
                content = { Text(text = "Search devices") }
            )
        }
    } else {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize(),
        ) {
            Card {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(16.dp, 8.dp),
                ) {
                    Text(text = "name: ${uiState.routerDevice.name}")
                    Text(text = "ip: ${uiState.routerDevice.ip}")
                    Text(text = "macAddress: ${uiState.routerDevice.macAddress}")
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                items(uiState.topologyData.connectedDevices) { connectedDevice ->
                    Card {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth().padding(16.dp, 8.dp),
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
}

class TopologyViewModel(
    private val getLocalRouterDevice: GetLocalRouterDeviceUseCase,
    private val getRouterDeviceTopologyData: GetRouterDeviceTopologyDataUseCase,
) : ViewModel() {
    private val _uiState: MutableStateFlow<TopologyUiState> = MutableStateFlow(TopologyUiState())
    val uiState: StateFlow<TopologyUiState> get() = _uiState.asStateFlow()

    fun loadTopologyData() {
        viewModelScope.launch {
            delay(2.seconds.inWholeMilliseconds) // todo: remove
            val routerDevice = getLocalRouterDevice()
            val topologyData = getRouterDeviceTopologyData(routerDevice)
            _uiState.update { it.copy(routerDevice = routerDevice, topologyData = topologyData, topologyDataLoaded = true) }
        }
    }
}

data class TopologyUiState(
    val routerDevice: RouterDevice = RouterDevice.empty,
    val topologyData: RouterDeviceTopologyData = RouterDeviceTopologyData.empty,
    val topologyDataLoaded: Boolean = false,
)
