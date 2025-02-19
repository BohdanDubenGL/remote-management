package com.globallogic.rdkb.remotemanagement.data.repository.impl

import com.globallogic.rdkb.remotemanagement.data.datasource.LocalRouterDeviceDataSource
import com.globallogic.rdkb.remotemanagement.data.datasource.RemoteRouterDeviceDataSource
import com.globallogic.rdkb.remotemanagement.data.preferences.AppPreferences
import com.globallogic.rdkb.remotemanagement.domain.entity.ConnectedDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDeviceSettings
import com.globallogic.rdkb.remotemanagement.domain.error.DeviceError
import com.globallogic.rdkb.remotemanagement.domain.repository.RouterDeviceRepository
import com.globallogic.rdkb.remotemanagement.domain.usecase.routerdevice.RouterDeviceAction
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource.Failure
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource.Success
import com.globallogic.rdkb.remotemanagement.domain.utils.dataOrElse
import com.globallogic.rdkb.remotemanagement.domain.utils.mapError

class RouterDeviceRepositoryImpl(
    private val appPreferences: AppPreferences,
    private val remoteRouterDeviceDataSource: RemoteRouterDeviceDataSource,
    private val localRouterDeviceDataSource: LocalRouterDeviceDataSource,
) : RouterDeviceRepository {

    override suspend fun getDeviceList(): Resource<List<RouterDevice>, DeviceError.NoDevicesFound> {
        val email = appPreferences.currentUserEmailPref.get()
            ?: return Failure(DeviceError.NoDevicesFound)

        return localRouterDeviceDataSource.loadRouterDevicesForUser(email)
            .mapError { error -> DeviceError.NoDevicesFound }
    }

    override suspend fun getRouterDeviceConnectedDevices(device: RouterDevice): Resource<List<ConnectedDevice>, DeviceError.NoConnectedDevicesFound> {
        val connectedDevices = remoteRouterDeviceDataSource.loadConnectedDevicesForRouterDevice(device)
            .dataOrElse { error -> return Failure(DeviceError.NoConnectedDevicesFound) }

        localRouterDeviceDataSource.saveConnectedDevices(device, connectedDevices)
            .dataOrElse { error -> return Failure(DeviceError.NoConnectedDevicesFound) }

        return localRouterDeviceDataSource.loadConnectedDevices(device)
            .mapError { error -> DeviceError.NoConnectedDevicesFound }
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
            RouterDeviceAction.Restart -> remoteRouterDeviceDataSource.restartDevice(device)
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

    override suspend fun setupRouterDevice(device: RouterDevice, settings: RouterDeviceSettings): Resource<Unit, DeviceError.SetupDevice> {
        return remoteRouterDeviceDataSource.setupDevice(device, settings)
            .mapError { error -> DeviceError.SetupDevice }
    }
}
