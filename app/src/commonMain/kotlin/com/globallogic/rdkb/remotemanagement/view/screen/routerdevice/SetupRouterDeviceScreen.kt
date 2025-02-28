package com.globallogic.rdkb.remotemanagement.view.screen.routerdevice

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.globallogic.rdkb.remotemanagement.domain.entity.AccessPointGroup
import com.globallogic.rdkb.remotemanagement.domain.entity.BandSettings
import com.globallogic.rdkb.remotemanagement.domain.entity.DeviceAccessPointSettings
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice
import com.globallogic.rdkb.remotemanagement.domain.usecase.routerdevice.GetAccessPointGroupsUseCase
import com.globallogic.rdkb.remotemanagement.domain.usecase.routerdevice.GetAccessPointSettingsUseCase
import com.globallogic.rdkb.remotemanagement.domain.usecase.routerdevice.GetSelectedRouterDeviceUseCase
import com.globallogic.rdkb.remotemanagement.domain.usecase.routerdevice.SetupDeviceAccessPointUseCase
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource.Failure
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource.Success
import com.globallogic.rdkb.remotemanagement.domain.utils.ResourceState
import com.globallogic.rdkb.remotemanagement.domain.utils.dataOrElse
import com.globallogic.rdkb.remotemanagement.domain.utils.map
import com.globallogic.rdkb.remotemanagement.domain.utils.mapErrorToData
import com.globallogic.rdkb.remotemanagement.view.base.MviViewModel
import com.globallogic.rdkb.remotemanagement.view.component.AppButton
import com.globallogic.rdkb.remotemanagement.view.component.AppCard
import com.globallogic.rdkb.remotemanagement.view.component.AppCheckBox
import com.globallogic.rdkb.remotemanagement.view.component.AppComboBox
import com.globallogic.rdkb.remotemanagement.view.component.AppDrawResourceState
import com.globallogic.rdkb.remotemanagement.view.component.AppPasswordTextField
import com.globallogic.rdkb.remotemanagement.view.component.AppSwitch
import com.globallogic.rdkb.remotemanagement.view.component.AppTextField
import com.globallogic.rdkb.remotemanagement.view.component.AppTitleTextWithIcon
import com.globallogic.rdkb.remotemanagement.view.error.UiResourceError
import com.globallogic.rdkb.remotemanagement.view.navigation.LocalNavController
import com.globallogic.rdkb.remotemanagement.view.navigation.Screen
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SetupRouterDeviceScreen(
    navController: NavController = LocalNavController.current,
    setupRouterDeviceViewModel: SetupRouterDeviceViewModel = koinViewModel()
) {
    val uiState by setupRouterDeviceViewModel.uiState.collectAsStateWithLifecycle()

    AppDrawResourceState(
        resourceState = uiState,
        onSuccess = { state ->
            SetupRouterDeviceContent(
                uiState = state,
                onAccessPointGroupChanged = setupRouterDeviceViewModel::onAccessPointGroupChanged,
                onSsidEntered = setupRouterDeviceViewModel::onSsidEntered,
                onPasswordEntered = setupRouterDeviceViewModel::onPasswordEntered,
                onEnabledChanged = setupRouterDeviceViewModel::onEnabledChanged,
                onSecurityModeChanged = setupRouterDeviceViewModel::onSecurityModeChanged,
                onSameAsFirstChanged = setupRouterDeviceViewModel::onSameAsFirstChanged,
                onSaveClicked = setupRouterDeviceViewModel::saveData,
                onDataSaved = { navController.navigate(Screen.RouterDeviceGraph) {
                    popUpTo<Screen.RouterDeviceGraph>()
                } }
            )
        }
    )
}

