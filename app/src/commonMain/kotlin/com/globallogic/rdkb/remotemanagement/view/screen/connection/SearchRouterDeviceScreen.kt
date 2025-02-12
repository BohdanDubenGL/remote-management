package com.globallogic.rdkb.remotemanagement.view.screen.connection

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
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.globallogic.rdkb.remotemanagement.domain.entity.FoundRouterDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice
import com.globallogic.rdkb.remotemanagement.domain.usecase.routerdeviceconnection.ConnectToRouterDeviceUseCase
import com.globallogic.rdkb.remotemanagement.domain.usecase.routerdeviceconnection.SearchRouterDevicesUseCase
import com.globallogic.rdkb.remotemanagement.domain.utils.dataOrElse
import com.globallogic.rdkb.remotemanagement.domain.utils.map
import com.globallogic.rdkb.remotemanagement.view.base.MviViewModel
import com.globallogic.rdkb.remotemanagement.view.component.AppCard
import com.globallogic.rdkb.remotemanagement.view.component.AppIcon
import com.globallogic.rdkb.remotemanagement.view.component.AppTextProperty
import com.globallogic.rdkb.remotemanagement.view.component.AppTitleText
import com.globallogic.rdkb.remotemanagement.view.component.SetupFloatingActionButton
import com.globallogic.rdkb.remotemanagement.view.navigation.FloatingActionButtonState
import com.globallogic.rdkb.remotemanagement.view.navigation.LocalNavController
import com.globallogic.rdkb.remotemanagement.view.navigation.Screen
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SearchRouterDeviceScreen(
    navController: NavController = LocalNavController.current,
    searchRouterDeviceViewModel: SearchRouterDeviceViewModel = koinViewModel(),
) {
    val uiState by searchRouterDeviceViewModel.uiState.collectAsStateWithLifecycle()
    SearchRouterDeviceContent(
        uiState = uiState,
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
    connectToRouterDevice: (FoundRouterDevice) -> Unit,
    addDeviceManually: () -> Unit,
    onDeviceConnected: () -> Unit,
) {
    SideEffect {
        if (uiState is SearchRouterDeviceUiState.Connected) onDeviceConnected()
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
                    AppCard(
                        modifier = Modifier.clickable { connectToRouterDevice(foundDevice) }
                    ) {
                        Column(
                            horizontalAlignment = Alignment.Start,
                            verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
                            modifier = Modifier.fillMaxWidth().padding(16.dp, 16.dp)
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
                                AppTitleText(text = foundDevice.name, color = MaterialTheme.colorScheme.tertiary)
                            }

                            AppTextProperty(name = "Name:", value = foundDevice.name)
                            AppTextProperty(name = "IP address:", value = foundDevice.ip)
                            AppTextProperty(name = "MAC address:", value = foundDevice.macAddress)
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
) : MviViewModel<SearchRouterDeviceUiState>(SearchRouterDeviceUiState.None) {

    override suspend fun onInitState() = searchDevices()

    private fun searchDevices() = launchOnViewModelScope {
        updateState { SearchRouterDeviceUiState.Searching }
        updateState { state ->
            searchRouterDevices()
                .map { foundDevices -> SearchRouterDeviceUiState.FoundDevices(foundRouterDevices = foundDevices) }
                .dataOrElse { error -> state }
        }
    }

    fun connectToDevice(foundRouterDevice: FoundRouterDevice) = launchOnViewModelScope {
        updateState { SearchRouterDeviceUiState.Connecting(foundRouterDevice = foundRouterDevice) }
        updateState { state ->
            connectToRouterDevice(foundRouterDevice)
                .map { routerDevice -> SearchRouterDeviceUiState.Connected(routerDevice = routerDevice) }
                .dataOrElse { error -> state }
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
