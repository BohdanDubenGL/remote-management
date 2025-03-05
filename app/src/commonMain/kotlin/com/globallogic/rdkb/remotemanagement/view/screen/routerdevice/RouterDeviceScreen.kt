package com.globallogic.rdkb.remotemanagement.view.screen.routerdevice

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Lan
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Router
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.globallogic.rdkb.remotemanagement.domain.entity.AccessPointGroup
import com.globallogic.rdkb.remotemanagement.domain.entity.AccessPointSettings
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice
import com.globallogic.rdkb.remotemanagement.domain.usecase.routerdevice.DoRouterDeviceActionUseCase
import com.globallogic.rdkb.remotemanagement.domain.usecase.routerdevice.GetAccessPointGroupsUseCase
import com.globallogic.rdkb.remotemanagement.domain.usecase.routerdevice.GetAccessPointSettingsUseCase
import com.globallogic.rdkb.remotemanagement.domain.usecase.routerdevice.GetSelectedRouterDeviceUseCase
import com.globallogic.rdkb.remotemanagement.domain.usecase.routerdevice.RouterDeviceAction
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource.Success
import com.globallogic.rdkb.remotemanagement.domain.utils.ResourceState
import com.globallogic.rdkb.remotemanagement.domain.utils.dataOrElse
import com.globallogic.rdkb.remotemanagement.domain.utils.flatMapError
import com.globallogic.rdkb.remotemanagement.domain.utils.map
import com.globallogic.rdkb.remotemanagement.domain.utils.mapError
import com.globallogic.rdkb.remotemanagement.domain.utils.onFailure
import com.globallogic.rdkb.remotemanagement.view.base.MviViewModel
import com.globallogic.rdkb.remotemanagement.view.component.AppButton
import com.globallogic.rdkb.remotemanagement.view.component.AppCard
import com.globallogic.rdkb.remotemanagement.view.component.AppDrawResourceState
import com.globallogic.rdkb.remotemanagement.view.component.AppHorizontalPivots
import com.globallogic.rdkb.remotemanagement.view.component.AppLoadingSpinner
import com.globallogic.rdkb.remotemanagement.view.component.AppTextProperty
import com.globallogic.rdkb.remotemanagement.view.component.AppTitleTextWithIcon
import com.globallogic.rdkb.remotemanagement.view.error.UiResourceError
import com.globallogic.rdkb.remotemanagement.view.navigation.LocalNavController
import com.globallogic.rdkb.remotemanagement.view.navigation.Screen
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RouterDeviceScreen(
    navController: NavController = LocalNavController.current,
    routerDeviceViewModel: RouterDeviceViewModel = koinViewModel()
) {
    val uiState by routerDeviceViewModel.uiState.collectAsStateWithLifecycle()

    AppDrawResourceState(
        resourceState = uiState,
        onSuccess = { state ->
            when(state) {
                is RouterDeviceUiState.DeviceRemoved -> {
                    SideEffect {
                        navController.navigate(Screen.HomeGraph.Topology) {
                            popUpTo<Screen.RootGraph>()
                        }
                    }
                }
                is RouterDeviceUiState.DeviceLoaded -> {
                    RouterDeviceContent(
                        uiState = state,
                        onAccessPointGroupClicked = routerDeviceViewModel::onAccessPointGroupClicked,
                        onRestartDevice = routerDeviceViewModel::onRestartRouterDevice,
                        onFactoryResetDevice = routerDeviceViewModel::onFactoryResetRouterDevice,
                        onRemoveDevice = routerDeviceViewModel::onRemoveRouterDevice,
                        onOpenWebGui = routerDeviceViewModel::onOpenWebGui,
                        onWebGuiOpened = routerDeviceViewModel::onWebGuiOpened,
                    )
                }
            }
        }
    )
}

