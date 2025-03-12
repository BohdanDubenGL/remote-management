package com.globallogic.rdkb.remotemanagement.data.datasource.impl

import com.globallogic.rdkb.remotemanagement.data.datasource.RemoteRouterDeviceDataSource
import com.globallogic.rdkb.remotemanagement.data.error.IoDeviceError
import com.globallogic.rdkb.remotemanagement.data.network.service.RdkCentralAccessorService
import com.globallogic.rdkb.remotemanagement.data.network.service.model.Band
import com.globallogic.rdkb.remotemanagement.data.upnp.UpnpService
import com.globallogic.rdkb.remotemanagement.data.wifi.model.WifiInfo
import com.globallogic.rdkb.remotemanagement.domain.entity.AccessPoint
import com.globallogic.rdkb.remotemanagement.domain.entity.AccessPointGroup
import com.globallogic.rdkb.remotemanagement.domain.entity.AccessPointSettings
import com.globallogic.rdkb.remotemanagement.domain.entity.ConnectedDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.ConnectedDeviceStats
import com.globallogic.rdkb.remotemanagement.domain.entity.DeviceAccessPointSettings
import com.globallogic.rdkb.remotemanagement.domain.entity.FoundRouterDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.WifiMotionEvent
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource.Failure
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource.Success
import com.globallogic.rdkb.remotemanagement.domain.utils.dataOrElse
import com.globallogic.rdkb.remotemanagement.domain.utils.map
import com.globallogic.rdkb.remotemanagement.domain.utils.mapError
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.supervisorScope

