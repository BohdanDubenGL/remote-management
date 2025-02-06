package com.globallogic.rdkb.remotemanagement.data.datasource.impl

import com.globallogic.rdkb.remotemanagement.data.datasource.LocalRouterDeviceDataSource
import com.globallogic.rdkb.remotemanagement.data.db.RouterDeviceDao
import com.globallogic.rdkb.remotemanagement.data.db.dto.ConnectedDeviceDto
import com.globallogic.rdkb.remotemanagement.data.db.dto.RouterDeviceDto
import com.globallogic.rdkb.remotemanagement.data.db.dto.UserRouterDeviceDto
import com.globallogic.rdkb.remotemanagement.domain.entity.ConnectedDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDeviceInfo
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDeviceTopologyData
import com.globallogic.rdkb.remotemanagement.domain.utils.runCatchingSafe
import kotlinx.datetime.Clock

class LocalRouterDeviceDataSourceImpl(
    private val deviceDao: RouterDeviceDao,
) : LocalRouterDeviceDataSource {

    override suspend fun loadRouterDevicesForUser(userEmail: String): Result<List<RouterDevice>> = runCatchingSafe {
        val devices = deviceDao.findRouterDevicesForUser(userEmail)
        devices.mapNotNull(RouterDeviceMapper::toRouterDevice)
    }

    override suspend fun findRouterDeviceByMacAddress(macAddress: String): Result<RouterDevice?> = runCatchingSafe {
        val device = deviceDao.findRouterDeviceByMacAddress(macAddress)
        RouterDeviceMapper.toRouterDevice(device)
    }

    override suspend fun saveConnectedDevices(device: RouterDevice, connectedDevices: List<ConnectedDevice>): Result<Unit> = runCatchingSafe {
        val connectedDevicesDto = connectedDevices.map { ConnectedDeviceMapper.toConnectedDevice(device, it) }
        deviceDao.insertConnectedDevices(*connectedDevicesDto.toTypedArray())
    }

    override suspend fun loadConnectedDevices(device: RouterDevice): Result<List<ConnectedDevice>> = runCatchingSafe {
        val connectedDevices = deviceDao.findConnectedDevices(device.macAddress)
        connectedDevices.map(ConnectedDeviceMapper::toConnectedDevice)
    }

    override suspend fun loadDeviceInfo(device: RouterDevice): Result<RouterDeviceInfo?> = runCatchingSafe {
        val routerDevice = deviceDao.findRouterDeviceByMacAddress(device.macAddress)
        RouterDeviceMapper.toRouterDeviceInfo(routerDevice)
    }

    override suspend fun loadTopologyData(device: RouterDevice): Result<RouterDeviceTopologyData?> = runCatchingSafe {
        val routerDevice = findRouterDeviceByMacAddress(device.macAddress).getOrThrow() ?: return@runCatchingSafe null
        val deviceInfo = loadDeviceInfo(device).getOrThrow() ?: return@runCatchingSafe null
        RouterDeviceTopologyData(
            lanConnected = deviceInfo.lanConnected,
            routerDevice = routerDevice,
            connectedDevices = loadConnectedDevices(device).getOrThrow()
        )
    }

    override suspend fun saveRouterDevice(device: RouterDeviceInfo, userEmail: String): Result<Unit> = runCatchingSafe {
        val deviceDto = RouterDeviceMapper.toRouterDeviceInfo(device)
        deviceDao.insertRouterDevice(deviceDto)
        deviceDao.insertUserRouterDevice(UserRouterDeviceDto(userEmail, device.macAddress))
    }

    override suspend fun removeRouterDevice(device: RouterDevice, userEmail: String): Result<Unit> = runCatchingSafe {
        deviceDao.deleteUserRouterDevice(UserRouterDeviceDto(userEmail, device.macAddress))
        deviceDao.deleteRouterDeviceByMacAddress(device.macAddress)
    }

    override suspend fun findLocalRouterDevice(userEmail: String): Result<RouterDevice?> = runCatchingSafe {
        val device = deviceDao.findRouterDevicesForUser(userEmail).firstOrNull { it.lanConnected }
        RouterDeviceMapper.toRouterDevice(device)
    }
}

private object RouterDeviceMapper {
    fun toRouterDevice(device: RouterDeviceDto?): RouterDevice? = when(device) {
        null -> null
        else -> RouterDevice(
            name = device.modelName,
            ip = device.ipAddress,
            macAddress = device.macAddress
        )
    }
    fun toRouterDeviceInfo(device: RouterDeviceDto?): RouterDeviceInfo? = when(device) {
        null -> null
        else -> RouterDeviceInfo(
            lanConnected = device.lanConnected,
            connectedExtender = device.connectedExtender,
            modelName = device.modelName,
            ipAddress = device.ipAddress,
            macAddress = device.macAddress,
            firmwareVersion = device.firmwareVersion,
            serialNumber = device.serialNumber,
            processorLoadPercent = device.processorLoadPercent,
            memoryUsagePercent = device.memoryUsagePercent,
            totalDownloadTraffic = device.totalDownloadTraffic,
            totalUploadTraffic = device.totalUploadTraffic,
            availableBands = device.availableBands.split(",").toSet()
        )
    }
    fun toRouterDeviceInfo(device: RouterDeviceInfo): RouterDeviceDto = RouterDeviceDto(
        lanConnected = device.lanConnected,
        connectedExtender = device.connectedExtender,
        modelName = device.modelName,
        ipAddress = device.ipAddress,
        macAddress = device.macAddress,
        firmwareVersion = device.firmwareVersion,
        serialNumber = device.serialNumber,
        processorLoadPercent = device.processorLoadPercent,
        memoryUsagePercent = device.memoryUsagePercent,
        totalDownloadTraffic = device.totalDownloadTraffic,
        totalUploadTraffic = device.totalUploadTraffic,
        availableBands = device.availableBands.joinToString(separator = ","),
        updatedAt = Clock.System.now().toEpochMilliseconds()
    )
}

private object ConnectedDeviceMapper {
    fun toConnectedDevice(device: ConnectedDeviceDto): ConnectedDevice = ConnectedDevice(
        macAddress = device.macAddress,
        hostName = device.hostName,
        ssid = device.ssid,
        channel = device.channel,
        rssi = device.rssi,
        bandWidth = device.bandWidth
    )

    fun toConnectedDevice(routerDevice: RouterDevice, device: ConnectedDevice): ConnectedDeviceDto = ConnectedDeviceDto(
        routerDeviceMacAddress = routerDevice.macAddress,
        macAddress = device.macAddress,
        hostName = device.hostName,
        ssid = device.ssid,
        channel = device.channel,
        rssi = device.rssi,
        bandWidth = device.bandWidth
    )
}
