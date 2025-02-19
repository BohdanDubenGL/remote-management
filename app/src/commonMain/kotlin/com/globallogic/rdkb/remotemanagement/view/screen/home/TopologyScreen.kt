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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.globallogic.rdkb.remotemanagement.domain.entity.ConnectedDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice
import com.globallogic.rdkb.remotemanagement.domain.usecase.routerdevice.GetLocalRouterDeviceUseCase
import com.globallogic.rdkb.remotemanagement.domain.usecase.routerdevice.GetRouterDeviceConnectedDevicesUseCase
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource
import com.globallogic.rdkb.remotemanagement.domain.utils.ResourceState
import com.globallogic.rdkb.remotemanagement.domain.utils.dataOrElse
import com.globallogic.rdkb.remotemanagement.view.base.MviViewModel
import com.globallogic.rdkb.remotemanagement.view.component.AppButton
import com.globallogic.rdkb.remotemanagement.view.component.AppCard
import com.globallogic.rdkb.remotemanagement.view.component.AppDrawResourceState
import com.globallogic.rdkb.remotemanagement.view.component.AppTitleText
import com.globallogic.rdkb.remotemanagement.view.component.Client
import com.globallogic.rdkb.remotemanagement.view.component.Network
import com.globallogic.rdkb.remotemanagement.view.component.Router
import com.globallogic.rdkb.remotemanagement.view.component.SetupFloatingActionButton
import com.globallogic.rdkb.remotemanagement.view.component.TopologyDiagram
import com.globallogic.rdkb.remotemanagement.view.error.UiResourceError
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

    AppDrawResourceState(
        resourceState = uiState,
        onSuccess = { state ->
            TopologyContent(
                uiState = state,
                searchRouterDevices = { navController.navigate(Screen.ConnectionGraph.SearchRouterDevice) }
            )
        }
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
    when(uiState) {
        is TopologyUiState.NoData -> Column(
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
        is TopologyUiState.Data -> AppCard(
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp)
                .padding(top = 32.dp, bottom = 72.dp),
        ) {
            TopologyDiagram(
                Network("Internet"),
                Router(uiState.routerDevice.modelName),
                uiState.connectedDevices.map { Client(it.hostName) }
            )
        }
    }
}

class TopologyViewModel(
    private val getLocalRouterDevice: GetLocalRouterDeviceUseCase,
    private val getRouterDeviceConnectedDevices: GetRouterDeviceConnectedDevicesUseCase,
) : MviViewModel<ResourceState<TopologyUiState, UiResourceError>>(ResourceState.None) {

    override suspend fun onSubscribeState() = loadTopologyData()

    fun loadTopologyData() = launchOnViewModelScope {
        updateState { ResourceState.Loading }
        updateState { state ->
            val routerDevice = getLocalRouterDevice()
                .dataOrElse { error -> return@updateState Resource.Success(TopologyUiState.NoData) }
            val connectedDevices = getRouterDeviceConnectedDevices(routerDevice)
                .dataOrElse { error -> return@updateState Resource.Success(TopologyUiState.NoData) }
            Resource.Success(TopologyUiState.Data(routerDevice = routerDevice, connectedDevices = connectedDevices))
        }
    }
}

sealed interface TopologyUiState {
    data object NoData : TopologyUiState
    data class Data(
        val routerDevice: RouterDevice,
        val connectedDevices: List<ConnectedDevice>,
    ) : TopologyUiState
}
