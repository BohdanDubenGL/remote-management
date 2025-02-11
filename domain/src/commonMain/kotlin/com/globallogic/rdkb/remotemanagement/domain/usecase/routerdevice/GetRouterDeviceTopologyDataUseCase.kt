package com.globallogic.rdkb.remotemanagement.domain.usecase.routerdevice

import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDeviceTopologyData
import com.globallogic.rdkb.remotemanagement.domain.error.DeviceError
import com.globallogic.rdkb.remotemanagement.domain.repository.RouterDeviceRepository
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource

class GetRouterDeviceTopologyDataUseCase(
    private val routerDeviceRepository: RouterDeviceRepository
) {
    suspend operator fun invoke(device: RouterDevice): Resource<RouterDeviceTopologyData, DeviceError.NoTopologyDataFound> =
        getRouterDeviceTopologyData(device)

    suspend fun getRouterDeviceTopologyData(device: RouterDevice): Resource<RouterDeviceTopologyData, DeviceError.NoTopologyDataFound> =
        routerDeviceRepository.getRouterDeviceTopologyData(device)
}
