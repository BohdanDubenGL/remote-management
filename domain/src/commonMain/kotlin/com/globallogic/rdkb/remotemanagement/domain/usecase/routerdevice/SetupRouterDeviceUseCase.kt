package com.globallogic.rdkb.remotemanagement.domain.usecase.routerdevice

import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDeviceSettings
import com.globallogic.rdkb.remotemanagement.domain.error.DeviceError
import com.globallogic.rdkb.remotemanagement.domain.repository.RouterDeviceRepository
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource

class SetupRouterDeviceUseCase(
    private val routerDeviceRepository: RouterDeviceRepository
) {
    suspend operator fun invoke(device: RouterDevice, settings: RouterDeviceSettings): Resource<Unit, DeviceError.SetupDevice> =
        setupRouterDevice(device, settings)

    suspend fun setupRouterDevice(device: RouterDevice, settings: RouterDeviceSettings): Resource<Unit, DeviceError.SetupDevice> =
        routerDeviceRepository.setupRouterDevice(device, settings)
}