@Composable
private fun RouterDeviceContent(
    uiState: RouterDeviceUiState.DeviceLoaded,
    onAccessPointGroupClicked: (AccessPointGroup) -> Unit,
    onRestartDevice: () -> Unit,
    onFactoryResetDevice: () -> Unit,
    onRemoveDevice: () -> Unit,
    onOpenWebGui: () -> Unit,
    onWebGuiOpened: () -> Unit,
) {
    val uriHandler = LocalUriHandler.current
    SideEffect {
        if (uiState.openWebGui) {
            onWebGuiOpened()
            uriHandler.openUri(uiState.webGuiUrl)
        }
    }
    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top),
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        AppTitleTextWithIcon(
            text = uiState.routerDevice.modelName,
            imageVector = Icons.Default.Router,
            modifier = Modifier.padding(horizontal = 32.dp),
        )
        AppCard(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                AppTextProperty(name = "Manufacturer:", value = uiState.routerDevice.manufacturer, vertical = true)
                AppTextProperty(name = "Firmware version:", value = uiState.routerDevice.firmwareVersion, vertical = true)
            }
        }
        AppTitleTextWithIcon(
            text = "Network info",
            imageVector = Icons.Default.Lan,
            modifier = Modifier.padding(horizontal = 32.dp),
        )
        AppCard(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                AppTextProperty(name = "IP v4:", value = uiState.routerDevice.ipAddressV4)
                AppTextProperty(name = "IP v6:", value = uiState.routerDevice.ipAddressV6)
                AppTextProperty(name = "Mac address:", value = uiState.routerDevice.macAddress)
                AppTextProperty(name = "Available bands:", value = uiState.routerDevice.availableBands.joinToString(separator = ","))
            }
        }
        if (uiState.currentAccessPointGroup != null) {
            AppTitleTextWithIcon(
                text = "WiFi interfaces",
                imageVector = Icons.Default.Wifi,
                modifier = Modifier.padding(horizontal = 32.dp),
            )
            AppHorizontalPivots(
                pivots = uiState.accessPointGroups,
                selectedPivot = uiState.currentAccessPointGroup,
                pivotMapper = AccessPointGroup::name,
                onPivotClick = onAccessPointGroupClicked
            )
            AppDrawResourceState(
                resourceState = uiState.accessPointGroupSettings,
                onSuccess = { state ->
                    state.accessPoints.forEach { accessPoint ->
                        AppCard(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        ) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                AppTitleTextWithIcon(
                                    text = accessPoint.ssid,
                                    imageVector = Icons.Default.Wifi,
                                    fontSize = 20.sp,
                                    iconSize = 24.dp,
                                )
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.padding(start = 40.dp)
                                ) {
                                    AppTextProperty(name = "Enabled:", value = accessPoint.enabled)
                                    AppTextProperty(name = "Security mode:", value = accessPoint.securityMode)
                                    AppTextProperty(name = "Band:", value = accessPoint.band)
                                    AppTextProperty(name = "Clients:", value = accessPoint.clientsCount)
                                }
                            }
                        }
                    }
                },
                onLoading = {
                    AppLoadingSpinner(modifier = Modifier.height(450.dp).padding(128.dp))
                }
            )
        }
        AppTitleTextWithIcon(
            text = "Memory info",
            imageVector = Icons.Default.Memory,
            modifier = Modifier.padding(horizontal = 32.dp),
        )
        AppCard(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                AppTextProperty(name = "Total memory:", value = uiState.routerDevice.totalMemory)
                AppTextProperty(name = "Free memory:", value = "${uiState.routerDevice.freeMemory} (${uiState.routerDevice.freeMemoryPercent}%)")
            }
        }
        AppTitleTextWithIcon(
            text = "Actions",
            imageVector = Icons.Default.Category,
            modifier = Modifier.padding(horizontal = 32.dp),
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                AppButton(
                    text = "Open WEB GUI",
                    cornerRadius = 12.dp,
                    onClick = onOpenWebGui,
                    modifier = Modifier,
                )
                AppButton(
                    text = "Restart",
                    cornerRadius = 12.dp,
                    onClick = onRestartDevice,
                    modifier = Modifier,
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                AppButton(
                    text = "Factory reset",
                    cornerRadius = 12.dp,
                    onClick = onFactoryResetDevice,
                    modifier = Modifier,
                )
                AppButton(
                    text = "Remove device",
                    cornerRadius = 12.dp,
                    onClick = onRemoveDevice,
                    modifier = Modifier
                )
            }
        }
    }
}

