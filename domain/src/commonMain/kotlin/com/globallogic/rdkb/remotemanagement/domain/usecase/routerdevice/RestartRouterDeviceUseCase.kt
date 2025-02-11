package com.globallogic.rdkb.remotemanagement.domain.usecase.routerdevice

import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice
import com.globallogic.rdkb.remotemanagement.domain.error.DeviceError
import com.globallogic.rdkb.remotemanagement.domain.repository.RouterDeviceRepository
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource

class RestartRouterDeviceUseCase(
    private val routerDeviceRepository: RouterDeviceRepository
) {
    suspend operator fun invoke(device: RouterDevice): Resource<Unit, DeviceError.RestartDevice> =
        restartRouterDevice(device)

    suspend fun restartRouterDevice(device: RouterDevice): Resource<Unit, DeviceError.RestartDevice> =
        routerDeviceRepository.restartRouterDevice(device)
}
