package com.globallogic.rdkb.remotemanagement.data.datasource

import com.globallogic.rdkb.remotemanagement.domain.entity.FoundRouterDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice

interface RouterDeviceConnectionDataSource {
    suspend fun findAvailableRouterDevices(): Result<List<FoundRouterDevice>>

    suspend fun connectToRouterDevice(macAddress: String): Result<RouterDevice?>
}
