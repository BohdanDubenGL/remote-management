package com.globallogic.rdkb.remotemanagement.data.repository.fake

import com.globallogic.rdkb.remotemanagement.domain.entity.FoundRouterDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice
import com.globallogic.rdkb.remotemanagement.domain.repository.RouterDeviceConnectionRepository

class FakeRouterDeviceConnectionRepository : RouterDeviceConnectionRepository {
    override suspend fun connectToRouterDevice(device: FoundRouterDevice) = Unit
    override suspend fun getDeviceList(): List<RouterDevice> = emptyList()
    override suspend fun searchRouterDevices(): List<FoundRouterDevice> = emptyList()
}
