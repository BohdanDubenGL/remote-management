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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.globallogic.rdkb.remotemanagement.domain.entity.BandSettings
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDeviceSettings
import com.globallogic.rdkb.remotemanagement.domain.usecase.routerdevice.GetRouterDeviceInfoUseCase
import com.globallogic.rdkb.remotemanagement.domain.usecase.routerdevice.GetSelectedRouterDeviceUseCase
import com.globallogic.rdkb.remotemanagement.domain.usecase.routerdevice.SetupRouterDeviceUseCase
import com.globallogic.rdkb.remotemanagement.domain.utils.dataOrElse
import com.globallogic.rdkb.remotemanagement.domain.utils.map
import com.globallogic.rdkb.remotemanagement.domain.utils.runCatchingSafe
import com.globallogic.rdkb.remotemanagement.view.component.AppButton
import com.globallogic.rdkb.remotemanagement.view.component.AppCard
import com.globallogic.rdkb.remotemanagement.view.component.AppPasswordTextField
import com.globallogic.rdkb.remotemanagement.view.component.AppTextField
import com.globallogic.rdkb.remotemanagement.view.component.AppTitleText
import com.globallogic.rdkb.remotemanagement.view.navigation.LocalNavController
import com.globallogic.rdkb.remotemanagement.view.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SetupRouterDeviceScreen(
    navController: NavController = LocalNavController.current,
    setupRouterDeviceViewModel: SetupRouterDeviceViewModel = koinViewModel()
) {
    val uiState by setupRouterDeviceViewModel.uiState.collectAsStateWithLifecycle()
    SetupRouterDeviceContent(
        uiState = uiState,
        loadRouterDevice = setupRouterDeviceViewModel::loadRouterDevice,
        onSsidEntered = setupRouterDeviceViewModel::onSsidEntered,
        onPasswordEntered = setupRouterDeviceViewModel::onPasswordEntered,
        onSameAsFirstChanged = setupRouterDeviceViewModel::onSameAsFirstChanged,
        onSaveClicked = setupRouterDeviceViewModel::saveData,
        onDataSaved = { navController.navigate(Screen.RouterDeviceGraph) {
            popUpTo<Screen.RouterDeviceGraph>()
        } }
    )
}

@Composable
private fun SetupRouterDeviceContent(
    uiState: SetupRouterDeviceUiState,
    loadRouterDevice: () -> Unit,
    onSsidEntered: (band: String, String) -> Unit,
    onPasswordEntered: (band: String, String) -> Unit,
    onSameAsFirstChanged: (band: String, Boolean) -> Unit,
    onSaveClicked: () -> Unit,
    onDataSaved: () -> Unit,
) {
    SideEffect {
        if (uiState.dataSaved) onDataSaved()
        else if (uiState.routerDevice == null) loadRouterDevice()
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier.fillMaxSize()
    ) {
        val firstBand = uiState.bandsSettings.firstOrNull()
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier.padding(horizontal = 40.dp)
        ) {
            itemsIndexed(uiState.bandsSettings) { index, bandSettings ->
                AppCard(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    ) {
                        AppTitleText(text = bandSettings.frequency, fontSize = 22.sp)
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
                        if (index != 0) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = bandSettings.sameAsFirst,
                                    onCheckedChange = { onSameAsFirstChanged(bandSettings.frequency, it) },
                                )
                                AppTitleText(text = "Same as first", fontSize = 16.sp)
                            }
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
    private val getRouterDeviceInfo: GetRouterDeviceInfoUseCase,
    private val setupRouterDevice: SetupRouterDeviceUseCase,
) : ViewModel() {
    private val _uiState: MutableStateFlow<SetupRouterDeviceUiState> = MutableStateFlow(SetupRouterDeviceUiState())
    val uiState: StateFlow<SetupRouterDeviceUiState> get() = _uiState.asStateFlow()

    fun onSsidEntered(band: String, ssid: String) {
        _uiState.update { it.copy(
            bandsSettings = it.bandsSettings.map { if (it.frequency == band) it.copy(ssid = ssid) else it }
        ) }
    }

    fun onPasswordEntered(band: String, password: String) {
        _uiState.update { it.copy(
            bandsSettings = it.bandsSettings.map { if (it.frequency == band) it.copy(password = password) else it }
        ) }
    }

    fun onSameAsFirstChanged(band: String, sameAsFirst: Boolean) {
        _uiState.update { it.copy(
            bandsSettings = it.bandsSettings.map { if (it.frequency == band) it.copy(sameAsFirst = sameAsFirst) else it }
        ) }
    }

    fun loadRouterDevice() {
        viewModelScope.launch {
            _uiState.update { state ->
                val routerDevice = getSelectedRouterDevice()
                    .dataOrElse { error -> return@update state }
                val routerDeviceInfo = getRouterDeviceInfo(routerDevice)
                    .dataOrElse { error -> return@update state }
                state.copy(
                    routerDevice = routerDevice,
                    bandsSettings = routerDeviceInfo.availableBands
                        .map { BandSettings(it, "", "", false) }
                )
            }
        }
    }

    fun saveData() {
        viewModelScope.launch {
            _uiState.update { state ->
                state.routerDevice ?: return@update state
                setupRouterDevice(state.routerDevice, RouterDeviceSettings(state.bandsSettings))
                    .map { state.copy(dataSaved = true) }
                    .dataOrElse { error -> state }
            }
        }
    }
}

data class SetupRouterDeviceUiState(
    val routerDevice: RouterDevice? = null,
    val bandsSettings: List<BandSettings> = emptyList(),
    val dataSaved: Boolean = false,
)
