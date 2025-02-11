package com.globallogic.rdkb.remotemanagement.domain.usecase.routerdevice

import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice
import com.globallogic.rdkb.remotemanagement.domain.error.DeviceError
import com.globallogic.rdkb.remotemanagement.domain.repository.RouterDeviceRepository
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource

class GetLocalRouterDeviceUseCase(
    private val routerDeviceRepository: RouterDeviceRepository
) {
    suspend operator fun invoke(): Resource<RouterDevice, DeviceError.NoDeviceFound> =
        getLocalRouterDevice()

    suspend fun getLocalRouterDevice(): Resource<RouterDevice, DeviceError.NoDeviceFound> =
        routerDeviceRepository.getLocalRouterDevice()
}
