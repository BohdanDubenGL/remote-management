package com.globallogic.rdkb.remotemanagement.data.network.service

import com.globallogic.rdkb.remotemanagement.domain.utils.Resource
import com.globallogic.rdkb.remotemanagement.domain.utils.ThrowableResourceError

interface RdkCentralService {
    suspend fun getAvailableDevices(): Resource<List<String>, ThrowableResourceError>

    suspend fun getModelName(macAddress: String): Resource<String, ThrowableResourceError>
    suspend fun getSoftwareVersion(macAddress: String): Resource<String, ThrowableResourceError>
    suspend fun getAdditionalSoftwareVersion(macAddress: String): Resource<String, ThrowableResourceError>
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

    suspend fun getBandSsid(macAddress: String, band: Int): Resource<String, ThrowableResourceError>
    suspend fun setBandSsid(macAddress: String, band: Int, ssid: String): Resource<Unit, ThrowableResourceError>
    suspend fun setBandPassword(macAddress: String, band: Int, password: String): Resource<Unit, ThrowableResourceError>

    suspend fun rebootDevice(macAddress: String): Resource<Unit, ThrowableResourceError>
    suspend fun factoryResetDevice(macAddress: String): Resource<Unit, ThrowableResourceError>
}

class CantLoadDevicesException() : Exception("Can't load devices from server")

class CantLoadDevicePropertyException(
    val macAddress: String,
    val property: RouterDeviceProperty.Property,
) : Exception("Can't load '${property.name}' for device with mac address '$macAddress'")

class CantChangeDevicePropertyException(
    val macAddress: String,
    val property: RouterDeviceProperty.Property,
    val value: String,
) : Exception("Can't change '${property.name}' for device with mac address '$macAddress' to value '$value'")
