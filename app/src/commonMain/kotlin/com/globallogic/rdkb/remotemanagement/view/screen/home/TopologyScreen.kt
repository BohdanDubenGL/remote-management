package com.globallogic.rdkb.remotemanagement.view.screen.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDeviceTopologyData
import com.globallogic.rdkb.remotemanagement.domain.usecase.routerdevice.GetLocalRouterDeviceUseCase
import com.globallogic.rdkb.remotemanagement.domain.usecase.routerdevice.GetRouterDeviceTopologyDataUseCase
import com.globallogic.rdkb.remotemanagement.domain.utils.dataOrElse
import com.globallogic.rdkb.remotemanagement.view.base.MviViewModel
import com.globallogic.rdkb.remotemanagement.view.component.AppButton
import com.globallogic.rdkb.remotemanagement.view.component.AppCard
import com.globallogic.rdkb.remotemanagement.view.component.AppTitleText
import com.globallogic.rdkb.remotemanagement.view.component.Client
import com.globallogic.rdkb.remotemanagement.view.component.Network
import com.globallogic.rdkb.remotemanagement.view.component.Router
import com.globallogic.rdkb.remotemanagement.view.component.SetupFloatingActionButton
import com.globallogic.rdkb.remotemanagement.view.component.TopologyDiagram
import com.globallogic.rdkb.remotemanagement.view.navigation.FloatingActionButtonState
import com.globallogic.rdkb.remotemanagement.view.navigation.LocalNavController
import com.globallogic.rdkb.remotemanagement.view.navigation.Screen
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TopologyScreen(
    navController: NavController = LocalNavController.current,
    topologyViewModel: TopologyViewModel = koinViewModel(),
) {
    val uiState by topologyViewModel.uiState.collectAsStateWithLifecycle()
    TopologyContent(
        uiState = uiState,
        searchRouterDevices = { navController.navigate(Screen.ConnectionGraph.SearchRouterDevice) }
    )
}

@Composable
private fun TopologyContent(
    uiState: TopologyUiState,
    searchRouterDevices: () -> Unit,
) {
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
        AppCard(
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp)
                .padding(top = 32.dp, bottom = 72.dp),
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
) : MviViewModel<TopologyUiState>(TopologyUiState()) {

    override suspend fun onCollectState() = loadTopologyData()

    private fun loadTopologyData() = launchUpdateState { state ->
        val routerDevice = getLocalRouterDevice()
            .dataOrElse { error -> return@launchUpdateState state.copy(topologyDataLoaded = true) }
        val topologyData = getRouterDeviceTopologyData(routerDevice)
            .dataOrElse { error -> return@launchUpdateState state.copy(topologyDataLoaded = true) }
        state.copy(routerDevice = routerDevice, topologyData = topologyData, topologyDataLoaded = true)
    }
}

data class TopologyUiState(
    val routerDevice: RouterDevice? = null,
    val topologyData: RouterDeviceTopologyData? = null,
    val topologyDataLoaded: Boolean = false,
)
