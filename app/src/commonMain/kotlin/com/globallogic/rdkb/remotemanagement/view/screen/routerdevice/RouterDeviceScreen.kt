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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Router
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice
import com.globallogic.rdkb.remotemanagement.domain.usecase.routerdevice.GetSelectedRouterDeviceUseCase
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource
import com.globallogic.rdkb.remotemanagement.domain.utils.ResourceState
import com.globallogic.rdkb.remotemanagement.domain.utils.dataOrElse
import com.globallogic.rdkb.remotemanagement.view.base.MviViewModel
import com.globallogic.rdkb.remotemanagement.view.component.AppCard
import com.globallogic.rdkb.remotemanagement.view.component.AppDrawResourceState
import com.globallogic.rdkb.remotemanagement.view.component.AppIcon
import com.globallogic.rdkb.remotemanagement.view.component.AppTextProperty
import com.globallogic.rdkb.remotemanagement.view.component.AppTitleText
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
            )
        }
    )
}

@Composable
private fun RouterDeviceContent(
    uiState: RouterDeviceUiState,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier.fillMaxSize()
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    AppIcon(
                        imageVector = Icons.Default.Router,
                        contentColor = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(40.dp),
                        iconPadding = 8.dp,
                    )
                    Spacer(modifier = Modifier.weight(1F))
                    AppTitleText(text = uiState.routerDevice.modelName, color = MaterialTheme.colorScheme.tertiary)
                }

                AppTextProperty(name = "Name:", value = uiState.routerDevice.modelName)
                AppTextProperty(name = "Manufacturer:", value = uiState.routerDevice.manufacturer)
                AppTextProperty(name = "IP v4:", value = uiState.routerDevice.ipAddressV4)
                AppTextProperty(name = "IP v6:", value = uiState.routerDevice.ipAddressV6)
                AppTextProperty(name = "Mac address:", value = uiState.routerDevice.macAddress)
                AppTextProperty(name = "Model name:", value = uiState.routerDevice.modelName)
                AppTextProperty(name = "Firmware version:", value = uiState.routerDevice.firmwareVersion, vertical = true)
                AppTextProperty(name = "Total memory:", value = uiState.routerDevice.totalMemory)
                AppTextProperty(name = "Free memory:", value = "${uiState.routerDevice.freeMemory} (${uiState.routerDevice.freeMemoryPercent}%)")
                AppTextProperty(name = "Available bands:", value = uiState.routerDevice.availableBands.joinToString(separator = ","))
            }
        }
    }
}

class RouterDeviceViewModel(
    private val getSelectedRouterDevice: GetSelectedRouterDeviceUseCase,
) : MviViewModel<ResourceState<RouterDeviceUiState, UiResourceError>>(ResourceState.None) {

    override suspend fun onInitState() = loadSelectedRouterDeviceInfo()

    private fun loadSelectedRouterDeviceInfo() = launchUpdateState { state ->
        val routerDevice = getSelectedRouterDevice()
            .dataOrElse { error -> return@launchUpdateState state }
        Resource.Success(RouterDeviceUiState(routerDevice = routerDevice))
    }
}

data class RouterDeviceUiState(
    val routerDevice: RouterDevice,
)
