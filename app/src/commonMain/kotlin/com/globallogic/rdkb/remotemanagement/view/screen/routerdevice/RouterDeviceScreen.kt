package com.globallogic.rdkb.remotemanagement.view.screen.routerdevice

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Router
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
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
import com.globallogic.rdkb.remotemanagement.domain.usecase.routerdevice.GetSelectedRouterDeviceUseCase
import com.globallogic.rdkb.remotemanagement.domain.utils.dataOrElse
import com.globallogic.rdkb.remotemanagement.domain.utils.runCatchingSafe
import com.globallogic.rdkb.remotemanagement.view.component.AppCard
import com.globallogic.rdkb.remotemanagement.view.component.AppIcon
import com.globallogic.rdkb.remotemanagement.view.component.AppTextProperty
import com.globallogic.rdkb.remotemanagement.view.component.AppTitleText
import com.globallogic.rdkb.remotemanagement.view.navigation.LocalNavController
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
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            AppCard(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    if (uiState.routerDevice != null && uiState.routerDeviceInfo != null) {
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
                            AppTitleText(text = uiState.routerDevice.name, color = MaterialTheme.colorScheme.tertiary)
                        }

                        AppTextProperty(name = "Name:", value = uiState.routerDevice.name)
                        AppTextProperty(name = "IP:", value = uiState.routerDevice.ip)
                        AppTextProperty(name = "Mac address:", value = uiState.routerDevice.macAddress)
                        AppTextProperty(name = "Lan connected:", value = uiState.routerDeviceInfo.lanConnected.toString())
                        AppTextProperty(name = "Connected extender:", value = uiState.routerDeviceInfo.connectedExtender.toString())
                        AppTextProperty(name = "Model name:", value = uiState.routerDeviceInfo.modelName)
                        AppTextProperty(name = "Firmware version:", value = uiState.routerDeviceInfo.firmwareVersion)
                        AppTextProperty(name = "Processor load (%):", value = uiState.routerDeviceInfo.processorLoadPercent.toString())
                        AppTextProperty(name = "Memory usage (%):", value = uiState.routerDeviceInfo.memoryUsagePercent.toString())
                        AppTextProperty(name = "Total download (kB):", value = uiState.routerDeviceInfo.totalDownloadTraffic.toString())
                        AppTextProperty(name = "Total upload (kB):", value = uiState.routerDeviceInfo.totalUploadTraffic.toString())
                        AppTextProperty(name = "Available bands:", value = uiState.routerDeviceInfo.availableBands.joinToString(separator = ","))
                    }
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
            _uiState.update { state ->
                val routerDevice = getSelectedRouterDevice()
                    .dataOrElse { error -> return@update state }
                val routerDeviceInfo = getRouterDeviceInfo(routerDevice)
                    .dataOrElse { error -> return@update state }
                state.copy(routerDevice = routerDevice, routerDeviceInfo = routerDeviceInfo, routerDeviceInfoLoaded = true)
            }
        }
    }
}

data class RouterDeviceUiState(
    val routerDeviceInfoLoaded: Boolean = false,
    val routerDevice: RouterDevice? = null,
    val routerDeviceInfo: RouterDeviceInfo? = null,
)
