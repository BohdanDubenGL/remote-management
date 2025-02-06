package com.globallogic.rdkb.remotemanagement.data.datasource.fake

import com.globallogic.rdkb.remotemanagement.data.datasource.RemoteRouterDeviceDataSource
import com.globallogic.rdkb.remotemanagement.domain.entity.ConnectedDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDeviceInfo
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDeviceSettings

fun RemoteRouterDeviceDataSource.fake(): RemoteRouterDeviceDataSource =
    FakeRemoteRouterDeviceDataSourceImpl(this)

private class FakeRemoteRouterDeviceDataSourceImpl(
    private val original: RemoteRouterDeviceDataSource
) : RemoteRouterDeviceDataSource by original {
    private val hardcodedDevice: FakeRouterDevice = FakeRouterDevice(
        name = "Hardcoded Router",
        macAddress = "dc:a6:32:0e:b8:bb",
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
    )

    override suspend fun loadRouterDeviceInfo(device: RouterDevice): Result<RouterDeviceInfo> {
        return when (device.macAddress) {
            hardcodedDevice.macAddress -> Result.success(hardcodedDevice.toRouterDeviceInfo())
            else -> original.loadRouterDeviceInfo(device)
        }
    }

    override suspend fun loadConnectedDevicesForRouterDevice(device: RouterDevice): Result<List<ConnectedDevice>> {
        return when (device.macAddress) {
            hardcodedDevice.macAddress -> Result.success(hardcodedDevice.toConnectedDevices())
            else -> original.loadConnectedDevicesForRouterDevice(device)
        }
    }

    override suspend fun factoryResetDevice(device: RouterDevice): Result<Unit> {
        return when (device.macAddress) {
            hardcodedDevice.macAddress -> Result.success(Unit)
            else -> original.factoryResetDevice(device)
        }
    }

    override suspend fun restartDevice(device: RouterDevice): Result<Unit> {
        return when (device.macAddress) {
            hardcodedDevice.macAddress -> Result.success(Unit)
            else -> original.restartDevice(device)
        }
    }

    override suspend fun setupDevice(device: RouterDevice, settings: RouterDeviceSettings): Result<Unit> {
        return when (device.macAddress) {
            hardcodedDevice.macAddress -> Result.success(Unit)
            else -> original.setupDevice(device, settings)
        }
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
        fun toRouterDeviceInfo(): RouterDeviceInfo = RouterDeviceInfo(lanConnected, connectedExtender, modelName, ipAddress, macAddress, firmwareVersion, serialNumber, processorLoadPercent, memoryUsagePercent, totalDownloadTraffic, totalUploadTraffic, availableBands)
        fun toConnectedDevices(): List<ConnectedDevice> = connectedDevices.map { it.toDomain() }
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