class RouterDeviceViewModel(
    private val getSelectedRouterDevice: GetSelectedRouterDeviceUseCase,
    private val getAccessPointGroups: GetAccessPointGroupsUseCase,
    private val getAccessPointSettings: GetAccessPointSettingsUseCase,
    private val doRouterDeviceAction: DoRouterDeviceActionUseCase,
) : MviViewModel<ResourceState<RouterDeviceUiState, UiResourceError>>(ResourceState.None) {

    override suspend fun onInitState() = loadSelectedRouterDeviceInfo()

    private fun loadSelectedRouterDeviceInfo() = launchUpdateStateFromFlow { originalState ->
        send(ResourceState.Loading)
        val routerDevice = getSelectedRouterDevice()
            .dataOrElse { error -> return@launchUpdateStateFromFlow }
        val accessPointGroups = getAccessPointGroups(routerDevice)
            .dataOrElse { error -> return@launchUpdateStateFromFlow }
        val currentAccessPointGroup = accessPointGroups.firstOrNull()
        val accessPointGroupSettings = when (currentAccessPointGroup) {
            null -> ResourceState.None
            else -> getAccessPointSettings(routerDevice, currentAccessPointGroup)
                .mapError { error -> UiResourceError("", "") }
        }
        val state = Success(
            RouterDeviceUiState.DeviceLoaded(
                routerDevice = routerDevice,
                currentAccessPointGroup = accessPointGroups.firstOrNull(),
                accessPointGroups = accessPointGroups,
                accessPointGroupSettings = accessPointGroupSettings,
                webGuiUrl = "http://${routerDevice.ipAddressV4}/",
                openWebGui = false,
        ))
        send(state)
    }

    fun onAccessPointGroupClicked(accessPointGroup: AccessPointGroup) = launchOnViewModelScope {
        val originalState = uiState.value
        if (originalState !is Success) return@launchOnViewModelScope
        val data = originalState.data as? RouterDeviceUiState.DeviceLoaded ?: return@launchOnViewModelScope
        if (data.accessPointGroupSettings !is Success) return@launchOnViewModelScope

        updateState { state ->
            Success(data.copy(
                currentAccessPointGroup = accessPointGroup,
                accessPointGroupSettings = ResourceState.Loading,
            ))
        }
        updateState { state ->
            val routerDevice = getSelectedRouterDevice()
                .dataOrElse { error -> return@updateState originalState }
            val accessPointGroupSettings =
                getAccessPointSettings(routerDevice, accessPointGroup)
                    .dataOrElse { error -> return@updateState originalState }

            Success(data.copy(
                currentAccessPointGroup = accessPointGroup,
                accessPointGroupSettings = Success(accessPointGroupSettings),
            ))
        }
    }

    fun onRestartRouterDevice() = launchUpdateStateFromFlow { state ->
        if (state is Success) {
            val data = state.data as? RouterDeviceUiState.DeviceLoaded ?: return@launchUpdateStateFromFlow
            send(ResourceState.Loading)
            doRouterDeviceAction(data.routerDevice, RouterDeviceAction.Restart)
                .onFailure { loadSelectedRouterDeviceInfo() }
            send(state)
        }
    }

    fun onFactoryResetRouterDevice() = launchUpdateStateFromFlow { state ->
        if (state is Success) {
            val data = state.data as? RouterDeviceUiState.DeviceLoaded ?: return@launchUpdateStateFromFlow
            send(ResourceState.Loading)
            doRouterDeviceAction(data.routerDevice, RouterDeviceAction.FactoryReset)
                .onFailure { loadSelectedRouterDeviceInfo() }
            send(state)
        }
    }

    fun onRemoveRouterDevice() = launchUpdateStateFromFlow { state ->
        if (state is Success) {
            val data = state.data as? RouterDeviceUiState.DeviceLoaded ?: return@launchUpdateStateFromFlow
            send(ResourceState.Loading)
            val newState = doRouterDeviceAction(data.routerDevice, RouterDeviceAction.Remove)
                .onFailure { loadSelectedRouterDeviceInfo() }
                .map { RouterDeviceUiState.DeviceRemoved }
                .flatMapError { error -> state }
            send(newState)
        }
    }

    fun onOpenWebGui() = launchUpdateStateFromFlow { state ->
        if (state is Success) {
            val data = state.data as? RouterDeviceUiState.DeviceLoaded ?: return@launchUpdateStateFromFlow
            send(Success(data.copy(openWebGui = true)))
        }
    }

    fun onWebGuiOpened() = launchUpdateStateFromFlow { state ->
        if (state is Success) {
            val data = state.data as? RouterDeviceUiState.DeviceLoaded ?: return@launchUpdateStateFromFlow
            send(Success(data.copy(openWebGui = false)))
        }
    }
}

sealed interface RouterDeviceUiState {
    data class DeviceLoaded(
        val routerDevice: RouterDevice,
        val currentAccessPointGroup: AccessPointGroup?,
        val accessPointGroups: List<AccessPointGroup>,
        val accessPointGroupSettings: ResourceState<AccessPointSettings, UiResourceError>,
        val webGuiUrl: String,
        val openWebGui: Boolean,
    ): RouterDeviceUiState
    data object DeviceRemoved: RouterDeviceUiState
}
