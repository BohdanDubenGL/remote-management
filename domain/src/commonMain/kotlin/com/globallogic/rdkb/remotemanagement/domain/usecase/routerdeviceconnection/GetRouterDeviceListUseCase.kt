package com.globallogic.rdkb.remotemanagement.domain.usecase.routerdeviceconnection

import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice
import com.globallogic.rdkb.remotemanagement.domain.error.DeviceError
import com.globallogic.rdkb.remotemanagement.domain.repository.RouterDeviceRepository
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource

class GetRouterDeviceListUseCase(
    private val routerDeviceRepository: RouterDeviceRepository,
) {
    suspend operator fun invoke(): Resource<List<RouterDevice>, DeviceError.NoDevicesFound> = getDeviceList()

    suspend fun getDeviceList(): Resource<List<RouterDevice>, DeviceError.NoDevicesFound> =
        routerDeviceRepository.getDeviceList()
}
