package com.globallogic.rdkb.remotemanagement.data.repository.impl

import com.globallogic.rdkb.remotemanagement.data.datasource.LocalRouterDeviceDataSource
import com.globallogic.rdkb.remotemanagement.data.datasource.RemoteRouterDeviceDataSource
import com.globallogic.rdkb.remotemanagement.data.preferences.AppPreferences
import com.globallogic.rdkb.remotemanagement.domain.entity.AccessPointGroup
import com.globallogic.rdkb.remotemanagement.domain.entity.AccessPointSettings
import com.globallogic.rdkb.remotemanagement.domain.entity.ConnectedDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.DeviceAccessPointSettings
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice
import com.globallogic.rdkb.remotemanagement.domain.error.DeviceError
import com.globallogic.rdkb.remotemanagement.domain.repository.RouterDeviceRepository
import com.globallogic.rdkb.remotemanagement.domain.usecase.routerdevice.RouterDeviceAction
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource.Failure
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource.Success
import com.globallogic.rdkb.remotemanagement.domain.utils.ResourceState
import com.globallogic.rdkb.remotemanagement.domain.utils.dataOrElse
import com.globallogic.rdkb.remotemanagement.domain.utils.map
import com.globallogic.rdkb.remotemanagement.domain.utils.mapError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow

class RouterDeviceRepositoryImpl(
    private val appPreferences: AppPreferences,
    private val remoteRouterDeviceDataSource: RemoteRouterDeviceDataSource,
    private val localRouterDeviceDataSource: LocalRouterDeviceDataSource,
) : RouterDeviceRepository {

    override suspend fun getDeviceList(forceUpdate: Boolean): Flow<ResourceState<List<RouterDevice>, DeviceError.NoDevicesFound>> = channelFlow {
        send(ResourceState.Loading)

        val email = appPreferences.currentUserEmailPref.get()
        if (email == null) {
            send(Failure(DeviceError.NoDevicesFound))
            return@channelFlow
        }

        val savedDevices =  localRouterDeviceDataSource.loadRouterDevicesForUser(email)
            .mapError { error -> DeviceError.NoDevicesFound }
        send(savedDevices)

        if (forceUpdate) {
            savedDevices.dataOrElse { error -> emptyList() }
                .forEach { savedDevice ->
                    remoteRouterDeviceDataSource.findRouterDeviceByMacAddress(savedDevice.macAddress)
                        .map { updatedDevice -> localRouterDeviceDataSource.saveRouterDevice(updatedDevice, email) }
                }

            val updatedDevices = localRouterDeviceDataSource.loadRouterDevicesForUser(email)
                .mapError { error -> DeviceError.NoDevicesFound }
            send(updatedDevices)
        }
    }

    override suspend fun getRouterDeviceConnectedDevices(device: RouterDevice, forceUpdate: Boolean): Flow<ResourceState<List<ConnectedDevice>, DeviceError.NoConnectedDevicesFound>> = channelFlow {
        send(ResourceState.Loading)

        val savedConnectedDevices = localRouterDeviceDataSource.loadConnectedDevices(device)
            .mapError { error -> DeviceError.NoConnectedDevicesFound }
        send(savedConnectedDevices)

        if (forceUpdate) {
            val connectedDevices = remoteRouterDeviceDataSource.loadConnectedDevicesForRouterDevice(device)
                .dataOrElse { error -> null }
            if (connectedDevices != null) {
                localRouterDeviceDataSource.saveConnectedDevices(device, connectedDevices)
                    .dataOrElse { error -> null }

                val updatedConnectedDevices = localRouterDeviceDataSource.loadConnectedDevices(device)
                    .mapError { error -> DeviceError.NoConnectedDevicesFound }
                send(updatedConnectedDevices)
            }
        }
    }

    override suspend fun loadAccessPointGroups(device: RouterDevice): Resource<List<AccessPointGroup>, DeviceError.WifiSettings> {
        return remoteRouterDeviceDataSource.loadAccessPointGroups(device)
            .mapError { error -> DeviceError.WifiSettings }
    }

    override suspend fun getDeviceAccessPointSettings(device: RouterDevice, accessPointGroup: AccessPointGroup): Resource<AccessPointSettings, DeviceError.WifiSettings> {
        return remoteRouterDeviceDataSource.loadAccessPointSettings(device, accessPointGroup)
            .mapError { error -> DeviceError.WifiSettings }
    }

    override suspend fun selectRouterDevice(device: RouterDevice): Success<Unit> {
        appPreferences.currentRouterDeviceMacAddressPref.set(device.macAddress)
        return Success(Unit)
    }

    override suspend fun getSelectRouterDevice(): Resource<RouterDevice, DeviceError.NoDeviceFound> {
        val macAddress = appPreferences.currentRouterDeviceMacAddressPref.get()
            ?: return Failure(DeviceError.NoDeviceFound)
        return localRouterDeviceDataSource.findRouterDeviceByMacAddress(macAddress)
            .mapError { error -> DeviceError.NoDeviceFound }
    }

    override suspend fun getLocalRouterDevice(): Resource<RouterDevice, DeviceError.NoDeviceFound> {
        val email = appPreferences.currentUserEmailPref.get()
            ?: return Failure(DeviceError.NoDeviceFound)
        return localRouterDeviceDataSource.findLocalRouterDevice(email)
            .mapError { error -> DeviceError.NoDeviceFound }
    }

    override suspend fun doAction(device: RouterDevice, action: RouterDeviceAction): Resource<Unit, DeviceError.NoDeviceFound> {
        return when(action) {
            RouterDeviceAction.Restart -> remoteRouterDeviceDataSource.rebootDevice(device)
                .mapError { error -> DeviceError.NoDeviceFound }
            RouterDeviceAction.FactoryReset -> remoteRouterDeviceDataSource.factoryResetDevice(device)
                .mapError { error -> DeviceError.NoDeviceFound }
            RouterDeviceAction.Remove -> removeRouterDevice(device)
        }
    }

    private suspend fun removeRouterDevice(device: RouterDevice): Resource<Unit, DeviceError.NoDeviceFound> {
        val email = appPreferences.currentUserEmailPref.get()
            ?: return Failure(DeviceError.NoDeviceFound)
        localRouterDeviceDataSource.removeRouterDevice(device, email)
            .dataOrElse { error -> return Failure(DeviceError.NoDeviceFound) }

        val macAddress = appPreferences.currentRouterDeviceMacAddressPref.get()
            ?: return Failure(DeviceError.NoDeviceFound)
        if (device.macAddress == macAddress) {
            appPreferences.currentRouterDeviceMacAddressPref.reset()
        }

        return Success(Unit)
    }

    override suspend fun setupDeviceAccessPoint(device: RouterDevice, accessPointGroup: AccessPointGroup, settings: DeviceAccessPointSettings): Resource<Unit, DeviceError.SetupDevice> {
        return remoteRouterDeviceDataSource.setupAccessPoint(device, accessPointGroup, settings)
            .mapError { error -> DeviceError.SetupDevice }
    }
}
