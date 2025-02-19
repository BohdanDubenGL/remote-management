package com.globallogic.rdkb.remotemanagement.domain.repository

import com.globallogic.rdkb.remotemanagement.domain.entity.ConnectedDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDeviceSettings
import com.globallogic.rdkb.remotemanagement.domain.error.DeviceError
import com.globallogic.rdkb.remotemanagement.domain.usecase.routerdevice.RouterDeviceAction
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource

interface RouterDeviceRepository {
    suspend fun getDeviceList(): Resource<List<RouterDevice>, DeviceError.NoDevicesFound>

    suspend fun doAction(device: RouterDevice, action: RouterDeviceAction): Resource<Unit, DeviceError.NoDeviceFound>
    suspend fun getRouterDeviceConnectedDevices(device: RouterDevice): Resource<List<ConnectedDevice>, DeviceError.NoConnectedDevicesFound>
    suspend fun setupRouterDevice(device: RouterDevice, settings: RouterDeviceSettings): Resource<Unit, DeviceError.SetupDevice>

    suspend fun selectRouterDevice(device: RouterDevice): Resource.Success<Unit>
    suspend fun getSelectRouterDevice(): Resource<RouterDevice, DeviceError.NoDeviceFound>
    suspend fun getLocalRouterDevice(): Resource<RouterDevice, DeviceError.NoDeviceFound>
}
