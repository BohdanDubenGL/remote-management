package com.globallogic.rdkb.remotemanagement.data.datasource

import com.globallogic.rdkb.remotemanagement.data.error.IoDeviceError
import com.globallogic.rdkb.remotemanagement.domain.entity.ConnectedDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDeviceInfo
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDeviceSettings
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource

interface RemoteRouterDeviceDataSource {

    suspend fun loadRouterDeviceInfo(device: RouterDevice): Resource<RouterDeviceInfo, IoDeviceError.NoDeviceInfoFound>

    suspend fun loadConnectedDevicesForRouterDevice(device: RouterDevice): Resource<List<ConnectedDevice>, IoDeviceError.LoadConnectedDevicesForRouterDevice>

    suspend fun factoryResetDevice(device: RouterDevice): Resource<Unit, IoDeviceError.FactoryResetDevice>

    suspend fun restartDevice(device: RouterDevice): Resource<Unit, IoDeviceError.RestartDevice>

    suspend fun setupDevice(device: RouterDevice, settings: RouterDeviceSettings): Resource<Unit, IoDeviceError.SetupDevice>
}
