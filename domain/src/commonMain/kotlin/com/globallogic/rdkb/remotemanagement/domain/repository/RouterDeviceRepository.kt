package com.globallogic.rdkb.remotemanagement.domain.repository

import com.globallogic.rdkb.remotemanagement.domain.entity.AccessPointClient
import com.globallogic.rdkb.remotemanagement.domain.entity.AccessPointGroup
import com.globallogic.rdkb.remotemanagement.domain.entity.AccessPointSettings
import com.globallogic.rdkb.remotemanagement.domain.entity.ConnectedDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.DeviceAccessPointSettings
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.TopologyData
import com.globallogic.rdkb.remotemanagement.domain.entity.WifiMotionData
import com.globallogic.rdkb.remotemanagement.domain.error.DeviceError
import com.globallogic.rdkb.remotemanagement.domain.usecase.routerdevice.RouterDeviceAction
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource
import com.globallogic.rdkb.remotemanagement.domain.utils.ResourceState
import kotlinx.coroutines.flow.Flow

interface RouterDeviceRepository {
    suspend fun getDeviceList(forceUpdate: Boolean): Flow<ResourceState<List<RouterDevice>, DeviceError.NoDevicesFound>>

    suspend fun getTopologyData(): Flow<ResourceState<TopologyData, DeviceError.Topology>>

    suspend fun getRouterDeviceConnectedDevices(forceUpdate: Boolean): Flow<ResourceState<List<ConnectedDevice>, DeviceError.NoConnectedDevicesFound>>
    suspend fun loadAccessPointGroups(forceUpdate: Boolean): Flow<ResourceState<List<AccessPointGroup>, DeviceError.WifiSettings>>
    suspend fun getDeviceAccessPointSettings(accessPointGroup: AccessPointGroup, forceUpdate: Boolean): Flow<ResourceState<AccessPointSettings, DeviceError.WifiSettings>>
    suspend fun setupDeviceAccessPoint(accessPointGroup: AccessPointGroup, settings: DeviceAccessPointSettings): Resource<Unit, DeviceError.SetupDevice>
    suspend fun doAction(action: RouterDeviceAction): Resource<Unit, DeviceError.NoDeviceFound>

    suspend fun selectRouterDevice(device: RouterDevice): Resource.Success<Unit>
    suspend fun getSelectRouterDevice(): Resource<RouterDevice, DeviceError.NoDeviceFound>

    suspend fun loadWifiMotionData(updateIntervalMillis: Long): Flow<ResourceState<WifiMotionData, DeviceError.WifiMotion>>
    suspend fun startWifiMotion(accessPointClient: AccessPointClient): Resource<Unit, DeviceError.WifiMotion>
    suspend fun stopWifiMotion(): Resource<Unit, DeviceError.WifiMotion>
}
