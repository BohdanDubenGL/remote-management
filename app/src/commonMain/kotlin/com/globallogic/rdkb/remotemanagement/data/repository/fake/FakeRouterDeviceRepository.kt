package com.globallogic.rdkb.remotemanagement.data.repository.fake

import com.globallogic.rdkb.remotemanagement.domain.entity.ConnectedDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDeviceInfo
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDeviceSettings
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDeviceTopologyData
import com.globallogic.rdkb.remotemanagement.domain.repository.RouterDeviceRepository

class FakeRouterDeviceRepository(
    private val connectedDevices: Map<RouterDevice, List<ConnectedDevice>> = mapOf(
        RouterDevice("Controller1", "192.168.1.150", "9a:1a:22:49:73:0c") to listOf(
            ConnectedDevice("9a:1a:22:49:73:00", "DEV-49:73:1c", "Telecom-4e6ca9", 60, -41, "160 MHz"),
            ConnectedDevice("96:ab:87:f7:5b:9f", "DEV-f7:5b:9f", "Telecom-4e6ca9", 60, -42, "160 MHz"),
        ),
        RouterDevice("Controller2", "192.168.1.151", "9a:1a:22:49:73:1c") to listOf(
            ConnectedDevice("9a:1a:22:49:73:00", "DEV-49:73:1c", "Telecom-4e6ca9", 60, -41, "160 MHz"),
        ),
        RouterDevice("Controller3", "192.168.1.152", "9a:1a:22:49:73:2c") to listOf(
            ConnectedDevice("96:ab:87:f7:5b:9f", "DEV-f7:5b:9f", "Telecom-4e6ca9", 60, -42, "160 MHz"),
        ),
    ),
    private val routerDeviceInfos: Map<RouterDevice, RouterDeviceInfo> = mapOf(
        RouterDevice("Controller1", "192.168.1.150", "9a:1a:22:49:73:0c") to
                RouterDeviceInfo(true, 0, "ar1840", "192.168.1.150", "v1.0.8", "BS100651024E6CA9", 0, 20, 1900L, 11800L, availableBands = setOf("5GHz", "2.4GHz", "6GHz")),
        RouterDevice("Controller2", "192.168.1.151", "9a:1a:22:49:73:1c") to
                RouterDeviceInfo(true, 0, "ar1841", "192.168.1.151", "v1.0.9", "BS100651024E6CA9", 0, 20, 1900L, 11800L, availableBands = setOf("5GHz", "2.4GHz", "6GHz")),
        RouterDevice("Controller3", "192.168.1.152", "9a:1a:22:49:73:2c") to
                RouterDeviceInfo(false, 0, "ar1842", "192.168.1.152", "v1.0.7", "BS100651024E6CA9", 0, 20, 1900L, 11800L, availableBands = setOf("5GHz", "2.4GHz", "6GHz")),
    ),
    private var selectedRouterDevice: RouterDevice = RouterDevice.empty,
) : RouterDeviceRepository {
    override suspend fun factoryResetRouterDevice(device: RouterDevice): Unit = Unit

    override suspend fun getRouterDevice(deviceMacAddress: String): RouterDevice {
        return connectedDevices.keys.firstOrNull { it.macAddress == deviceMacAddress } ?: RouterDevice.empty
    }

    override suspend fun getRouterDeviceConnectedDevices(device: RouterDevice): List<ConnectedDevice> = connectedDevices.getOrElse(device, ::emptyList)

    override suspend fun getRouterDeviceInfo(device: RouterDevice): RouterDeviceInfo {
        return routerDeviceInfos.getOrElse(device, RouterDeviceInfo::empty)
    }

    override suspend fun getRouterDeviceTopologyData(device: RouterDevice): RouterDeviceTopologyData {
        return RouterDeviceTopologyData(getRouterDeviceInfo(device).lanConnected, device, getRouterDeviceConnectedDevices(device))
    }

    override suspend fun restartRouterDevice(device: RouterDevice): Unit = Unit

    override suspend fun setupRouterDevice(device: RouterDevice, settings: RouterDeviceSettings): Unit = Unit

    override suspend fun selectRouterDevice(device: RouterDevice) {
        selectedRouterDevice = device
    }

    override suspend fun getSelectRouterDevice(): RouterDevice = selectedRouterDevice
}
