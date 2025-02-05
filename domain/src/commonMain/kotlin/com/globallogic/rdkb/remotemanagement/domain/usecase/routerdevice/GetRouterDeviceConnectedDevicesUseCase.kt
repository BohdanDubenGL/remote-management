package com.globallogic.rdkb.remotemanagement.domain.usecase.routerdevice

import com.globallogic.rdkb.remotemanagement.domain.entity.ConnectedDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice
import com.globallogic.rdkb.remotemanagement.domain.repository.RouterDeviceRepository

class GetRouterDeviceConnectedDevicesUseCase(
    private val routerDeviceRepository: RouterDeviceRepository
) {
    suspend operator fun invoke(device: RouterDevice): Result<List<ConnectedDevice>> = getRouterDeviceConnectedDevices(device)

    suspend fun getRouterDeviceConnectedDevices(device: RouterDevice): Result<List<ConnectedDevice>> = routerDeviceRepository.getRouterDeviceConnectedDevices(device)
}
