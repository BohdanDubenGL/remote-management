package com.globallogic.rdkb.remotemanagement.domain.repository

import com.globallogic.rdkb.remotemanagement.domain.entity.AccessPointGroup
import com.globallogic.rdkb.remotemanagement.domain.entity.ConnectedDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.DeviceAccessPointSettings
import com.globallogic.rdkb.remotemanagement.domain.entity.AccessPointSettings
import com.globallogic.rdkb.remotemanagement.domain.error.DeviceError
import com.globallogic.rdkb.remotemanagement.domain.usecase.routerdevice.RouterDeviceAction
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource
import com.globallogic.rdkb.remotemanagement.domain.utils.ResourceState
import kotlinx.coroutines.flow.Flow

interface RouterDeviceRepository {
    suspend fun getDeviceList(forceUpdate: Boolean): Flow<ResourceState<List<RouterDevice>, DeviceError.NoDevicesFound>>

    suspend fun getRouterDeviceConnectedDevices(device: RouterDevice, forceUpdate: Boolean): Flow<ResourceState<List<ConnectedDevice>, DeviceError.NoConnectedDevicesFound>>
    suspend fun loadAccessPointGroups(device: RouterDevice): Resource<List<AccessPointGroup>, DeviceError.WifiSettings>
    suspend fun getDeviceAccessPointSettings(device: RouterDevice, accessPointGroup: AccessPointGroup): Resource<AccessPointSettings, DeviceError.WifiSettings>
    suspend fun setupDeviceAccessPoint(device: RouterDevice, accessPointGroup: AccessPointGroup, settings: DeviceAccessPointSettings): Resource<Unit, DeviceError.SetupDevice>
    suspend fun doAction(device: RouterDevice, action: RouterDeviceAction): Resource<Unit, DeviceError.NoDeviceFound>

    suspend fun selectRouterDevice(device: RouterDevice): Resource.Success<Unit>
    suspend fun getSelectRouterDevice(): Resource<RouterDevice, DeviceError.NoDeviceFound>
    suspend fun getLocalRouterDevice(): Resource<RouterDevice, DeviceError.NoDeviceFound>
}
