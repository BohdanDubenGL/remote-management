package com.globallogic.rdkb.remotemanagement.domain.usecase.routerdeviceconnection

import com.globallogic.rdkb.remotemanagement.domain.entity.FoundRouterDevice
import com.globallogic.rdkb.remotemanagement.domain.repository.RouterDeviceConnectionRepository

class SearchRouterDevicesUseCase(
    private val routerDeviceConnectionRepository: RouterDeviceConnectionRepository
) {
    suspend operator fun invoke(): List<FoundRouterDevice> = searchRouterDevices()

    suspend fun searchRouterDevices(): List<FoundRouterDevice> = routerDeviceConnectionRepository.searchRouterDevices()
}
