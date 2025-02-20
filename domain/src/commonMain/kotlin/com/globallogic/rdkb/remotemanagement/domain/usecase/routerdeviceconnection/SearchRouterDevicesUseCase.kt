package com.globallogic.rdkb.remotemanagement.domain.usecase.routerdeviceconnection

import com.globallogic.rdkb.remotemanagement.domain.entity.FoundRouterDevice
import com.globallogic.rdkb.remotemanagement.domain.error.DeviceError
import com.globallogic.rdkb.remotemanagement.domain.repository.RouterDeviceConnectionRepository
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource

class SearchRouterDevicesUseCase(
    private val routerDeviceConnectionRepository: RouterDeviceConnectionRepository
) {
    suspend operator fun invoke(): Resource<List<FoundRouterDevice>, DeviceError.NoAvailableRouterDevices> = searchRouterDevices()

    suspend fun searchRouterDevices(): Resource<List<FoundRouterDevice>, DeviceError.NoAvailableRouterDevices> =
        routerDeviceConnectionRepository.searchRouterDevices()
}
