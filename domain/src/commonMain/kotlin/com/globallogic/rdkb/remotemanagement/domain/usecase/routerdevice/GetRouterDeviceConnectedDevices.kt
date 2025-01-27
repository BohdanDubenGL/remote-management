package com.globallogic.rdkb.remotemanagement.domain.usecase.routerdevice

import com.globallogic.rdkb.remotemanagement.domain.entity.ConnectedDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice
import com.globallogic.rdkb.remotemanagement.domain.repository.RouterDeviceRepository

class GetRouterDeviceConnectedDevices(
    private val routerDeviceRepository: RouterDeviceRepository
) {
    suspend operator fun invoke(device: RouterDevice): List<ConnectedDevice> = getRouterDeviceConnectedDevices(device)

    suspend fun getRouterDeviceConnectedDevices(device: RouterDevice): List<ConnectedDevice> = routerDeviceRepository.getRouterDeviceConnectedDevices(device)
}
