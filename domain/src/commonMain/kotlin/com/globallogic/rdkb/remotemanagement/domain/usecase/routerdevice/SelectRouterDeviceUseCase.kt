package com.globallogic.rdkb.remotemanagement.domain.usecase.routerdevice

import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice
import com.globallogic.rdkb.remotemanagement.domain.repository.RouterDeviceRepository

class SelectRouterDeviceUseCase(
    private val routerDeviceRepository: RouterDeviceRepository
) {
    suspend operator fun invoke(device: RouterDevice): Result<Unit> = selectRouterDevice(device)

    suspend fun selectRouterDevice(device: RouterDevice): Result<Unit> = routerDeviceRepository.selectRouterDevice(device)
}
