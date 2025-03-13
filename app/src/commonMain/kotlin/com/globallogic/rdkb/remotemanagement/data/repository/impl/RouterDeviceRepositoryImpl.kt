package com.globallogic.rdkb.remotemanagement.data.repository.impl

import com.globallogic.rdkb.remotemanagement.data.datasource.LocalRouterDeviceDataSource
import com.globallogic.rdkb.remotemanagement.data.datasource.RemoteRouterDeviceDataSource
import com.globallogic.rdkb.remotemanagement.data.preferences.AppPreferences
import com.globallogic.rdkb.remotemanagement.domain.entity.AccessPointGroup
import com.globallogic.rdkb.remotemanagement.domain.entity.AccessPointSettings
import com.globallogic.rdkb.remotemanagement.domain.entity.Band
import com.globallogic.rdkb.remotemanagement.domain.entity.ConnectedDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.DeviceAccessPointSettings
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.TopologyData
import com.globallogic.rdkb.remotemanagement.domain.entity.WifiMotionData
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
import com.globallogic.rdkb.remotemanagement.domain.utils.onFailure
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive

class RouterDeviceRepositoryImpl(
    private val appPreferences: AppPreferences,
    private val remoteRouterDeviceDataSource: RemoteRouterDeviceDataSource,
    private val localRouterDeviceDataSource: LocalRouterDeviceDataSource,
) : RouterDeviceRepository {

    override suspend fun getDeviceList(
        forceUpdate: Boolean
    ): Flow<ResourceState<List<RouterDevice>, DeviceError.NoDevicesFound>> = channelFlow {
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

    override suspend fun getTopologyData(): Flow<ResourceState<TopologyData, DeviceError.Topology>> = channelFlow {
        send(ResourceState.Loading)
        val device = getLocalRouterDevice()
            .onFailure { error -> send(Failure(DeviceError.Topology)) }
            .dataOrElse { error -> return@channelFlow }
        getRouterDeviceConnectedDevices(device, forceUpdate = true)
            .map { connectedDevicesState ->
                when(connectedDevicesState) {
                    is ResourceState.None -> connectedDevicesState
                    is ResourceState.Loading -> connectedDevicesState
                    is Resource -> connectedDevicesState
                        .mapError { error -> DeviceError.Topology }
                        .map { connectedDevices ->
                            TopologyData(
                                routerDevice = device,
                                connectedDevices = connectedDevices
                            )
                        }
                }
            }
            .collectLatest(::send)
    }

    private suspend fun getLocalRouterDevice(): Resource<RouterDevice, DeviceError.NoDeviceFound> {
        val email = appPreferences.currentUserEmailPref.get()
            ?: return Failure(DeviceError.NoDeviceFound)
        val savedDevices = localRouterDeviceDataSource.loadRouterDevicesForUser(email)
            .dataOrElse { error -> return Failure(DeviceError.NoDeviceFound) }
        when(savedDevices.size) {
            0 -> return Failure(DeviceError.NoDeviceFound)
            1 -> return Success(savedDevices.first())
        }
        val macAvailableDevices = remoteRouterDeviceDataSource.findAvailableRouterDevices()
            .dataOrElse { error -> emptyList() }
            .map { device -> device.macAddress }

        val localDevice = savedDevices
            .reversed()
            .firstOrNull { device -> device.macAddress in macAvailableDevices }
            ?: savedDevices.firstOrNull()
            ?: return Failure(DeviceError.NoDeviceFound)
        return Success(localDevice)
    }

    override suspend fun getRouterDeviceConnectedDevices(
        forceUpdate: Boolean
    ): Flow<ResourceState<List<ConnectedDevice>, DeviceError.NoConnectedDevicesFound>> = channelFlow {
        send(ResourceState.Loading)

        val device = getSelectRouterDevice()
            .onFailure { error -> send(Failure(DeviceError.NoConnectedDevicesFound)) }
            .dataOrElse { error -> return@channelFlow }
        getRouterDeviceConnectedDevices(device, forceUpdate)
            .collectLatest(::send)
    }

    private fun getRouterDeviceConnectedDevices(
        device: RouterDevice,
        forceUpdate: Boolean
    ): Flow<ResourceState<List<ConnectedDevice>, DeviceError.NoConnectedDevicesFound>> = channelFlow {
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

    override suspend fun loadAccessPointGroups(
        forceUpdate: Boolean
    ): Flow<ResourceState<List<AccessPointGroup>, DeviceError.WifiSettings>> = channelFlow {
        send(ResourceState.Loading)

        val device = getSelectRouterDevice()
            .onFailure { error -> send(Failure(DeviceError.WifiSettings)) }
            .dataOrElse { error -> return@channelFlow }

        val savedAccessPointGroups = localRouterDeviceDataSource.loadAccessPointGroups(device)
            .mapError { error -> DeviceError.WifiSettings }
        send(savedAccessPointGroups)

        if (forceUpdate) {
            val accessPointGroups = remoteRouterDeviceDataSource.loadAccessPointGroups(device)
                .dataOrElse { error -> null }
            if (accessPointGroups != null) {
                localRouterDeviceDataSource.saveAccessPointGroups(device, accessPointGroups)
                    .dataOrElse { error -> null }

                val updatedAccessPointGroups = localRouterDeviceDataSource.loadAccessPointGroups(device)
                    .mapError { error -> DeviceError.WifiSettings }
                send(updatedAccessPointGroups)
            }
        }
    }

    override suspend fun getDeviceAccessPointSettings(
        accessPointGroup: AccessPointGroup,
        forceUpdate: Boolean
    ): Flow<ResourceState<AccessPointSettings, DeviceError.WifiSettings>> = channelFlow {
        send(ResourceState.Loading)

        val device = getSelectRouterDevice()
            .onFailure { error -> send(Failure(DeviceError.WifiSettings)) }
            .dataOrElse { error -> return@channelFlow }
        val savedAccessPointSettings = localRouterDeviceDataSource.loadDeviceAccessPointSettings(device, accessPointGroup)
            .mapError { error -> DeviceError.WifiSettings }
        send(savedAccessPointSettings)

        if (forceUpdate) {
            val accessPointSettings = remoteRouterDeviceDataSource.loadAccessPointSettings(device, accessPointGroup)
                .dataOrElse { error -> null }
            if (accessPointSettings != null) {
                localRouterDeviceDataSource.saveDeviceAccessPointSettings(device, accessPointSettings)
                    .dataOrElse { error -> null }

                val updatedAccessPointSettings = localRouterDeviceDataSource.loadDeviceAccessPointSettings(device, accessPointGroup)
                    .mapError { error -> DeviceError.WifiSettings }
                send(updatedAccessPointSettings)
            }
        }
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

    override suspend fun doAction(
        action: RouterDeviceAction
    ): Resource<Unit, DeviceError.NoDeviceFound> {
        val device = getSelectRouterDevice()
            .dataOrElse { error -> return Failure(DeviceError.NoDeviceFound) }
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

    override suspend fun setupDeviceAccessPoint(
        accessPointGroup: AccessPointGroup,
        settings: DeviceAccessPointSettings
    ): Resource<Unit, DeviceError.SetupDevice> {
        val device = getSelectRouterDevice()
            .dataOrElse { error -> return Failure(DeviceError.SetupDevice) }
        return remoteRouterDeviceDataSource.setupAccessPoint(device, accessPointGroup, settings)
            .mapError { error -> DeviceError.SetupDevice }
    }

    override suspend fun loadWifiMotionData(
        updateIntervalMillis: Long
    ): Flow<ResourceState<WifiMotionData, DeviceError.WifiMotion>> = channelFlow {
        send(ResourceState.Loading)

        val device = getSelectRouterDevice()
            .onFailure { send(Failure(DeviceError.WifiMotion)) }
            .dataOrElse { error -> return@channelFlow }
        val connectedDevicesFlow = getRouterDeviceConnectedDevices(forceUpdate = true)
            .stateIn(this, SharingStarted.Eagerly, ResourceState.None)
        val motionEventsFlow = remoteRouterDeviceDataSource.pollWifiMotionEvents(device, updateIntervalMillis)
            .stateIn(this, SharingStarted.Eagerly, Success(emptyList()))

        delay(updateIntervalMillis)

        while(isActive) {
            val currentHostMacAddress = remoteRouterDeviceDataSource.getWifiMotionState(device)
                .dataOrElse { error -> null }
            val motionPercent = remoteRouterDeviceDataSource.getWifiMotionPercent(device)
                .dataOrElse { error -> null }
            val motionEvents = motionEventsFlow.value.data
            val hosts = when(val hosts = connectedDevicesFlow.value) {
                is ResourceState.None -> null
                is ResourceState.Loading -> null
                is Failure -> null
                is Success -> hosts.data.filter { it.band == Band.Band_5 }
            }

            if (currentHostMacAddress != null && motionPercent != null && hosts != null && motionEvents != null) {
                val motionData = WifiMotionData(
                    currentHostMacAddress = currentHostMacAddress,
                    motionPercent = motionPercent,
                    hosts = hosts,
                    events = motionEvents,
                )
                send(Success(motionData))
            } else {
                send(Failure(DeviceError.WifiMotion))
            }

            delay(updateIntervalMillis)
        }
    }

    override suspend fun startWifiMotion(
        connectedDevice: ConnectedDevice
    ): Resource<Unit, DeviceError.WifiMotion> {
        val device = getSelectRouterDevice()
            .dataOrElse { error -> return Failure(DeviceError.WifiMotion) }
        return remoteRouterDeviceDataSource.startWifiMotion(device, connectedDevice)
            .mapError { error -> DeviceError.WifiMotion }
    }

    override suspend fun stopWifiMotion(): Resource<Unit, DeviceError.WifiMotion> {
        val device = getSelectRouterDevice()
            .dataOrElse { error -> return Failure(DeviceError.WifiMotion) }
        return remoteRouterDeviceDataSource.stopWifiMotion(device)
            .mapError { error -> DeviceError.WifiMotion }
    }
}
