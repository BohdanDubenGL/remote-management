package com.globallogic.rdkb.remotemanagement.data.network.service.impl

import com.globallogic.rdkb.remotemanagement.data.network.service.RdkCentralAccessorService
import com.globallogic.rdkb.remotemanagement.data.network.service.RdkCentralPropertyService
import com.globallogic.rdkb.remotemanagement.data.network.service.model.DeviceProperty
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource
import com.globallogic.rdkb.remotemanagement.domain.utils.ThrowableResourceError

class AccessPointClientAccessor(
    private val rdkCentralPropertyService: RdkCentralPropertyService,
    private val macAddress: String,
    private val accessPointId: Int,
    override val clientId: Int,
): RdkCentralAccessorService.AccessPointClientAccessor {
    override suspend fun getClientMacAddress(): Resource<String, ThrowableResourceError> {
        return rdkCentralPropertyService.getDeviceProperty(macAddress, DeviceProperty.WifiClientsMacAddress(accessPointId, clientId))
    }

    override suspend fun getClientActive(): Resource<Boolean, ThrowableResourceError> {
        return rdkCentralPropertyService.getDeviceProperty(macAddress, DeviceProperty.WifiClientsActive(accessPointId, clientId))
    }
}
