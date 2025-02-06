package com.globallogic.rdkb.remotemanagement.domain.usecase.routerdevice

import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice
import com.globallogic.rdkb.remotemanagement.domain.repository.RouterDeviceRepository

class RestartRouterDeviceUseCase(
    private val routerDeviceRepository: RouterDeviceRepository
) {
    suspend operator fun invoke(device: RouterDevice): Result<Unit> = restartRouterDevice(device)

    suspend fun restartRouterDevice(device: RouterDevice): Result<Unit> = routerDeviceRepository.restartRouterDevice(device)
}
