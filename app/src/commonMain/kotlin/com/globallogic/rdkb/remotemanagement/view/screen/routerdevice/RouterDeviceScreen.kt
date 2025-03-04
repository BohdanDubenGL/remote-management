package com.globallogic.rdkb.remotemanagement.view.screen.routerdevice

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lan
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Router
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.globallogic.rdkb.remotemanagement.domain.entity.AccessPointGroup
import com.globallogic.rdkb.remotemanagement.domain.entity.AccessPointSettings
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice
import com.globallogic.rdkb.remotemanagement.domain.usecase.routerdevice.GetAccessPointGroupsUseCase
import com.globallogic.rdkb.remotemanagement.domain.usecase.routerdevice.GetAccessPointSettingsUseCase
import com.globallogic.rdkb.remotemanagement.domain.usecase.routerdevice.GetSelectedRouterDeviceUseCase
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource.Success
import com.globallogic.rdkb.remotemanagement.domain.utils.ResourceState
import com.globallogic.rdkb.remotemanagement.domain.utils.dataOrElse
import com.globallogic.rdkb.remotemanagement.domain.utils.mapError
import com.globallogic.rdkb.remotemanagement.view.base.MviViewModel
import com.globallogic.rdkb.remotemanagement.view.component.AppCard
import com.globallogic.rdkb.remotemanagement.view.component.AppDrawResourceState
import com.globallogic.rdkb.remotemanagement.view.component.AppHorizontalPivots
import com.globallogic.rdkb.remotemanagement.view.component.AppLoadingSpinner
import com.globallogic.rdkb.remotemanagement.view.component.AppTextProperty
import com.globallogic.rdkb.remotemanagement.view.component.AppTitleTextWithIcon
import com.globallogic.rdkb.remotemanagement.view.error.UiResourceError
import com.globallogic.rdkb.remotemanagement.view.navigation.LocalNavController
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
            RouterDeviceContent(
                uiState = state,
                onAccessPointGroupClicked = routerDeviceViewModel::onAccessPointGroupClicked,
            )
        }
    )
}

@Composable
private fun RouterDeviceContent(
    uiState: RouterDeviceUiState,
    onAccessPointGroupClicked: (AccessPointGroup) -> Unit,
) {
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
    }
}

class RouterDeviceViewModel(
    private val getSelectedRouterDevice: GetSelectedRouterDeviceUseCase,
    private val getAccessPointGroups: GetAccessPointGroupsUseCase,
    private val getAccessPointSettings: GetAccessPointSettingsUseCase,
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
            RouterDeviceUiState(
                routerDevice = routerDevice,
                currentAccessPointGroup = accessPointGroups.firstOrNull(),
                accessPointGroups = accessPointGroups,
                accessPointGroupSettings = accessPointGroupSettings,
        ))
        send(state)
    }

    fun onAccessPointGroupClicked(accessPointGroup: AccessPointGroup) = launchOnViewModelScope {
        val originalState = uiState.value
        if (originalState !is Success || originalState.data.accessPointGroupSettings !is Success) {
            return@launchOnViewModelScope
        }
        updateState { state ->
            Success(originalState.data.copy(
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

            Success(originalState.data.copy(
                currentAccessPointGroup = accessPointGroup,
                accessPointGroupSettings = Success(accessPointGroupSettings),
            ))
        }
    }
}

data class RouterDeviceUiState(
    val routerDevice: RouterDevice,
    val currentAccessPointGroup: AccessPointGroup?,
    val accessPointGroups: List<AccessPointGroup>,
    val accessPointGroupSettings: ResourceState<AccessPointSettings, UiResourceError>,
)
