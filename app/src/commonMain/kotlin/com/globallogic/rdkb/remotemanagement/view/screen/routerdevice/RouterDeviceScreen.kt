package com.globallogic.rdkb.remotemanagement.view.screen.routerdevice

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDeviceInfo
import com.globallogic.rdkb.remotemanagement.domain.usecase.routerdevice.GetRouterDeviceInfoUseCase
import com.globallogic.rdkb.remotemanagement.domain.usecase.routerdevice.GetRouterDeviceUseCase
import com.globallogic.rdkb.remotemanagement.domain.usecase.routerdevice.GetSelectedRouterDeviceUseCase
import com.globallogic.rdkb.remotemanagement.view.LocalNavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.Duration.Companion.seconds

@Composable
fun RouterDeviceScreen(
    navController: NavController = LocalNavController.current,
    routerDeviceViewModel: RouterDeviceViewModel = koinViewModel()
) {
    val uiState by routerDeviceViewModel.uiState.collectAsStateWithLifecycle()
    RouterDeviceContent(
        uiState = uiState,
        loadSelectedRouterDeviceInfo = routerDeviceViewModel::loadSelectedRouterDeviceInfo,
    )
}

@Composable
private fun RouterDeviceContent(
    uiState: RouterDeviceUiState,
    loadSelectedRouterDeviceInfo: () -> Unit,
) {
    SideEffect {
        if (!uiState.routerDeviceInfoLoaded) loadSelectedRouterDeviceInfo()
    }
    if (uiState.routerDeviceInfoLoaded) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier.fillMaxSize().padding(32.dp, 32.dp)
        ) {
            Card(
                shape = RoundedCornerShape(4.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp, 16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text("name: " + uiState.routerDevice.name)
                    Text("ip: " + uiState.routerDevice.ip)
                    Text("macAddress: " + uiState.routerDevice.macAddress)
                    Text("lanConnected: " + uiState.routerDeviceInfo.lanConnected)
                    Text("connectedExtender: " + uiState.routerDeviceInfo.connectedExtender)
                    Text("modelName: " + uiState.routerDeviceInfo.modelName)
                    Text("firmwareVersion: " + uiState.routerDeviceInfo.firmwareVersion)
                    Text("processorLoadPercent: " + uiState.routerDeviceInfo.processorLoadPercent)
                    Text("memoryUsagePercent: " + uiState.routerDeviceInfo.memoryUsagePercent)
                    Text("totalDownloadTraffic: " + uiState.routerDeviceInfo.totalDownloadTraffic)
                    Text("totalUploadTraffic: " + uiState.routerDeviceInfo.totalUploadTraffic)
                    Text("availableBands: " + uiState.routerDeviceInfo.availableBands)
                }
            }
        }
    } else {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(text = "Loading...")
        }
    }
}

class RouterDeviceViewModel(
    private val getSelectedRouterDevice: GetSelectedRouterDeviceUseCase,
    private val getRouterDeviceInfo: GetRouterDeviceInfoUseCase
) : ViewModel() {
    private val _uiState: MutableStateFlow<RouterDeviceUiState> = MutableStateFlow(RouterDeviceUiState())
    val uiState: StateFlow<RouterDeviceUiState> get() = _uiState.asStateFlow()

    fun loadSelectedRouterDeviceInfo() {
        viewModelScope.launch {
            delay(1.seconds.inWholeMilliseconds)
            val routerDevice = getSelectedRouterDevice()
            val routerDeviceInfo = getRouterDeviceInfo(routerDevice)
            _uiState.update { it.copy(routerDevice = routerDevice, routerDeviceInfo = routerDeviceInfo, routerDeviceInfoLoaded = true) }
        }
    }
}

data class RouterDeviceUiState(
    val routerDeviceInfoLoaded: Boolean = false,
    val routerDevice: RouterDevice = RouterDevice.empty,
    val routerDeviceInfo: RouterDeviceInfo = RouterDeviceInfo.empty,
)
