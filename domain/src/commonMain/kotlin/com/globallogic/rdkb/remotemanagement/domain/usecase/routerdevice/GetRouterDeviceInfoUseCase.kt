package com.globallogic.rdkb.remotemanagement.domain.usecase.routerdevice

import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDeviceInfo
import com.globallogic.rdkb.remotemanagement.domain.error.DeviceError
import com.globallogic.rdkb.remotemanagement.domain.repository.RouterDeviceRepository
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource

class GetRouterDeviceInfoUseCase(
    private val routerDeviceRepository: RouterDeviceRepository
) {
    suspend operator fun invoke(device: RouterDevice): Resource<RouterDeviceInfo, DeviceError.NoDeviceInfoFound> =
        getRouterDeviceInfo(device)

    suspend fun getRouterDeviceInfo(device: RouterDevice): Resource<RouterDeviceInfo, DeviceError.NoDeviceInfoFound> =
        routerDeviceRepository.getRouterDeviceInfo(device)
}
