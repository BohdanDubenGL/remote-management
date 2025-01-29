package com.globallogic.rdkb.remotemanagement.view.screen.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.globallogic.rdkb.remotemanagement.domain.usecase.routerdeviceconnection.GetRouterDeviceListUseCase
import com.globallogic.rdkb.remotemanagement.view.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RouterDeviceListScreen(
    navController: NavController,
    routerDeviceListViewModel: RouterDeviceListViewModel = koinViewModel(),
) {
    val uiState by routerDeviceListViewModel.uiState.collectAsStateWithLifecycle()
    RouterDeviceList(
        uiState = uiState,
        loadRouterDevices = routerDeviceListViewModel::loadRouterDevices,
        onRouterDeviceClicked = { navController.navigate(Screen.RouterDeviceGraph.RouterDevice(it.macAddress)) },
    )
}

@Composable
private fun RouterDeviceList(
    uiState: RouterDeviceListUiState,
    loadRouterDevices: () -> Unit,
    onRouterDeviceClicked: (RouterDevice) -> Unit
) {
    SideEffect {
        loadRouterDevices()
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier.fillMaxSize(),
    ) {
        LazyColumn {
            items(uiState.routerDevices, RouterDevice::macAddress) { routerDevice ->
                Card(
                    modifier = Modifier
                        .padding(16.dp, 8.dp)
                        .clickable { onRouterDeviceClicked(routerDevice) }
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp, 8.dp).fillMaxWidth()
                    ) {
                        Text(text = "name: ${routerDevice.name}")
                        Text(text = "ip: ${routerDevice.ip}")
                        Text(text = "mac: ${routerDevice.macAddress}")
                    }
                }
            }
        }
    }
}

class RouterDeviceListViewModel(
    private val getRouterDeviceList: GetRouterDeviceListUseCase,
) : ViewModel() {
    private val _uiState: MutableStateFlow<RouterDeviceListUiState> = MutableStateFlow(RouterDeviceListUiState())
    val uiState: StateFlow<RouterDeviceListUiState> get() = _uiState.asStateFlow()

    fun loadRouterDevices() {
        viewModelScope.launch {
            val routerDevices = getRouterDeviceList()
            _uiState.update { it.copy(routerDevices = routerDevices) }
        }
    }
}

data class RouterDeviceListUiState(
    val routerDevices: List<RouterDevice> = emptyList()
)
