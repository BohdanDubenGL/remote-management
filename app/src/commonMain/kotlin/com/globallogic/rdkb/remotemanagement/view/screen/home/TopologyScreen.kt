package com.globallogic.rdkb.remotemanagement.view.screen.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Devices
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.Router
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.globallogic.rdkb.remotemanagement.view.component.AppDrawResourceState
import com.globallogic.rdkb.remotemanagement.view.component.AppTitleText
import com.globallogic.rdkb.remotemanagement.view.component.SetupFloatingActionButton
import com.globallogic.rdkb.remotemanagement.view.component.TopologyDiagram
import com.globallogic.rdkb.remotemanagement.view.component.TopologyNode
import com.globallogic.rdkb.remotemanagement.view.error.UiResourceError
import com.globallogic.rdkb.remotemanagement.view.navigation.FloatingActionButtonState
import com.globallogic.rdkb.remotemanagement.view.navigation.LocalNavController
import com.globallogic.rdkb.remotemanagement.view.navigation.Screen
import kotlinx.coroutines.flow.collectLatest
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
        is TopologyUiState.Data -> {
            TopologyDiagram(
                network = TopologyNode(
                    name = "Internet",
                    icon = Icons.Outlined.Public,
                    iconColor = Color(0xff25ade8),
                    rippleColor = MaterialTheme.colorScheme.tertiaryContainer,
                    rippleIntervalMillis = 1_000L,
                    rippleDurationMillis = 5_000,
                    rippleTarget = 300F,
                    rippleCount = 3,
                ),
                router = TopologyNode(
                    name = uiState.routerDevice.modelName,
                    icon = Icons.Outlined.Router,
                    iconColor = Color(0xffe89d25),
                    rippleColor = MaterialTheme.colorScheme.tertiaryContainer,
                    rippleIntervalMillis = 2_500L,
                    rippleDurationMillis = 10_000,
                    rippleTarget = 600F,
                    rippleCount = 5,
                ),
                clients = uiState.connectedDevices.map { connectedDevice ->
                    TopologyNode(
                        name = connectedDevice.hostName,
                        icon = Icons.Outlined.Devices,
                        iconColor = Color(0xff25e8d5),
                        rippleColor = MaterialTheme.colorScheme.tertiaryContainer,
                        rippleIntervalMillis = 500L,
                        rippleDurationMillis = 1_500,
                        rippleTarget = 150F,
                        rippleCount = 3,
                    )
                },
                modifier = Modifier.fillMaxSize(),
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
        val routerDevice = getLocalRouterDevice()
            .dataOrElse { error ->
                updateState { Resource.Success(TopologyUiState.NoData) }
                return@launchOnViewModelScope
            }
        getRouterDeviceConnectedDevices(routerDevice).collectLatest { connectedDevices ->
            updateState { state ->
                when(connectedDevices) {
                    is ResourceState.None -> connectedDevices
                    is ResourceState.Loading -> connectedDevices
                    is Resource -> Resource.Success(TopologyUiState.Data(
                        routerDevice = routerDevice,
                        connectedDevices = connectedDevices
                            .dataOrElse { error -> return@updateState Resource.Success(TopologyUiState.NoData) }
                    ))
                }
            }
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
