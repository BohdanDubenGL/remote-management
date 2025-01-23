package com.globallogic.rdkb.remotemanagement.domain.usecase.routerdevice

import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice
import com.globallogic.rdkb.remotemanagement.domain.repository.RouterDeviceRepository

class RestartRouterDeviceUseCase(
    private val routerDeviceRepository: RouterDeviceRepository
) {
    suspend operator fun invoke(device: RouterDevice): Unit = restartRouterDevice(device)

    suspend fun restartRouterDevice(device: RouterDevice): Unit = routerDeviceRepository.restartRouterDevice(device)
}
