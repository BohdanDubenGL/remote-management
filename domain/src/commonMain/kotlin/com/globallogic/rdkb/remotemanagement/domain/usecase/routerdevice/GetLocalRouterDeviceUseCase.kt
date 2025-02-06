package com.globallogic.rdkb.remotemanagement.domain.usecase.routerdevice

import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice
import com.globallogic.rdkb.remotemanagement.domain.repository.RouterDeviceRepository

class GetLocalRouterDeviceUseCase(
    private val routerDeviceRepository: RouterDeviceRepository
) {
    suspend operator fun invoke(): Result<RouterDevice?> = getLocalRouterDevice()

    suspend fun getLocalRouterDevice(): Result<RouterDevice?> = routerDeviceRepository.getLocalRouterDevice()
}
