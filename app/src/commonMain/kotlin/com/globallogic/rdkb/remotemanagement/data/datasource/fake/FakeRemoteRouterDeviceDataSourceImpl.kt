package com.globallogic.rdkb.remotemanagement.data.datasource.fake

import com.globallogic.rdkb.remotemanagement.data.datasource.RemoteRouterDeviceDataSource
import com.globallogic.rdkb.remotemanagement.data.error.IoDeviceError
import com.globallogic.rdkb.remotemanagement.domain.entity.ConnectedDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.FoundRouterDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDeviceSettings
import com.globallogic.rdkb.remotemanagement.domain.entity.Wifi
import com.globallogic.rdkb.remotemanagement.domain.entity.WifiSettings
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource.Success
import com.globallogic.rdkb.remotemanagement.domain.utils.flatMapData
import com.globallogic.rdkb.remotemanagement.domain.utils.map

fun RemoteRouterDeviceDataSource.fake(): RemoteRouterDeviceDataSource =
    FakeRemoteRouterDeviceDataSourceImpl(this)

private class FakeRemoteRouterDeviceDataSourceImpl(
    private val original: RemoteRouterDeviceDataSource
) : RemoteRouterDeviceDataSource by original {
    private val hardcodedDevice: FakeRouterDevice = FakeRouterDevice(
        name = "Hardcoded Router",
        macAddress = "dc:a6:32:0e:b9:bb",
        modelName = "ar1840",
        ipAddress = "192.168.1.150",
        firmwareVersion = "v1.0.8",
        connectedDevices = listOf(
            FakeConnectedDevice(
                macAddress = "9a:1a:22:49:73:00",
                hostName = "DEV-49:73:1c",
                ipAddress = "192.168.1.151",
            ),
            FakeConnectedDevice(
                macAddress = "96:ab:87:f7:5b:9f",
                hostName = "DEV-f7:5b:9f",
                ipAddress = "192.168.1.152",
            ),
        ),
    )

    override suspend fun findAvailableRouterDevices(): Resource<List<FoundRouterDevice>, IoDeviceError.NoAvailableRouterDevices> {
        return Success(listOf(hardcodedDevice.toFoundRouterDevice()))
            .flatMapData { fake ->
                original.findAvailableRouterDevices().map { fake + it }
            }
    }

    override suspend fun findRouterDeviceByMacAddress(macAddress: String): Resource<RouterDevice, IoDeviceError.CantConnectToRouterDevice> {
        return when (macAddress) {
            hardcodedDevice.macAddress -> Success(hardcodedDevice.toRouterDeviceInfo())
            else -> original.findRouterDeviceByMacAddress(macAddress)
        }
    }

    override suspend fun loadConnectedDevicesForRouterDevice(device: RouterDevice): Resource<List<ConnectedDevice>, IoDeviceError.LoadConnectedDevicesForRouterDevice> {
        return when (device.macAddress) {
            hardcodedDevice.macAddress -> Success(hardcodedDevice.toConnectedDevices())
            else -> original.loadConnectedDevicesForRouterDevice(device)
        }
    }

    override suspend fun loadWifiSettings(device: RouterDevice): Resource<WifiSettings, IoDeviceError.WifiSettings> {
        return when (device.macAddress) {
            hardcodedDevice.macAddress -> Success(WifiSettings(hardcodedDevice.wifiSettings.map { it.toDomain() }))
            else -> original.loadWifiSettings(device)
        }
    }

    override suspend fun factoryResetDevice(device: RouterDevice): Resource<Unit, IoDeviceError.FactoryResetDevice> {
        return when (device.macAddress) {
            hardcodedDevice.macAddress -> Success(Unit)
            else -> original.factoryResetDevice(device)
        }
    }

    override suspend fun rebootDevice(device: RouterDevice): Resource<Unit, IoDeviceError.RestartDevice> {
        return when (device.macAddress) {
            hardcodedDevice.macAddress -> Success(Unit)
            else -> original.rebootDevice(device)
        }
    }

    override suspend fun setupDevice(device: RouterDevice, settings: RouterDeviceSettings): Resource<Unit, IoDeviceError.SetupDevice> {
        return when (device.macAddress) {
            hardcodedDevice.macAddress -> Success(Unit)
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
        val additionalFirmwareVersion: String = "v1.0.16",
        val serialNumber: String = "BS100651024E6CA9",
        val totalMemory: Long = 1024,
        val freeMemory: Long = 128,
        val wifiSettings: Set<FakeWifiSettings> = setOf(
            FakeWifiSettings("2.4GHz", "2.4 wifi"),
            FakeWifiSettings("5GHz", "5 wifi"),
            FakeWifiSettings("6GHz", "6 wifi"),
        ),
        val connectedDevices: List<FakeConnectedDevice> = emptyList(),
    ) {
        fun toFoundRouterDevice(): FoundRouterDevice = FoundRouterDevice(
            name = name,
            ip = ipAddress,
            macAddress = macAddress
        )
        fun toConnectedDevices(): List<ConnectedDevice> = connectedDevices.map { it.toDomain() }
        fun toRouterDeviceInfo(): RouterDevice = RouterDevice(
            lanConnected = lanConnected,
            modelName = modelName,
            ipAddress = ipAddress,
            macAddress = macAddress,
            firmwareVersion = firmwareVersion,
            additionalFirmwareVersion = additionalFirmwareVersion,
            serialNumber = serialNumber,
            totalMemory = totalMemory,
            freeMemory = freeMemory,
            availableBands = wifiSettings.map { it.band }.toSet()
        )
    }

    data class FakeConnectedDevice(
        val macAddress: String,
        val hostName: String,
        val ipAddress: String,
        val vendorClassId: String = "android",
    ) {
        fun toDomain(): ConnectedDevice = ConnectedDevice(
            isActive = true,
            macAddress = macAddress,
            hostName = hostName,
            ipAddress = ipAddress,
            vendorClassId = vendorClassId,
        )
    }

    data class FakeWifiSettings(
        val band: String,
        val ssid: String,
    ) {
        fun toDomain(): Wifi = Wifi(band, ssid)
    }
}
