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
        rdkCentralAccessorService.getAvailableDevices()
            .map { devices ->
                devices
                    .filter { macAddress -> isMacAddressSimilarToCurrentWifi(macAddress, currentWifi) }
                    .map { macAddress -> async { loadFoundRouterDevice(macAddress) } }
                    .awaitAll()
                    .filterNotNull()
            }
            .mapError { error -> IoDeviceError.NoAvailableRouterDevices }
    }

    private suspend fun loadFoundRouterDevice(macAddress: String): FoundRouterDevice? = coroutineScope {
        val name = async { rdkCentralAccessorService.getModelName(macAddress) }
        val ip = async { rdkCentralAccessorService.getIpAddressV4(macAddress) }
        val mac = async { rdkCentralAccessorService.getMacAddress(macAddress) }

        FoundRouterDevice(
            name = name.await().dataOrElse { error -> return@coroutineScope null },
            ip = ip.await().dataOrElse { error -> return@coroutineScope null },
            macAddress = mac.await().dataOrElse { error -> return@coroutineScope null },
        )
    }

    private fun isMacAddressSimilarToCurrentWifi(macAddress: String, currentWifiInfo: WifiInfo?): Boolean {
        val currentWifiBssid = currentWifiInfo?.bssid?.replace(":", "")
        val similarBytes = 8
        return macAddress.take(similarBytes).equals(currentWifiBssid?.take(similarBytes), ignoreCase = true)
    }

    override suspend fun findRouterDeviceByMacAddress(macAddress: String): Resource<RouterDevice, IoDeviceError.CantConnectToRouterDevice> = coroutineScope {
        val formattedMacAddress = macAddress.replace(":", "")

        val name = async { rdkCentralAccessorService.getModelName(formattedMacAddress) }
        val manufacturer = async { rdkCentralAccessorService.getManufacturer(formattedMacAddress) }
        val ipv4 = async { rdkCentralAccessorService.getIpAddressV4(formattedMacAddress) }
        val ipv6 = async { rdkCentralAccessorService.getIpAddressV6(formattedMacAddress) }
        val mac = async { rdkCentralAccessorService.getMacAddress(formattedMacAddress) }
        val softwareVersion = async { rdkCentralAccessorService.getSoftwareVersion(formattedMacAddress) }
        val serialNumber = async { rdkCentralAccessorService.getSerialNumber(formattedMacAddress) }
        val bands = async { rdkCentralAccessorService.getOperatingFrequencyBands(formattedMacAddress) }
        val totalMemory = async { rdkCentralAccessorService.getTotalMemory(formattedMacAddress) }
        val freeMemory = async { rdkCentralAccessorService.getFreeMemory(formattedMacAddress) }

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
        Success(device)
    }

    override suspend fun loadConnectedDevicesForRouterDevice(device: RouterDevice): Resource<List<ConnectedDevice>, IoDeviceError.LoadConnectedDevicesForRouterDevice> = coroutineScope {
        val deviceCount = rdkCentralAccessorService.getConnectedDevicesCount(device.macAddress)
            .dataOrElse { error -> return@coroutineScope Failure(IoDeviceError.LoadConnectedDevicesForRouterDevice) }
        val connectedDevices = List(deviceCount) { index ->
            async { loadConnectedDevice(device.macAddress, index.inc()) }
        }
            .awaitAll()
            .filterNotNull()
        Success(connectedDevices)
    }

    private suspend fun loadConnectedDevice(macAddress: String, index: Int): ConnectedDevice? = coroutineScope {
        val isActive = async { rdkCentralAccessorService.getConnectedDeviceActive(macAddress, index) }
        val hostName = async { rdkCentralAccessorService.getConnectedDeviceHostName(macAddress, index) }
        val mac = async { rdkCentralAccessorService.getConnectedDeviceMacAddress(macAddress, index) }
        val ip = async { rdkCentralAccessorService.getConnectedDeviceIpAddress(macAddress, index) }
        val vendorClassId = async { rdkCentralAccessorService.getConnectedDeviceVendorClassId(macAddress, index) }

        ConnectedDevice(
            macAddress = mac.await().dataOrElse { error -> return@coroutineScope null },
            hostName = hostName.await().dataOrElse { error -> return@coroutineScope null },
            ipAddress = ip.await().dataOrElse { error -> return@coroutineScope null },
            isActive = isActive.await().dataOrElse { error -> return@coroutineScope null },
            vendorClassId = vendorClassId.await().dataOrElse { error -> return@coroutineScope null },
        )
    }

    override suspend fun loadAccessPointGroups(device: RouterDevice): Resource<List<AccessPointGroup>, IoDeviceError.WifiSettings> = coroutineScope {
        val bands = device.availableBands
            .mapNotNull { band -> Band.entries.firstOrNull { it.displayedName == band } }
        val accessPointGroups = (1..8)
            .map { accessPointGroupId -> async {
                val names = bands.map { band -> async {
                    val accessPointId = band.radio + accessPointGroupId
                    rdkCentralAccessorService.getWifiName(device.macAddress, accessPointId)
                        .dataOrElse { error -> null }
                } }
                    .awaitAll()
                    .filterNotNull()
                val prefixName = names.findCommonPrefix()
                    .replace("_", " ")
                    .takeIf { it.length > 3 }
                val name = when {
                    accessPointGroupId == 1 -> "Main${prefixName?.let { " ($it)" }}"
                    prefixName != null -> prefixName
                    else -> names.joinToString("/")
                }

                AccessPointGroup(id = accessPointGroupId, name = name)
            } }
            .awaitAll()
        return@coroutineScope Success(accessPointGroups)
    }

    override suspend fun loadAccessPointSettings(device: RouterDevice, accessPointGroup: AccessPointGroup): Resource<AccessPointSettings, IoDeviceError.WifiSettings> = coroutineScope {
        val bands = device.availableBands
            .mapNotNull { band -> Band.entries.firstOrNull { it.displayedName == band } }

        val accessPoints = bands
            .map { band -> async { loadAccessPoint(device.macAddress, accessPointGroup, band) } }
            .awaitAll()
            .filterNotNull()

        Success(AccessPointSettings(
            accessPointGroup = accessPointGroup,
            accessPoints = accessPoints,
        ))
    }

    private suspend fun loadAccessPoint(macAddress: String, accessPointGroup: AccessPointGroup, band: Band): AccessPoint? = coroutineScope {
        val accessPoint = band.radio + accessPointGroup.id

        val enabled = async { rdkCentralAccessorService.getWifiEnabled(macAddress, accessPoint) }
        val ssid = async { rdkCentralAccessorService.getWifiSsid(macAddress, accessPoint) }
        val availableSecurityModes = async { rdkCentralAccessorService.getWifiAvailableSecurityModes(macAddress, accessPoint) }
        val securityMode = async { rdkCentralAccessorService.getWifiSecurityMode(macAddress, accessPoint) }

        AccessPoint(
            enabled = enabled.await().dataOrElse { error -> return@coroutineScope null },
            band = band.displayedName,
            ssid = ssid.await().dataOrElse { error -> return@coroutineScope null },
            availableSecurityModes = availableSecurityModes.await().dataOrElse { error -> return@coroutineScope null },
            securityMode = securityMode.await().dataOrElse { error -> return@coroutineScope null }
        )
    }

    override suspend fun setupAccessPoint(device: RouterDevice, settings: DeviceAccessPointSettings): Resource<Unit, IoDeviceError.SetupDevice> {
        if (settings.bands.isEmpty()) return Success(Unit)

        settings.bands.forEach { band ->
            val bandType = Band.entries.firstOrNull { it.displayedName == band.frequency }
                ?: return Failure(IoDeviceError.SetupDevice)
            val accessPoint = bandType.radio + wifiIdDelta // todo:

            if (band.ssid.isNotBlank()) {
                rdkCentralAccessorService.setWifiSsid(device.macAddress, accessPoint, band.ssid)
                    .dataOrElse { error -> return Failure(IoDeviceError.SetupDevice) }
            }
            if (band.password.isNotBlank()) {
                rdkCentralAccessorService.setWifiPassword(device.macAddress, accessPoint, band.password)
                    .dataOrElse { error -> return Failure(IoDeviceError.SetupDevice) }
            }
            rdkCentralAccessorService.setWifiEnabled(device.macAddress, accessPoint, band.enabled)
            rdkCentralAccessorService.setWifiSecurityMode(device.macAddress, accessPoint, band.securityMode)
        }
        return Success(Unit)
    }

    override suspend fun factoryResetDevice(device: RouterDevice): Resource<Unit, IoDeviceError.FactoryResetDevice> {
        return rdkCentralAccessorService.factoryResetDevice(device.macAddress)
            .mapError { error -> IoDeviceError.FactoryResetDevice }
    }

    override suspend fun rebootDevice(device: RouterDevice): Resource<Unit, IoDeviceError.RestartDevice> {
        return rdkCentralAccessorService.rebootDevice(device.macAddress)
            .mapError { error -> IoDeviceError.RestartDevice }
    }

    companion object {
        private const val wifiIdDelta: Int = 1

        private fun List<String>.findCommonPrefix(): String {
            if (isEmpty()) return ""
            return reduce { acc, s -> acc.commonPrefixWith(s) }
        }
    }
}
