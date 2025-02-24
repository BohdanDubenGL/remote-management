package com.globallogic.rdkb.remotemanagement.data.datasource.impl

import com.globallogic.rdkb.remotemanagement.data.datasource.RemoteRouterDeviceDataSource
import com.globallogic.rdkb.remotemanagement.data.error.IoDeviceError
import com.globallogic.rdkb.remotemanagement.data.network.service.RdkCentralService
import com.globallogic.rdkb.remotemanagement.data.network.service.model.Band
import com.globallogic.rdkb.remotemanagement.data.wifi.WifiScanner
import com.globallogic.rdkb.remotemanagement.data.wifi.model.WifiInfo
import com.globallogic.rdkb.remotemanagement.domain.entity.ConnectedDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.FoundRouterDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDeviceSettings
import com.globallogic.rdkb.remotemanagement.domain.entity.Wifi
import com.globallogic.rdkb.remotemanagement.domain.entity.WifiSettings
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
    private val rdkCentralService: RdkCentralService,
    private val wifiScanner: WifiScanner,
) : RemoteRouterDeviceDataSource {

    override suspend fun findAvailableRouterDevices(): Resource<List<FoundRouterDevice>, IoDeviceError.NoAvailableRouterDevices> = supervisorScope {
        val currentWifi = wifiScanner.getCurrentWifi()
        rdkCentralService.getAvailableDevices()
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
        val name = async { rdkCentralService.getModelName(macAddress) }
        val ip = async { rdkCentralService.getIpAddressV4(macAddress) }
        val mac = async { rdkCentralService.getMacAddress(macAddress) }

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

        val name = async { rdkCentralService.getModelName(formattedMacAddress) }
        val manufacturer = async { rdkCentralService.getManufacturer(formattedMacAddress) }
        val ipv4 = async { rdkCentralService.getIpAddressV4(formattedMacAddress) }
        val ipv6 = async { rdkCentralService.getIpAddressV6(formattedMacAddress) }
        val mac = async { rdkCentralService.getMacAddress(formattedMacAddress) }
        val softwareVersion = async { rdkCentralService.getSoftwareVersion(formattedMacAddress) }
        val serialNumber = async { rdkCentralService.getSerialNumber(formattedMacAddress) }
        val bands = async { rdkCentralService.getOperatingFrequencyBands(formattedMacAddress) }
        val totalMemory = async { rdkCentralService.getTotalMemory(formattedMacAddress) }
        val freeMemory = async { rdkCentralService.getFreeMemory(formattedMacAddress) }

        val device = RouterDevice(
            lanConnected = true,
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
        val deviceCount = rdkCentralService.getConnectedDevicesCount(device.macAddress)
            .dataOrElse { error -> return@coroutineScope Failure(IoDeviceError.LoadConnectedDevicesForRouterDevice) }
        val connectedDevices = List(deviceCount) { index ->
            async { loadConnectedDevice(device.macAddress, index) }
        }
            .awaitAll()
            .filterNotNull()
        Success(connectedDevices)
    }

    private suspend fun loadConnectedDevice(macAddress: String, index: Int): ConnectedDevice? = coroutineScope {
        val isActive = async { rdkCentralService.getConnectedDeviceActive(macAddress, index) }
        val hostName = async { rdkCentralService.getConnectedDeviceHostName(macAddress, index) }
        val mac = async { rdkCentralService.getConnectedDeviceMacAddress(macAddress, index) }
        val ip = async { rdkCentralService.getConnectedDeviceIpAddress(macAddress, index) }
        val vendorClassId = async { rdkCentralService.getConnectedDeviceVendorClassId(macAddress, index) }

        ConnectedDevice(
            macAddress = mac.await().dataOrElse { error -> return@coroutineScope null },
            hostName = hostName.await().dataOrElse { error -> return@coroutineScope null },
            ipAddress = ip.await().dataOrElse { error -> return@coroutineScope null },
            isActive = isActive.await().dataOrElse { error -> return@coroutineScope null },
            vendorClassId = vendorClassId.await().dataOrElse { error -> return@coroutineScope null },
        )
    }

    override suspend fun loadWifiSettings(device: RouterDevice): Resource<WifiSettings, IoDeviceError.WifiSettings> = coroutineScope {
        val bands = device.availableBands
            .mapNotNull { band -> Band.entries.firstOrNull { it.displayedName == band } }

        val wifi = bands
            .map { band -> async {
                rdkCentralService.getBandSsid(device.macAddress, band.id + wifiIdDelta)
                    .map { ssid -> Wifi(band.displayedName, ssid) }
            } }
            .awaitAll()
            .map { it.dataOrElse { return@coroutineScope Failure(IoDeviceError.WifiSettings) } }

        Success(WifiSettings(wifi))
    }

    override suspend fun setupDevice(device: RouterDevice, settings: RouterDeviceSettings): Resource<Unit, IoDeviceError.SetupDevice> {
        if (settings.bands.isEmpty()) return Success(Unit)

        settings.bands.forEach { band ->
            val bandType = Band.entries.firstOrNull { it.displayedName == band.frequency }
                ?: return Failure(IoDeviceError.SetupDevice)
            if (band.ssid.isNotBlank()) {
                rdkCentralService.setBandSsid(device.macAddress, bandType.id + wifiIdDelta, band.ssid)
                    .dataOrElse { error -> return Failure(IoDeviceError.SetupDevice) }
            }
            if (band.password.isNotBlank()) {
                rdkCentralService.setBandPassword(device.macAddress, bandType.id + wifiIdDelta, band.password)
                    .dataOrElse { error -> return Failure(IoDeviceError.SetupDevice) }
            }
        }
        return Success(Unit)
    }

    override suspend fun factoryResetDevice(device: RouterDevice): Resource<Unit, IoDeviceError.FactoryResetDevice> {
        return rdkCentralService.factoryResetDevice(device.macAddress)
            .mapError { error -> IoDeviceError.FactoryResetDevice }
    }

    override suspend fun rebootDevice(device: RouterDevice): Resource<Unit, IoDeviceError.RestartDevice> {
        return rdkCentralService.rebootDevice(device.macAddress)
            .mapError { error -> IoDeviceError.RestartDevice }
    }

    companion object {
        private const val wifiIdDelta: Int = 1
    }
}
