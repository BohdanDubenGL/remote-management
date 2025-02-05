package com.globallogic.rdkb.remotemanagement.data.datasource.fake

import com.globallogic.rdkb.remotemanagement.domain.entity.FoundRouterDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice

class FakeRouterDeviceConnectionDataSource {
    private val availableRouterDevices: List<FakeRouterDevice> = listOf(
        FakeRouterDevice(
            name = "Controller1",
            macAddress = "9a:1a:22:49:73:3c",
            ipAddress = "192.168.1.150",
        ),
        FakeRouterDevice(
            name = "Controller2",
            macAddress = "9a:1a:22:49:73:4c",
            ipAddress = "192.168.1.151",
        ),
        FakeRouterDevice(
            name = "Controller3",
            macAddress = "9a:1a:22:49:73:5c",
            ipAddress = "192.168.1.152",
        )
    )

    suspend fun findAvailableRouterDevices(): List<FoundRouterDevice> {
        return availableRouterDevices.map { it.toFoundRouterDevice() }
    }

    suspend fun connectToRouterDevice(macAddress: String): RouterDevice? {
        val device = availableRouterDevices.firstOrNull { it.macAddress == macAddress } ?: return null
        return device.toRouterDevice()
    }

    private data class FakeRouterDevice(
        val name: String = "Controller",
        val macAddress: String = "9a:1a:22:49:73:3c",
        val ipAddress: String = "192.168.1.150",
    ) {
        fun toFoundRouterDevice(): FoundRouterDevice = FoundRouterDevice(name, ipAddress, macAddress)
        fun toRouterDevice(): RouterDevice = RouterDevice(name, ipAddress, macAddress)
    }
}
