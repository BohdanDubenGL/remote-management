package com.globallogic.rdkb.remotemanagement.data.network.service.impl

import com.globallogic.rdkb.remotemanagement.data.network.service.RdkCentralAccessorService
import com.globallogic.rdkb.remotemanagement.data.network.service.RdkCentralPropertyService
import com.globallogic.rdkb.remotemanagement.data.network.service.model.DeviceProperty
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource
import com.globallogic.rdkb.remotemanagement.domain.utils.ThrowableResourceError

class ConnectedDeviceStatsAccessor(
    private val rdkCentralPropertyService: RdkCentralPropertyService,
    private val macAddress: String,
    private val accessPointId: Int,
    private val clientId: Int,
) : RdkCentralAccessorService.ConnectedDeviceStatsAccessor {
    override suspend fun getBytesSent(): Resource<Long, ThrowableResourceError> {
        return rdkCentralPropertyService.getDeviceProperty(macAddress, DeviceProperty.WifiClientBytesSent(accessPointId, clientId))
    }

    override suspend fun getBytesReceived(): Resource<Long, ThrowableResourceError> {
        return rdkCentralPropertyService.getDeviceProperty(macAddress, DeviceProperty.WifiClientBytesReceived(accessPointId, clientId))
    }

    override suspend fun getPacketsSent(): Resource<Long, ThrowableResourceError> {
        return rdkCentralPropertyService.getDeviceProperty(macAddress, DeviceProperty.WifiClientPacketsSent(accessPointId, clientId))
    }

    override suspend fun getPacketsReceived(): Resource<Long, ThrowableResourceError> {
        return rdkCentralPropertyService.getDeviceProperty(macAddress, DeviceProperty.WifiClientPacketsReceived(accessPointId, clientId))
    }

    override suspend fun getErrorsSent(): Resource<Long, ThrowableResourceError> {
        return rdkCentralPropertyService.getDeviceProperty(macAddress, DeviceProperty.WifiClientErrorsSent(accessPointId, clientId))
    }
}
