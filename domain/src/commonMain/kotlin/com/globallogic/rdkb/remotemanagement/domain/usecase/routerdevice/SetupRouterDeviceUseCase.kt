package com.globallogic.rdkb.remotemanagement.domain.usecase.routerdevice

import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDeviceSettings
import com.globallogic.rdkb.remotemanagement.domain.repository.RouterDeviceRepository

class SetupRouterDeviceUseCase(
    private val routerDeviceRepository: RouterDeviceRepository
) {
    suspend operator fun invoke(device: RouterDevice, settings: RouterDeviceSettings): Unit = setupRouterDevice(device, settings)

    suspend fun setupRouterDevice(device: RouterDevice, settings: RouterDeviceSettings): Unit = routerDeviceRepository.setupRouterDevice(device, settings)
}
