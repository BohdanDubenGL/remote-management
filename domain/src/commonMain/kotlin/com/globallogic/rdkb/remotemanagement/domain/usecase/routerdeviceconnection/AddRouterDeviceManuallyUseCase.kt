package com.globallogic.rdkb.remotemanagement.domain.usecase.routerdeviceconnection

import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice
import com.globallogic.rdkb.remotemanagement.domain.error.DeviceError
import com.globallogic.rdkb.remotemanagement.domain.repository.RouterDeviceConnectionRepository
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource

class AddRouterDeviceManuallyUseCase(
    private val routerDeviceConnectionRepository: RouterDeviceConnectionRepository
) {
    suspend operator fun invoke(macAddress: String): Resource<RouterDevice, DeviceError.CantConnectToRouterDevice> =
        addRouterDeviceManually(macAddress)

    suspend fun addRouterDeviceManually(macAddress: String): Resource<RouterDevice, DeviceError.CantConnectToRouterDevice> =
        routerDeviceConnectionRepository.addRouterDeviceManually(macAddress)
}
