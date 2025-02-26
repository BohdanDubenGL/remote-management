package com.globallogic.rdkb.remotemanagement.data.network.service.impl

import com.globallogic.rdkb.remotemanagement.data.network.service.RdkCentralPropertyService
import com.globallogic.rdkb.remotemanagement.data.network.service.RdkCentralAccessorService
import com.globallogic.rdkb.remotemanagement.data.network.service.model.DeviceProperty
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource.Success
import com.globallogic.rdkb.remotemanagement.domain.utils.ThrowableResourceError
import com.globallogic.rdkb.remotemanagement.domain.utils.map
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class RdkCentralAccessorServiceImpl(
    private val rdkCentralPropertyService: RdkCentralPropertyService
): RdkCentralAccessorService {
    override suspend fun getAvailableDevices(): Resource<List<String>, ThrowableResourceError> {
        return rdkCentralPropertyService.getAvailableDevices()
    }

    override suspend fun getModelName(macAddress: String): Resource<String, ThrowableResourceError> {
        return rdkCentralPropertyService.getDeviceProperty(macAddress, DeviceProperty.ModelName)
    }

    override suspend fun getManufacturer(macAddress: String): Resource<String, ThrowableResourceError> {
        return rdkCentralPropertyService.getDeviceProperty(macAddress, DeviceProperty.Manufacturer)
    }

    override suspend fun getSoftwareVersion(macAddress: String): Resource<String, ThrowableResourceError> {
        return rdkCentralPropertyService.getDeviceProperty(macAddress, DeviceProperty.SoftwareVersion)
    }

    override suspend fun getIpAddressV4(macAddress: String): Resource<String, ThrowableResourceError> {
        return rdkCentralPropertyService.getDeviceProperty(macAddress, DeviceProperty.IpAddressV4)
    }

    override suspend fun getIpAddressV6(macAddress: String): Resource<String, ThrowableResourceError> {
        return rdkCentralPropertyService.getDeviceProperty(macAddress, DeviceProperty.IpAddressV6)
    }

    override suspend fun getMacAddress(macAddress: String): Resource<String, ThrowableResourceError> {
        return rdkCentralPropertyService.getDeviceProperty(macAddress, DeviceProperty.MacAddress)
    }

    override suspend fun getSerialNumber(macAddress: String): Resource<String, ThrowableResourceError> {
        return rdkCentralPropertyService.getDeviceProperty(macAddress, DeviceProperty.SerialNumber)
    }

    override suspend fun getOperatingFrequencyBands(macAddress: String): Resource<Set<String>, ThrowableResourceError> = coroutineScope {
        val band1 = async { rdkCentralPropertyService.getDeviceProperty(macAddress, DeviceProperty.OperatingFrequencyBand(10_000)) }
        val band2 = async { rdkCentralPropertyService.getDeviceProperty(macAddress, DeviceProperty.OperatingFrequencyBand(10_100)) }
        val band3 = async { rdkCentralPropertyService.getDeviceProperty(macAddress, DeviceProperty.OperatingFrequencyBand(10_200)) }

        val bands = listOf(band1, band2, band3)
            .awaitAll()
            .mapNotNull { band -> band.data }
            .toSet()
        Success(bands)
    }


    override suspend fun getTotalMemory(macAddress: String): Resource<Long, ThrowableResourceError> {
        return rdkCentralPropertyService.getDeviceProperty(macAddress, DeviceProperty.TotalMemory)
    }

    override suspend fun getFreeMemory(macAddress: String): Resource<Long, ThrowableResourceError> {
        return rdkCentralPropertyService.getDeviceProperty(macAddress, DeviceProperty.FreeMemory)
    }


    override suspend fun getConnectedDevicesCount(macAddress: String): Resource<Int, ThrowableResourceError> {
        return rdkCentralPropertyService.getDeviceProperty(macAddress, DeviceProperty.ConnectedDeviceCount)
            .map { it.toInt() }
    }

    override suspend fun getConnectedDeviceActive(macAddress: String, index: Int): Resource<Boolean, ThrowableResourceError> {
        return rdkCentralPropertyService.getDeviceProperty(macAddress, DeviceProperty.ConnectedDeviceActive(index))
    }

    override suspend fun getConnectedDeviceHostName(macAddress: String, index: Int): Resource<String, ThrowableResourceError> {
        return rdkCentralPropertyService.getDeviceProperty(macAddress, DeviceProperty.ConnectedDeviceHostName(index))
    }

    override suspend fun getConnectedDeviceMacAddress(macAddress: String, index: Int): Resource<String, ThrowableResourceError> {
        return rdkCentralPropertyService.getDeviceProperty(macAddress, DeviceProperty.ConnectedDeviceMacAddress(index))
    }

    override suspend fun getConnectedDeviceIpAddress(macAddress: String, index: Int): Resource<String, ThrowableResourceError> {
        return rdkCentralPropertyService.getDeviceProperty(macAddress, DeviceProperty.ConnectedDeviceIpAddress(index))
    }

    override suspend fun getConnectedDeviceVendorClassId(macAddress: String, index: Int): Resource<String, ThrowableResourceError> {
        return rdkCentralPropertyService.getDeviceProperty(macAddress, DeviceProperty.ConnectedDeviceVendorClassId(index))
    }


    override suspend fun getWifiName(macAddress: String, accessPoint: Int): Resource<String, ThrowableResourceError> {
        return rdkCentralPropertyService.getDeviceProperty(macAddress, DeviceProperty.WifiName(accessPoint))
    }

    override suspend fun getWifiEnabled(macAddress: String, accessPoint: Int): Resource<Boolean, ThrowableResourceError> {
        return rdkCentralPropertyService.getDeviceProperty(macAddress, DeviceProperty.WifiEnabled(accessPoint))
    }

    override suspend fun setWifiEnabled(macAddress: String, accessPoint: Int, enabled: Boolean): Resource<Unit, ThrowableResourceError> {
        return rdkCentralPropertyService.setDeviceProperty(macAddress, DeviceProperty.WifiEnabled(accessPoint), enabled)
    }

    override suspend fun getWifiSsid(macAddress: String, accessPoint: Int): Resource<String, ThrowableResourceError> {
        return rdkCentralPropertyService.getDeviceProperty(macAddress, DeviceProperty.WifiSsid(accessPoint))
    }

    override suspend fun setWifiSsid(macAddress: String, accessPoint: Int, ssid: String): Resource<Unit, ThrowableResourceError> {
        return rdkCentralPropertyService.setDeviceProperty(macAddress, DeviceProperty.WifiSsid(accessPoint), ssid)
    }

    override suspend fun setWifiPassword(macAddress: String, accessPoint: Int, password: String): Resource<Unit, ThrowableResourceError> {
        return rdkCentralPropertyService.setDeviceProperty(macAddress, DeviceProperty.WifiPassword(accessPoint), password)
    }

    override suspend fun getWifiAvailableSecurityModes(macAddress: String, accessPoint: Int): Resource<List<String>, ThrowableResourceError> {
        return rdkCentralPropertyService.getDeviceProperty(macAddress, DeviceProperty.WifiAvailableSecurityModes(accessPoint))
            .map { it.split(",") }
    }

    override suspend fun getWifiSecurityMode(macAddress: String, accessPoint: Int): Resource<String, ThrowableResourceError> {
        return rdkCentralPropertyService.getDeviceProperty(macAddress, DeviceProperty.WifiSecurityMode(accessPoint))
    }

    override suspend fun setWifiSecurityMode(macAddress: String, accessPoint: Int, securityMode: String): Resource<Unit, ThrowableResourceError> {
        return rdkCentralPropertyService.setDeviceProperty(macAddress, DeviceProperty.WifiSecurityMode(accessPoint), securityMode)
    }


    override suspend fun rebootDevice(macAddress: String): Resource<Unit, ThrowableResourceError> {
        return rdkCentralPropertyService.doDeviceAction(macAddress, DeviceProperty.ActionReboot)
    }

    override suspend fun factoryResetDevice(macAddress: String): Resource<Unit, ThrowableResourceError> {
        return rdkCentralPropertyService.doDeviceAction(macAddress, DeviceProperty.ActionFactoryReset)
    }
}
