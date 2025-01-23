package com.globallogic.rdkb.remotemanagement.domain.usecase.routerdeviceconnection

import com.globallogic.rdkb.remotemanagement.domain.entity.FoundRouterDevice
import com.globallogic.rdkb.remotemanagement.domain.repository.RouterDeviceConnectionRepository

class ConnectToRouterDeviceUseCase(
    private val routerDeviceConnectionRepository: RouterDeviceConnectionRepository
) {
    suspend operator fun invoke(device: FoundRouterDevice): Unit = connectToRouterDevice(device)

    suspend fun connectToRouterDevice(device: FoundRouterDevice): Unit = routerDeviceConnectionRepository.connectToRouterDevice(device)
}
