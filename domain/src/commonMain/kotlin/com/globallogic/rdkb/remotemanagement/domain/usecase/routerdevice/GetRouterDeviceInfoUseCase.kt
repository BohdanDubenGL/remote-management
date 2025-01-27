package com.globallogic.rdkb.remotemanagement.domain.usecase.routerdevice

import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDeviceInfo
import com.globallogic.rdkb.remotemanagement.domain.repository.RouterDeviceRepository

class GetRouterDeviceInfoUseCase(
    private val routerDeviceRepository: RouterDeviceRepository
) {
    suspend operator fun invoke(device: RouterDevice): RouterDeviceInfo = getRouterDeviceInfo(device)

    suspend fun getRouterDeviceInfo(device: RouterDevice): RouterDeviceInfo = routerDeviceRepository.getRouterDeviceInfo(device)
}
