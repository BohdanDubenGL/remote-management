package com.globallogic.rdkb.remotemanagement.domain.usecase.routerdeviceconnection

import com.globallogic.rdkb.remotemanagement.domain.entity.FoundRouterDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice
import com.globallogic.rdkb.remotemanagement.domain.repository.RouterDeviceConnectionRepository

class ConnectToRouterDeviceUseCase(
    private val routerDeviceConnectionRepository: RouterDeviceConnectionRepository
) {
    suspend operator fun invoke(device: FoundRouterDevice): RouterDevice = connectToRouterDevice(device)

    suspend fun connectToRouterDevice(device: FoundRouterDevice): RouterDevice = routerDeviceConnectionRepository.connectToRouterDevice(device)
}
