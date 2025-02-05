package com.globallogic.rdkb.remotemanagement.data.repository.fake

import com.globallogic.rdkb.remotemanagement.data.datasource.fake.FakeRouterDeviceConnectionDataSource
import com.globallogic.rdkb.remotemanagement.data.datasource.fake.FakeRouterDeviceDataSource
import com.globallogic.rdkb.remotemanagement.domain.entity.FoundRouterDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice
import com.globallogic.rdkb.remotemanagement.domain.repository.RouterDeviceConnectionRepository

class FakeRouterDeviceConnectionRepository(
    private val routerDeviceDataSource: FakeRouterDeviceDataSource,
    private val routerDeviceConnectionDataSource: FakeRouterDeviceConnectionDataSource,
) : RouterDeviceConnectionRepository {

    override suspend fun connectToRouterDevice(device: FoundRouterDevice): RouterDevice {
        return addRouterDeviceManually(device.macAddress)
    }

    override suspend fun addRouterDeviceManually(macAddress: String): RouterDevice {
        val routerDevice = routerDeviceConnectionDataSource.connectToRouterDevice(macAddress) ?: return RouterDevice.empty
        routerDeviceDataSource.saveRouterDevice(routerDevice.macAddress)
        return routerDevice
    }

    override suspend fun searchRouterDevices(): List<FoundRouterDevice> {
        val connectedDevices = routerDeviceDataSource.findSavedRouterDevices().map { it.macAddress }
        return routerDeviceConnectionDataSource.findAvailableRouterDevices()
            .filter { it.macAddress !in connectedDevices }
    }
}