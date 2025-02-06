package com.globallogic.rdkb.remotemanagement.data.repository.impl

import com.globallogic.rdkb.remotemanagement.data.datasource.LocalRouterDeviceDataSource
import com.globallogic.rdkb.remotemanagement.data.datasource.RemoteRouterDeviceDataSource
import com.globallogic.rdkb.remotemanagement.data.preferences.AppPreferences
import com.globallogic.rdkb.remotemanagement.domain.entity.ConnectedDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDeviceInfo
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDeviceSettings
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDeviceTopologyData
import com.globallogic.rdkb.remotemanagement.domain.repository.RouterDeviceRepository
import com.globallogic.rdkb.remotemanagement.domain.utils.runCatchingSafe

class RouterDeviceRepositoryImpl(
    private val appPreferences: AppPreferences,
    private val remoteRouterDeviceDataSource: RemoteRouterDeviceDataSource,
    private val localRouterDeviceDataSource: LocalRouterDeviceDataSource,
) : RouterDeviceRepository {
    private suspend fun currentUserEmail(): String = appPreferences.currentUserEmailPref.get() ?: error("No user")
    private suspend fun currentRouterDeviceMacAddress(): String = appPreferences.currentRouterDeviceMacAddressPref.get() ?: error("No current device")

    override suspend fun getDeviceList(): Result<List<RouterDevice>> {
        return localRouterDeviceDataSource.loadRouterDevicesForUser(currentUserEmail())
    }

    override suspend fun getRouterDeviceConnectedDevices(device: RouterDevice): Result<List<ConnectedDevice>> {
        return remoteRouterDeviceDataSource.loadConnectedDevicesForRouterDevice(device)
            .mapCatching { connectedDevices ->
                localRouterDeviceDataSource.saveConnectedDevices(device, connectedDevices).getOrThrow()
                return localRouterDeviceDataSource.loadConnectedDevices(device)
            }
    }

    override suspend fun getRouterDeviceInfo(device: RouterDevice): Result<RouterDeviceInfo?> {
        return remoteRouterDeviceDataSource.loadRouterDeviceInfo(device)
            .mapCatching { deviceInfo ->
                localRouterDeviceDataSource.saveRouterDevice(deviceInfo, currentUserEmail()).getOrThrow()
                localRouterDeviceDataSource.loadDeviceInfo(device).getOrThrow()
            }
    }

    override suspend fun getRouterDeviceTopologyData(device: RouterDevice): Result<RouterDeviceTopologyData?> = runCatchingSafe {
        remoteRouterDeviceDataSource.loadRouterDeviceInfo(device)
            .mapCatching { deviceInfo -> localRouterDeviceDataSource.saveRouterDevice(deviceInfo, currentUserEmail()) }
            .getOrThrow()

        remoteRouterDeviceDataSource.loadConnectedDevicesForRouterDevice(device)
            .mapCatching { connectedDevices -> localRouterDeviceDataSource.saveConnectedDevices(device, connectedDevices) }
            .getOrThrow()

        localRouterDeviceDataSource.loadTopologyData(device).getOrThrow()
    }

    override suspend fun removeRouterDevice(device: RouterDevice): Result<Unit> {
        return localRouterDeviceDataSource.removeRouterDevice(device, currentUserEmail())
            .mapCatching {
                if (device.macAddress == currentRouterDeviceMacAddress()) {
                    appPreferences.currentRouterDeviceMacAddressPref.reset()
                }
            }
    }

    override suspend fun selectRouterDevice(device: RouterDevice): Result<Unit> = runCatchingSafe {
        appPreferences.currentRouterDeviceMacAddressPref.set(device.macAddress)
    }

    override suspend fun getSelectRouterDevice(): Result<RouterDevice?> {
        return localRouterDeviceDataSource.findRouterDeviceByMacAddress(currentRouterDeviceMacAddress())
    }

    override suspend fun getLocalRouterDevice(): Result<RouterDevice?> {
        return localRouterDeviceDataSource.findLocalRouterDevice(currentUserEmail())
    }

    override suspend fun factoryResetRouterDevice(device: RouterDevice): Result<Unit> {
        return remoteRouterDeviceDataSource.factoryResetDevice(device)
    }

    override suspend fun restartRouterDevice(device: RouterDevice): Result<Unit> {
        return remoteRouterDeviceDataSource.restartDevice(device)
    }

    override suspend fun setupRouterDevice(device: RouterDevice, settings: RouterDeviceSettings): Result<Unit> {
        return remoteRouterDeviceDataSource.setupDevice(device, settings)
    }
}
