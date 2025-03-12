package com.globallogic.rdkb.remotemanagement.data.network.service.impl

import com.globallogic.rdkb.remotemanagement.data.network.service.RdkCentralAccessorService
import com.globallogic.rdkb.remotemanagement.data.network.service.RdkCentralPropertyService
import com.globallogic.rdkb.remotemanagement.domain.entity.Band
import com.globallogic.rdkb.remotemanagement.data.network.service.model.DeviceProperty
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource.Success
import com.globallogic.rdkb.remotemanagement.domain.utils.ThrowableResourceError
import com.globallogic.rdkb.remotemanagement.domain.utils.map
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class DeviceAccessor(
    private val rdkCentralPropertyService: RdkCentralPropertyService,
    private val macAddress: String,
) : RdkCentralAccessorService.DeviceAccessor {

    override suspend fun rebootDevice(): Resource<Unit, ThrowableResourceError> {
        return rdkCentralPropertyService.doDeviceAction(macAddress, DeviceProperty.ActionReboot)
    }

    override suspend fun factoryResetDevice(): Resource<Unit, ThrowableResourceError> {
        return rdkCentralPropertyService.doDeviceAction(macAddress, DeviceProperty.ActionFactoryReset)
    }

    override suspend fun getModelName(): Resource<String, ThrowableResourceError> {
        return rdkCentralPropertyService.getDeviceProperty(macAddress, DeviceProperty.ModelName)
    }

    override suspend fun getManufacturer(): Resource<String, ThrowableResourceError> {
        return rdkCentralPropertyService.getDeviceProperty(macAddress, DeviceProperty.Manufacturer)
    }

    override suspend fun getSoftwareVersion(): Resource<String, ThrowableResourceError> {
        return rdkCentralPropertyService.getDeviceProperty(macAddress, DeviceProperty.SoftwareVersion)
    }

    override suspend fun getIpAddressV4(): Resource<String, ThrowableResourceError> {
        return rdkCentralPropertyService.getDeviceProperty(macAddress, DeviceProperty.IpAddressV4)
    }

    override suspend fun getIpAddressV6(): Resource<String, ThrowableResourceError> {
        return rdkCentralPropertyService.getDeviceProperty(macAddress, DeviceProperty.IpAddressV6)
    }

    override suspend fun getMacAddress(): Resource<String, ThrowableResourceError> {
        return rdkCentralPropertyService.getDeviceProperty(macAddress, DeviceProperty.MacAddress)
    }

    override suspend fun getSerialNumber(): Resource<String, ThrowableResourceError> {
        return rdkCentralPropertyService.getDeviceProperty(macAddress, DeviceProperty.SerialNumber)
    }

    override suspend fun getOperatingFrequencyBands(): Resource<Set<String>, ThrowableResourceError> = coroutineScope {
        val bands = Band.entries
            .map { band -> async {
                rdkCentralPropertyService.getDeviceProperty(macAddress, DeviceProperty.OperatingFrequencyBand(band.radio))
            } }
            .awaitAll()
            .mapNotNull { band -> band.data }
            .toSet()
        Success(bands)
    }


    override suspend fun getTotalMemory(): Resource<Long, ThrowableResourceError> {
        return rdkCentralPropertyService.getDeviceProperty(macAddress, DeviceProperty.TotalMemory)
    }

    override suspend fun getFreeMemory(): Resource<Long, ThrowableResourceError> {
        return rdkCentralPropertyService.getDeviceProperty(macAddress, DeviceProperty.FreeMemory)
    }


    override suspend fun getConnectedDevicesCount(): Resource<Int, ThrowableResourceError> {
        return rdkCentralPropertyService.getDeviceProperty(macAddress, DeviceProperty.ConnectedDeviceCount)
            .map { it.toInt() }
    }

    override suspend fun connectedDevices(): Resource<List<RdkCentralAccessorService.ConnectedDeviceAccessor>, ThrowableResourceError> {
        return getConnectedDevicesCount().map { count ->
            List(count) { index -> connectedDevice(index + 1) }
        }
    }

    override fun accessPointGroups(): List<RdkCentralAccessorService.AccessPointGroupAccessor> =
        accessPointGroupIndices.map(::accessPointGroup)

    override fun accessPoints(): List<RdkCentralAccessorService.AccessPointAccessor> =
        accessPointGroups().flatMap(RdkCentralAccessorService.AccessPointGroupAccessor::accessPoints)

    override fun connectedDevice(deviceId: Int): RdkCentralAccessorService.ConnectedDeviceAccessor =
        ConnectedDeviceAccessor(rdkCentralPropertyService, macAddress, deviceId)

    override fun accessPointGroup(accessPointGroupId: Int): RdkCentralAccessorService.AccessPointGroupAccessor =
        AccessPointGroupAccessor(rdkCentralPropertyService, macAddress, accessPointGroupId)

    override fun accessPoint(accessPointGroupId: Int, band: Band): RdkCentralAccessorService.AccessPointAccessor =
        AccessPointAccessor(rdkCentralPropertyService, macAddress, accessPointGroupId + band.radio, band)

    override fun wifiMotion(): RdkCentralAccessorService.WifiMotionAccessor =
        WifiMotionAccessor(rdkCentralPropertyService, macAddress)

    companion object {
        private val accessPointGroupIndices: IntRange = 1..8
    }
}
