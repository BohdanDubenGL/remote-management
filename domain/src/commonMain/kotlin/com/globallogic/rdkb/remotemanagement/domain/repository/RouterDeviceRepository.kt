package com.globallogic.rdkb.remotemanagement.domain.repository

import com.globallogic.rdkb.remotemanagement.domain.entity.ConnectedDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDeviceInfo
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDeviceSettings
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDeviceTopologyData

interface RouterDeviceRepository {
    suspend fun getDeviceList(): Result<List<RouterDevice>>

    suspend fun factoryResetRouterDevice(device: RouterDevice): Result<Unit>
    suspend fun getRouterDeviceConnectedDevices(device: RouterDevice): Result<List<ConnectedDevice>>
    suspend fun getRouterDeviceInfo(device: RouterDevice): Result<RouterDeviceInfo?>
    suspend fun getRouterDeviceTopologyData(device: RouterDevice): Result<RouterDeviceTopologyData?>
    suspend fun restartRouterDevice(device: RouterDevice): Result<Unit>
    suspend fun setupRouterDevice(device: RouterDevice, settings: RouterDeviceSettings): Result<Unit>
    suspend fun removeRouterDevice(device: RouterDevice): Result<Unit>

    suspend fun selectRouterDevice(device: RouterDevice): Result<Unit>
    suspend fun getSelectRouterDevice(): Result<RouterDevice?>
    suspend fun getLocalRouterDevice(): Result<RouterDevice?>
}
