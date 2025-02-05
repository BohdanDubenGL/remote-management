package com.globallogic.rdkb.remotemanagement.view.screen.routerdevice

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.globallogic.rdkb.remotemanagement.domain.utils.runCatchingSafe
import com.globallogic.rdkb.remotemanagement.view.LocalNavController
import com.globallogic.rdkb.remotemanagement.view.Screen
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
            modifier = Modifier.padding(40.dp, 16.dp)
        ) {
            itemsIndexed(uiState.bandsSettings) { index, bandSettings ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    modifier = Modifier.padding(16.dp, 8.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(48.dp, 16.dp),
                    ) {
                        Text(text = bandSettings.frequency)
                        TextField(
                            value = if (bandSettings.sameAsFirst) firstBand?.ssid.orEmpty() else bandSettings.ssid,
                            onValueChange = { onSsidEntered(bandSettings.frequency, it) },
                            label = { Text(text = "SSID") },
                            placeholder = { Text(text = "Enter SSID") },
                            enabled = !bandSettings.sameAsFirst,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        TextField(
                            value = if (bandSettings.sameAsFirst) firstBand?.password.orEmpty() else bandSettings.password,
                            onValueChange = { onPasswordEntered(bandSettings.frequency, it) },
                            label = { Text(text = "Password") },
                            placeholder = { Text(text = "Enter new password") },
                            visualTransformation = PasswordVisualTransformation(),
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
                                Text(text = "Same as first")
                            }
                        }
                    }
                }
            }
            item {
                Button(
                    onClick = onSaveClicked,
                    content = { Text(text = "Save") },
                    modifier = Modifier.width(200.dp),
                )
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
            runCatchingSafe {
                val routerDevice = getSelectedRouterDevice().getOrThrow() ?: return@launch
                val routerDeviceInfo = getRouterDeviceInfo(routerDevice).getOrThrow()
                _uiState.update { it.copy(
                    routerDevice = routerDevice,
                    bandsSettings = routerDeviceInfo?.availableBands?.map { BandSettings(it, "", "", false) }.orEmpty()
                ) }
            }
                .onFailure { it.printStackTrace() }
        }
    }

    fun saveData() {
        viewModelScope.launch {
            val state = _uiState.value
            val routerDevice = state.routerDevice ?: return@launch
            setupRouterDevice(routerDevice, RouterDeviceSettings(state.bandsSettings))
                .onFailure { it.printStackTrace() }
            _uiState.update { it.copy(dataSaved = true) }
        }
    }
}

data class SetupRouterDeviceUiState(
    val routerDevice: RouterDevice? = null,
    val bandsSettings: List<BandSettings> = emptyList(),
    val dataSaved: Boolean = false,
)
