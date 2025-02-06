package com.globallogic.rdkb.remotemanagement.domain.usecase.routerdevice

import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice
import com.globallogic.rdkb.remotemanagement.domain.repository.RouterDeviceRepository

class FactoryResetRouterDeviceUseCase(
    private val routerDeviceRepository: RouterDeviceRepository
) {
    suspend operator fun invoke(device: RouterDevice): Result<Unit> = factoryResetRouterDevice(device)

    suspend fun factoryResetRouterDevice(device: RouterDevice): Result<Unit> = routerDeviceRepository.factoryResetRouterDevice(device)
}
