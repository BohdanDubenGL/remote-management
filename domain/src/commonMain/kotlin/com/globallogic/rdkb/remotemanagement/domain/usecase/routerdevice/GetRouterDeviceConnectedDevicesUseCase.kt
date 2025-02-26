package com.globallogic.rdkb.remotemanagement.domain.usecase.routerdevice

import com.globallogic.rdkb.remotemanagement.domain.entity.ConnectedDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice
import com.globallogic.rdkb.remotemanagement.domain.error.DeviceError
import com.globallogic.rdkb.remotemanagement.domain.repository.RouterDeviceRepository
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource
import com.globallogic.rdkb.remotemanagement.domain.utils.ResourceState
import kotlinx.coroutines.flow.Flow

class GetRouterDeviceConnectedDevicesUseCase(
    private val routerDeviceRepository: RouterDeviceRepository
) {
    suspend operator fun invoke(device: RouterDevice, forceUpdate: Boolean = true): Flow<ResourceState<List<ConnectedDevice>, DeviceError.NoConnectedDevicesFound>> =
        getRouterDeviceConnectedDevices(device, forceUpdate)

    suspend fun getRouterDeviceConnectedDevices(device: RouterDevice, forceUpdate: Boolean): Flow<ResourceState<List<ConnectedDevice>, DeviceError.NoConnectedDevicesFound>> =
        routerDeviceRepository.getRouterDeviceConnectedDevices(device, forceUpdate)
}
