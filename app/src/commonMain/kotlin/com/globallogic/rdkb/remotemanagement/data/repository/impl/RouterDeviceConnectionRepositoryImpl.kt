package com.globallogic.rdkb.remotemanagement.data.repository.impl

import com.globallogic.rdkb.remotemanagement.data.datasource.LocalRouterDeviceDataSource
import com.globallogic.rdkb.remotemanagement.data.datasource.RemoteRouterDeviceDataSource
import com.globallogic.rdkb.remotemanagement.data.datasource.RouterDeviceConnectionDataSource
import com.globallogic.rdkb.remotemanagement.data.preferences.AppPreferences
import com.globallogic.rdkb.remotemanagement.domain.entity.FoundRouterDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice
import com.globallogic.rdkb.remotemanagement.domain.repository.RouterDeviceConnectionRepository
import com.globallogic.rdkb.remotemanagement.domain.utils.runCatchingSafe

class RouterDeviceConnectionRepositoryImpl(
    private val appPreferences: AppPreferences,
    private val routerDeviceConnectionDataSource: RouterDeviceConnectionDataSource,
    private val localRouterDeviceDataSource: LocalRouterDeviceDataSource,
    private val remoteRouterDeviceDataSource: RemoteRouterDeviceDataSource,
) : RouterDeviceConnectionRepository {
    private suspend fun currentUserEmail(): String = appPreferences.currentUserEmailPref.get() ?: error("No user")

    override suspend fun connectToRouterDevice(device: FoundRouterDevice): Result<RouterDevice> {
        return addRouterDeviceManually(device.macAddress)
    }

    override suspend fun addRouterDeviceManually(macAddress: String): Result<RouterDevice> = runCatchingSafe {
        val device = routerDeviceConnectionDataSource.connectToRouterDevice(macAddress).getOrThrow() ?: error("Can't connect")
        val deviceInfo = remoteRouterDeviceDataSource.loadRouterDeviceInfo(device).getOrThrow()
        localRouterDeviceDataSource.saveRouterDevice(deviceInfo, currentUserEmail())
        device
    }

    override suspend fun searchRouterDevices(): Result<List<FoundRouterDevice>> {
        return localRouterDeviceDataSource.loadRouterDevicesForUser(currentUserEmail())
            .map { devices -> devices.map { it.macAddress } }
            .mapCatching { connectedDevices ->
                routerDeviceConnectionDataSource.findAvailableRouterDevices().getOrThrow()
                    .filter { it.macAddress !in connectedDevices }
            }
    }
}
