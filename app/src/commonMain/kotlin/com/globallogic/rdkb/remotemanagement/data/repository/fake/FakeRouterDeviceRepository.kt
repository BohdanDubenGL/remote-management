package com.globallogic.rdkb.remotemanagement.data.repository.fake

import com.globallogic.rdkb.remotemanagement.domain.entity.ConnectedDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDeviceInfo
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDeviceSettings
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDeviceTopologyData
import com.globallogic.rdkb.remotemanagement.domain.repository.RouterDeviceRepository

class FakeRouterDeviceRepository : RouterDeviceRepository {
    override suspend fun factoryResetRouterDevice(device: RouterDevice): Unit = Unit
    override suspend fun getRouterDeviceConnectedDevices(device: RouterDevice): List<ConnectedDevice> = emptyList()
    override suspend fun getRouterDeviceInfo(device: RouterDevice): RouterDeviceInfo = RouterDeviceInfo.empty
    override suspend fun getRouterDeviceTopologyData(device: RouterDevice): RouterDeviceTopologyData = RouterDeviceTopologyData.empty
    override suspend fun restartRouterDevice(device: RouterDevice): Unit = Unit
    override suspend fun setupRouterDevice(device: RouterDevice, settings: RouterDeviceSettings): Unit = Unit
}
