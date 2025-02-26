package com.globallogic.rdkb.remotemanagement.data.network.service

import com.globallogic.rdkb.remotemanagement.domain.utils.Resource
import com.globallogic.rdkb.remotemanagement.domain.utils.ThrowableResourceError

interface RdkCentralAccessorService {
    suspend fun getAvailableDevices(): Resource<List<String>, ThrowableResourceError>

    suspend fun getModelName(macAddress: String): Resource<String, ThrowableResourceError>
    suspend fun getManufacturer(macAddress: String): Resource<String, ThrowableResourceError>
    suspend fun getSoftwareVersion(macAddress: String): Resource<String, ThrowableResourceError>
    suspend fun getIpAddressV4(macAddress: String): Resource<String, ThrowableResourceError>
    suspend fun getIpAddressV6(macAddress: String): Resource<String, ThrowableResourceError>
    suspend fun getMacAddress(macAddress: String): Resource<String, ThrowableResourceError>
    suspend fun getSerialNumber(macAddress: String): Resource<String, ThrowableResourceError>
    suspend fun getOperatingFrequencyBands(macAddress: String): Resource<Set<String>, ThrowableResourceError>

    suspend fun getTotalMemory(macAddress: String): Resource<Long, ThrowableResourceError>
    suspend fun getFreeMemory(macAddress: String): Resource<Long, ThrowableResourceError>

    suspend fun getConnectedDevicesCount(macAddress: String): Resource<Int, ThrowableResourceError>
    suspend fun getConnectedDeviceActive(macAddress: String, index: Int): Resource<Boolean, ThrowableResourceError>
    suspend fun getConnectedDeviceHostName(macAddress: String, index: Int): Resource<String, ThrowableResourceError>
    suspend fun getConnectedDeviceMacAddress(macAddress: String, index: Int): Resource<String, ThrowableResourceError>
    suspend fun getConnectedDeviceIpAddress(macAddress: String, index: Int): Resource<String, ThrowableResourceError>
    suspend fun getConnectedDeviceVendorClassId(macAddress: String, index: Int): Resource<String, ThrowableResourceError>

    suspend fun getWifiName(macAddress: String, accessPoint: Int): Resource<String, ThrowableResourceError>
    suspend fun getWifiEnabled(macAddress: String, accessPoint: Int): Resource<Boolean, ThrowableResourceError>
    suspend fun setWifiEnabled(macAddress: String, accessPoint: Int, enabled: Boolean): Resource<Unit, ThrowableResourceError>
    suspend fun getWifiSsid(macAddress: String, accessPoint: Int): Resource<String, ThrowableResourceError>
    suspend fun setWifiSsid(macAddress: String, accessPoint: Int, ssid: String): Resource<Unit, ThrowableResourceError>
    suspend fun setWifiPassword(macAddress: String, accessPoint: Int, password: String): Resource<Unit, ThrowableResourceError>
    suspend fun getWifiSecurityMode(macAddress: String, accessPoint: Int): Resource<String, ThrowableResourceError>
    suspend fun setWifiSecurityMode(macAddress: String, accessPoint: Int, securityMode: String): Resource<Unit, ThrowableResourceError>
    suspend fun getWifiAvailableSecurityModes(macAddress: String, accessPoint: Int): Resource<List<String>, ThrowableResourceError>

    suspend fun rebootDevice(macAddress: String): Resource<Unit, ThrowableResourceError>
    suspend fun factoryResetDevice(macAddress: String): Resource<Unit, ThrowableResourceError>
}
