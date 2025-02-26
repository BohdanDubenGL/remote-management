package com.globallogic.rdkb.remotemanagement.data.network.service.impl

import com.globallogic.rdkb.remotemanagement.data.network.service.RdkCentralAccessorService
import com.globallogic.rdkb.remotemanagement.data.network.service.RdkCentralPropertyService
import com.globallogic.rdkb.remotemanagement.data.network.service.model.DeviceProperty
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource.Failure
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource.Success
import com.globallogic.rdkb.remotemanagement.domain.utils.ThrowableResourceError
import com.globallogic.rdkb.remotemanagement.domain.utils.dataOrElse

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

    override suspend fun deviceStats(): Resource<RdkCentralAccessorService.ConnectedDeviceStatsAccessor, ThrowableResourceError> {
        val associatedDevice = rdkCentralPropertyService.getDeviceProperty(macAddress, DeviceProperty.ConnectedDeviceAssociatedDevice(deviceId))
            .dataOrElse { error -> return Failure(error) }

        val match = associatedDeviceRegex.find(associatedDevice)
            ?: return Failure(ThrowableResourceError(IllegalArgumentException("Unexpected value of 'AssociatedDevice' property: $associatedDevice")))
        val (accessPointIdString, clientIdString) = match.destructured
        val accessPointId = accessPointIdString.toIntOrNull()
            ?: return Failure(ThrowableResourceError(IllegalArgumentException("Can't parse accessPointId: $associatedDevice")))
        val clientId = clientIdString.toIntOrNull()
            ?: return Failure(ThrowableResourceError(IllegalArgumentException("Can't parse clientId: $associatedDevice")))

        val connectedDeviceStatsAccessor = ConnectedDeviceStatsAccessor(rdkCentralPropertyService, macAddress, accessPointId, clientId)
        return Success(connectedDeviceStatsAccessor)
    }

    companion object {
        private val associatedDeviceRegex = "Device.WiFi.AccessPoint\\.(\\d+)\\.AssociatedDevice\\.(\\d+)".toRegex()
    }
}
