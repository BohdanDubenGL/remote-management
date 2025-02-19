package com.globallogic.rdkb.remotemanagement.data.datasource.impl

import com.globallogic.rdkb.remotemanagement.data.datasource.RouterDeviceConnectionDataSource
import com.globallogic.rdkb.remotemanagement.data.error.IoDeviceError
import com.globallogic.rdkb.remotemanagement.data.network.service.RdkCentralApiService
import com.globallogic.rdkb.remotemanagement.data.network.service.RouterDeviceProperty
import com.globallogic.rdkb.remotemanagement.data.wifi.WifiScanner
import com.globallogic.rdkb.remotemanagement.data.wifi.model.WifiInfo
import com.globallogic.rdkb.remotemanagement.domain.entity.FoundRouterDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource.Failure
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource.Success
import com.globallogic.rdkb.remotemanagement.domain.utils.dataOrElse
import com.globallogic.rdkb.remotemanagement.domain.utils.flatMapData
import com.globallogic.rdkb.remotemanagement.domain.utils.map
import com.globallogic.rdkb.remotemanagement.domain.utils.mapError
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.supervisorScope

class RouterDeviceConnectionDataSourceImpl(
    private val rdkCentralApiService: RdkCentralApiService,
    private val wifiScanner: WifiScanner,
) : RouterDeviceConnectionDataSource {
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

    override suspend fun connectToRouterDevice(macAddress: String): Resource<RouterDevice, IoDeviceError.CantConnectToRouterDevice> {
        return rdkCentralApiService.getDeviceProperties(
            macAddress.replace(":", ""),
            RouterDeviceProperty.ModelName,
            RouterDeviceProperty.IpAddressV4,
            RouterDeviceProperty.MacAddress,
        )
            .flatMapData { response ->
                if (response.parameters?.size != 3) {
                    return@flatMapData Failure<IoDeviceError.CantConnectToRouterDevice>(
                        IoDeviceError.CantConnectToRouterDevice
                    )
                }
                val (name, ip, mac) = response.parameters
                val device = RouterDevice(
                    name = name?.value.orEmpty(),
                    ip = ip?.value.orEmpty(),
                    macAddress = mac?.value.orEmpty()
                )
                Success(device)
            }
            .mapError { error -> IoDeviceError.CantConnectToRouterDevice }
    }
}
