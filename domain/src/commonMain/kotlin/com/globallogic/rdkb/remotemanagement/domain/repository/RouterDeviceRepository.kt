package com.globallogic.rdkb.remotemanagement.domain.repository

import com.globallogic.rdkb.remotemanagement.domain.entity.ConnectedDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDeviceInfo
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDeviceSettings
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDeviceTopologyData

interface RouterDeviceRepository {
    suspend fun factoryResetRouterDevice(device: RouterDevice): Unit = Unit

    suspend fun getRouterDeviceConnectedDevices(device: RouterDevice): List<ConnectedDevice> = emptyList()

    suspend fun getRouterDeviceInfo(device: RouterDevice): RouterDeviceInfo = RouterDeviceInfo.empty

    suspend fun getRouterDeviceTopologyData(device: RouterDevice): RouterDeviceTopologyData = RouterDeviceTopologyData.empty

    suspend fun restartRouterDevice(device: RouterDevice): Unit = Unit

    suspend fun setupRouterDevice(device: RouterDevice, settings: RouterDeviceSettings): Unit = Unit

    companion object {
        val empty: RouterDeviceRepository = object : RouterDeviceRepository { }
    }
}
