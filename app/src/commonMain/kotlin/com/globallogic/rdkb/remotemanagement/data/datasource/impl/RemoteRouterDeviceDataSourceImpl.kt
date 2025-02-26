package com.globallogic.rdkb.remotemanagement.data.datasource.impl

import com.globallogic.rdkb.remotemanagement.data.datasource.RemoteRouterDeviceDataSource
import com.globallogic.rdkb.remotemanagement.data.error.IoDeviceError
import com.globallogic.rdkb.remotemanagement.data.network.service.RdkCentralAccessorService
import com.globallogic.rdkb.remotemanagement.data.network.service.model.Band
import com.globallogic.rdkb.remotemanagement.data.wifi.WifiScanner
import com.globallogic.rdkb.remotemanagement.data.wifi.model.WifiInfo
import com.globallogic.rdkb.remotemanagement.domain.entity.ConnectedDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.FoundRouterDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.DeviceAccessPointSettings
import com.globallogic.rdkb.remotemanagement.domain.entity.AccessPoint
import com.globallogic.rdkb.remotemanagement.domain.entity.AccessPointGroup
import com.globallogic.rdkb.remotemanagement.domain.entity.AccessPointSettings
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource.Failure
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource.Success
import com.globallogic.rdkb.remotemanagement.domain.utils.dataOrElse
import com.globallogic.rdkb.remotemanagement.domain.utils.map
import com.globallogic.rdkb.remotemanagement.domain.utils.mapError
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.supervisorScope

