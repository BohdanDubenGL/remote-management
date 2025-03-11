package com.globallogic.rdkb.remotemanagement.view.screen.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Router
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice
import com.globallogic.rdkb.remotemanagement.domain.usecase.routerdevice.SelectRouterDeviceUseCase
import com.globallogic.rdkb.remotemanagement.domain.usecase.routerdeviceconnection.GetRouterDeviceListUseCase
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource
import com.globallogic.rdkb.remotemanagement.domain.utils.ResourceState
import com.globallogic.rdkb.remotemanagement.domain.utils.dataOrElse
import com.globallogic.rdkb.remotemanagement.domain.utils.map
import com.globallogic.rdkb.remotemanagement.domain.utils.mapErrorToData
import com.globallogic.rdkb.remotemanagement.view.base.MviViewModel
import com.globallogic.rdkb.remotemanagement.view.component.AppButton
import com.globallogic.rdkb.remotemanagement.view.component.AppCard
import com.globallogic.rdkb.remotemanagement.view.component.AppDrawResourceState
import com.globallogic.rdkb.remotemanagement.view.component.AppTextProperty
import com.globallogic.rdkb.remotemanagement.view.component.AppTitleText
import com.globallogic.rdkb.remotemanagement.view.component.AppTitleTextWithIcon
import com.globallogic.rdkb.remotemanagement.view.error.UiResourceError
import com.globallogic.rdkb.remotemanagement.view.navigation.LocalNavController
import com.globallogic.rdkb.remotemanagement.view.navigation.Screen
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RouterDeviceListScreen(
    navController: NavController = LocalNavController.current,
    routerDeviceListViewModel: RouterDeviceListViewModel = koinViewModel(),
) {
    val uiState by routerDeviceListViewModel.uiState.collectAsStateWithLifecycle()

    AppDrawResourceState(
        resourceState = uiState,
        onSuccess =  { state ->
            RouterDeviceListContent(
                uiState = state,
                searchRouterDevices = {
                    navController.navigate(Screen.ConnectionGraph.SearchRouterDevice)
                },
                onRouterDeviceClicked = { routerDevice ->
                    routerDeviceListViewModel.onRouterDeviceSelected(routerDevice)
                    navController.navigate(Screen.RouterDeviceGraph)
                },
            )
        }
    )
}

@Composable
private fun RouterDeviceListContent(
    uiState: RouterDeviceListUiState,
    searchRouterDevices: () -> Unit,
    onRouterDeviceClicked: (RouterDevice) -> Unit,
) {
    if (uiState.routerDevices.isEmpty()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize(),
        ) {
            AppTitleText(text = "No saved devices")
            Spacer(modifier = Modifier.height(16.dp))
            AppButton(
                text = "Search devices",
                onClick = searchRouterDevices,
                modifier = Modifier.width(250.dp)
            )
        }
    } else {
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier.fillMaxSize(),
        ) {
            items(uiState.routerDevices) { routerDevice ->
                AppCard(
                    modifier = Modifier
                        .padding(16.dp, 8.dp)
                        .clickable { onRouterDeviceClicked(routerDevice) }
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp, 8.dp).fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AppTitleTextWithIcon(
                            text = routerDevice.modelName,
                            imageVector = Icons.Default.Router,
                        )
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(start = 48.dp)
                        ) {
                            AppTextProperty(name = "ip:", value = routerDevice.ipAddressV4)
                            AppTextProperty(name = "mac:", value = routerDevice.macAddress)
                        }
                    }
                }
            }
        }
    }
}

class RouterDeviceListViewModel(
    private val getRouterDeviceList: GetRouterDeviceListUseCase,
    private val selectRouterDevice: SelectRouterDeviceUseCase,
) : MviViewModel<ResourceState<RouterDeviceListUiState, UiResourceError>>(ResourceState.None) {
    override suspend fun onSubscribeState() {
        loadRouterDevices()
    }

    private fun loadRouterDevices() = launchOnViewModelScope {
        getRouterDeviceList()
            .collectLatest { routerDevices ->
                updateState { state ->
                    when(routerDevices) {
                        is ResourceState.None -> routerDevices
                        is ResourceState.Loading -> routerDevices
                        is Resource -> routerDevices
                            .map { routerDevices -> RouterDeviceListUiState(routerDevices = routerDevices) }
                            .mapErrorToData { error -> RouterDeviceListUiState(routerDevices = emptyList()) }
                    }
                }
            }
    }

    fun onRouterDeviceSelected(routerDevice: RouterDevice) = launchUpdateState { state ->
        selectRouterDevice(routerDevice)
            .map { state }
            .dataOrElse { error -> state }
    }
}

data class RouterDeviceListUiState(
    val routerDevices: List<RouterDevice>
)
