package com.globallogic.rdkb.remotemanagement.domain.usecase.wifimotion

import com.globallogic.rdkb.remotemanagement.domain.entity.WifiMotionData
import com.globallogic.rdkb.remotemanagement.domain.error.DeviceError
import com.globallogic.rdkb.remotemanagement.domain.repository.RouterDeviceRepository
import com.globallogic.rdkb.remotemanagement.domain.utils.ResourceState
import kotlinx.coroutines.flow.Flow

class GetWifiMotionDataUseCase(
    private val routerDeviceRepository: RouterDeviceRepository
) {
    suspend operator fun invoke(updateIntervalMillis: Long = 1_000): Flow<ResourceState<WifiMotionData, DeviceError.WifiMotion>> =
        getWifiMotionData(updateIntervalMillis)

    suspend fun getWifiMotionData(updateIntervalMillis: Long): Flow<ResourceState<WifiMotionData, DeviceError.WifiMotion>> {
        return routerDeviceRepository.loadWifiMotionData(updateIntervalMillis)
    }
}
