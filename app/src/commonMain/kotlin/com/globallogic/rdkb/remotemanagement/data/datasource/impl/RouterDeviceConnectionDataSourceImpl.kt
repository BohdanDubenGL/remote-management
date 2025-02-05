package com.globallogic.rdkb.remotemanagement.data.datasource.impl

import com.globallogic.rdkb.remotemanagement.data.datasource.RouterDeviceConnectionDataSource
import com.globallogic.rdkb.remotemanagement.domain.entity.FoundRouterDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice

class RouterDeviceConnectionDataSourceImpl(

) : RouterDeviceConnectionDataSource {
    override suspend fun findAvailableRouterDevices(): Result<List<FoundRouterDevice>> {
        return Result.success(emptyList())
    }

    override suspend fun connectToRouterDevice(macAddress: String): Result<RouterDevice?> {
        return Result.success(null)
    }
}
