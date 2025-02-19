package com.globallogic.rdkb.remotemanagement.data.datasource.impl

import com.globallogic.rdkb.remotemanagement.data.datasource.RemoteRouterDeviceDataSource
import com.globallogic.rdkb.remotemanagement.data.error.IoDeviceError
import com.globallogic.rdkb.remotemanagement.data.network.service.RdkCentralApiService
import com.globallogic.rdkb.remotemanagement.data.network.service.RouterDeviceProperty
import com.globallogic.rdkb.remotemanagement.data.wifi.WifiScanner
import com.globallogic.rdkb.remotemanagement.data.wifi.model.WifiInfo
import com.globallogic.rdkb.remotemanagement.domain.entity.ConnectedDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.FoundRouterDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDeviceSettings
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource.Failure
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource.Success
import com.globallogic.rdkb.remotemanagement.domain.utils.component6
import com.globallogic.rdkb.remotemanagement.domain.utils.component7
import com.globallogic.rdkb.remotemanagement.domain.utils.dataOrElse
import com.globallogic.rdkb.remotemanagement.domain.utils.flatMapData
import com.globallogic.rdkb.remotemanagement.domain.utils.map
import com.globallogic.rdkb.remotemanagement.domain.utils.mapError
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.supervisorScope

class RemoteRouterDeviceDataSourceImpl(
    private val rdkCentralApiService: RdkCentralApiService,
    private val wifiScanner: WifiScanner,
) : RemoteRouterDeviceDataSource {
    override suspend fun findAvailableRouterDevices(): Resource<List<FoundRouterDevice>, IoDeviceError.NoAvailableRouterDevices> = supervisorScope {
        val currentWifi = wifiScanner.getCurrentWifi()
        rdkCentralApiService.getAvailableDevices()
            .map { response ->
                response.devices
                    ?.mapNotNull { device -> device?.id?.split(":")?.getOrNull(1) }
                    ?.filter { macAddress -> isMacAddressSimilarToCurrentWifi(macAddress, currentWifi) }
                    ?.map { macAddress -> async { loadFoundRouterDevice(macAddress) } }
                    ?.awaitAll()
                    ?.filterNotNull()
                    .orEmpty()
            }
            .mapError { error -> IoDeviceError.NoAvailableRouterDevices }
    }

    private suspend fun loadFoundRouterDevice(macAddress: String): FoundRouterDevice? {
        return rdkCentralApiService.getDeviceProperties(
            macAddress,
            RouterDeviceProperty.ModelName,
            RouterDeviceProperty.IpAddressV4,
            RouterDeviceProperty.MacAddress,
        )
            .map { response ->
                if (response.parameters?.size != 3) return@map null
                val (name, ip, mac) = response.parameters
                FoundRouterDevice(
                    name = name?.value.orEmpty(),
                    ip = ip?.value.orEmpty(),
                    macAddress = mac?.value.orEmpty()
                )
            }
            .dataOrElse { error -> null }
    }

    private fun isMacAddressSimilarToCurrentWifi(macAddress: String, currentWifiInfo: WifiInfo?): Boolean {
        val currentWifiBssid = currentWifiInfo?.bssid?.replace(":", "")
        val similarBytes = 2
        return macAddress.take(similarBytes).equals(currentWifiBssid?.take(similarBytes), ignoreCase = true)
    }

    override suspend fun findRouterDeviceByMacAddress(macAddress: String): Resource<RouterDevice, IoDeviceError.CantConnectToRouterDevice> {
        return rdkCentralApiService.getDeviceProperties(
            macAddress.replace(":", ""),
            RouterDeviceProperty.ModelName,
            RouterDeviceProperty.IpAddressV4,
            RouterDeviceProperty.MacAddress,
            RouterDeviceProperty.SoftwareVersion,
            RouterDeviceProperty.SerialNumber,
            RouterDeviceProperty.OperatingFrequencyBand(10_000),
            RouterDeviceProperty.OperatingFrequencyBand(10_100),
//            RouterDeviceProperty.OperatingFrequencyBand(10_200),
        )
            .flatMapData { response ->
                if (response.parameters?.size != 7) {
                    return@flatMapData Failure(IoDeviceError.CantConnectToRouterDevice)
                }
                val (modelName, ip, mac, firmwareVersion, serialNumber, band1, band2) = response.parameters
                val info = RouterDevice(
                    lanConnected = true,
                    connectedExtender = 0,
                    modelName = modelName?.value.orEmpty(),
                    ipAddress = ip?.value.orEmpty(),
                    macAddress = mac?.value.orEmpty(),
                    firmwareVersion = firmwareVersion?.value.orEmpty(),
                    serialNumber = serialNumber?.value.orEmpty(),
                    processorLoadPercent = 0,
                    memoryUsagePercent = 0,
                    totalDownloadTraffic = 0,
                    totalUploadTraffic = 0,
                    availableBands = setOf(band1?.value.orEmpty(), band2?.value.orEmpty()),
                )
                Success(info)
            }
            .mapError { error -> IoDeviceError.CantConnectToRouterDevice }
    }

    override suspend fun loadConnectedDevicesForRouterDevice(device: RouterDevice): Resource<List<ConnectedDevice>, IoDeviceError.LoadConnectedDevicesForRouterDevice> {
        return Success(emptyList())
    }

    override suspend fun setupDevice(device: RouterDevice, settings: RouterDeviceSettings): Resource<Unit, IoDeviceError.SetupDevice> {
        return Success(Unit)
    }

    override suspend fun factoryResetDevice(device: RouterDevice): Resource<Unit, IoDeviceError.FactoryResetDevice> {
        return Success(Unit)
    }

    override suspend fun restartDevice(device: RouterDevice): Resource<Unit, IoDeviceError.RestartDevice> {
        return Success(Unit)
    }
}
