package com.globallogic.rdkb.remotemanagement.domain.repository

import com.globallogic.rdkb.remotemanagement.domain.entity.FoundRouterDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice

interface RouterDeviceConnectionRepository {
    suspend fun connectToRouterDevice(device: FoundRouterDevice): Result<RouterDevice>
    suspend fun addRouterDeviceManually(macAddress: String): Result<RouterDevice>

    suspend fun searchRouterDevices(): Result<List<FoundRouterDevice>>
}
