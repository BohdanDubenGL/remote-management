package com.globallogic.rdkb.remotemanagement.data.repository.fake

import com.globallogic.rdkb.remotemanagement.domain.entity.FoundRouterDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice
import com.globallogic.rdkb.remotemanagement.domain.repository.RouterDeviceConnectionRepository

class FakeRouterDeviceConnectionRepository(
    private val foundRouterDevices: List<FoundRouterDevice> = listOf(
        FoundRouterDevice("NewController1", "192.168.1.153", "9a:1a:22:49:73:3c"),
        FoundRouterDevice("NewController2", "192.168.1.154", "9a:1a:22:49:73:4c"),
        FoundRouterDevice("NewController3", "192.168.1.155", "9a:1a:22:49:73:5c"),
    ),
    private val savedRouterDevices: List<RouterDevice> = listOf(
        RouterDevice("Controller1", "192.168.1.150", "9a:1a:22:49:73:0c"),
        RouterDevice("Controller2", "192.168.1.151", "9a:1a:22:49:73:1c"),
        RouterDevice("Controller3", "192.168.1.152", "9a:1a:22:49:73:2c"),
    )
) : RouterDeviceConnectionRepository {
    override suspend fun connectToRouterDevice(device: FoundRouterDevice): RouterDevice = RouterDevice(device.name, device.ip, device.macAddress)
    override suspend fun addRouterDeviceManually(macAddress: String): RouterDevice {
        return connectToRouterDevice(FoundRouterDevice("New Device", "ip.ip.ip.ip", macAddress))
    }

    override suspend fun getDeviceList(): List<RouterDevice> = savedRouterDevices
    override suspend fun searchRouterDevices(): List<FoundRouterDevice> = foundRouterDevices
}
