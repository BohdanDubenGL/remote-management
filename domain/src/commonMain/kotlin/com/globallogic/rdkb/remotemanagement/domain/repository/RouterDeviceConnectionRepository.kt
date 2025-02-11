package com.globallogic.rdkb.remotemanagement.domain.repository

import com.globallogic.rdkb.remotemanagement.domain.entity.FoundRouterDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice
import com.globallogic.rdkb.remotemanagement.domain.error.DeviceError
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource

interface RouterDeviceConnectionRepository {
    suspend fun connectToRouterDevice(device: FoundRouterDevice): Resource<RouterDevice, DeviceError.CantConnectToRouterDevice>
    suspend fun addRouterDeviceManually(macAddress: String): Resource<RouterDevice, DeviceError.CantConnectToRouterDevice>

    suspend fun searchRouterDevices(): Resource<List<FoundRouterDevice>, DeviceError.NoAvailableRouterDevices>
}
