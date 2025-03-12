package com.globallogic.rdkb.remotemanagement.view.screen.routerdevice

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.outlined.Devices
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.globallogic.rdkb.remotemanagement.domain.entity.ConnectedDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.WifiMotionEvent
import com.globallogic.rdkb.remotemanagement.domain.usecase.wifimotion.GetWifiMotionDataUseCase
import com.globallogic.rdkb.remotemanagement.domain.usecase.wifimotion.StartWifiMotionUseCase
import com.globallogic.rdkb.remotemanagement.domain.usecase.wifimotion.StopWifiMotionUseCase
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource.Success
import com.globallogic.rdkb.remotemanagement.domain.utils.ResourceState
import com.globallogic.rdkb.remotemanagement.domain.utils.map
import com.globallogic.rdkb.remotemanagement.domain.utils.mapError
import com.globallogic.rdkb.remotemanagement.domain.utils.onSuccess
import com.globallogic.rdkb.remotemanagement.view.base.MviViewModel
import com.globallogic.rdkb.remotemanagement.view.component.AppCard
import com.globallogic.rdkb.remotemanagement.view.component.AppDrawResourceState
import com.globallogic.rdkb.remotemanagement.view.component.AppTitleTextWithIcon
import com.globallogic.rdkb.remotemanagement.view.component.WaveProgressBar
import com.globallogic.rdkb.remotemanagement.view.error.UiResourceError
import com.globallogic.rdkb.remotemanagement.view.navigation.LocalNavController
import com.globallogic.rdkb.remotemanagement.view.theme.RobotoMono
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun WifiMotionScreen(
    navController: NavController = LocalNavController.current,
    wifiMotionViewModel: WifiMotionViewModel = koinViewModel()
) {
    val uiState by wifiMotionViewModel.uiState.collectAsStateWithLifecycle()

    AppDrawResourceState(
        resourceState = uiState,
        onSuccess = { state ->
            WifiMotionContent(
                uiState = state,
                wifiMotionViewModel::onConnectedDeviceSelected,
            )
        }
    )
}

@Composable
fun WifiMotionContent(
    uiState: WifiMotionUiState,
    onConnectedDeviceSelected: (ConnectedDevice) -> Unit,
) {
    var eventsExpanded by remember { mutableStateOf(false) }
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize(),
    ) {
        AnimatedVisibility(!eventsExpanded) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Spacer(modifier = Modifier.height(64.dp))
                WaveProgressBar(
                    progress = uiState.motionPercent,
                    content = { percent ->
                        Text(
                            text = "$percent%",
                            fontSize = 32.sp,
                            fontFamily = FontFamily.RobotoMono,
                        )
                    },
                    modifier = Modifier.size(200.dp),
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    item {
                        Spacer(modifier = Modifier.width(16.dp))
                    }
                    items(uiState.connectedDevices) { connectedDevice ->
                        val selected = uiState.selectedConnectedDevice == connectedDevice
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .clickable(
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() },
                                    onClick = { onConnectedDeviceSelected(connectedDevice) },
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Devices,
                                tint = if (selected) Color.White else Color(0xff25e8d5),
                                contentDescription = "",
                                modifier = Modifier
                                    .size(64.dp, 64.dp)
                                    .background(if (selected) Color(0xff25e8d5) else Color.Black, CircleShape)
                                    .padding(4.dp)
                                    .background(if (selected) Color(0xff25e8d5) else MaterialTheme.colorScheme.secondaryContainer, CircleShape)
                                    .padding(all = 12.dp),
                            )
                            Text(text = connectedDevice.hostName, fontSize = 11.sp)
                        }
                    }
                    item {
                        Spacer(modifier = Modifier.width(16.dp))
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
        AppCard {
            Column(
                modifier = Modifier.fillMaxSize(),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    AppTitleTextWithIcon(text = "Event Log", imageVector = Icons.Default.HistoryEdu)
                    Spacer(modifier = Modifier.weight(1F))
                    Icon(
                        imageVector = if (eventsExpanded) Icons.Default.ArrowDropDown else Icons.Default.ArrowDropUp,
                        contentDescription = "",
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(48.dp)
                            .clickable { eventsExpanded = !eventsExpanded },
                    )
                }
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
                ) {
                    items(uiState.motionEvents) { motionEvent ->
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text(
                                text = motionEvent.formattedTime,
                                color = Color.LightGray,
                                fontSize = 12.sp,
                            )
                            Text(
                                text = "${motionEvent.deviceMacAddress} ${motionEvent.type}",
                                fontSize = 14.sp,
                            )
                        }
                    }
                }
            }
        }
    }
}

class WifiMotionViewModel(
    private val getWifiMotionData: GetWifiMotionDataUseCase,
    private val startWifiMotion: StartWifiMotionUseCase,
    private val stopWifiMotion: StopWifiMotionUseCase,
) : MviViewModel<ResourceState<WifiMotionUiState, UiResourceError>>(ResourceState.None) {

    override suspend fun onInitState() = loadRouterData()

    private fun loadRouterData() = launchUpdateStateFromFlow { state ->
        send(ResourceState.Loading)

        getWifiMotionData()
            .map { wifiMotionDataState ->
                when(wifiMotionDataState) {
                    is ResourceState.None -> wifiMotionDataState
                    is ResourceState.Loading -> wifiMotionDataState
                    is Resource -> wifiMotionDataState
                        .mapError { error -> UiResourceError("Error", "Can't load motion data") }
                        .map { wifiMotionData ->
                            when (val state = uiState.value) {
                                is Success -> state.data.copy(
                                    connectedDevices = wifiMotionData.hosts,
                                    motionState = wifiMotionData.isRunning,
                                    motionPercent = wifiMotionData.motionPercent,
                                    motionEvents = wifiMotionData.events,
                                )
                                else -> WifiMotionUiState(
                                    connectedDevices = wifiMotionData.hosts,
                                    selectedConnectedDevice = wifiMotionData.selectedHost,
                                    motionState = wifiMotionData.isRunning,
                                    motionPercent = wifiMotionData.motionPercent,
                                    motionEvents = wifiMotionData.events,
                                )
                            }
                        }
                }
            }
            .collectLatest(::send)
    }

    fun onConnectedDeviceSelected(connectedDevice: ConnectedDevice) = launchUpdateStateFromFlow { state ->
        if (state !is Success) return@launchUpdateStateFromFlow
        if (state.data.selectedConnectedDevice == connectedDevice) {
            stopWifiMotion()
                .onSuccess { send(Success(state.data.copy(selectedConnectedDevice = null))) }
        } else {
            startWifiMotion(connectedDevice)
                .onSuccess { send(Success(state.data.copy(selectedConnectedDevice = connectedDevice))) }
        }
    }
}

data class WifiMotionUiState(
    val connectedDevices: List<ConnectedDevice>,
    val selectedConnectedDevice: ConnectedDevice?,
    val motionState: Boolean,
    val motionPercent: Int,
    val motionEvents: List<WifiMotionEvent>,
)
