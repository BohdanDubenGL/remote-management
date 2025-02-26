package com.globallogic.rdkb.remotemanagement.view.screen.connection

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Router
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.globallogic.rdkb.remotemanagement.data.permission.Permission
import com.globallogic.rdkb.remotemanagement.data.permission.RequestResult
import com.globallogic.rdkb.remotemanagement.domain.entity.FoundRouterDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice
import com.globallogic.rdkb.remotemanagement.domain.usecase.routerdeviceconnection.ConnectToRouterDeviceUseCase
import com.globallogic.rdkb.remotemanagement.domain.usecase.routerdeviceconnection.SearchRouterDevicesUseCase
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource
import com.globallogic.rdkb.remotemanagement.domain.utils.ResourceState
import com.globallogic.rdkb.remotemanagement.domain.utils.map
import com.globallogic.rdkb.remotemanagement.domain.utils.mapError
import com.globallogic.rdkb.remotemanagement.domain.utils.mapErrorToData
import com.globallogic.rdkb.remotemanagement.view.base.MviViewModel
import com.globallogic.rdkb.remotemanagement.view.component.AppButton
import com.globallogic.rdkb.remotemanagement.view.component.AppCard
import com.globallogic.rdkb.remotemanagement.view.component.AppDrawResourceState
import com.globallogic.rdkb.remotemanagement.view.component.AppIcon
import com.globallogic.rdkb.remotemanagement.view.component.AppTextProperty
import com.globallogic.rdkb.remotemanagement.view.component.AppTitleText
import com.globallogic.rdkb.remotemanagement.view.component.AppTitleTextWithIcon
import com.globallogic.rdkb.remotemanagement.view.component.SetupFloatingActionButton
import com.globallogic.rdkb.remotemanagement.view.error.UiResourceError
import com.globallogic.rdkb.remotemanagement.view.navigation.FloatingActionButtonState
import com.globallogic.rdkb.remotemanagement.view.navigation.LocalNavController
import com.globallogic.rdkb.remotemanagement.view.navigation.Screen
import com.globallogic.rdkb.remotemanagement.view.permission.LocalPermissionController
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SearchRouterDeviceScreen(
    navController: NavController = LocalNavController.current,
    searchRouterDeviceViewModel: SearchRouterDeviceViewModel = koinViewModel(),
) {
    val uiState by searchRouterDeviceViewModel.uiState.collectAsStateWithLifecycle()

    AppDrawResourceState(
        resourceState = uiState,
        onNone = {
            val permissionController = LocalPermissionController.current
            LaunchedEffect(permissionController) {
                when(permissionController.requestPermission(Permission.Location)) {
                    RequestResult.Granted -> searchRouterDeviceViewModel.searchDevices()
                    else -> searchRouterDeviceViewModel.locationPermissionDenied()
                }
            }
        },
        onSuccess = { state ->
            when(state) {
                is SearchRouterDeviceUiState.Connected -> SideEffect {
                    navController.navigateUp()
                }
                is SearchRouterDeviceUiState.FoundDevices -> {
                    SearchRouterDeviceContent(
                        uiState = state,
                        connectToRouterDevice = searchRouterDeviceViewModel::connectToDevice,
                        addDeviceManually = { navController.navigate(Screen.ConnectionGraph.AddRouterDeviceManually) {
                            popUpTo<Screen.HomeGraph.Topology>()
                        } },
                    )
                }
            }
        }
    )
}

@Composable
private fun SearchRouterDeviceContent(
    uiState: SearchRouterDeviceUiState.FoundDevices,
    connectToRouterDevice: (FoundRouterDevice) -> Unit,
    addDeviceManually: () -> Unit,
) {
    SetupFloatingActionButton(
        floatingActionButtonState = FloatingActionButtonState.Shown(
            buttonIcon = Icons.Default.Keyboard,
            iconDescription = "Add manually",
            buttonAction = addDeviceManually
        )
    )
    if (uiState.foundRouterDevices.isEmpty()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize(),
        ) {
            AppTitleText(text = "No device found")
            Spacer(modifier = Modifier.height(16.dp))
            AppButton(
                text = "Add device manually",
                onClick = addDeviceManually,
                modifier = Modifier.width(250.dp)
            )
        }
    } else {
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
                        verticalArrangement = Arrangement.spacedBy(
                            4.dp,
                            Alignment.CenterVertically
                        ),
                        modifier = Modifier.fillMaxWidth().padding(16.dp, 16.dp)
                    ) {
                        AppTitleTextWithIcon(
                            text = foundDevice.name,
                            imageVector = Icons.Default.Router,
                        )
                        AppTextProperty(name = "ip:", value = foundDevice.ip)
                        AppTextProperty(name = "mac:", value = foundDevice.macAddress)
                    }
                }
            }
        }
    }
}

class SearchRouterDeviceViewModel(
    private val searchRouterDevices: SearchRouterDevicesUseCase,
    private val connectToRouterDevice: ConnectToRouterDeviceUseCase,
) : MviViewModel<ResourceState<SearchRouterDeviceUiState, UiResourceError>>(ResourceState.None) {

    fun locationPermissionDenied() = launchUpdateState { state ->
        Resource.Failure(UiResourceError(
            errorMessage = "Can't load devices",
            errorDescription = "App need location permission to check your local router.",
        ))
    }

    fun searchDevices() = launchOnViewModelScope {
        updateState { ResourceState.Loading }
        updateState { state ->
            searchRouterDevices()
                .map { foundDevices -> SearchRouterDeviceUiState.FoundDevices(foundRouterDevices = foundDevices) }
                .mapErrorToData { error -> SearchRouterDeviceUiState.FoundDevices(foundRouterDevices = emptyList()) }
        }
    }

    fun connectToDevice(foundRouterDevice: FoundRouterDevice) = launchOnViewModelScope {
        updateState { state -> ResourceState.Loading }
        updateState { state ->
            connectToRouterDevice(foundRouterDevice)
                .map { routerDevice -> SearchRouterDeviceUiState.Connected(routerDevice = routerDevice) }
                .mapError { error -> UiResourceError("Connection error", "Can't connect to device") }
        }
    }
}

sealed interface SearchRouterDeviceUiState {
    data class FoundDevices(val foundRouterDevices: List<FoundRouterDevice>) : SearchRouterDeviceUiState
    data class Connected(val routerDevice: RouterDevice) : SearchRouterDeviceUiState
}
