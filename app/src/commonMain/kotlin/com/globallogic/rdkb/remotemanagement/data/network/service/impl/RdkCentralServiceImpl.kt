package com.globallogic.rdkb.remotemanagement.data.network.service.impl

import com.globallogic.rdkb.remotemanagement.data.network.service.CantLoadDevicePropertyException
import com.globallogic.rdkb.remotemanagement.data.network.service.CantLoadDevicesException
import com.globallogic.rdkb.remotemanagement.data.network.service.RdkCentralApiService
import com.globallogic.rdkb.remotemanagement.data.network.service.RdkCentralService
import com.globallogic.rdkb.remotemanagement.data.network.service.RouterDeviceProperty
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource.Failure
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource.Success
import com.globallogic.rdkb.remotemanagement.domain.utils.ThrowableResourceError
import com.globallogic.rdkb.remotemanagement.domain.utils.flatMapData
import com.globallogic.rdkb.remotemanagement.domain.utils.map
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class RdkCentralServiceImpl(
    private val rdkCentralApiService: RdkCentralApiService
): RdkCentralService {
    override suspend fun getAvailableDevices(): Resource<List<String>, ThrowableResourceError> {
        return rdkCentralApiService.getAvailableDevices()
            .map { response -> response.devices?.mapNotNull { device -> device?.id?.split(":")?.getOrNull(1) } }
            .flatMapData { devices ->
                when (devices) {
                    null -> Failure(ThrowableResourceError(CantLoadDevicesException()))
                    else -> Success(devices)
                }
            }
    }

    override suspend fun getModelName(macAddress: String): Resource<String, ThrowableResourceError> {
        return rdkCentralApiService.getDevicePropertyAsValue(macAddress.formatMac(), RouterDeviceProperty.ModelName)
    }

    override suspend fun getManufacturer(macAddress: String): Resource<String, ThrowableResourceError> {
        return rdkCentralApiService.getDevicePropertyAsValue(macAddress.formatMac(), RouterDeviceProperty.Manufacturer)
    }

    override suspend fun getSoftwareVersion(macAddress: String): Resource<String, ThrowableResourceError> {
        return rdkCentralApiService.getDevicePropertyAsValue(macAddress.formatMac(), RouterDeviceProperty.SoftwareVersion)
    }

    override suspend fun getIpAddressV4(macAddress: String): Resource<String, ThrowableResourceError> {
        return rdkCentralApiService.getDevicePropertyAsValue(macAddress.formatMac(), RouterDeviceProperty.IpAddressV4)
    }

    override suspend fun getIpAddressV6(macAddress: String): Resource<String, ThrowableResourceError> {
        return rdkCentralApiService.getDevicePropertyAsValue(macAddress.formatMac(), RouterDeviceProperty.IpAddressV6)
    }

    override suspend fun getMacAddress(macAddress: String): Resource<String, ThrowableResourceError> {
        return rdkCentralApiService.getDevicePropertyAsValue(macAddress.formatMac(), RouterDeviceProperty.MacAddress)
    }

    override suspend fun getSerialNumber(macAddress: String): Resource<String, ThrowableResourceError> {
        return rdkCentralApiService.getDevicePropertyAsValue(macAddress.formatMac(), RouterDeviceProperty.SerialNumber)
    }

    override suspend fun getOperatingFrequencyBands(macAddress: String): Resource<Set<String>, ThrowableResourceError> = coroutineScope {
        val formattedMac = macAddress.formatMac()
        val band1 = async { rdkCentralApiService.getDevicePropertyAsValue(formattedMac, RouterDeviceProperty.OperatingFrequencyBand(10_000)) }
        val band2 = async { rdkCentralApiService.getDevicePropertyAsValue(formattedMac, RouterDeviceProperty.OperatingFrequencyBand(10_100)) }
        val band3 = async { rdkCentralApiService.getDevicePropertyAsValue(formattedMac, RouterDeviceProperty.OperatingFrequencyBand(10_200)) }

        val bands = listOf(band1, band2, band3)
            .awaitAll()
            .mapNotNull { band -> band.data }
            .toSet()
        Success(bands)
    }


    override suspend fun getTotalMemory(macAddress: String): Resource<Long, ThrowableResourceError> {
        return rdkCentralApiService.getDevicePropertyAsValue(macAddress.formatMac(), RouterDeviceProperty.TotalMemory, String::toLongOrNull)
    }

    override suspend fun getFreeMemory(macAddress: String): Resource<Long, ThrowableResourceError> {
        return rdkCentralApiService.getDevicePropertyAsValue(macAddress.formatMac(), RouterDeviceProperty.FreeMemory, String::toLongOrNull)
    }


    override suspend fun getConnectedDevicesCount(macAddress: String): Resource<Int, ThrowableResourceError> {
        return rdkCentralApiService.getDevicePropertyAsValue(macAddress.formatMac(), RouterDeviceProperty.ConnectedDeviceCount, String::toIntOrNull)
    }

    override suspend fun getConnectedDeviceActive(macAddress: String, index: Int): Resource<Boolean, ThrowableResourceError> {
        return rdkCentralApiService.getDevicePropertyAsValue(macAddress.formatMac(), RouterDeviceProperty.ConnectedDeviceActive(index), String::toBooleanStrictOrNull)
    }

    override suspend fun getConnectedDeviceHostName(macAddress: String, index: Int): Resource<String, ThrowableResourceError> {
        return rdkCentralApiService.getDevicePropertyAsValue(macAddress.formatMac(), RouterDeviceProperty.ConnectedDeviceHostName(index))
    }

    override suspend fun getConnectedDeviceMacAddress(macAddress: String, index: Int): Resource<String, ThrowableResourceError> {
        return rdkCentralApiService.getDevicePropertyAsValue(macAddress.formatMac(), RouterDeviceProperty.ConnectedDeviceMacAddress(index))
    }

    override suspend fun getConnectedDeviceIpAddress(macAddress: String, index: Int): Resource<String, ThrowableResourceError> {
        return rdkCentralApiService.getDevicePropertyAsValue(macAddress.formatMac(), RouterDeviceProperty.ConnectedDeviceIpAddress(index))
    }

    override suspend fun getConnectedDeviceVendorClassId(macAddress: String, index: Int): Resource<String, ThrowableResourceError> {
        return rdkCentralApiService.getDevicePropertyAsValue(macAddress.formatMac(), RouterDeviceProperty.ConnectedDeviceVendorClassId(index))
    }


    override suspend fun getBandSsid(macAddress: String, band: Int): Resource<String, ThrowableResourceError> {
        return rdkCentralApiService.getDevicePropertyAsValue(macAddress.formatMac(), RouterDeviceProperty.ChangeBandSsid(band))
    }

    override suspend fun setBandSsid(macAddress: String, band: Int, ssid: String): Resource<Unit, ThrowableResourceError> {
        return rdkCentralApiService.setDeviceProperty(macAddress.formatMac(), RouterDeviceProperty.ChangeBandSsid(band), ssid)
            .map { Unit }
    }

    override suspend fun setBandPassword(macAddress: String, band: Int, password: String): Resource<Unit, ThrowableResourceError> {
        return rdkCentralApiService.setDeviceProperty(macAddress.formatMac(), RouterDeviceProperty.ChangeBandPassword(band), password)
            .map { Unit }
    }


    override suspend fun rebootDevice(macAddress: String): Resource<Unit, ThrowableResourceError> {
        return rdkCentralApiService.doDeviceAction(macAddress.formatMac(), RouterDeviceProperty.ActionReboot)
    }

    override suspend fun factoryResetDevice(macAddress: String): Resource<Unit, ThrowableResourceError> {
        return rdkCentralApiService.doDeviceAction(macAddress.formatMac(), RouterDeviceProperty.ActionFactoryReset)
    }


    companion object {
        private fun String.formatMac(): String = replace(":", "").lowercase()

        private suspend fun RdkCentralApiService.getDevicePropertyAsValue(macAddress: String, property: RouterDeviceProperty.Property): Resource<String, ThrowableResourceError> {
            return getDeviceProperties(macAddress, property)
                .map { response -> response.parameters?.firstOrNull()?.value }
                .flatMapData { value ->
                    when (value) {
                        null -> Failure(property.cantLoad(macAddress))
                        else -> Success(value)
                    }
                }
        }

        private suspend fun <T: Any> RdkCentralApiService.getDevicePropertyAsValue(macAddress: String, property: RouterDeviceProperty.Property, mapper: (String) -> T?): Resource<T, ThrowableResourceError> {
            return getDeviceProperties(macAddress, property)
                .map { response -> response.parameters?.firstOrNull()?.value?.let(mapper) }
                .flatMapData { value ->
                    when (value) {
                        null -> Failure(property.cantLoad(macAddress))
                        else -> Success(value)
                    }
                }
        }

        private fun RouterDeviceProperty.Property.cantLoad(macAddress: String): ThrowableResourceError {
            return ThrowableResourceError(CantLoadDevicePropertyException(macAddress, this))
        }
    }
}