class RemoteRouterDeviceDataSourceImpl(
    private val rdkCentralAccessorService: RdkCentralAccessorService,
    private val wifiScanner: WifiScanner,
) : RemoteRouterDeviceDataSource {

    override suspend fun findAvailableRouterDevices(): Resource<List<FoundRouterDevice>, IoDeviceError.NoAvailableRouterDevices> = supervisorScope {
        val currentWifi = wifiScanner.getCurrentWifi()
        val foundDevices = rdkCentralAccessorService.getAvailableDevices()
            .map { devices ->
                devices
                    .filter { macAddress -> isMacAddressSimilarToCurrentWifi(macAddress, currentWifi) }
                    .map { macAddress -> async { loadFoundRouterDevice(macAddress) } }
                    .awaitAll()
                    .filterNotNull()
            }
            .mapError { error -> IoDeviceError.NoAvailableRouterDevices }
        return@supervisorScope foundDevices
    }

    private suspend fun loadFoundRouterDevice(macAddress: String): FoundRouterDevice? = coroutineScope {
        val deviceAccessor = rdkCentralAccessorService.device(macAddress)
        val name = async { deviceAccessor.getModelName() }
        val ip = async { deviceAccessor.getIpAddressV4() }
        val mac = async { deviceAccessor.getMacAddress() }

        return@coroutineScope FoundRouterDevice(
            name = name.await().dataOrElse { error -> return@coroutineScope null },
            ip = ip.await().dataOrElse { error -> return@coroutineScope null },
            macAddress = mac.await().dataOrElse { error -> return@coroutineScope null },
        )
    }

    private fun isMacAddressSimilarToCurrentWifi(macAddress: String, currentWifiInfo: WifiInfo?): Boolean {
        val currentWifiBssid = currentWifiInfo?.bssid?.replace(":", "")
        return macAddress.take(macAddressSimilarSymbols)
            .equals(currentWifiBssid?.take(macAddressSimilarSymbols), ignoreCase = true)
    }

    override suspend fun findRouterDeviceByMacAddress(macAddress: String): Resource<RouterDevice, IoDeviceError.CantConnectToRouterDevice> = coroutineScope {
        val deviceAccessor = rdkCentralAccessorService.device(macAddress)

        val name = async { deviceAccessor.getModelName() }
        val manufacturer = async { deviceAccessor.getManufacturer() }
        val ipv4 = async { deviceAccessor.getIpAddressV4() }
        val ipv6 = async { deviceAccessor.getIpAddressV6() }
        val mac = async { deviceAccessor.getMacAddress() }
        val softwareVersion = async { deviceAccessor.getSoftwareVersion() }
        val serialNumber = async { deviceAccessor.getSerialNumber() }
        val bands = async { deviceAccessor.getOperatingFrequencyBands() }
        val totalMemory = async { deviceAccessor.getTotalMemory() }
        val freeMemory = async { deviceAccessor.getFreeMemory() }

        val device = RouterDevice(
            modelName = name.await().dataOrElse { error -> return@coroutineScope Failure(IoDeviceError.CantConnectToRouterDevice) },
            manufacturer = manufacturer.await().dataOrElse { error -> return@coroutineScope Failure(IoDeviceError.CantConnectToRouterDevice) },
            ipAddressV4 = ipv4.await().dataOrElse { error -> return@coroutineScope Failure(IoDeviceError.CantConnectToRouterDevice) },
            ipAddressV6 = ipv6.await().dataOrElse { error -> return@coroutineScope Failure(IoDeviceError.CantConnectToRouterDevice) },
            macAddress = mac.await().dataOrElse { error -> return@coroutineScope Failure(IoDeviceError.CantConnectToRouterDevice) },
            firmwareVersion = softwareVersion.await().dataOrElse { error -> return@coroutineScope Failure(IoDeviceError.CantConnectToRouterDevice) },
            serialNumber = serialNumber.await().dataOrElse { error -> return@coroutineScope Failure(IoDeviceError.CantConnectToRouterDevice) },
            totalMemory = totalMemory.await().dataOrElse { error -> return@coroutineScope Failure(IoDeviceError.CantConnectToRouterDevice) },
            freeMemory = freeMemory.await().dataOrElse { error -> return@coroutineScope Failure(IoDeviceError.CantConnectToRouterDevice) },
            availableBands = bands.await().dataOrElse { error -> return@coroutineScope Failure(IoDeviceError.CantConnectToRouterDevice) },
        )
        return@coroutineScope Success(device)
    }

    override suspend fun loadConnectedDevicesForRouterDevice(device: RouterDevice): Resource<List<ConnectedDevice>, IoDeviceError.LoadConnectedDevicesForRouterDevice> = coroutineScope {
        val deviceAccessor = rdkCentralAccessorService.device(device.macAddress)
        val connectedDevicesAccessors = deviceAccessor.connectedDevices()
            .dataOrElse { error -> return@coroutineScope Failure(IoDeviceError.LoadConnectedDevicesForRouterDevice) }
        val connectedDevices = connectedDevicesAccessors
            .map { connectedDeviceAccessor -> async {
                loadConnectedDevice(connectedDeviceAccessor)
            } }
            .awaitAll()
            .filterNotNull()
        return@coroutineScope Success(connectedDevices)
    }

    private suspend fun loadConnectedDevice(connectedDeviceAccessor: RdkCentralAccessorService.ConnectedDeviceAccessor): ConnectedDevice? = coroutineScope {
        val isActive = async { connectedDeviceAccessor.getConnectedDeviceActive() }
        val hostName = async { connectedDeviceAccessor.getConnectedDeviceHostName() }
        val mac = async { connectedDeviceAccessor.getConnectedDeviceMacAddress() }
        val ip = async { connectedDeviceAccessor.getConnectedDeviceIpAddress() }
        val vendorClassId = async { connectedDeviceAccessor.getConnectedDeviceVendorClassId() }

        return@coroutineScope ConnectedDevice(
            macAddress = mac.await().dataOrElse { error -> return@coroutineScope null },
            hostName = hostName.await().dataOrElse { error -> return@coroutineScope null },
            ipAddress = ip.await().dataOrElse { error -> return@coroutineScope null },
            isActive = isActive.await().dataOrElse { error -> return@coroutineScope null },
            vendorClassId = vendorClassId.await().dataOrElse { error -> return@coroutineScope null },
        )
    }

    override suspend fun loadAccessPointGroups(device: RouterDevice): Resource<List<AccessPointGroup>, IoDeviceError.WifiSettings> = coroutineScope {
        val deviceAccessor = rdkCentralAccessorService.device(device.macAddress)
        val accessPointGroups = deviceAccessor.accessPointGroups()
            .map { accessPointGroupAccessor -> async {
                loadAccessPointGroup(accessPointGroupAccessor)
            } }
            .awaitAll()
        return@coroutineScope Success(accessPointGroups)
    }

    private suspend fun loadAccessPointGroup(
        accessPointGroupAccessor: RdkCentralAccessorService.AccessPointGroupAccessor,
    ): AccessPointGroup = coroutineScope {
        val names = accessPointGroupAccessor.accessPoints()
            .map { accessPointAccessor -> async {
                accessPointAccessor.getWifiName()
                    .dataOrElse { error -> null }
            } }
            .awaitAll()
            .filterNotNull()
        val prefixName = names.findCommonPrefix()
            .replace("_", " ")
            .takeIf { it.length > 3 }
        val name = when {
            accessPointGroupAccessor.accessPointGroupId == 1 -> "Main${prefixName?.let { " ($it)" }}"
            prefixName != null -> prefixName
            else -> names.joinToString("/")
        }

        return@coroutineScope AccessPointGroup(
            id = accessPointGroupAccessor.accessPointGroupId,
            name = name
        )
    }

    override suspend fun loadAccessPointSettings(device: RouterDevice, accessPointGroup: AccessPointGroup): Resource<AccessPointSettings, IoDeviceError.WifiSettings> = coroutineScope {
        val accessPoints = rdkCentralAccessorService.device(device.macAddress)
            .accessPointGroup(accessPointGroup.id)
            .accessPoints()
            .map { accessPointAccessor -> async {
                loadAccessPoint(accessPointAccessor)
            } }
            .awaitAll()
            .filterNotNull()

        return@coroutineScope Success(AccessPointSettings(
            accessPointGroup = accessPointGroup,
            accessPoints = accessPoints,
        ))
    }

    private suspend fun loadAccessPoint(accessPointAccessor: RdkCentralAccessorService.AccessPointAccessor): AccessPoint? = coroutineScope {
        val enabled = async { accessPointAccessor.getWifiEnabled() }
        val ssid = async { accessPointAccessor.getWifiSsid() }
        val availableSecurityModes = async { accessPointAccessor.getWifiAvailableSecurityModes() }
        val securityMode = async { accessPointAccessor.getWifiSecurityMode() }

        return@coroutineScope AccessPoint(
            enabled = enabled.await().dataOrElse { error -> return@coroutineScope null },
            band = accessPointAccessor.band.displayedName,
            ssid = ssid.await().dataOrElse { error -> return@coroutineScope null },
            availableSecurityModes = availableSecurityModes.await().dataOrElse { error -> return@coroutineScope null },
            securityMode = securityMode.await().dataOrElse { error -> return@coroutineScope null }
        )
    }

    override suspend fun setupAccessPoint(device: RouterDevice, accessPointGroup: AccessPointGroup, settings: DeviceAccessPointSettings): Resource<Unit, IoDeviceError.SetupDevice> {
        if (settings.bands.isEmpty()) return Success(Unit)

        val accessPointGroupAccessor = rdkCentralAccessorService.device(device.macAddress)
            .accessPointGroup(accessPointGroup.id)

        settings.bands.forEach { band ->
            val bandType = Band.entries.firstOrNull { it.displayedName == band.frequency }
                ?: return Failure(IoDeviceError.SetupDevice)
            val accessPointAccessor = accessPointGroupAccessor.accessPoint(bandType)

            if (band.ssid.isNotBlank()) {
                accessPointAccessor.setWifiSsid(band.ssid)
                    .dataOrElse { error -> return Failure(IoDeviceError.SetupDevice) }
            }
            if (band.password.isNotBlank()) {
                accessPointAccessor.setWifiPassword(band.password)
                    .dataOrElse { error -> return Failure(IoDeviceError.SetupDevice) }
            }
            accessPointAccessor.setWifiEnabled(band.enabled)
            accessPointAccessor.setWifiSecurityMode( band.securityMode)
        }
        return Success(Unit)
    }

    override suspend fun factoryResetDevice(device: RouterDevice): Resource<Unit, IoDeviceError.FactoryResetDevice> {
        val deviceAccessor = rdkCentralAccessorService.device(device.macAddress)
        return deviceAccessor.factoryResetDevice()
            .mapError { error -> IoDeviceError.FactoryResetDevice }
    }

    override suspend fun rebootDevice(device: RouterDevice): Resource<Unit, IoDeviceError.RestartDevice> {
        val deviceAccessor = rdkCentralAccessorService.device(device.macAddress)
        return deviceAccessor.rebootDevice()
            .mapError { error -> IoDeviceError.RestartDevice }
    }

    companion object {
        private const val macAddressSimilarSymbols: Int = 8

        private fun List<String>.findCommonPrefix(): String = when {
            isEmpty() -> ""
            else -> reduce { acc, s -> acc.commonPrefixWith(s) }
        }
    }
}