class RemoteRouterDeviceDataSourceImpl(
    private val rdkCentralAccessorService: RdkCentralAccessorService,
    private val upnpService: UpnpService,
) : RemoteRouterDeviceDataSource {

    override suspend fun findAvailableRouterDevices(): Resource<List<FoundRouterDevice>, IoDeviceError.NoAvailableRouterDevices> = supervisorScope {
        val foundDevices = rdkCentralAccessorService.getAvailableDevices()
            .map { devices ->
                filterAvailableDevices(devices)
                    .map { macAddress -> async { loadFoundRouterDevice(macAddress) } }
                    .awaitAll()
                    .filterNotNull()
            }
            .mapError { error -> IoDeviceError.NoAvailableRouterDevices }
        return@supervisorScope foundDevices
    }

    private suspend fun filterAvailableDevices(devices: List<String>): List<String> {
        val upnpDeviceMacAddresses = upnpService.getDevices()
            .map { device -> upnpService.getDeviceMac(device) }
            .map { mac -> mac.replace(":", "").lowercase() }
        return devices
            .filter { macAddress -> macAddress in upnpDeviceMacAddresses }
    }

    private fun isMacAddressSimilarToCurrentWifi(macAddress: String, currentWifiInfo: WifiInfo?): Boolean {
        val currentWifiBssid = currentWifiInfo?.bssid?.replace(":", "")?.lowercase()
        return macAddress.take(macAddressSimilarSymbols)
            .equals(currentWifiBssid?.take(macAddressSimilarSymbols), ignoreCase = true)
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
        val connectedDeviceStatsAccessor = connectedDeviceAccessor.deviceStats()
            .dataOrElse { error -> null }

        val isActive = async { connectedDeviceAccessor.getConnectedDeviceActive() }
        val hostName = async { connectedDeviceAccessor.getConnectedDeviceHostName() }
        val mac = async { connectedDeviceAccessor.getConnectedDeviceMacAddress() }
        val ip = async { connectedDeviceAccessor.getConnectedDeviceIpAddress() }
        val vendorClassId = async { connectedDeviceAccessor.getConnectedDeviceVendorClassId() }

        val bytesSent = async { connectedDeviceStatsAccessor?.getBytesSent() }
        val bytesReceived = async { connectedDeviceStatsAccessor?.getBytesReceived() }
        val packetsSent = async { connectedDeviceStatsAccessor?.getPacketsSent() }
        val packetsReceived = async { connectedDeviceStatsAccessor?.getPacketsReceived() }
        val errorsSent = async { connectedDeviceStatsAccessor?.getErrorsSent() }

        return@coroutineScope ConnectedDevice(
            macAddress = mac.await().dataOrElse { error -> return@coroutineScope null },
            hostName = hostName.await().dataOrElse { error -> return@coroutineScope null },
            ipAddress = ip.await().dataOrElse { error -> return@coroutineScope null },
            isActive = isActive.await().dataOrElse { error -> return@coroutineScope null },
            vendorClassId = vendorClassId.await().dataOrElse { error -> return@coroutineScope null },
            stats = ConnectedDeviceStats(
                bytesSent = bytesSent.await()?.data ?: 0L,
                bytesReceived = bytesReceived.await()?.data ?: 0L,
                packetsSent = packetsSent.await()?.data ?: 0L,
                packetsReceived = packetsReceived.await()?.data ?: 0L,
                errorsSent = errorsSent.await()?.data ?: 0L,
            )
        )
    }

    override suspend fun loadAccessPointGroups(device: RouterDevice): Resource<List<AccessPointGroup>, IoDeviceError.WifiSettings> = coroutineScope {
        val deviceAccessor = rdkCentralAccessorService.device(device.macAddress)
        val accessPointGroups = deviceAccessor.accessPointGroups()
            .map { accessPointGroupAccessor -> async {
                loadAccessPointGroup(accessPointGroupAccessor)
            } }
            .awaitAll()
            .filterNotNull()
        return@coroutineScope Success(accessPointGroups)
    }

    private suspend fun loadAccessPointGroup(
        accessPointGroupAccessor: RdkCentralAccessorService.AccessPointGroupAccessor,
    ): AccessPointGroup? = coroutineScope {
        val names = accessPointGroupAccessor.accessPoints()
            .map { accessPointAccessor -> async {
                accessPointAccessor.getWifiName()
                    .dataOrElse { error -> null }
            } }
            .awaitAll()
            .filterNotNull()
        if (names.isEmpty()) return@coroutineScope null
        val prefixName = names.findCommonPrefix()
            .replace("_", " ")
            .takeIf { it.length > 3 }
        val name = when {
            accessPointGroupAccessor.accessPointGroupId == 1 -> "Main${prefixName?.let { " ($it)" }.orEmpty()}"
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
        val clientsCount = async { accessPointAccessor.getWifiClientsCount() }

        return@coroutineScope AccessPoint(
            enabled = enabled.await().dataOrElse { error -> return@coroutineScope null },
            band = accessPointAccessor.band.displayedName,
            ssid = ssid.await().dataOrElse { error -> return@coroutineScope null },
            availableSecurityModes = availableSecurityModes.await().dataOrElse { error -> return@coroutineScope null },
            securityMode = securityMode.await().dataOrElse { error -> return@coroutineScope null },
            clientsCount = clientsCount.await().dataOrElse { error -> return@coroutineScope null },
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

    override suspend fun getWifiMotionState(device: RouterDevice): Resource<String, IoDeviceError.WifiMotion> {
        return rdkCentralAccessorService.device(device.macAddress)
            .wifiMotion()
            .getSensingDeviceMacAddress()
            .mapError { error -> IoDeviceError.WifiMotion }
    }

    override suspend fun getWifiMotionPercent(device: RouterDevice): Resource<Int, IoDeviceError.WifiMotion> {
        return rdkCentralAccessorService.device(device.macAddress)
            .wifiMotion()
            .getMotionPercent()
            .mapError { error -> IoDeviceError.WifiMotion }
    }

    override suspend fun startWifiMotion(device: RouterDevice, connectedDevice: ConnectedDevice): Resource<Unit, IoDeviceError.WifiMotion> {
        return rdkCentralAccessorService.device(device.macAddress)
            .wifiMotion()
            .setSensingDeviceMacAddress(connectedDevice.macAddress)
            .mapError { error -> IoDeviceError.WifiMotion }
    }

    override suspend fun stopWifiMotion(device: RouterDevice): Resource<Unit, IoDeviceError.WifiMotion> {
        return rdkCentralAccessorService.device(device.macAddress)
            .wifiMotion()
            .setSensingDeviceMacAddress("")
            .mapError { error -> IoDeviceError.WifiMotion }
    }

    override suspend fun loadWifiMotionEvents(device: RouterDevice): Resource<List<WifiMotionEvent>, IoDeviceError.WifiMotion> = coroutineScope {
        return@coroutineScope rdkCentralAccessorService.device(device.macAddress)
            .wifiMotion()
            .getSensingEvents()
            .map { motionEventAccessors ->
                motionEventAccessors
                    .map { motionEventAccessor -> async {
                        motionEventAccessor.getMotionEvent()
                            .dataOrElse { error -> null }
                    } }
                    .awaitAll()
                    .filterNotNull()
            }
            .mapError { error -> IoDeviceError.WifiMotion }
    }

    override suspend fun pollWifiMotionEvents(
        device: RouterDevice,
        updateIntervalMillis: Long,
    ): Flow<Resource<List<WifiMotionEvent>, IoDeviceError.WifiMotion>> = channelFlow {
        val wifiMotionAccessor = rdkCentralAccessorService.device(device.macAddress)
            .wifiMotion()
        var events = wifiMotionAccessor
            .getSensingEvents()
            .dataOrElse { error -> emptyList() }
            .map { motionEventAccessor -> async {
                motionEventAccessor.getMotionEvent()
                    .dataOrElse { error -> null }
            } }
            .awaitAll()
            .filterNotNull()
        send(Success(events))

        while (isActive) {
            val eventCount = wifiMotionAccessor.getSensingEventCount()
                .dataOrElse { error -> 0 }
            when {
                eventCount == events.size -> Unit
                eventCount > events.size -> {
                    val eventIds = events.map { it.eventId }
                    val newEvents = wifiMotionAccessor
                        .getSensingEvents()
                        .dataOrElse { error -> emptyList() }
                        .filter { it.eventId !in eventIds }
                        .map { motionEventAccessor -> async {
                            motionEventAccessor.getMotionEvent()
                                .dataOrElse { error -> null }
                        } }
                        .awaitAll()
                        .filterNotNull()
                    events = events + newEvents
                    send(Success(events))
                }
                eventCount < events.size -> {
                    events = wifiMotionAccessor
                        .getSensingEvents()
                        .dataOrElse { error -> emptyList() }
                        .map { motionEventAccessor -> async {
                            motionEventAccessor.getMotionEvent()
                                .dataOrElse { error -> null }
                        } }
                        .awaitAll()
                        .filterNotNull()
                    send(Success(events))
                }
            }

            delay(updateIntervalMillis)
        }
    }

    companion object {
        private const val macAddressSimilarSymbols: Int = 8

        private fun List<String>.findCommonPrefix(): String = when {
            isEmpty() -> ""
            else -> reduce { acc, s -> acc.commonPrefixWith(s) }
        }
    }
}
