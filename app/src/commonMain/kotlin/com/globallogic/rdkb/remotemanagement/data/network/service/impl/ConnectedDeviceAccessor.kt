package com.globallogic.rdkb.remotemanagement.data.network.service.impl

import com.globallogic.rdkb.remotemanagement.data.network.service.RdkCentralAccessorService
import com.globallogic.rdkb.remotemanagement.data.network.service.RdkCentralPropertyService
import com.globallogic.rdkb.remotemanagement.data.network.service.model.DeviceProperty
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource
import com.globallogic.rdkb.remotemanagement.domain.utils.ThrowableResourceError

class ConnectedDeviceAccessor(
    private val rdkCentralPropertyService: RdkCentralPropertyService,
    private val macAddress: String,
    private val deviceId: Int,
) : RdkCentralAccessorService.ConnectedDeviceAccessor {
    override suspend fun getConnectedDeviceActive(): Resource<Boolean, ThrowableResourceError> {
        return rdkCentralPropertyService.getDeviceProperty(macAddress, DeviceProperty.ConnectedDeviceActive(deviceId))
    }

    override suspend fun getConnectedDeviceHostName(): Resource<String, ThrowableResourceError> {
        return rdkCentralPropertyService.getDeviceProperty(macAddress, DeviceProperty.ConnectedDeviceHostName(deviceId))
    }

    override suspend fun getConnectedDeviceMacAddress(): Resource<String, ThrowableResourceError> {
        return rdkCentralPropertyService.getDeviceProperty(macAddress, DeviceProperty.ConnectedDeviceMacAddress(deviceId))
    }

    override suspend fun getConnectedDeviceIpAddress(): Resource<String, ThrowableResourceError> {
        return rdkCentralPropertyService.getDeviceProperty(macAddress, DeviceProperty.ConnectedDeviceIpAddress(deviceId))
    }

    override suspend fun getConnectedDeviceVendorClassId(): Resource<String, ThrowableResourceError> {
        return rdkCentralPropertyService.getDeviceProperty(macAddress, DeviceProperty.ConnectedDeviceVendorClassId(deviceId))
    }
}
