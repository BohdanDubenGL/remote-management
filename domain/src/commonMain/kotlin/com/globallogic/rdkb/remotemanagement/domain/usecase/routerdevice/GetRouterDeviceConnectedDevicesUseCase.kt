package com.globallogic.rdkb.remotemanagement.domain.usecase.routerdevice

import com.globallogic.rdkb.remotemanagement.domain.entity.ConnectedDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice
import com.globallogic.rdkb.remotemanagement.domain.error.DeviceError
import com.globallogic.rdkb.remotemanagement.domain.repository.RouterDeviceRepository
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource

class GetRouterDeviceConnectedDevicesUseCase(
    private val routerDeviceRepository: RouterDeviceRepository
) {
    suspend operator fun invoke(device: RouterDevice): Resource<List<ConnectedDevice>, DeviceError.NoConnectedDevicesFound> =
        getRouterDeviceConnectedDevices(device)

    suspend fun getRouterDeviceConnectedDevices(device: RouterDevice): Resource<List<ConnectedDevice>, DeviceError.NoConnectedDevicesFound> =
        routerDeviceRepository.getRouterDeviceConnectedDevices(device)
}
