package com.globallogic.rdkb.remotemanagement.data.network.service

import com.globallogic.rdkb.remotemanagement.data.network.service.model.Band
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource
import com.globallogic.rdkb.remotemanagement.domain.utils.ThrowableResourceError

interface RdkCentralAccessorService {
    suspend fun getAvailableDevices(): Resource<List<String>, ThrowableResourceError>

    fun device(macAddress: String): DeviceAccessor

    interface DeviceAccessor {
        suspend fun rebootDevice(): Resource<Unit, ThrowableResourceError>
        suspend fun factoryResetDevice(): Resource<Unit, ThrowableResourceError>

        suspend fun getModelName(): Resource<String, ThrowableResourceError>
        suspend fun getManufacturer(): Resource<String, ThrowableResourceError>
        suspend fun getSoftwareVersion(): Resource<String, ThrowableResourceError>
        suspend fun getIpAddressV4(): Resource<String, ThrowableResourceError>
        suspend fun getIpAddressV6(): Resource<String, ThrowableResourceError>
        suspend fun getMacAddress(): Resource<String, ThrowableResourceError>
        suspend fun getSerialNumber(): Resource<String, ThrowableResourceError>
        suspend fun getOperatingFrequencyBands(): Resource<Set<String>, ThrowableResourceError>

        suspend fun getTotalMemory(): Resource<Long, ThrowableResourceError>
        suspend fun getFreeMemory(): Resource<Long, ThrowableResourceError>

        suspend fun getConnectedDevicesCount(): Resource<Int, ThrowableResourceError>
        suspend fun connectedDevices(): Resource<List<ConnectedDeviceAccessor>, ThrowableResourceError>

        fun accessPoints(): List<AccessPointAccessor>
        fun accessPointGroups(): List<AccessPointGroupAccessor>

        fun connectedDevice(deviceId: Int): ConnectedDeviceAccessor
        fun accessPoint(accessPointGroupId: Int, band: Band): AccessPointAccessor
        fun accessPointGroup(accessPointGroupId: Int): AccessPointGroupAccessor
    }

    interface ConnectedDeviceAccessor {
        suspend fun getConnectedDeviceActive(): Resource<Boolean, ThrowableResourceError>
        suspend fun getConnectedDeviceHostName(): Resource<String, ThrowableResourceError>
        suspend fun getConnectedDeviceMacAddress(): Resource<String, ThrowableResourceError>
        suspend fun getConnectedDeviceIpAddress(): Resource<String, ThrowableResourceError>
        suspend fun getConnectedDeviceVendorClassId(): Resource<String, ThrowableResourceError>

        suspend fun deviceStats(): Resource<ConnectedDeviceStatsAccessor, ThrowableResourceError>
    }

    interface ConnectedDeviceStatsAccessor {
        suspend fun getBytesSent(): Resource<Long, ThrowableResourceError>
        suspend fun getBytesReceived(): Resource<Long, ThrowableResourceError>
        suspend fun getPacketsSent(): Resource<Long, ThrowableResourceError>
        suspend fun getPacketsReceived(): Resource<Long, ThrowableResourceError>
        suspend fun getErrorsSent(): Resource<Long, ThrowableResourceError>
    }

    interface AccessPointGroupAccessor {
        val accessPointGroupId: Int

        fun accessPoint(band: Band): AccessPointAccessor
        fun accessPoints(): List<AccessPointAccessor>
    }

    interface AccessPointAccessor {
        val band: Band

        suspend fun getWifiName(): Resource<String, ThrowableResourceError>
        suspend fun getWifiEnabled(): Resource<Boolean, ThrowableResourceError>
        suspend fun getWifiSsid(): Resource<String, ThrowableResourceError>
        suspend fun getWifiSecurityMode(): Resource<String, ThrowableResourceError>
        suspend fun getWifiAvailableSecurityModes(): Resource<List<String>, ThrowableResourceError>
        suspend fun getWifiClientsCount(): Resource<Int, ThrowableResourceError>

        suspend fun setWifiEnabled(enabled: Boolean): Resource<Unit, ThrowableResourceError>
        suspend fun setWifiSsid(ssid: String): Resource<Unit, ThrowableResourceError>
        suspend fun setWifiPassword(password: String): Resource<Unit, ThrowableResourceError>
        suspend fun setWifiSecurityMode(securityMode: String): Resource<Unit, ThrowableResourceError>
    }
}
