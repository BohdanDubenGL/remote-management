package com.globallogic.rdkb.remotemanagement.domain.repository

import com.globallogic.rdkb.remotemanagement.domain.entity.ConnectedDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDeviceInfo
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDeviceSettings
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDeviceTopologyData

interface RouterDeviceRepository {
    suspend fun factoryResetRouterDevice(device: RouterDevice)
    suspend fun getRouterDeviceConnectedDevices(device: RouterDevice): List<ConnectedDevice>
    suspend fun getRouterDevice(deviceMacAddress: String): RouterDevice
    suspend fun getRouterDeviceInfo(device: RouterDevice): RouterDeviceInfo
    suspend fun getRouterDeviceTopologyData(device: RouterDevice): RouterDeviceTopologyData
    suspend fun restartRouterDevice(device: RouterDevice)
    suspend fun setupRouterDevice(device: RouterDevice, settings: RouterDeviceSettings)
    suspend fun removeRouterDevice(device: RouterDevice)

    suspend fun selectRouterDevice(device: RouterDevice)
    suspend fun getSelectRouterDevice(): RouterDevice
    suspend fun getLocalRouterDevice(): RouterDevice
}
