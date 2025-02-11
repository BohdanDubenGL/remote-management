package com.globallogic.rdkb.remotemanagement.data.repository.impl

import com.globallogic.rdkb.remotemanagement.data.datasource.LocalRouterDeviceDataSource
import com.globallogic.rdkb.remotemanagement.data.datasource.RemoteRouterDeviceDataSource
import com.globallogic.rdkb.remotemanagement.data.preferences.AppPreferences
import com.globallogic.rdkb.remotemanagement.domain.entity.ConnectedDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDeviceInfo
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDeviceSettings
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDeviceTopologyData
import com.globallogic.rdkb.remotemanagement.domain.error.DeviceError
import com.globallogic.rdkb.remotemanagement.domain.repository.RouterDeviceRepository
import com.globallogic.rdkb.remotemanagement.domain.utils.BuildResourceScope.failure
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource
import com.globallogic.rdkb.remotemanagement.domain.utils.buildResource
import com.globallogic.rdkb.remotemanagement.domain.utils.dataOrElse
import com.globallogic.rdkb.remotemanagement.domain.utils.mapError

class RouterDeviceRepositoryImpl(
    private val appPreferences: AppPreferences,
    private val remoteRouterDeviceDataSource: RemoteRouterDeviceDataSource,
    private val localRouterDeviceDataSource: LocalRouterDeviceDataSource,
) : RouterDeviceRepository {

    override suspend fun getDeviceList(): Resource<List<RouterDevice>, DeviceError.NoDevicesFound> = buildResource {
        val email = appPreferences.currentUserEmailPref.get()
            ?: return failure(DeviceError.NoDevicesFound)

        return localRouterDeviceDataSource.loadRouterDevicesForUser(email)
            .mapError { error -> DeviceError.NoDevicesFound }
    }

    override suspend fun getRouterDeviceConnectedDevices(device: RouterDevice): Resource<List<ConnectedDevice>, DeviceError.NoConnectedDevicesFound> = buildResource {
        val connectedDevices = remoteRouterDeviceDataSource.loadConnectedDevicesForRouterDevice(device)
            .dataOrElse { error -> return failure(DeviceError.NoConnectedDevicesFound) }

        localRouterDeviceDataSource.saveConnectedDevices(device, connectedDevices)
            .dataOrElse { error -> return failure(DeviceError.NoConnectedDevicesFound) }

        return localRouterDeviceDataSource.loadConnectedDevices(device)
            .mapError { error -> DeviceError.NoConnectedDevicesFound }
    }

    override suspend fun getRouterDeviceInfo(device: RouterDevice): Resource<RouterDeviceInfo, DeviceError.NoDeviceInfoFound> = buildResource {
        val deviceInfo = remoteRouterDeviceDataSource.loadRouterDeviceInfo(device)
            .dataOrElse { error -> return failure(DeviceError.NoDeviceInfoFound) }

        val email = appPreferences.currentUserEmailPref.get()
            ?: return failure(DeviceError.NoDeviceInfoFound)
        localRouterDeviceDataSource.saveRouterDevice(deviceInfo, email)
            .dataOrElse { error -> return failure(DeviceError.NoDeviceInfoFound) }
        localRouterDeviceDataSource.loadDeviceInfo(device)
            .dataOrElse { error -> return failure(DeviceError.NoDeviceInfoFound) }

        return success(deviceInfo)
    }

    override suspend fun getRouterDeviceTopologyData(device: RouterDevice): Resource<RouterDeviceTopologyData, DeviceError.NoTopologyDataFound> = buildResource {
        val deviceInfo = remoteRouterDeviceDataSource.loadRouterDeviceInfo(device)
            .dataOrElse { error -> return failure(DeviceError.NoTopologyDataFound) }
        val email = appPreferences.currentUserEmailPref.get()
            ?: return failure(DeviceError.NoTopologyDataFound)
        localRouterDeviceDataSource.saveRouterDevice(deviceInfo, email)
            .dataOrElse { error -> return failure(DeviceError.NoTopologyDataFound) }

        val connectedDevices = remoteRouterDeviceDataSource.loadConnectedDevicesForRouterDevice(device)
            .dataOrElse { error -> return failure(DeviceError.NoTopologyDataFound) }

        localRouterDeviceDataSource.saveConnectedDevices(device, connectedDevices)
            .dataOrElse { error -> return failure(DeviceError.NoTopologyDataFound) }

        return localRouterDeviceDataSource.loadTopologyData(device)
            .mapError { error -> return failure(DeviceError.NoTopologyDataFound) }
    }

    override suspend fun removeRouterDevice(device: RouterDevice): Resource<Unit, DeviceError.NoDevicesFound> = buildResource {
        val email = appPreferences.currentUserEmailPref.get()
            ?: return failure(DeviceError.NoDevicesFound)
        localRouterDeviceDataSource.removeRouterDevice(device, email)
            .dataOrElse { error -> return failure(DeviceError.NoDevicesFound) }

        val macAddress = appPreferences.currentRouterDeviceMacAddressPref.get()
            ?: return failure(DeviceError.NoDevicesFound)
        if (device.macAddress == macAddress) {
            appPreferences.currentRouterDeviceMacAddressPref.reset()
        }

        return success(Unit)
    }

    override suspend fun selectRouterDevice(device: RouterDevice): Resource.Success<Unit> {
        appPreferences.currentRouterDeviceMacAddressPref.set(device.macAddress)
        return Resource.Success(Unit)
    }

    override suspend fun getSelectRouterDevice(): Resource<RouterDevice, DeviceError.NoDeviceFound> = buildResource {
        val macAddress = appPreferences.currentRouterDeviceMacAddressPref.get()
            ?: return failure(DeviceError.NoDeviceFound)
        return localRouterDeviceDataSource.findRouterDeviceByMacAddress(macAddress)
            .mapError { error -> DeviceError.NoDeviceFound }
    }

    override suspend fun getLocalRouterDevice(): Resource<RouterDevice, DeviceError.NoDeviceFound> {
        val email = appPreferences.currentUserEmailPref.get()
            ?: return failure(DeviceError.NoDeviceFound)
        return localRouterDeviceDataSource.findLocalRouterDevice(email)
            .mapError { error -> DeviceError.NoDeviceFound }
    }

    override suspend fun factoryResetRouterDevice(device: RouterDevice): Resource<Unit, DeviceError.FactoryResetDevice> {
        return remoteRouterDeviceDataSource.factoryResetDevice(device)
            .mapError { error -> DeviceError.FactoryResetDevice }
    }

    override suspend fun restartRouterDevice(device: RouterDevice): Resource<Unit, DeviceError.RestartDevice> {
        return remoteRouterDeviceDataSource.restartDevice(device)
            .mapError { error -> DeviceError.RestartDevice }
    }

    override suspend fun setupRouterDevice(device: RouterDevice, settings: RouterDeviceSettings): Resource<Unit, DeviceError.SetupDevice> {
        return remoteRouterDeviceDataSource.setupDevice(device, settings)
            .mapError { error -> DeviceError.SetupDevice }
    }
}
