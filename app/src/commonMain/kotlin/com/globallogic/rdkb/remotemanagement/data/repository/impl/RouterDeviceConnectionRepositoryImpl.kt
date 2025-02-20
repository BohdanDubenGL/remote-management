package com.globallogic.rdkb.remotemanagement.data.repository.impl

import com.globallogic.rdkb.remotemanagement.data.datasource.LocalRouterDeviceDataSource
import com.globallogic.rdkb.remotemanagement.data.datasource.RemoteRouterDeviceDataSource
import com.globallogic.rdkb.remotemanagement.data.preferences.AppPreferences
import com.globallogic.rdkb.remotemanagement.domain.entity.FoundRouterDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice
import com.globallogic.rdkb.remotemanagement.domain.error.DeviceError
import com.globallogic.rdkb.remotemanagement.domain.repository.RouterDeviceConnectionRepository
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource.Failure
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource.Success
import com.globallogic.rdkb.remotemanagement.domain.utils.dataOrElse
import com.globallogic.rdkb.remotemanagement.domain.utils.map
import com.globallogic.rdkb.remotemanagement.domain.utils.mapError

class RouterDeviceConnectionRepositoryImpl(
    private val appPreferences: AppPreferences,
    private val routerDeviceDataSource: RemoteRouterDeviceDataSource,
    private val localRouterDeviceDataSource: LocalRouterDeviceDataSource,
) : RouterDeviceConnectionRepository {

    override suspend fun connectToRouterDevice(device: FoundRouterDevice): Resource<RouterDevice, DeviceError.CantConnectToRouterDevice> {
        return addRouterDeviceManually(device.macAddress)
    }

    override suspend fun addRouterDeviceManually(macAddress: String): Resource<RouterDevice, DeviceError.CantConnectToRouterDevice> {
        val deviceInfo = routerDeviceDataSource.findRouterDeviceByMacAddress(macAddress)
            .dataOrElse { error -> return Failure(DeviceError.CantConnectToRouterDevice) }

        val email = appPreferences.currentUserEmailPref.get()
            ?: return Failure(DeviceError.CantConnectToRouterDevice)
        localRouterDeviceDataSource.saveRouterDevice(deviceInfo, email)
            .dataOrElse { error -> return Failure(DeviceError.CantConnectToRouterDevice) }

        return Success(deviceInfo)
    }

    override suspend fun searchRouterDevices(): Resource<List<FoundRouterDevice>, DeviceError.NoAvailableRouterDevices> {
        val email = appPreferences.currentUserEmailPref.get()
            ?: return Failure(DeviceError.NoAvailableRouterDevices)

        val connectedDevices = localRouterDeviceDataSource.loadRouterDevicesForUser(email)
            .map { devices -> devices.map { it.macAddress } }
            .dataOrElse { error -> return Failure(DeviceError.NoAvailableRouterDevices) }

        return routerDeviceDataSource.findAvailableRouterDevices()
            .map { availableDevices -> availableDevices.filter { it.macAddress !in connectedDevices } }
            .mapError { error -> DeviceError.NoAvailableRouterDevices }
    }
}
