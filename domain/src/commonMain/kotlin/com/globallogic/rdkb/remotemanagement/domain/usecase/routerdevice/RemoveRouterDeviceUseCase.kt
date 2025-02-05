package com.globallogic.rdkb.remotemanagement.domain.usecase.routerdevice

import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice
import com.globallogic.rdkb.remotemanagement.domain.repository.RouterDeviceRepository

class RemoveRouterDeviceUseCase(
    private val routerDeviceRepository: RouterDeviceRepository
) {
    suspend operator fun invoke(device: RouterDevice): Result<Unit> = removeRouterDevice(device)

    suspend fun removeRouterDevice(device: RouterDevice): Result<Unit> = routerDeviceRepository.removeRouterDevice(device)
}
