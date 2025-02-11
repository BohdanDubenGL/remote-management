package com.globallogic.rdkb.remotemanagement.data.datasource.impl

import com.globallogic.rdkb.remotemanagement.data.datasource.RouterDeviceConnectionDataSource
import com.globallogic.rdkb.remotemanagement.data.error.IoDeviceError
import com.globallogic.rdkb.remotemanagement.domain.entity.FoundRouterDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource
import com.globallogic.rdkb.remotemanagement.domain.utils.buildResource

class RouterDeviceConnectionDataSourceImpl(

) : RouterDeviceConnectionDataSource {
    override suspend fun findAvailableRouterDevices(): Resource<List<FoundRouterDevice>, IoDeviceError.NoAvailableRouterDevices> = buildResource {
        return success(emptyList())
    }

    override suspend fun connectToRouterDevice(macAddress: String): Resource<RouterDevice, IoDeviceError.CantConnectToRouterDevice> = buildResource {
        return failure(IoDeviceError.CantConnectToRouterDevice)
    }
}
