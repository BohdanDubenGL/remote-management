package com.globallogic.rdkb.remotemanagement.data.datasource.fake

import com.globallogic.rdkb.remotemanagement.domain.entity.ConnectedDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.FoundRouterDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDeviceInfo
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDeviceSettings
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDeviceTopologyData

class FakeRouterDeviceDataSource {
    private val availableRouterDevices: List<FakeRouterDevice> = listOf(
        FakeRouterDevice(
            name = "Controller1",
            macAddress = "9a:1a:22:49:73:3c",
            modelName = "ar1840",
            ipAddress = "192.168.1.150",
            firmwareVersion = "v1.0.8",
            connectedDevices = listOf(
                FakeConnectedDevice(
                    macAddress = "9a:1a:22:49:73:00",
                    hostName = "DEV-49:73:1c",
                ),
                FakeConnectedDevice(
                    macAddress = "96:ab:87:f7:5b:9f",
                    hostName = "DEV-f7:5b:9f",
                ),
            ),
        ),
        FakeRouterDevice(
            name = "Controller2",
            macAddress = "9a:1a:22:49:73:4c",
            modelName = "ar1841",
            ipAddress = "192.168.1.151",
            firmwareVersion = "v1.0.9",
            connectedDevices = listOf(
                FakeConnectedDevice(
                    macAddress = "9a:1a:22:49:73:00",
                    hostName = "DEV-49:73:1c",
                ),
            ),
        ),
        FakeRouterDevice(
            name = "Controller3",
            macAddress = "9a:1a:22:49:73:5c",
            lanConnected = false,
            modelName = "ar1842",
            ipAddress = "192.168.1.152",
            firmwareVersion = "v1.0.7",
            connectedDevices = listOf(
                FakeConnectedDevice(
                    macAddress = "96:ab:87:f7:5b:9f",
                    hostName = "DEV-f7:5b:9f",
                ),
            ),
        )
    )
    private val savedRouterDevices: MutableList<FakeRouterDevice> = mutableListOf()

    suspend fun findSavedRouterDevices(): List<RouterDevice> {
        return savedRouterDevices.map { it.toRouterDevice() }
    }

    suspend fun findSavedRouterDeviceByMacAddress(macAddress: String): RouterDevice? {
        return savedRouterDevices.firstOrNull { it.macAddress == macAddress }?.toRouterDevice()
    }

    suspend fun findConnectedDevicesForRouterByMacAddress(macAddress: String): List<ConnectedDevice> {
        return savedRouterDevices.firstOrNull { it.macAddress == macAddress }?.toConnectedDevices().orEmpty()
    }

    suspend fun findDeviceInfoByMacAddress(macAddress: String): RouterDeviceInfo? {
        return savedRouterDevices.firstOrNull { it.macAddress == macAddress }?.toRouterDeviceInfo()
    }

    suspend fun findTopologyDataByMacAddress(macAddress: String): RouterDeviceTopologyData? {
        return savedRouterDevices.firstOrNull { it.macAddress == macAddress }?.toTopologyData()
    }

    suspend fun saveRouterDevice(macAddress: String) {
        val fakeRouterDevice = availableRouterDevices.firstOrNull { it.macAddress == macAddress } ?: error("Can't save device")
        val added = savedRouterDevices.add(fakeRouterDevice)
        if (!added) error("Can't save device")
    }

    suspend fun removeRouterDevice(macAddress: String) {
        val fakeRouterDevice = savedRouterDevices.firstOrNull { it.macAddress == macAddress } ?: error("Can't remove device")
        val removed = savedRouterDevices.remove(fakeRouterDevice)
        if (!removed) error("Can't remove device")
    }

    suspend fun actionFactoryResetDevice(macAddress: String): Unit = Unit

    suspend fun actionRestartDevice(macAddress: String): Unit = Unit

    suspend fun actionSetupRouterDevice(macAddress: String, settings: RouterDeviceSettings): Unit = Unit

    suspend fun findLocalRouterDevice(): RouterDevice? {
        return savedRouterDevices.firstOrNull { it.lanConnected }?.toRouterDevice()
    }

    private data class FakeRouterDevice(
        val name: String = "Controller",
        val macAddress: String = "9a:1a:22:49:73:3c",
        val lanConnected: Boolean = true,
        val connectedExtender: Int = 0,
        val modelName: String = "ar1840",
        val ipAddress: String = "192.168.1.150",
        val firmwareVersion: String = "v1.0.8",
        val serialNumber: String = "BS100651024E6CA9",
        val processorLoadPercent: Int = 10,
        val memoryUsagePercent: Int = 20,
        val totalDownloadTraffic: Long = 1900L,
        val totalUploadTraffic: Long = 11800L,
        val availableBands: Set<String> = setOf("5GHz", "2.4GHz", "6GHz"),
        val connectedDevices: List<FakeConnectedDevice> = emptyList(),
    ) {
        fun toFoundRouterDevice(): FoundRouterDevice = FoundRouterDevice(name, ipAddress, macAddress)
        fun toRouterDevice(): RouterDevice = RouterDevice(name, ipAddress, macAddress)
        fun toRouterDeviceInfo(): RouterDeviceInfo = RouterDeviceInfo(lanConnected, connectedExtender, modelName, ipAddress, firmwareVersion, serialNumber, processorLoadPercent, memoryUsagePercent, totalDownloadTraffic, totalUploadTraffic, availableBands)
        fun toConnectedDevices(): List<ConnectedDevice> = connectedDevices.map { it.toDomain() }
        fun toTopologyData(): RouterDeviceTopologyData = RouterDeviceTopologyData(lanConnected, toRouterDevice(), toConnectedDevices())
    }

    data class FakeConnectedDevice(
        val macAddress: String,
        val hostName: String,
        val ssid: String = "Telecom-4e6ca9",
        val channel: Int = 60,
        val rssi: Int = -42,
        val bandWidth: String = "160 MHz",
    ) {
        fun toDomain(): ConnectedDevice = ConnectedDevice(macAddress, hostName, ssid, channel, rssi, bandWidth)
    }
}
