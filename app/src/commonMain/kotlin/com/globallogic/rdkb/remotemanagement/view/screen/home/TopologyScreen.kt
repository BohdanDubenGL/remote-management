package com.globallogic.rdkb.remotemanagement.view.screen.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
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
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDeviceTopologyData
import com.globallogic.rdkb.remotemanagement.domain.usecase.routerdevice.GetLocalRouterDeviceUseCase
import com.globallogic.rdkb.remotemanagement.domain.usecase.routerdevice.GetRouterDeviceTopologyDataUseCase
import com.globallogic.rdkb.remotemanagement.view.component.AppButton
import com.globallogic.rdkb.remotemanagement.view.component.AppErrorWithButton
import com.globallogic.rdkb.remotemanagement.view.component.AppLoadingWithButton
import com.globallogic.rdkb.remotemanagement.view.component.AppTitleText
import com.globallogic.rdkb.remotemanagement.view.navigation.FloatingActionButtonState
import com.globallogic.rdkb.remotemanagement.view.navigation.LocalNavController
import com.globallogic.rdkb.remotemanagement.view.navigation.Screen
import com.globallogic.rdkb.remotemanagement.view.component.Client
import com.globallogic.rdkb.remotemanagement.view.component.Network
import com.globallogic.rdkb.remotemanagement.view.component.Router
import com.globallogic.rdkb.remotemanagement.view.component.SetupFloatingActionButton
import com.globallogic.rdkb.remotemanagement.view.component.TopologyDiagram
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

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
        loadTopologyData()
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
            AppTitleText(text = "Loading...")
        }
    } else if (uiState.topologyData == null) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize(),
        ) {
            AppTitleText(text = "No data")
            Spacer(modifier = Modifier.height(16.dp))
            AppButton(
                text = "Search devices",
                onClick = searchRouterDevices,
                modifier = Modifier.width(250.dp)
            )
        }
    } else {
        Card(
            elevation = CardDefaults.cardElevation(8.dp),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
        ) {
            TopologyDiagram(
                Network("Internet"),
                Router(uiState.topologyData.routerDevice.name),
                uiState.topologyData.connectedDevices.map { Client(it.hostName) }
            )
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
            getLocalRouterDevice()
                .mapCatching { routerDevice ->
                    val topologyData = routerDevice?.let { getRouterDeviceTopologyData(it).getOrThrow() }
                    _uiState.update { it.copy(routerDevice = routerDevice, topologyData = topologyData, topologyDataLoaded = true) }
                }
                .onFailure { it.printStackTrace() }
        }
    }
}

data class TopologyUiState(
    val routerDevice: RouterDevice? = null,
    val topologyData: RouterDeviceTopologyData? = null,
    val topologyDataLoaded: Boolean = false,
)
