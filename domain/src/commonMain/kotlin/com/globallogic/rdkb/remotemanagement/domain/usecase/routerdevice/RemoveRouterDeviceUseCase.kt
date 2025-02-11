package com.globallogic.rdkb.remotemanagement.domain.usecase.routerdevice

import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice
import com.globallogic.rdkb.remotemanagement.domain.error.DeviceError
import com.globallogic.rdkb.remotemanagement.domain.repository.RouterDeviceRepository
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource

class RemoveRouterDeviceUseCase(
    private val routerDeviceRepository: RouterDeviceRepository
) {
    suspend operator fun invoke(device: RouterDevice): Resource<Unit, DeviceError> =
        removeRouterDevice(device)

    suspend fun removeRouterDevice(device: RouterDevice): Resource<Unit, DeviceError> =
        routerDeviceRepository.removeRouterDevice(device)
}
