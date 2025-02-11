package com.globallogic.rdkb.remotemanagement.domain.repository

import com.globallogic.rdkb.remotemanagement.domain.entity.ConnectedDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDeviceInfo
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDeviceSettings
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDeviceTopologyData
import com.globallogic.rdkb.remotemanagement.domain.error.DeviceError
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource

interface RouterDeviceRepository {
    suspend fun getDeviceList(): Resource<List<RouterDevice>, DeviceError.NoDevicesFound>

    suspend fun factoryResetRouterDevice(device: RouterDevice): Resource<Unit, DeviceError.FactoryResetDevice>
    suspend fun getRouterDeviceConnectedDevices(device: RouterDevice): Resource<List<ConnectedDevice>, DeviceError.NoConnectedDevicesFound>
    suspend fun getRouterDeviceInfo(device: RouterDevice): Resource<RouterDeviceInfo, DeviceError.NoDeviceInfoFound>
    suspend fun getRouterDeviceTopologyData(device: RouterDevice): Resource<RouterDeviceTopologyData, DeviceError.NoTopologyDataFound>
    suspend fun restartRouterDevice(device: RouterDevice): Resource<Unit, DeviceError.RestartDevice>
    suspend fun setupRouterDevice(device: RouterDevice, settings: RouterDeviceSettings): Resource<Unit, DeviceError.SetupDevice>
    suspend fun removeRouterDevice(device: RouterDevice): Resource<Unit, DeviceError.NoDevicesFound>

    suspend fun selectRouterDevice(device: RouterDevice): Resource.Success<Unit>
    suspend fun getSelectRouterDevice(): Resource<RouterDevice, DeviceError.NoDeviceFound>
    suspend fun getLocalRouterDevice(): Resource<RouterDevice, DeviceError.NoDeviceFound>
}
