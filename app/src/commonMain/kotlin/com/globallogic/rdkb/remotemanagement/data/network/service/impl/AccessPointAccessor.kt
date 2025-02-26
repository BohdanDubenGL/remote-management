package com.globallogic.rdkb.remotemanagement.data.network.service.impl

import com.globallogic.rdkb.remotemanagement.data.network.service.RdkCentralAccessorService
import com.globallogic.rdkb.remotemanagement.data.network.service.RdkCentralPropertyService
import com.globallogic.rdkb.remotemanagement.data.network.service.model.Band
import com.globallogic.rdkb.remotemanagement.data.network.service.model.DeviceProperty
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource
import com.globallogic.rdkb.remotemanagement.domain.utils.ThrowableResourceError
import com.globallogic.rdkb.remotemanagement.domain.utils.map

class AccessPointAccessor(
    private val rdkCentralPropertyService: RdkCentralPropertyService,
    private val macAddress: String,
    private val accessPointId: Int,
    override val band: Band,
) : RdkCentralAccessorService.AccessPointAccessor {

    override suspend fun getWifiName(): Resource<String, ThrowableResourceError> {
        return rdkCentralPropertyService.getDeviceProperty(macAddress, DeviceProperty.WifiName(accessPointId))
    }

    override suspend fun getWifiEnabled(): Resource<Boolean, ThrowableResourceError> {
        return rdkCentralPropertyService.getDeviceProperty(macAddress, DeviceProperty.WifiEnabled(accessPointId))
    }

    override suspend fun getWifiSsid(): Resource<String, ThrowableResourceError> {
        return rdkCentralPropertyService.getDeviceProperty(macAddress, DeviceProperty.WifiSsid(accessPointId))
    }

    override suspend fun getWifiAvailableSecurityModes(): Resource<List<String>, ThrowableResourceError> {
        return rdkCentralPropertyService.getDeviceProperty(macAddress, DeviceProperty.WifiAvailableSecurityModes(accessPointId))
            .map { it.split(",") }
    }

    override suspend fun getWifiSecurityMode(): Resource<String, ThrowableResourceError> {
        return rdkCentralPropertyService.getDeviceProperty(macAddress, DeviceProperty.WifiSecurityMode(accessPointId))
    }


    override suspend fun setWifiEnabled(enabled: Boolean): Resource<Unit, ThrowableResourceError> {
        return rdkCentralPropertyService.setDeviceProperty(macAddress, DeviceProperty.WifiEnabled(accessPointId), enabled)
    }

    override suspend fun setWifiSsid(ssid: String): Resource<Unit, ThrowableResourceError> {
        return rdkCentralPropertyService.setDeviceProperty(macAddress, DeviceProperty.WifiSsid(accessPointId), ssid)
    }

    override suspend fun setWifiPassword(password: String): Resource<Unit, ThrowableResourceError> {
        return rdkCentralPropertyService.setDeviceProperty(macAddress, DeviceProperty.WifiPassword(accessPointId), password)
    }

    override suspend fun setWifiSecurityMode(securityMode: String): Resource<Unit, ThrowableResourceError> {
        return rdkCentralPropertyService.setDeviceProperty(macAddress, DeviceProperty.WifiSecurityMode(accessPointId), securityMode)
    }
}