@Composable
private fun SetupRouterDeviceContent(
    uiState: SetupRouterDeviceUiState,
    onAccessPointGroupChanged: (AccessPointGroup) -> Unit,
    onSsidEntered: (band: String, String) -> Unit,
    onPasswordEntered: (band: String, String) -> Unit,
    onEnabledChanged: (band: String, Boolean) -> Unit,
    onSecurityModeChanged: (band: String, String) -> Unit,
    onSameAsFirstChanged: (band: String, Boolean) -> Unit,
    onSaveClicked: () -> Unit,
    onDataSaved: () -> Unit,
) {
    SideEffect {
        if (uiState.dataSaved) onDataSaved()
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier.fillMaxSize()
    ) {
        val firstBand = uiState.bandsSettings.firstOrNull()
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            item {
                AppComboBox(
                    label = "Access point group",
                    options = uiState.availableAccessPointGroups,
                    selectedOption = uiState.accessPointGroup,
                    optionMapper = AccessPointGroup::name,
                    onSelected = onAccessPointGroupChanged,
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            itemsIndexed(uiState.bandsSettings) { index, bandSettings ->
                AppCard(
                    modifier = Modifier
                ) {
                    Column(
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                        ) {
                            AppTitleTextWithIcon(
                                text = bandSettings.frequency,
                                imageVector = Icons.Default.Wifi,
                            )
                            Spacer(modifier = Modifier.weight(1F))
                            AppSwitch(
                                checked = bandSettings.enabled,
                                onCheckedChange = { onEnabledChanged(bandSettings.frequency, it) },
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        AppTextField(
                            value = if (bandSettings.sameAsFirst) firstBand?.ssid.orEmpty() else bandSettings.ssid,
                            onValueChange = { onSsidEntered(bandSettings.frequency, it) },
                            label = "SSID",
                            placeholder = "Enter SSID",
                            enabled = !bandSettings.sameAsFirst,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        AppPasswordTextField(
                            value = if (bandSettings.sameAsFirst) firstBand?.password.orEmpty() else bandSettings.password,
                            onValueChange = { onPasswordEntered(bandSettings.frequency, it) },
                            label = "Password",
                            placeholder = "Enter new password",
                            enabled = !bandSettings.sameAsFirst,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        AppComboBox(
                            label = "Security mode",
                            options = bandSettings.availableSecurityModes,
                            selectedOption = if (bandSettings.sameAsFirst) firstBand?.securityMode.orEmpty() else bandSettings.securityMode,
                            onSelected = { onSecurityModeChanged(bandSettings.frequency, it) },
                            enabled = !bandSettings.sameAsFirst,
                        )

                        if (index != 0) {
                            AppCheckBox(
                                text = "Same as first",
                                checked = bandSettings.sameAsFirst,
                                onCheckedChange = { onSameAsFirstChanged(bandSettings.frequency, it) },
                            )
                        }
                    }
                }
            }
            item {
                AppButton(
                    text = "Save",
                    onClick = onSaveClicked,
                    modifier = Modifier.width(200.dp),
                )
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

class SetupRouterDeviceViewModel(
    private val getSelectedRouterDevice: GetSelectedRouterDeviceUseCase,
    private val setupDeviceAccessPoint: SetupDeviceAccessPointUseCase,
    private val getAccessPointGroups: GetAccessPointGroupsUseCase,
    private val getAccessPointSettings: GetAccessPointSettingsUseCase,
) : MviViewModel<ResourceState<SetupRouterDeviceUiState, UiResourceError>>(ResourceState.None) {

    override suspend fun onInitState() = loadRouterDevice()

    fun onAccessPointGroupChanged(accessPointGroup: AccessPointGroup) = launchOnViewModelScope {
        val originalState = uiState.value
        updateState { state -> ResourceState.Loading }
        updateState { state ->
            when (originalState) {
                is Success -> {
                    val device = getSelectedRouterDevice()
                        .dataOrElse { error -> return@updateState state }
                    val wifiSettings = getAccessPointSettings(device, accessPointGroup)
                        .dataOrElse { error -> return@updateState Failure(UiResourceError("Error", "No data")) }
                    Success(originalState.data.copy(
                        routerDevice = device,
                        accessPointGroup = accessPointGroup,
                        bandsSettings = wifiSettings.accessPoints
                            .map { wifi -> BandSettings(
                                frequency = wifi.band,
                                ssid = wifi.ssid,
                                password = "",
                                securityMode = wifi.securityMode,
                                availableSecurityModes = wifi.availableSecurityModes,
                                enabled = wifi.enabled,
                                sameAsFirst = false
                            ) }
                    ))
                }
                else -> state
            }
        }
    }

    fun onSsidEntered(band: String, ssid: String) = updateState { state ->
        when (state) {
            is Success -> {
                Success(state.data.copy(
                    bandsSettings = state.data.bandsSettings
                        .map { if (it.frequency == band) it.copy(ssid = ssid) else it }
                ))
            }
            else -> state
        }
    }

    fun onPasswordEntered(band: String, password: String) = updateState { state ->
        when (state) {
            is Success -> {
                Success(state.data.copy(
                    bandsSettings = state.data.bandsSettings
                        .map { if (it.frequency == band) it.copy(password = password) else it }
                ))
            }
            else -> state
        }
    }

    fun onEnabledChanged(band: String, enabled: Boolean) = updateState { state ->
        when (state) {
            is Success -> {
                Success(state.data.copy(
                    bandsSettings = state.data.bandsSettings
                        .map { if (it.frequency == band) it.copy(enabled = enabled) else it }
                ))
            }
            else -> state
        }
    }

    fun onSecurityModeChanged(band: String, securityMode: String) = updateState { state ->
        when (state) {
            is Success -> {
                Success(state.data.copy(
                    bandsSettings = state.data.bandsSettings
                        .map { if (it.frequency == band) it.copy(securityMode = securityMode) else it }
                ))
            }
            else -> state
        }
    }

    fun onSameAsFirstChanged(band: String, sameAsFirst: Boolean) = updateState { state ->
        when (state) {
            is Success -> {
                Success(state.data.copy(
                    bandsSettings = state.data.bandsSettings
                        .map { if (it.frequency == band) it.copy(sameAsFirst = sameAsFirst) else it }
                ))
            }
            else -> state
        }
    }

    private fun loadRouterDevice() = launchOnViewModelScope {
        updateState { state -> ResourceState.Loading }
        updateState { state ->
            val device = getSelectedRouterDevice()
                .dataOrElse { error -> return@updateState state }
            val accessPointGroups = getAccessPointGroups(device)
                .dataOrElse { error -> return@updateState Failure(UiResourceError("Error", "No data")) }
            val currentAccessPointGroup = accessPointGroups.firstOrNull()
                ?: return@updateState Failure(UiResourceError("Error", "No data"))
            val wifiSettings = getAccessPointSettings(device, currentAccessPointGroup)
                .dataOrElse { error -> return@updateState Failure(UiResourceError("Error", "No data")) }

            Success(SetupRouterDeviceUiState(
                routerDevice = device,
                availableAccessPointGroups = accessPointGroups,
                accessPointGroup = currentAccessPointGroup,
                bandsSettings = wifiSettings.accessPoints
                    .map { wifi ->
                        BandSettings(
                            frequency = wifi.band,
                            ssid = wifi.ssid,
                            password = "",
                            securityMode = wifi.securityMode,
                            availableSecurityModes = wifi.availableSecurityModes,
                            enabled = wifi.enabled,
                            sameAsFirst = false
                        ) },
            ))
        }
    }

    fun saveData() = launchUpdateState { state ->
        when(state) {
            is Success -> {
                setupDeviceAccessPoint(state.data.routerDevice, state.data.accessPointGroup, DeviceAccessPointSettings(state.data.bandsSettings))
                    .map { state.data.copy(dataSaved = true) }
                    .mapErrorToData { error -> state.data }
            }
            else -> state
        }
    }
}

data class SetupRouterDeviceUiState(
    val routerDevice: RouterDevice,
    val availableAccessPointGroups: List<AccessPointGroup> = emptyList(),
    val accessPointGroup: AccessPointGroup,
    val bandsSettings: List<BandSettings> = emptyList(),
    val dataSaved: Boolean = false,
)
