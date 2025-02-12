package com.globallogic.rdkb.remotemanagement.view.screen.home

import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Router
import androidx.compose.material3.MaterialTheme
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
import com.globallogic.rdkb.remotemanagement.domain.utils.dataOrElse
import com.globallogic.rdkb.remotemanagement.domain.utils.map
import com.globallogic.rdkb.remotemanagement.view.base.MviViewModel
import com.globallogic.rdkb.remotemanagement.view.component.AppCard
import com.globallogic.rdkb.remotemanagement.view.component.AppIcon
import com.globallogic.rdkb.remotemanagement.view.component.AppTextProperty
import com.globallogic.rdkb.remotemanagement.view.component.AppTitleText
import com.globallogic.rdkb.remotemanagement.view.navigation.LocalNavController
import com.globallogic.rdkb.remotemanagement.view.navigation.Screen
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RouterDeviceListScreen(
    navController: NavController = LocalNavController.current,
    routerDeviceListViewModel: RouterDeviceListViewModel = koinViewModel(),
) {
    val uiState by routerDeviceListViewModel.uiState.collectAsStateWithLifecycle()

    RouterDeviceListContent(
        uiState = uiState,
        onRouterDeviceClicked = { routerDevice ->
            routerDeviceListViewModel.onRouterDeviceSelected(routerDevice)
            navController.navigate(Screen.RouterDeviceGraph)
        },
    )
}

@Composable
private fun RouterDeviceListContent(
    uiState: RouterDeviceListUiState,
    onRouterDeviceClicked: (RouterDevice) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier.fillMaxSize(),
    ) {
        LazyColumn {
            items(uiState.routerDevices) { routerDevice ->
                AppCard(
                    modifier = Modifier
                        .padding(16.dp, 8.dp)
                        .clickable { onRouterDeviceClicked(routerDevice) }
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp, 8.dp).fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            AppIcon(
                                imageVector = Icons.Default.Router,
                                contentColor = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.weight(1F))
                            AppTitleText(text = routerDevice.name, color = MaterialTheme.colorScheme.tertiary)
                        }

                        AppTextProperty(name = "Name:", value = routerDevice.name)
                        AppTextProperty(name = "IP address:", value = routerDevice.ip)
                        AppTextProperty(name = "MAC address:", value = routerDevice.macAddress)
                    }
                }
            }
        }
    }
}

class RouterDeviceListViewModel(
    private val getRouterDeviceList: GetRouterDeviceListUseCase,
    private val selectRouterDevice: SelectRouterDeviceUseCase,
) : MviViewModel<RouterDeviceListUiState>(RouterDeviceListUiState()) {
    override suspend fun onCollectState() {
        loadRouterDevices()
    }

    private fun loadRouterDevices() = launchUpdateState { state ->
        getRouterDeviceList()
            .map { routerDevices -> state.copy(routerDevices = routerDevices) }
            .dataOrElse { error -> state }
    }

    fun onRouterDeviceSelected(routerDevice: RouterDevice) = launchUpdateState { state ->
        selectRouterDevice(routerDevice)
            .map { state }
            .dataOrElse { error -> state }
    }
}

data class RouterDeviceListUiState(
    val routerDevices: List<RouterDevice> = emptyList()
)
