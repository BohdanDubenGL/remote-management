package com.globallogic.rdkb.remotemanagement.domain.usecase.routerdeviceconnection

import com.globallogic.rdkb.remotemanagement.domain.entity.FoundRouterDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice
import com.globallogic.rdkb.remotemanagement.domain.error.DeviceError
import com.globallogic.rdkb.remotemanagement.domain.repository.RouterDeviceConnectionRepository
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource

class ConnectToRouterDeviceUseCase(
    private val routerDeviceConnectionRepository: RouterDeviceConnectionRepository
) {
    suspend operator fun invoke(device: FoundRouterDevice): Resource<RouterDevice, DeviceError.CantConnectToRouterDevice> =
        connectToRouterDevice(device)

    suspend fun connectToRouterDevice(device: FoundRouterDevice): Resource<RouterDevice, DeviceError.CantConnectToRouterDevice> =
        routerDeviceConnectionRepository.connectToRouterDevice(device)
}
