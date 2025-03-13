package com.globallogic.rdkb.remotemanagement.data.datasource

import com.globallogic.rdkb.remotemanagement.data.error.IoDeviceError
import com.globallogic.rdkb.remotemanagement.domain.entity.AccessPointGroup
import com.globallogic.rdkb.remotemanagement.domain.entity.ConnectedDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.FoundRouterDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.DeviceAccessPointSettings
import com.globallogic.rdkb.remotemanagement.domain.entity.AccessPointSettings
import com.globallogic.rdkb.remotemanagement.domain.entity.WifiMotionEvent
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource
import kotlinx.coroutines.flow.Flow

interface RemoteRouterDeviceDataSource {
    suspend fun findAvailableRouterDevices(): Resource<List<FoundRouterDevice>, IoDeviceError.NoAvailableRouterDevices>

    suspend fun findRouterDeviceByMacAddress(macAddress: String): Resource<RouterDevice, IoDeviceError.CantConnectToRouterDevice>

    suspend fun loadConnectedDevicesForRouterDevice(device: RouterDevice): Resource<List<ConnectedDevice>, IoDeviceError.LoadConnectedDevicesForRouterDevice>

    suspend fun factoryResetDevice(device: RouterDevice): Resource<Unit, IoDeviceError.FactoryResetDevice>

    suspend fun rebootDevice(device: RouterDevice): Resource<Unit, IoDeviceError.RestartDevice>

    suspend fun loadAccessPointGroups(device: RouterDevice): Resource<List<AccessPointGroup>, IoDeviceError.WifiSettings>

    suspend fun loadAccessPointSettings(device: RouterDevice, accessPointGroup: AccessPointGroup): Resource<AccessPointSettings, IoDeviceError.WifiSettings>

    suspend fun setupAccessPoint(device: RouterDevice, accessPointGroup: AccessPointGroup, settings: DeviceAccessPointSettings): Resource<Unit, IoDeviceError.SetupDevice>

    suspend fun getWifiMotionState(device: RouterDevice): Resource<String, IoDeviceError.WifiMotion>

    suspend fun getWifiMotionPercent(device: RouterDevice): Resource<Int, IoDeviceError.WifiMotion>

    suspend fun startWifiMotion(device: RouterDevice, connectedDevice: ConnectedDevice): Resource<Unit, IoDeviceError.WifiMotion>

    suspend fun stopWifiMotion(device: RouterDevice): Resource<Unit, IoDeviceError.WifiMotion>

    suspend fun pollWifiMotionEvents(device: RouterDevice, updateIntervalMillis: Long): Flow<Resource<List<WifiMotionEvent>, IoDeviceError.WifiMotion>>
}
