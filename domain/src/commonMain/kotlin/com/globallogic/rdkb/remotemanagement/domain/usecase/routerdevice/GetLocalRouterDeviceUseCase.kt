package com.globallogic.rdkb.remotemanagement.domain.usecase.routerdevice

import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice
import com.globallogic.rdkb.remotemanagement.domain.repository.RouterDeviceRepository

class GetLocalRouterDeviceUseCase(
    private val routerDeviceRepository: RouterDeviceRepository
) {
    suspend operator fun invoke(): RouterDevice = getLocalRouterDevice()

    suspend fun getLocalRouterDevice(): RouterDevice = routerDeviceRepository.getLocalRouterDevice()
}
