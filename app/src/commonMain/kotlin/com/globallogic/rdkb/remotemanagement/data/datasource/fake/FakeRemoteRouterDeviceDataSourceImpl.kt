package com.globallogic.rdkb.remotemanagement.data.datasource.fake

import com.globallogic.rdkb.remotemanagement.data.datasource.RemoteRouterDeviceDataSource
import com.globallogic.rdkb.remotemanagement.data.error.IoDeviceError
import com.globallogic.rdkb.remotemanagement.domain.entity.AccessPoint
import com.globallogic.rdkb.remotemanagement.domain.entity.AccessPointGroup
import com.globallogic.rdkb.remotemanagement.domain.entity.AccessPointSettings
import com.globallogic.rdkb.remotemanagement.domain.entity.ConnectedDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.ConnectedDeviceStats
import com.globallogic.rdkb.remotemanagement.domain.entity.DeviceAccessPointSettings
import com.globallogic.rdkb.remotemanagement.domain.entity.FoundRouterDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource.Success
import com.globallogic.rdkb.remotemanagement.domain.utils.flatMapData
import com.globallogic.rdkb.remotemanagement.domain.utils.map
import com.globallogic.rdkb.remotemanagement.domain.utils.mapErrorToData

fun RemoteRouterDeviceDataSource.fake(): RemoteRouterDeviceDataSource =
    FakeRemoteRouterDeviceDataSourceImpl(this)

private class FakeRemoteRouterDeviceDataSourceImpl(
    private val original: RemoteRouterDeviceDataSource
) : RemoteRouterDeviceDataSource by original {
    private val hardcodedDevice: FakeRouterDevice = FakeRouterDevice(
        name = "Hardcoded Router",
        macAddress = "dc:a6:32:0e:b9:bb",
        modelName = "ar1840",
        ipAddressV4 = "192.168.1.150",
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
                original.findAvailableRouterDevices()
                    .map { fake + it }
                    .mapErrorToData { fake }
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

    override suspend fun loadAccessPointGroups(device: RouterDevice): Resource<List<AccessPointGroup>, IoDeviceError.WifiSettings> {
        return when (device.macAddress) {
            hardcodedDevice.macAddress -> Success(listOf("Main", "Group 1").mapIndexed { index, name -> AccessPointGroup(index + 1, name) })
            else -> original.loadAccessPointGroups(device)
        }
    }

    override suspend fun loadAccessPointSettings(device: RouterDevice, accessPointGroup: AccessPointGroup): Resource<AccessPointSettings, IoDeviceError.WifiSettings> {
        return when (device.macAddress) {
            hardcodedDevice.macAddress -> Success(AccessPointSettings(accessPointGroup, hardcodedDevice.wifiSettings.map { it.toDomain() }))
            else -> original.loadAccessPointSettings(device, accessPointGroup)
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

    override suspend fun setupAccessPoint(device: RouterDevice, accessPointGroup: AccessPointGroup, settings: DeviceAccessPointSettings): Resource<Unit, IoDeviceError.SetupDevice> {
        return when (device.macAddress) {
            hardcodedDevice.macAddress -> Success(Unit)
            else -> original.setupAccessPoint(device, accessPointGroup, settings)
        }
    }

    private data class FakeRouterDevice(
        val name: String = "Controller",
        val manufacturer: String = "Controller",
        val macAddress: String = "9a:1a:22:49:73:3c",
        val connectedExtender: Int = 0,
        val modelName: String = "ar1840",
        val ipAddressV4: String = "192.168.1.150",
        val ipAddressV6: String = "192.168.1.151",
        val firmwareVersion: String = "v1.0.8",
        val serialNumber: String = "BS100651024E6CA9",
        val totalMemory: Long = 1024,
        val freeMemory: Long = 128,
        val wifiSettings: Set<FakeWifiSettings> = setOf(
            FakeWifiSettings(band = "2.4GHz", ssid = "2.4 wifi"),
            FakeWifiSettings(band = "5GHz", ssid = "5 wifi"),
            FakeWifiSettings(band = "6GHz", ssid = "6 wifi"),
        ),
        val connectedDevices: List<FakeConnectedDevice> = emptyList(),
    ) {
        fun toFoundRouterDevice(): FoundRouterDevice = FoundRouterDevice(
            name = name,
            ip = ipAddressV4,
            macAddress = macAddress
        )
        fun toConnectedDevices(): List<ConnectedDevice> = connectedDevices.map { it.toDomain() }
        fun toRouterDeviceInfo(): RouterDevice = RouterDevice(
            modelName = modelName,
            manufacturer = manufacturer,
            ipAddressV4 = ipAddressV4,
            ipAddressV6 = ipAddressV6,
            macAddress = macAddress,
            firmwareVersion = firmwareVersion,
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
        val stats: FakeConnectedDeviceStats = FakeConnectedDeviceStats(),
    ) {
        fun toDomain(): ConnectedDevice = ConnectedDevice(
            isActive = true,
            macAddress = macAddress,
            hostName = hostName,
            ipAddress = ipAddress,
            vendorClassId = vendorClassId,
            stats = stats.toDomain(),
        )
    }

    data class FakeWifiSettings(
        val enabled: Boolean = true,
        val band: String,
        val ssid: String,
        val availableSecurityModes: List<String> = listOf("public", "private"),
        val securityMode: String = availableSecurityModes.first(),
        val clientsCount: Int = 0,
    ) {
        fun toDomain(): AccessPoint = AccessPoint(
            enabled = enabled,
            band = band,
            ssid = ssid,
            availableSecurityModes = availableSecurityModes,
            securityMode = securityMode,
            clientsCount = clientsCount,
        )
    }

    data class FakeConnectedDeviceStats(
        val bytesSent: Long = 100,
        val bytesReceived: Long = 200,
        val packagesSent: Long = 300,
        val packagesReceived: Long = 400,
        val errorsReceived: Long = 10,
    ) {
        fun toDomain(): ConnectedDeviceStats = ConnectedDeviceStats(
            bytesSent = bytesSent,
            bytesReceived = bytesReceived,
            packetsSent = packagesSent,
            packetsReceived = packagesReceived,
            errorsSent = errorsReceived
        )
    }
}
