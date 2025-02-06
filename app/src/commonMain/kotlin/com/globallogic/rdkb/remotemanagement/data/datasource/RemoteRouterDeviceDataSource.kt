package com.globallogic.rdkb.remotemanagement.data.datasource

import com.globallogic.rdkb.remotemanagement.domain.entity.ConnectedDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDeviceInfo
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDeviceSettings

interface RemoteRouterDeviceDataSource {

    suspend fun loadRouterDeviceInfo(device: RouterDevice): Result<RouterDeviceInfo>

    suspend fun loadConnectedDevicesForRouterDevice(device: RouterDevice): Result<List<ConnectedDevice>>

    suspend fun factoryResetDevice(device: RouterDevice): Result<Unit>

    suspend fun restartDevice(device: RouterDevice): Result<Unit>

    suspend fun setupDevice(device: RouterDevice, settings: RouterDeviceSettings): Result<Unit>
}
