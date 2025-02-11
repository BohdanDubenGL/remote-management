package com.globallogic.rdkb.remotemanagement.domain.usecase.routerdevice

import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice
import com.globallogic.rdkb.remotemanagement.domain.repository.RouterDeviceRepository
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource

class SelectRouterDeviceUseCase(
    private val routerDeviceRepository: RouterDeviceRepository
) {
    suspend operator fun invoke(device: RouterDevice): Resource.Success<Unit> =
        selectRouterDevice(device)

    suspend fun selectRouterDevice(device: RouterDevice): Resource.Success<Unit> =
        routerDeviceRepository.selectRouterDevice(device)
}
