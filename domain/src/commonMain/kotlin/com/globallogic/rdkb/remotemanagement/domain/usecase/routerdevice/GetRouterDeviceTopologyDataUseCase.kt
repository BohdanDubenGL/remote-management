package com.globallogic.rdkb.remotemanagement.domain.usecase.routerdevice

import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDeviceTopologyData
import com.globallogic.rdkb.remotemanagement.domain.repository.RouterDeviceRepository

class GetRouterDeviceTopologyDataUseCase(
    private val routerDeviceRepository: RouterDeviceRepository
) {
    suspend operator fun invoke(device: RouterDevice): Result<RouterDeviceTopologyData?> = getRouterDeviceTopologyData(device)

    suspend fun getRouterDeviceTopologyData(device: RouterDevice): Result<RouterDeviceTopologyData?> = routerDeviceRepository.getRouterDeviceTopologyData(device)
}
