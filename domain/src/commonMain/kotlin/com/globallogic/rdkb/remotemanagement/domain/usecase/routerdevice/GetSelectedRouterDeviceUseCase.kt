package com.globallogic.rdkb.remotemanagement.domain.usecase.routerdevice

import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice
import com.globallogic.rdkb.remotemanagement.domain.repository.RouterDeviceRepository

class GetSelectedRouterDeviceUseCase(
    private val routerDeviceRepository: RouterDeviceRepository
) {
    suspend operator fun invoke(): RouterDevice = getSelectedRouterDevice()

    suspend fun getSelectedRouterDevice(): RouterDevice = routerDeviceRepository.getSelectRouterDevice()
}
