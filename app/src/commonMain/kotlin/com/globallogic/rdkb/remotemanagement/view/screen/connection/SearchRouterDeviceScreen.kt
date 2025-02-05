package com.globallogic.rdkb.remotemanagement.view.screen.connection

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
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
import com.globallogic.rdkb.remotemanagement.domain.entity.FoundRouterDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice
import com.globallogic.rdkb.remotemanagement.domain.usecase.routerdeviceconnection.ConnectToRouterDeviceUseCase
import com.globallogic.rdkb.remotemanagement.domain.usecase.routerdeviceconnection.SearchRouterDevicesUseCase
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
fun SearchRouterDeviceScreen(
    navController: NavController = LocalNavController.current,
    searchRouterDeviceViewModel: SearchRouterDeviceViewModel = koinViewModel(),
) {
    val uiState by searchRouterDeviceViewModel.uiState.collectAsStateWithLifecycle()
    SearchRouterDeviceContent(
        uiState = uiState,
        searchDevices = searchRouterDeviceViewModel::searchDevices,
        connectToRouterDevice = searchRouterDeviceViewModel::connectToDevice,
        addDeviceManually = { navController.navigate(Screen.ConnectionGraph.AddRouterDeviceManually) {
            popUpTo<Screen.HomeGraph.Topology>()
        } },
        onDeviceConnected = navController::navigateUp
    )
}

@Composable
private fun SearchRouterDeviceContent(
    uiState: SearchRouterDeviceUiState,
    searchDevices: () -> Unit,
    connectToRouterDevice: (FoundRouterDevice) -> Unit,
    addDeviceManually: () -> Unit,
    onDeviceConnected: () -> Unit,
) {
    SideEffect {
        when (uiState) {
            is SearchRouterDeviceUiState.None -> searchDevices()
            is SearchRouterDeviceUiState.Connected -> onDeviceConnected()
            else -> Unit
        }
    }
    SetupFloatingActionButton(
        floatingActionButtonState = FloatingActionButtonState.Shown(
            buttonIcon = Icons.Default.Search,
            iconDescription = "Add manually",
            buttonAction = addDeviceManually
        )
    )
    when(uiState) {
        is SearchRouterDeviceUiState.None -> Unit
        is SearchRouterDeviceUiState.Searching -> {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize(),
            ) {
                Text(text = "Searching...")
            }
        }
        is SearchRouterDeviceUiState.FoundDevices -> {
            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top),
                modifier = Modifier.fillMaxSize().padding(24.dp, 8.dp)
            ) {
                items(uiState.foundRouterDevices) { foundDevice ->
                    Card(
                        shape = RoundedCornerShape(8.dp),
                        elevation = CardDefaults.cardElevation(4.dp),
                        modifier = Modifier.clickable { connectToRouterDevice(foundDevice) }
                    ) {
                        Column(
                            horizontalAlignment = Alignment.Start,
                            verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
                            modifier = Modifier.fillMaxWidth().padding(16.dp, 16.dp)
                        ) {
                            Text(text = "name: ${foundDevice.name}")
                            Text(text = "ip: ${foundDevice.ip}")
                            Text(text = "macAddress: ${foundDevice.macAddress}")
                        }
                    }
                }
            }
        }
        is SearchRouterDeviceUiState.Connecting -> {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize(),
            ) {
                Text(text = "Connecting...")
            }
        }
        is SearchRouterDeviceUiState.Connected -> {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize(),
            ) {
                Text(text = "Connected")
            }
        }
    }
}

class SearchRouterDeviceViewModel(
    private val searchRouterDevices: SearchRouterDevicesUseCase,
    private val connectToRouterDevice: ConnectToRouterDeviceUseCase,
) : ViewModel() {
    private val _uiState: MutableStateFlow<SearchRouterDeviceUiState> = MutableStateFlow(SearchRouterDeviceUiState.None)
    val uiState: StateFlow<SearchRouterDeviceUiState> get() = _uiState.asStateFlow()

    fun searchDevices() {
        viewModelScope.launch {
            _uiState.update { SearchRouterDeviceUiState.Searching }
            searchRouterDevices()
                .onSuccess { foundDevices -> _uiState.update { SearchRouterDeviceUiState.FoundDevices(foundRouterDevices = foundDevices) } }
                .onFailure { it.printStackTrace() }
        }
    }

    fun connectToDevice(foundRouterDevice: FoundRouterDevice) {
        viewModelScope.launch {
            _uiState.update { SearchRouterDeviceUiState.Connecting(foundRouterDevice = foundRouterDevice) }
            connectToRouterDevice(foundRouterDevice)
                .onSuccess { routerDevice -> _uiState.update { SearchRouterDeviceUiState.Connected(routerDevice = routerDevice) } }
                .onFailure { it.printStackTrace() }

        }
    }
}

sealed interface SearchRouterDeviceUiState {
    data object None : SearchRouterDeviceUiState
    data object Searching : SearchRouterDeviceUiState
    data class FoundDevices(val foundRouterDevices: List<FoundRouterDevice>) : SearchRouterDeviceUiState
    data class Connecting(val foundRouterDevice: FoundRouterDevice) : SearchRouterDeviceUiState
    data class Connected(val routerDevice: RouterDevice) : SearchRouterDeviceUiState
}
