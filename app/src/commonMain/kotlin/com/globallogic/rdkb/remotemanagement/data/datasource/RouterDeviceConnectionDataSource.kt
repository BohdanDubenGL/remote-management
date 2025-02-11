package com.globallogic.rdkb.remotemanagement.data.datasource

import com.globallogic.rdkb.remotemanagement.data.error.IoDeviceError
import com.globallogic.rdkb.remotemanagement.domain.entity.FoundRouterDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource

interface RouterDeviceConnectionDataSource {
    suspend fun findAvailableRouterDevices(): Resource<List<FoundRouterDevice>, IoDeviceError.NoAvailableRouterDevices>

    suspend fun connectToRouterDevice(macAddress: String): Resource<RouterDevice, IoDeviceError.CantConnectToRouterDevice>
}
